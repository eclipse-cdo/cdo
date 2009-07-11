/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageProcessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLocker;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalCDOSession extends CDOSession, PackageProcessor, PackageLoader, RevisionLocker, ILifecycle
{
  /**
   * @since 3.0
   */
  public InternalCDOSessionConfiguration getConfiguration();

  public CDOSessionProtocol getSessionProtocol();

  /**
   * @since 3.0
   */
  public void setSessionProtocol(CDOSessionProtocol sessionProtocol);

  public InternalCDOPackageRegistry getPackageRegistry();

  /**
   * @since 3.0
   */
  public InternalCDORevisionManager getRevisionManager();

  public void setExceptionHandler(CDOSession.ExceptionHandler exceptionHandler);

  /**
   * @since 3.0
   */
  public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager);

  /**
   * @since 3.0
   */
  public void setRepositoryInfo(CDORepositoryInfo repositoryInfo);

  /**
   * @since 3.0
   */
  public void setRemoteSessionManager(InternalCDORemoteSessionManager remoteSessionManager);

  /**
   * @since 3.0
   */
  public void setSessionID(int sessionID);

  public void setUserID(String userID);

  public void viewDetached(InternalCDOView view);

  /**
   * @since 3.0
   */
  public Object resolveElementProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex);

  public void handleCommitNotification(long timeStamp, Collection<CDOPackageUnit> newPackageUnits,
      Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects, Collection<CDORevisionDelta> deltas,
      InternalCDOView excludedView);

  public void handleSyncResponse(long timestamp, Collection<CDOPackageUnit> newPackageUnits,
      Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects);

  /**
   * In some cases we need to sync without propagating event. Lock is a good example.
   */
  public void handleUpdateRevision(final long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
      Collection<CDOID> detachedObjects);
}
