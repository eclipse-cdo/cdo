/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageEOFException;
import javax.jms.StreamMessage;

import java.io.IOException;

public class StreamMessageImpl extends MessageImpl implements StreamMessage
{
  @Override
  public boolean readBoolean()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public byte readByte()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public int readBytes(byte[] value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public char readChar()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public double readDouble()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public float readFloat()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public int readInt()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public long readLong()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public Object readObject()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public short readShort()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public String readString()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void reset()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeBoolean(boolean value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeByte(byte value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeBytes(byte[] value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeBytes(byte[] value, int offset, int length)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeChar(char value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeDouble(double value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeFloat(float value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeInt(int value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeLong(long value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeObject(Object value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeShort(short value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void writeString(String value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void populate(Message source) throws JMSException
  {
    super.populate(source);
    StreamMessage from = (StreamMessage)source;
    from.reset();
    try
    {
      while (true)
      {
        Object object = from.readObject();
        writeObject(object);
      }
    }
    catch (MessageEOFException ignore)
    {
    }
  }

  @Override
  public void write(ExtendedDataOutputStream out) throws IOException
  {
    super.write(out);
  }

  @Override
  public void read(ExtendedDataInputStream in) throws IOException
  {
    super.read(in);
  }
}
