/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
import javax.jms.MapMessage;
import javax.jms.Message;

import java.io.IOException;
import java.util.Enumeration;

public class MapMessageImpl extends MessageImpl implements MapMessage
{
  public boolean getBoolean(String name)
  {
    throw new NotYetImplementedException();
  }

  public byte getByte(String name)
  {
    throw new NotYetImplementedException();
  }

  public byte[] getBytes(String name)
  {
    throw new NotYetImplementedException();
  }

  public char getChar(String name)
  {
    throw new NotYetImplementedException();
  }

  public double getDouble(String name)
  {
    throw new NotYetImplementedException();
  }

  public float getFloat(String name)
  {
    throw new NotYetImplementedException();
  }

  public int getInt(String name)
  {
    throw new NotYetImplementedException();
  }

  public long getLong(String name)
  {
    throw new NotYetImplementedException();
  }

  public Enumeration<?> getMapNames()
  {
    throw new NotYetImplementedException();
  }

  public Object getObject(String name)
  {
    throw new NotYetImplementedException();
  }

  public short getShort(String name)
  {
    throw new NotYetImplementedException();
  }

  public String getString(String name)
  {
    throw new NotYetImplementedException();
  }

  public boolean itemExists(String name)
  {
    throw new NotYetImplementedException();
  }

  public void setBoolean(String name, boolean value)
  {
    throw new NotYetImplementedException();
  }

  public void setByte(String name, byte value)
  {
    throw new NotYetImplementedException();
  }

  public void setBytes(String name, byte[] value)
  {
    throw new NotYetImplementedException();
  }

  public void setBytes(String name, byte[] value, int offset, int length)
  {
    throw new NotYetImplementedException();
  }

  public void setChar(String name, char value)
  {
    throw new NotYetImplementedException();
  }

  public void setDouble(String name, double value)
  {
    throw new NotYetImplementedException();
  }

  public void setFloat(String name, float value)
  {
    throw new NotYetImplementedException();
  }

  public void setInt(String name, int value)
  {
    throw new NotYetImplementedException();
  }

  public void setLong(String name, long value)
  {
    throw new NotYetImplementedException();
  }

  public void setObject(String name, Object value)
  {
    throw new NotYetImplementedException();
  }

  public void setShort(String name, short value)
  {
    throw new NotYetImplementedException();
  }

  public void setString(String name, String value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void populate(Message source) throws JMSException
  {
    super.populate(source);
    MapMessage map = (MapMessage)source;
    Enumeration<?> e = map.getMapNames();
    while (e.hasMoreElements())
    {
      String name = (String)e.nextElement();
      Object value = map.getObject(name);
      setObject(name, value);
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
