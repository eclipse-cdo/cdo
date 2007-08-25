/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.eclipse.net4j.util.io.SortedFileMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class SortedFileMapTest extends AbstractOMTest
{
  public void testMap() throws Exception
  {
    File file = new File("SortedFileMapTest.dat");
    SortedFileMap<Integer, String> map = new TestMap(file);

    try
    {
      map.put(1, "Eike");
      map.put(3, "René");
      map.put(7, "Birte");
      map.put(9, "Mama");
      map.put(11, "Papa");
      map.put(15, "Julchen");
    }
    finally
    {
      map.close();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TestMap extends SortedFileMap<Integer, String>
  {
    public TestMap(File file)
    {
      super(file, "rw");
    }

    @Override
    public int getKeySize()
    {
      return 4;
    }

    @Override
    protected Integer readKey(DataInput in) throws IOException
    {
      return in.readInt();
    }

    @Override
    protected void writeKey(DataOutput out, Integer key) throws IOException
    {
      out.writeInt(key);
    }

    @Override
    public int getValueSize()
    {
      return 20;
    }

    @Override
    protected String readValue(DataInput in) throws IOException
    {
      return ExtendedIOUtil.readString(in);
    }

    @Override
    protected void writeValue(DataOutput out, String value) throws IOException
    {
      byte[] bytes = value.getBytes();
      if (bytes.length + 4 > getValueSize())
      {
        throw new IllegalArgumentException("Value size of " + getValueSize() + " exceeded: " + value);
      }

      ExtendedIOUtil.writeByteArray(out, bytes);
    }
  }
}
