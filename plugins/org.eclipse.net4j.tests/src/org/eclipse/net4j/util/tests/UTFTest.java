/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author Eike Stepper
 */
public class UTFTest extends AbstractOMTest
{
  private static final int UNSIGNED_SHORT_MAX = (1 << 16) - 1;

  public void testUTF8_OneOctet() throws Exception
  {
    final int MAX = UNSIGNED_SHORT_MAX / 10 + 1;
    String part = "0123456789"; //$NON-NLS-1$
    assertEquals(10, part.length());

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < MAX; i++)
    {
      builder.append(part);
    }

    String str = builder.toString();
    assertEquals(true, str.length() > UNSIGNED_SHORT_MAX);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeUTF(str);

    String received = baos.toString("UTF-8"); //$NON-NLS-1$
    assertEquals(str, received);
  }

  public void testUTF8_ThreeOctets() throws Exception
  {
    final int MAX = UNSIGNED_SHORT_MAX >> 1;
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < MAX; i++)
    {
      builder.append("\u6771"); //$NON-NLS-1$
    }

    String str = builder.toString();
    assertEquals(MAX, str.length());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeUTF(str);

    String received = baos.toString("UTF-8"); //$NON-NLS-1$
    assertEquals(str, received);
  }
}
