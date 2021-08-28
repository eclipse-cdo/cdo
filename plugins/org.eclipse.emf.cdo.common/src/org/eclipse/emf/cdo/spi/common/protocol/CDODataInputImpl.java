/*
 * Copyright (c) 2012-2017, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.common.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lob.CDOLobUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
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
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOClassNotFoundException;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockAreaImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockChangeInfoImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockStateImpl;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.CDOIDAndBranchImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOClearFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOContainerFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOUnsetFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList.ConfigurableEquality;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.2
 */
public abstract class CDODataInputImpl extends ExtendedDataInput.Delegating implements CDODataInput
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDODataInputImpl.class);

  public CDODataInputImpl(ExtendedDataInput delegate)
  {
    super(delegate);
  }

  /**
   * @since 4.6
   */
  @Override
  public int readXInt() throws IOException
  {
    if (isXCompression())
    {
      return readVarInt();
    }

    return readInt();
  }

  /**
   * @since 4.6
   */
  @Override
  public long readXLong() throws IOException
  {
    if (isXCompression())
    {
      return readVarLong();
    }

    return readLong();
  }

  @Override
  public CDOPackageUnit readCDOPackageUnit(ResourceSet resourceSet) throws IOException
  {
    InternalCDOPackageUnit packageUnit = (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
    packageUnit.read(this, resourceSet);
    return packageUnit;
  }

  @Override
  public CDOPackageUnit[] readCDOPackageUnits(ResourceSet resourceSet) throws IOException
  {
    int size = readXInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} package units", size); //$NON-NLS-1$
    }

    CDOPackageUnit[] packageUnits = new CDOPackageUnit[size];
    for (int i = 0; i < size; i++)
    {
      packageUnits[i] = readCDOPackageUnit(resourceSet);
    }

    return packageUnits;
  }

  @Override
  public CDOPackageUnit.Type readCDOPackageUnitType() throws IOException
  {
    return CDOPackageUnit.Type.values()[readByte()];
  }

  @Override
  public CDOPackageInfo readCDOPackageInfo() throws IOException
  {
    InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
    packageInfo.read(this);
    return packageInfo;
  }

  @Override
  public String readCDOPackageURI() throws IOException
  {
    return getPackageURICompressor().read(this);
  }

  @Override
  public CDOClassifierRef readCDOClassifierRef() throws IOException
  {
    return new CDOClassifierRef(this);
  }

  @Override
  public EClassifier readCDOClassifierRefAndResolve() throws IOException
  {
    CDOClassifierRef classifierRef = readCDOClassifierRef();
    EClassifier classifier = classifierRef.resolve(getPackageRegistry());
    if (classifier == null)
    {
      throw new CDOClassNotFoundException(classifierRef.getPackageURI(), classifierRef.getClassifierName());
    }

    return classifier;
  }

  @Override
  public CDOType readCDOType() throws IOException
  {
    byte typeID = readByte();
    return CDOModelUtil.getType(typeID);
  }

  @Override
  public CDOBranch readCDOBranch() throws IOException
  {
    int branchID = readXInt();
    CDOBranch branch = getBranchManager().getBranch(branchID);
    if (branch == null)
    {
      throw new IOException("Branch not found: " + branchID);
    }

    return branch;
  }

  @Override
  public CDOBranchPoint readCDOBranchPoint() throws IOException
  {
    CDOBranch branch = readCDOBranch();
    long timeStamp = readXLong();
    return branch.getPoint(timeStamp);
  }

  @Override
  public CDOBranchVersion readCDOBranchVersion() throws IOException
  {
    CDOBranch branch = readCDOBranch();
    int version = readXInt();
    return branch.getVersion(version);
  }

  @Override
  public CDOChangeSetData readCDOChangeSetData() throws IOException
  {
    int size1 = readXInt();
    List<CDOIDAndVersion> newObjects = new ArrayList<>(size1);
    for (int i = 0; i < size1; i++)
    {
      boolean revision = readBoolean();
      CDOIDAndVersion data = revision ? readCDORevision() : readCDOIDAndVersion();
      newObjects.add(data);
    }

    int size2 = readXInt();
    List<CDORevisionKey> changedObjects = new ArrayList<>(size2);
    for (int i = 0; i < size2; i++)
    {
      boolean delta = readBoolean();
      CDORevisionKey data = delta ? readCDORevisionDelta() : readCDORevisionKey();
      changedObjects.add(data);
    }

    int size3 = readXInt();
    List<CDOIDAndVersion> detachedObjects = new ArrayList<>(size3);
    for (int i = 0; i < size3; i++)
    {
      CDOID id = readCDOID();
      int version = readXInt();
      boolean isCDORevisionKey = readBoolean();
      CDOIDAndVersion data;
      if (isCDORevisionKey)
      {
        CDOBranch branch = readCDOBranch();
        data = CDORevisionUtil.createRevisionKey(id, branch, version);
      }
      else
      {
        data = new CDOIDAndVersionImpl(id, version);
      }

      detachedObjects.add(data);
    }

    return new CDOChangeSetDataImpl(newObjects, changedObjects, detachedObjects);
  }

  @Override
  public CDOCommitData readCDOCommitData() throws IOException
  {
    InternalCDOPackageRegistry packageRegistry = (InternalCDOPackageRegistry)getPackageRegistry();
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.setPackageRegistry(packageRegistry);

    int size = readXInt();
    List<CDOPackageUnit> newPackageUnits = new ArrayList<>(size);
    for (int i = 0; i < size; i++)
    {
      CDOPackageUnit data = readCDOPackageUnit(resourceSet);
      newPackageUnits.add(data);
      packageRegistry.putPackageUnit((InternalCDOPackageUnit)data);
    }

    CDOChangeSetData data = readCDOChangeSetData();
    return CDOCommitInfoUtil.createCommitData(newPackageUnits, data.getNewObjects(), data.getChangedObjects(), data.getDetachedObjects());
  }

  @Override
  public CDOCommitInfo readCDOCommitInfo() throws IOException
  {
    InternalCDOCommitInfoManager commitInfoManager = (InternalCDOCommitInfoManager)getCommitInfoManager();
    long timeStamp = readXLong();
    long previousTimeStamp = readXLong();

    if (readBoolean())
    {
      CDOBranch branch = readCDOBranch();
      String userID = readString();
      String comment = readString();
      CDOBranchPoint mergeSource = CDOBranchUtil.readBranchPointOrNull(this);
      CDOCommitData commitData = readCDOCommitData();

      return commitInfoManager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, mergeSource, commitData);
    }

    return new FailureCommitInfo(commitInfoManager, timeStamp, previousTimeStamp);
  }

  @Override
  public CDOLockChangeInfo readCDOLockChangeInfo() throws IOException
  {
    boolean isInvalidateAll = readBoolean();
    if (isInvalidateAll)
    {
      return CDOLockUtil.createLockChangeInfo();
    }

    CDOBranchPoint branchPoint = readCDOBranchPoint();
    CDOLockOwner lockOwner = readCDOLockOwner();
    Operation operation = readEnum(Operation.class);
    LockType lockType = readCDOLockType();

    int n = readXInt();
    CDOLockState[] lockStates = new CDOLockState[n];
    for (int i = 0; i < n; i++)
    {
      lockStates[i] = readCDOLockState();
    }

    return new CDOLockChangeInfoImpl(branchPoint, lockOwner, lockStates, operation, lockType);
  }

  @Override
  public LockArea readCDOLockArea() throws IOException
  {
    String durableLockingID = readString();
    CDOBranch branch = readCDOBranch();
    long timestamp = readXLong();
    String userID = readString();
    boolean readOnly = readBoolean();

    int nLockStates = readXInt();
    Map<CDOID, LockGrade> locks = CDOIDUtil.createMap();
    for (int i = 0; i < nLockStates; i++)
    {
      CDOID key = readCDOID();
      LockGrade value = readEnum(LockGrade.class);
      locks.put(key, value);
    }

    return new CDOLockAreaImpl(durableLockingID, userID, branch.getPoint(timestamp), readOnly, locks);
  }

  @Override
  public CDOLockOwner readCDOLockOwner() throws IOException
  {
    int session = readXInt();
    int view = readXInt();
    String lockAreaID = readString();
    return CDOLockUtil.createLockOwner(session, view, lockAreaID);
  }

  @Override
  public CDOLockState readCDOLockState() throws IOException
  {
    Object target;
    boolean sendingBranchWithID = readBoolean();
    if (!sendingBranchWithID)
    {
      target = readCDOID();
    }
    else
    {
      target = readCDOIDAndBranch();
    }

    InternalCDOLockState lockState = new CDOLockStateImpl(target);

    int nReadLockOwners = readXInt();
    for (int i = 0; i < nReadLockOwners; i++)
    {
      CDOLockOwner lockOwner = readCDOLockOwner();
      lockState.addReadLockOwner(lockOwner);
    }

    boolean hasWriteLock = readBoolean();
    if (hasWriteLock)
    {
      CDOLockOwner lockOwner = readCDOLockOwner();
      lockState.setWriteLockOwner(lockOwner);
    }

    boolean hasWriteOption = readBoolean();
    if (hasWriteOption)
    {
      CDOLockOwner lockOwner = readCDOLockOwner();
      lockState.setWriteOptionOwner(lockOwner);
    }

    return lockState;
  }

  @Override
  public LockType readCDOLockType() throws IOException
  {
    return readEnum(LockType.class);
  }

  @Override
  public CDOID readCDOID() throws IOException
  {
    return CDOIDUtil.read(this);
  }

  @Override
  public CDOIDReference readCDOIDReference() throws IOException
  {
    return new CDOIDReference(this);
  }

  @Override
  public CDOIDAndVersion readCDOIDAndVersion() throws IOException
  {
    CDOID id = readCDOID();
    int version = readXInt();
    return new CDOIDAndVersionImpl(id, version);
  }

  @Override
  public CDOIDAndBranch readCDOIDAndBranch() throws IOException
  {
    CDOID id = readCDOID();
    CDOBranch branch = readCDOBranch();
    return new CDOIDAndBranchImpl(id, branch);
  }

  @Override
  public CDORevisionKey readCDORevisionKey() throws IOException
  {
    CDOID id = readCDOID();
    CDOBranch branch = readCDOBranch();
    int version = readXInt();
    return CDORevisionUtil.createRevisionKey(id, branch, version);
  }

  @Override
  public CDORevision readCDORevision() throws IOException
  {
    return readCDORevision(true);
  }

  @Override
  public CDORevision readCDORevision(boolean freeze) throws IOException
  {
    boolean notNull = readBoolean();
    if (notNull)
    {
      InternalCDORevision revision = (InternalCDORevision)getRevisionFactory().createRevision(null);
      revision.read(this);

      if (freeze)
      {
        revision.freeze();
      }

      return revision;
    }

    return null;
  }

  @Override
  public CDORevisable readCDORevisable() throws IOException
  {
    CDOBranch branch = readCDOBranch();
    int version = readXInt();
    long timeStamp = readXLong();
    long revised = readXLong();
    return CDORevisionUtil.createRevisable(branch, version, timeStamp, revised);
  }

  @Override
  public CDOList readCDOList(EClass owner, EStructuralFeature feature) throws IOException
  {
    int referenceChunk;
    int size = readXInt();
    if (size < 0)
    {
      size = -size;
      referenceChunk = readXInt();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read feature {0}: size={1}, referenceChunk={2}", feature.getName(), size, referenceChunk); //$NON-NLS-1$
      }
    }
    else
    {
      referenceChunk = size;
      if (TRACER.isEnabled())
      {
        TRACER.format("Read feature {0}: size={1}", feature.getName(), size); //$NON-NLS-1$
      }
    }

    CDOType type = CDOModelUtil.getType(feature.getEType());

    InternalCDOList list = (InternalCDOList)getListFactory().createList(size, size, referenceChunk);
    if (feature instanceof EReference && list instanceof ConfigurableEquality)
    {
      ((ConfigurableEquality)list).setUseEquals(false);
    }

    for (int j = 0; j < referenceChunk; j++)
    {
      Object value = type.readValue(this);
      list.set(j, value);

      if (TRACER.isEnabled())
      {
        TRACER.trace("    " + value); //$NON-NLS-1$
      }
    }

    return list;
  }

  @Override
  public Object readCDOFeatureValue(EStructuralFeature feature) throws IOException
  {
    CDOType type = CDOModelUtil.getType(feature);

    Object value = type.readValue(this);
    if (value instanceof CDOLob<?>)
    {
      CDOLob<?> lob = (CDOLob<?>)value;
      CDOLobUtil.setStore(getLobStore(), lob);
    }

    return value;
  }

  @Override
  public CDORevisionDelta readCDORevisionDelta() throws IOException
  {
    return new CDORevisionDeltaImpl(this);
  }

  @Override
  public CDOFeatureDelta readCDOFeatureDelta(EClass owner) throws IOException
  {
    int typeOrdinal = readXInt();
    CDOFeatureDelta.Type type = CDOFeatureDelta.Type.values()[typeOrdinal];
    switch (type)
    {
    case ADD:
      return new CDOAddFeatureDeltaImpl(this, owner);

    case SET:
      return new CDOSetFeatureDeltaImpl(this, owner);

    case LIST:
      return new CDOListFeatureDeltaImpl(this, owner);

    case MOVE:
      return new CDOMoveFeatureDeltaImpl(this, owner);

    case CLEAR:
      return new CDOClearFeatureDeltaImpl(this, owner);

    case REMOVE:
      return new CDORemoveFeatureDeltaImpl(this, owner);

    case CONTAINER:
      return new CDOContainerFeatureDeltaImpl(this, owner);

    case UNSET:
      return new CDOUnsetFeatureDeltaImpl(this, owner);

    default:
      throw new IOException(MessageFormat.format(Messages.getString("CDODataInputImpl.5"), typeOrdinal)); //$NON-NLS-1$
    }
  }

  @Override
  public Object readCDORevisionOrPrimitive() throws IOException
  {
    CDOType type = readCDOType();
    return type.readValue(this);
  }

  @Override
  public Object readCDORevisionOrPrimitiveOrClassifier() throws IOException
  {
    boolean isClassifier = readBoolean();
    if (isClassifier)
    {
      return readCDOClassifierRefAndResolve();
    }

    return readCDORevisionOrPrimitive();
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

  protected abstract CDOBranchManager getBranchManager();

  protected abstract CDOCommitInfoManager getCommitInfoManager();

  protected abstract CDORevisionFactory getRevisionFactory();

  protected abstract CDOListFactory getListFactory();

  protected abstract CDOLobStore getLobStore();

  /**
   * A concrete subclass of {@link CDODataInputImpl}.
   *
   * @author Eike Stepper
   */
  public static final class Default extends CDODataInputImpl
  {
    public Default(ExtendedDataInput delegate)
    {
      super(delegate);
    }

    @Override
    protected CDORevisionFactory getRevisionFactory()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOPackageRegistry getPackageRegistry()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected CDOLobStore getLobStore()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected CDOListFactory getListFactory()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected CDOCommitInfoManager getCommitInfoManager()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected CDOBranchManager getBranchManager()
    {
      throw new UnsupportedOperationException();
    }
  }
}
