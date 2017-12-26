/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.apireports;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public class Activator extends Plugin
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.releng.apireports"; //$NON-NLS-1$

  private static Activator plugin;

  public Activator()
  {
  }

  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    super.stop(context);
  }

  public static Activator getDefault()
  {
    return plugin;
  }

  public static IStatus errorStatus(String message)
  {
    return new Status(IStatus.ERROR, PLUGIN_ID, message);
  }

  public static Status errorStatus(Throwable ex)
  {
    return new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex);
  }

  public static void log(Throwable ex)
  {
    log(errorStatus(ex));
  }

  public static void log(IStatus status)
  {
    getDefault().getLog().log(status);
  }
}
