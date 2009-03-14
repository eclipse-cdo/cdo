/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Simon McDuff
 */
public class CommitTransactionPhase3Indication extends CommitTransactionIndication
{
  public CommitTransactionPhase3Indication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE3);
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    indicatingTransaction(in);
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    commitContext.commit(monitor);
    boolean success = respondingException(out, commitContext.getRollbackMessage());
    if (success)
    {
      respondingMappingNewPackages(out);
    }

    commitContext.postCommit(success);
  }

  @Override
  protected void indicatingTransaction(CDODataInput in) throws Exception
  {
    int viewID = in.readInt();
    commitContext = getRepository().getCommitManager().get(getTransaction(viewID));
  }
}
