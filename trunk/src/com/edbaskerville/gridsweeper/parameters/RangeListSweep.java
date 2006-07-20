package com.edbaskerville.gridsweeper.parameters;

import java.util.*;
import java.math.*;

public class RangeListSweep extends SingleSweep
{
	BigDecimal start;
	BigDecimal end;
	BigDecimal increment;
	
	public RangeListSweep(String name, BigDecimal start, BigDecimal end, BigDecimal increment)
	{
		super(name);
		
		this.start = start;
		this.end = end;
		this.increment = increment.abs();
	}

	@Override
	public List<ParameterMap> generateMaps()
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>();
		
		// If increment is zero, return two values: start and end
		if(increment.compareTo(BigDecimal.ZERO) == 0)
		{
			maps.add(new ParameterMap(name, start));
			
			if(!(start.compareTo(end) == 0))
			{
				maps.add(new ParameterMap(name, end));
			}
		}
		
		// If start <= end, then increase by increment
		else if(start.compareTo(end) <= 0)
		for(BigDecimal value = start; value.compareTo(end) <= 0; value = value.add(increment))
		{
			maps.add(new ParameterMap(name, value));
		}
		
		// Otherwise, decrease by increment
		else
			for(BigDecimal value = start; value.compareTo(end) >= 0; value = value.subtract(increment))
		{
			maps.add(new ParameterMap(name, value));
		}
		
		return maps;
	}
}
