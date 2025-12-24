/*
 * Copyright (c) 2010-2012, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class QueryLobsRequest extends CDOClientRequest<List<byte[]>>
{
  private Collection<byte[]> ids;

  public QueryLobsRequest(CDOClientProtocol protocol, Collection<byte[]> ids)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY_LOBS);
    this.ids = ids;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(ids.size());
    for (byte[] id : ids)
    {
      out.writeByteArray(id);
    }
  }

  @Override
  protected List<byte[]> confirming(CDODataInput in) throws IOException
  {
    int size = in.readXInt();
    List<byte[]> result = new ArrayList<>(size);
    for (int i = 0; i < size; i++)
    {
      result.add(in.readByteArray());
    }

    return result;
  }
}
