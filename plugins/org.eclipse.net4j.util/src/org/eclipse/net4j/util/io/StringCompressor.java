/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO Detect possible race condition and let the client win.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public class StringCompressor
{
  private int increment;

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
    increment = client ? 1 : -1;
  }

  public boolean isClient()
  {
    return increment == 1;
  }

  public void write(ExtendedDataOutput out, String string) throws IOException
  {
    if (string == null)
    {
      out.writeInt(0);
      return;
    }

    int idToWrite;
    String stringToWrite;
    synchronized (stringToID)
    {
      Integer id = stringToID.get(string);
      if (id == null)
      {
        lastID += increment;
        stringToID.put(string, lastID);
        idToString.put(lastID, string);
        idToWrite = lastID;
        stringToWrite = string;
      }
      else
      {
        idToWrite = id;
        stringToWrite = null;
      }
    }

    out.writeInt(idToWrite);
    if (stringToWrite != null)
    {
      out.writeString(stringToWrite);
    }
  }

  public String read(ExtendedDataInput in) throws IOException
  {
    int id = in.readInt();
    if (id == 0)
    {
      return null;
    }

    String string;
    synchronized (stringToID)
    {
      string = idToString.get(id);
    }

    if (string == null)
    {
      string = in.readString();
      synchronized (stringToID)
      {
        stringToID.put(string, id);
        idToString.put(id, string);
      }
    }

    return string;
  }
}
