package com.edbaskerville.gridsweeper;

public interface Sweep
{
	public java.util.List<ParameterMap> generateMaps() throws SweepLengthException, DuplicateParameterException;
}
