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

import org.eclipse.net4j.util.om.OMTraceHandler;
import org.eclipse.net4j.util.om.OMTracer;

import java.io.Serializable;

/**
 * @author Eike Stepper
 */
public class OMTraceHandlerEventImpl implements OMTraceHandler.Event, Serializable
{
  private static final long serialVersionUID = 1L;

  protected long timeStamp;

  protected OMTracer tracer;

  protected Class context;

  protected Object instance;

  protected String message;

  protected Throwable throwable;

  public OMTraceHandlerEventImpl(OMTracer tracer, Class context, Object instance, String message,
      Throwable throwable)
  {
    if (tracer == null)
    {
      throw new IllegalArgumentException("tracer == null");
    }

    if (context == null)
    {
      throw new IllegalArgumentException("context == null");
    }

    timeStamp = System.currentTimeMillis();
    this.tracer = tracer;
    this.context = context;
    this.instance = instance;
    this.message = message;
    this.throwable = throwable;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public OMTracer getTracer()
  {
    return tracer;
  }

  public Class getContext()
  {
    return context;
  }

  public Object getInstance()
  {
    return instance;
  }

  public String getMessage()
  {
    return message;
  }

  public Throwable getThrowable()
  {
    return throwable;
  }
}
