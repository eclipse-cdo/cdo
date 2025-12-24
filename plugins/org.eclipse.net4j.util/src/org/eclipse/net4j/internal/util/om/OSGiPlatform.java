/*
 * Copyright (c) 2007, 2008, 2010-2012, 2019-2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.om.OMBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.service.debug.DebugOptions;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class OSGiPlatform extends AbstractPlatform
{
  private final Map<InternalOMJob, Job> eclipseJobs = Collections.synchronizedMap(new HashMap<>());

  BundleContext systemContext;

  public OSGiPlatform(Object systemContext)
  {
    this.systemContext = (BundleContext)systemContext;

    try
    {
      setDebugging(Platform.inDebugMode());
    }
    catch (Throwable ignore)
    {
    }
  }

  @Override
  public boolean isOSGiRunning()
  {
    return true;
  }

  @Override
  public String getProperty(String key)
  {
    if (key == null)
    {
      return null;
    }

    return systemContext != null ? systemContext.getProperty(key) : null;
  }

  @Override
  public String getProperty(String key, String defaultValue)
  {
    String property = getProperty(key);
    return property != null ? property : defaultValue;
  }

  @Override
  public String[] getCommandLineArgs()
  {
    return Platform.getCommandLineArgs();
  }

  public void setCommandLineArgs(String[] args)
  {
    throw new UnsupportedOperationException("Set command line arguements inside the OSGi enviorment is not needed.");
  }

  @Override
  public void scheduleJob(InternalOMJob job)
  {
    Job eclipseJob = new Job(job.getName())
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          return job.run(monitor);
        }
        finally
        {
          eclipseJobs.remove(job);
        }
      }
    };

    eclipseJobs.put(job, eclipseJob);

    eclipseJob.setSystem(job.isSystem());
    eclipseJob.schedule();
  }

  @Override
  public void cancelJob(InternalOMJob job)
  {
    Job eclipseJob = eclipseJobs.get(job);
    if (eclipseJob != null)
    {
      eclipseJob.cancel();
    }
  }

  @Override
  public void renameJob(InternalOMJob job, String name)
  {
    Job eclipseJob = eclipseJobs.get(job);
    if (eclipseJob != null)
    {
      eclipseJob.setName(name);
    }
  }

  @Override
  protected OMBundle createBundle(String bundleID, Class<?> accessor)
  {
    return new OSGiBundle(this, bundleID, accessor);
  }

  @Override
  protected String getDebugOption(String bundleID, String option)
  {
    try
    {
      DebugOptions debugOptions = getDebugOptions();
      return debugOptions.getOption(bundleID + "/" + option); //$NON-NLS-1$
    }
    catch (RuntimeException ex)
    {
      return null;
    }
  }

  @Override
  protected void setDebugOption(String bundleID, String option, String value, boolean ifAbsent)
  {
    try
    {
      DebugOptions debugOptions = getDebugOptions();
      option = bundleID + "/" + option; //$NON-NLS-1$

      if (ifAbsent && debugOptions.getOption(option) != null)
      {
        return;
      }

      debugOptions.setOption(option, value);
    }
    catch (RuntimeException ex)
    {
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected DebugOptions getDebugOptions() throws NoClassDefFoundError, NullPointerException
  {
    ServiceReference ref = systemContext.getServiceReference(DebugOptions.class.getName());
    return (DebugOptions)systemContext.getService(ref);
  }
}
