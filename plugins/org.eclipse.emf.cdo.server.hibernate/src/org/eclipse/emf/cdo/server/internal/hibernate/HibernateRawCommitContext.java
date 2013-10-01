/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used during raw import.
 *
 * @author Martin Taal
 */
public class HibernateRawCommitContext implements InternalCommitContext
{
  private Map<CDOID, CDOID> idMappings = CDOIDUtil.createMap();

  private List<InternalCDORevision> dirtyObjects = new ArrayList<InternalCDORevision>();

  private List<InternalCDORevision> newObjects = new ArrayList<InternalCDORevision>();

  private int idCounter = 1;

  private CDOBranchPoint branchPoint;

  private boolean usingEcore;

  private boolean usingEtypes;

  private Map<Object, Object> data;

  public CDORevision getRevision(CDOID id)
  {
    for (CDORevision cdoRevision : newObjects)
    {
      if (id.equals(cdoRevision.getID()))
      {
        return cdoRevision;
      }
    }

    for (CDORevision cdoRevision : dirtyObjects)
    {
      if (id.equals(cdoRevision.getID()))
      {
        return cdoRevision;
      }
    }

    return null;
  }

  public CDOBranchPoint getBranchPoint()
  {
    if (branchPoint == null)
    {
      branchPoint = new CDOHibernateBranchPointImpl(System.currentTimeMillis());
    }

    return branchPoint;
  }

  public long getPreviousTimeStamp()
  {
    return 0;
  }

  public String getUserID()
  {
    return null;
  }

  public String getCommitComment()
  {
    return null;
  }

  public long getLastUpdateTime()
  {
    return 0;
  }

  public long getTimeStamp()
  {
    return 0;
  }

  public void setLastTreeRestructuringCommit(long lastTreeRestructuringCommit)
  {
  }

  public boolean isAutoReleaseLocksEnabled()
  {
    return false;
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return HibernateThreadContext.getCurrentStoreAccessor().getStore().getRepository().getPackageRegistry();
  }

  public byte getSecurityImpact()
  {
    return CommitNotificationInfo.IMPACT_NONE;
  }

  public void setSecurityImpact(byte securityImpact, Set<? extends Object> impactedRules)
  {
  }

  public boolean isClearResourcePathCache()
  {
    return false;
  }

  public boolean isUsingEcore()
  {
    return usingEcore;
  }

  public boolean isUsingEtypes()
  {
    return usingEtypes;
  }

  public InternalCDOPackageUnit[] getNewPackageUnits()
  {
    return new InternalCDOPackageUnit[0];
  }

  public CDOLockState[] getLocksOnNewObjects()
  {
    return null;
  }

  public InternalCDORevision[] getNewObjects()
  {
    return newObjects.toArray(new InternalCDORevision[0]);
  }

  public InternalCDORevision[] getDirtyObjects()
  {
    return dirtyObjects.toArray(new InternalCDORevision[0]);
  }

  public void addNewObject(InternalCDORevision cdoRevision)
  {
    cdoRevision.setVersion(1);
    CDOID newCDOID = CDOIDUtil.createTempObject(idCounter++);
    addIDMapping(cdoRevision.getID(), newCDOID);
    cdoRevision.setID(newCDOID);
    cdoRevision.setBranchPoint(getBranchPoint());
    newObjects.add(cdoRevision);
  }

  public void addDirtyObject(InternalCDORevision cdoRevision)
  {
    cdoRevision.setBranchPoint(getBranchPoint());
    dirtyObjects.add(cdoRevision);
  }

  public InternalCDORevisionDelta[] getDirtyObjectDeltas()
  {
    return null;
  }

  public CDOID[] getDetachedObjects()
  {
    return new CDOID[0];
  }

  public Map<CDOID, EClass> getDetachedObjectTypes()
  {
    return Collections.emptyMap();
  }

  public CDOBranchVersion[] getDetachedObjectVersions()
  {
    return new CDOBranchVersion[0];
  }

  public ExtendedDataInputStream getLobs()
  {
    return null;
  }

  public Map<CDOID, CDOID> getIDMappings()
  {
    return idMappings;
  }

  public CDOCommitInfo createCommitInfo()
  {
    return null;
  }

  public byte getRollbackReason()
  {
    return CDOProtocolConstants.ROLLBACK_REASON_UNKNOWN;
  }

  public String getRollbackMessage()
  {
    return null;
  }

  public List<CDOIDReference> getXRefs()
  {
    return null;
  }

  public List<LockState<Object, IView>> getPostCommmitLockStates()
  {
    return null;
  }

  public void preWrite()
  {
  }

  public void write(OMMonitor monitor)
  {
  }

  public void commit(OMMonitor monitor)
  {
  }

  public void rollback(String message)
  {
  }

  public void postCommit(boolean success)
  {
  }

  public InternalCDORevision[] getDetachedRevisions()
  {
    return new InternalCDORevision[0];
  }

  public void setClearResourcePathCache(boolean clearResourcePathCache)
  {
  }

  public void setUsingEcore(boolean usingEcore)
  {
    this.usingEcore = usingEcore;
  }

  public void setUsingEtypes(boolean usingEtypes)
  {
    this.usingEtypes = usingEtypes;
  }

  public void setNewPackageUnits(InternalCDOPackageUnit[] newPackageUnits)
  {
  }

  public void setLocksOnNewObjects(CDOLockState[] locksOnNewObjects)
  {
  }

  public void setNewObjects(InternalCDORevision[] newObjects)
  {
  }

  public void setDirtyObjectDeltas(InternalCDORevisionDelta[] dirtyObjectDeltas)
  {
  }

  public void setDetachedObjects(CDOID[] detachedObjects)
  {
  }

  public void setDetachedObjectTypes(Map<CDOID, EClass> detachedObjectTypes)
  {
  }

  public void setDetachedObjectVersions(CDOBranchVersion[] detachedObjectVersions)
  {
  }

  public void setLastUpdateTime(long lastUpdateTime)
  {
  }

  public void setAutoReleaseLocksEnabled(boolean on)
  {
  }

  public void setCommitComment(String comment)
  {
  }

  public void setLobs(ExtendedDataInputStream in)
  {
  }

  public void addIDMapping(CDOID oldID, CDOID newID)
  {
    // this can happen if an id is recreated to itself
    idMappings.remove(newID);
    idMappings.put(oldID, newID);
  }

  public void applyIDMappings(OMMonitor monitor)
  {
  }

  public InternalTransaction getTransaction()
  {
    return null;
  }

  public <T> T getData(Object key)
  {
    if (data == null)
    {
      return null;
    }

    @SuppressWarnings("unchecked")
    T result = (T)data.get(key);
    return result;
  }

  public synchronized <T extends Object> T setData(Object key, T value)
  {
    if (data == null)
    {
      data = new HashMap<Object, Object>();
    }

    @SuppressWarnings("unchecked")
    T old = (T)data.put(key, value);
    return old;
  }
}
