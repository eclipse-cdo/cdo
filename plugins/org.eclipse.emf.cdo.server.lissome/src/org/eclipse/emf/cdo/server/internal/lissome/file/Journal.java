/*
 * Copyright (c) 2012, 2016, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome.file;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeFile;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeFileHandle;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeFileOperation;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeStore;
import org.eclipse.emf.cdo.server.internal.lissome.bundle.OM;
import org.eclipse.emf.cdo.server.internal.lissome.optimizer.CommitTransactionTask;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Journal extends LissomeFile
{
  public static final String EXTENSION = "journal";

  public static final byte PACKAGE_UNITS_BLOCK = 1;

  public static final byte COMMIT_TRANSACTION_BLOCK = 2;

  public static final byte CREATE_BRANCH_BLOCK = 3;

  private static final ContextTracer TRACER = new ContextTracer(OM.JOURNAL, Journal.class);

  private static final long serialVersionUID = 1L;

  private static final boolean ZIP_PACKAGE_BYTES = true;

  private Map<String, Long> ePackagePointers = new HashMap<>();

  private long commitPointer;

  private long newCommitPointer;

  private long packageUnitPointer;

  private long newPackageUnitPointer;

  private CommitTransactionTask commitTransactionTask;

  private final LissomeFileHandle writer;

  public Journal(LissomeStore store) throws FileNotFoundException
  {
    super(store, store.getRepository().getName() + "." + EXTENSION);
    writer = openWriter();
  }

  @Override
  protected LissomeFileHandle openHandle(String mode)
  {
    try
    {
      return new LissomeFileHandle(this, mode)
      {
        @Override
        public CDORevision getRevision(long pointer)
        {
          return super.getRevision(-pointer);
        }
      };
    }
    catch (FileNotFoundException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public void firstStart()
  {
    try
    {
      writer.seek(0);
      writer.writeXLong(commitPointer);
      writer.writeXLong(packageUnitPointer);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      cleanUp();
    }
  }

  public void reStart()
  {
    try
    {
      writer.seek(0);
      commitPointer = writer.readXLong();
      packageUnitPointer = writer.readXLong();
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      cleanUp();
    }
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    LissomeFileHandle reader = openReader();

    try
    {
      List<InternalCDOPackageUnit> result = new ArrayList<>();
      InternalCDOPackageRegistry packageRegistry = getStore().getRepository().getPackageRegistry();

      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.setPackageRegistry(packageRegistry);

      long filePointer = packageUnitPointer;
      while (filePointer != 0)
      {
        reader.seek(filePointer);
        filePointer = reader.readXLong();

        int size = reader.readXInt();
        for (int i = 0; i < size; i++)
        {
          InternalCDOPackageUnit packageUnit = (InternalCDOPackageUnit)reader.readCDOPackageUnit(resourceSet);
          packageUnit.setPackageRegistry(packageRegistry);
          result.add(packageUnit);

          long ePackagePointer = reader.readXLong();
          ePackagePointers.put(packageUnit.getID(), ePackagePointer);

          EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
          mapPackage(ePackage, reader);
        }
      }

      return result;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.close(reader);
    }
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    LissomeFileHandle reader = openReader();

    try
    {
      long ePackagePointer = ePackagePointers.get(packageUnit.getID());
      reader.seek(ePackagePointer);
      byte[] bytes = reader.readByteArray();

      EPackage ePackage = createEPackage(packageUnit, bytes);
      return EMFUtil.getAllPackages(ePackage);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.close(reader);
    }
  }

  protected void checkMetaObject(ENamedElement element, String name) throws IOException
  {
    if (!element.getName().equals(name))
    {
      throw new IOException("Name '" + name + "' expected: " + element);
    }
  }

  protected void checkListSize(EList<?> list, int size) throws IOException
  {
    if (list.size() != size)
    {
      throw new IOException("Size " + size + " expected: " + list);
    }
  }

  protected void mapPackage(EPackage ePackage, LissomeFileHandle reader) throws IOException
  {
    LissomeStore store = getStore();

    checkMetaObject(ePackage, reader.readString());
    store.mapMetaObject(ePackage, reader.readXInt());

    EList<EClassifier> eClassifiers = ePackage.getEClassifiers();
    checkListSize(eClassifiers, reader.readXInt());

    for (EClassifier eClassifier : eClassifiers)
    {
      checkMetaObject(eClassifier, reader.readString());
      store.mapMetaObject(eClassifier, reader.readXInt());

      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;

        EList<EStructuralFeature> eStructuralFeatures = eClass.getEStructuralFeatures();
        checkListSize(eStructuralFeatures, reader.readXInt());

        for (EStructuralFeature eStructuralFeature : eStructuralFeatures)
        {
          checkMetaObject(eStructuralFeature, reader.readString());
          store.mapMetaObject(eStructuralFeature, reader.readXInt());
        }
      }
    }

    EList<EPackage> eSubpackages = ePackage.getESubpackages();
    checkListSize(eSubpackages, reader.readXInt());

    for (EPackage eSubpackage : eSubpackages)
    {
      mapPackage(eSubpackage);
    }
  }

  protected void mapPackage(EPackage ePackage) throws IOException
  {
    LissomeStore store = getStore();

    writer.writeString(ePackage.getName());
    writer.writeXInt(store.mapMetaObject(ePackage));

    EList<EClassifier> eClassifiers = ePackage.getEClassifiers();
    writer.writeXInt(eClassifiers.size());

    for (EClassifier eClassifier : eClassifiers)
    {
      writer.writeString(eClassifier.getName());
      writer.writeXInt(store.mapMetaObject(eClassifier));

      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;

        EList<EStructuralFeature> eStructuralFeatures = eClass.getEStructuralFeatures();
        writer.writeXInt(eStructuralFeatures.size());

        for (EStructuralFeature eStructuralFeature : eStructuralFeatures)
        {
          writer.writeString(eStructuralFeature.getName());
          writer.writeXInt(store.mapMetaObject(eStructuralFeature));
        }
      }
    }

    EList<EPackage> eSubpackages = ePackage.getESubpackages();
    writer.writeXInt(eSubpackages.size());

    for (EPackage eSubpackage : eSubpackages)
    {
      mapPackage(eSubpackage);
    }
  }

  private EPackage createEPackage(InternalCDOPackageUnit packageUnit, byte[] bytes)
  {
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(getStore().getRepository().getPackageRegistry());
    return EMFUtil.createEPackage(packageUnit.getID(), bytes, ZIP_PACKAGE_BYTES, resourceSet, false);
  }

  private byte[] getEPackageBytes(InternalCDOPackageUnit packageUnit)
  {
    EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
    return EMFUtil.getEPackageBytes(ePackage, ZIP_PACKAGE_BYTES, getStore().getRepository().getPackageRegistry());
  }

  public void writePackageUnits(final InternalCDOPackageUnit[] packageUnits, final OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("writePackageUnits: {0}", Arrays.asList(packageUnits)); //$NON-NLS-1$
    }

    monitor.begin(packageUnits.length);

    try
    {
      writer.append(new LissomeFileOperation()
      {
        @Override
        public void execute(LissomeFileHandle writer) throws IOException
        {
          writer.writeByte(PACKAGE_UNITS_BLOCK);
          writePackageUnits(writer, packageUnits, monitor);
        }
      });
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writePackageUnits(LissomeFileHandle writer, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor) throws IOException
  {
    writer.writeXInt(packageUnits.length);
    for (InternalCDOPackageUnit packageUnit : packageUnits)
    {
      long ePackagePointer = writer.getFilePointer();
      ePackagePointers.put(packageUnit.getID(), ePackagePointer);

      byte[] bytes = getEPackageBytes(packageUnit);
      writer.writeByteArray(bytes);

      monitor.worked(3);
    }

    newPackageUnitPointer = writer.getFilePointer();
    writer.writeXLong(packageUnitPointer);

    writer.writeXInt(packageUnits.length);
    for (InternalCDOPackageUnit packageUnit : packageUnits)
    {
      writer.writeCDOPackageUnit(packageUnit, false);

      long ePackagePointer = ePackagePointers.get(packageUnit.getID());
      writer.writeXLong(ePackagePointer);

      EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
      mapPackage(ePackage);

      monitor.worked(1);
    }
  }

  public long createBranch(final int branchID, final BranchInfo branchInfo)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("createBranch: {0}, {1}", branchID, branchInfo); //$NON-NLS-1$
    }

    return writer.append(new LissomeFileOperation()
    {
      @Override
      public void execute(LissomeFileHandle writer) throws IOException
      {
        writer.writeByte(CREATE_BRANCH_BLOCK);
        writer.writeXInt(branchID);
        writer.writeString(branchInfo.getName());
        writer.writeXInt(branchInfo.getBaseBranchID());
        writer.writeXLong(branchInfo.getBaseTimeStamp());
      }
    });
  }

  public void write(final InternalCommitContext context, final OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("write: {0}", context); //$NON-NLS-1$
    }

    try
    {
      final CDOBranchPoint branchPoint = context.getBranchPoint();
      final long previousTimeStamp = context.getPreviousTimeStamp();
      final String userID = context.getUserID();
      final String commitComment = context.getCommitComment();

      final InternalCDOPackageUnit[] newPackageUnits = context.getNewPackageUnits();
      final InternalCDORevision[] newObjects = context.getNewObjects();
      final CDOID[] detachedObjects = context.getDetachedObjects();
      final Map<CDOID, EClass> detachedObjectTypes = context.getDetachedObjectTypes();
      final CDOBranchVersion[] detachedObjectVersions = context.getDetachedObjectVersions();
      final InternalCDORevisionDelta[] dirtyObjectDeltas = context.getDirtyObjectDeltas();

      int revisionWork = detachedObjects.length + 3 * newObjects.length + 2 * dirtyObjectDeltas.length;
      monitor.begin(1 + 4 * newPackageUnits.length + revisionWork);

      commitTransactionTask = new CommitTransactionTask(context);

      newCommitPointer = writer.append(new LissomeFileOperation()
      {
        @Override
        public void execute(LissomeFileHandle writer) throws IOException
        {
          writer.writeByte(COMMIT_TRANSACTION_BLOCK);
          writer.writeXLong(commitPointer);
          writer.writeCDOBranchPoint(branchPoint);
          writer.writeXLong(previousTimeStamp);
          writer.writeString(userID);
          writer.writeString(commitComment);
          monitor.worked();

          // New package units
          if (newPackageUnits.length != 0)
          {
            writePackageUnits(writer, newPackageUnits, monitor);
          }

          InternalCDORevision[] detachedRevisions = null;
          if (detachedObjects.length != 0 && detachedObjectTypes != null && detachedObjectVersions != null)
          {
            detachedRevisions = new InternalCDORevision[detachedObjects.length];
            commitTransactionTask.setDetachedRevisions(detachedRevisions);
          }

          // Detached objects
          CDOBranch transactionBranch = branchPoint.getBranch();
          long timeStamp = branchPoint.getTimeStamp();

          writer.writeBoolean(detachedObjectTypes != null);
          writer.writeBoolean(detachedObjectVersions != null);
          writer.writeXInt(detachedObjects.length);

          for (int i = 0; i < detachedObjects.length; i++)
          {
            CDOID id = detachedObjects[i];
            EClass eClass = detachedObjectTypes != null ? detachedObjectTypes.get(id) : null;
            CDOBranchVersion branchVersion = detachedObjectVersions != null ? detachedObjectVersions[i] : null;

            if (detachedRevisions != null)
            {
              int version = branchVersion.getBranch() == transactionBranch ? branchVersion.getVersion() + 1 : CDOBranchVersion.FIRST_VERSION;

              detachedRevisions[i] = new DetachedCDORevision(eClass, id, transactionBranch, version, timeStamp);
            }

            if (eClass != null)
            {
              int cid = getStore().getMetaID(eClass);
              writer.writeXInt(cid);
            }

            if (branchVersion != null)
            {
              int version = branchVersion.getVersion();
              if (branchVersion.getBranch() == transactionBranch)
              {
                writer.writeXInt(version);
              }
              else
              {
                writer.writeXInt(-version);
                writer.writeCDOBranch(branchVersion.getBranch());
              }
            }

            monitor.worked();
          }

          // New objects
          Map<CDORevision, Long> newObjectPointers = commitTransactionTask.getNewObjectPointers();
          writer.writeXInt(newObjects.length);
          for (InternalCDORevision revision : newObjects)
          {
            long pointer = writer.getFilePointer();
            newObjectPointers.put(revision, pointer);
            writer.writeCDORevision(revision, CDORevision.UNCHUNKED);
            monitor.worked();
          }

          // Dirty object deltas
          writer.writeXInt(dirtyObjectDeltas.length);
          for (InternalCDORevisionDelta revisionDelta : dirtyObjectDeltas)
          {
            writer.writeCDORevisionDelta(revisionDelta);
            monitor.worked();
          }

          // Large objects
          ExtendedDataInputStream in = context.getLobs();
          if (in != null)
          {
            int count = in.readInt();
            for (int i = 0; i < count; i++)
            {
              byte[] id = in.readByteArray();
              commitTransactionTask.getLobs().add(id);

              long size = in.readLong();
              if (size > 0)
              {
                writeBlob(id, size, in);
              }
              else
              {
                writeClob(id, -size, new InputStreamReader(in));
              }
            }
          }
        }
      });

      commitTransactionTask.setNewCommitPointer(newCommitPointer);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    // TODO: implement LissomeStoreAccessor.writeBlob(id, size, inputStream)
    throw new UnsupportedOperationException();
  }

  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    // TODO: implement LissomeStoreAccessor.writeClob(id, size, reader)
    throw new UnsupportedOperationException();
  }

  protected void removeLob(byte[] id)
  {
    // TODO: implement LissomeStoreAccessor.removeLob(id)
    throw new UnsupportedOperationException();
  }

  public CommitTransactionTask commit(OMMonitor monitor)
  {
    try
    {
      if (newCommitPointer != commitPointer || newPackageUnitPointer != packageUnitPointer)
      {
        commitPointer = newCommitPointer;
        packageUnitPointer = newPackageUnitPointer;

        writer.seek(0L);
        writer.writeXLong(commitPointer);
        writer.writeXLong(packageUnitPointer);
      }

      return commitTransactionTask;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      cleanUp();
    }
  }

  public void rollback(IStoreAccessor.CommitContext commitContext)
  {
    try
    {
      List<byte[]> lobs = commitTransactionTask.getLobs();
      for (byte[] id : lobs)
      {
        removeLob(id);
      }
    }
    finally
    {
      cleanUp();
    }
  }

  protected void cleanUp()
  {
    commitTransactionTask = null;
    newCommitPointer = commitPointer;
    newPackageUnitPointer = packageUnitPointer;
  }

  public CDOCommitInfo readCommitInfo(LissomeFileHandle reader, long pointer)
  {
    try
    {
      reader.seek(pointer);
      reader.readByte(); // COMMIT_TRANSACTION_BLOCK
      reader.readXLong(); // commitPointer

      CDOBranchPoint branchPoint = reader.readCDOBranchPoint();
      CDOBranch branch = branchPoint.getBranch();
      long timeStamp = branchPoint.getTimeStamp();
      long previousTimeStamp = reader.readXLong();
      String userID = reader.readString();
      String comment = reader.readString();

      InternalCDOCommitInfoManager commitInfoManager = getStore().getRepository().getCommitInfoManager();
      return commitInfoManager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, null, null);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }
}
