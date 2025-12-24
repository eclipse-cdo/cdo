/*
 * Copyright (c) 2010-2013, 2015-2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectExternalImpl;
import org.eclipse.emf.cdo.internal.net4j.messages.Messages;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * <p>
 * Phase 2 consist of sending the mapping of temporary/persistent CDOID from other CDOTransaction.
 * <p>
 * It will return confirmation only when the commit is ready to flush to disk.
 *
 * @author Simon McDuff
 */
public class CommitXATransactionPhase2Request extends CommitXATransactionRequest
{
  public CommitXATransactionPhase2Request(CDOClientProtocol protocol, InternalCDOXACommitContext xaContext)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_XA_COMMIT_TRANSACTION_PHASE2, xaContext);
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    requestingTransactionInfo(out);
    requestingIDMapping(out);
  }

  /**
   * Write IDs that are needed. only If it needs to
   */
  protected void requestingIDMapping(CDODataOutput out) throws IOException
  {
    InternalCDOXACommitContext context = getCommitContext();
    Map<CDOIDTempObjectExternalImpl, InternalCDOTransaction> requestedIDs = context.getRequestedIDs();
    int size = requestedIDs.size();
    out.writeXInt(size);

    for (Map.Entry<CDOIDTempObjectExternalImpl, InternalCDOTransaction> entry : requestedIDs.entrySet())
    {
      CDOIDTempObjectExternalImpl tempID = entry.getKey();
      URI oldURIExternal = URI.createURI(tempID.toURIFragment());
      CDOID oldCDOID = CDOIDUtil.read(oldURIExternal.fragment());

      InternalCDOXACommitContext commitContext = context.getTransactionManager().getCommitContext(entry.getValue());
      if (commitContext == null)
      {
        throw new IllegalStateException(MessageFormat.format(Messages.getString("CommitTransactionPhase2Request.1"), entry //$NON-NLS-1$
            .getValue()));
      }

      CDOID newID = commitContext.getResult().getIDMappings().get(oldCDOID);
      if (newID == null)
      {
        throw new IllegalStateException(MessageFormat.format(Messages.getString("CommitTransactionPhase2Request.2"), oldCDOID //$NON-NLS-1$
            .toURIFragment()));
      }

      CDOID newIDExternal = CDOURIUtil.convertExternalCDOID(oldURIExternal, newID);
      out.writeCDOID(tempID);
      out.writeCDOID(newIDExternal);

      context.getResult().addIDMapping(tempID, newIDExternal);
    }
  }

  @Override
  protected CommitTransactionResult confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    return confirmingCheckError(in);
  }
}
