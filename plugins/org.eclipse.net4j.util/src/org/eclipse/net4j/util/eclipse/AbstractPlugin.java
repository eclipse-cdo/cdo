/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.eclipse;


import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.apache.log4j.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import java.io.IOException;

import java.net.URL;


/**
 * The main plugin class to be used in the desktop.
 */
public abstract class AbstractPlugin extends Plugin implements ClassLoaderFactory
{
  /**
   * Logger for this class
   */
  private final Logger logger = Logger.getLogger(getClass());

  private String pluginId;

  private ResourceBundle resourceBundle;

  private BundleContext bundleContext;

  /**
   * The constructor.
   */
  protected AbstractPlugin()
  {
  }

  public String getPluginId()
  {
    return pluginId;
  }

  public ClassLoader getClassLoader()
  {
    return getClass().getClassLoader();
  }

  public Logger getLogger()
  {
    return logger;
  }

  /**
   * This method is called upon plug-in activation
   */
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    bundleContext = context;
    pluginId = context.getBundle().getSymbolicName();

    try
    {
      if (resourceBundle == null)
      {
        try
        {
          resourceBundle = ResourceBundle.getBundle(pluginId);
        }
        catch (MissingResourceException x)
        {
          resourceBundle = null;
        }
      }

      if (logger.isDebugEnabled()) logger.debug("ENTER STARTUP");
      doStart();
    }
    catch (Throwable t)
    {
      logger.error("Startup error", t);
    }
    finally
    {
      if (logger.isDebugEnabled()) logger.debug("EXIT STARTUP");
    }
  }

  /**
   * This method is called upon plug-in deactivation
   */
  public void stop(BundleContext context) throws Exception
  {
    try
    {
      if (logger.isDebugEnabled()) logger.debug("ENTER SHUTDOWN");
      doStop();
    }
    catch (Throwable t)
    {
      logger.error("Shutdown error", t);
    }
    finally
    {
      if (logger.isDebugEnabled()) logger.debug("EXIT SHUTDOWN");
    }

    super.stop(context);
    resourceBundle = null;
  }

  /**
   * 
   */
  protected void doStart() throws Exception
  {
  }

  /**
   * 
   */
  protected void doStop() throws Exception
  {
  }

  /**
   * Returns the string from the plugin's resource bundle,
   * or 'key' if not found.
   */
  public String getResourceString(String key)
  {
    ResourceBundle bundle = getResourceBundle();

    try
    {
      return (bundle != null) ? bundle.getString(key) : key;
    }
    catch (MissingResourceException e)
    {
      return key;
    }
  }

  /**
   * Returns the plugin's resource bundle,
   */
  public ResourceBundle getResourceBundle()
  {
    return resourceBundle;
  }

  /**
   * @return Returns the bundleContext.
   */
  public BundleContext getBundleContext()
  {
    return bundleContext;
  }

  public String getBundleLocation() throws IOException
  {
    Bundle bundle = bundleContext.getBundle();
    return getBundleLocation(bundle);
  }

  /**
   * Progress monitor helpers
   */
  public static void checkCanceled(IProgressMonitor monitor)
  {
    if (monitor.isCanceled()) cancelOperation();
  }

  public static void cancelOperation()
  {
    throw new OperationCanceledException();
  }

  public static IProgressMonitor monitorFor(IProgressMonitor monitor)
  {
    if (monitor == null) return new NullProgressMonitor();
    return monitor;
  }

  public static IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks)
  {
    if (monitor == null) return new NullProgressMonitor();
    if (monitor instanceof NullProgressMonitor) return monitor;
    return new SubProgressMonitor(monitor, ticks);
  }

  public static IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks, int style)
  {
    if (monitor == null) return new NullProgressMonitor();
    if (monitor instanceof NullProgressMonitor) return monitor;
    return new SubProgressMonitor(monitor, ticks, style);
  }

  public static String getBundleLocation(Bundle bundle) throws IOException
  {
    URL url = bundle.getEntry("/");
    return FileLocator.toFileURL(url).getFile();
  }

  public void debug(String message)
  {
    logger.debug(message);
    getLog().log(new Status(IStatus.INFO, pluginId, IStatus.OK, message, null));
  }

  public void warn(String message)
  {
    warn(message, null);
  }

  public void error(String message)
  {
    error(message, null);
  }

  public void warn(Throwable t)
  {
    warn(t.getMessage(), t);
  }

  public void error(Throwable t)
  {
    error(t.getMessage(), t);
  }

  public void warn(String message, Throwable t)
  {
    logger.warn(message, t);
    getLog().log(new Status(IStatus.WARNING, pluginId, IStatus.OK, message, t));
  }

  public void error(String message, Throwable t)
  {
    logger.error(message, t);
    getLog().log(new Status(IStatus.ERROR, pluginId, IStatus.OK, message, t));
  }
}
