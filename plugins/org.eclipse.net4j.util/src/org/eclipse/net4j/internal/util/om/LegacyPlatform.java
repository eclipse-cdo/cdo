/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.om.LegacyUtil;
import org.eclipse.net4j.util.om.OMBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class LegacyPlatform extends AbstractPlatform
{
  private final Map<String, String> debugOptions = new ConcurrentHashMap<>(0);

  private final Map<InternalOMJob, IProgressMonitor> jobMonitors = Collections.synchronizedMap(new HashMap<>());

  public LegacyPlatform()
  {
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
  protected void setDebugOption(String bundleID, String option, String value)
  {
    debugOptions.put(bundleID + "/" + option, value); //$NON-NLS-1$
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
