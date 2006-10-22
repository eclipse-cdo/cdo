/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.stream.BufferInputStream;
import org.eclipse.net4j.util.stream.BufferOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class Signal implements Runnable
{
  private SignalProtocol protocol;

  private int correlationID;

  private BufferInputStream inputStream;

  private BufferOutputStream outputStream;

  private DataInputStream dataInputStream;

  private DataOutputStream dataOutputStream;

  protected Signal()
  {
  }

  protected final SignalProtocol getProtocol()
  {
    return protocol;
  }

  protected final int getCorrelationID()
  {
    return correlationID;
  }

  protected final BufferInputStream getInputStream()
  {
    return inputStream;
  }

  protected final BufferOutputStream getOutputStream()
  {
    return outputStream;
  }

  protected DataInputStream getDataInputStream()
  {
    if (dataInputStream == null)
    {
      dataInputStream = new DataInputStream(inputStream);
    }

    return dataInputStream;
  }

  protected DataOutputStream getDataOutputStream()
  {
    if (dataOutputStream == null)
    {
      dataOutputStream = new DataOutputStream(outputStream);
    }

    return dataOutputStream;
  }

  protected void writeByteArray(byte[] bytes) throws IOException
  {
    getDataOutputStream().writeInt(bytes.length);
    getDataOutputStream().write(bytes);
  }

  protected byte[] readByteArray() throws IOException
  {
    int length = getDataInputStream().readInt();
    byte[] bytes = new byte[length];
    getDataInputStream().read(bytes);
    return bytes;
  }

  public final void run()
  {
    try
    {
      execute(inputStream, outputStream);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      getProtocol().stopSignal(this);
    }
  }

  protected abstract void execute(BufferInputStream in, BufferOutputStream out) throws Exception;

  protected abstract short getSignalID();

  void setProtocol(SignalProtocol protocol)
  {
    this.protocol = protocol;
  }

  void setCorrelationID(int correlationID)
  {
    this.correlationID = correlationID;
  }

  void setInputStream(BufferInputStream inputStream)
  {
    this.inputStream = inputStream;
  }

  void setOutputStream(BufferOutputStream outputStream)
  {
    this.outputStream = outputStream;
  }
}
