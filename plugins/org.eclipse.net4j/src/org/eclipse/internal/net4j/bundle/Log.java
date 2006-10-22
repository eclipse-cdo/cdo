package org.eclipse.internal.net4j.util.bundle;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;


public final class Log
{
  private static ServiceTracker logTracker;

  private Log()
  {
  };

  static void init(BundleContext bc)
  {
    logTracker = new ServiceTracker(bc, LogService.class.getName(), null);
    logTracker.open();
  }

  static void dispose()
  {
    if (logTracker != null)
    {
      logTracker.close();
      logTracker = null;
    }
  }

  public static void debug(String message)
  {
    log(LogService.LOG_DEBUG, message, null);
  }

  public static void debug(String message, Throwable t)
  {
    log(LogService.LOG_DEBUG, message, null);
  }

  public static void info(String message)
  {
    log(LogService.LOG_INFO, message, null);
  }

  public static void info(String message, Throwable t)
  {
    log(LogService.LOG_INFO, message, null);
  }

  public static void warn(String message)
  {
    log(LogService.LOG_WARNING, message, null);
  }

  public static void warn(String message, Throwable t)
  {
    log(LogService.LOG_WARNING, message, null);
  }

  public static void error(String message)
  {
    log(LogService.LOG_ERROR, message, null);
  }

  public static void error(String message, Throwable t)
  {
    log(LogService.LOG_ERROR, message, null);
  }

  public static void log(int level, String message)
  {
    log(level, message, null);
  }

  public static void log(int level, String message, Throwable t)
  {
    LogService logService = (LogService)logTracker.getService();
    if (logService != null)
    {
      logService.log(level, message, t);
    }
    else
    {
      switch (level)
      {
      case LogService.LOG_DEBUG:
        System.out.print("[DEBUG] ");
        break;

      case LogService.LOG_INFO:
        System.out.print("[INFO] ");
        break;

      case LogService.LOG_WARNING:
        System.out.print("[WARN] ");
        break;

      case LogService.LOG_ERROR:
        System.out.print("[ERROR] ");
        break;

      default:
        break;
      }

      System.out.println(message);
      if (t != null)
      {
        t.printStackTrace();
      }
    }
  }
}