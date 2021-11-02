/*
 * Copyright (c) 2016, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalTransaction.CommitAttempt;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ResetTransactionIndication extends CDOServerReadIndication
{
  private int transactionID;

  private int commitNumber;

  public ResetTransactionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_RESET_TRANSACTION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    transactionID = in.readXInt();
    commitNumber = in.readXInt();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalTransaction transaction = (InternalTransaction)getView(transactionID);
    CommitAttempt lastCommitSuccess = transaction.getLastCommitAttempt();

    if (lastCommitSuccess != null && lastCommitSuccess.getCommitNumber() == commitNumber)
    {
      out.writeBoolean(true);
      out.writeXLong(lastCommitSuccess.getTimeStamp());
      out.writeXLong(lastCommitSuccess.getPreviousTimeStamp());
    }
    else
    {
      out.writeBoolean(false);
    }
  }
}
