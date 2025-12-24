/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.spi.server.InternalSession;

/**
 * @author Eike Stepper
 */
public final class ServerSession
{
  private static final ThreadLocal<InternalSession> THREAD_LOCAL = new ThreadLocal<>();

  private ServerSession()
  {
  }

  public static InternalSession get()
  {
    return THREAD_LOCAL.get();
  }

  static void set(InternalSession serverSession)
  {
    THREAD_LOCAL.set(serverSession);
  }

  static void unset()
  {
    THREAD_LOCAL.remove();
  }
}
