/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.util.om;

import org.eclipse.net4j.util.om.OMBundle;

import org.eclipse.internal.net4j.bundle.Net4j;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public abstract class OSGiActivator implements BundleActivator
{
  public OSGiActivator()
  {
  }

  public final void start(BundleContext context) throws Exception
  {
    try
    {
      getOMBundle().setBundleContext(context);
      Net4j.Activator.traceStart(context);
      start();
    }
    catch (Error error)
    {
      getOMBundle().logger().error(error);
      throw error;
    }
    catch (Exception ex)
    {
      getOMBundle().logger().error(ex);
      throw ex;
    }
  }

  public final void stop(BundleContext context) throws Exception
  {
    try
    {
      Net4j.Activator.traceStop(context);
      stop();
      getOMBundle().setBundleContext(null);
    }
    catch (Error error)
    {
      getOMBundle().logger().error(error);
      throw error;
    }
    catch (Exception ex)
    {
      getOMBundle().logger().error(ex);
      throw ex;
    }
  }

  protected void start() throws Exception
  {
  }

  protected void stop() throws Exception
  {
  }

  protected abstract OMBundle getOMBundle();
}
