package com.edbaskerville.gridsweeper.parameters;

import java.util.Random;

public interface Sweep
{
	public java.util.List<ParameterMap> generateMaps(Random rng) throws SweepLengthException, DuplicateParameterException;
}
