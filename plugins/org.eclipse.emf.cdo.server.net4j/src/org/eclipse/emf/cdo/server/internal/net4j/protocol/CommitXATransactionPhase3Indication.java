/*
 * Copyright (c) 2010-2013, 2016, 2017 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Simon McDuff
 */
public class CommitXATransactionPhase3Indication extends CommitTransactionIndication
{
  public CommitXATransactionPhase3Indication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_XA_COMMIT_TRANSACTION_PHASE3);
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    initializeCommitContext(in);
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    commitContext.commit(monitor);
    boolean success = respondingException(out, CDOProtocolConstants.ROLLBACK_REASON_UNKNOWN, commitContext.getRollbackMessage(), null);
    commitContext.postCommit(success);
  }

  @Override
  protected void initializeCommitContext(CDODataInput in) throws Exception
  {
    int viewID = in.readXInt();
    commitContext = getRepository().getCommitManager().get(getTransaction(viewID));
  }
}
