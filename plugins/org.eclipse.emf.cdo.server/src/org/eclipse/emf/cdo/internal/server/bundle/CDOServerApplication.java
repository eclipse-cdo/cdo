/*
 * Copyright (c) 2007-2012, 2018-2022, 2025-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OSGiApplication;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Adapts the shared CDO server lifecycle to the Equinox application contract.
 *
 * @author Eike Stepper
 */
public class CDOServerApplication extends AbstractCDOServerApplication implements IApplication
{
  /**
   * The registered Equinox application identifier.
   */
  public static final String ID = OM.BUNDLE_ID + ".app"; //$NON-NLS-1$

  /**
   * Compatibility alias for {@link AbstractCDOServerApplication#PROP_TITLE}.
   */
  public static final String PROP_TITLE = AbstractCDOServerApplication.PROP_TITLE;

  /**
   * Compatibility alias for {@link AbstractCDOServerApplication#PROP_CONFIG_FILE_NAME}.
   */
  public static final String PROP_CONFIG_FILE_NAME = AbstractCDOServerApplication.PROP_CONFIG_FILE_NAME;

  /**
   * Compatibility alias for {@link AbstractCDOServerApplication#PROP_CONFIGURATOR_TYPE}.
   */
  public static final String PROP_CONFIGURATOR_TYPE = AbstractCDOServerApplication.PROP_CONFIGURATOR_TYPE;

  /**
   * Compatibility alias for {@link AbstractCDOServerApplication#PROP_CONFIGURATOR_DESCRIPTION}.
   */
  public static final String PROP_CONFIGURATOR_DESCRIPTION = AbstractCDOServerApplication.PROP_CONFIGURATOR_DESCRIPTION;

  /**
   * Compatibility alias for {@link AbstractCDOServerApplication#PROP_BROWSER_PORT}.
   */
  public static final String PROP_BROWSER_PORT = AbstractCDOServerApplication.PROP_BROWSER_PORT;

  /**
   * Compatibility alias for {@link AbstractCDOServerApplication#PROP_BROWSER_TYPE}.
   */
  public static final String PROP_BROWSER_TYPE = AbstractCDOServerApplication.PROP_BROWSER_TYPE;

  private final CountDownLatch stopLatch = new CountDownLatch(1);

  private final AtomicBoolean stopRequested = new AtomicBoolean();

  private volatile IApplicationContext context;

  private volatile boolean running;

  /**
   * Creates the Equinox server application adapter.
   */
  public CDOServerApplication()
  {
  }

  /**
   * Returns the Equinox application identifier registered for the CDO server.
   *
   * @return the registered application identifier
   */
  public String getApplicationID()
  {
    return ID;
  }

  /**
   * Returns the application context supplied by Equinox while this application is running.
   *
   * @return the current application context, or {@code null} when no start is in progress
   */
  public IApplicationContext getApplicationContext()
  {
    return context;
  }

  /**
   * Indicates whether the application has completed startup and is waiting for Equinox to stop it.
   *
   * @return {@code true} while the application start call is waiting for shutdown
   */
  public boolean isRunning()
  {
    return running;
  }

  @Override
  public Object start(IApplicationContext context) throws Exception
  {
    this.context = context;
    OSGiApplication.traceStart(ID);

    try
    {
      synchronized (this)
      {
        activate();

        if (stopRequested.get())
        {
          deactivateAndLog();
        }
        else if (context != null)
        {
          context.applicationRunning();
        }

        running = true;
      }

      stopLatch.await();
      return EXIT_OK;
    }
    catch (Error error)
    {
      deactivateAfterStartFailure(error);
      OM.LOG.error(error);
      throw error;
    }
    catch (Exception ex)
    {
      deactivateAfterStartFailure(ex);
      OM.LOG.error(ex);
      throw ex;
    }
    finally
    {
      running = false;
      this.context = null;
    }
  }

  @Override
  public void stop()
  {
    OSGiApplication.traceStop(ID);
    stopRequested.set(true);

    try
    {
      synchronized (this)
      {
        deactivateAndLog();
      }
    }
    catch (Error error)
    {
      OM.LOG.error(error);
      throw error;
    }
    finally
    {
      context = null;
      stopLatch.countDown();
    }
  }

  private void deactivateAndLog()
  {
    Exception exception = deactivate();
    if (exception != null)
    {
      OM.LOG.error(exception);
    }
  }

  private void deactivateAfterStartFailure(Throwable failure)
  {
    if (isActive())
    {
      try
      {
        Exception exception = deactivate();
        if (exception != null)
        {
          failure.addSuppressed(exception);
        }
      }
      catch (Error error)
      {
        failure.addSuppressed(error);
      }
    }
  }

  /**
   * Returns the global managed container used by server components and legacy callers.
   *
   * @return the shared global managed container
   */
  public static IManagedContainer getContainer()
  {
    return IManagedContainer.INSTANCE;
  }
}
