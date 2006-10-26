/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMLogger;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OMTracer;

/**
 * @author Eike Stepper
 */
public final class Net4j
{
  public static final OMBundle BUNDLE = OMPlatform.INSTANCE
      .bundle("org.eclipse.net4j", Net4j.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug");

  public static final OMTracer DEBUG_LIFECYCLE = DEBUG.tracer("lifecycle");

  public static final OMTracer DEBUG_LIFECYCLE_DUMP = DEBUG_LIFECYCLE.tracer("dump");

  public static final OMTracer DEBUG_BUFFER = DEBUG.tracer("buffer");

  public static final OMTracer DEBUG_CHANNEL = DEBUG.tracer("channel");

  public static final OMTracer DEBUG_SELECTOR = DEBUG.tracer("selector");

  public static final OMTracer DEBUG_ACCEPTOR = DEBUG.tracer("acceptor");

  public static final OMTracer DEBUG_CONNECTOR = DEBUG.tracer("connector");

  public static final OMTracer PERF = BUNDLE.tracer("perf");

  public static final OMLogger LOG = BUNDLE.logger();

  private Net4j()
  {
  }
}
