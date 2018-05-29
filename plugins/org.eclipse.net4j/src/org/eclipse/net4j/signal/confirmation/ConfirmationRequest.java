/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.signal.confirmation;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.confirmation.Confirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Christian W. Damus (CEA LIST)
 *
 * @since 4.3
 */
public class ConfirmationRequest extends RequestWithMonitoring<Confirmation>
{
  private final ConfirmationPrompt prompt;

  public ConfirmationRequest(SignalProtocol<?> protocol, short signalID, String name, ConfirmationPrompt prompt)
  {
    super(protocol, signalID, name);
    this.prompt = prompt;
  }

  public ConfirmationRequest(SignalProtocol<?> protocol, short signalID, ConfirmationPrompt prompt)
  {
    super(protocol, signalID);
    this.prompt = prompt;
  }

  public ConfirmationRequest(SignalProtocol<?> protocol, Enum<?> literal, ConfirmationPrompt prompt)
  {
    super(protocol, literal);
    this.prompt = prompt;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    prompt.write(out);
  }

  @Override
  protected Confirmation confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    if (in.readBoolean())
    {
      return in.readEnum(Confirmation.class);
    }

    return null;
  }
}
