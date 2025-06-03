/*
 * Copyright (c) 2021-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.protocol;

/**
 * @author Eike Stepper
 */
class CDOProtocolVersion
{
  private static final int VALUE;

  static
  {
    VALUE = 52; // Provide an entity directory service and protocol #89

    // VALUE = 51; // Support login peeking
    // VALUE = 50; // Support optional lock state prefetching while opening a CDOUnit #20
    // VALUE = 49; // Add security support to CDO-LM #5
    // VALUE = 48; // SIGNAL_LOCK_OWNER_REMAPPED_NOTIFICATION
    // VALUE = 47; // Bug 580439 - Truncated CDOLobs in repository
    // VALUE = 46; // SIGNAL_REMOTE_TOPIC
    // VALUE = 45; // SIGNAL_AUTHORIZE_OPERATIONS
    // VALUE = 44; // SIGNAL_VIEW_CLOSED_NOTIFICATION
    // VALUE = 43; // SIGNAL_DELETE_BRANCH
    // VALUE = 42; // CDOPrefetcherManager, see bug 576893
    // VALUE = 41; // ReconnectionSession mechanism does not update some items after a reconnection
    // VALUE = 40; // Prevent protocol version constant inlining
    // VALUE = 39; // CDOLockOwner.isDurableView becomes derived
    // VALUE = 38; // CDOBranchTag support
    // VALUE = 37; // SIGNAL_ACKNOWLEDGE_COMPRESSED_STRINGS
    // VALUE = 36; // CDOID.NIL
    // VALUE = 35; // DiffieHellman.Server.Challenge.getSecretAlgorithmKeyLen()
    // VALUE = 34; // CDOSessionProtocol.loadMergeData2()
    // VALUE = 33; // CDOCommitInfo.getMergeSource()
    // VALUE = 32; // ROLLBACK_REASON_UNIT_INTEGRITY
    // VALUE = 31; // CDOCommonRepository.isSupportingUnits()
    // VALUE = 30; // UnitOpcode
    // VALUE = 29; // SIGNAL_UNIT
    // VALUE = 28; // SIGNAL_RESET_TRANSACTION
    // VALUE = 27; // SIGNAL_OPENED_SESSION
    // VALUE = 26; // Add prefetch depth in LockStateRequest/Indication
    // VALUE = 25; // OpenSessionResponse.repositoryAuthenticating
    // VALUE = 24; // SIGNAL_LOAD_OBJECT_LIFETIME
    // VALUE = 23; // Fix branch renaming
    // VALUE = 22; // Prefetch returns RevisionInfo instead of CDORevision to have PointerCDORevision
    // VALUE = 21; // Update how CDOChangeSetData's detachedObject is encoded, see bug 449171
    // VALUE = 20; // Have OMMonitor optional in RequestWithMonitoring/IndicationWithMonitoring
    // VALUE = 19; // Branch renaming
    // VALUE = 18; // Password change protocol
    // VALUE = 17; // Last update for make query fail in sequence
  }

  static int getValue()
  {
    return VALUE;
  }
}
