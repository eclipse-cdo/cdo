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
import javax.jms.MapMessage;
import javax.jms.Message;

import java.io.IOException;
import java.util.Enumeration;

public class MapMessageImpl extends MessageImpl implements MapMessage
{
  @Override
  public boolean getBoolean(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public byte getByte(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public byte[] getBytes(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public char getChar(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public double getDouble(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public float getFloat(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public int getInt(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public long getLong(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public Enumeration<?> getMapNames()
  {
    throw new NotYetImplementedException();
  }

  @Override
  public Object getObject(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public short getShort(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public String getString(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public boolean itemExists(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setBoolean(String name, boolean value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setByte(String name, byte value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setBytes(String name, byte[] value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setBytes(String name, byte[] value, int offset, int length)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setChar(String name, char value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setDouble(String name, double value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setFloat(String name, float value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setInt(String name, int value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setLong(String name, long value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setObject(String name, Object value)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public void setShort(String name, short value)
  {
    throw new NotYetImplementedException();
  }

  @Override
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
