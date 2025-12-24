/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.admin.protocol;

import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSCreateDestinationRequest extends RequestWithConfirmation<Boolean>
{
  private byte type;

  private String name;

  public JMSCreateDestinationRequest(JMSAdminProtocol protocol, byte type, String name)
  {
    super(protocol, JMSAdminProtocolConstants.SIGNAL_CREATE_DESTINATION);
    this.type = type;
    this.name = name;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeByte(type);
    out.writeString(name);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readBoolean();
  }
}
