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
 *    Simon McDuff - http://bugs.eclipse.org/215688
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.io.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.io.CDODataOutputImpl;
import org.eclipse.emf.cdo.session.CDORevisionManager;
import org.eclipse.emf.cdo.session.CDOSessionPackageManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackage;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.revision.CDOListWithElementProxiesImpl;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
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
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      CommitTransactionRequest.class);

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
    return (InternalCDOSession)getProtocol().getInfraStructure();
  }

  protected CDORevisionManager getRevisionManager()
  {
    return getSession().getRevisionManager();
  }

  protected CDOSessionPackageManager getPackageManager()
  {
    return getSession().getPackageManager();
  }

  protected CDOPackageURICompressor getPackageURICompressor()
  {
    return getSession();
  }

  protected CDOIDProvider getIDProvider()
  {
    return commitContext.getTransaction();
  }

  protected CDOIDObjectFactory getIDFactory()
  {
    return getSession();
  }

  @Override
  protected final void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    requesting(new CDODataOutputImpl(out)
    {
      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CommitTransactionRequest.this.getPackageURICompressor();
      }

      public CDOIDProvider getIDProvider()
      {
        return CommitTransactionRequest.this.getIDProvider();
      }
    }, monitor);
  }

  @Override
  protected final CommitTransactionResult confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    return confirming(new CDODataInputImpl(in)
    {
      @Override
      protected CDORevisionResolver getRevisionResolver()
      {
        return CommitTransactionRequest.this.getRevisionManager();
      }

      @Override
      protected CDOPackageManager getPackageManager()
      {
        return CommitTransactionRequest.this.getPackageManager();
      }

      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CommitTransactionRequest.this.getPackageURICompressor();
      }

      @Override
      protected CDOIDObjectFactory getIDFactory()
      {
        return CommitTransactionRequest.this.getIDFactory();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListWithElementProxiesImpl.FACTORY;
      }
    }, monitor);
  }

  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    requestingTransactionInfo(out);
    requestingCommit(out);
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
    confirmingIdMapping(in, result);
    return result;
  }

  protected void requestingTransactionInfo(CDODataOutput out) throws IOException
  {
    out.writeInt(commitContext.getTransaction().getViewID());
  }

  protected void requestingCommit(CDODataOutput out) throws IOException
  {
    List<CDOPackage> newPackages = commitContext.getNewPackages();
    Collection<CDOResource> newResources = commitContext.getNewResources().values();
    Collection<CDOObject> newObjects = commitContext.getNewObjects().values();
    Collection<CDORevisionDelta> revisionDeltas = commitContext.getRevisionDeltas().values();
    Collection<CDOID> detachedObjects = commitContext.getDetachedObjects().keySet();

    out.writeBoolean(commitContext.getTransaction().options().isAutoReleaseLocksEnabled());
    out.writeInt(newPackages.size());
    out.writeInt(newResources.size() + newObjects.size());
    out.writeInt(revisionDeltas.size());
    out.writeInt(detachedObjects.size());

    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing {0} new packages", newPackages.size());
    }

    for (CDOPackage newPackage : newPackages)
    {
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Writing package {0}", newPackage);
      }

      out.writeCDOPackage(newPackage);
      out.writeString(((InternalCDOPackage)newPackage).basicGetEcore());
    }

    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing {0} new objects", newResources.size() + newObjects.size());
    }

    writeRevisions(out, newResources);
    writeRevisions(out, newObjects);

    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing {0} dirty objects", revisionDeltas.size());
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
    CommitTransactionResult result = new CommitTransactionResult(commitContext, timeStamp);
    return result;
  }

  protected void confirmingNewPackage(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    InternalCDOSession session = commitContext.getTransaction().getSession();
    List<CDOPackage> newPackages = commitContext.getNewPackages();
    for (CDOPackage newPackage : newPackages)
    {
      if (newPackage.getParentURI() == null)
      {
        CDOIDMetaRange oldRange = newPackage.getMetaIDRange();
        CDOIDMetaRange newRange = in.readCDOIDMetaRange();
        ((InternalCDOPackage)newPackage).setMetaIDRange(newRange);
        for (int i = 0; i < oldRange.size(); i++)
        {
          CDOIDTemp oldID = (CDOIDTemp)oldRange.get(i);
          CDOID newID = newRange.get(i);
          session.remapMetaInstance(oldID, newID);
          result.addIDMapping(oldID, newID);
        }
      }
    }
  }

  /*
   * Write ids that are needed
   */
  public void confirmingIdMapping(CDODataInput in, CommitTransactionResult result) throws IOException
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
