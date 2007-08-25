package edu.umich.lsa.cscs.gridsweeper.example;

import edu.umich.lsa.cscs.gridsweeper.*;
import static edu.umich.lsa.cscs.gridsweeper.StringUtils.*;
import java.util.Random;
import java.io.PrintStream;
import java.io.FileOutputStream;

public class SampleAdapter implements Adapter
{
	String message;
	
	public SampleAdapter(Settings settings) throws AdapterException
	{
		message = settings.getProperty("message");
		if(message == null)
		{
			throw new AdapterException("The \"message\" property must be specified.");
		}
	}
	
	public RunResults run(ParameterMap parameterMap, int runNumber, int numRuns, int rngSeed) throws AdapterException
	{
		try
		{
			Random rng = new Random((long)rngSeed);
			
			PrintStream ostream = new PrintStream(new FileOutputStream("output." + formatPaddedInt(runNumber, numRuns - 1)));
			ostream.println("Message: " + message);
			ostream.println("A random number: " + rng.nextGaussian());
			
			for(String name : parameterMap.keySet())
			{
				ostream.println(name + " = " + parameterMap.get(name));
			}
			
			ostream.close();
			
			return new RunResults(0, message, null, null);
		}
		catch(Exception e)
		{
			throw new AdapterException("Received exception running things.", e);
		}
	}
}
