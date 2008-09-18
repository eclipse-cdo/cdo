/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *		Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDORevisionManager;
import org.eclipse.emf.cdo.analyzer.CDOFetchRuleManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.LoadChunkRequest;
import org.eclipse.emf.internal.cdo.protocol.LoadRevisionByTimeRequest;
import org.eclipse.emf.internal.cdo.protocol.LoadRevisionByVersionRequest;
import org.eclipse.emf.internal.cdo.protocol.LoadRevisionRequest;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.PerfTracer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDORevisionManagerImpl extends CDORevisionResolverImpl implements CDORevisionManager
{
  private static final PerfTracer LOADING = new PerfTracer(OM.PERF_REVISION_LOADING, CDORevisionManagerImpl.class);

  private CDOSessionImpl session;

  private CDOFetchRuleManager ruleManager = CDOFetchRuleManager.NOOP;

  public CDORevisionManagerImpl(CDOSessionImpl session)
  {
    this.session = session;
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return session;
  }

  /**
   * @since 2.0
   */
  public CDOID resolveReferenceProxy(CDORevision rev, CDOFeature feature, CDOReferenceProxy referenceProxy,
      int accessIndex)
  {
    // Get proxy values
    InternalCDORevision revision = (InternalCDORevision)rev;
    int fetchIndex = referenceProxy.getIndex();

    // Get appropriate chunk size
    int chunkSize = session.getReferenceChunkSize();
    if (chunkSize == CDORevision.UNCHUNKED)
    {
      // Can happen if CDOSession.setReferenceChunkSize() was called meanwhile
      chunkSize = Integer.MAX_VALUE;
    }

    MoveableList<Object> list = revision.getList(feature);
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
      IChannel channel = session.getChannel();
      LoadChunkRequest request = new LoadChunkRequest(channel, revision, feature, accessIndex, fetchIndex, fromIndex,
          toIndex);

      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
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
  protected InternalCDORevision loadRevision(CDOID id, int referenceChunk)
  {
    return send(new LoadRevisionRequest(session.getChannel(), Collections.singleton(id), referenceChunk)).get(0);
  }

  @Override
  protected InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    return send(
        new LoadRevisionByTimeRequest(session.getChannel(), Collections.singleton(id), referenceChunk, timeStamp)).get(
        0);
  }

  @Override
  protected InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    return send(new LoadRevisionByVersionRequest(session.getChannel(), id, referenceChunk, version)).get(0);
  }

  @Override
  protected List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    return send(new LoadRevisionRequest(session.getChannel(), ids, referenceChunk));
  }

  @Override
  protected List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    return send(new LoadRevisionByTimeRequest(session.getChannel(), ids, referenceChunk, timeStamp));
  }

  private List<InternalCDORevision> send(LoadRevisionRequest request)
  {
    try
    {
      LOADING.start(request);
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
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
    finally
    {
      LOADING.stop(request);
    }
  }

  public CDOFetchRuleManager getRuleManager()
  {
    return ruleManager;
  }

  public void setRuleManager(CDOFetchRuleManager ruleManager)
  {
    this.ruleManager = ruleManager;
  }
}
