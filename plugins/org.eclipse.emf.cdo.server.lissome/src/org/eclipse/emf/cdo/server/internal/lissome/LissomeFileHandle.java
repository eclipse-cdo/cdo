/*
 * Copyright (c) 2012, 2013, 2015-2017, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermissionProvider;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionUnchunker;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.DataInputExtender;
import org.eclipse.net4j.util.io.DataInputOutputFile;
import org.eclipse.net4j.util.io.DataOutputExtender;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.eclipse.net4j.util.io.ExtendedIOUtil.ClassResolver;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LissomeFileHandle extends DataInputOutputFile implements CDODataInput, CDODataOutput, LissomeFile.RevisionProvider
{
  private static final boolean X_COMPRESSION = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.lissome.X_COMPRESSION");

  private LissomeFile file;

  private CDODataInput in;

  private CDODataOutput out;

  public LissomeFileHandle(LissomeFile file, String mode) throws FileNotFoundException
  {
    super(file, mode);
    this.file = file;
  }

  public LissomeFile getFile()
  {
    return file;
  }

  public LissomeStore getStore()
  {
    return file.getStore();
  }

  @Override
  public CDOCommonSession getSession()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOPackageRegistry getPackageRegistry()
  {
    return getStore().getRepository().getPackageRegistry();
  }

  public CDOBranchManager getBranchManager()
  {
    return getStore().getRepository().getBranchManager();
  }

  public CDOCommitInfoManager getCommitInfoManager()
  {
    return getStore().getRepository().getCommitInfoManager();
  }

  @Override
  public CDORevisionUnchunker getRevisionUnchunker()
  {
    return null;
  }

  public CDORevisionFactory getRevisionFactory()
  {
    return CDORevisionFactory.DEFAULT;
  }

  public CDOListFactory getListFactory()
  {
    return CDOListFactory.DEFAULT;
  }

  public CDOLobStore getLobStore()
  {
    return null;
  }

  @Override
  public CDOIDProvider getIDProvider()
  {
    return CDOIDProvider.NOOP;
  }

  @Override
  public CDOPermissionProvider getPermissionProvider()
  {
    return out().getPermissionProvider();
  }

  @Override
  public CDORevision getRevision(long pointer)
  {
    try
    {
      seek(pointer);
      return readCDORevision();
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  private CDODataInput in()
  {
    if (in == null)
    {
      in = createDataInput();
    }

    return in;
  }

  protected CDODataInput createDataInput()
  {
    ExtendedDataInput extendedDataInput = new DataInputExtender(this);
    return new CDODataInputImpl(extendedDataInput)
    {
      @Override
      public CDORevision readCDORevision(boolean freeze) throws IOException
      {
        boolean detached = readBoolean();
        if (detached)
        {
          int cid = readXInt();
          EClass eClass = (EClass)file.getStore().getMetaObject(cid);

          CDOID id = readCDOID();
          CDOBranch branch = readCDOBranch();
          int version = readXInt();
          long timeStamp = readXLong();
          long revised = readXLong();

          return new DetachedCDORevision(eClass, id, branch, version, timeStamp, revised);
        }

        return super.readCDORevision(freeze);
      }

      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return LissomeFileHandle.this.getPackageRegistry();
      }

      @Override
      protected CDOBranchManager getBranchManager()
      {
        return LissomeFileHandle.this.getBranchManager();
      }

      @Override
      protected CDOCommitInfoManager getCommitInfoManager()
      {
        return LissomeFileHandle.this.getCommitInfoManager();
      }

      @Override
      protected CDORevisionFactory getRevisionFactory()
      {
        return LissomeFileHandle.this.getRevisionFactory();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return LissomeFileHandle.this.getListFactory();
      }

      @Override
      protected CDOLobStore getLobStore()
      {
        return LissomeFileHandle.this.getLobStore();
      }
    };
  }

  @Override
  public int readVarInt() throws IOException
  {
    return ExtendedIOUtil.readVarInt(in());
  }

  @Override
  public long readVarLong() throws IOException
  {
    return ExtendedIOUtil.readVarLong(in());
  }

  @Override
  public int readXInt() throws IOException
  {
    if (isXCompression())
    {
      return readVarInt();
    }

    return readInt();
  }

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
  public byte[] readByteArray() throws IOException
  {
    return in().readByteArray();
  }

  @Override
  public Object readObject() throws IOException
  {
    return in().readObject();
  }

  @Override
  public Object readObject(ClassLoader classLoader) throws IOException
  {
    return in().readObject(classLoader);
  }

  @Override
  public Object readObject(ClassResolver classResolver) throws IOException
  {
    return in().readObject(classResolver);
  }

  @Override
  public String readString() throws IOException
  {
    return in().readString();
  }

  @Override
  public <T extends Enum<?>> T readEnum(Class<T> type) throws IOException
  {
    return in().readEnum(type);
  }

  @Override
  public Throwable readException() throws IOException
  {
    return in().readException();
  }

  @Override
  public CDOPackageUnit readCDOPackageUnit(ResourceSet resourceSet) throws IOException
  {
    return in().readCDOPackageUnit(resourceSet);
  }

  @Override
  public CDOPackageUnit[] readCDOPackageUnits(ResourceSet resourceSet) throws IOException
  {
    return in().readCDOPackageUnits(resourceSet);
  }

  @Override
  public Type readCDOPackageUnitType() throws IOException
  {
    return in().readCDOPackageUnitType();
  }

  @Override
  public CDOPackageInfo readCDOPackageInfo() throws IOException
  {
    return in().readCDOPackageInfo();
  }

  @Override
  public CDOClassifierRef readCDOClassifierRef() throws IOException
  {
    return in().readCDOClassifierRef();
  }

  @Override
  public EClassifier readCDOClassifierRefAndResolve() throws IOException
  {
    return in().readCDOClassifierRefAndResolve();
  }

  @Override
  public String readCDOPackageURI() throws IOException
  {
    return in().readCDOPackageURI();
  }

  @Override
  public CDOType readCDOType() throws IOException
  {
    return in().readCDOType();
  }

  @Override
  public CDOBranch readCDOBranch() throws IOException
  {
    return in().readCDOBranch();
  }

  @Override
  public CDOBranchPoint readCDOBranchPoint() throws IOException
  {
    return in().readCDOBranchPoint();
  }

  @Override
  public CDOBranchVersion readCDOBranchVersion() throws IOException
  {
    return in().readCDOBranchVersion();
  }

  @Override
  public CDOChangeSetData readCDOChangeSetData() throws IOException
  {
    return in().readCDOChangeSetData();
  }

  @Override
  public CDOCommitData readCDOCommitData() throws IOException
  {
    return in().readCDOCommitData();
  }

  @Override
  public CDOCommitInfo readCDOCommitInfo() throws IOException
  {
    return in().readCDOCommitInfo();
  }

  @Override
  public CDOID readCDOID() throws IOException
  {
    return in().readCDOID();
  }

  @Override
  public CDOIDReference readCDOIDReference() throws IOException
  {
    return in().readCDOIDReference();
  }

  @Override
  public CDOIDAndVersion readCDOIDAndVersion() throws IOException
  {
    return in().readCDOIDAndVersion();
  }

  @Override
  public CDOIDAndBranch readCDOIDAndBranch() throws IOException
  {
    return in().readCDOIDAndBranch();
  }

  @Override
  public CDORevisionKey readCDORevisionKey() throws IOException
  {
    return in().readCDORevisionKey();
  }

  @Override
  public CDORevision readCDORevision() throws IOException
  {
    return in().readCDORevision();
  }

  @Override
  public CDORevision readCDORevision(boolean freeze) throws IOException
  {
    return in().readCDORevision(freeze);
  }

  @Override
  public CDORevisable readCDORevisable() throws IOException
  {
    return in().readCDORevisable();
  }

  @Override
  public CDOList readCDOList(EClass owner, EStructuralFeature feature) throws IOException
  {
    return in().readCDOList(owner, feature);
  }

  @Override
  public Object readCDOFeatureValue(EStructuralFeature feature) throws IOException
  {
    return in().readCDOFeatureValue(feature);
  }

  @Override
  public CDORevisionDelta readCDORevisionDelta() throws IOException
  {
    return in().readCDORevisionDelta();
  }

  @Override
  public CDOFeatureDelta readCDOFeatureDelta(EClass owner) throws IOException
  {
    return in().readCDOFeatureDelta(owner);
  }

  @Override
  public Object readCDORevisionOrPrimitive() throws IOException
  {
    return in().readCDORevisionOrPrimitive();
  }

  @Override
  public Object readCDORevisionOrPrimitiveOrClassifier() throws IOException
  {
    return in().readCDORevisionOrPrimitiveOrClassifier();
  }

  @Override
  public LockType readCDOLockType() throws IOException
  {
    return in().readCDOLockType();
  }

  @Override
  public CDOLockChangeInfo readCDOLockChangeInfo() throws IOException
  {
    return in().readCDOLockChangeInfo();
  }

  @Override
  public CDOLockOwner readCDOLockOwner() throws IOException
  {
    return in().readCDOLockOwner();
  }

  @Override
  public CDOLockDelta readCDOLockDelta() throws IOException
  {
    return in().readCDOLockDelta();
  }

  @Override
  public CDOLockState readCDOLockState() throws IOException
  {
    return in().readCDOLockState();
  }

  @Override
  public LockArea readCDOLockArea() throws IOException
  {
    return in().readCDOLockArea();
  }

  private CDODataOutput out()
  {
    if (out == null)
    {
      out = createDataOutput();
    }

    return out;
  }

  protected CDODataOutput createDataOutput()
  {
    ExtendedDataOutput extendedDataOutput = new DataOutputExtender(this);
    return new CDODataOutputImpl(extendedDataOutput)
    {
      @Override
      public void writeCDORevision(CDORevision revision, int referenceChunk, CDOBranchPoint securityContext) throws IOException
      {
        boolean detached = revision instanceof DetachedCDORevision;
        writeBoolean(detached);
        if (detached)
        {
          int cid = file.getStore().getMetaID(revision.getEClass());
          writeXInt(cid);

          writeCDOID(revision.getID());
          writeCDOBranch(revision.getBranch());
          writeXInt(revision.getVersion());
          writeXLong(revision.getTimeStamp());
          writeXLong(revision.getRevised());
        }
        else
        {
          super.writeCDORevision(revision, referenceChunk, securityContext);
        }
      }

      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return LissomeFileHandle.this.getPackageRegistry();
      }

      @Override
      public CDOIDProvider getIDProvider()
      {
        return LissomeFileHandle.this.getIDProvider();
      }
    };
  }

  @Override
  public void writeVarInt(int v) throws IOException
  {
    ExtendedIOUtil.writeVarInt(out(), v);
  }

  @Override
  public void writeVarLong(long v) throws IOException
  {
    ExtendedIOUtil.writeVarLong(out(), v);
  }

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
  public void writeByteArray(byte[] b) throws IOException
  {
    out().writeByteArray(b);
  }

  @Override
  public void writeObject(Object object) throws IOException
  {
    out().writeObject(object);
  }

  @Override
  public void writeString(String str) throws IOException
  {
    out().writeString(str);
  }

  @Override
  public void writeEnum(Enum<?> literal) throws IOException
  {
    out().writeEnum(literal);
  }

  @Override
  public void writeException(Throwable t) throws IOException
  {
    out().writeException(t);
  }

  @Override
  public void writeCDOPackageUnit(CDOPackageUnit packageUnit, boolean withPackages) throws IOException
  {
    out().writeCDOPackageUnit(packageUnit, withPackages);
  }

  @Override
  public void writeCDOPackageUnits(CDOPackageUnit... packageUnit) throws IOException
  {
    out().writeCDOPackageUnits(packageUnit);
  }

  @Override
  public void writeCDOPackageUnitType(Type type) throws IOException
  {
    out().writeCDOPackageUnitType(type);
  }

  @Override
  public void writeCDOPackageInfo(CDOPackageInfo packageInfo) throws IOException
  {
    out().writeCDOPackageInfo(packageInfo);
  }

  @Override
  public void writeCDOClassifierRef(CDOClassifierRef eClassifierRef) throws IOException
  {
    out().writeCDOClassifierRef(eClassifierRef);
  }

  @Override
  public void writeCDOClassifierRef(EClassifier eClassifier) throws IOException
  {
    out().writeCDOClassifierRef(eClassifier);
  }

  @Override
  public void writeCDOPackageURI(String uri) throws IOException
  {
    out().writeCDOPackageURI(uri);
  }

  @Override
  public void writeCDOType(CDOType cdoType) throws IOException
  {
    out().writeCDOType(cdoType);
  }

  @Override
  public void writeCDOBranch(CDOBranch branch) throws IOException
  {
    out().writeCDOBranch(branch);
  }

  @Override
  public void writeCDOBranchPoint(CDOBranchPoint branchPoint) throws IOException
  {
    out().writeCDOBranchPoint(branchPoint);
  }

  @Override
  public void writeCDOBranchVersion(CDOBranchVersion branchVersion) throws IOException
  {
    out().writeCDOBranchVersion(branchVersion);
  }

  @Override
  public void writeCDOChangeSetData(CDOChangeSetData changeSetData) throws IOException
  {
    out().writeCDOChangeSetData(changeSetData);
  }

  @Override
  public void writeCDOCommitData(CDOCommitData commitData) throws IOException
  {
    out().writeCDOCommitData(commitData);
  }

  @Override
  public void writeCDOCommitInfo(CDOCommitInfo commitInfo) throws IOException
  {
    out().writeCDOCommitInfo(commitInfo);
  }

  @Override
  public void writeCDOID(CDOID id) throws IOException
  {
    out().writeCDOID(id);
  }

  @Override
  public void writeCDOIDReference(CDOIDReference idReference) throws IOException
  {
    out().writeCDOIDReference(idReference);
  }

  @Override
  public void writeCDOIDAndVersion(CDOIDAndVersion idAndVersion) throws IOException
  {
    out().writeCDOIDAndVersion(idAndVersion);
  }

  @Override
  public void writeCDOIDAndBranch(CDOIDAndBranch idAndBranch) throws IOException
  {
    out().writeCDOIDAndBranch(idAndBranch);
  }

  @Override
  public void writeCDORevisionKey(CDORevisionKey revisionKey) throws IOException
  {
    out().writeCDORevisionKey(revisionKey);
  }

  @Override
  public void writeCDORevision(CDORevision revision, int referenceChunk) throws IOException
  {
    out().writeCDORevision(revision, referenceChunk);
  }

  @Override
  public void writeCDORevision(CDORevision revision, int referenceChunk, CDOBranchPoint securityContext) throws IOException
  {
    out().writeCDORevision(revision, referenceChunk, securityContext);
  }

  @Override
  public void writeCDORevisable(CDORevisable revisable) throws IOException
  {
    out().writeCDORevisable(revisable);
  }

  @Override
  public void writeCDOList(EClass owner, EStructuralFeature feature, CDOList list, int referenceChunk) throws IOException
  {
    out().writeCDOList(owner, feature, list, referenceChunk);
  }

  @Override
  public void writeCDOFeatureValue(EStructuralFeature feature, Object value) throws IOException
  {
    out().writeCDOFeatureValue(feature, value);
  }

  @Override
  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException
  {
    out().writeCDORevisionDelta(revisionDelta);
  }

  @Override
  public void writeCDOFeatureDelta(EClass owner, CDOFeatureDelta featureDelta) throws IOException
  {
    out().writeCDOFeatureDelta(owner, featureDelta);
  }

  @Override
  public void writeCDORevisionOrPrimitive(Object value) throws IOException
  {
    out().writeCDORevisionOrPrimitive(value);
  }

  @Override
  public void writeCDORevisionOrPrimitiveOrClassifier(Object value) throws IOException
  {
    out().writeCDORevisionOrPrimitiveOrClassifier(value);
  }

  @Override
  public void writeCDOLockType(LockType lockType) throws IOException
  {
    out().writeCDOLockType(lockType);
  }

  @Override
  public void writeCDOLockChangeInfo(CDOLockChangeInfo lockChangeInfo) throws IOException
  {
    out().writeCDOLockChangeInfo(lockChangeInfo);
  }

  @Override
  public void writeCDOLockChangeInfo(CDOLockChangeInfo lockChangeInfo, Set<CDOID> filter) throws IOException
  {
    out().writeCDOLockChangeInfo(lockChangeInfo, filter);
  }

  @Override
  public void writeCDOLockState(CDOLockState lockState) throws IOException
  {
    out().writeCDOLockState(lockState);
  }

  @Override
  public void writeCDOLockDelta(CDOLockDelta lockDelta) throws IOException
  {
    out().writeCDOLockDelta(lockDelta);
  }

  @Override
  public void writeCDOLockOwner(CDOLockOwner lockOwner) throws IOException
  {
    out().writeCDOLockOwner(lockOwner);
  }

  @Override
  public void writeCDOLockArea(LockArea lockArea) throws IOException
  {
    out().writeCDOLockArea(lockArea);
  }

  public int write(long startPointer, LissomeFileOperation operation)
  {
    try
    {
      seek(startPointer);
      operation.execute(this);
      flush();

      long endPointer = getFilePointer();
      return (int)(endPointer - startPointer);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public long append(LissomeFileOperation operation)
  {
    try
    {
      long startPointer = length();
      write(startPointer, operation);
      return startPointer;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  protected boolean isXCompression()
  {
    return X_COMPRESSION;
  }
}
