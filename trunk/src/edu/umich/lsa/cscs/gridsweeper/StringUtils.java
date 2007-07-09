package edu.umich.lsa.cscs.gridsweeper;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * A collection of useful methods for manipulating strings.
 * TODO: Ensure that this code, and other code, is path separator-agnostic.
 * Somebody may want to run this on Windows someday (ugh).
 * @author Ed Baskerville
 *
 */
public class StringUtils
{
	private static String fileSep;
	
	static
	{
		fileSep = System.getProperty("file.separator");
	}
	
	static StringList tokenize(String string)
	{
		return tokenize(string, " ", true);
	}
	
	static StringList tokenize(String string, String delimiter, boolean unescape)
	{
		StringList tokens = new StringList();
		
		String[] tokensArray = string.split(delimiter);
		
		for(String token : tokensArray)
		{
			if(unescape) tokens.add(unescape(token));
			else tokens.add(token);
		}
		
		return tokens;
	}
	
	static String escape(String string, String chars)
	{
		Map<String, String> replacements = new HashMap<String, String>();
		for(int i = 0; i < chars.length(); i++)
		{
			String original = chars.substring(i, i+1);
			replacements.put(original, escape(original)); 
		}

		StringBuilder escapedStringBuilder = new StringBuilder();
		for(int i = 0; i < string.length(); i++)
		{
			String original = string.substring(i, i+1);
			if(replacements.containsKey(original))
			{
				escapedStringBuilder.append(replacements.get(original));
			}
			else
			{
				escapedStringBuilder.append(original);
			}
		}
		
		return escapedStringBuilder.toString();
	}
	
	static String escape(String string)
	{
		String escapedString;
		try
		{
			escapedString = java.net.URLEncoder.encode(string, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			return null;
		}
		return escapedString;
	}
	
	static String unescape(String string)
	{
		String unescapedString;
		try
		{
			unescapedString = java.net.URLDecoder.decode(string, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			return null;
		}
		return unescapedString;
	}
	
	static String replace(String target, String replacement)
	{
		int index = target.indexOf(replacement);
		if(index != -1)
		{
			String beginning = target.substring(0, index);
			String end = target.substring(index + replacement.length());
			
			return beginning + replacement + end; 
		}
		return target;
	}
	
	static String lastPathComponent(String string)
	{
		if(string == null) return null;
		if(string.equals(fileSep)) return string;
		
		int length = string.length();
		if(length == 0) return string;
		
		// Remove trailing slash
		if(string.substring(length - 1).equals(fileSep)) string = string.substring(0, length - 1);
		
		// Find final slash
		int finalSlashLoc = string.lastIndexOf(fileSep);
		if(finalSlashLoc == -1) return string;
		else return string.substring(finalSlashLoc + 1);
	}
	
	static String deleteLastPathComponent(String string)
	{
		if(string == null) return null;
		
		int length = string.length();
		if(length == 0) return string;
		if(string.equals(fileSep)) return string;
		
		StringBuffer stringBuf = new StringBuffer(string);
		
		// Remove trailing slash
		if(string.substring(length - 1).equals(fileSep)) stringBuf.setLength(--length);
		
		// Find final slash
		int finalSlashLoc = stringBuf.lastIndexOf(fileSep);
		
		// If no final slash, then we just turn this into the empty string
		if(finalSlashLoc == -1)
		{
			stringBuf.setLength(0);	
		}
		
		// If the final slash is an absolute path indicator, delete everything after that.
		else if(finalSlashLoc == 0)
		{
			stringBuf.setLength(finalSlashLoc + 1);
		}
		
		// If it's an internal path separator, delete it and everything after it.
		else
		{
			stringBuf.setLength(finalSlashLoc);
		}
		
		return stringBuf.toString();
	}

	
	static String expandTildeInPath(String path)
	{
		if(path.length() >= 2 && path.substring(0, 2).equals("~" + fileSep))
		{
			return System.getProperty("user.home") + path.substring(1);
		}
		return path;
	}

	public static String getFileSeparator()
	{
		return fileSep;
	}

	public static void setFileSeparator(String fileSeparator)
	{
		StringUtils.fileSep = fileSeparator;
	}
	
	public static String appendPathComponent(String path, String component)
	{
		if(path.equals(""))
		{
			return component;
		}
		
		int pathLength = path.length();
		boolean pathHasFinalSlash = (pathLength > 0)
			&& (path.substring(pathLength - 1).equals(fileSep));
		
		int componentLength = component.length();
		boolean componentHasStartingSlash = (componentLength > 0)
			&& (component.substring(0, 1).equals(fileSep));
		
		if(!(pathHasFinalSlash || componentHasStartingSlash))
		{
			return path + fileSep + component;
		}
		else if(pathHasFinalSlash && componentHasStartingSlash)
		{
			return path + component.substring(1);
		}
		else
		{
			return path + component;
		}
	}

	public static StringList pathComponents(String path)
	{
		StringList tokens = tokenize(path, "/", false);
		
		int size = tokens.size();
		
		// If there's a final slash, add it to the last item
		if(path.length() > 0 && path.charAt(path.length() - 1) == '/')
		{
			String lastItem = tokens.get(size - 1);
			tokens.remove(size - 1);
			tokens.add(lastItem + "/");
		}
		
		// Replace empty string caused by initial slash with actual slash
		if(size > 0 && tokens.get(0).equals(""))
		{
			tokens.set(0, "/");
		}
		
		return tokens;
	}
}
