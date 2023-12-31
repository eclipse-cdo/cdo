/*
 * Copyright (c) 2007-2012, 2015, 2016, 2018-2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.bundle;

import org.eclipse.net4j.internal.util.om.InternalOMJob;
import org.eclipse.net4j.internal.util.om.LegacyPlatform;
import org.eclipse.net4j.internal.util.om.OSGiPlatform;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogFilter;
import org.eclipse.net4j.util.om.log.OMLogHandler;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.log.OMLogger.Level;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.OMTraceHandler;
import org.eclipse.net4j.util.om.trace.OMTraceHandlerEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public abstract class AbstractPlatform implements OMPlatform
{
  public static final String SYSTEM_PROPERTY_OSGI_STATE = "osgi.instance.area"; //$NON-NLS-1$

  public static final String SYSTEM_PROPERTY_NET4J_STATE = "net4j.state"; //$NON-NLS-1$

  public static final String SYSTEM_PROPERTY_NET4J_CONFIG = "net4j.config"; //$NON-NLS-1$

  public static final String SYSTEM_PROPERTY_NET4J_USER_DIR = "net4j.user.dir"; //$NON-NLS-1$

  static Object systemContext;

  private static ContextTracer __TRACER__;

  private final Map<String, AbstractBundle> bundles = new ConcurrentHashMap<>(0);

  private final ConcurrentArray<OMLogFilter> logFilters = new ConcurrentArray.Unique<OMLogFilter>()
  {
    @Override
    protected OMLogFilter[] newArray(int length)
    {
      return new OMLogFilter[length];
    }
  };

  private final ConcurrentArray<OMLogHandler> logHandlers = new ConcurrentArray.Unique<OMLogHandler>()
  {
    @Override
    protected OMLogHandler[] newArray(int length)
    {
      return new OMLogHandler[length];
    }
  };

  private final ConcurrentArray<OMTraceHandler> traceHandlers = new ConcurrentArray.Unique<OMTraceHandler>()
  {
    @Override
    protected OMTraceHandler[] newArray(int length)
    {
      return new OMTraceHandler[length];
    }
  };

  private final File userFolder = initUserFolder();

  private boolean debugging;

  protected AbstractPlatform()
  {
    debugging = isProperty("debug"); //$NON-NLS-1$
  }

  private File initUserFolder()
  {
    try
    {
      String userDir = System.getProperty(SYSTEM_PROPERTY_NET4J_USER_DIR);
      if (userDir != null && userDir.length() != 0)
      {
        return new File(userDir);
      }
    }
    catch (Throwable ex)
    {
      //$FALL-THROUGH$
    }

    return new File(System.getProperty("user.home"), ".eclipse");
  }

  @Override
  public synchronized OMBundle bundle(String bundleID, Class<?> accessor)
  {
    AbstractBundle bundle = bundles.get(bundleID);
    if (bundle == null)
    {
      bundle = (AbstractBundle)createBundle(bundleID, accessor);
      bundles.put(bundleID, bundle);
    }
    else if (accessor != null)
    {
      if (bundle.getAccessor() == null)
      {
        bundle.setAccessor(accessor);
      }
    }

    return bundle;
  }

  @Override
  public void addLogFilter(OMLogFilter logFilter)
  {
    logFilters.add(logFilter);
  }

  @Override
  public void removeLogFilter(OMLogFilter logFilter)
  {
    logFilters.remove(logFilter);
  }

  @Override
  public void addLogHandler(OMLogHandler logHandler)
  {
    logHandlers.add(logHandler);
  }

  @Override
  public void removeLogHandler(OMLogHandler logHandler)
  {
    logHandlers.remove(logHandler);
  }

  @Override
  public void addTraceHandler(OMTraceHandler traceHandler)
  {
    traceHandlers.add(traceHandler);
  }

  @Override
  public void removeTraceHandler(OMTraceHandler traceHandler)
  {
    traceHandlers.remove(traceHandler);
  }

  @Override
  public boolean isExtensionRegistryAvailable()
  {
    try
    {
      return internalExtensionRegistryAvailable();
    }
    catch (Throwable ex)
    {
      return false;
    }
  }

  @Override
  public boolean isDebugging()
  {
    return debugging;
  }

  @Override
  public void setDebugging(boolean debugging)
  {
    this.debugging = debugging;
  }

  @Override
  public File getUserFolder()
  {
    return userFolder;
  }

  @Override
  public File getStateFolder()
  {
    String state = getProperty(SYSTEM_PROPERTY_NET4J_STATE);
    if (state == null)
    {
      state = getProperty(SYSTEM_PROPERTY_OSGI_STATE);
      if (state == null)
      {
        state = "state"; //$NON-NLS-1$
      }
      else
      {
        try
        {
          URI uri = new URI(state);
          state = new File(new File(uri), ".metadata").getAbsolutePath(); //$NON-NLS-1$
        }
        catch (Exception ex)
        {
          OM.LOG.error("Property " + SYSTEM_PROPERTY_OSGI_STATE + " is not a proper file URI: " + state); //$NON-NLS-1$ //$NON-NLS-2$
          state = "state"; //$NON-NLS-1$
        }
      }
    }

    File stateFolder = new File(state);
    if (!stateFolder.exists())
    {
      if (!stateFolder.mkdirs())
      {
        throw new IORuntimeException("State folder " + stateFolder.getAbsolutePath() + " could not be created"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    if (!stateFolder.isDirectory())
    {
      throw new IORuntimeException("State folder " + stateFolder.getAbsolutePath() + " is not a directoy"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    return stateFolder;
  }

  @Override
  public File getConfigFolder()
  {
    String config = getProperty(SYSTEM_PROPERTY_NET4J_CONFIG, "config"); //$NON-NLS-1$
    File configFolder = new File(config);
    if (!configFolder.exists())
    {
      if (!configFolder.mkdirs())
      {
        OM.LOG.error("Config folder " + configFolder.getAbsolutePath() + " could not be created"); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
      }
    }

    if (!configFolder.isDirectory())
    {
      OM.LOG.error("Config folder " + configFolder.getAbsolutePath() + " is not a directoy"); //$NON-NLS-1$ //$NON-NLS-2$
      return null;
    }

    return configFolder;
  }

  @Override
  public File getConfigFile(String name)
  {
    File configFolder = getConfigFolder();
    if (configFolder == null)
    {
      return null;
    }

    return new File(configFolder, name);
  }

  @Override
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
      OM.LOG.error("Config file " + configFile.getAbsolutePath() + " could not be read"); //$NON-NLS-1$ //$NON-NLS-2$
      return null;
    }
    finally
    {
      IOUtil.closeSilent(fis);
    }
  }

  public void log(OMLogger logger, Level level, String msg, Throwable t)
  {
    if (!logFilters.isEmpty())
    {
      for (OMLogFilter logFilter : logFilters.get())
      {
        try
        {
          if (logFilter.filter(logger, level, msg, t))
          {
            if (TRACER().isEnabled())
            {
              TRACER().format("Filtered log event: logger={0}, level={1}, msg={2}\n{3}", logger, level, msg, t);
            }

            return;
          }
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

    if (!logHandlers.isEmpty())
    {
      for (OMLogHandler logHandler : logHandlers.get())
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
  }

  public void trace(OMTraceHandlerEvent event)
  {
    if (!traceHandlers.isEmpty())
    {
      for (OMTraceHandler traceHandler : traceHandlers.get())
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
  }

  protected Map<String, AbstractBundle> getBundles()
  {
    return bundles;
  }

  @Override
  public String getProperty(String key)
  {
    if (key == null)
    {
      return null;
    }

    return System.getProperty(key);
  }

  @Override
  public String getProperty(String key, String defaultValue)
  {
    if (key == null)
    {
      return null;
    }

    return System.getProperty(key, defaultValue);
  }

  @Override
  public int getProperty(String key, int defaultValue)
  {
    String property = getProperty(key);
    if (property == null)
    {
      return defaultValue;
    }

    return Integer.parseInt(property);
  }

  @Override
  public long getProperty(String key, long defaultValue)
  {
    String property = getProperty(key);
    if (property == null)
    {
      return defaultValue;
    }

    return Long.parseLong(property);
  }

  @Override
  public <T extends Enum<T>> T getProperty(String key, Class<T> enumType)
  {
    String property = getProperty(key);
    if (property != null)
    {
      for (T enumValue : enumType.getEnumConstants())
      {
        if (enumValue.name().equalsIgnoreCase(property))
        {
          return enumValue;
        }
      }
    }

    return null;
  }

  @Override
  public <T extends Enum<T>> T getProperty(String key, T defaultValue)
  {
    T result = getProperty(key, defaultValue.getDeclaringClass());
    if (result != null)
    {
      return result;
    }

    return defaultValue;
  }

  @Override
  public boolean isProperty(String key)
  {
    return isProperty(key, false);
  }

  @Override
  public boolean isProperty(String key, boolean defaultValue)
  {
    String property = getProperty(key);
    if (property == null)
    {
      return defaultValue;
    }

    return Boolean.parseBoolean(property);
  }

  protected abstract OMBundle createBundle(String bundleID, Class<?> accessor);

  protected abstract String getDebugOption(String bundleID, String option);

  protected abstract void setDebugOption(String bundleID, String option, String value, boolean ifAbsent);

  protected void setDebugOption(String bundleID, String option, String value)
  {
    setDebugOption(bundleID, option, value, false);
  }

  public abstract void scheduleJob(InternalOMJob job);

  public abstract void cancelJob(InternalOMJob job);

  public abstract void renameJob(InternalOMJob job, String name);

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
      __TRACER__ = new ContextTracer(OM.DEBUG_OM, AbstractPlatform.class);
    }

    return __TRACER__;
  }

  private static boolean internalExtensionRegistryAvailable() throws Throwable
  {
    return org.eclipse.core.runtime.Platform.getExtensionRegistry() != null;
  }
}
