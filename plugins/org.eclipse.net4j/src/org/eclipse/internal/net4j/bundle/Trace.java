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

import org.eclipse.net4j.util.operation.AbstractTracer;
import org.eclipse.net4j.util.operation.IDebugOptions;
import org.eclipse.net4j.util.operation.ITracer;

/**
 * @author Eike Stepper
 */
public final class Trace
{
  public static final ITracer DEBUG = new Tracer("debug");

  public static final ITracer CONNECTOR = DEBUG.child("connector");

  public static final ITracer CONNECTOR_STATE = CONNECTOR.child("state");

  public static final ITracer CONNECTOR_CHANNELS = CONNECTOR.child("channels");

  private static IDebugOptions debugOptions;

  private Trace()
  {
  }

  public static void setDebugOptions(IDebugOptions debugOptions)
  {
    Trace.debugOptions = debugOptions;
  }

  public static void unsetDebugOptions(IDebugOptions options)
  {
    if (Trace.debugOptions == debugOptions)
    {
      Trace.debugOptions = null;
    }
  }

  private static class Tracer extends AbstractTracer
  {
    public Tracer(String tracerName)
    {
      super(tracerName);
    }

    public boolean isEnabled()
    {
      return debugOptions.isDebugging() && debugOptions.getBooleanOption(getTracerName(), false);
    }

    public void setEnabled(boolean enabled)
    {
      debugOptions.setOption(getTracerName(), Boolean.toString(enabled));
    }

    public ITracer child(String tracerName)
    {
      return new Tracer(getTracerName() + "." + tracerName);
    }
  }
}
