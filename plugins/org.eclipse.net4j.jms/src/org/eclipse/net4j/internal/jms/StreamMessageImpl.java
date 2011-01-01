/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
  public boolean readBoolean()
  {
    throw new NotYetImplementedException();
  }

  public byte readByte()
  {
    throw new NotYetImplementedException();
  }

  public int readBytes(byte[] value)
  {
    throw new NotYetImplementedException();
  }

  public char readChar()
  {
    throw new NotYetImplementedException();
  }

  public double readDouble()
  {
    throw new NotYetImplementedException();
  }

  public float readFloat()
  {
    throw new NotYetImplementedException();
  }

  public int readInt()
  {
    throw new NotYetImplementedException();
  }

  public long readLong()
  {
    throw new NotYetImplementedException();
  }

  public Object readObject()
  {
    throw new NotYetImplementedException();
  }

  public short readShort()
  {
    throw new NotYetImplementedException();
  }

  public String readString()
  {
    throw new NotYetImplementedException();
  }

  public void reset()
  {
    throw new NotYetImplementedException();
  }

  public void writeBoolean(boolean value)
  {
    throw new NotYetImplementedException();
  }

  public void writeByte(byte value)
  {
    throw new NotYetImplementedException();
  }

  public void writeBytes(byte[] value)
  {
    throw new NotYetImplementedException();
  }

  public void writeBytes(byte[] value, int offset, int length)
  {
    throw new NotYetImplementedException();
  }

  public void writeChar(char value)
  {
    throw new NotYetImplementedException();
  }

  public void writeDouble(double value)
  {
    throw new NotYetImplementedException();
  }

  public void writeFloat(float value)
  {
    throw new NotYetImplementedException();
  }

  public void writeInt(int value)
  {
    throw new NotYetImplementedException();
  }

  public void writeLong(long value)
  {
    throw new NotYetImplementedException();
  }

  public void writeObject(Object value)
  {
    throw new NotYetImplementedException();
  }

  public void writeShort(short value)
  {
    throw new NotYetImplementedException();
  }

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
