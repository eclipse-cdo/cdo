/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms.util;

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import javax.jms.MessageFormatException;

import java.io.IOException;

public final class TypeUtil
{
  private TypeUtil()
  {
  }

  public static boolean getBoolean(Object value) throws MessageFormatException
  {
    if (value instanceof Boolean)
    {
      return (Boolean)value;
    }
    else if (value instanceof String)
    {
      return Boolean.valueOf((String)value);
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    throw new MessageFormatException(conversionProblem(boolean.class, value));
  }

  public static byte getByte(Object value) throws MessageFormatException
  {
    if (value instanceof Byte)
    {
      return (Byte)value;
    }
    else if (value instanceof String)
    {
      return Byte.parseByte((String)value);
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    throw new MessageFormatException(conversionProblem(byte.class, value));
  }

  public static short getShort(Object value) throws MessageFormatException
  {
    if (value instanceof Short)
    {
      return (Short)value;
    }
    else if (value instanceof Byte)
    {
      return (Byte)value;
    }
    else if (value instanceof String)
    {
      return Short.parseShort((String)value);
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    throw new MessageFormatException(conversionProblem(short.class, value));
  }

  public static char getChar(Object value) throws MessageFormatException
  {
    if (value instanceof Character)
    {
      return (Character)value;
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    throw new MessageFormatException(conversionProblem(char.class, value));
  }

  public static int getInt(Object value) throws MessageFormatException
  {
    if (value instanceof Integer)
    {
      return (Integer)value;
    }
    else if (value instanceof Short)
    {
      return (Short)value;
    }
    else if (value instanceof Byte)
    {
      return (Byte)value;
    }
    else if (value instanceof String)
    {
      return Integer.parseInt((String)value);
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    throw new MessageFormatException(conversionProblem(int.class, value));
  }

  public static long getLong(Object value) throws MessageFormatException
  {
    if (value instanceof Long)
    {
      return (Long)value;
    }
    else if (value instanceof Integer)
    {
      return (Integer)value;
    }
    else if (value instanceof Short)
    {
      return (Short)value;
    }
    else if (value instanceof Byte)
    {
      return (Byte)value;
    }
    else if (value instanceof String)
    {
      return Long.parseLong((String)value);
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    throw new MessageFormatException(conversionProblem(long.class, value));
  }

  public static float getFloat(Object value) throws MessageFormatException
  {
    if (value instanceof Float)
    {
      return (Float)value;
    }
    else if (value instanceof String)
    {
      return Float.parseFloat((String)value);
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    throw new MessageFormatException(conversionProblem(float.class, value));
  }

  public static double getDouble(Object value) throws MessageFormatException
  {
    if (value instanceof Double)
    {
      return (Double)value;
    }
    else if (value instanceof Float)
    {
      return (Float)value;
    }
    else if (value instanceof String)
    {
      return Double.parseDouble((String)value);
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    throw new MessageFormatException(conversionProblem(double.class, value));
  }

  public static String getString(Object value) throws MessageFormatException
  {
    if (value instanceof byte[])
    {
      throw new MessageFormatException(conversionProblem(String.class, value));
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    return String.valueOf(value);
  }

  public static byte[] getBytes(Object value) throws MessageFormatException
  {
    if (value instanceof byte[])
    {
      byte[] bytes = (byte[])value;
      byte[] result = new byte[bytes.length];
      System.arraycopy(bytes, 0, result, 0, bytes.length);
    }
    else if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    throw new MessageFormatException(conversionProblem(byte[].class, value));
  }

  public static void write(ExtendedDataOutputStream out, Object value) throws IOException
  {
    if (value instanceof Boolean)
    {
      out.writeByte(JMSProtocolConstants.TYPE_BOOLEAN);
      out.writeBoolean((Boolean)value);
    }
    else if (value instanceof Byte)
    {
      out.writeByte(JMSProtocolConstants.TYPE_BYTE);
      out.writeByte((Byte)value);
    }
    else if (value instanceof Character)
    {
      out.writeByte(JMSProtocolConstants.TYPE_CHAR);
      out.writeChar((Character)value);
    }
    else if (value instanceof Double)
    {
      out.writeByte(JMSProtocolConstants.TYPE_DOUBLE);
      out.writeDouble((Double)value);
    }
    else if (value instanceof Float)
    {
      out.writeByte(JMSProtocolConstants.TYPE_FLOAT);
      out.writeFloat((Float)value);
    }
    else if (value instanceof Long)
    {
      out.writeByte(JMSProtocolConstants.TYPE_LONG);
      out.writeLong((Long)value);
    }
    else if (value instanceof Short)
    {
      out.writeByte(JMSProtocolConstants.TYPE_SHORT);
      out.writeShort((Short)value);
    }
    else if (value instanceof String)
    {
      out.writeByte(JMSProtocolConstants.TYPE_STRING);
      out.writeString((String)value);
    }

    throw new IllegalArgumentException("value: " + value); //$NON-NLS-1$
  }

  public static Object read(ExtendedDataInputStream in) throws IOException
  {
    byte type = in.readByte();
    switch (type)
    {
    case JMSProtocolConstants.TYPE_BOOLEAN:
      return in.readBoolean();
    case JMSProtocolConstants.TYPE_BYTE:
      return in.readByte();
    case JMSProtocolConstants.TYPE_CHAR:
      return in.readChar();
    case JMSProtocolConstants.TYPE_DOUBLE:
      return in.readDouble();
    case JMSProtocolConstants.TYPE_FLOAT:
      return in.readFloat();
    case JMSProtocolConstants.TYPE_LONG:
      return in.readLong();
    case JMSProtocolConstants.TYPE_SHORT:
      return in.readShort();
    case JMSProtocolConstants.TYPE_STRING:
      return in.readString();
    }

    throw new IOException("Invalid type: " + type); //$NON-NLS-1$
  }

  private static String conversionProblem(Class<?> type, Object value)
  {
    return "Cannot convert values of type " + value.getClass().getName() + " to " + type.getName(); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
