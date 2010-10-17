/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.io.IOUtil;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
      throw new IllegalStateException("bundle == null"); //$NON-NLS-1$
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
      throw new IllegalStateException("bundle == null"); //$NON-NLS-1$
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
        OM.TRACER.format("Starting bundle {0}", context.getBundle().getSymbolicName()); //$NON-NLS-1$
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
        OM.TRACER.format("Stopping bundle {0}", context.getBundle().getSymbolicName()); //$NON-NLS-1$
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.1
   */
  public static abstract class ConfigHandler
  {
    public ConfigHandler()
    {
    }

    public final void start(File configFile) throws Exception
    {
      FileInputStream fis = null;

      try
      {
        fis = new FileInputStream(configFile);
        ObjectInputStream ois = new ObjectInputStream(fis);

        Object config = ois.readObject();
        IOUtil.close(ois);
        startWithConfig(config);
      }
      finally
      {
        IOUtil.close(fis);
      }
    }

    public final void stop(File configFile) throws Exception
    {
      FileOutputStream fos = null;

      try
      {
        Object config = stopWithConfig();

        fos = new FileOutputStream(configFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(config);
        IOUtil.close(oos);
      }
      finally
      {
        IOUtil.close(fos);
      }
    }

    protected abstract void startWithConfig(Object config) throws Exception;

    protected abstract Object stopWithConfig() throws Exception;
  }

  /**
   * @author Eike Stepper
   * @since 3.1
   */
  public static abstract class WithConfig extends OSGiActivator
  {
    private ConfigHandler handler = new ConfigHandler()
    {
      @Override
      protected void startWithConfig(Object config) throws Exception
      {
        doStartWithConfig(config);
      }

      @Override
      protected Object stopWithConfig() throws Exception
      {
        return doStopWithConfig();
      }
    };

    public WithConfig(OMBundle bundle)
    {
      super(bundle);
    }

    @Override
    protected final void doStart() throws Exception
    {
      File configFile = getOMBundle().getConfigFile();
      if (configFile.exists())
      {
        handler.start(configFile);
      }
    }

    @Override
    protected final void doStop() throws Exception
    {
      File configFile = getOMBundle().getConfigFile();
      handler.stop(configFile);
    }

    protected abstract void doStartWithConfig(Object config) throws Exception;

    protected abstract Object doStopWithConfig() throws Exception;
  }
}
