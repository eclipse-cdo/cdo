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
package org.eclipse.net4j.util.security;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public interface INegotiationContext
{
  public ByteBuffer getBuffer();

  public void transmitBuffer(ByteBuffer buffer);

  public Receiver getReceiver();

  public void setReceiver(Receiver receiver);

  public Enum<?> getState();

  public void setState(Enum<?> state);

  public void setUserID(String userID);

  public void setFinished(boolean success);

  public Object getInfo();

  public void setInfo(Object info);

  /**
   * @author Eike Stepper
   */
  public interface Receiver
  {
    public void receiveBuffer(INegotiationContext context, ByteBuffer buffer);
  }

}
