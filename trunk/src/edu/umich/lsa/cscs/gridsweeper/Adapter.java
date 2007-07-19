package edu.umich.lsa.cscs.gridsweeper;

import java.math.BigInteger;

import edu.umich.lsa.cscs.gridsweeper.parameters.ParameterMap;

/**
 * The interface for adapters, which execute a model from a
 * set of parameters and return results to the GridSweeperRunner object.
 * Java interfaces cannot specify constructors, but <code>Adapter</code>
 * objects are assumed to support a single-argument constructor with signature
 * {@code Adapter(java.util.Properties properties)}.
 * @author Ed Baskerville
 *
 */
public interface Adapter 
{
	/**
	 * Executes a model and returns results. 
	 * @param parameterMap The parameter assignments for this run.
	 * @param runNumber A number that identifies the run among multiple
	 * runs with the same parameter settings. This number may be used
	 * to name output files, for example.
	 * @param rngSeed The seed for the model's random number generator.
	 * @return A {@link RunResults} object containing the process exit status,
	 * a string message describing the run, and data from standard input
	 * and standard error.
	 * @throws AdapterException For any reason defined by the implementing class.
	 */
	public RunResults run(ParameterMap parameterMap, int runNumber, BigInteger rngSeed) throws AdapterException;
}
