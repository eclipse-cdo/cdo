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
import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.internal.jms.util.MessageUtil;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class JMSClientMessageRequest extends RequestWithConfirmation<String>
{
  private MessageImpl message;

  public JMSClientMessageRequest(IChannel channel, MessageImpl message)
  {
    super(channel);
    this.message = message;
  }

  @Override
  protected short getSignalID()
  {
    return JMSProtocolConstants.SIGNAL_CLIENT_MESSAGE;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    MessageUtil.write(out, message);
  }

  @Override
  protected String confirming(ExtendedDataInputStream in) throws IOException
  {
    return in.readString();
  }
}
