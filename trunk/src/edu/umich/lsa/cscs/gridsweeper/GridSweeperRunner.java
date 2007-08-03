package edu.umich.lsa.cscs.gridsweeper;

import java.io.*;

import static edu.umich.lsa.cscs.gridsweeper.StringUtils.*;

import edu.umich.lsa.cscs.gridsweeper.parameters.ParameterMap;

/**
 * The GridSweeperRunner command-line tool to actually run the model
 * on the execution host.
 * @author Ed Baskerville
 *
 */
public class GridSweeperRunner
{
	/**
	 * Runs the model. First, reads the {@link RunSetup} object from standard input,
	 * and extracts settings for the run. If file transfer is on, input files
	 * are then downloaded from the file transfer system. Then an adapter object
	 * is created as specified in the run setup and used to actually run the model.
	 * Finally, if necessary, files are staged back to the file transfer system
	 * to be retrieved at the submission host. 
	 */
	public static void main(String[] args)
	{
		RunResults results;
		try
		{
			// Load RunSetup object
			ObjectInputStream stdinStream = new ObjectInputStream(System.in);
			RunSetup setup = (RunSetup)stdinStream.readObject();
			
			// Get GridSweeper settings
			Settings settings = setup.getSettings();
			
			// Download input files
			boolean useFileTransfer = settings.getBooleanProperty("UseFileTransfer");
			FileTransferSystem fts = null;
			if(useFileTransfer)
			{
				String className = settings.getSetting("FileTransferSystemClassName");
				Settings ftsSettings = settings.getSettingsForClass(className);
				fts = FileTransferSystemFactory.getFactory().getFileTransferSystem(className, ftsSettings);
				fts.connect();
				
				StringMap inputFiles = setup.getInputFiles();
				
				for(String key : inputFiles.keySet())
				{
					String path = inputFiles.get(key);
					String fileTransferSubpath = appendPathComponent(setup.getFileTransferSubpath(), "input");
					
					String remotePath = appendPathComponent(fileTransferSubpath, path);
					String localPath = path;
					
					fts.downloadFile(remotePath, localPath);
				}
				
				fts.disconnect();
			}
			
			String adapterClassName = settings.getSetting("AdapterClass");
			Settings adapterSettings = settings.getSettingsForClass(adapterClassName);
			Adapter adapter = AdapterFactory.createAdapter(adapterClassName, adapterSettings);
			
			// Run!
			ParameterMap parameters = setup.getParameters();
			int runNumber = setup.getRunNumber();
			int numRuns = setup.getNumRuns();
			int rngSeed = setup.getRngSeed();
			results = adapter.run(parameters, runNumber, rngSeed);
			
			// Stage files listed in run properties back to server (if asked for)
			if(useFileTransfer)
			{
				fts.connect();
				
				StringList outputFiles = setup.getOutputFiles();
				
				for(String outputFile : outputFiles)
				{
					String fileTransferSubpath = setup.getFileTransferSubpath();
					String remotePath = appendPathComponent(fileTransferSubpath, outputFile);
					
					fts.uploadFile(outputFile, remotePath);
				}
				
				fts.disconnect();
			}
			
			// If file transfer is off, write standard output and standard error
			// to local files.
			// If file transfer is on, this will happen at the client end of things
			if(!useFileTransfer)
			{
				String rnStr = getRNString(numRuns, runNumber);
				
				String stdoutFilename = "stdout." + rnStr;
				byte[] stdoutData = results.getStdoutData();
				if(stdoutData.length > 0)
					writeData(stdoutFilename, stdoutData);
				
				String stderrFilename = "stderr." + rnStr;
				byte[] stderrData = results.getStderrData();
				if(stderrData.length > 0)
					writeData(stderrFilename, stderrData);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			results = new RunResults(e); 
		}
		
		// Write results to stdout
		// Set up ObjectOutputStream to write RunResults object
		try
		{
			ObjectOutputStream stdoutStream = new ObjectOutputStream(System.out);
			stdoutStream.writeObject(results);
		}
		catch(Exception e) {}
	}
	
	private static String getRNString(int numRuns, int runNumber)
	{
		String numRunsStr = "" + numRuns;
		String rnNoZerosStr = "" + runNumber;
		
		StringBuffer rnStrBuf = new StringBuffer();
		int diff = numRunsStr.length() - rnNoZerosStr.length();
		for(int i = 0; i < diff; i++)
		{
			rnStrBuf.append("0");
		}
		rnStrBuf.append(rnNoZerosStr);
		return rnStrBuf.toString();
	}

	private static void writeData(String filename, byte[] data) throws IOException
	{
		OutputStream os = new BufferedOutputStream(new FileOutputStream(filename));
		os.write(data);
		os.close();
	}
}
