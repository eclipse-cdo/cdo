/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDORevisionManager;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl.MoveableList;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.protocol.util.TransportException;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.signal.IFailOverStrategy;

import org.eclipse.emf.internal.cdo.protocol.LoadChunkRequest;
import org.eclipse.emf.internal.cdo.protocol.LoadRevisionRequest;

/**
 * @author Eike Stepper
 */
public class CDORevisionManagerImpl extends CDORevisionResolverImpl implements CDORevisionManager
{
  private CDOSessionImpl session;

  public CDORevisionManagerImpl(CDOSessionImpl session)
  {
    this.session = session;
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public CDOID resolveReferenceProxy(CDOReferenceProxy referenceProxy)
  {
    // Get proxy values
    CDORevisionImpl revision = (CDORevisionImpl)referenceProxy.getRevision();
    CDOFeature feature = referenceProxy.getFeature();
    int accessIndex = referenceProxy.getIndex();

    // Get appropriate chunk size
    int chunkSize = session.getReferenceChunkSize();
    if (chunkSize == CDORevisionImpl.UNCHUNKED)
    {
      // Can happen if CDOSession.setReferenceChunkSize() was called meanwhile
      chunkSize = Integer.MAX_VALUE;
    }

    MoveableList list = revision.getList(feature);
    int size = list.size();
    int fromIndex = accessIndex;
    int toIndex = accessIndex;
    boolean minReached = false;
    boolean maxReached = false;
    boolean alternation = false;
    for (int i = 0; i < chunkSize; i++)
    {
      if (alternation)
      {
        if (!maxReached && toIndex < size - 1 && list.get(toIndex + 1) instanceof CDOReferenceProxy)
        {
          ++toIndex;
        }
        else
        {
          maxReached = true;
        }

        if (!minReached)
        {
          alternation = false;
        }
      }
      else
      {
        if (!minReached && fromIndex > 0 && list.get(fromIndex - 1) instanceof CDOReferenceProxy)
        {
          --fromIndex;
        }
        else
        {
          minReached = true;
        }

        if (!maxReached)
        {
          alternation = true;
        }
      }

      if (minReached && maxReached)
      {
        break;
      }
    }

    try
    {
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
      IChannel channel = session.getChannel();
      LoadChunkRequest request = new LoadChunkRequest(channel, revision, feature, accessIndex, fromIndex, toIndex);
      return failOverStrategy.send(request);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }

  @Override
  protected CDORevisionImpl loadRevision(CDOID id, int referenceChunk)
  {
    try
    {
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
      IChannel channel = session.getChannel();
      LoadRevisionRequest request = new LoadRevisionRequest(channel, id, referenceChunk);
      return failOverStrategy.send(request);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }

  @Override
  protected CDORevisionImpl loadRevision(CDOID id, int referenceChunk, long timeStamp)
  {
    try
    {
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
      IChannel channel = session.getChannel();
      LoadRevisionRequest request = new LoadRevisionRequest(channel, id, referenceChunk, timeStamp);
      return failOverStrategy.send(request);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }
}
