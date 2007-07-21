/**
 * 
 */
package edu.umich.lsa.cscs.gridsweeper;

import static edu.umich.lsa.cscs.gridsweeper.DLogger.*;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

import static edu.umich.lsa.cscs.gridsweeper.StringUtils.*;
import edu.umich.lsa.cscs.gridsweeper.GridSweeper.RunType;
import edu.umich.lsa.cscs.gridsweeper.parameters.*;

/**
 * {@code GridSweeperTool} is the principal class of the command-line
 * gsweep tool (and its gdrone, etc. variants). This class implements
 * a command-line interface on top of the {@code GridSweeper} class, which
 * implements actual program functionality.
 * @author Ed Baskerville
 *
 */
public class GridSweeperTool
{
	/**
	 * A state enum for the argument-parsing state machine.
	 * @author Ed Baskerville
	 *
	 */
	enum ArgState
	{
		START,
		ADAPTER,
		NAME,
		NUM_RUNS,
		SEED,
		COMMAND,
		INPUT,
		OUTPUT
	}
	
	
	static String className;
	
	static Matcher equalMatcher;
	static Matcher numMatcher;

	static
	{
		className = GridSweeperTool.class.getName();
		
		// Matches non-empty LHS = non-empty RHS, groups trimmed LHS and trimmed RHS
		equalMatcher = Pattern.compile("\\A\\s*([^=]+?)\\s*=\\s*([^=]+?)\\s*\\z").matcher("");
		//equalMatcher = Pattern.compile("([^=]+)=([^=]+)").matcher("");
		
		// Matches signed decimal number
		numMatcher = Pattern.compile("\\A(-?\\d*\\.?\\d*)\\z").matcher("");
		
	}
	
	GridSweeper gs;
	
	String experimentPath;
	String outputPath;
	Experiment experiment;


	public static void main(String[] args)
	{
		boolean debug = false;
		
		for(String arg : args)
		{
			if(arg.equals("-D") || arg.equals("--debug"))
			{
				DLogger.addConsoleHandler(Level.ALL);
				debug = true;
			}
		}
		
		GridSweeperTool tool = new GridSweeperTool();
		
		try
		{
			tool.run(args);
		}
		catch(GridSweeperException e)
		{
			System.err.println(e.getMessage());
			
			if(debug)
			{
				e.printStackTrace();
			}
		}
	}
	
	public GridSweeperTool()
	{
		gs = new GridSweeper();
	}
	
	/**
	 * Does everything: loads the experiment, runs the experiment, and (soon)
	 * monitors the experiment.
	 * @param args Command-line arguments.
	 * @throws GridSweeperException If the GRIDSWEEPER_ROOT environment variable
	 * is not set, or if parsing, loading, setup, running, or monitoring jobs
	 * generate any other uncaught exceptions.
	 */
	public void run(String[] args) throws GridSweeperException
	{
		// Set up logging to /tmp/gridsweeper.log
		DLogger.addFileHandler(Level.ALL, "%t/gridsweeper.log");
		
		entering(className, "main");
		
		String root = System.getenv("GRIDSWEEPER_ROOT");
		if(root == null)
			throw new GridSweeperException("GRIDSWEEPER_ROOT environment variable not set.");
		gs.setRoot(root);
		
		// Parse args
		Settings cliSettings = new Settings();
		Settings adapterSettings = new Settings();
		Settings fileTransferSettings = new Settings();
		List<Sweep> cliSweeps = new ArrayList<Sweep>();
		parseArgs(args, cliSettings, adapterSettings, fileTransferSettings, cliSweeps);
		
		// Load experiment file
		loadExperiment();
		
		// Override name if specified at command line
		String name = cliSettings.getProperty("Name");
		if(name != null)
		{
			experiment.setName(name);
			cliSettings.remove("Name");
		}
		
		// Override numRuns if specified at command line
		String numRuns = cliSettings.getProperty("NumRuns");
		if(numRuns != null)
		{
			try
			{
				int numRunsI = Integer.parseInt(numRuns);
				experiment.setNumRuns(numRunsI);
			}
			catch(NumberFormatException e)
			{
				throw new GridSweeperException("Invalid number of runs specfication " + numRuns + ".");
			}
			cliSettings.remove("NumRuns");
		}
		
		// Override location in seed table if specified at command line
		String seedStr = cliSettings.getProperty("Seed");
		if(seedStr != null)
		{
			String[] pieces = seedStr.split(":");
			if(pieces.length != 2)
			{
				throw new GridSweeperException("Invalid seed table location " + seedStr);
			}
			String rowStr = pieces[0];
			String colStr = pieces[1];
			try
			{
				int row = Integer.parseInt(rowStr); 
				int col = Integer.parseInt(colStr);
				
				experiment.setFirstSeedRow(row);
				experiment.setSeedCol(col);
			}
			catch(NumberFormatException e)
			{
				throw new GridSweeperException("Invalid seed table location " + seedStr);
			}
		}
		
		// Combine settings from command-line arguments and experiment
		experiment.getSettings().putAll(cliSettings);
		
		experiment.getSettings().putAllForClass(
				adapterSettings,
				experiment.getSettings().getProperty("AdapterClass"));
		
		experiment.getSettings().putAllForClass(
				fileTransferSettings,
				experiment.getSettings().getProperty("FileTransferSystemClass"));
		
		// Load adapter settings by prepending class prefix
		for(Object keyObj : adapterSettings.keySet())
		{
			String key = (String)keyObj;
			experiment.getSettings().setProperty(
					experiment.getSettings().getProperty("AdapterClass")
					+ "." + key, adapterSettings.getProperty(key));
		}
		
		for(Sweep sweep : cliSweeps)
		{
			experiment.getRootSweep().add(sweep);
		}
		
		// Write experiment XML as provided by --output option
		if(outputPath != null) try
		{
			experiment.writeToFile(outputPath, false);
		}
		catch(FileNotFoundException e)
		{
			throw new GridSweeperException(
					"Could not write to output path " + outputPath + ".", e);
		}
		
		gs.setExperiment(experiment);
		
		// Generate experiment cases, etc.
		gs.setUpExperiment();
		
		// Run jobs
		gs.runExperiment();
		
		// Finish running jobs
		gs.finish();
		
		exiting(className, "main");
	}
	
	/**
	 * <p>Parses command-line arguments.</p>
	 * 
	 * <table valign="top">
	 * 
	 * <tr>
	 * <td><b>Switch</b></td>
	 * <td><b>Description</b></td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>-a, --adapter</td>
	 * <td>
	 * Adapter class name, e.g., {@code edu.umich.lsa.cscs.gridsweeper.DroneAdapter}.
	 * </td>
	 * </tr>
	 *
	 * <tr>
	 * <td>-n, --name</td>
	 * <td>
	 * Experiment name. This string is used to name the experiment output directory.
	 * </td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>-N, --numruns</td>
	 * <td>
	 * Number of runs to perform for each parameter assignment.
	 * </td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>-S, --seed</td>
	 * <td>
	 * The location of the first seed in the seed generation table,
	 * given as row:col.
	 * </td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>-c, --command</td>
	 * <td>
	 * Command (model executable path) to run. In effect, this sets the "command"
	 * setting in the adapter domain, e.g.,
	 * {@code edu.umich.lsa.cscs.gridsweeper.Drone.command}, so if the adapter class
	 * does not support the "command" setting, this argument has no effect.
	 * </td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>-i, --input</td>
	 * <td>
	 * Path to experiment XML input file. This is usually required,
	 * but can theoretically be left out if all the necessary experiment
	 * information is provided with other command-line switches—for the
	 * Drone adapter, this would mean that {@code -c} and {@code -n}
	 * would be required, among others. Note that command-line settings
	 * override any settings made in the experiment XML file, which
	 * in turn override any settings in the user’s {@code ~/.gridsweeper}
	 * configuration file.
	 * </td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>-o, --output</td>
	 * <td>
	 * <p>Path at which experiment XML should be written. The outputted file
	 * will contain all the settings needed to re-run the experiment using
	 * <br/><br/>
	 * {@code gsweep -i <experimentXMLPath>}
	 * <br/><br/>
	 * and nothing else. This will <em>not</em> be an exact replica of this
	 * run, because the seed for GridSweeper’s random number generator will
	 * be generated at runtime. A file that will be able to produce an
	 * <em>exact</em> reproduction of this experiment will be generated
	 * in the experiment results directory.</p>
	 * <p>
	 * All settings will be written out to this file, including those
	 * set in the user’s {@code ~/.gridsweeper} configuration file,
	 * in the input experiment file, and those set at the command line,
	 * so that the above command should re-run the experiment as before
	 * unless some setting in the {@code ~/.gridsweeper} changes the behavior. 
	 * </p>
	 * </td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>-O, --output-only</td>
	 * <td>
	 * <p>The same as {@code -o}, except the only thing that will happen
	 * is the production of an output file. The experiment will not be run,
	 * not even to generate output directories and case files.</p>
	 * 
	 * <p>The no-run behavior of this option can be replaced by a dry run
	 * by following this option with {@code -d} or {@code --dry}.
	 * Following {@code -d} or {@code --dry} with this option will
	 * replace dry-run behavior with no-run behavior.</p>  
	 * </td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>-d, --dry</td>
	 * <td>
	 * <p>Perform a “dry run”, simulating the parameter sweep without actually
	 * submitting jobs to the grid to be run. Output directories are created
	 * and populated with control files needed to reproduce cases.</p>
	 * </td>
	 * </tr>
	 * 
	 * <tr>
	 * <td><em>param</em>=<em>value</em></td>
	 * <td>
	 * Sets a fixed parameter <em>param</em> to value <em>value</em>,
	 * valid for all runs of the model.
	 * </td>
	 * </tr>
	 * 
	 * <tr>
	 * <td><em>param</em>=<em>start</em>:<em>incr</em>:<em>end</em></td>
	 * <td>
	 * Sweeps parameter <em>param</em>, starting at value <em>start</em>
	 * and incrementing by <em>incr</em> until the value is greater than
	 * <em>end</em>. The value <em>end</em> is only used, then, if it is
	 * exactly a multiple of <em>incr</em> greater than <em>start</em>.
	 * (Rounding error is not a problem, because an infinite-precision
	 * decimal number representation is used.)   
	 * </td>
	 * </tr>
	 * 
	 * </table>
	 * @param args Command-line arguments.
	 */
	void parseArgs(String[] args, Settings cliSettings, 
			Settings adapterSettings, Settings fileTransferSettings,
			List<Sweep> cliSweeps)
		throws GridSweeperException
	{
		
		entering(className, "parseArgs");
		
		ArgState state = ArgState.START;
		
		for(String arg : args)
		{
			finer("parsing argument: " + arg);
			switch(state)
			{
				case START:
					if(arg.equals("-a") || arg.equals("--adapter"))
						state = ArgState.ADAPTER;
					else if(arg.equals("-n") || arg.equals("--name"))
						state = ArgState.NAME;
					else if(arg.equals("-N") || arg.equals("--numruns"))
						state = ArgState.NUM_RUNS;
					else if(arg.equals("-S") || arg.equals("--seed"))
						state = ArgState.SEED;
					else if(arg.equals("-c") || arg.equals("--command"))
						state = ArgState.COMMAND;
					else if(arg.equals("-i") || arg.equals("--input"))
						state = ArgState.INPUT;
					else if(arg.equals("-o") || arg.equals("--output"))
						state = ArgState.OUTPUT;
					else if(arg.equals("-O") || arg.equals("--output-only"))
					{
						gs.setRunType(RunType.NORUN);
						state = ArgState.OUTPUT;
					}
					else if(arg.equals("-d") || arg.equals("--dry"))
					{
						gs.setRunType(RunType.DRY);
					}
					else if(arg.equals("-D") || arg.equals("--debug"))
					{
						// Do nothing; checked for first thing in main
					}
					else
					{
						Sweep sweep = parseSweepArg(arg);
						assert(sweep != null);
						cliSweeps.add(sweep);
					}
					break;
				case ADAPTER:
					cliSettings.put("AdapterClass", arg);
					state = ArgState.START;
					break;
				case NAME:
					cliSettings.put("Name", arg);
					state = ArgState.START;
					break;
				case NUM_RUNS:
					cliSettings.put("NumRuns", arg);
					state = ArgState.START;
					break;
				case SEED:
					cliSettings.put("Seed", arg);
					state = ArgState.START;
					break;
				case COMMAND:
					adapterSettings.put("command", arg);
					state = ArgState.START;
					break;
				case INPUT:
					experimentPath = arg;
					state = ArgState.START;
					break;
				case OUTPUT:
					outputPath = arg;
					state = ArgState.START;
					break;
			}
		}
		
		exiting(className, "parseArgs");
	}
	
	/**
	 * <p>Attempts to parse a non-switch command line argument
	 * as a parameter sweep specification.</p>
	 * 
	 * <p>Supported types of sweeps:</p>
	 * 
	 * <ul>
	 * <li>Single values: <br/>{@code r=0.1}</li>
	 * <li>List sweeps: <br/>{@code r=0.1, 0.2, 0.3}</li>
	 * <li>Range sweeps: <br/>{@code r=0.1:0.1:1.0}</li>
	 * <li>
	 *     Parallel lists with multiple parameters:<br/>
	 *     {@code r s = 0.1 1.1, 0.2 1.2, 0.3 1.3}<br/>
	 *     {@code r s  = 0.1,0.2,0.3 1.1,1.2,1.3}<br/>
	 * </li>
	 * <li>
	 * 	   Parallel range sweeps with multiple parameters:<br/>
	 *     {@code r s = 0.1:0.1:0.3 1.1:0.1:1.3}
	 * <li>
	 * </ul>
	 * 
	 * <p>If multiple sweeps are provided, they are “multiplied
	 *  together”—all possible combinations of parameter values
	 *  generated by each individual sweep are generated.</p>
	 * @param arg The argument representing the sweep
	 * @return The parsed sweep.
	 * @throws GridSweeperException When the argument does not represent a valid sweep.
	 */
	Sweep parseSweepArg(String arg) throws GridSweeperException
	{
		// Code below is the decision tree for parsing sweeps.
		
		// Check for single equals sign, required for all sweep types,
		// and assigns LHS and RHS.
		equalMatcher.reset(arg);
		if(!equalMatcher.matches())
		{
			parseFail(arg);
		}
		String lhs = equalMatcher.group(1);
		String rhs = equalMatcher.group(2);
		
		// Check for pieces separated by whitespace on LHS
		String[] lhsWords = lhs.split("\\s+");
		assert(lhsWords.length > 0);
		if(lhsWords.length > 1)
		{
			// Parse as a parallel sweep (multiple parameter names on LHS)
			return parseParallelSweep(arg, lhsWords, rhs);
		}
		else
		{
			// Parse as a single-parameter sweep
			return parseSingleSweep(arg, lhs, rhs);
		}
	}

	Sweep parseParallelSweep(String arg, String[] names, String rhs) throws GridSweeperException
	{
		// Split at colon boundaries to see if this is a range sweep list
		String[] rhsColonPieces = rhs.split("\\s*:\\s*");
		assert(rhsColonPieces.length > 0);
		
		if(rhsColonPieces.length > 1)
		{
			// Interpret as a range sweep list.
			return parseParallelRangeSweep(arg, names, rhsColonPieces);
		}
		else
		{
			// Interpret as a parallel single-value list
			return parseParallelListSweep(arg, names, rhs);
		}
	}

	Sweep parseParallelRangeSweep(String arg, String[] names, String[] rhsColonPieces) throws GridSweeperException
	{
		// The number of pieces must be equal to
		// 3 * names.length (start/end/incr for each parameter)
		// - (names.length - 1) (pieces at boundaries between
		//                       ranges are shared)
		// = 2 * names.length + 1 
		if(rhsColonPieces.length != 2 * names.length + 1)
		{
			parseFail(arg);
		}
		
		ParallelCombinationSweep sweep = new ParallelCombinationSweep();
		
		// Look for range sweeps components in the correct locations,
		// splitting at whitespace boundaries as necessary
		
		try
		{
			BigDecimal start;
			BigDecimal incr;
			BigDecimal end;
			
			String[] endStart = null;
			
			// Extract the sweep start, end, increment for each parameter
			for(int i = 0; i < names.length; i++)
			{
				if(i == 0)
				{
					// Take the first piece for the range start
					start = new BigDecimal(rhsColonPieces[0]);
				}
				else
				{
					// Take the second half of the piece
					// whose first piece was used for the last range end
					start = new BigDecimal(endStart[1]);
				}
				
				// Range increment always appears
				// as a lone piece at indexes 1, 3, 5, ...
				incr = new BigDecimal(rhsColonPieces[i*2 + 1]);
				
				if(i == names.length - 1)
				{
					// Take the last piece for the range end
					end = new BigDecimal(rhsColonPieces[i*2 + 2]);
				}
				else
				{
					// Split the last piece into two, using the
					// first for the range end and saving
					// the second for the next range start
					endStart = rhsColonPieces[i*2 + 2].split("\\s+");
					if(endStart.length != 2) parseFail(arg);
					end = new BigDecimal(endStart[0]);
				}
				
				sweep.add(new RangeListSweep(names[i], start, end, incr));
			}
		}
		catch(NumberFormatException e)
		{
			parseFail(arg);
		}
		
		// Check to make sure all sweeps generate the same
		// number of values
		boolean first = true;
		int numValsReq = 0;
		for(Sweep rangeSweep : sweep.getChildren())
		{
			int numVals = 0;
			try { numVals = rangeSweep.generateMaps().size(); }
			catch(Exception e){assert(false);}
			
			if(first)
			{
				numValsReq = numVals;
				first = false;
			}
			else if(numVals != numValsReq)
				parseFail(arg);
		}
		
		return sweep;
	}
	
	Sweep parseParallelListSweep(String arg, String[] names, String rhs) throws GridSweeperException
	{
		ParallelCombinationSweep sweep = new ParallelCombinationSweep();
		
		// Split at comma boundaries
		String[] commaPieces = rhs.split("\\s*,\\s*");
		assert(commaPieces.length > 0);
		
		List<String[]> commaWhitespacePieces = new ArrayList<String []>();
		boolean allMatchNameCount = true;
		for(String commaPiece : commaPieces)
		{
			// If empty string, give up
			if(commaPiece.equals("")) parseFail(arg);
			
			String[] whitespacePieces = commaPiece.split("\\s+");
			assert(whitespacePieces.length > 0);
			
			// If any lengths are different from the number of names,
			// this can't be a list in the format
			// p1 p2 p3 = p1v1 p2v1 p3v1, p1v2 p2v2 p3v2, p1v3 p2v3 p3v3, ...
			if(allMatchNameCount && whitespacePieces.length != names.length) allMatchNameCount = false;
			
			commaWhitespacePieces.add(whitespacePieces);
		}
		
		// If every whitespace split has the same number of pieces as the name count,
		// then we'll interpret this as a list of this type:
		// p1 p2 p3 = p1v1 p2v1 p3v1, p1v2 p2v2 p3v2, p1v3 p2v3 p3v3, ...
		if(allMatchNameCount)
		{
			for(int i = 0; i < names.length; i++)
			{
				StringList values = new StringList();
				for(String[] whitespacePieces : commaWhitespacePieces)
				{
					String piece = whitespacePieces[i];
					if(piece.equals("")) parseFail(arg);
					values.add(unescape(piece));
				}
				
				ListSweep subSweep = new ListSweep(names[i], values);
				sweep.add(subSweep);
			}
		}
		else
		{
			// Otherwise, we'll assume the list is of this type:
			// p1 p2 p3 p4 p5 = p1v1,p1v2,p1v3,p1v4 p2v1,p2v2,p2v3,p2v4 p3v1,p3v2,p3v3,p3v4 p4v1,p4v2,p4v3,p4v4 p5v1,p5v2,p5v3,p5v4
			// meaning the commaWhitespacePieces arrays will have counts, e.g.,
			// 1 1 1 2 1 1 2 1 1 2 1 1 2 1 1 1
			
			// First, figure out how many values each list should have
			// by looking for the first instance of a 2-count array
			int numVals = 0;
			int size = commaWhitespacePieces.size();
			for(int i = 0; i < size; i++)
			{
				numVals++;
				String[] whitespacePieces = commaWhitespacePieces.get(i);
				if(whitespacePieces.length == 2) break;
				else if(whitespacePieces.length != 1) parseFail(arg);
			}
			
			// Verify that the total length of the commonWhitespacePieces list is correct
			// Analogous to the situation with parallel range list sweeps,
			// there should be this many items:
			// numVals * names.length
			// - (names.length - 1) (pieces at boundaries between
			//                         ranges are shared)
			// = (numVals - 1) * names.length + 1
			// Sanity check for example above: numVals = 4, names.length = 5,
			// so # items should = (4 - 1) * 5 + 1 = 16. Check.
			if(size != (numVals - 1) * names.length + 1) parseFail(arg);
			
			// For each parameter name...
			for(int i = 0; i < names.length; i++)
			{
				StringList values = new StringList();
				
				// Get each value in the list, with special treatment at boundaries
				for(int j = 0; j < numVals; j++)
				{
					String piece;
					if(j == 0)
					{
						String[] whitespacePieces = commaWhitespacePieces.get(i * (numVals - 1));
						if(i == 0)
						{
							if(whitespacePieces.length != 1) parseFail(arg);
							piece = whitespacePieces[0];
						}
						else
						{
							if(whitespacePieces.length != 2) parseFail(arg);
							piece = whitespacePieces[1];
						}
					}
					else if(j == numVals - 1)
					{
						String[] whitespacePieces = commaWhitespacePieces.get((i + 1) * (numVals - 1));
						if(i == names.length - 1)
						{
							if(whitespacePieces.length != 1) parseFail(arg);
						}
						else
						{
							if(whitespacePieces.length != 2) parseFail(arg);
						}
						piece = whitespacePieces[0];
					}
					else
					{
						String[] whitespacePieces = commaWhitespacePieces.get(i * (numVals - 1) + j);
						if(whitespacePieces.length != 1) parseFail(arg);
						piece = whitespacePieces[0];
					}
					
					if(piece.equals("")) parseFail(arg);
					values.add(unescape(piece));
				}
				
				sweep.add(new ListSweep(names[i], values));
			}
		}
		
		return sweep;
	}
	
	Sweep parseSingleSweep(String arg, String name, String rhs) throws GridSweeperException
	{
		// Check for pieces separated by colons on RHS
		String[] rhsColonPieces = rhs.split("\\s*:\\s*");
		assert(rhsColonPieces.length > 0);
		if(rhsColonPieces.length > 1)
		{
			// Parse as a range sweep
			return parseRangeSweep(arg, name, rhsColonPieces);
		}
		else
		{
			// Parse as a list or single-value sweep
			
			// Check for pieces separated by commas on RHS
			String[] rhsCommaPieces = rhs.split("\\s*,\\s*");
			assert(rhsCommaPieces.length > 0);
			if(rhsCommaPieces.length > 1)
			{
				// Parse as a list sweep
				return parseListSweep(arg, name, rhsCommaPieces);
			}
			else
			{
				return parseSingleValueSweep(arg, name, rhs); 
			}
		}
	}

	Sweep parseRangeSweep(String arg, String name, String[] rhsColonPieces) throws GridSweeperException
	{
		// Must have exactly three pieces
		if(rhsColonPieces.length != 3)
		{
			parseFail(arg);
		}
		
		// Reject if any pieces are the empty string or non-numeric
		for(int i = 0; i < rhsColonPieces.length; i++)
		{
			if(rhsColonPieces[i].equals(""))
			{
				parseFail(arg);
			}
			
			numMatcher.reset(rhsColonPieces[i]);
			if(!numMatcher.matches())
			{
				parseFail(arg);
			}
		}
		
		BigDecimal start = new BigDecimal(rhsColonPieces[0]);
		BigDecimal incr = new BigDecimal(rhsColonPieces[1]);
		BigDecimal end = new BigDecimal(rhsColonPieces[2]);
		
		return new RangeListSweep(name, start, end, incr);
	}

	Sweep parseListSweep(String arg, String name, String[] values) throws GridSweeperException
	{
		// Whitespace-trim, unescape, and reject if any pieces are the empty string
		for(int i = 0; i < values.length; i++)
		{
			values[i] = unescape(values[i].trim());
			if(values[i].equals(""))
			{
				parseFail(arg);
			}
		}
		
		return new ListSweep(name, new StringList(values));
	}

	Sweep parseSingleValueSweep(String arg, String name, String value) throws GridSweeperException
	{
		return new SingleValueSweep(name, unescape(value));
	}
	
	private void parseFail(String arg) throws GridSweeperException
	{
		throw new GridSweeperException("Invalid sweep specification: " + arg);
	}
	
	/**
	 * Loads the experiment from the provided XML file.
	 * @throws GridSweeperException If the experiment path is not provided,
	 * or if the file cannot be loaded or parsed.
	 */
	private void loadExperiment() throws GridSweeperException
	{
		Settings sharedSettings = Settings.sharedSettings();
		
		entering(className, "loadExperiment");
		
		if(experimentPath == null)
		{
			experiment = new Experiment(sharedSettings);
		}
		
		else try
		{
			experiment = new Experiment(sharedSettings,
					new java.net.URL("file", "", experimentPath));
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not load experiment file.", e);
		}
		
		exiting(className, "loadExperiment");
	}
}
