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
package org.eclipse.emf.internal.cdo.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

/**
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMTracer DEBUG_UTIL = DEBUG.tracer("util"); //$NON-NLS-1$

  public static final OMTracer DEBUG_MODEL = DEBUG.tracer("model"); //$NON-NLS-1$

  public static final OMTracer DEBUG_PROTOCOL = DEBUG.tracer("protocol"); //$NON-NLS-1$

  public static final OMTracer DEBUG_REPOSITORY = DEBUG.tracer("repository"); //$NON-NLS-1$

  public static final OMTracer DEBUG_SESSION = DEBUG.tracer("session"); //$NON-NLS-1$

  public static final OMTracer DEBUG_VIEW = DEBUG.tracer("view"); //$NON-NLS-1$

  public static final OMTracer DEBUG_TRANSCTION = DEBUG.tracer("transaction"); //$NON-NLS-1$

  public static final OMTracer DEBUG_OBJECT = DEBUG.tracer("object"); //$NON-NLS-1$

  public static final OMTracer DEBUG_RESOURCE = DEBUG.tracer("resource"); //$NON-NLS-1$

  public static final OMTracer DEBUG_REVISION = DEBUG.tracer("revision"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();
}
