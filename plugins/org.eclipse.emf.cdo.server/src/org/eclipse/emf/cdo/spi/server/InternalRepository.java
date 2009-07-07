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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.InternalNotificationManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.List;

/**
 * @author Eike Stepper
 */
public interface InternalRepository extends IRepository, InternalCDOPackageRegistry.PackageLoader
{
  public InternalRevisionManager getRevisionManager();

  public void setRevisionManager(InternalRevisionManager revisionManager);

  public InternalSessionManager getSessionManager();

  public void setSessionManager(InternalSessionManager sessionManager);

  public InternalLockManager getLockManager();

  public InternalQueryManager getQueryManager();

  public void setQueryHandlerProvider(IQueryHandlerProvider queryHandlerProvider);

  public InternalCommitManager getCommitManager();

  public InternalNotificationManager getNotificationManager();

  public InternalCDOPackageRegistry getPackageRegistry();

  public InternalCDOPackageRegistry getPackageRegistry(boolean considerCommitContext);

  public long createCommitTimeStamp();

  public void notifyReadAccessHandlers(InternalSession session, CDORevision[] revisions,
      List<CDORevision> additionalRevisions);

  public void notifyWriteAccessHandlers(ITransaction transaction, IStoreAccessor.CommitContext commitContext,
      OMMonitor monitor);
}
