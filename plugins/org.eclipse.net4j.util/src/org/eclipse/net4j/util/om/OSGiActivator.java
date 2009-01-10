/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.internal.util.bundle.OM;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public abstract class OSGiActivator implements BundleActivator
{
  private OMBundle omBundle;

  /**
   * @since 2.0
   */
  protected BundleContext bundleContext;

  public OSGiActivator(OMBundle omBundle)
  {
    this.omBundle = omBundle;
  }

  public final OMBundle getOMBundle()
  {
    return omBundle;
  }

  public final void start(BundleContext context) throws Exception
  {
    bundleContext = context;
    OSGiActivator.traceStart(context);
    if (omBundle == null)
    {
      throw new IllegalStateException("bundle == null");
    }

    try
    {
      omBundle.setBundleContext(context);
      ((AbstractBundle)omBundle).start();
      doStart();
    }
    catch (Error error)
    {
      omBundle.logger().error(error);
      throw error;
    }
    catch (Exception ex)
    {
      omBundle.logger().error(ex);
      throw ex;
    }
  }

  public final void stop(BundleContext context) throws Exception
  {
    OSGiActivator.traceStop(context);
    if (omBundle == null)
    {
      throw new IllegalStateException("bundle == null");
    }

    try
    {
      doStop();
      ((AbstractBundle)omBundle).stop();
      omBundle.setBundleContext(null);
    }
    catch (Error error)
    {
      omBundle.logger().error(error);
      throw error;
    }
    catch (Exception ex)
    {
      omBundle.logger().error(ex);
      throw ex;
    }
  }

  @Override
  public final boolean equals(Object obj)
  {
    return super.equals(obj);
  }

  @Override
  public final int hashCode()
  {
    return super.hashCode();
  }

  @Override
  public final String toString()
  {
    return super.toString();
  }

  @Override
  protected final Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  @Override
  protected final void finalize() throws Throwable
  {
    super.finalize();
  }

  /**
   * @since 2.0
   */
  protected void doStart() throws Exception
  {
  }

  /**
   * @since 2.0
   */
  protected void doStop() throws Exception
  {
  }

  /**
   * @since 2.0
   */
  public static void traceStart(BundleContext context)
  {
    try
    {
      if (OM.TRACER.isEnabled())
      {
        OM.TRACER.format("Starting bundle {0}", context.getBundle().getSymbolicName());
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  /**
   * @since 2.0
   */
  public static void traceStop(BundleContext context)
  {
    try
    {
      if (OM.TRACER.isEnabled())
      {
        OM.TRACER.format("Stopping bundle {0}", context.getBundle().getSymbolicName());
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }
}
