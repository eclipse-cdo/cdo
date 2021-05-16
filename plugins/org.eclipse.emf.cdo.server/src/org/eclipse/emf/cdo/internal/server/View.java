/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants.UnitOpcode;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.IUnit;
import org.eclipse.emf.cdo.server.IUnitManager;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class View extends Lifecycle implements InternalView, CDOCommonView.Options
{
  private InternalSession session;

  private final int viewID;

  /**
   * Needed here so we can compute the hashCode even after session becomes null due to deactivation!
   */
  private final int sessionID;

  private CDOBranchPoint branchPoint;

  private CDOBranchPoint normalizedBranchPoint;

  private String durableLockingID;

  private final InternalRepository repository;

  private final Set<CDOID> changeSubscriptionIDs = new HashSet<>();

  private final Set<CDOID> openUnitRoots = new HashSet<>();

  private boolean lockNotificationsEnabled;

  private boolean closed;

  private final IRegistry<String, Object> properties = new HashMapRegistry.AutoCommit<>();

  /**
   * @since 2.0
   */
  public View(InternalSession session, int viewID, CDOBranchPoint branchPoint)
  {
    this.session = session;
    this.viewID = viewID;
    sessionID = session.getSessionID();

    repository = session.getManager().getRepository();
    setBranchPoint(branchPoint);
  }

  @Override
  public ExecutorService getExecutorService()
  {
    return session.getExecutorService();
  }

  @Override
  public InternalSession getSession()
  {
    return session;
  }

  @Override
  public int getSessionID()
  {
    return session.getSessionID();
  }

  @Override
  public int getViewID()
  {
    return viewID;
  }

  @Override
  public CDOBranch getBranch()
  {
    return branchPoint.getBranch();
  }

  @Override
  public long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  @Override
  public boolean isReadOnly()
  {
    return true;
  }

  @Override
  public boolean isHistorical()
  {
    return branchPoint.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE;
  }

  @Override
  public boolean isDurableView()
  {
    return durableLockingID != null;
  }

  @Override
  public String getDurableLockingID()
  {
    return durableLockingID;
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalRepository getRepository()
  {
    return repository;
  }

  @Override
  public InternalCDORevision getRevision(CDOID id)
  {
    CDORevisionManager revisionManager = repository.getRevisionManager();
    return (InternalCDORevision)revisionManager.getRevision(id, normalizedBranchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
  }

  private List<CDORevision> getRevisions(List<CDOID> ids)
  {
    InternalCDORevisionManager revisionManager = repository.getRevisionManager();
    return revisionManager.getRevisions(ids, normalizedBranchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
  }

  @Override
  public void changeTarget(CDOBranchPoint branchPoint, List<CDOID> invalidObjects, List<CDORevisionDelta> allChangedObjects, List<CDOID> allDetachedObjects)
  {
    List<CDORevision> oldRevisions = getRevisions(invalidObjects);
    setBranchPoint(branchPoint);
    List<CDORevision> newRevisions = getRevisions(invalidObjects);

    Iterator<CDORevision> it = newRevisions.iterator();
    for (CDORevision oldRevision : oldRevisions)
    {
      CDORevision newRevision = it.next();
      if (newRevision == null)
      {
        allDetachedObjects.add(oldRevision.getID());
      }
      else if (newRevision != oldRevision)
      {
        // Fix for Bug 369646: ensure that revisions are fully loaded
        repository.ensureChunks((InternalCDORevision)newRevision, CDORevision.UNCHUNKED);
        repository.ensureChunks((InternalCDORevision)oldRevision, CDORevision.UNCHUNKED);

        CDORevisionDelta delta = newRevision.compare(oldRevision);
        allChangedObjects.add(delta);
      }
    }
  }

  @Override
  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    checkOpen();
    validateTimeStamp(branchPoint.getTimeStamp());

    InternalCDOBranchManager branchManager = getSession().getManager().getRepository().getBranchManager();
    this.branchPoint = CDOBranchUtil.adjustBranchPoint(branchPoint, branchManager);
    normalizedBranchPoint = CDOBranchUtil.normalizeBranchPoint(this.branchPoint);
  }

  protected void validateTimeStamp(long timeStamp) throws IllegalArgumentException
  {
    if (timeStamp != UNSPECIFIED_DATE)
    {
      repository.validateTimeStamp(timeStamp);
    }
  }

  @Override
  public void setDurableLockingID(String durableLockingID)
  {
    this.durableLockingID = durableLockingID;
  }

  @Override
  public boolean openUnit(CDOID rootID, UnitOpcode opcode, CDORevisionHandler revisionHandler, OMMonitor monitor)
  {
    IUnitManager unitManager = repository.getUnitManager();
    IUnit unit = unitManager.getUnit(rootID);

    switch (opcode)
    {
    case CREATE:
    case CREATE_AND_OPEN:
      if (unit != null)
      {
        return false;
      }

      unit = unitManager.createUnit(rootID, this, revisionHandler, monitor);
      break;

    case OPEN:
    case OPEN_DEMAND_CREATE:
      if (unit == null)
      {
        if (opcode.isCreate())
        {
          unit = unitManager.createUnit(rootID, this, revisionHandler, monitor);
          break;
        }

        return false;
      }

      unit.open(this, revisionHandler, monitor);
      break;
    }

    if (opcode.isOpen())
    {
      openUnitRoots.add(rootID);
    }

    return true;
  }

  @Override
  public void closeUnit(CDOID rootID)
  {
    openUnitRoots.remove(rootID);
  }

  @Override
  public boolean isInOpenUnit(CDOID id)
  {
    if (openUnitRoots.isEmpty())
    {
      return false;
    }

    if (openUnitRoots.contains(id))
    {
      return true;
    }

    InternalCDORevision revision = getRevision(id);
    if (revision != null)
    {
      CDOID parentID = revision.getResourceID();
      if (parentID == id)
      {
        // This must be the root resource; break the recursion.
        return false;
      }

      if (CDOIDUtil.isNull(parentID))
      {
        parentID = (CDOID)revision.getContainerID();
      }

      return isInOpenUnit(parentID);
    }

    return false;
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized void subscribe(CDOID id)
  {
    checkOpen();
    changeSubscriptionIDs.add(id);
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized void unsubscribe(CDOID id)
  {
    checkOpen();
    changeSubscriptionIDs.remove(id);
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized boolean hasSubscription(CDOID id)
  {
    if (isClosed())
    {
      return false;
    }

    if (changeSubscriptionIDs.contains(id))
    {
      return true;
    }

    return false;
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized void clearChangeSubscription()
  {
    checkOpen();
    changeSubscriptionIDs.clear();
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(sessionID, viewID);
  }

  @Override
  public String toString()
  {
    int sessionID = session == null ? 0 : session.getSessionID();
    return MessageFormat.format("{0}[{1}:{2}]", getClassName(), sessionID, viewID); //$NON-NLS-1$
  }

  protected String getClassName()
  {
    return "View"; //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  @Override
  public void close()
  {
    deactivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (!isClosed())
    {
      session.viewClosed(this);
    }

    super.doDeactivate();
  }

  /**
   * @since 2.0
   */
  @Override
  public void doClose()
  {
    clearChangeSubscription();
    openUnitRoots.clear();
    closed = true;
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean isClosed()
  {
    return closed;
  }

  protected void checkOpen()
  {
    if (isClosed())
    {
      throw new IllegalStateException("View closed"); //$NON-NLS-1$
    }
  }

  @Override
  public IOptionsContainer getContainer()
  {
    return this;
  }

  @Override
  public Options options()
  {
    return this;
  }

  @Override
  public final IRegistry<String, Object> properties()
  {
    return properties;
  }

  @Override
  public boolean isLockNotificationEnabled()
  {
    return lockNotificationsEnabled;
  }

  @Override
  public void setLockNotificationEnabled(boolean enable)
  {
    lockNotificationsEnabled = enable;
  }
}
