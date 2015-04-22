/*
 * Copyright (c) 2007, 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms.protocol;

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSDeregisterConsumerRequest extends RequestWithConfirmation<Boolean>
{
  private int sessionID;

  private long consumerID;

  /**
   * @since 2.0
   */
  public JMSDeregisterConsumerRequest(JMSClientProtocol protocol, int sessionID, long consumerID)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_DEREGISTER_CONSUMER);
    this.sessionID = sessionID;
    this.consumerID = consumerID;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeInt(sessionID);
    out.writeLong(consumerID);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readBoolean();
  }
}
