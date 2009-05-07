/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class StringCompressor implements StringIO
{
  private static final boolean DEBUG = false;

  private static final byte DEBUG_STRING = 0;

  private static final byte DEBUG_INT = 1;

  private static final int NULL_ID = 0;

  private static final int STRING_FOLLOWS = Integer.MIN_VALUE;

  private boolean client;

  private int lastID;

  private Map<String, Integer> stringToID = new HashMap<String, Integer>();

  private Map<Integer, String> idToString = new HashMap<Integer, String>();

  /**
   * Creates a StringCompressor instance.
   * 
   * @param client
   *          Must be different on both sides of the stream.
   */
  public StringCompressor(boolean client)
  {
    this.client = client;
  }

  public boolean isClient()
  {
    return client;
  }

  public void write(ExtendedDataOutput out, String string) throws IOException
  {
    if (string == null)
    {
      writeInt(out, NULL_ID);
      return;
    }

    synchronized (stringToID)
    {
      Integer id = stringToID.get(string);
      if (id == null)
      {
        lastID += client ? 1 : -1;
        stringToID.put(string, lastID);
        idToString.put(lastID, string);
        writeInt(out, STRING_FOLLOWS);
        writeInt(out, lastID);
        writeString(out, string);
      }
      else
      {
        writeInt(out, id);
      }
    }
  }

  public String read(ExtendedDataInput in) throws IOException
  {
    int id = readInt(in);
    if (id == NULL_ID)
    {
      return null;
    }

    if (id == STRING_FOLLOWS)
    {
      id = readInt(in);
      String string = readString(in);
      synchronized (stringToID)
      {
        stringToID.put(string, id);
        idToString.put(id, string);
      }

      return string;
    }
    else
    {
      synchronized (stringToID)
      {
        String string = idToString.get(id);
        if (string == null)
        {
          throw new IOException("String ID unknown: " + id);
        }

        return string;
      }
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("StringCompressor[client={0}]", client);
  }

  private void writeInt(ExtendedDataOutput out, int value) throws IOException
  {
    if (DEBUG)
    {
      out.writeByte(DEBUG_INT);
    }

    out.writeInt(value);
  }

  private void writeString(ExtendedDataOutput out, String value) throws IOException
  {
    if (DEBUG)
    {
      out.writeByte(DEBUG_STRING);
    }

    out.writeString(value);
  }

  private int readInt(ExtendedDataInput in) throws IOException
  {
    if (DEBUG)
    {
      if (DEBUG_INT != in.readByte())
      {
        throw new IOException("Not an integer value");
      }
    }

    return in.readInt();
  }

  private String readString(ExtendedDataInput in) throws IOException
  {
    if (DEBUG)
    {
      if (DEBUG_STRING != in.readByte())
      {
        throw new IOException("Not a string value");
      }
    }

    return in.readString();
  }
}
