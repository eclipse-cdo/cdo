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

import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocol extends CDOProtocolImpl
{
  public CDOServerProtocol()
  {
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
      return new OpenSessionIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_LIBRARIES:
      return new LoadLibrariesIndication();

    case CDOProtocolConstants.SIGNAL_VIEWS_CHANGED:
      return new ViewsChangedIndication();

    case CDOProtocolConstants.SIGNAL_RESOURCE_ID:
      return new ResourceIDIndication();

    case CDOProtocolConstants.SIGNAL_RESOURCE_PATH:
      return new ResourcePathIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_PACKAGE:
      return new LoadPackageIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION:
      return new LoadRevisionIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_TIME:
      return new LoadRevisionByTimeIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION:
      return new LoadRevisionByVersionIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_CHUNK:
      return new LoadChunkIndication();

    case CDOProtocolConstants.SIGNAL_VERIFY_REVISION:
      return new VerifyRevisionIndication();

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION:
      return new CommitTransactionIndication();

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE1:
      return new CommitTransactionPhase1Indication();

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE2:
      return new CommitTransactionPhase2Indication();

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE3:
      return new CommitTransactionPhase3Indication();

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_CANCEL:
      return new CommitTransactionCancelIndication();

    case CDOProtocolConstants.SIGNAL_QUERY:
      return new QueryIndication();

    case CDOProtocolConstants.SIGNAL_QUERY_CANCEL:
      return new QueryCancelIndication();

    case CDOProtocolConstants.SIGNAL_SYNC:
      return new SyncRevisionIndication();

    case CDOProtocolConstants.SIGNAL_PASSIVE_UPDATE:
      return new PassiveUpdateIndication();

    case CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION:
      return new ChangeSubscriptionIndication();

    default:
      return null;
    }
  }
}
