/*
 * Copyright (c) 2012-2017, 2019-2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.protocol;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.model.CDOType.Handler;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermissionProvider;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionUnchunker;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class CDODataOutputImpl extends ExtendedDataOutput.Delegating implements CDODataOutput
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDODataOutputImpl.class);

  public CDODataOutputImpl(ExtendedDataOutput delegate)
  {
    super(delegate);
  }

  /**
   * @since 4.6
   */
  @Override
  public void writeXInt(int v) throws IOException
  {
    if (isXCompression())
    {
      writeVarInt(v);
    }
    else
    {
      writeInt(v);
    }
  }

  /**
   * @since 4.6
   */
  @Override
  public void writeXLong(long v) throws IOException
  {
    if (isXCompression())
    {
      writeVarLong(v);
    }
    else
    {
      writeLong(v);
    }
  }

  @Override
  public void writeCDOPackageUnit(CDOPackageUnit packageUnit, boolean withPackages) throws IOException
  {
    ((InternalCDOPackageUnit)packageUnit).write(this, withPackages);
  }

  @Override
  public void writeCDOPackageUnits(CDOPackageUnit... packageUnits) throws IOException
  {
    int size = packageUnits.length;
    writeXInt(size);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} package units", size); //$NON-NLS-1$
    }

    for (CDOPackageUnit packageUnit : packageUnits)
    {
      writeCDOPackageUnit(packageUnit, false);
    }
  }

  @Override
  public void writeCDOPackageUnitType(CDOPackageUnit.Type type) throws IOException
  {
    writeByte(type.ordinal());
  }

  @Override
  public void writeCDOPackageInfo(CDOPackageInfo packageInfo) throws IOException
  {
    ((InternalCDOPackageInfo)packageInfo).write(this);
  }

  @Override
  public void writeCDOClassifierRef(CDOClassifierRef eClassifierRef) throws IOException
  {
    eClassifierRef.write(this);
  }

  @Override
  public void writeCDOClassifierRef(EClassifier eClassifier) throws IOException
  {
    writeCDOClassifierRef(new CDOClassifierRef(eClassifier));
  }

  @Override
  public void writeCDOPackageURI(String uri) throws IOException
  {
    getPackageURICompressor().write(this, uri);
  }

  @Override
  public void writeCDOType(CDOType cdoType) throws IOException
  {
    ((CDOTypeImpl)cdoType).write(this);
  }

  @Override
  public void writeCDOBranch(CDOBranch branch) throws IOException
  {
    writeXInt(branch.getID());
  }

  @Override
  public void writeCDOBranchPoint(CDOBranchPoint branchPoint) throws IOException
  {
    writeCDOBranch(branchPoint.getBranch());
    writeXLong(branchPoint.getTimeStamp());
  }

  @Override
  public void writeCDOBranchVersion(CDOBranchVersion branchVersion) throws IOException
  {
    writeCDOBranch(branchVersion.getBranch());
    writeXInt(branchVersion.getVersion());
  }

  @Override
  public void writeCDOChangeSetData(CDOChangeSetData changeSetData) throws IOException
  {
    Collection<CDOIDAndVersion> newObjects = changeSetData.getNewObjects();
    writeXInt(newObjects.size());
    for (CDOIDAndVersion data : newObjects)
    {
      if (data instanceof CDORevision)
      {
        writeBoolean(true);
        writeCDORevision((CDORevision)data, CDORevision.UNCHUNKED);
      }
      else
      {
        writeBoolean(false);
        writeCDOIDAndVersion(data);
      }
    }

    Collection<CDORevisionKey> changedObjects = changeSetData.getChangedObjects();
    writeXInt(changedObjects.size());
    for (CDORevisionKey data : changedObjects)
    {
      if (data instanceof CDORevisionDelta)
      {
        writeBoolean(true);
        writeCDORevisionDelta((CDORevisionDelta)data);
      }
      else
      {
        writeBoolean(false);
        writeCDORevisionKey(data);
      }
    }

    Collection<CDOIDAndVersion> detachedObjects = changeSetData.getDetachedObjects();
    writeXInt(detachedObjects.size());
    for (CDOIDAndVersion data : detachedObjects)
    {
      writeCDOID(data.getID());
      boolean isCDORevisionKey = data instanceof CDORevisionKey;
      int version = data.getVersion();
      writeXInt(version);
      writeBoolean(isCDORevisionKey);
      if (isCDORevisionKey)
      {
        CDORevisionKey revisionKey = (CDORevisionKey)data;
        writeCDOBranch(revisionKey.getBranch());
      }
    }
  }

  @Override
  public void writeCDOCommitData(CDOCommitData commitData) throws IOException
  {
    Collection<CDOPackageUnit> newPackageUnits = commitData.getNewPackageUnits();
    writeXInt(newPackageUnits.size());
    for (CDOPackageUnit data : newPackageUnits)
    {
      writeCDOPackageUnit(data, false);
    }

    writeCDOChangeSetData(commitData);
  }

  @Override
  public void writeCDOCommitInfo(CDOCommitInfo commitInfo) throws IOException
  {
    writeXLong(commitInfo.getTimeStamp());
    writeXLong(commitInfo.getPreviousTimeStamp());

    CDOBranch branch = commitInfo.getBranch();
    if (branch != null)
    {
      writeBoolean(true);
      writeCDOBranch(branch);
      writeString(commitInfo.getUserID());
      writeString(commitInfo.getComment());
      CDOBranchUtil.writeBranchPointOrNull(this, commitInfo.getMergeSource());
      writeCDOCommitData(commitInfo);
    }
    else
    {
      // FailureCommitInfo
      writeBoolean(false);
    }
  }

  @Override
  public void writeCDOLockChangeInfo(CDOLockChangeInfo lockChangeInfo) throws IOException
  {
    writeCDOLockChangeInfo(lockChangeInfo, null);
  }

  @Override
  public void writeCDOLockChangeInfo(CDOLockChangeInfo lockChangeInfo, Set<CDOID> filter) throws IOException
  {
    if (lockChangeInfo.isInvalidateAll())
    {
      writeBoolean(true);
      return;
    }

    writeBoolean(false);
    writeCDOBranchPoint(lockChangeInfo);
    writeCDOLockOwner(lockChangeInfo.getLockOwner());
    writeCDOLockDeltas(Arrays.asList(lockChangeInfo.getLockDeltas()), filter == null ? null : delta -> filter.contains(delta.getID()));
    writeCDOLockStates(Arrays.asList(lockChangeInfo.getLockStates()), filter == null ? null : state -> filter.contains(state.getID()));
    writeBoolean(lockChangeInfo.isAdministrative());
  }

  @Override
  public void writeCDOLockArea(LockArea lockArea) throws IOException
  {
    writeString(lockArea.getDurableLockingID());
    writeCDOBranch(lockArea.getBranch());
    writeXLong(lockArea.getTimeStamp());
    writeString(lockArea.getUserID());
    writeBoolean(lockArea.isReadOnly());

    Map<CDOID, LockGrade> locks = lockArea.getLocks();
    writeXInt(locks.size());

    for (Map.Entry<CDOID, LockGrade> entry : locks.entrySet())
    {
      writeCDOID(entry.getKey());
      writeEnum(entry.getValue());
    }
  }

  @Override
  public void writeCDOLockOwner(CDOLockOwner lockOwner) throws IOException
  {
    if (lockOwner == null)
    {
      writeXInt(CDOLockUtil.DURABLE_SESSION_ID - 1);
    }
    else
    {
      writeXInt(lockOwner.getSessionID());
      writeXInt(lockOwner.getViewID());
      writeString(lockOwner.getDurableLockingID());
    }
  }

  @Override
  public void writeCDOLockDelta(CDOLockDelta lockDelta) throws IOException
  {
    if (lockDelta == null)
    {
      writeByte(-1);
      return;
    }

    byte opcode = 0;
    CDOID id;
    CDOBranch branch;

    Object target = lockDelta.getTarget();
    if (target instanceof CDOIDAndBranch)
    {
      CDOIDAndBranch idAndBranch = (CDOIDAndBranch)target;
      id = idAndBranch.getID();
      branch = idAndBranch.getBranch();
      opcode += 10;
    }
    else if (target instanceof CDOID)
    {
      id = (CDOID)target;
      branch = null;
    }
    else
    {
      throw new AssertionError("Unexpected type: " + target.getClass().getSimpleName());
    }

    LockType type = lockDelta.getType();
    opcode += type.ordinal();

    writeByte(opcode);
    writeCDOID(id);

    if (opcode >= 10)
    {
      writeCDOBranch(branch);
    }

    writeCDOLockOwner(lockDelta.getOldOwner());
    writeCDOLockOwner(lockDelta.getNewOwner());
  }

  @Override
  public void writeCDOLockState(CDOLockState lockState) throws IOException
  {
    if (lockState == null)
    {
      writeByte(0);
      return;
    }

    Object o = lockState.getLockedObject();
    if (o instanceof CDOID)
    {
      writeByte(1);
      writeCDOID((CDOID)o);
    }
    else if (o instanceof CDOIDAndBranch)
    {
      writeByte(2);
      writeCDOIDAndBranch((CDOIDAndBranch)o);
    }
    else
    {
      throw new AssertionError("Unexpected type: " + o.getClass().getSimpleName());
    }

    Set<CDOLockOwner> readLockOwners = lockState.getReadLockOwners();
    writeXInt(readLockOwners.size());
    for (CDOLockOwner readLockOwner : readLockOwners)
    {
      writeCDOLockOwner(readLockOwner);
    }

    CDOLockOwner writeLockOwner = lockState.getWriteLockOwner();
    if (writeLockOwner != null)
    {
      writeBoolean(true);
      writeCDOLockOwner(writeLockOwner);
    }
    else
    {
      writeBoolean(false);
    }

    CDOLockOwner writeOptionOwner = lockState.getWriteOptionOwner();
    if (writeOptionOwner != null)
    {
      writeBoolean(true);
      writeCDOLockOwner(writeOptionOwner);
    }
    else
    {
      writeBoolean(false);
    }
  }

  @Override
  public void writeCDOLockType(LockType lockType) throws IOException
  {
    writeEnum(lockType);
  }

  @Override
  public void writeCDOID(CDOID id) throws IOException
  {
    CDOIDUtil.write(this, id);
  }

  @Override
  public void writeCDOIDReference(CDOIDReference idReference) throws IOException
  {
    idReference.write(this);
  }

  @Override
  public void writeCDOIDAndVersion(CDOIDAndVersion idAndVersion) throws IOException
  {
    writeCDOID(idAndVersion.getID());
    writeXInt(idAndVersion.getVersion());
  }

  @Override
  public void writeCDOIDAndBranch(CDOIDAndBranch idAndBranch) throws IOException
  {
    writeCDOID(idAndBranch.getID());
    writeCDOBranch(idAndBranch.getBranch());
  }

  @Override
  public void writeCDORevisionKey(CDORevisionKey revisionKey) throws IOException
  {
    if (revisionKey == null)
    {
      writeCDOID(CDOID.NULL);
    }
    else
    {
      writeCDOID(revisionKey.getID());
      writeCDOBranch(revisionKey.getBranch());
      writeXInt(revisionKey.getVersion());
    }
  }

  @Override
  public void writeCDORevision(CDORevision revision, int referenceChunk) throws IOException
  {
    writeCDORevision(revision, referenceChunk, null);
  }

  @Override
  public void writeCDORevision(CDORevision revision, int referenceChunk, CDOBranchPoint securityContext) throws IOException
  {
    if (revision != null)
    {
      writeBoolean(true);
      ((InternalCDORevision)revision).write(this, referenceChunk, securityContext);
    }
    else
    {
      writeBoolean(false);
    }
  }

  @Override
  public void writeCDORevisable(CDORevisable revisable) throws IOException
  {
    writeCDOBranch(revisable.getBranch());
    writeXInt(revisable.getVersion());
    writeXLong(revisable.getTimeStamp());
    writeXLong(revisable.getRevised());
  }

  @Override
  public void writeCDOList(EClass owner, EStructuralFeature feature, CDOList list, int referenceChunk) throws IOException
  {
    writeCDOList(this, owner, feature, list, referenceChunk, null);
  }

  @Override
  public void writeCDOFeatureValue(EStructuralFeature feature, Object value) throws IOException
  {
    CDOType type = CDOModelUtil.getType(feature);
    type.writeValue(this, value);
  }

  @Override
  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException
  {
    ((CDORevisionDeltaImpl)revisionDelta).write(this);
  }

  @Override
  public void writeCDOFeatureDelta(EClass owner, CDOFeatureDelta featureDelta) throws IOException
  {
    ((CDOFeatureDeltaImpl)featureDelta).write(this, owner);
  }

  @Override
  public void writeCDORevisionOrPrimitive(Object value) throws IOException
  {
    // Value conversions
    if (value == null)
    {
      value = CDOID.NULL;
    }
    else if (value instanceof EObject)
    {
      value = getIDProvider().provideCDOID(value);
    }
    else if (value instanceof CDORevision)
    {
      value = ((CDORevision)value).getID();
    }

    // Type determination
    CDOType type = null;
    if (value instanceof CDOID)
    {
      type = CDOType.OBJECT;
    }
    else if (value instanceof Throwable)
    {
      type = CDOType.EXCEPTION;
    }
    else
    {
      try
      {
        type = CDOModelUtil.getPrimitiveType(value.getClass());
      }
      catch (IllegalArgumentException ex)
      {
        Handler handler = CDOType.Handler.Registry.INSTANCE.getHandlerByValue(value);
        if (handler == null)
        {
          throw ex;
        }

        type = CDOType.HANDLER;
      }
    }

    writeCDOType(type);
    type.writeValue(this, value);
  }

  @Override
  public void writeCDORevisionOrPrimitiveOrClassifier(Object value) throws IOException
  {
    if (value instanceof EClassifier)
    {
      writeBoolean(true);
      writeCDOClassifierRef((EClass)value);
    }
    else
    {
      writeBoolean(false);
      writeCDORevisionOrPrimitive(value);
    }
  }

  @Override
  public CDOCommonSession getSession()
  {
    return null;
  }

  @Override
  public CDOPackageRegistry getPackageRegistry()
  {
    return null;
  }

  @Override
  public CDOIDProvider getIDProvider()
  {
    return null;
  }

  @Override
  public CDOPermissionProvider getPermissionProvider()
  {
    return CDORevision.PERMISSION_PROVIDER;
  }

  /**
   * @since 4.3
   */
  @Override
  public CDORevisionUnchunker getRevisionUnchunker()
  {
    return null;
  }

  /**
   * @since 4.6
   */
  protected boolean isXCompression()
  {
    return false;
  }

  protected StringIO getPackageURICompressor()
  {
    return StringIO.DIRECT;
  }

  /**
   * @since 4.26
   */
  public static void writeCDOList(CDODataOutput out, EClass owner, EStructuralFeature feature, CDOList list, int referenceChunk,
      UnaryOperator<CDOID> idConverter) throws IOException
  {
    // TODO Simon: Could most of this stuff be moved into the list?
    // (only if protected methods of this class don't need to become public)
    int size = list == null ? 0 : list.size();
    if (size > 0)
    {
      // Need to adjust the referenceChunk in case where we do not have enough value in the list.
      // Even if the referenceChunk is specified, a provider of data could have override that value.
      int sizeToLook = referenceChunk == CDORevision.UNCHUNKED ? size : Math.min(referenceChunk, size);
      for (int i = 0; i < sizeToLook; i++)
      {
        Object element = list.get(i, false);
        if (element == CDORevisionUtil.UNINITIALIZED)
        {
          referenceChunk = i;
          break;
        }
      }
    }

    if (referenceChunk != CDORevision.UNCHUNKED && referenceChunk < size)
    {
      // This happens only on server-side
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing feature {0}: size={1}, referenceChunk={2}", feature.getName(), size, referenceChunk); //$NON-NLS-1$
      }

      out.writeXInt(-size);
      out.writeXInt(referenceChunk);
      size = referenceChunk;
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing feature {0}: size={1}", feature.getName(), size); //$NON-NLS-1$
      }

      out.writeXInt(size);
    }

    if (feature instanceof EReference)
    {
      CDOIDProvider idProvider = out.getIDProvider();

      for (int j = 0; j < size; j++)
      {
        Object value = list.get(j, false);
        if (value != null)
        {
          if (idProvider != null)
          {
            value = idProvider.provideCDOID(value);
          }

          if (idConverter != null)
          {
            value = idConverter.apply((CDOID)value);
          }
        }

        if (TRACER.isEnabled())
        {
          TRACER.trace("    " + value); //$NON-NLS-1$
        }

        out.writeCDOFeatureValue(feature, value);
      }
    }
    else
    {
      for (int j = 0; j < size; j++)
      {
        Object value = list.get(j, false);

        if (TRACER.isEnabled())
        {
          TRACER.trace("    " + value); //$NON-NLS-1$
        }

        out.writeCDOFeatureValue(feature, value);
      }
    }
  }
}
