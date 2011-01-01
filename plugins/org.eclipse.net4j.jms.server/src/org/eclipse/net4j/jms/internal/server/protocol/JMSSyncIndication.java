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
package org.eclipse.net4j.jms.internal.server.protocol;

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSSyncIndication extends IndicationWithResponse
{
  public JMSSyncIndication(JMSServerProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_SYNC);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeLong(System.currentTimeMillis());
  }
}
