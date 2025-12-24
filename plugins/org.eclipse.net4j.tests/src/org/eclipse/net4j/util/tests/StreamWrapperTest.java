/*
 * Copyright (c) 2010-2013, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.XORInputStream;
import org.eclipse.net4j.util.io.XOROutputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public class StreamWrapperTest extends AbstractOMTest
{
  @SuppressWarnings("resource")
  public void testXORStreams() throws Exception
  {
    int[] key = { 1, 2, 3, 4 };

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    XOROutputStream xorOutputStream = new XOROutputStream(byteArrayOutputStream, key);
    PrintStream printStream = new PrintStream(xorOutputStream);
    printStream.println("Hello world!");
    printStream.println("Hello world!");

    XORInputStream xorInputStream = new XORInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), key);

    InputStreamReader inputStreamReader = new InputStreamReader(xorInputStream);
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    assertEquals("Hello world!", bufferedReader.readLine());
    assertEquals("Hello world!", bufferedReader.readLine());
  }
}
