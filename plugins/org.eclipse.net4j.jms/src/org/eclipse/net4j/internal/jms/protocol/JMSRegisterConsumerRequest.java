/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.jms.protocol;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.jms.DestinationImpl;
import org.eclipse.net4j.internal.jms.util.DestinationUtil;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class JMSRegisterConsumerRequest extends RequestWithConfirmation<Long>
{
  private int sessionID;

  private DestinationImpl destination;

  private String messageSelector;

  private boolean noLocal;

  private boolean durable;

  public JMSRegisterConsumerRequest(IChannel channel, int sessionID, DestinationImpl destination,
      String messageSelector, boolean noLocal, boolean durable)
  {
    super(channel);
    this.sessionID = sessionID;
    this.destination = destination;
    this.messageSelector = messageSelector;
    this.noLocal = noLocal;
    this.durable = durable;
  }

  @Override
  protected short getSignalID()
  {
    return JMSProtocolConstants.SIGNAL_REGISTER_CONSUMER;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeInt(sessionID);
    DestinationUtil.write(out, destination);
    out.writeString(messageSelector);
    out.writeBoolean(noLocal);
    out.writeBoolean(durable);
  }

  @Override
  protected Long confirming(ExtendedDataInputStream in) throws IOException
  {
    return in.readLong();
  }
}
