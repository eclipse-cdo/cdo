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
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.lob.CDOBlob;
import org.eclipse.emf.cdo.common.model.lob.CDOClob;
import org.eclipse.emf.cdo.common.model.lob.CDOLob;
import org.eclipse.emf.cdo.common.model.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.MetaInstanceMapper;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.revision.CDOListWithElementProxiesImpl;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CommitTransactionRequest extends RequestWithMonitoring<CommitTransactionResult>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CommitTransactionRequest.class);

  private CDOIDProvider idProvider; // CDOTransaction

  private int transactionID;

  private String comment;

  private boolean releaseLocks;

  private CDOCommitData commitData;

  private Collection<CDOLob<?>> lobs;

  private ExtendedDataOutputStream stream;

  public CommitTransactionRequest(CDOClientProtocol protocol, int transactionID, String comment, boolean releaseLocks,
      CDOIDProvider idProvider, CDOCommitData commitData, Collection<CDOLob<?>> lobs)
  {
    this(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION, transactionID, comment, releaseLocks, idProvider,
        commitData, lobs);
  }

  public CommitTransactionRequest(CDOClientProtocol protocol, short signalID, int transactionID, String comment,
      boolean releaseLocks, CDOIDProvider idProvider, CDOCommitData commitData, Collection<CDOLob<?>> lobs)
  {
    super(protocol, signalID);
    this.transactionID = transactionID;
    this.comment = comment;
    this.releaseLocks = releaseLocks;
    this.idProvider = idProvider;
    this.commitData = commitData;
    this.lobs = lobs;
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
    return idProvider;
  }

  @Override
  protected final void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    stream = out;
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
    out.writeInt(transactionID);
  }

  protected void requestingCommit(CDODataOutput out) throws IOException
  {
    List<CDOPackageUnit> newPackageUnits = commitData.getNewPackageUnits();
    List<CDOIDAndVersion> newObjects = commitData.getNewObjects();
    List<CDORevisionKey> changedObjects = commitData.getChangedObjects();
    List<CDOIDAndVersion> detachedObjects = commitData.getDetachedObjects();

    out.writeBoolean(releaseLocks);
    out.writeString(comment);
    out.writeInt(newPackageUnits.size());
    out.writeInt(newObjects.size());
    out.writeInt(changedObjects.size());
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
      TRACER.format("Writing {0} new objects", newObjects.size()); //$NON-NLS-1$
    }

    for (CDOIDAndVersion newObject : newObjects)
    {
      out.writeCDORevision((CDORevision)newObject, CDORevision.UNCHUNKED);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} dirty objects", changedObjects.size()); //$NON-NLS-1$
    }

    for (CDORevisionKey changedObject : changedObjects)
    {
      out.writeCDORevisionDelta((CDORevisionDelta)changedObject);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} detached objects", detachedObjects.size()); //$NON-NLS-1$
    }

    boolean ensuringReferentialIntegrity = getSession().getRepositoryInfo().isEnsuringReferentialIntegrity();
    for (CDOIDAndVersion detachedObject : detachedObjects)
    {
      CDOID id = detachedObject.getID();
      out.writeCDOID(id);
      if (ensuringReferentialIntegrity)
      {
        EClass eClass = getObjectType(id);
        out.writeCDOClassifierRef(eClass);
      }
    }

    requestingLobs(out);
  }

  protected void requestingLobs(CDODataOutput out) throws IOException
  {
    out.writeInt(lobs.size());
    for (CDOLob<?> lob : lobs)
    {
      out.writeByteArray(lob.getID());
      long size = lob.getSize();
      if (lob instanceof CDOBlob)
      {
        CDOBlob blob = (CDOBlob)lob;
        out.writeLong(size);
        IOUtil.copyBinary(blob.getContents(), stream, size);
      }
      else
      {
        CDOClob clob = (CDOClob)lob;
        out.writeLong(-size);
        IOUtil.copyCharacter(clob.getContents(), new OutputStreamWriter(stream), size);
      }
    }
  }

  protected EClass getObjectType(CDOID id)
  {
    CDOTransaction transaction = (CDOTransaction)getSession().getView(transactionID);
    CDOObject object = transaction.getObject(id);
    return object.eClass();
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
      protected CDOCommitInfoManager getCommitInfoManager()
      {
        return getSession().getCommitInfoManager();
      }

      @Override
      protected CDORevisionFactory getRevisionFactory()
      {
        return getSession().getRevisionManager().getFactory();
      }

      @Override
      protected CDOLobStore getLobStore()
      {
        return getSession().getLobStore();
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

    result = confirmingResult(in);
    confirmingMappingNewPackages(in, result);
    confirmingMappingNewObjects(in, result);
    return result;
  }

  protected CommitTransactionResult confirmingCheckError(CDODataInput in) throws IOException
  {
    boolean success = in.readBoolean();
    if (!success)
    {
      String rollbackMessage = in.readString();
      OM.LOG.error(rollbackMessage);
      return new CommitTransactionResult(idProvider, rollbackMessage);
    }

    return null;
  }

  protected CommitTransactionResult confirmingResult(CDODataInput in) throws IOException
  {
    CDOBranchPoint branchPoint = in.readCDOBranchPoint();
    long previousTimeStamp = in.readLong();
    return new CommitTransactionResult(idProvider, branchPoint, previousTimeStamp);
  }

  protected void confirmingMappingNewPackages(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    MetaInstanceMapper metaInstanceMapper = getSession().getPackageRegistry().getMetaInstanceMapper();
    for (CDOPackageUnit newPackageUnit : commitData.getNewPackageUnits())
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
          result.addIDMapping(oldID, newID);
          remapMetaInstanceID(metaInstanceMapper, oldID, newID);
        }
      }
    }
  }

  protected void remapMetaInstanceID(MetaInstanceMapper metaInstanceMapper, CDOIDTemp oldID, CDOID newID)
  {
    metaInstanceMapper.remapMetaInstanceID(oldID, newID);
  }

  protected void confirmingMappingNewObjects(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    for (;;)
    {
      CDOID id = in.readCDOID();
      if (CDOIDUtil.isNull(id))
      {
        break;
      }

      if (id instanceof CDOIDTemp)
      {
        CDOIDTemp oldID = (CDOIDTemp)id;
        CDOID newID = in.readCDOID();
        result.addIDMapping(oldID, newID);
      }
      else
      {
        throw new ClassCastException("Not a temporary ID: " + id);
      }
    }
  }

  @Override
  protected int getMonitorProgressSeconds()
  {
    org.eclipse.emf.cdo.net4j.CDOSession session = (org.eclipse.emf.cdo.net4j.CDOSession)getSession();
    return session.options().getProgressInterval();
  }

  @Override
  protected int getMonitorTimeoutSeconds()
  {
    org.eclipse.emf.cdo.net4j.CDOSession session = (org.eclipse.emf.cdo.net4j.CDOSession)getSession();
    return session.options().getCommitTimeout();
  }
}
