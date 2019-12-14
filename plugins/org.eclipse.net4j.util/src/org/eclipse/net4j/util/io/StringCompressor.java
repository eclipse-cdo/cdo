/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class StringCompressor implements StringIO
{
  /**
   * @since 3.0
   */
  public static boolean BYPASS = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.util.io.StringCompressor.BYPASS");

  private static final int NULL_ID = 0;

  private static final int INFO_FOLLOWS = Integer.MIN_VALUE;

  private static final byte NOTHING_FOLLOWS = 1;

  private static final byte STRING_FOLLOWS = 2;

  private static final byte ACK_FOLLOWS = 3;

  private static final boolean DEBUG = false;

  private static final byte DEBUG_STRING = -1;

  private static final byte DEBUG_INT = -2;

  private static final byte DEBUG_BYTE = -3;

  private boolean client;

  private int lastID;

  private Map<String, ID> stringToID = new HashMap<>();

  private Map<Integer, String> idToString = new HashMap<>();

  private Collection<Integer> pendingAcknowledgements = createAcknowledgementCollection();

  private long lastAcknowledgementCheck;

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

  @Override
  public void write(ExtendedDataOutput out, String string) throws IOException
  {
    if (DEBUG)
    {
      trace("BEGIN", string);
    }

    if (string == null)
    {
      writeInt(out, NULL_ID);
      return;
    }

    ID id;
    Collection<Integer> acknowledgements = null;
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

      lastAcknowledgementCheck = System.currentTimeMillis();
      if (!pendingAcknowledgements.isEmpty())
      {
        acknowledgements = pendingAcknowledgements;
        pendingAcknowledgements = createAcknowledgementCollection();
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

      writeAcknowledgements(out, acknowledgements);
    }
    else
    {
      writeInt(out, id.getValue());
    }
  }

  @Override
  public String read(ExtendedDataInput in) throws IOException
  {
    if (DEBUG)
    {
      trace("BEGIN", "?");
    }

    int id = readInt(in);
    if (id == NULL_ID)
    {
      return null;
    }

    String string = null;
    Collection<Integer> acks = null;
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
            acks = createAcknowledgementCollection();
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
      processAcknowledgements(acks);

      if (string != null)
      {
        stringToID.put(string, new ID(id));
        idToString.put(id, string);
        pendingAcknowledgements.add(id);
      }
      else
      {
        string = idToString.get(id);
        if (string == null)
        {
          throw new IOException("String ID unknown: " + id); //$NON-NLS-1$
        }
      }
    }

    return string;
  }

  /**
   * @since 3.8
   */
  public Collection<Integer> getPendingAcknowledgements(long timeout)
  {
    Collection<Integer> acknowledgements = null;

    synchronized (this)
    {
      long now = System.currentTimeMillis();
      if (lastAcknowledgementCheck + timeout < now)
      {
        lastAcknowledgementCheck = now;
        if (!pendingAcknowledgements.isEmpty())
        {
          acknowledgements = pendingAcknowledgements;
          pendingAcknowledgements = createAcknowledgementCollection();
        }
      }
    }

    return acknowledgements;
  }

  /**
   * @since 3.8
   */
  public void writeAcknowledgements(ExtendedDataOutput out, Collection<Integer> acknowledgements) throws IOException
  {
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

  /**
   * @since 3.8
   */
  public Collection<Integer> readAcknowledgements(ExtendedDataInput in) throws IOException
  {
    Collection<Integer> acknowledgements = createAcknowledgementCollection();
    while (in.readByte() == ACK_FOLLOWS)
    {
      acknowledgements.add(in.readInt());
    }

    return acknowledgements;
  }

  /**
   * @since 3.8
   */
  public void processAcknowledgements(Collection<Integer> acknowledgements)
  {
    if (acknowledgements != null)
    {
      synchronized (this)
      {
        for (int value : acknowledgements)
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
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("StringCompressor[client={0}]", client); //$NON-NLS-1$
  }

  private void writeByte(ExtendedDataOutput out, byte value) throws IOException
  {
    if (DEBUG)
    {
      trace("writeByte", value);
      out.writeByte(DEBUG_BYTE);
    }

    out.writeByte(value);
  }

  private void writeInt(ExtendedDataOutput out, int value) throws IOException
  {
    if (DEBUG)
    {
      trace("writeInt", value);
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
      trace("writeString", value);
      out.writeByte(DEBUG_STRING);
    }

    out.writeString(value);
  }

  private byte readByte(ExtendedDataInput in) throws IOException
  {
    if (DEBUG)
    {
      byte type = in.readByte();
      if (DEBUG_BYTE != type)
      {
        throw new IOException("Not a byte value (type=" + type + ")"); //$NON-NLS-1$
      }
    }

    byte value = in.readByte();
    if (DEBUG)
    {
      trace("readByte", value);
    }

    return value;
  }

  private int readInt(ExtendedDataInput in) throws IOException
  {
    if (DEBUG)
    {
      byte type = in.readByte();
      if (DEBUG_INT != type)
      {
        throw new IOException("Not an integer value (type=" + type + ")"); //$NON-NLS-1$
      }
    }

    int value = in.readInt();
    if (DEBUG)
    {
      trace("readInt", value);
    }

    return value;
  }

  /**
   * @since 3.0
   */
  protected String readString(ExtendedDataInput in) throws IOException
  {
    if (DEBUG)
    {
      byte type = in.readByte();
      if (DEBUG_STRING != type)
      {
        throw new IOException("Not a string value (type=" + type + ")"); //$NON-NLS-1$
      }
    }

    String value = in.readString();
    if (DEBUG)
    {
      trace("readString", value);
    }

    return value;
  }

  private void trace(String prefix, Object value)
  {
    if (value instanceof Byte)
    {
      byte opcode = (Byte)value;
      switch (opcode)
      {
      case NOTHING_FOLLOWS:
        value = "NOTHING_FOLLOWS";
        break;

      case STRING_FOLLOWS:
        value = "STRING_FOLLOWS";
        break;

      case ACK_FOLLOWS:
        value = "STRING_FOLLOWS";
        break;
      }
    }

    if (value instanceof Integer)
    {
      int opcode = (Integer)value;
      if (opcode == INFO_FOLLOWS)
      {
        value = "INFO_FOLLOWS";
      }
    }

    String msg = "[" + Thread.currentThread().getName() + "] " + prefix + ": " + value;
    if (!client)
    {
      msg = "                                                                   " + msg;
    }

    IOUtil.OUT().println(msg);
  }

  private static Collection<Integer> createAcknowledgementCollection()
  {
    return new HashSet<>();
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
      CheckUtil.checkArg(value != INFO_FOLLOWS, "value");
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
