/*
 * Copyright (c) 2007, 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.SortedFileMap;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class SortedFileMapTest extends AbstractOMTest
{
  public void testMap() throws Exception
  {
    File file = createTempFile("testMap-", ".dat"); //$NON-NLS-1$
    if (file.exists())
    {
      file.delete();
    }

    SortedFileMap<Integer, String> map = null;

    try
    {
      map = new TestMap(file);
      for (int i = 0; i < 500; i++)
      {
        map.put(i, "Value " + i); //$NON-NLS-1$
      }

      for (int i = 0; i < 500; i++)
      {
        String value = map.get(i);
        IOUtil.OUT().println(value);
      }
    }
    finally
    {
      IOUtil.close(map);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TestMap extends SortedFileMap<Integer, String>
  {
    public TestMap(File file)
    {
      super(file, "rw"); //$NON-NLS-1$
    }

    @Override
    public int getKeySize()
    {
      return 4;
    }

    @Override
    protected Integer readKey(ExtendedDataInput in) throws IOException
    {
      return in.readInt();
    }

    @Override
    protected void writeKey(ExtendedDataOutput out, Integer key) throws IOException
    {
      out.writeInt(key);
    }

    @Override
    public int getValueSize()
    {
      return 20;
    }

    @Override
    protected String readValue(ExtendedDataInput in) throws IOException
    {
      return in.readString();
    }

    @Override
    protected void writeValue(ExtendedDataOutput out, String value) throws IOException
    {
      byte[] bytes = value.getBytes();
      if (bytes.length + 4 > getValueSize())
      {
        throw new IllegalArgumentException("Value size of " + getValueSize() + " exceeded: " + value); //$NON-NLS-1$ //$NON-NLS-2$
      }

      out.writeByteArray(bytes);
    }
  }
}
