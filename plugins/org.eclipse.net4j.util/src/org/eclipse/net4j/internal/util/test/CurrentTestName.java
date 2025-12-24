/*
 * Copyright (c) 2020, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.test;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;

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

  public static void appendTo(File file)
  {
    if (!StringUtil.isEmpty(name))
    {
      IOUtil.appendText(file, name + StringUtil.NL);
    }
  }

  public static void appendTo(String filename)
  {
    appendTo(new File(filename));
  }

  private CurrentTestName()
  {
  }
}
