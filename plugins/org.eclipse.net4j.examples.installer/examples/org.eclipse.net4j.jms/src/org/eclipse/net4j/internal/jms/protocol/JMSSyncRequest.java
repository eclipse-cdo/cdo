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

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSSyncRequest extends RequestWithConfirmation<Long>
{
  private long clientTime0;

  /**
   * @since 2.0
   */
  public JMSSyncRequest(JMSClientProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_SYNC);
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    clientTime0 = System.currentTimeMillis();
  }

  @Override
  protected Long confirming(ExtendedDataInputStream in) throws Exception
  {
    long serverTime = in.readLong();
    long clientTime1 = System.currentTimeMillis();
    long roundTripDuration = clientTime1 - clientTime0;
    return serverTime + roundTripDuration / 2;
  }
}
