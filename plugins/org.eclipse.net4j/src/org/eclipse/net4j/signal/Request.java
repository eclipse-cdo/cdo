/*
 * Copyright (c) 2006-2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * Represents the sender side of a one-way {@link Signal signal}, i.e., one with no response.
 *
 * @author Eike Stepper
 */
public abstract class Request extends SignalActor
{
  /**
   * @since 2.0
   */
  public Request(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  /**
   * @since 2.0
   */
  public Request(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  /**
   * @since 2.0
   */
  public Request(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  /**
   * @since 2.0
   */
  public void sendAsync() throws Exception
  {
    getProtocol().startSignal(this, getProtocol().getTimeout());
  }

  @Override
  void doExecute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    doOutput(out);
  }

  protected abstract void requesting(ExtendedDataOutputStream out) throws Exception;

  @Override
  void doExtendedOutput(ExtendedDataOutputStream out) throws Exception
  {
    requesting(out);
  }
}
