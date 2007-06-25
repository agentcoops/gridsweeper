package com.edbaskerville.gridsweeper;

import java.util.*;
import com.edbaskerville.gridsweeper.parameters.*;

/**
 * Represents a single case of an experiment, including parameter settings
 * and a list of random seeds, one for each run of the case.
 * TODO: There's a major design bug in how these are represented. Stochastic
 * parameter settings will not vary from run to run with this representation.
 * The solution: associate a different parameter map for every run?
 * Seems wasteful, but is certainly the simplest solution.
 * @author Ed Baskerville
 *
 */
public class ExperimentCase
{
	ParameterMap parameterMap;
	List<Long> rngSeeds;
	
	/**
	 * Initializes the experiment case with the parameter settings and random seeds.
	 * @param parameterMap The parameter assignments.
	 * @param rngSeeds The random seeds.
	 */
	public ExperimentCase(ParameterMap parameterMap, List<Long> rngSeeds)
	{
		this.parameterMap = parameterMap;
		this.rngSeeds = rngSeeds;
	}

	/**
	 * Getter for the parameter assignments for this case.
	 * @return The parameter assignments for this case.
	 */
	public ParameterMap getParameterMap()
	{
		return parameterMap;
	}

	/**
	 * Getter for the list of random seeds.
	 * @return The list of random seeds.
	 */
	public List<Long> getRngSeeds()
	{
		return rngSeeds;
	}
}
