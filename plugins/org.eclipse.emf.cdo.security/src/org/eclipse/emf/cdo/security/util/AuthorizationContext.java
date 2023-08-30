/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.util;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public final class AuthorizationContext
{
  private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

  public static Map<String, Object> get()
  {
    return THREAD_LOCAL.get();
  }

  public static void set(Map<String, Object> map)
  {
    if (map == null)
    {
      THREAD_LOCAL.remove();
    }
    else
    {
      THREAD_LOCAL.set(map);
    }
  }
}
