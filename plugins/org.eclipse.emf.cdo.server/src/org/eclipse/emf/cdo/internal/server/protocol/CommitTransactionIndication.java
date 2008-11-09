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
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.Transaction;
import org.eclipse.emf.cdo.internal.server.Transaction.InternalCommitContext;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContextImpl.TransactionPackageManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;

import org.eclipse.net4j.util.WrappedException;
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

  protected InternalCommitContext commitContext;

  public CommitTransactionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION);
  }

  protected CommitTransactionIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected TransactionPackageManager getPackageManager()
  {
    return commitContext.getPackageManager();
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    try
    {
      indicatingCommit(in);
      indicatingCommit();
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    boolean success = false;

    try
    {
      success = respondingException(out, commitContext.getRollbackMessage());
      if (success)
      {
        respondingTimestamp(out);
        respondingMappingNewPackages(out);
        respondingMappingNewObjects(out);
      }
    }
    finally
    {
      commitContext.postCommit(success);
    }
  }

  protected void indicationTransaction(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    commitContext = getTransaction(viewID).createCommitContext();
  }

  protected void indicatingCommit(CDODataInput in) throws IOException
  {
    // Create transaction context
    indicationTransaction(in);
    commitContext.preCommit();

    boolean autoReleaseLocksEnabled = in.readBoolean();
    commitContext.setAutoReleaseLocksEnabled(autoReleaseLocksEnabled);

    TransactionPackageManager packageManager = commitContext.getPackageManager();
    CDOPackage[] newPackages = new CDOPackage[in.readInt()];
    CDORevision[] newObjects = new CDORevision[in.readInt()];
    CDORevisionDelta[] dirtyObjectDeltas = new CDORevisionDelta[in.readInt()];
    CDOID[] detachedObjects = new CDOID[in.readInt()];

    // New packages
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} new packages", newPackages.length);
    }

    for (int i = 0; i < newPackages.length; i++)
    {
      InternalCDOPackage newPackage = (InternalCDOPackage)in.readCDOPackage();
      newPackage.setEcore(in.readString());
      newPackages[i] = newPackage;
      packageManager.addPackage(newPackage);
    }

    // New objects
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} new objects", newObjects.length);
    }

    for (int i = 0; i < newObjects.length; i++)
    {
      newObjects[i] = in.readCDORevision();
    }

    // Dirty objects
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} dirty object deltas", dirtyObjectDeltas.length);
    }

    for (int i = 0; i < dirtyObjectDeltas.length; i++)
    {
      dirtyObjectDeltas[i] = in.readCDORevisionDelta();
    }

    for (int i = 0; i < detachedObjects.length; i++)
    {
      detachedObjects[i] = in.readCDOID();
    }

    commitContext.setNewPackages(newPackages);
    commitContext.setNewObjects(newObjects);
    commitContext.setDirtyObjectDeltas(dirtyObjectDeltas);
    commitContext.setDetachedObjects(detachedObjects);
  }

  protected void indicatingCommit()
  {
    commitContext.write();
    if (commitContext.getRollbackMessage() == null)
    {
      commitContext.commit();
    }
  }

  protected boolean respondingException(CDODataOutput out, String rollbackMessage) throws IOException
  {
    boolean success = rollbackMessage == null;
    out.writeBoolean(success);
    if (!success)
    {
      out.writeString(rollbackMessage);
    }

    return success;
  }

  protected void respondingTimestamp(CDODataOutput out) throws IOException
  {
    out.writeLong(commitContext.getTimeStamp());
  }

  protected void respondingMappingNewPackages(CDODataOutput out) throws IOException
  {
    // Meta ID ranges
    List<CDOIDMetaRange> metaRanges = commitContext.getMetaIDRanges();
    for (CDOIDMetaRange metaRange : metaRanges)
    {
      out.writeCDOIDMetaRange(metaRange);
    }
  }

  protected void respondingMappingNewObjects(CDODataOutput out) throws IOException
  {
    // ID mappings
    Map<CDOIDTemp, CDOID> idMappings = commitContext.getIDMappings();
    for (Entry<CDOIDTemp, CDOID> entry : idMappings.entrySet())
    {
      CDOIDTemp oldID = entry.getKey();
      if (!oldID.isMeta())
      {
        CDOID newID = entry.getValue();
        out.writeCDOID(oldID);
        out.writeCDOID(newID);
      }
    }

    out.writeCDOID(CDOID.NULL);
  }

  protected Transaction getTransaction(int viewID)
  {
    IView view = getSession().getView(viewID);
    if (view instanceof Transaction)
    {
      return (Transaction)view;
    }

    throw new IllegalStateException("Illegal transaction: " + view);
  }
}
