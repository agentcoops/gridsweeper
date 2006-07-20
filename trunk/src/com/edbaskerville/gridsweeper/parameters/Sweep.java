package com.edbaskerville.gridsweeper.parameters;

public interface Sweep
{
	public java.util.List<ParameterMap> generateMaps() throws SweepLengthException, DuplicateParameterException;
}
