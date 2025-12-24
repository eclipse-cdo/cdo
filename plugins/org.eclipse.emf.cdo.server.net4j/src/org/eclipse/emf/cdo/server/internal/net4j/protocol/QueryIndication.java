/*
 * Copyright (c) 2009-2013, 2016, 2017, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.common.CDOQueryInfoImpl;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandler.PotentiallySlow;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalView;

import java.io.IOException;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class QueryIndication extends CDOServerReadIndication
{
  private boolean xrefs;

  private boolean disableResponseFlushing;

  private boolean disableResponseTimeout;

  private InternalQueryResult queryResult;

  public QueryIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readXInt();
    InternalView view = getView(viewID);
    CDOQueryInfo queryInfo = new CDOQueryInfoImpl(in);

    InternalQueryManager queryManager = getRepository().getQueryManager();
    queryResult = queryManager.execute(view, queryInfo);

    String queryLanguage = queryInfo.getQueryLanguage();
    xrefs = queryLanguage.equals(CDOProtocolConstants.QUERY_LANGUAGE_XREFS);

    Map<String, Object> parameters = queryInfo.getParameters();
    disableResponseFlushing = xrefs || Boolean.TRUE.equals(parameters.get(CDOQueryInfo.PARAM_DISABLE_RESPONSE_FLUSHING));

    disableResponseTimeout = Boolean.TRUE.equals(parameters.get(CDOQueryInfo.PARAM_DISABLE_RESPONSE_TIMEOUT));
    if (!disableResponseTimeout)
    {
      IQueryHandler handler = queryResult.getQueryHandler();
      if (handler instanceof PotentiallySlow)
      {
        PotentiallySlow potentiallySlow = (PotentiallySlow)handler;
        if (potentiallySlow.isSlow(queryInfo))
        {
          disableResponseTimeout = true;
        }
      }
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    if (disableResponseTimeout)
    {
      try
      {
        monitor(1, 10, () -> doRespond(out));
      }
      catch (IOException | RuntimeException | Error ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new IOException(ex);
      }
    }
    else
    {
      doRespond(out);
    }
  }

  private void doRespond(CDODataOutput out) throws IOException
  {
    // Return queryID immediately.
    out.writeXInt(queryResult.getQueryID());
    flushUnlessDisabled();

    for (;;)
    {
      Object object;

      try
      {
        if (!queryResult.hasNext())
        {
          break;
        }

        object = queryResult.next();
      }
      catch (Throwable ex)
      {
        out.writeBoolean(true);
        out.writeCDORevisionOrPrimitive(ex);
        break;
      }

      out.writeBoolean(true);

      if (xrefs)
      {
        CDOIDReference idReference = (CDOIDReference)object;
        out.writeCDOIDReference(idReference);
      }
      else
      {
        out.writeCDORevisionOrPrimitive(object);
      }

      try
      {
        if (queryResult.peek() == null)
        {
          flushUnlessDisabled();
        }
      }
      catch (IOException ex)
      {
        throw ex;
      }
      catch (Throwable ex)
      {
        // Ignore execution exceptions from peek(); they're handled.
      }
    }

    // No more results.
    out.writeBoolean(false);
  }

  private void flushUnlessDisabled() throws IOException
  {
    if (!disableResponseFlushing)
    {
      flush();
    }
  }
}
