package com.edbaskerville.gridsweeper;

import java.util.*;

public class RunInfo
{
	private String remoteCommand;
	private List<String> arguments;
	private Map<String, String> environment;
	private List<CaseInfo> cases;
	
	public RunInfo()
	{
		arguments = new ArrayList<String>();
		environment = new HashMap<String, String>();
		cases = new ArrayList<CaseInfo>();
	}
	
	public class CaseInfo
	{
		private List<String> moreArguments;
		
		public CaseInfo()
		{
			moreArguments = new ArrayList<String>();
		}

		public List<String> getMoreArguments()
		{
			return moreArguments;
		}

		public void setMoreArguments(List<String> moreArguments)
		{
			this.moreArguments = moreArguments;
		}
	}

	public List<String> getArguments()
	{
		return arguments;
	}

	public void setArguments(List<String> arguments)
	{
		this.arguments = arguments;
	}

	public List<CaseInfo> getCases()
	{
		return cases;
	}

	public void setCases(List<CaseInfo> cases)
	{
		this.cases = cases;
	}

	public Map<String, String> getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(Map<String, String> environment)
	{
		this.environment = environment;
	}

	public String getRemoteCommand()
	{
		return remoteCommand;
	}

	public void setRemoteCommand(String remoteCommand)
	{
		this.remoteCommand = remoteCommand;
	}
}
