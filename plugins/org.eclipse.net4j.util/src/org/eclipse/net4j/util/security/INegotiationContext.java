/*
 * Copyright (c) 2007-2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public interface INegotiationContext
{
  /**
   * @since 2.0
   */
  public static final int NO_TIMEOUT = -1;

  public ByteBuffer getBuffer();

  public void transmitBuffer(ByteBuffer buffer);

  public Receiver getReceiver();

  public void setReceiver(Receiver receiver);

  public Enum<?> getState();

  public void setState(Enum<?> state);

  public void setUserID(String userID);

  public Object getInfo();

  public void setInfo(Object info);

  public void setFinished(boolean success);

  /**
   * @since 2.0
   */
  public Enum<?> waitUntilFinished(long timeout);

  /**
   * @author Eike Stepper
   */
  public interface Receiver
  {
    public void receiveBuffer(INegotiationContext context, ByteBuffer buffer);
  }
}
