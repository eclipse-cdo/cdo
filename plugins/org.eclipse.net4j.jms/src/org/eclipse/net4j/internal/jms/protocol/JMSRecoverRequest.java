/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
public class JMSRecoverRequest extends RequestWithConfirmation<Object>
{
  private int sessionID;

  /**
   * @since 2.0
   */
  public JMSRecoverRequest(JMSClientProtocol protocol, int sessionID)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_RECOVER);
    this.sessionID = sessionID;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeInt(sessionID);
  }

  @Override
  protected Object confirming(ExtendedDataInputStream in) throws Exception
  {
    return null;
  }
}
