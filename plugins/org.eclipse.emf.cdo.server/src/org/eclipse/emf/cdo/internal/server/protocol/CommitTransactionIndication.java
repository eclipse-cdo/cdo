/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.internal.server.Transaction;
import org.eclipse.emf.cdo.internal.server.Transaction.TransactionPackageManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class CommitTransactionIndication extends CDOServerIndication
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      CommitTransactionIndication.class);

  private Transaction transaction;

  public CommitTransactionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    IRepository repository = getRepository();
    CDORevisionResolver revisionResolver = repository.getRevisionManager();

    int viewID = in.readInt();
    transaction = getTransaction(viewID);
    transaction.preCommit();

    TransactionPackageManager packageManager = transaction.getPackageManager();
    CDOPackage[] newPackages = new CDOPackage[in.readInt()];
    CDORevision[] newObjects = new CDORevision[in.readInt()];
    CDORevisionDelta[] dirtyObjectDeltas = new CDORevisionDelta[in.readInt()];

    // New packages
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} new packages", newPackages.length);
    }

    for (int i = 0; i < newPackages.length; i++)
    {
      newPackages[i] = CDOModelUtil.readPackage(packageManager, in);
      packageManager.addPackage(newPackages[i]);
    }

    // New objects
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} new objects", newObjects.length);
    }

    for (int i = 0; i < newObjects.length; i++)
    {
      newObjects[i] = CDORevisionUtil.read(in, revisionResolver, packageManager);
    }

    // Dirty objects
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} dirty object deltas", dirtyObjectDeltas.length);
    }

    for (int i = 0; i < dirtyObjectDeltas.length; i++)
    {
      dirtyObjectDeltas[i] = new CDORevisionDeltaImpl(in, packageManager);
    }

    transaction.commit(newPackages, newObjects, dirtyObjectDeltas);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    boolean success = false;

    try
    {
      String rollbackMessage = transaction.getRollbackMessage();
      success = rollbackMessage == null;
      out.writeBoolean(success);
      if (success)
      {
        out.writeLong(transaction.getTimeStamp());

        // Meta ID ranges
        List<CDOIDMetaRange> metaRanges = transaction.getMetaIDRanges();
        for (CDOIDMetaRange metaRange : metaRanges)
        {
          CDOIDUtil.writeMetaRange(out, metaRange);
        }

        // ID mappings
        Map<CDOIDTemp, CDOID> idMappings = transaction.getIDMappings();
        for (Entry<CDOIDTemp, CDOID> entry : idMappings.entrySet())
        {
          CDOIDTemp oldID = entry.getKey();
          if (!oldID.isMeta())
          {
            CDOID newID = entry.getValue();
            CDOIDUtil.write(out, oldID);
            CDOIDUtil.write(out, newID);
          }
        }

        CDOIDUtil.write(out, CDOID.NULL);
      }
      else
      {
        out.writeString(rollbackMessage);
      }
    }
    finally
    {
      transaction.postCommit(success);
    }
  }

  private Transaction getTransaction(int viewID)
  {
    IView view = getSession().getView(viewID);
    if (view instanceof Transaction)
    {
      return (Transaction)view;
    }

    throw new IllegalStateException("Illegal transaction: " + view);
  }
}
