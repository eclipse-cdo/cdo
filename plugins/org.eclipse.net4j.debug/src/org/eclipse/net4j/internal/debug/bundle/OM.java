/*
 * Copyright (c) 2007, 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.debug.bundle;

import org.eclipse.net4j.internal.debug.RemoteTraceManager;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.net4j.debug"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  static void start() throws Exception
  {
    if (isRemoteTracing())
    {
      RemoteTraceManager.INSTANCE.activate();
    }
  }

  static void stop() throws Exception
  {
    if (isRemoteTracing())
    {
      RemoteTraceManager.INSTANCE.deactivate();
    }
  }

  private static boolean isRemoteTracing()
  {
    // TODO Make configurable
    return true;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends UIActivator
  {
    public Activator()
    {
      super(BUNDLE);
    }
  }
}
