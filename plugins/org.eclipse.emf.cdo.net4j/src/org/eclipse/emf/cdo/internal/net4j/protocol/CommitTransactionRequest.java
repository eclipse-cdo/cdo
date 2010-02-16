/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 215688
 *    Simon McDuff - bug 213402
 *    Andre Dietisheim - bug 256649
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.io.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.io.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;

import org.eclipse.emf.internal.cdo.revision.CDOListWithElementProxiesImpl;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CommitTransactionRequest extends RequestWithMonitoring<CommitTransactionResult>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CommitTransactionRequest.class);

  protected InternalCDOCommitContext commitContext;

  public CommitTransactionRequest(CDOClientProtocol protocol, InternalCDOCommitContext commitContext)
  {
    this(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION, commitContext);
  }

  public CommitTransactionRequest(CDOClientProtocol protocol, short signalID, InternalCDOCommitContext commitContext)
  {
    super(protocol, signalID);
    this.commitContext = commitContext;
  }

  protected InternalCDOCommitContext getCommitContext()
  {
    return commitContext;
  }

  @Override
  public CDOClientProtocol getProtocol()
  {
    return (CDOClientProtocol)super.getProtocol();
  }

  protected InternalCDOSession getSession()
  {
    return (InternalCDOSession)getProtocol().getSession();
  }

  protected CDOIDProvider getIDProvider()
  {
    return commitContext.getTransaction();
  }

  @Override
  protected final void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    requesting(new CDODataOutputImpl(out)
    {
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return getSession().getPackageRegistry();
      }

      @Override
      public CDOIDProvider getIDProvider()
      {
        return CommitTransactionRequest.this.getIDProvider();
      }

      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }
    }, monitor);
  }

  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    requestingTransactionInfo(out);
    requestingCommit(out);
  }

  protected void requestingTransactionInfo(CDODataOutput out) throws IOException
  {
    out.writeInt(commitContext.getTransaction().getViewID());
  }

  protected void requestingCommit(CDODataOutput out) throws IOException
  {
    InternalCDOTransaction transaction = commitContext.getTransaction();
    List<CDOPackageUnit> newPackageUnits = commitContext.getNewPackageUnits();
    Collection<CDOResource> newResources = commitContext.getNewResources().values();
    Collection<CDOObject> newObjects = commitContext.getNewObjects().values();
    Collection<CDORevisionDelta> revisionDeltas = commitContext.getRevisionDeltas().values();
    Collection<CDOID> detachedObjects = commitContext.getDetachedObjects().keySet();

    out.writeBoolean(transaction.options().isAutoReleaseLocksEnabled());
    out.writeString(transaction.getCommitComment());
    out.writeInt(newPackageUnits.size());
    out.writeInt(newResources.size() + newObjects.size());
    out.writeInt(revisionDeltas.size());
    out.writeInt(detachedObjects.size());

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} new package units", newPackageUnits.size()); //$NON-NLS-1$
    }

    for (CDOPackageUnit newPackageUnit : newPackageUnits)
    {
      out.writeCDOPackageUnit(newPackageUnit, true);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} new objects", newResources.size() + newObjects.size()); //$NON-NLS-1$
    }

    writeRevisions(out, newResources);
    writeRevisions(out, newObjects);

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} dirty objects", revisionDeltas.size()); //$NON-NLS-1$
    }

    for (CDORevisionDelta revisionDelta : revisionDeltas)
    {
      out.writeCDORevisionDelta(revisionDelta);
    }

    for (CDOID id : detachedObjects)
    {
      out.writeCDOID(id);
    }
  }

  @Override
  protected final CommitTransactionResult confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    return confirming(new CDODataInputImpl(in)
    {
      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }

      @Override
      protected CDOPackageRegistry getPackageRegistry()
      {
        return getSession().getPackageRegistry();
      }

      @Override
      protected CDOBranchManager getBranchManager()
      {
        return getSession().getBranchManager();
      }

      @Override
      protected CDORevisionFactory getRevisionFactory()
      {
        return getSession().getRevisionManager().getFactory();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListWithElementProxiesImpl.FACTORY;
      }
    }, monitor);
  }

  protected CommitTransactionResult confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    CommitTransactionResult result = confirmingCheckError(in);
    if (result != null)
    {
      return result;
    }

    result = confirmingTransactionResult(in);
    confirmingNewPackage(in, result);
    confirmingIDMappings(in, result);
    return result;
  }

  protected CommitTransactionResult confirmingCheckError(CDODataInput in) throws IOException
  {
    boolean success = in.readBoolean();
    if (!success)
    {
      String rollbackMessage = in.readString();
      OM.LOG.error(rollbackMessage);
      return new CommitTransactionResult(commitContext, rollbackMessage);
    }

    return null;
  }

  protected CommitTransactionResult confirmingTransactionResult(CDODataInput in) throws IOException
  {
    long timeStamp = in.readLong();
    return new CommitTransactionResult(commitContext, timeStamp);
  }

  protected void confirmingNewPackage(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    InternalCDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    for (CDOPackageUnit newPackageUnit : commitContext.getNewPackageUnits())
    {
      for (CDOPackageInfo packageInfo : newPackageUnit.getPackageInfos())
      {
        CDOIDMetaRange oldRange = packageInfo.getMetaIDRange();
        CDOIDMetaRange newRange = in.readCDOIDMetaRange();
        ((InternalCDOPackageInfo)packageInfo).setMetaIDRange(newRange);
        for (int i = 0; i < oldRange.size(); i++)
        {
          CDOIDTemp oldID = (CDOIDTemp)oldRange.get(i);
          CDOID newID = newRange.get(i);
          packageRegistry.getMetaInstanceMapper().remapMetaInstanceID(oldID, newID);
          result.addIDMapping(oldID, newID);
        }
      }
    }
  }

  /*
   * Write IDs that are needed
   */
  protected void confirmingIDMappings(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    for (;;)
    {
      CDOIDTemp oldID = (CDOIDTemp)in.readCDOID();
      if (CDOIDUtil.isNull(oldID))
      {
        break;
      }

      CDOID newID = in.readCDOID();
      result.addIDMapping(oldID, newID);
    }
  }

  @Override
  protected int getMonitorProgressSeconds()
  {
    org.eclipse.emf.cdo.net4j.CDOSession session = (org.eclipse.emf.cdo.net4j.CDOSession)commitContext.getTransaction()
        .getSession();
    return session.options().getProgressInterval();
  }

  @Override
  protected int getMonitorTimeoutSeconds()
  {
    org.eclipse.emf.cdo.net4j.CDOSession session = (org.eclipse.emf.cdo.net4j.CDOSession)commitContext.getTransaction()
        .getSession();
    return session.options().getCommitTimeout();
  }

  private void writeRevisions(CDODataOutput out, Collection<?> objects) throws IOException
  {
    for (Iterator<?> it = objects.iterator(); it.hasNext();)
    {
      CDOObject object = (CDOObject)it.next();
      CDORevision revision = object.cdoRevision();
      out.writeCDORevision(revision, CDORevision.UNCHUNKED);
    }
  }
}
