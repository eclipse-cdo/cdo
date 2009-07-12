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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

  private static final byte DEBUG_BYTE = 2;

  private static final int NULL_ID = 0;

  private static final int INFO_FOLLOWS = Integer.MIN_VALUE;

  private static final byte NOTHING_FOLLOWS = 0;

  private static final byte STRING_FOLLOWS = 1;

  private static final byte ACK_FOLLOWS = 2;

  private boolean client;

  private int lastID;

  private Map<String, ID> stringToID = new HashMap<String, ID>();

  private Map<Integer, String> idToString = new HashMap<Integer, String>();

  private List<Integer> pendingAcknowledgements = new ArrayList<Integer>();

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

    ID id;
    List<Integer> acknowledgements = null;
    boolean stringFollows = false;
    synchronized (this)
    {
      id = stringToID.get(string);
      if (id == null)
      {
        lastID += client ? 1 : -1;
        id = new ID(lastID);

        stringToID.put(string, id);
        idToString.put(id.getValue(), string);
        stringFollows = true;
      }
      else if (!id.isAcknowledged())
      {
        stringFollows = true;
      }

      if (!pendingAcknowledgements.isEmpty())
      {
        acknowledgements = pendingAcknowledgements;
        pendingAcknowledgements = new ArrayList<Integer>();
      }
    }

    if (stringFollows || acknowledgements != null)
    {
      writeInt(out, INFO_FOLLOWS);
      writeInt(out, id.getValue());

      if (stringFollows)
      {
        writeByte(out, STRING_FOLLOWS);
        writeString(out, string);
      }

      if (acknowledgements != null)
      {
        for (int ack : acknowledgements)
        {
          writeByte(out, ACK_FOLLOWS);
          writeInt(out, ack);
        }
      }

      writeByte(out, NOTHING_FOLLOWS);
    }
    else
    {
      writeInt(out, id.getValue());
    }
  }

  public String read(ExtendedDataInput in) throws IOException
  {
    int id = readInt(in);
    if (id == NULL_ID)
    {
      return null;
    }

    String string = null;
    List<Integer> acks = null;
    if (id == INFO_FOLLOWS)
    {
      id = readInt(in);

      boolean moreInfos = true;
      while (moreInfos)
      {
        byte info = readByte(in);
        switch (info)
        {
        case NOTHING_FOLLOWS:
          moreInfos = false;
          break;

        case STRING_FOLLOWS:
          string = readString(in);
          break;

        case ACK_FOLLOWS:
          if (acks == null)
          {
            acks = new ArrayList<Integer>();
          }

          acks.add(readInt(in));
          break;

        default:
          throw new IOException("Invalid info: " + info); //$NON-NLS-1$
        }
      }
    }

    synchronized (this)
    {
      if (string != null)
      {
        acknowledge(acks);
        stringToID.put(string, new ID(id));
        idToString.put(id, string);
        pendingAcknowledgements.add(id);
      }
      else
      {
        acknowledge(acks);
        string = idToString.get(id);
        if (string == null)
        {
          throw new IOException("String ID unknown: " + id); //$NON-NLS-1$
        }
      }
    }

    return string;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("StringCompressor[client={0}]", client); //$NON-NLS-1$
  }

  private void acknowledge(List<Integer> acks)
  {
    if (acks != null)
    {
      for (int value : acks)
      {
        String string = idToString.get(value);
        if (string != null)
        {
          ID id = stringToID.get(string);
          if (id != null)
          {
            id.setAcknowledged();
          }
        }
      }
    }
  }

  private void writeByte(ExtendedDataOutput out, byte value) throws IOException
  {
    if (DEBUG)
    {
      out.writeByte(DEBUG_BYTE);
    }

    out.writeByte(value);
  }

  private void writeInt(ExtendedDataOutput out, int value) throws IOException
  {
    if (DEBUG)
    {
      out.writeByte(DEBUG_INT);
    }

    out.writeInt(value);
  }

  /**
   * @since 3.0
   */
  protected void writeString(ExtendedDataOutput out, String value) throws IOException
  {
    if (DEBUG)
    {
      out.writeByte(DEBUG_STRING);
    }

    out.writeString(value);
  }

  private byte readByte(ExtendedDataInput in) throws IOException
  {
    if (DEBUG)
    {
      if (DEBUG_BYTE != in.readByte())
      {
        throw new IOException("Not a byte value"); //$NON-NLS-1$
      }
    }

    return in.readByte();
  }

  private int readInt(ExtendedDataInput in) throws IOException
  {
    if (DEBUG)
    {
      if (DEBUG_INT != in.readByte())
      {
        throw new IOException("Not an integer value"); //$NON-NLS-1$
      }
    }

    return in.readInt();
  }

  /**
   * @since 3.0
   */
  protected String readString(ExtendedDataInput in) throws IOException
  {
    if (DEBUG)
    {
      if (DEBUG_STRING != in.readByte())
      {
        throw new IOException("Not a string value"); //$NON-NLS-1$
      }
    }

    return in.readString();
  }

  /**
   * @author Eike Stepper
   */
  private static final class ID
  {
    private int value;

    private boolean acknowledged;

    public ID(int value)
    {
      this.value = value;
    }

    public int getValue()
    {
      return value;
    }

    public boolean isAcknowledged()
    {
      return acknowledged;
    }

    public void setAcknowledged()
    {
      acknowledged = true;
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static class Counting extends StringCompressor
  {
    private long stringsRead;

    private long stringsWritten;

    public Counting(boolean client)
    {
      super(client);
    }

    public long getStringsRead()
    {
      return stringsRead;
    }

    public long getStringsWritten()
    {
      return stringsWritten;
    }

    @Override
    protected String readString(ExtendedDataInput in) throws IOException
    {
      synchronized (this)
      {
        ++stringsRead;
      }

      return super.readString(in);
    }

    @Override
    protected void writeString(ExtendedDataOutput out, String value) throws IOException
    {
      synchronized (this)
      {
        ++stringsWritten;
      }

      super.writeString(out, value);
    }
  }
}
