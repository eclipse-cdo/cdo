/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.explorer.bundle;

import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutManagerImpl;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryManagerImpl;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import java.io.File;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.explorer"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  private static final String STATE_LOCATION = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.explorer.stateLocation");

  private static String stateLocation;

  private static CDORepositoryManagerImpl repositoryManager;

  private static CDOCheckoutManagerImpl checkoutManager;

  public static String getStateLocation()
  {
    return stateLocation;
  }

  public static void initializeManagers(File stateLocation)
  {
    disposeManagers();

    if (repositoryManager == null)
    {
      repositoryManager = new CDORepositoryManagerImpl(new File(stateLocation, "rp"));
      Exception exception = LifecycleUtil.activateSilent(repositoryManager);
      if (exception != null)
      {
        LOG.error(exception);
      }
    }

    if (checkoutManager == null)
    {
      checkoutManager = new CDOCheckoutManagerImpl(new File(stateLocation, "co"));
      Exception exception = LifecycleUtil.activateSilent(checkoutManager);
      if (exception != null)
      {
        LOG.error(exception);
      }
    }
  }

  public static void disposeManagers()
  {
    if (checkoutManager != null)
    {
      Exception exception = LifecycleUtil.deactivate(checkoutManager);
      if (exception != null)
      {
        LOG.error(exception);
      }

      checkoutManager = null;
    }

    if (repositoryManager != null)
    {
      Exception exception = LifecycleUtil.deactivate(repositoryManager);
      if (exception != null)
      {
        LOG.error(exception);
      }

      repositoryManager = null;
    }
  }

  public static CDORepositoryManagerImpl getRepositoryManager()
  {
    return repositoryManager;
  }

  public static CDOCheckoutManagerImpl getCheckoutManager()
  {
    return checkoutManager;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator
  {
    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      super.doStart();

      stateLocation = STATE_LOCATION != null ? STATE_LOCATION : BUNDLE.getStateLocation();
      initializeManagers(new File(stateLocation));
    }

    @Override
    protected void doStop() throws Exception
    {
      disposeManagers();
      super.doStop();
    }
  }
}
