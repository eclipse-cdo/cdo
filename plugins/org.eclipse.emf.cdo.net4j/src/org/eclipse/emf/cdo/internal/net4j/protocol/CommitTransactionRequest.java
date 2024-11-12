/*
 * Copyright (c) 2009-2013, 2016, 2017, 2019, 2021, 2022, 2024 Eike Stepper (Loehne, Germany) and others.
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
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitData;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

import org.eclipse.emf.internal.cdo.object.CDOObjectReferenceImpl;
import org.eclipse.emf.internal.cdo.view.AbstractCDOView;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOSession.CommitToken;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CommitTransactionRequest extends CDOClientRequestWithMonitoring<CommitTransactionResult>
{
  private static long sleepMillisForTesting = 0L;

  private final int commitNumber;

  private final String commitComment;

  private final Map<String, String> commitProperties;

  private final CDOBranchPoint commitMergeSource;

  private final CDOCommitData commitData;

  private final Collection<CDOLob<?>> lobs;

  private final Collection<CDOLockState> locksOnNewObjects;

  private final Collection<CDOID> idsToUnlock;

  private final int viewID;

  /**
   * Is <code>null</code> in {@link CommitDelegationRequest}.
   */
  private final InternalCDOTransaction transaction;

  private boolean clearResourcePathCache;

  public CommitTransactionRequest(CDOClientProtocol protocol, InternalCDOCommitContext context)
  {
    this(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION, context);
  }

  public CommitTransactionRequest(CDOClientProtocol protocol, short signalID, InternalCDOCommitContext context)
  {
    super(protocol, signalID);
    transaction = context.getTransaction();

    CommitToken commitToken = transaction.getCommitToken();
    if (commitToken != null)
    {
      commitNumber = commitToken.getCommitNumber();
    }
    else
    {
      commitNumber = 0;
    }

    commitComment = context.getCommitComment();
    commitProperties = context.getCommitProperties();
    commitMergeSource = context.getCommitMergeSource();
    commitData = context.getCommitData();
    lobs = context.getLobs();
    locksOnNewObjects = context.getLocksOnNewObjects();
    idsToUnlock = context.getIDsToUnlock();
    viewID = context.getViewID();
  }

  @Override
  protected int getMonitorTimeoutSeconds()
  {
    org.eclipse.emf.cdo.net4j.CDONet4jSession session = (org.eclipse.emf.cdo.net4j.CDONet4jSession)getSession();
    return session.options().getCommitTimeout();
  }

  @Override
  protected CDOIDProvider getIDProvider()
  {
    return transaction;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    requestingTransactionInfo(out);
    requestingCommit(out);
  }

  protected void requestingTransactionInfo(CDODataOutput out) throws IOException
  {
    out.writeXInt(viewID);
  }

  protected void requestingCommit(CDODataOutput out) throws IOException
  {
    List<CDOPackageUnit> newPackageUnits = commitData.getNewPackageUnits();
    List<CDOIDAndVersion> newObjects = commitData.getNewObjects();
    List<CDORevisionKey> changedObjects = commitData.getChangedObjects();
    List<CDOIDAndVersion> detachedObjects = commitData.getDetachedObjects();

    out.writeXLong(getLastUpdateTime());
    out.writeXInt(commitNumber);
    out.writeString(commitComment);
    ExtendedIOUtil.writeProperties(out, commitProperties);
    CDOBranchUtil.writeBranchPointOrNull(out, commitMergeSource);
    out.writeXLong(transaction.options().getOptimisticLockingTimeout());

    out.writeXInt(locksOnNewObjects.size());
    out.writeXInt(idsToUnlock.size());
    out.writeXInt(newPackageUnits.size());
    out.writeXInt(newObjects.size());
    out.writeXInt(changedObjects.size());
    out.writeXInt(detachedObjects.size());

    for (CDOLockState lockState : locksOnNewObjects)
    {
      out.writeCDOLockState(lockState);
    }

    for (CDOID id : idsToUnlock)
    {
      out.writeCDOID(id);
    }

    for (CDOPackageUnit newPackageUnit : newPackageUnits)
    {
      out.writeCDOPackageUnit(newPackageUnit, true);
    }

    for (CDOIDAndVersion newObject : newObjects)
    {
      out.writeCDORevision((CDORevision)newObject, CDORevision.UNCHUNKED);

      if (sleepMillisForTesting != 0L)
      {
        ConcurrencyUtil.sleep(sleepMillisForTesting);
      }
    }

    CDORepositoryInfo repositoryInfo = getSession().getRepositoryInfo();
    CDOID rootResourceID = repositoryInfo.getRootResourceID();
    for (CDORevisionKey changedObject : changedObjects)
    {
      CDORevisionDelta delta = (CDORevisionDelta)changedObject;
      if (!clearResourcePathCache && AbstractCDOView.canHaveResourcePathImpact(delta, rootResourceID))
      {
        clearResourcePathCache = true;
      }

      out.writeCDORevisionDelta(delta);
    }

    out.writeBoolean(clearResourcePathCache);

    boolean auditing = repositoryInfo.isSupportingAudits();
    boolean branching = repositoryInfo.isSupportingBranches();
    boolean ensuringReferentialIntegrity = repositoryInfo.isEnsuringReferentialIntegrity();
    CDOBranch transactionBranch = getBranch();

    for (CDOIDAndVersion detachedObject : detachedObjects)
    {
      CDOID id = detachedObject.getID();
      out.writeCDOID(id);
      if (auditing || ensuringReferentialIntegrity)
      {
        EClass eClass = getObjectType(id);
        out.writeCDOClassifierRef(eClass);
      }

      if (auditing)
      {
        int version = detachedObject.getVersion();
        if (branching && detachedObject instanceof CDORevisionKey)
        {
          CDOBranch branch = ((CDORevisionKey)detachedObject).getBranch();
          if (branch != transactionBranch)
          {
            out.writeXInt(-version);
            out.writeCDOBranch(branch);
            continue;
          }
        }

        out.writeXInt(version);
      }
    }

    requestingLobs();
  }

  protected void requestingLobs() throws IOException
  {
    ExtendedDataOutputStream out = getRequestStream();
    out.writeInt(lobs.size());
    for (CDOLob<?> lob : lobs)
    {
      Closeable closeable = null;

      try
      {
        byte[] id = lob.getID();
        out.writeByteArray(id);

        // Size of CDOBlob is in bytes.
        // Size of CDOClob is in characters.
        long size = lob.getSize();
        out.writeLong(size);

        if (lob instanceof CDOBlob)
        {
          CDOBlob blob = (CDOBlob)lob;
          out.writeBoolean(true); // Binary

          InputStream contents = blob.getContents();
          closeable = contents;
          IOUtil.copyBinary(contents, out, size);
        }
        else
        {
          CDOClob clob = (CDOClob)lob;
          out.writeBoolean(false); // Character

          long byteCount = clob.getByteCount();
          out.writeLong(byteCount);

          Reader contents = clob.getContents();
          closeable = contents;
          IOUtil.copyCharacter(contents, new OutputStreamWriter(out), size);
        }
      }
      finally
      {
        IOUtil.close(closeable);
      }
    }
  }

  protected long getLastUpdateTime()
  {
    return transaction.getLastUpdateTime();
  }

  protected CDOBranch getBranch()
  {
    return transaction.getBranch();
  }

  protected EClass getObjectType(CDOID id)
  {
    CDOObject object = transaction.getObject(id);
    return object.eClass();
  }

  @Override
  protected CommitTransactionResult confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    CommitTransactionResult result = confirmingCheckError(in);
    if (result != null)
    {
      return result;
    }

    result = confirmingResult(in);
    confirmingMappingNewObjects(in, result);
    confirmingLocks(in, result);
    confirmingNewPermissions(in, result);
    confirmingNewCommitData(in, result);
    return result;
  }

  protected CommitTransactionResult confirmingCheckError(CDODataInput in) throws IOException
  {
    boolean success = in.readBoolean();
    if (success)
    {
      return null;
    }

    CommitTransactionResult result = new CommitTransactionResult();
    result.setIDProvider(transaction);
    result.setClearResourcePathCache(clearResourcePathCache);
    result.setRollbackReason(in.readByte());
    result.setRollbackMessage(in.readString());
    result.setBranchPoint(in.readCDOBranchPoint());
    result.setPreviousTimeStamp(in.readXLong());

    int size = in.readXInt();
    if (size != 0)
    {
      List<CDOObjectReference> xRefs = new ArrayList<>(size);
      result.setXRefs(xRefs);

      for (int i = 0; i < size; i++)
      {
        CDOIDReference idReference = in.readCDOIDReference();
        xRefs.add(new CDOObjectReferenceImpl(transaction, idReference));
      }
    }

    return result;
  }

  protected CommitTransactionResult confirmingResult(CDODataInput in) throws IOException
  {
    CommitTransactionResult result = new CommitTransactionResult();
    result.setIDProvider(transaction);
    result.setClearResourcePathCache(clearResourcePathCache);
    result.setBranchPoint(in.readCDOBranchPoint());
    result.setPreviousTimeStamp(in.readXLong());
    result.setSecurityImpact(in.readByte());
    return result;
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

  protected void confirmingLocks(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    List<CDOLockDelta> lockDeltas = in.readCDOLockDeltas();
    result.setLockDeltas(lockDeltas);

    List<CDOLockState> lockStates = in.readCDOLockStates();
    result.setLockStates(lockStates);
  }

  protected void confirmingNewPermissions(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    if (in.readBoolean())
    {
      int n = in.readXInt();
      for (int i = 0; i < n; i++)
      {
        CDOID id = in.readCDOID();
        CDOPermission permission = in.readEnum(CDOPermission.class);

        result.addNewPermission(id, permission);
      }
    }
  }

  protected void confirmingNewCommitData(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    if (in.readBoolean())
    {
      CommitData commitData = new CDOProtocol.CommitData(in);
      result.setNewCommitData(commitData);
    }
  }
}
