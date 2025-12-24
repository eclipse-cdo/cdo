/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.internal.jms.BytesMessageImpl;
import org.eclipse.net4j.internal.jms.MapMessageImpl;
import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.internal.jms.ObjectMessageImpl;
import org.eclipse.net4j.internal.jms.StreamMessageImpl;
import org.eclipse.net4j.internal.jms.TextMessageImpl;
import org.eclipse.net4j.internal.jms.bundle.OM;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import java.io.IOException;

public final class MessageUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, MessageUtil.class);

  private MessageUtil()
  {
  }

  public static byte getType(Message message)
  {
    if (message instanceof BytesMessage)
    {
      return JMSProtocolConstants.MESSAGE_TYPE_BYTES;
    }

    if (message instanceof MapMessage)
    {
      return JMSProtocolConstants.MESSAGE_TYPE_MAP;
    }

    if (message instanceof ObjectMessage)
    {
      return JMSProtocolConstants.MESSAGE_TYPE_OBJECT;
    }

    if (message instanceof StreamMessage)
    {
      return JMSProtocolConstants.MESSAGE_TYPE_STREAM;
    }

    if (message instanceof TextMessage)
    {
      return JMSProtocolConstants.MESSAGE_TYPE_TEXT;
    }

    throw new IllegalArgumentException("message: " + message); //$NON-NLS-1$
  }

  public static String getTypeName(byte type)
  {
    switch (type)
    {
    case JMSProtocolConstants.MESSAGE_TYPE_BYTES:
      return "BYTES_MESSAGE"; //$NON-NLS-1$

    case JMSProtocolConstants.MESSAGE_TYPE_MAP:
      return "MAP_MESSAGE"; //$NON-NLS-1$

    case JMSProtocolConstants.MESSAGE_TYPE_OBJECT:
      return "OBJECT_MESSAGE"; //$NON-NLS-1$

    case JMSProtocolConstants.MESSAGE_TYPE_STREAM:
      return "STREAM_MESSAGE"; //$NON-NLS-1$

    case JMSProtocolConstants.MESSAGE_TYPE_TEXT:
      return "TEXT_MESSAGE"; //$NON-NLS-1$
    }

    throw new IllegalArgumentException("type: " + type); //$NON-NLS-1$
  }

  public static MessageImpl create(byte type)
  {
    switch (type)
    {
    case JMSProtocolConstants.MESSAGE_TYPE_BYTES:
      return new BytesMessageImpl();

    case JMSProtocolConstants.MESSAGE_TYPE_MAP:
      return new MapMessageImpl();

    case JMSProtocolConstants.MESSAGE_TYPE_OBJECT:
      return new ObjectMessageImpl();

    case JMSProtocolConstants.MESSAGE_TYPE_STREAM:
      return new StreamMessageImpl();

    case JMSProtocolConstants.MESSAGE_TYPE_TEXT:
      return new TextMessageImpl();
    }

    throw new IllegalArgumentException("type: " + type); //$NON-NLS-1$
  }

  public static MessageImpl copy(Message source) throws JMSException
  {
    byte type = getType(source);
    MessageImpl result = create(type);
    result.populate(source);
    return result;
  }

  public static MessageImpl convert(Message source) throws JMSException
  {
    if (source instanceof MessageImpl)
    {
      return (MessageImpl)source;
    }

    return copy(source);
  }

  public static void write(ExtendedDataOutputStream out, MessageImpl message) throws IOException
  {
    byte type = getType(message);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0}", getTypeName(type)); //$NON-NLS-1$
    }

    out.writeByte(type);
    message.write(out);
  }

  public static MessageImpl read(ExtendedDataInputStream in) throws IOException
  {
    byte type = in.readByte();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0}", getTypeName(type)); //$NON-NLS-1$
    }

    MessageImpl message = create(type);
    message.read(in);
    return message;
  }
}
