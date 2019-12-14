/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome.optimizer;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeStore;
import org.eclipse.emf.cdo.server.internal.lissome.db.IndexWriter;
import org.eclipse.emf.cdo.server.internal.lissome.file.Vob;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CommitTransactionTask implements OptimizerTask
{
  protected final CDOBranchPoint branchPoint;

  protected final long previousTimeStamp;

  protected final String userID;

  protected final String commitComment;

  protected final InternalCDOPackageUnit[] newPackageUnits;

  protected final InternalCDORevision[] newObjects;

  protected final InternalCDORevisionDelta[] dirtyObjectDeltas;

  protected final InternalCDORevision[] dirtyObjects;

  protected final CDOID[] detachedObjects;

  protected final Map<CDOID, EClass> detachedObjectTypes;

  protected final CDOBranchVersion[] detachedObjectVersions;

  protected final List<byte[]> lobs = new ArrayList<>();

  private long newCommitPointer;

  private long newPackageUnitPointer;

  private Map<CDORevision, Long> newObjectPointers = new HashMap<>();

  private InternalCDORevision[] detachedRevisions;

  public CommitTransactionTask(InternalCommitContext context)
  {
    branchPoint = context.getBranchPoint();
    previousTimeStamp = context.getPreviousTimeStamp();
    userID = context.getUserID();
    commitComment = context.getCommitComment();
    newPackageUnits = context.getNewPackageUnits();
    newObjects = context.getNewObjects();
    dirtyObjectDeltas = context.getDirtyObjectDeltas();
    dirtyObjects = context.getDirtyObjects();
    detachedObjects = context.getDetachedObjects();
    detachedObjectTypes = context.getDetachedObjectTypes();
    detachedObjectVersions = context.getDetachedObjectVersions();
  }

  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  public boolean isValid(CDOBranchPoint branchPoint)
  {
    long timeStamp = branchPoint.getTimeStamp();
    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE && timeStamp < this.branchPoint.getTimeStamp())
    {
      return false;
    }

    return branchPoint.getBranch() == this.branchPoint.getBranch();
  }

  public void setDetachedRevisions(InternalCDORevision[] detachedRevisions)
  {
    this.detachedRevisions = detachedRevisions;
  }

  public long getNewCommitPointer()
  {
    return newCommitPointer;
  }

  public void setNewCommitPointer(long newCommitPointer)
  {
    this.newCommitPointer = newCommitPointer;
  }

  public long getNewPackageUnitPointer()
  {
    return newPackageUnitPointer;
  }

  public Map<CDORevision, Long> getNewObjectPointers()
  {
    return newObjectPointers;
  }

  public List<byte[]> getLobs()
  {
    return lobs;
  }

  public CDOCommitInfo createCommitInfo(InternalCDOCommitInfoManager commitInfoManager)
  {
    CDOBranch branch = branchPoint.getBranch();
    long timeStamp = branchPoint.getTimeStamp();
    return commitInfoManager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, commitComment, null, null);
  }

  @Override
  public void execute(Optimizer optimizer) throws IOException
  {
    LissomeStore store = optimizer.getStore();
    IndexWriter indexWriter = store.getIndex().getWriter();
    Vob vob = store.getVob();

    indexWriter.addCommitInfo(branchPoint, newCommitPointer);

    addObjects(indexWriter);
    updateObjects(indexWriter, vob);
    detachObjects(indexWriter, vob);

    indexWriter.commit();
  }

  protected void addObjects(IndexWriter indexWriter)
  {
    if (newObjects.length != 0)
    {
      long[] pointers = new long[newObjects.length];
      for (int i = 0; i < newObjects.length; i++)
      {
        InternalCDORevision revision = newObjects[i];
        pointers[i] = -newObjectPointers.get(revision);
      }

      indexWriter.addObjects(newObjects, pointers);
    }
  }

  protected void updateObjects(IndexWriter indexWriter, Vob vob)
  {
    if (dirtyObjects.length != 0)
    {
      long[] pointers = new long[dirtyObjects.length];
      for (int i = 0; i < dirtyObjects.length; i++)
      {
        InternalCDORevision revision = dirtyObjects[i];
        pointers[i] = vob.addRevision(revision);
      }

      indexWriter.updateObjects(dirtyObjects, pointers);
    }
  }

  protected void detachObjects(IndexWriter indexWriter, Vob vob)
  {
    if (detachedObjects.length != 0)
    {
      long[] pointers = new long[detachedRevisions.length];
      for (int i = 0; i < detachedRevisions.length; i++)
      {
        InternalCDORevision revision = detachedRevisions[i];
        pointers[i] = vob.addRevision(revision);
      }

      indexWriter.detachObjects(branchPoint, detachedObjects, detachedRevisions, pointers);
    }
  }

  public void cacheRevisions(CDORevisionCacheAdder cache)
  {
    for (int i = 0; i < newObjects.length; i++)
    {
      InternalCDORevision revision = newObjects[i];
      cache.addRevision(revision);
    }

    for (int i = 0; i < dirtyObjects.length; i++)
    {
      InternalCDORevision revision = dirtyObjects[i];
      cache.addRevision(revision);
    }

    for (int i = 0; i < detachedObjects.length; i++)
    {
      CDOID id = detachedObjects[i];
      EClass eClass = detachedObjectTypes.get(id);
      CDOBranchVersion branchVersion = detachedObjectVersions[i];
      CDOBranch branch = branchVersion.getBranch();
      int version = branchVersion.getVersion();
      long timeStamp = branchPoint.getTimeStamp();

      DetachedCDORevision revision = new DetachedCDORevision(eClass, id, branch, version, timeStamp);
      cache.addRevision(revision);
    }
  }
}
