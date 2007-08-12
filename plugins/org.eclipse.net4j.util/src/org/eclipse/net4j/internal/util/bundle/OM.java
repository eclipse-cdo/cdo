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

import org.eclipse.net4j.internal.util.container.PluginTransportContainer;
import org.eclipse.net4j.internal.util.om.OSGiBundle;
import org.eclipse.net4j.internal.util.om.log.EclipseLoggingBridge;
import org.eclipse.net4j.internal.util.om.log.PrintLogHandler;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.internal.util.om.trace.PrintTraceHandler;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
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

  public static final OMLogger LOG = BUNDLE.logger();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OM, OM.class);

  static void stop() throws Exception
  {
    PluginTransportContainer.dispose();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator implements BundleActivator
  {
    public void start(BundleContext context) throws Exception
    {
      AbstractPlatform.systemContext = context;
      OM.BUNDLE.setBundleContext(context);
      ((OSGiBundle)OM.BUNDLE).start();

      PrintTraceHandler.CONSOLE.setPattern("{6} [{0}] {5}");
      AbstractPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
      AbstractPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

      try
      {
        AbstractPlatform.INSTANCE.addLogHandler(EclipseLoggingBridge.INSTANCE);
      }
      catch (Exception ignore)
      {
      }

      traceStart(context);
    }

    public void stop(BundleContext context) throws Exception
    {
      traceStop(context);
      ((OSGiBundle)OM.BUNDLE).stop();
      OM.BUNDLE.setBundleContext(null);
      AbstractPlatform.systemContext = null;
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
