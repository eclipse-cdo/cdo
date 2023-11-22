/*
 * Copyright (c) 2020, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
