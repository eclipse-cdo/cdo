/*
 * Copyright (c) 2008-2012, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.bundle;

import org.eclipse.emf.cdo.common.util.URIHandlerRegistry;
import org.eclipse.emf.cdo.internal.common.util.URIHandlerRegistryImpl;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.eclipse.emf.ecore.resource.URIHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.common"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMTracer DEBUG_PROTOCOL = DEBUG.tracer("protocol"); //$NON-NLS-1$

  public static final OMTracer DEBUG_ID = DEBUG.tracer("id"); //$NON-NLS-1$

  public static final OMTracer DEBUG_REVISION = DEBUG.tracer("revision"); //$NON-NLS-1$

  public static final OMTracer PERF = BUNDLE.tracer("perf"); //$NON-NLS-1$

  public static final OMTracer PERF_REVISION = PERF.tracer("revision"); //$NON-NLS-1$

  public static final OMTracer PERF_REVISION_READING = PERF_REVISION.tracer("reading"); //$NON-NLS-1$

  public static final OMTracer PERF_REVISION_WRITING = PERF_REVISION.tracer("writing"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator
  {
    private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, Activator.class);

    private static final boolean disableURIHandlerRegistry = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.common.disableURIHandlerRegistry");

    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      super.doStart();

      if (!disableURIHandlerRegistry)
      {
        try
        {
          List<URIHandler> defaultHandlers = new ArrayList<>(URIHandler.DEFAULT_HANDLERS);
          defaultHandlers.add(4, URIHandlerRegistryImpl.INSTANCE); // Add our registry before EMF's catch-all handler.

          Field field = ReflectUtil.getField(URIHandler.class, "DEFAULT_HANDLERS");
          ReflectUtil.setValue(field, null, Collections.unmodifiableList(defaultHandlers), true);
        }
        catch (Throwable t)
        {
          try
          {
            if (TRACER.isEnabled())
            {
              TRACER.format(URIHandlerRegistry.class + ".INSTANCE could not be added to " + URIHandler.class + ".DEFAULT_HANDLERS", t);
            }
          }
          catch (Throwable ignored)
          {
            //$FALL-THROUGH$
          }
        }
      }
    }
  }
}
