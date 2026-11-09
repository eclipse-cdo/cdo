/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.spi.server.InternalSession;

/**
 * Carries the initiating session separately from the store accessor context.
 *
 * @author Eike Stepper
 */
public final class ServerOperationContext
{
  private static final ThreadLocal<Context> THREAD_LOCAL = new ThreadLocal<>();

  private ServerOperationContext()
  {
  }

  public static InternalSession getSession()
  {
    Context context = THREAD_LOCAL.get();
    return context == null ? null : context.session;
  }

  public static void setSession(InternalSession session)
  {
    THREAD_LOCAL.set(new Context(session, THREAD_LOCAL.get()));
  }

  public static void unsetSession()
  {
    Context context = THREAD_LOCAL.get();
    if (context == null || context.previous == null)
    {
      THREAD_LOCAL.remove();
    }
    else
    {
      THREAD_LOCAL.set(context.previous);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Context
  {
    public final InternalSession session;

    public final Context previous;

    public Context(InternalSession session, Context previous)
    {
      this.session = session;
      this.previous = previous;
    }
  }
}
