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
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CDOClientProtocol;
import org.eclipse.emf.internal.cdo.protocol.LoadChunkRequest;
import org.eclipse.emf.internal.cdo.protocol.LoadRevisionByTimeRequest;
import org.eclipse.emf.internal.cdo.protocol.LoadRevisionByVersionRequest;
import org.eclipse.emf.internal.cdo.protocol.LoadRevisionRequest;

import org.eclipse.net4j.signal.failover.IFailOverStrategy;
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
  public CDOID resolveReferenceProxy(CDORevision revision, CDOFeature feature, int accessIndex, int serverIndex)
  {
    return session.getCollectionLoadingPolicy().resolveProxy(this, revision, feature, accessIndex, serverIndex);
  }

  /**
   * @since 2.0
   */
  public CDOID loadChunkByRange(CDORevision revision, CDOFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex)
  {
    try
    {
      CDOClientProtocol protocol = session.getProtocol();
      LoadChunkRequest request = new LoadChunkRequest(protocol, (InternalCDORevision)revision, feature, accessIndex,
          fetchIndex, fromIndex, toIndex);

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
    CDOClientProtocol protocol = session.getProtocol();
    return send(new LoadRevisionRequest(protocol, Collections.singleton(id), referenceChunk)).get(0);
  }

  @Override
  protected InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    CDOClientProtocol protocol = session.getProtocol();
    return send(new LoadRevisionByTimeRequest(protocol, Collections.singleton(id), referenceChunk, timeStamp)).get(0);
  }

  @Override
  protected InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    CDOClientProtocol protocol = session.getProtocol();
    return send(new LoadRevisionByVersionRequest(protocol, id, referenceChunk, version)).get(0);
  }

  @Override
  protected List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    CDOClientProtocol protocol = session.getProtocol();
    return send(new LoadRevisionRequest(protocol, ids, referenceChunk));
  }

  @Override
  protected List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    CDOClientProtocol protocol = session.getProtocol();
    return send(new LoadRevisionByTimeRequest(protocol, ids, referenceChunk, timeStamp));
  }

  /**
   * @since 2.0
   */
  @Override
  protected CDOPackageManager getPackageManager()
  {
    return session.getPackageManager();
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
