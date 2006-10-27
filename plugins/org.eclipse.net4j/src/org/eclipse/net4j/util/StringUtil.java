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
package org.eclipse.net4j.util;

/**
 * @author Eike Stepper
 */
public final class StringUtil
{
  private StringUtil()
  {
  }

  public static int occurrences(String str, char c)
  {
    int count = 0;
    for (int i = 0; (i = str.indexOf(c, i)) != -1; ++i)
    {
      ++count;
    }

    return count;
  }
}
