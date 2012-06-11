/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSLogoffRequest extends Request
{
  /**
   * @since 2.0
   */
  public JMSLogoffRequest(JMSClientProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_LOGOFF);
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
  }
}
