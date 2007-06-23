package com.edbaskerville.gridsweeper.parameters;

import java.util.Random;

/**
 * The root interface for parameter sweeps. Defines one method, <code>generateMaps()</code>,
 * which generates parameter name/value maps from parameter sweep settings.
 * @author Ed Baskerville 
 */
public interface Sweep
{
	/**
	 * Generates an enumerated list of {@link ParameterMap} objects, each of which
	 * maps parameters to values. The resulting maps can be passed on to model runs through
	 * {@link com.edbaskerville.gridsweeper.Adapter} objects.
	 * @param rng The random number generator to use (for stochastic sweeps).
	 * @return A list of {@code ParameterMap} objects, one for each set of parameter/value
	 * assignments.
	 * @throws SweepLengthException If a length requirement&mdash;e.g., child sweeps
	 * must be the same length&mdash;is violated. 
	 * @throws DuplicateParameterException If child sweeps generate duplicate/conflicting
	 * settings for a parameter.
	 */
	public java.util.List<ParameterMap> generateMaps(Random rng) throws SweepLengthException, DuplicateParameterException;
}
