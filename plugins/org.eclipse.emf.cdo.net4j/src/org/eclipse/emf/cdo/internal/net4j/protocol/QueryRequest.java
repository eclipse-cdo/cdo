/*
 * Copyright (c) 2009-2013, 2017, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.internal.common.CDOQueryInfoImpl;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.object.CDOObjectReferenceImpl;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryRequest extends CDOClientRequest<Boolean>
{
  private CDOView view;

  private AbstractQueryIterator<?> queryResult;

  public QueryRequest(CDOClientProtocol protocol, CDOView view, AbstractQueryIterator<?> queryResult)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY);
    this.view = view;
    this.queryResult = queryResult;
  }

  @Override
  protected CDOIDProvider getIDProvider()
  {
    return (InternalCDOView)view;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(view.getViewID());
    ((CDOQueryInfoImpl)queryResult.getQueryInfo()).write(out);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    int queryID = in.readXInt();
    queryResult.setQueryID(queryID);
    CDOQueryQueue<Object> resultQueue = queryResult.getQueue();

    boolean xrefs = queryResult.getQueryInfo().getQueryLanguage().equals(CDOProtocolConstants.QUERY_LANGUAGE_XREFS);

    try
    {
      while (in.readBoolean())
      {
        Object element;
        if (xrefs)
        {
          CDOIDReference delegate = in.readCDOIDReference();
          element = new CDOObjectReferenceImpl(view, delegate);
        }
        else
        {
          element = in.readCDORevisionOrPrimitive();
        }

        resultQueue.add(element);
      }
    }
    catch (RuntimeException | Error ex)
    {
      resultQueue.setException(ex);
    }
    catch (IOException ex)
    {
      resultQueue.setException(new IORuntimeException(ex.getMessage(), ex));
    }
    catch (Throwable ex)
    {
      resultQueue.setException(new CDOException(ex.getMessage(), ex));
    }
    finally
    {
      resultQueue.close();
    }

    return true;
  }

  @Override
  protected void onRemoteException(RemoteException ex)
  {
    CDOQueryQueue<Object> resultQueue = queryResult.getQueue();
    resultQueue.setException(ex);
  }
}
