/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.test.util;


import junit.framework.Assert;


public class TestUtils
{
  public static void assertContains(String message, String text, String word)
  {
    if (text == null)
    {
      Assert.assertNull(message, word);
    }

    Assert.assertTrue(message, text.matches(".*" + word + ".*"));
  }

  public static void assertContains(String text, String word)
  {
    String message = "'" + text + "' should contain '" + word + "'";
    assertContains(message, text, word);
  }

  public static void assertContains(String message, Throwable t, String word)
  {
    String text = t.getMessage();
    assertContains(message, text, word);
  }

  public static void assertContains(Throwable t, String word)
  {
    String text = t.getMessage();
    assertContains(text, word);
  }
}
