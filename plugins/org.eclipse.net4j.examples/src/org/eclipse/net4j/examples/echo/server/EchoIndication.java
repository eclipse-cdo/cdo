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
package org.eclipse.net4j.examples.echo.server;

import org.eclipse.net4j.examples.echo.EchoProtocol;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EchoIndication extends IndicationWithResponse implements EchoProtocol
{
  private String message;

  public EchoIndication(EchoServerProtocol protocol)
  {
    super(protocol, ECHO_SIGNAL);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    message = in.readString();
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeString(message);
  }
}
