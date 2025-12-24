/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadLobIndication extends CDOServerReadIndication
{
  private byte[] id;

  public LoadLobIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_LOB);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    id = in.readByteArray();
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    getRepository().loadLob(id, out);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    throw new UnsupportedOperationException();
  }
}
