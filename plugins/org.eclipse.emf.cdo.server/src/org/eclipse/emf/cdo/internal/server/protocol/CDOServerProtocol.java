/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233273    
 *    Simon McDuff - http://bugs.eclipse.org/230832                   
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.common.CDOProtocolImpl;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.server.IRepositoryProvider;

import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocol extends CDOProtocolImpl
{
  private IRepositoryProvider repositoryProvider;

  public CDOServerProtocol(IRepositoryProvider repositoryProvider)
  {
    this.repositoryProvider = repositoryProvider;
  }

  public IRepositoryProvider getRepositoryProvider()
  {
    return repositoryProvider;
  }

  @Override
  public Session getSession()
  {
    return (Session)super.getSession();
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOProtocolConstants.SIGNAL_OPEN_SESSION:
      return new OpenSessionIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_LIBRARIES:
      return new LoadLibrariesIndication(this);

    case CDOProtocolConstants.SIGNAL_VIEWS_CHANGED:
      return new ViewsChangedIndication(this);

    case CDOProtocolConstants.SIGNAL_RESOURCE_ID:
      return new ResourceIDIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_PACKAGE:
      return new LoadPackageIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION:
      return new LoadRevisionIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_TIME:
      return new LoadRevisionByTimeIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION:
      return new LoadRevisionByVersionIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_CHUNK:
      return new LoadChunkIndication(this);

    case CDOProtocolConstants.SIGNAL_VERIFY_REVISION:
      return new VerifyRevisionIndication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION:
      return new CommitTransactionIndication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE1:
      return new CommitTransactionPhase1Indication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE2:
      return new CommitTransactionPhase2Indication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE3:
      return new CommitTransactionPhase3Indication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_CANCEL:
      return new CommitTransactionCancelIndication(this);

    case CDOProtocolConstants.SIGNAL_QUERY:
      return new QueryIndication(this);

    case CDOProtocolConstants.SIGNAL_QUERY_CANCEL:
      return new QueryCancelIndication(this);

    case CDOProtocolConstants.SIGNAL_SYNC:
      return new SyncRevisionIndication(this);

    case CDOProtocolConstants.SIGNAL_PASSIVE_UPDATE:
      return new SetPassiveUpdateIndication(this);

    case CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION:
      return new ChangeSubscriptionIndication(this);

    case CDOProtocolConstants.SIGNAL_SET_AUDIT:
      return new SetAuditIndication(this);

    case CDOProtocolConstants.SIGNAL_REPOSITORY_TIME:
      return new RepositoryTimeIndication(this);

    case CDOProtocolConstants.SIGNAL_LOCK_OBJECTS:
      return new LockObjectsIndication(this);

    case CDOProtocolConstants.SIGNAL_UNLOCK_OBJECTS:
      return new UnlockObjectsIndication(this);

    case CDOProtocolConstants.SIGNAL_OBJECT_LOCKED:
      return new ObjectLockedIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }
}
