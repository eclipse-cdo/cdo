/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.model.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class LoadLobRequest extends CDOClientRequest<Boolean>
{
  private CDOLobInfo info;

  private OutputStream out;

  public LoadLobRequest(CDOClientProtocol protocol, CDOLobInfo info, OutputStream out)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_LOB);
    this.info = info;
    this.out = out;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeByteArray(info.getID());
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    IOUtil.copyBinary(in, out, info.getSize());
    return true;
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    throw new UnsupportedOperationException();
  }
}
