/*
 * Copyright (c) 2007-2013, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.OSGiActivator.StateHandler;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

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

  /**
   * @since 3.1
   */
  public ImageDescriptor loadImageDescriptor(String path)
  {
    return imageDescriptorFromPlugin(omBundle.getBundleID(), path);
  }

  @Override
  @SuppressWarnings("deprecation")
  public IDialogSettings getDialogSettings()
  {
    return super.getDialogSettings();
  }

  /**
   * @since 3.4
   */
  public IDialogSettings getDialogSettings(Class<?> clazz)
  {
    return getDialogSettings(clazz.getName());
  }

  /**
   * @since 3.4
   */
  public IDialogSettings getDialogSettings(String section)
  {
    return DialogSettings.getOrCreateSection(getDialogSettings(), section);
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
      setBundleContext(context);
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
      ((AbstractBundle)omBundle).stop();
      setBundleContext(null);
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

  @SuppressWarnings("deprecation")
  private void setBundleContext(BundleContext context)
  {
    omBundle.setBundleContext(context);
  }

  /**
   * @author Eike Stepper
   * @since 3.1
   */
  public static abstract class WithState extends UIActivator
  {
    private StateHandler handler = new StateHandler(getOMBundle())
    {
      @Override
      protected void startWithState(Object state) throws Exception
      {
        doStartWithState(state);
      }

      @Override
      protected Object stopWithState() throws Exception
      {
        return doStopWithState();
      }
    };

    public WithState(OMBundle bundle)
    {
      super(bundle);
    }

    @Override
    protected final void doStart() throws Exception
    {
      handler.start();
    }

    @Override
    protected final void doStop() throws Exception
    {
      handler.stop();
    }

    protected abstract void doStartWithState(Object state) throws Exception;

    protected abstract Object doStopWithState() throws Exception;
  }
}
