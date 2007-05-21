/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMLogger;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OMTracer;
import org.eclipse.net4j.util.om.log.EclipseLoggingBridge;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public final class OM
{
  public static final String BUNDLE_ID = "org.eclipse.net4j.util"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMTracer DEBUG_LIFECYCLE = DEBUG.tracer("lifecycle"); //$NON-NLS-1$

  public static final OMTracer DEBUG_LIFECYCLE_DUMP = DEBUG_LIFECYCLE.tracer("dump"); //$NON-NLS-1$

  public static final OMTracer DEBUG_CONCURRENCY = DEBUG.tracer("concurrency"); //$NON-NLS-1$

  public static final OMTracer DEBUG_REGISTRY = DEBUG.tracer("registry"); //$NON-NLS-1$

  public static final OMTracer DEBUG_OM = DEBUG.tracer("om"); //$NON-NLS-1$

  public static final OMTracer PERF = BUNDLE.tracer("perf"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OM, OM.class);

  private OM()
  {
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator implements BundleActivator
  {
    public void start(BundleContext context) throws Exception
    {
      AbstractOMPlatform.systemContext = context;
      OM.BUNDLE.setBundleContext(context);

      PrintTraceHandler.CONSOLE.setPattern("{6} [{0}] {5}");
      AbstractOMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
      AbstractOMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

      try
      {
        AbstractOMPlatform.INSTANCE.addLogHandler(EclipseLoggingBridge.INSTANCE);
      }
      catch (Exception ignore)
      {
      }

      traceStart(context);
    }

    public void stop(BundleContext context) throws Exception
    {
      traceStop(context);
      OM.BUNDLE.setBundleContext(null);
      AbstractOMPlatform.systemContext = null;
    }

    public static void traceStart(BundleContext context)
    {
      try
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Starting bundle {0}", context.getBundle().getSymbolicName());
        }
      }
      catch (Exception ignore)
      {
      }
    }

    public static void traceStop(BundleContext context)
    {
      try
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Stopping bundle {0}", context.getBundle().getSymbolicName());
        }
      }
      catch (Exception ignore)
      {
      }
    }
  }
}
