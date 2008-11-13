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
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.internal.server.Transaction;
import org.eclipse.emf.cdo.internal.server.Transaction.InternalCommitContext;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContextImpl.TransactionPackageManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class CommitTransactionIndication extends IndicationWithMonitoring
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      CommitTransactionIndication.class);

  protected InternalCommitContext commitContext;

  private int halfWork;

  public CommitTransactionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION);
  }

  protected CommitTransactionIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  public CDOServerProtocol getProtocol()
  {
    return (CDOServerProtocol)super.getProtocol();
  }

  protected Session getSession()
  {
    return getProtocol().getSession();
  }

  protected CDOPackageURICompressor getPackageURICompressor()
  {
    return getSession();
  }

  protected CDOIDProvider getIDProvider()
  {
    return getSession();
  }

  protected CDOIDObjectFactory getIDFactory()
  {
    return getStore().getCDOIDObjectFactory();
  }

  protected SessionManager getSessionManager()
  {
    return getSession().getSessionManager();
  }

  protected Repository getRepository()
  {
    Repository repository = (Repository)getSessionManager().getRepository();
    if (!repository.isActive())
    {
      throw new IllegalStateException("Repository has been deactivated");
    }

    return repository;
  }

  protected RevisionManager getRevisionManager()
  {
    return getRepository().getRevisionManager();
  }

  protected TransactionPackageManager getPackageManager()
  {
    return commitContext.getPackageManager();
  }

  protected IStore getStore()
  {
    IStore store = getRepository().getStore();
    if (!LifecycleUtil.isActive(store))
    {
      throw new IllegalStateException("Store has been deactivated");
    }

    return store;
  }

  @Override
  protected final void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    indicating(new CDODataInputImpl(in)
    {
      @Override
      protected CDORevisionResolver getRevisionResolver()
      {
        return CommitTransactionIndication.this.getRevisionManager();
      }

      @Override
      protected CDOPackageManager getPackageManager()
      {
        return CommitTransactionIndication.this.getPackageManager();
      }

      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CommitTransactionIndication.this.getPackageURICompressor();
      }

      @Override
      protected CDOIDObjectFactory getIDFactory()
      {
        return CommitTransactionIndication.this.getIDFactory();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListImpl.FACTORY;
      }
    }, monitor);
  }

  @Override
  protected final void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    responding(new CDODataOutputImpl(out)
    {
      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CommitTransactionIndication.this.getPackageURICompressor();
      }

      public CDOIDProvider getIDProvider()
      {
        return CommitTransactionIndication.this.getIDProvider();
      }
    }, monitor);
  }

  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    try
    {
      monitor.begin(2);
      indicatingCommit(in, monitor.fork(1));
      indicatingCommit(monitor.fork(1));
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
    finally
    {
      monitor.done();
    }
  }

  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
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

  protected void indicationTransaction(CDODataInput in) throws Exception
  {
    int viewID = in.readInt();
    commitContext = getTransaction(viewID).createCommitContext();
  }

  protected void indicatingCommit(CDODataInput in, OMMonitor monitor) throws Exception
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

    halfWork = newPackages.length + newObjects.length + dirtyObjectDeltas.length + detachedObjects.length;
    monitor.begin(2 * halfWork);

    try
    {
      for (int i = 0; i < newPackages.length; i++)
      {
        InternalCDOPackage newPackage = (InternalCDOPackage)in.readCDOPackage();
        newPackage.setEcore(in.readString());
        newPackages[i] = newPackage;
        packageManager.addPackage(newPackage);
        monitor.worked(1);
      }

      // New objects
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Reading {0} new objects", newObjects.length);
      }

      for (int i = 0; i < newObjects.length; i++)
      {
        newObjects[i] = in.readCDORevision();
        monitor.worked(1);
      }

      // Dirty objects
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Reading {0} dirty object deltas", dirtyObjectDeltas.length);
      }

      for (int i = 0; i < dirtyObjectDeltas.length; i++)
      {
        dirtyObjectDeltas[i] = in.readCDORevisionDelta();
        monitor.worked(1);
      }

      for (int i = 0; i < detachedObjects.length; i++)
      {
        detachedObjects[i] = in.readCDOID();
        monitor.worked(1);
      }

      commitContext.setNewPackages(newPackages);
      commitContext.setNewObjects(newObjects);
      commitContext.setDirtyObjectDeltas(dirtyObjectDeltas);
      commitContext.setDetachedObjects(detachedObjects);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void indicatingCommit(OMMonitor monitor)
  {
    monitor.begin(2);
    try
    {
      commitContext.write(monitor.fork(1));
      if (commitContext.getRollbackMessage() == null)
      {
        commitContext.commit(monitor.fork(1));
      }
      else
      {
        monitor.worked(1);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected boolean respondingException(CDODataOutput out, String rollbackMessage) throws Exception
  {
    boolean success = rollbackMessage == null;
    out.writeBoolean(success);
    if (!success)
    {
      out.writeString(rollbackMessage);
    }

    return success;
  }

  protected void respondingTimestamp(CDODataOutput out) throws Exception
  {
    out.writeLong(commitContext.getTimeStamp());
  }

  protected void respondingMappingNewPackages(CDODataOutput out) throws Exception
  {
    // Meta ID ranges
    List<CDOIDMetaRange> metaRanges = commitContext.getMetaIDRanges();
    for (CDOIDMetaRange metaRange : metaRanges)
    {
      out.writeCDOIDMetaRange(metaRange);
    }
  }

  protected void respondingMappingNewObjects(CDODataOutput out) throws Exception
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
