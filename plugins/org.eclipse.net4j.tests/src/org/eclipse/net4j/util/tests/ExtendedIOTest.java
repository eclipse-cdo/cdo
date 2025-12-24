/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Eike Stepper
 */
public class ExtendedIOTest extends AbstractOMTest
{
  @SuppressWarnings("unchecked")
  public void testObject() throws Exception
  {
    final HashMap<String, String> map = createMap();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream edos = new ExtendedDataOutputStream(baos);
    edos.writeObject(map);
    edos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ExtendedDataInputStream edis = new ExtendedDataInputStream(bais);
    HashMap<String, String> result = (HashMap<String, String>)edis.readObject();
    assertEquals(map, result);
  }

  public void testObject1() throws Exception
  {
    final byte[] byteArray = createByteArray1();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream edos = new ExtendedDataOutputStream(baos);
    edos.writeObject(byteArray);
    edos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ExtendedDataInputStream edis = new ExtendedDataInputStream(bais);
    byte[] result = (byte[])edis.readObject();
    assertEquals(true, Arrays.equals(byteArray, result));
  }

  public void testObject2() throws Exception
  {
    final byte[] byteArray = createByteArray2();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream edos = new ExtendedDataOutputStream(baos);
    edos.writeObject(byteArray);
    edos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ExtendedDataInputStream edis = new ExtendedDataInputStream(bais);
    byte[] result = (byte[])edis.readObject();
    assertEquals(true, Arrays.equals(byteArray, result));
  }

  public void testByteArray1() throws Exception
  {
    final byte[] byteArray = createByteArray1();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream edos = new ExtendedDataOutputStream(baos);
    edos.writeByteArray(byteArray);
    edos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ExtendedDataInputStream edis = new ExtendedDataInputStream(bais);
    byte[] result = edis.readByteArray();
    assertEquals(true, Arrays.equals(byteArray, result));
  }

  public void testByteArray2() throws Exception
  {
    final byte[] byteArray = createByteArray2();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream edos = new ExtendedDataOutputStream(baos);
    edos.writeByteArray(byteArray);
    edos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ExtendedDataInputStream edis = new ExtendedDataInputStream(bais);
    byte[] result = edis.readByteArray();
    assertEquals(true, Arrays.equals(byteArray, result));
  }

  private byte[] createByteArray1() throws IOException
  {
    HashMap<String, String> map = createMap();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream edos = new ExtendedDataOutputStream(baos);
    edos.writeObject(map);
    edos.close();
    return baos.toByteArray();
  }

  private byte[] createByteArray2()
  {
    byte[] byteArray = new byte[256];
    byte v = Byte.MIN_VALUE;
    for (int i = 0; i < byteArray.length; i++)
    {
      byteArray[i] = v++;
    }

    return byteArray;
  }

  private HashMap<String, String> createMap()
  {
    HashMap<String, String> map = new HashMap<>();
    putMap(map, "org.eclipse.net4j.util.io.CachedFileMap.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.CloseableIterator.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.DataInputExtender.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.DataOutputExtender.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.DelegatingInputStream.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.DelegatingOutputStream.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.DelegatingStreamWrapper.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.ExtendedDataInput.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.ExtendedDataInputStream.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.ExtendedDataOutput.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.ExtendedDataOutputStream.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.ExtendedIOUtil.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.GZIPStreamWrapper.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.IOFilter.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.IORunnable.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.IORuntimeException.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.IOUtil.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.IOVisitor.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.IStreamWrapper.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.NIOUtil.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.SortedFileMap.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.StreamWrapperChain.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.TMPUtil.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.XORInputStream.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.XOROutputStream.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.XORStreamWrapper.java"); //$NON-NLS-1$
    putMap(map, "org.eclipse.net4j.util.io.ZIPUtil.java"); //$NON-NLS-1$
    return map;
  }

  private void putMap(HashMap<String, String> map, String string)
  {
    map.put(string, string);
  }
}
