/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *		Simon McDuff - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.session.CDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDORevisionManagerImpl extends CDORevisionResolverImpl implements CDORevisionManager
{
  private InternalCDOSession session;

  private CDOFetchRuleManager ruleManager = CDOFetchRuleManager.NOOP;

  private RWLockManager<CDORevisionManager, Object> lockmanager = new RWLockManager<CDORevisionManager, Object>();

  private Set<CDORevisionManagerImpl> singletonCollection = Collections.singleton(this);

  /**
   * @since 2.0
   */
  public CDORevisionManagerImpl(InternalCDOSession session)
  {
    this.session = session;
  }

  /**
   * @since 2.0
   */
  public InternalCDOSession getSession()
  {
    return session;
  }

  /**
   * @since 2.0
   */
  public CDOFetchRuleManager getRuleManager()
  {
    return ruleManager;
  }

  /**
   * @since 2.0
   */
  public void setRuleManager(CDOFetchRuleManager ruleManager)
  {
    this.ruleManager = ruleManager;
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return session;
  }

  /**
   * @since 2.0
   */
  public Object resolveElementProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
  {
    return session.options().getCollectionLoadingPolicy().resolveProxy(this, revision, feature, accessIndex,
        serverIndex);
  }

  /**
   * @since 2.0
   */
  public Object loadChunkByRange(CDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex)
  {
    return session.getSessionProtocol().loadChunk((InternalCDORevision)revision, feature, accessIndex, fetchIndex,
        fromIndex, toIndex);
  }

  @Override
  protected InternalCDORevision loadRevision(CDOID id, int referenceChunk)
  {
    return loadRevisions(Collections.singleton(id), referenceChunk).get(0);
  }

  @Override
  protected InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    return loadRevisionsByTime(Collections.singleton(id), referenceChunk, timeStamp).get(0);
  }

  @Override
  protected InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    return session.getSessionProtocol().loadRevisionByVersion(id, referenceChunk, version);
  }

  @Override
  protected List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    return session.getSessionProtocol().loadRevisions(ids, referenceChunk);
  }

  @Override
  protected List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    return session.getSessionProtocol().loadRevisionsByTime(ids, referenceChunk, timeStamp);
  }

  @Override
  protected void acquireAtomicRequestLock(Object key)
  {
    try
    {
      lockmanager.lock(LockType.WRITE, key, this, RWLockManager.WAIT);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  protected void releaseAtomicRequestLock(Object key)
  {
    lockmanager.unlock(LockType.WRITE, key, singletonCollection);
  }
}
