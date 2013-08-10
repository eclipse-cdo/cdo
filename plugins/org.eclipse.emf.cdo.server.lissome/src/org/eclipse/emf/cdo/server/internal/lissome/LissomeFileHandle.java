/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome;

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
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionUnchunker;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.DataInputExtender;
import org.eclipse.net4j.util.io.DataInputOutputFile;
import org.eclipse.net4j.util.io.DataOutputExtender;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.ExtendedIOUtil.ClassResolver;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LissomeFileHandle extends DataInputOutputFile implements CDODataInput, CDODataOutput,
    LissomeFile.RevisionProvider
{
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

  public CDOIDProvider getIDProvider()
  {
    return CDOIDProvider.NOOP;
  }

  public CDOPermissionProvider getPermissionProvider()
  {
    return out().getPermissionProvider();
  }

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
          int cid = readInt();
          EClass eClass = (EClass)file.getStore().getMetaObject(cid);

          CDOID id = readCDOID();
          CDOBranch branch = readCDOBranch();
          int version = readInt();
          long timeStamp = readLong();
          long revised = readLong();

          return new DetachedCDORevision(eClass, id, branch, version, timeStamp, revised);
        }

        return super.readCDORevision(freeze);
      }

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

  public byte[] readByteArray() throws IOException
  {
    return in().readByteArray();
  }

  public Object readObject() throws IOException
  {
    return in().readObject();
  }

  public Object readObject(ClassLoader classLoader) throws IOException
  {
    return in().readObject(classLoader);
  }

  public Object readObject(ClassResolver classResolver) throws IOException
  {
    return in().readObject(classResolver);
  }

  public String readString() throws IOException
  {
    return in().readString();
  }

  public <T extends Enum<?>> T readEnum(Class<T> type) throws IOException
  {
    return in().readEnum(type);
  }

  public CDOPackageUnit readCDOPackageUnit(ResourceSet resourceSet) throws IOException
  {
    return in().readCDOPackageUnit(resourceSet);
  }

  public CDOPackageUnit[] readCDOPackageUnits(ResourceSet resourceSet) throws IOException
  {
    return in().readCDOPackageUnits(resourceSet);
  }

  public Type readCDOPackageUnitType() throws IOException
  {
    return in().readCDOPackageUnitType();
  }

  public CDOPackageInfo readCDOPackageInfo() throws IOException
  {
    return in().readCDOPackageInfo();
  }

  public CDOClassifierRef readCDOClassifierRef() throws IOException
  {
    return in().readCDOClassifierRef();
  }

  public EClassifier readCDOClassifierRefAndResolve() throws IOException
  {
    return in().readCDOClassifierRefAndResolve();
  }

  public String readCDOPackageURI() throws IOException
  {
    return in().readCDOPackageURI();
  }

  public CDOType readCDOType() throws IOException
  {
    return in().readCDOType();
  }

  public CDOBranch readCDOBranch() throws IOException
  {
    return in().readCDOBranch();
  }

  public CDOBranchPoint readCDOBranchPoint() throws IOException
  {
    return in().readCDOBranchPoint();
  }

  public CDOBranchVersion readCDOBranchVersion() throws IOException
  {
    return in().readCDOBranchVersion();
  }

  public CDOChangeSetData readCDOChangeSetData() throws IOException
  {
    return in().readCDOChangeSetData();
  }

  public CDOCommitData readCDOCommitData() throws IOException
  {
    return in().readCDOCommitData();
  }

  public CDOCommitInfo readCDOCommitInfo() throws IOException
  {
    return in().readCDOCommitInfo();
  }

  public CDOID readCDOID() throws IOException
  {
    return in().readCDOID();
  }

  public CDOIDReference readCDOIDReference() throws IOException
  {
    return in().readCDOIDReference();
  }

  public CDOIDAndVersion readCDOIDAndVersion() throws IOException
  {
    return in().readCDOIDAndVersion();
  }

  public CDOIDAndBranch readCDOIDAndBranch() throws IOException
  {
    return in().readCDOIDAndBranch();
  }

  public CDORevisionKey readCDORevisionKey() throws IOException
  {
    return in().readCDORevisionKey();
  }

  public CDORevision readCDORevision() throws IOException
  {
    return in().readCDORevision();
  }

  public CDORevision readCDORevision(boolean freeze) throws IOException
  {
    return in().readCDORevision(freeze);
  }

  public CDORevisable readCDORevisable() throws IOException
  {
    return in().readCDORevisable();
  }

  public CDOList readCDOList(EClass owner, EStructuralFeature feature) throws IOException
  {
    return in().readCDOList(owner, feature);
  }

  public Object readCDOFeatureValue(EStructuralFeature feature) throws IOException
  {
    return in().readCDOFeatureValue(feature);
  }

  public CDORevisionDelta readCDORevisionDelta() throws IOException
  {
    return in().readCDORevisionDelta();
  }

  public CDOFeatureDelta readCDOFeatureDelta(EClass owner) throws IOException
  {
    return in().readCDOFeatureDelta(owner);
  }

  public Object readCDORevisionOrPrimitive() throws IOException
  {
    return in().readCDORevisionOrPrimitive();
  }

  public Object readCDORevisionOrPrimitiveOrClassifier() throws IOException
  {
    return in().readCDORevisionOrPrimitiveOrClassifier();
  }

  public LockType readCDOLockType() throws IOException
  {
    return in().readCDOLockType();
  }

  public CDOLockChangeInfo readCDOLockChangeInfo() throws IOException
  {
    return in().readCDOLockChangeInfo();
  }

  public CDOLockOwner readCDOLockOwner() throws IOException
  {
    return in().readCDOLockOwner();
  }

  public CDOLockState readCDOLockState() throws IOException
  {
    return in().readCDOLockState();
  }

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
      public void writeCDORevision(CDORevision revision, int referenceChunk, CDOBranchPoint securityContext)
          throws IOException
      {
        boolean detached = revision instanceof DetachedCDORevision;
        writeBoolean(detached);
        if (detached)
        {
          int cid = file.getStore().getMetaID(revision.getEClass());
          writeInt(cid);

          writeCDOID(revision.getID());
          writeCDOBranch(revision.getBranch());
          writeInt(revision.getVersion());
          writeLong(revision.getTimeStamp());
          writeLong(revision.getRevised());
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

  public void writeByteArray(byte[] b) throws IOException
  {
    out().writeByteArray(b);
  }

  public void writeObject(Object object) throws IOException
  {
    out().writeObject(object);
  }

  public void writeString(String str) throws IOException
  {
    out().writeString(str);
  }

  public void writeEnum(Enum<?> literal) throws IOException
  {
    out().writeEnum(literal);
  }

  public void writeCDOPackageUnit(CDOPackageUnit packageUnit, boolean withPackages) throws IOException
  {
    out().writeCDOPackageUnit(packageUnit, withPackages);
  }

  public void writeCDOPackageUnits(CDOPackageUnit... packageUnit) throws IOException
  {
    out().writeCDOPackageUnits(packageUnit);
  }

  public void writeCDOPackageUnitType(Type type) throws IOException
  {
    out().writeCDOPackageUnitType(type);
  }

  public void writeCDOPackageInfo(CDOPackageInfo packageInfo) throws IOException
  {
    out().writeCDOPackageInfo(packageInfo);
  }

  public void writeCDOClassifierRef(CDOClassifierRef eClassifierRef) throws IOException
  {
    out().writeCDOClassifierRef(eClassifierRef);
  }

  public void writeCDOClassifierRef(EClassifier eClassifier) throws IOException
  {
    out().writeCDOClassifierRef(eClassifier);
  }

  public void writeCDOPackageURI(String uri) throws IOException
  {
    out().writeCDOPackageURI(uri);
  }

  public void writeCDOType(CDOType cdoType) throws IOException
  {
    out().writeCDOType(cdoType);
  }

  public void writeCDOBranch(CDOBranch branch) throws IOException
  {
    out().writeCDOBranch(branch);
  }

  public void writeCDOBranchPoint(CDOBranchPoint branchPoint) throws IOException
  {
    out().writeCDOBranchPoint(branchPoint);
  }

  public void writeCDOBranchVersion(CDOBranchVersion branchVersion) throws IOException
  {
    out().writeCDOBranchVersion(branchVersion);
  }

  public void writeCDOChangeSetData(CDOChangeSetData changeSetData) throws IOException
  {
    out().writeCDOChangeSetData(changeSetData);
  }

  public void writeCDOCommitData(CDOCommitData commitData) throws IOException
  {
    out().writeCDOCommitData(commitData);
  }

  public void writeCDOCommitInfo(CDOCommitInfo commitInfo) throws IOException
  {
    out().writeCDOCommitInfo(commitInfo);
  }

  public void writeCDOID(CDOID id) throws IOException
  {
    out().writeCDOID(id);
  }

  public void writeCDOIDReference(CDOIDReference idReference) throws IOException
  {
    out().writeCDOIDReference(idReference);
  }

  public void writeCDOIDAndVersion(CDOIDAndVersion idAndVersion) throws IOException
  {
    out().writeCDOIDAndVersion(idAndVersion);
  }

  public void writeCDOIDAndBranch(CDOIDAndBranch idAndBranch) throws IOException
  {
    out().writeCDOIDAndBranch(idAndBranch);
  }

  public void writeCDORevisionKey(CDORevisionKey revisionKey) throws IOException
  {
    out().writeCDORevisionKey(revisionKey);
  }

  public void writeCDORevision(CDORevision revision, int referenceChunk) throws IOException
  {
    out().writeCDORevision(revision, referenceChunk);
  }

  public void writeCDORevision(CDORevision revision, int referenceChunk, CDOBranchPoint securityContext)
      throws IOException
  {
    out().writeCDORevision(revision, referenceChunk, securityContext);
  }

  public void writeCDORevisable(CDORevisable revisable) throws IOException
  {
    out().writeCDORevisable(revisable);
  }

  public void writeCDOList(EClass owner, EStructuralFeature feature, CDOList list, int referenceChunk)
      throws IOException
  {
    out().writeCDOList(owner, feature, list, referenceChunk);
  }

  public void writeCDOFeatureValue(EStructuralFeature feature, Object value) throws IOException
  {
    out().writeCDOFeatureValue(feature, value);
  }

  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException
  {
    out().writeCDORevisionDelta(revisionDelta);
  }

  public void writeCDOFeatureDelta(EClass owner, CDOFeatureDelta featureDelta) throws IOException
  {
    out().writeCDOFeatureDelta(owner, featureDelta);
  }

  public void writeCDORevisionOrPrimitive(Object value) throws IOException
  {
    out().writeCDORevisionOrPrimitive(value);
  }

  public void writeCDORevisionOrPrimitiveOrClassifier(Object value) throws IOException
  {
    out().writeCDORevisionOrPrimitiveOrClassifier(value);
  }

  public void writeCDOLockType(LockType lockType) throws IOException
  {
    out().writeCDOLockType(lockType);
  }

  public void writeCDOLockChangeInfo(CDOLockChangeInfo lockChangeInfo) throws IOException
  {
    out().writeCDOLockChangeInfo(lockChangeInfo);
  }

  public void writeCDOLockState(CDOLockState lockState) throws IOException
  {
    out().writeCDOLockState(lockState);
  }

  public void writeCDOLockOwner(CDOLockOwner lockOwner) throws IOException
  {
    out().writeCDOLockOwner(lockOwner);
  }

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
}
