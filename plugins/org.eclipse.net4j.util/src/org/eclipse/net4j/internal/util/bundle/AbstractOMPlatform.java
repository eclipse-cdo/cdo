/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.bundle;

import org.eclipse.net4j.util.IOUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMLogHandler;
import org.eclipse.net4j.util.om.OMLogger;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OMTraceHandler;
import org.eclipse.net4j.util.om.OMLogger.Level;
import org.eclipse.net4j.util.om.OMTraceHandler.Event;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOMPlatform implements OMPlatform
{
  public static final String SYSTEM_PROPERTY_NET4J_CONFIG = "net4j.config"; //$NON-NLS-1$

  static Object systemContext;

  private static ContextTracer __TRACER__;

  private Map<String, AbstractOMBundle> bundles = new ConcurrentHashMap(0);

  private Queue<OMLogHandler> logHandlers = new ConcurrentLinkedQueue();

  private Queue<OMTraceHandler> traceHandlers = new ConcurrentLinkedQueue();

  private boolean debugging;

  protected AbstractOMPlatform()
  {
    debugging = Boolean.parseBoolean(System.getProperty("debug", "false")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public synchronized OMBundle bundle(String bundleID, Class accessor)
  {
    OMBundle bundle = bundles.get(bundleID);
    if (bundle == null)
    {
      bundle = createBundle(bundleID, accessor);
    }

    return bundle;
  }

  public void addLogHandler(OMLogHandler logHandler)
  {
    if (!logHandlers.contains(logHandler))
    {
      logHandlers.add(logHandler);
    }
  }

  public void removeLogHandler(OMLogHandler logHandler)
  {
    logHandlers.remove(logHandler);
  }

  public void addTraceHandler(OMTraceHandler traceHandler)
  {
    if (!traceHandlers.contains(traceHandler))
    {
      traceHandlers.add(traceHandler);
    }
  }

  public void removeTraceHandler(OMTraceHandler traceHandler)
  {
    traceHandlers.remove(traceHandler);
  }

  public boolean isDebugging()
  {
    return debugging;
  }

  public void setDebugging(boolean debugging)
  {
    this.debugging = debugging;
  }

  public File getConfigFolder()
  {
    String config = System.getProperty(SYSTEM_PROPERTY_NET4J_CONFIG);
    if (config == null)
    {
      return null;
    }

    File configFolder = new File(config);
    if (!configFolder.exists())
    {
      if (!configFolder.mkdirs())
      {
        OM.LOG.error("Config folder " + config + " could not be created");
        return null;
      }
    }

    if (!configFolder.isDirectory())
    {
      OM.LOG.error("Config folder " + config + " is not a directoy");
      return null;
    }

    return configFolder;
  }

  public File getConfigFile(String name)
  {
    File configFolder = getConfigFolder();
    if (configFolder == null)
    {
      return null;
    }

    return new File(configFolder, name);
  }

  public Properties getConfigProperties(String name)
  {
    File configFile = getConfigFile(name);
    if (configFile == null)
    {
      return null;
    }

    FileInputStream fis = null;
    try
    {
      fis = new FileInputStream(configFile);
      Properties properties = new Properties();
      properties.load(fis);
      return properties;
    }
    catch (IOException ex)
    {
      OM.LOG.error("Config file " + configFile.getAbsolutePath() + " could not be read");
      return null;
    }
    finally
    {
      IOUtil.closeSilent(fis);
    }
  }

  protected void log(OMLogger logger, Level level, String msg, Throwable t)
  {
    for (OMLogHandler logHandler : logHandlers)
    {
      try
      {
        logHandler.logged(logger, level, msg, t);
      }
      catch (Exception ex)
      {
        if (TRACER().isEnabled())
        {
          TRACER().trace(ex);
        }
      }
    }
  }

  protected void trace(Event event)
  {
    for (OMTraceHandler traceHandler : traceHandlers)
    {
      try
      {
        traceHandler.traced(event);
      }
      catch (Exception ex)
      {
        if (TRACER().isEnabled())
        {
          TRACER().trace(ex);
        }
      }
    }
  }

  protected abstract OMBundle createBundle(String bundleID, Class accessor);

  protected abstract String getDebugOption(String bundleID, String option);

  protected abstract void setDebugOption(String bundleID, String option, String value);

  /**
   * TODO Make configurable via system property
   */
  public static synchronized OMPlatform createPlatform()
  {
    try
    {
      if (systemContext != null)
      {
        return new OSGiPlatform(systemContext);
      }

      return new LegacyPlatform();
    }
    catch (Exception ex)
    {
      if (TRACER().isEnabled())
      {
        TRACER().trace(ex);
      }
    }

    return null;
  }

  private static ContextTracer TRACER()
  {
    if (__TRACER__ == null)
    {
      __TRACER__ = new ContextTracer(OM.DEBUG_OM, AbstractOMPlatform.class);
    }

    return __TRACER__;
  }
}