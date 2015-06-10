/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms.protocol;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.internal.jms.util.MessageUtil;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSClientMessageRequest extends RequestWithConfirmation<String>
{
  private MessageImpl message;

  /**
   * @since 2.0
   */
  public JMSClientMessageRequest(JMSClientProtocol protocol, MessageImpl message)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_CLIENT_MESSAGE);
    this.message = message;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    MessageUtil.write(out, message);
  }

  @Override
  protected String confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readString();
  }
}
