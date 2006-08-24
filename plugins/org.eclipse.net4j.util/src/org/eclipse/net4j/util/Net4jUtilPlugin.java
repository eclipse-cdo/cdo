/***************************************************************************
 * Copyright (c) 2004-2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;


import org.eclipse.net4j.util.eclipse.AbstractPlugin;

import org.eclipse.core.runtime.FileLocator;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.net.URL;


public class Net4jUtilPlugin extends AbstractPlugin
{
  //The shared instance.
  private static Net4jUtilPlugin plugin;

  private boolean inDebugMode;

  /**
   * The constructor.
   */
  public Net4jUtilPlugin()
  {
    if (plugin == null) plugin = this;
  }

  public void doStart() throws Exception
  {
    initializeLogger();
    //    determineDebugMode();
  }

  protected void doStop() throws Exception
  {
    plugin = null;
  }

  /**
   * Returns the shared instance.
   */
  public static Net4jUtilPlugin getDefault()
  {
    return plugin;
  }

  private void initializeLogger()
  {
    try
    {
      URL pluginURL = getBundle().getEntry("/config/log4j.xml");
      URL absoluteURL = FileLocator.toFileURL(pluginURL);

      DOMConfigurator.configure(absoluteURL);
    }
    catch (Exception ex)
    {
      IOHelper.log("Warning: Initialization of Log4j failed", ex);
    }

    Logger logger = Logger.getLogger(Net4jUtilPlugin.class);
    if (logger.isDebugEnabled())
    {
      logger.debug("Log4j initialized");
    }
  }

  //  private void determineDebugMode()
  //  {
  //    ServiceTracker environmentTracker = new ServiceTracker(getBundleContext(),
  //        EnvironmentInfo.class.getName(), null);
  //    environmentTracker.open();
  //    EnvironmentInfo environmentInfo = (EnvironmentInfo) environmentTracker.getService();
  //    environmentTracker.close();
  //
  //    if (environmentInfo == null) throw new IllegalStateException("environmentInfo == null");
  //    inDebugMode = environmentInfo.inDebugMode();
  //    System.out.println("DEBUG MODE: " + inDebugMode);
  //  }
  //
  //  public static boolean inDebugMode()
  //  {
  //    return getDefault().inDebugMode;
  //  }
}
