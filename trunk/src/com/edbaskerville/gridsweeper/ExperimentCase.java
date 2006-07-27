package com.edbaskerville.gridsweeper;

import java.util.*;
import com.edbaskerville.gridsweeper.parameters.*;

public class ExperimentCase
{
	Map<String, String> settings;
	Map<String, String> abbreviations;
	ParameterMap map;
	List<Long> rngSeeds;
	
	public ExperimentCase(Map<String, String> settings, Map<String, String> abbreviations, ParameterMap map, List<Long> rngSeeds)
	{
		this.settings = settings;
		this.abbreviations = abbreviations;
		this.map = map;
		this.rngSeeds = rngSeeds;
	}

	public Map<String, String> getSettings()
	{
		return settings;
	}
	
	public Map<String, String> getAbbreviations()
	{
		return abbreviations;
	}

	public ParameterMap getMap()
	{
		return map;
	}

	public List<Long> getRngSeeds()
	{
		return rngSeeds;
	}
}
