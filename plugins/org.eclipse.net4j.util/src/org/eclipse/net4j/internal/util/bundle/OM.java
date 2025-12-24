/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.bundle;

import org.eclipse.net4j.internal.util.container.PluginContainer;
import org.eclipse.net4j.internal.util.om.OSGiBundle;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.EclipseLoggingBridge;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.net4j.util"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMTracer DEBUG_LIFECYCLE = DEBUG.tracer("lifecycle"); //$NON-NLS-1$

  public static final OMTracer DEBUG_LIFECYCLE_DUMP = DEBUG_LIFECYCLE.tracer("dump"); //$NON-NLS-1$

  public static final OMTracer DEBUG_CONCURRENCY = DEBUG.tracer("concurrency"); //$NON-NLS-1$

  public static final OMTracer DEBUG_REGISTRY = DEBUG.tracer("registry"); //$NON-NLS-1$

  public static final OMTracer DEBUG_OM = DEBUG.tracer("om"); //$NON-NLS-1$

  public static final OMTracer DEBUG_MONITOR = DEBUG_OM.tracer("monitor"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OM, OM.class);

  /**
   * @author Eike Stepper
   */
  public static final class Activator implements BundleActivator
  {
    @Override
    public void start(BundleContext context) throws Exception
    {
      AbstractPlatform.systemContext = context;
      setBundleContext(context);
      ((OSGiBundle)OM.BUNDLE).start();

      // TODO Make configurable
      PrintTraceHandler.CONSOLE.setPattern("{6} [{0}] {5}"); //$NON-NLS-1$
      AbstractPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
      AbstractPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

      try
      {
        AbstractPlatform.INSTANCE.addLogHandler(EclipseLoggingBridge.INSTANCE);
      }
      catch (Throwable ignore)
      {
      }

      OSGiActivator.traceStart(context);
      IPluginContainer container = IPluginContainer.INSTANCE;
      if (TRACER.isEnabled())
      {
        TRACER.format("Plugin container created: {0}", container); //$NON-NLS-1$
      }
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
      OSGiActivator.traceStop(context);
      ((OSGiBundle)OM.BUNDLE).stop();
      PluginContainer.dispose();
      setBundleContext(null);
      AbstractPlatform.systemContext = null;
    }

    @SuppressWarnings("deprecation")
    private void setBundleContext(BundleContext context)
    {
      OM.BUNDLE.setBundleContext(context);
    }
  }
}
