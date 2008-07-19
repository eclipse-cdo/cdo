/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.examples.echo.client;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.examples.echo.EchoProtocol;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EchoRequest extends RequestWithConfirmation<String> implements EchoProtocol
{
  private String message;

  public EchoRequest(IChannel channel, String message)
  {
    super(channel);
    this.message = message;
  }

  @Override
  protected short getSignalID()
  {
    return ECHO_SIGNAL;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(message);
  }

  @Override
  protected String confirming(ExtendedDataInputStream in) throws IOException
  {
    return in.readString();
  }
}
