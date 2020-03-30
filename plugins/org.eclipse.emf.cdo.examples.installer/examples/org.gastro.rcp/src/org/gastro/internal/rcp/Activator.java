/*
 * Copyright (c) 2009-2012, 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.internal.rcp;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Eike Stepper
 */
public class Activator extends AbstractUIPlugin
{
  // The plug-in ID
  public static final String PLUGIN_ID = "org.gastro.internal.rcp";

  // The shared instance
  private static Activator plugin;

  /**
   * The constructor
   */
  public Activator()
  {
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;

    String configName = System.getProperty("gastro.config", "gastro.properties");
    InputStream fis = new FileInputStream(configName);

    try
    {
      Properties properties = new Properties();
      properties.load(fis);
      Configuration.INSTANCE.setProperties(properties);
    }
    finally
    {
      fis.close();
    }

    Model.INSTANCE.activate();
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(BundleContext context) throws Exception
  {
    Model.INSTANCE.deactivate();
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static Activator getDefault()
  {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in relative path
   *
   * @param path
   *          the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path)
  {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }
}
