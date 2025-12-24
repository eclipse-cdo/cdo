/*
 * Copyright (c) 2010-2012, 2016, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author Eike Stepper
 */
public class LoadLobRequest extends CDOClientRequest<Boolean>
{
  private CDOLobInfo info;

  private Object outputStreamOrWriter;

  public LoadLobRequest(CDOClientProtocol protocol, CDOLobInfo info, Object outputStreamOrWriter)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_LOB);
    this.info = info;
    this.outputStreamOrWriter = outputStreamOrWriter;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeByteArray(info.getID());
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    try
    {
      if (outputStreamOrWriter instanceof OutputStream)
      {
        IOUtil.copyBinary(in, (OutputStream)outputStreamOrWriter, info.getSize());
      }
      else
      {
        IOUtil.copyCharacter(new InputStreamReader(in), (Writer)outputStreamOrWriter, info.getSize());
      }
    }
    finally
    {
      ((Closeable)outputStreamOrWriter).close();
    }

    return true;
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    throw new UnsupportedOperationException();
  }
}
