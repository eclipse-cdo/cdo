/*
 * Copyright (c) 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.internal.server.ServerOperationContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;

/**
 * @author Eike Stepper
 */
public final class EmbeddedSession
{
  private static final ThreadLocal<Context> THREAD_LOCAL = new ThreadLocal<>();

  private EmbeddedSession()
  {
  }

  public static InternalSession get()
  {
    Context context = THREAD_LOCAL.get();
    return context == null ? null : context.serverSession;
  }

  public static void set(InternalSession serverSession)
  {
    THREAD_LOCAL.set(new Context(serverSession, THREAD_LOCAL.get()));
    ServerOperationContext.setSession(serverSession);
  }

  public static void unset()
  {
    ServerOperationContext.unsetSession();

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
   * Represents one nested embedded server-session context.
   *
   * @author Eike Stepper
   */
  private static final class Context
  {
    public final InternalSession serverSession;

    public final Context previous;

    public Context(InternalSession serverSession, Context previous)
    {
      this.serverSession = serverSession;
      this.previous = previous;
    }
  }
}
