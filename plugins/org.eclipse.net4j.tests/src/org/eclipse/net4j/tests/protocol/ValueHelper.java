/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.test.protocol;


import org.eclipse.net4j.core.Channel;
import org.eclipse.net4j.util.ImplementationError;

import java.util.ArrayList;
import java.util.List;


public class ValueHelper
{
  public static final byte END_OF_TRANSMISSION = -1;

  public static final byte BOOLEAN = 1;

  public static final byte CHARACTER = 2;

  public static final byte BYTE = 3;

  public static final byte SHORT = 4;

  public static final byte INTEGER = 5;

  public static final byte LONG = 6;

  public static final byte FLOAT = 7;

  public static final byte DOUBLE = 8;

  public static final byte STRING = 9;

  public static void transmitValues(Channel channel, Object[] values)
  {
    for (int i = 0; i < values.length; i++)
    {
      Object value = values[i];

      if (value instanceof Boolean)
      {
        channel.transmitByte(BOOLEAN);
        channel.transmitBoolean(((Boolean) value).booleanValue());
      }
      else if (value instanceof Character)
      {
        channel.transmitByte(CHARACTER);
        channel.transmitChar(((Character) value).charValue());
      }
      else if (value instanceof Byte)
      {
        channel.transmitByte(BYTE);
        channel.transmitByte(((Byte) value).byteValue());
      }
      else if (value instanceof Short)
      {
        channel.transmitByte(SHORT);
        channel.transmitShort(((Short) value).shortValue());
      }
      else if (value instanceof Integer)
      {
        channel.transmitByte(INTEGER);
        channel.transmitInt(((Integer) value).intValue());
      }
      else if (value instanceof Long)
      {
        channel.transmitByte(LONG);
        channel.transmitLong(((Long) value).longValue());
      }
      else if (value instanceof Float)
      {
        channel.transmitByte(FLOAT);
        channel.transmitFloat(((Float) value).floatValue());
      }
      else if (value instanceof Double)
      {
        channel.transmitByte(DOUBLE);
        channel.transmitDouble(((Double) value).doubleValue());
      }
      else if (value instanceof String)
      {
        channel.transmitByte(STRING);
        channel.transmitString((String) value);
      }
      else
      {
        throw new ImplementationError("invalid class: " + value.getClass().getName());
      }
    }

    channel.transmitByte(END_OF_TRANSMISSION);
  }

  public static Object[] receiveValues(Channel channel)
  {
    List list = new ArrayList();

    for (;;)
    {
      byte type = channel.receiveByte();

      switch (type)
      {
        case END_OF_TRANSMISSION:
          return list.toArray();

        case BOOLEAN:
          list.add(new Boolean(channel.receiveBoolean()));
          break;

        case CHARACTER:
          list.add(new Character(channel.receiveChar()));
          break;

        case BYTE:
          list.add(new Byte(channel.receiveByte()));
          break;

        case SHORT:
          list.add(new Short(channel.receiveShort()));
          break;

        case INTEGER:
          list.add(new Integer(channel.receiveInt()));
          break;

        case LONG:
          list.add(new Long(channel.receiveLong()));
          break;

        case FLOAT:
          list.add(new Float(channel.receiveFloat()));
          break;

        case DOUBLE:
          list.add(new Double(channel.receiveDouble()));
          break;

        case STRING:
          list.add(channel.receiveString());
          break;
      }
    }
  }

  public static int sizeOf(Object[] values)
  {
    int sum = 4; // END_OF_TRANSMISSION
    for (int i = 0; i < values.length; i++)
    {
      Object value = values[i];

      if (value instanceof Boolean)
      {
        sum += 1 + 1;
      }
      else if (value instanceof Character)
      {
        sum += 1 + 2;
      }
      else if (value instanceof Byte)
      {
        sum += 1 + 1;
      }
      else if (value instanceof Short)
      {
        sum += 1 + 2;
      }
      else if (value instanceof Integer)
      {
        sum += 1 + 4;
      }
      else if (value instanceof Long)
      {
        sum += 1 + 8;
      }
      else if (value instanceof Float)
      {
        sum += 1 + 4;
      }
      else if (value instanceof Double)
      {
        sum += 1 + 8;
      }
      else if (value instanceof String)
      {
        sum += 1 + 4 + 2 * ((String) value).length();
      }
      else
      {
        throw new ImplementationError("invalid class: " + value.getClass().getName());
      }
    }

    return sum;
  }
}
