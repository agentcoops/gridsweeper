package com.edbaskerville.gridsweeper;

import java.util.logging.*;

/**
 * A utility class wrapping the standard Java logging system with static methods.
 * @author Ed Baskerville
 *
 */
public class Logger
{
	private static java.util.logging.Logger logger;
	
	static
	{
		logger = java.util.logging.Logger.getLogger("com.edbaskerville.gridsweeper");
		logger.setLevel(Level.ALL);
		
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
	}
	
	public static java.util.logging.Logger getLogger()
	{
		return logger;
	}
	
	public static void config(String msg)
	{
		logger.config(msg);
	}

	public static void entering(String sourceClass, String sourceMethod, Object param1)
	{
		logger.entering(sourceClass, sourceMethod, param1);
	}

	public static void entering(String sourceClass, String sourceMethod, Object[] params)
	{
		logger.entering(sourceClass, sourceMethod, params);
	}

	public static void entering(String sourceClass, String sourceMethod)
	{
		logger.entering(sourceClass, sourceMethod);
	}

	public static void exiting(String sourceClass, String sourceMethod, Object result)
	{
		logger.exiting(sourceClass, sourceMethod, result);
	}

	public static void exiting(String sourceClass, String sourceMethod)
	{
		logger.exiting(sourceClass, sourceMethod);
	}

	public static void fine(String msg)
	{
		logger.fine(msg);
	}

	public static void finer(String msg)
	{
		logger.finer(msg);
	}

	public static void finest(String msg)
	{
		logger.finest(msg);
	}

	public static void info(String msg)
	{
		logger.info(msg);
	}

	public static void log(Level level, String msg, Object param1)
	{
		logger.log(level, msg, param1);
	}

	public static void log(Level level, String msg, Object[] params)
	{
		logger.log(level, msg, params);
	}

	public static void log(Level level, String msg, Throwable thrown)
	{
		logger.log(level, msg, thrown);
	}

	public static void log(Level level, String msg)
	{
		logger.log(level, msg);
	}

	public static void log(LogRecord record)
	{
		logger.log(record);
	}

	public static void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1)
	{
		logger.logp(level, sourceClass, sourceMethod, msg, param1);
	}

	public static void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params)
	{
		logger.logp(level, sourceClass, sourceMethod, msg, params);
	}

	public static void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown)
	{
		logger.logp(level, sourceClass, sourceMethod, msg, thrown);
	}

	public static void logp(Level level, String sourceClass, String sourceMethod, String msg)
	{
		logger.logp(level, sourceClass, sourceMethod, msg);
	}

	public static void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1)
	{
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
	}

	public static void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params)
	{
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
	}

	public static void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown)
	{
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
	}

	public static void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg)
	{
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg);
	}

	public static void severe(String msg)
	{
		logger.severe(msg);
	}

	public static void throwing(String sourceClass, String sourceMethod, Throwable thrown)
	{
		logger.throwing(sourceClass, sourceMethod, thrown);
	}

	public static void warning(String msg)
	{
		logger.warning(msg);
	}
	
}
