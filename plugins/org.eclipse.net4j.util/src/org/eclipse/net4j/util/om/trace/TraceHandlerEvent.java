/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.trace;

import java.io.Serializable;

/**
 * The default implementation of a {@link OMTraceHandlerEvent trace event}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public class TraceHandlerEvent implements OMTraceHandlerEvent, Serializable
{
  private static final long serialVersionUID = 1L;

  protected long timeStamp;

  protected OMTracer tracer;

  protected Class<?> context;

  protected String message;

  protected Throwable throwable;

  public TraceHandlerEvent(OMTracer tracer, Class<?> context, String message, Throwable throwable)
  {
    if (tracer == null)
    {
      throw new IllegalArgumentException("tracer == null"); //$NON-NLS-1$
    }

    if (context == null)
    {
      throw new IllegalArgumentException("context == null"); //$NON-NLS-1$
    }

    timeStamp = System.currentTimeMillis();
    this.tracer = tracer;
    this.context = context;
    this.message = message;
    this.throwable = throwable;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public OMTracer getTracer()
  {
    return tracer;
  }

  @Override
  public Class<?> getContext()
  {
    return context;
  }

  @Override
  public String getMessage()
  {
    return message;
  }

  @Override
  public Throwable getThrowable()
  {
    return throwable;
  }
}
