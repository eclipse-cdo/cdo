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
public class CommitTransactionCancelIndication extends CommitTransactionIndication
{
  public CommitTransactionCancelIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_CANCEL);
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    indicatingTransaction(in);
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    String exceptionMessage = null;
    try
    {
      if (commitContext != null)
      {
        getRepository().getCommitManager().rollback(commitContext);
      }
    }
    catch (Exception exception)
    {
      exceptionMessage = exception.getMessage();
    }

    if (commitContext != null && exceptionMessage == null)
    {
      exceptionMessage = commitContext.getRollbackMessage();
    }

    respondingException(out, exceptionMessage);
  }

  @Override
  protected void indicatingTransaction(CDODataInput in) throws Exception
  {
    int viewID = in.readInt();
    commitContext = getRepository().getCommitManager().get(getTransaction(viewID));
  }
}
