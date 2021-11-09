/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.LegacyUtil;
import org.eclipse.net4j.util.om.OMBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class LegacyPlatform extends AbstractPlatform
{
  public static final String OPTIONS = ".options";

  private final Map<String, String> debugOptions = new ConcurrentHashMap<>(0);

  private final Map<InternalOMJob, IProgressMonitor> jobMonitors = Collections.synchronizedMap(new HashMap<>());

  public LegacyPlatform()
  {
    String debugOptionsPath = System.getProperty("debug.options");
    if (debugOptionsPath == null)
    {
      debugOptionsPath = System.getProperty("osgi.debug");
    }

    if (debugOptionsPath != null)
    {
      loadDebugOptions(debugOptionsPath);
    }
  }

  private void loadDebugOptions(String debugOptionsPath)
  {
    if (debugOptionsPath.length() == 0)
    {
      debugOptionsPath = new File(System.getProperty("user.dir"), OPTIONS).toString();
    }
    else
    {
      File debugOptionsFile = new File(debugOptionsPath);
      if (debugOptionsFile.isDirectory())
      {
        debugOptionsPath = new File(debugOptionsFile, OPTIONS).toString();
      }
    }

    InputStream inputStream = null;
    Properties properties = new Properties();

    try
    {
      inputStream = new BufferedInputStream(new FileInputStream(debugOptionsPath));
      properties.load(inputStream);

      for (Map.Entry<Object, Object> entry : properties.entrySet())
      {
        try
        {
          String key = (String)entry.getKey();
          String value = (String)entry.getValue();
          debugOptions.put(key, value.trim());
        }
        catch (RuntimeException ignore)
        {
        }
      }
    }
    catch (IOException ignore)
    {
    }
    finally
    {
      closeSilent(inputStream);
    }
  }

  /**
   * This method is indirectly called from the constructor of this class.
   * The similar method in {@link IOUtil} must not be used because
   * {@link IOUtil} assumes a fully initialized OMPlatform.INSTANCE.
   */
  private static void closeSilent(Closeable closeable)
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }
    }
    catch (Exception ex)
    {
      // Don't use OM.LOG here!
      ex.printStackTrace();
    }
  }

  @Override
  public boolean isOSGiRunning()
  {
    return false;
  }

  @Override
  protected OMBundle createBundle(String bundleID, Class<?> accessor)
  {
    return new LegacyBundle(this, bundleID, accessor);
  }

  @Override
  protected String getDebugOption(String bundleID, String option)
  {
    return debugOptions.get(bundleID + "/" + option); //$NON-NLS-1$
  }

  @Override
  protected void setDebugOption(String bundleID, String option, String value, boolean ifAbsent)
  {
    option = bundleID + "/" + option; //$NON-NLS-1$

    if (ifAbsent)
    {
      debugOptions.putIfAbsent(option, value);
    }
    else
    {
      debugOptions.put(option, value);
    }
  }

  @Override
  public String[] getCommandLineArgs()
  {
    return LegacyUtil.getCommandLineArgs();
  }

  @Override
  public void scheduleJob(InternalOMJob job)
  {
    IProgressMonitor monitor = new NullProgressMonitor();
    jobMonitors.put(job, monitor);

    ExecutorService threadPool = ConcurrencyUtil.getExecutorService(IPluginContainer.INSTANCE);
    threadPool.submit(() -> {
      try
      {
        job.run(monitor);
      }
      finally
      {
        jobMonitors.remove(job);
      }
    });
  }

  @Override
  public void cancelJob(InternalOMJob job)
  {
    IProgressMonitor monitor = jobMonitors.get(job);
    if (monitor != null)
    {
      monitor.setCanceled(true);
    }
  }

  @Override
  public void renameJob(InternalOMJob job, String name)
  {
    // Do nothing.
  }

  public static boolean clearDebugOptions()
  {
    if (INSTANCE instanceof LegacyPlatform)
    {
      LegacyPlatform platform = (LegacyPlatform)INSTANCE;
      platform.debugOptions.clear();
      return true;
    }

    return false;
  }
}
