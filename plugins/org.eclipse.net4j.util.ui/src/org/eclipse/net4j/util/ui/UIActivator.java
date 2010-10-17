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
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.OSGiActivator.ConfigHandler;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class UIActivator extends AbstractUIPlugin
{
  private OMBundle omBundle;

  /**
   * @since 2.0
   */
  protected BundleContext bundleContext;

  public UIActivator(OMBundle omBundle)
  {
    this.omBundle = omBundle;
  }

  public final OMBundle getOMBundle()
  {
    return omBundle;
  }

  @Override
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
      super.start(context);
      omBundle.setBundleContext(context);
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

  @Override
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
      omBundle.setBundleContext(null);
      super.stop(context);
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
   * @author Eike Stepper
   * @since 3.1
   */
  public static abstract class WithConfig extends UIActivator
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
