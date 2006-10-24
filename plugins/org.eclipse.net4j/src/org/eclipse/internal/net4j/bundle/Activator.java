/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.bundle;

import org.eclipse.internal.net4j.util.operation.BundleDebugOptions;
import org.eclipse.internal.net4j.util.operation.BundleLogger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public class Activator implements BundleActivator
{
  public static final String BUNDLE_ID = "org.eclipse.net4j";

  private BundleLogger logger;

  private BundleDebugOptions debugOptions;

  public void start(BundleContext context) throws Exception
  {
    try
    {
      Log.setLogger(logger = new BundleLogger(context));
      Trace.setDebugOptions(debugOptions = new BundleDebugOptions(context));
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
    }
  }

  public void stop(BundleContext context) throws Exception
  {
    try
    {
      Trace.unsetDebugOptions(debugOptions);
      debugOptions.dispose();
    }
    finally
    {
      debugOptions = null;
    }

    try
    {
      Log.unsetLogger(logger);
      logger.dispose();
    }
    finally
    {
      logger = null;
    }
  }
}
