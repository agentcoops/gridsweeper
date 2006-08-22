package com.edbaskerville.gridsweeper;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class StringUtils
{
	static List<String> tokenize(String string)
	{
		return tokenize(string, " ", true);
	}
	
	static List<String> tokenize(String string, String delimiter, boolean unescape)
	{
		List<String> tokens = new ArrayList<String>();
		
		StringTokenizer tokenizer = new StringTokenizer(string, delimiter);
		
		while(tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if(unescape)
			{
				token = unescape(token);
			}
			tokens.add(token);
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

		StringBuffer escapedStringBuffer = new StringBuffer();
		for(int i = 0; i < string.length(); i++)
		{
			String original = string.substring(i, i+1);
			if(replacements.containsKey(original))
			{
				escapedStringBuffer.append(replacements.get(original));
			}
			else
			{
				escapedStringBuffer.append(original);
			}
		}
		
		return escapedStringBuffer.toString();
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
}
