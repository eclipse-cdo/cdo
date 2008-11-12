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
package org.eclipse.net4j.signal;

/**
 * @author Eike Stepper
 */
public abstract class SignalActor extends Signal
{
  /**
   * @since 2.0
   */
  public SignalActor(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
    setCorrelationID(protocol.getNextCorrelationID());
  }

  /**
   * @since 2.0
   */
  public SignalActor(SignalProtocol<?> protocol, short id)
  {
    super(protocol, id);
    setCorrelationID(protocol.getNextCorrelationID());
  }

  /**
   * @since 2.0
   */
  public SignalActor(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
    setCorrelationID(protocol.getNextCorrelationID());
  }

  @Override
  String getInputMeaning()
  {
    return "Confirming";
  }

  @Override
  String getOutputMeaning()
  {
    return "Requesting";
  }
}
