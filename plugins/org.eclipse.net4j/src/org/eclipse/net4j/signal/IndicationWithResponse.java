/*
 * Copyright (c) 2006-2008, 2011, 2012, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * Represents the receiver side of a two-way {@link SignalReactor signal}, i.e., one with a response.
 *
 * @author Eike Stepper
 */
public abstract class IndicationWithResponse extends SignalReactor
{
  /**
   * @since 2.0
   */
  public IndicationWithResponse(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  /**
   * @since 2.0
   */
  public IndicationWithResponse(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  /**
   * @since 2.0
   */
  public IndicationWithResponse(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  /**
   * @since 4.4
   */
  protected boolean closeChannelAfterException()
  {
    return false;
  }

  /**
   * @since 2.0
   */
  protected String getExceptionMessage(Throwable t)
  {
    return t.getLocalizedMessage();
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    boolean responding = false;

    try
    {
      doInput(in);
      responding = true;
      doOutput(out);
    }
    catch (Exception | Error ex)
    {
      sendExceptionSignal(ex, responding);
      throw ex;
    }
  }

  protected abstract void indicating(ExtendedDataInputStream in) throws Exception;

  /**
   * <b>Important Note:</b> The response must not be empty, i.e. the stream must be used at least to write a
   * <code>boolean</code>. Otherwise synchronization problems will result!
   */
  protected abstract void responding(ExtendedDataOutputStream out) throws Exception;

  @Override
  void doExtendedInput(ExtendedDataInputStream in) throws Exception
  {
    indicating(in);
  }

  @Override
  void doExtendedOutput(ExtendedDataOutputStream out) throws Exception
  {
    responding(out);
  }

  void sendExceptionSignal(Throwable t, boolean responding) throws Exception
  {
    SignalProtocol<?> protocol = getProtocol();
    int correlationID = -getCorrelationID();
    String message = getExceptionMessage(t);
    boolean closeChannel = closeChannelAfterException();

    RemoteExceptionRequest request = new RemoteExceptionRequest(protocol, correlationID, responding, message, t)
    {
      @Override
      protected boolean closeChannelAfterMe()
      {
        return closeChannel;
      }
    };

    request.sendAsync();
  }
}
