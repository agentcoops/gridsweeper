/*
	RangeListSweep.java
	
	Part of GridSweeper
	Copyright (c) 2006 - 2007 Ed Baskerville <software@edbaskerville.com>

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package edu.umich.lsa.cscs.gridsweeper;

import java.util.*;
import java.math.*;


/**
 * Represents a range of values assigned to a parameter. All values are represented with
 * the BigDecimal class, so rounding errors are not a problem.
 * @author Ed Baskerville
 *
 */
class RangeListSweep extends SingleSweep
{
	/**
	 * The first value in the range. 
	 */
	BigDecimal start;
	
	/**
	 * The last value in the range.
	 */
	BigDecimal end;
	
	/**
	 * The increment between values in the range.
	 */
	BigDecimal increment;
	
	/**
	 * Initializes the parameter name and range of values to assign to the parameter.
	 * @param name The parameter name to use.
	 * @param start The first value in the range.
	 * @param end The last value in the range.
	 * @param increment The increment between values in the range.
	 */
	public RangeListSweep(String name, BigDecimal start, BigDecimal end, BigDecimal increment)
	{
		super(name);
		
		this.start = start;
		this.end = end;
		this.increment = increment.abs();
	}

	/**
	 * Generates the parameter maps for this sweep. One map will be created
	 * for each parameter assignment, and parameter assignments will include
	 * the start value and the start value plus all integer multiples of the increment
	 * up to the end value. The end value will be included if and only if it is exactly
	 * equal to the start value plus an integer multiple of the increment.
	 * If the end value is less than the start value, multiples of the increment
	 * will be subtracted rather than added. If the increment is zero,
	 * two values will be assigned: the start value and the end value. 
	 * @return A list of parameter maps, each containing one assignment
	 * of the parameter to a value in the range. 
	 */
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

	public BigDecimal getStart() {
		return start;
	}

	public void setStart(BigDecimal start) {
		this.start = start;
	}

	public BigDecimal getEnd() {
		return end;
	}

	public void setEnd(BigDecimal end) {
		this.end = end;
	}

	public BigDecimal getIncrement() {
		return increment;
	}

	public void setIncrement(BigDecimal increment) {
		this.increment = increment;
	}

	public void writeXML(XMLWriter xmlWriter)
	{
		StringMap attrs = new StringMap();
		attrs.put("param", getName());
		attrs.put("start", getStart().toString());
		attrs.put("end", getEnd().toString());
		attrs.put("increment", getIncrement().toString());
		
		xmlWriter.printTagStart("range", attrs, true);
	}
}
