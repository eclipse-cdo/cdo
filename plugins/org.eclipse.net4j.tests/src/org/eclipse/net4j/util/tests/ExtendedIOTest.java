/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
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

  public void testByteArray() throws Exception
  {
    final byte[] byteArray = createByteArray();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream edos = new ExtendedDataOutputStream(baos);
    edos.writeByteArray(byteArray);
    edos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ExtendedDataInputStream edis = new ExtendedDataInputStream(bais);
    byte[] result = edis.readByteArray();
    assertTrue(Arrays.equals(byteArray, result));
  }

  private byte[] createByteArray() throws IOException
  {
    HashMap<String, String> map = createMap();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream edos = new ExtendedDataOutputStream(baos);
    edos.writeObject(map);
    edos.close();
    return baos.toByteArray();
  }

  private HashMap<String, String> createMap()
  {
    HashMap<String, String> map = new HashMap<String, String>();
    putMap(map, "org.eclipse.net4j.util.io.CachedFileMap.java");
    putMap(map, "org.eclipse.net4j.util.io.CloseableIterator.java");
    putMap(map, "org.eclipse.net4j.util.io.DataInputExtender.java");
    putMap(map, "org.eclipse.net4j.util.io.DataOutputExtender.java");
    putMap(map, "org.eclipse.net4j.util.io.DelegatingInputStream.java");
    putMap(map, "org.eclipse.net4j.util.io.DelegatingOutputStream.java");
    putMap(map, "org.eclipse.net4j.util.io.DelegatingStreamWrapper.java");
    putMap(map, "org.eclipse.net4j.util.io.ExtendedDataInput.java");
    putMap(map, "org.eclipse.net4j.util.io.ExtendedDataInputStream.java");
    putMap(map, "org.eclipse.net4j.util.io.ExtendedDataOutput.java");
    putMap(map, "org.eclipse.net4j.util.io.ExtendedDataOutputStream.java");
    putMap(map, "org.eclipse.net4j.util.io.ExtendedIOUtil.java");
    putMap(map, "org.eclipse.net4j.util.io.GZIPStreamWrapper.java");
    putMap(map, "org.eclipse.net4j.util.io.IOFilter.java");
    putMap(map, "org.eclipse.net4j.util.io.IORunnable.java");
    putMap(map, "org.eclipse.net4j.util.io.IORuntimeException.java");
    putMap(map, "org.eclipse.net4j.util.io.IOUtil.java");
    putMap(map, "org.eclipse.net4j.util.io.IOVisitor.java");
    putMap(map, "org.eclipse.net4j.util.io.IStreamWrapper.java");
    putMap(map, "org.eclipse.net4j.util.io.NIOUtil.java");
    putMap(map, "org.eclipse.net4j.util.io.SortedFileMap.java");
    putMap(map, "org.eclipse.net4j.util.io.StreamWrapperChain.java");
    putMap(map, "org.eclipse.net4j.util.io.TMPUtil.java");
    putMap(map, "org.eclipse.net4j.util.io.XORInputStream.java");
    putMap(map, "org.eclipse.net4j.util.io.XOROutputStream.java");
    putMap(map, "org.eclipse.net4j.util.io.XORStreamWrapper.java");
    putMap(map, "org.eclipse.net4j.util.io.ZIPUtil.java");
    return map;
  }

  private void putMap(HashMap<String, String> map, String string)
  {
    map.put(string, string);
  }
}
