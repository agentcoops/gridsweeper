package com.edbaskerville.gridsweeper;

//import java.util.*;
//import java.io.*;

public class GridController
{
	private GridDelegate delegate;
	
	public GridController()
	{
		super();
	}
	
	public void setDelegate(GridDelegate delegate)
	{
		this.delegate = delegate;
	}
	
	public GridDelegate getDelegate()
	{
		return delegate;
	}
	
	public void submitExperiment(Experiment experiment, String adapterClassName, boolean waitForCompletion)
	{
		//Properties properties = experiment.getProperties();
		
		// Get stdinData
		/*byte[] stdinData = null;
		String stdinPath = properties.getProperty("stdinPath");
		if(stdinPath != null)
		{
			try
			{
				FileInputStream inputStream = new FileInputStream(stdinPath);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				
				int b;
				while((b = inputStream.read()) != -1)
				{
					outputStream.write(b);
				}
				
				inputStream.close();
				outputStream.close();
				
				stdinData = outputStream.toByteArray();
			} catch(Exception e) {}
		}*/
		
		// Assemble cases
		
		// Submit to grid
		
		// If asked for, wait for completion
	}
}
