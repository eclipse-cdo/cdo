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

import org.eclipse.net4j.internal.jms.DestinationImpl;
import org.eclipse.net4j.internal.jms.QueueImpl;
import org.eclipse.net4j.internal.jms.TopicImpl;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import java.io.IOException;

public final class DestinationUtil
{
  private DestinationUtil()
  {
  }

  public static byte getType(Destination destination)
  {
    if (destination instanceof Queue)
    {
      return JMSProtocolConstants.DESTINATION_TYPE_QUEUE;
    }

    if (destination instanceof Topic)
    {
      return JMSProtocolConstants.DESTINATION_TYPE_TOPIC;
    }

    if (destination == null)
    {
      return JMSProtocolConstants.DESTINATION_TYPE_NULL;
    }

    throw new IllegalArgumentException("destination: " + destination); //$NON-NLS-1$
  }

  public static String getTypeName(byte type)
  {
    switch (type)
    {
    case JMSProtocolConstants.DESTINATION_TYPE_NULL:
      return "NULL_DESTINATION"; //$NON-NLS-1$

    case JMSProtocolConstants.DESTINATION_TYPE_QUEUE:
      return "QUEUE_DESTINATION"; //$NON-NLS-1$

    case JMSProtocolConstants.DESTINATION_TYPE_TOPIC:
      return "TOPIC_DESTINATION"; //$NON-NLS-1$
    }

    throw new IllegalArgumentException("type: " + type); //$NON-NLS-1$
  }

  public static String getName(Destination destination) throws JMSException
  {
    byte type = getType(destination);
    switch (type)
    {
    case JMSProtocolConstants.DESTINATION_TYPE_QUEUE:
      return ((Queue)destination).getQueueName();

    case JMSProtocolConstants.DESTINATION_TYPE_TOPIC:
      return ((Topic)destination).getTopicName();
    }

    return null;
  }

  public static DestinationImpl create(byte type, String name)
  {
    switch (type)
    {
    case JMSProtocolConstants.DESTINATION_TYPE_QUEUE:
      return new QueueImpl(name);

    case JMSProtocolConstants.DESTINATION_TYPE_TOPIC:
      return new TopicImpl(name);
    }

    return null;
  }

  public static DestinationImpl copy(Destination source) throws JMSException
  {
    byte type = getType(source);
    switch (type)
    {
    case JMSProtocolConstants.DESTINATION_TYPE_QUEUE:
      return new QueueImpl(((Queue)source).getQueueName());

    case JMSProtocolConstants.DESTINATION_TYPE_TOPIC:
      return new TopicImpl(((Topic)source).getTopicName());
    }

    return null;
  }

  public static DestinationImpl convert(Destination source) throws JMSException
  {
    if (source instanceof DestinationImpl)
    {
      return (DestinationImpl)source;
    }

    return copy(source);
  }

  public static void write(ExtendedDataOutputStream out, DestinationImpl destination) throws IOException
  {
    byte type = getType(destination);
    out.writeByte(type);
    if (type != JMSProtocolConstants.DESTINATION_TYPE_NULL)
    {
      out.writeString(destination.getName());
    }
  }

  public static DestinationImpl read(ExtendedDataInputStream in) throws IOException
  {
    byte type = in.readByte();
    String name = null;
    if (type != JMSProtocolConstants.DESTINATION_TYPE_NULL)
    {
      name = in.readString();
    }

    return create(type, name);
  }
}
