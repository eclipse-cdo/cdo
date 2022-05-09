/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client.bundle;

import org.eclipse.emf.cdo.lm.internal.client.AssemblyManager;
import org.eclipse.emf.cdo.lm.internal.client.SystemManager;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.log.OMLogger.Level;
import org.eclipse.net4j.util.om.trace.OMTracer;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.lm.client"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static void initializeManagers()
  {
    LifecycleUtil.activateSilent(SystemManager.INSTANCE);
    LifecycleUtil.activateSilent(AssemblyManager.INSTANCE);
  }

  public static void disposeManagers()
  {
    LifecycleUtil.deactivate(AssemblyManager.INSTANCE, Level.ERROR);
    LifecycleUtil.deactivate(SystemManager.INSTANCE, Level.ERROR);
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
      initializeManagers();
    }

    @Override
    protected void doStop() throws Exception
    {
      disposeManagers();
      super.doStop();
    }
  }
}
