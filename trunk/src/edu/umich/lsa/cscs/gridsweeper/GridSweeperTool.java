/**
 * 
 */
package edu.umich.lsa.cscs.gridsweeper;

import static edu.umich.lsa.cscs.gridsweeper.DLogger.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

import edu.umich.lsa.cscs.gridsweeper.*;
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
		COMMAND,
		INPUT,
		OUTPUT,
		RUNTYPE
	}
	
	
	static String className;

	static
	{
		className = GridSweeperTool.class.getName();
	}
	
	GridSweeper gs;
	
	String experimentPath;
	String outputPath;
	Experiment experiment;


	public static void main(String[] args) throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		tool.run(args);
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
		List<Sweep> cliSweeps = new ArrayList<Sweep>();
		parseArgs(args, cliSettings, cliSweeps);
		
		// Load experiment file
		loadExperiment();
		
		// Combine settings from command-line arguments and experiment
		gs.getSettings().putAll(experiment.getSettings());
		gs.getSettings().putAll(cliSettings);
		for(Sweep sweep : cliSweeps)
		{
			experiment.getRootSweep().add(sweep);
		}
		
		gs.setExperiment(experiment);
		
		// Generate experiment cases, etc.
		gs.setUpExperiment();
		
		// Run jobs
		gs.runJobs();
		
		// Finish running jobs
		gs.finish();
		
		exiting(className, "main");
	}
	
	/**
	 * <p>Parses command-line arguments. Currently only handles -a (adapter class),
	 * -e (experiment file path), and -d (whether to perform a dry run).</p>
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
	 * Only the first {@code -a} argument will be used. When GridSweeper is run
	 * using the {@code gdrone} tool, {@code -a} is already provided, and any
	 * provided by the user will be ignored.
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
	 * {@code gsweep -e <experimentXMLPath>}
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
	 * <td>-r, --runtype</td>
	 * <td>
	 * <p>Run style, either {@code run}, {@code dry}, or {@code norun}.
	 * Defaults to {@code run}. If {@code dry} is specified, a “dry run”
	 * is performed, simulating the parameter sweep without actually 
	 * submitting jobs to the grid to be run. Output directories are created
	 * and are populated with case files needed to reproduce each case,
	 * so you can test that all the parameter settings are correct before
	 * running the experiment for real. If {@code norun} is specified,
	 * the only effect of this command will be to generate a new experiment
	 * XML file as provided with the {@code -o} option.
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
	private void parseArgs(String[] args, Settings cliSettings, List<Sweep> cliSweeps)
		throws GridSweeperException
	{
		Matcher singleValueSweepMatcher;
		Matcher rangeSweepMatcher;
		
		// Create argument parsing regexes
		String nws = "(\\S+)"; // non-whitespace, at least one character, with group
		String ws = "\\s*";    // whitespace, zero or more, no group
		String num = "(-?\\d*\\.?\\d*)"; // signed decimal number
		
		singleValueSweepMatcher =
			Pattern.compile(nws + ws + "=" + ws + nws).matcher("");
		rangeSweepMatcher =
			Pattern.compile(nws+ws+"="+ws+num+ws+":"+ws+num+ws+":"+ws+num).matcher("");
		
		entering(className, "parseArgs");
		
		ArgState state = ArgState.START;
		
		for(String arg : args)
		{
			switch(state)
			{
				case START:
					if(arg.equals("-a") || arg.equals("--adapter"))
						state = ArgState.ADAPTER;
					else if(arg.equals("-c") || arg.equals("--command"))
						state = ArgState.COMMAND;
					else if(arg.equals("-i") || arg.equals("--input"))
						state = ArgState.INPUT;
					else if(arg.equals("-o") || arg.equals("--output"))
						state = ArgState.OUTPUT;
					else if(arg.equals("-r") || arg.equals("--runtype"))
						state = ArgState.RUNTYPE;
					else if(arg.equals("--debug"))
					{
						DLogger.addConsoleHandler(Level.ALL);
					}
					else
					{
						singleValueSweepMatcher.reset(arg);
						rangeSweepMatcher.reset(arg);

						if(rangeSweepMatcher.matches())
						{
							String name = rangeSweepMatcher.group(1);
							BigDecimal start = new BigDecimal(rangeSweepMatcher.group(2));
							BigDecimal incr = new BigDecimal(rangeSweepMatcher.group(3));
							BigDecimal end = new BigDecimal(rangeSweepMatcher.group(4));
							
							cliSweeps.add(new RangeListSweep(name, start, end, incr));
						}
						else if(singleValueSweepMatcher.matches())
						{
							String name = singleValueSweepMatcher.group(1);
							String value = singleValueSweepMatcher.group(2);
							fine("Matched parameter " + name + "=" + value);
							
							cliSweeps.add(new SingleValueSweep(name, value));
						}
					}
					break;
				case ADAPTER:
					if(!cliSettings.contains("AdapterClass"))
						cliSettings.put("AdapterClass", arg);
					state = ArgState.START;
					break;
				case COMMAND:
					if(!cliSettings.contains("command"))
						cliSettings.put("command", arg);
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
				case RUNTYPE:
					if(arg.equalsIgnoreCase("run"))
					{
						gs.setRunType(RunType.RUN);
					}
					else if(arg.equalsIgnoreCase("dry"))
					{
						gs.setRunType(RunType.DRY);
					}
					else if(arg.equalsIgnoreCase("norun"))
					{
						gs.setRunType(RunType.NORUN);
					}
					else
					{
						throw new GridSweeperException("Invalid run type " + arg + "specified.");
					}
					break;
			}
		}
		
		exiting(className, "parseArgs");
	}
	
	/**
	 * Loads the experiment from the provided XML file.
	 * @throws GridSweeperException If the experiment path is not provided,
	 * or if the file cannot be loaded or parsed.
	 */
	private void loadExperiment() throws GridSweeperException
	{
		entering(className, "loadExperiment");
		
		if(experimentPath == null)
		{
			experiment = new Experiment();
		}
		
		try
		{
			experiment = new Experiment(new java.net.URL("file", "", experimentPath));
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not load experiment file.", e);
		}
		
		exiting(className, "loadExperiment");
	}
}
