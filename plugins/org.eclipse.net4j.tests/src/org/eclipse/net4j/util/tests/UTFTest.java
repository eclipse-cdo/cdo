/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.tests;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author Eike Stepper
 */
public class UTFTest extends AbstractOMTest
{
  private static final int UNSIGNED_SHORT_MAX = (1 << 16) - 1;

  private static final int MAX = UNSIGNED_SHORT_MAX / 10 + 1;

  public void testUTF8() throws Exception
  {
    String part = "0123456789";
    assertEquals(10, part.length());

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < MAX; i++)
    {
      builder.append(part);
    }

    String str = builder.toString();
    assertTrue(str.length() > UNSIGNED_SHORT_MAX);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeUTF(str);

    String received = baos.toString("UTF-8");
    assertEquals(str, received);
  }
}
