/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/213402
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.internal.common.io.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.io.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.Transaction;
import org.eclipse.emf.cdo.internal.server.Transaction.InternalCommitContext;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.ProgressDistributable;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class CommitTransactionIndication extends IndicationWithMonitoring
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CommitTransactionIndication.class);

  @SuppressWarnings("unchecked")
  private static final ProgressDistributable<InternalCommitContext>[] ops = ProgressDistributor.array( //
      new ProgressDistributable.Default<InternalCommitContext>()
      {
        public void runLoop(int index, InternalCommitContext commitContext, OMMonitor monitor) throws Exception
        {
          commitContext.write(monitor.fork());
        }
      }, //
      new ProgressDistributable.Default<InternalCommitContext>()
      {
        public void runLoop(int index, InternalCommitContext commitContext, OMMonitor monitor) throws Exception
        {
          if (commitContext.getRollbackMessage() == null)
          {
            commitContext.commit(monitor.fork());
          }
          else
          {
            monitor.worked();
          }
        }
      });

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
  public CDOServerProtocol getProtocol()
  {
    return (CDOServerProtocol)super.getProtocol();
  }

  protected Session getSession()
  {
    return (Session)getProtocol().getSession();
  }

  protected Repository getRepository()
  {
    Repository repository = (Repository)getSession().getSessionManager().getRepository();
    if (!LifecycleUtil.isActive(repository))
    {
      throw new IllegalStateException("Repository has been deactivated");
    }

    return repository;
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
        return CommitTransactionIndication.this.getRepository().getRevisionManager();
      }

      @Override
      protected CDOPackageRegistry getPackageRegistry()
      {
        return commitContext.getPackageRegistry();
      }

      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }

      @Override
      protected CDOIDObjectFactory getIDFactory()
      {
        return CommitTransactionIndication.this.getStore().getCDOIDObjectFactory();
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
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }

      public CDOIDProvider getIDProvider()
      {
        return CommitTransactionIndication.this.getSession();
      }
    }, monitor);
  }

  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    try
    {
      monitor.begin(OMMonitor.TEN);
      indicatingCommit(in, monitor.fork(OMMonitor.ONE));
      indicatingCommit(monitor.fork(OMMonitor.TEN - OMMonitor.ONE));
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

  protected void indicatingTransaction(CDODataInput in) throws Exception
  {
    int viewID = in.readInt();
    commitContext = getTransaction(viewID).createCommitContext();
  }

  protected void indicatingCommit(CDODataInput in, OMMonitor monitor) throws Exception
  {
    // Create commit context
    indicatingTransaction(in);
    commitContext.preCommit();

    boolean autoReleaseLocksEnabled = in.readBoolean();
    commitContext.setAutoReleaseLocksEnabled(autoReleaseLocksEnabled);

    InternalCDOPackageUnit[] newPackageUnits = new InternalCDOPackageUnit[in.readInt()];
    InternalCDORevision[] newObjects = new InternalCDORevision[in.readInt()];
    InternalCDORevisionDelta[] dirtyObjectDeltas = new InternalCDORevisionDelta[in.readInt()];
    CDOID[] detachedObjects = new CDOID[in.readInt()];
    monitor.begin(newPackageUnits.length + newObjects.length + dirtyObjectDeltas.length + detachedObjects.length);

    try
    {
      // New package units
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {0} new package units", newPackageUnits.length);
      }

      InternalCDOPackageRegistry packageRegistry = commitContext.getPackageRegistry();
      for (int i = 0; i < newPackageUnits.length; i++)
      {
        newPackageUnits[i] = (InternalCDOPackageUnit)in.readCDOPackageUnit(packageRegistry);
        packageRegistry.putPackageUnit(newPackageUnits[i]); // Must happen before readCDORevision!!!
        monitor.worked();
      }

      // When all packages are deserialized and registered, resolve them
      for (InternalCDOPackageUnit packageUnit : newPackageUnits)
      {
        for (EPackage ePackage : packageUnit.getEPackages(true))
        {
          EcoreUtil.resolveAll(ePackage);
        }
      }

      // New objects
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {0} new objects", newObjects.length);
      }

      for (int i = 0; i < newObjects.length; i++)
      {
        newObjects[i] = (InternalCDORevision)in.readCDORevision();
        monitor.worked();
      }

      // Dirty objects
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {0} dirty object deltas", dirtyObjectDeltas.length);
      }

      for (int i = 0; i < dirtyObjectDeltas.length; i++)
      {
        dirtyObjectDeltas[i] = (InternalCDORevisionDelta)in.readCDORevisionDelta();
        monitor.worked();
      }

      for (int i = 0; i < detachedObjects.length; i++)
      {
        detachedObjects[i] = in.readCDOID();
        monitor.worked();
      }

      commitContext.setNewPackageUnits(newPackageUnits);
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
    ProgressDistributor distributor = getStore().getIndicatingCommitDistributor();
    distributor.run(ops, commitContext, monitor);
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
