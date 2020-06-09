/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.test;

/**
 * @author Eike Stepper
 */
public final class CurrentTestName
{
  private static String name;

  public static String get()
  {
    return name;
  }

  public static void set(String name)
  {
    CurrentTestName.name = name;
  }

  private CurrentTestName()
  {
  }
}
