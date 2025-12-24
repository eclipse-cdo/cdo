/*
 * Copyright (c) 2009-2012, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryCancelIndication extends CDOServerReadIndication
{
  private int queryID;

  public QueryCancelIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY_CANCEL);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    queryID = in.readXInt();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    try
    {
      getRepository().getQueryManager().cancel(queryID);
      out.writeBoolean(false);
    }
    catch (Exception exception)
    {
      out.writeBoolean(true);
      out.writeString(exception.getMessage());
    }
  }
}
