/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class View extends Lifecycle implements InternalView
{
  private InternalSession session;

  private int viewID;

  private CDOBranchPoint branchPoint;

  private InternalRepository repository;

  private Set<CDOID> changeSubscriptionIDs = new HashSet<CDOID>();

  /**
   * @since 2.0
   */
  public View(InternalSession session, int viewID, CDOBranchPoint branchPoint)
  {
    this.session = session;
    this.viewID = viewID;

    repository = session.getManager().getRepository();
    setBranchPoint(branchPoint);
  }

  public InternalSession getSession()
  {
    return session;
  }

  public int getViewID()
  {
    return viewID;
  }

  public CDOBranch getBranch()
  {
    return branchPoint.getBranch();
  }

  public long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  public boolean isReadOnly()
  {
    return true;
  }

  /**
   * @since 2.0
   */
  public InternalRepository getRepository()
  {
    checkOpen();
    return repository;
  }

  public InternalCDORevision getRevision(CDOID id)
  {
    CDORevisionManager revisionManager = repository.getRevisionManager();
    return (InternalCDORevision)revisionManager.getRevision(id, this, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE,
        true);
  }

  public boolean[] changeTarget(CDOBranchPoint branchPoint, List<CDOID> invalidObjects)
  {
    setBranchPoint(branchPoint);
    List<CDORevision> revisions = repository.getRevisionManager().getRevisions(invalidObjects, branchPoint, 0,
        CDORevision.DEPTH_NONE, true);
    boolean[] existanceFlags = new boolean[revisions.size()];
    for (int i = 0; i < existanceFlags.length; i++)
    {
      existanceFlags[i] = revisions.get(i) != null;
    }

    return existanceFlags;
  }

  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    checkOpen();
    long timeStamp = branchPoint.getTimeStamp();
    branchPoint = branchPoint.getBranch().getPoint(timeStamp);
    validateTimeStamp(timeStamp);
    this.branchPoint = branchPoint;
  }

  protected void validateTimeStamp(long timeStamp) throws IllegalArgumentException
  {
    if (timeStamp != UNSPECIFIED_DATE)
    {
      repository.validateTimeStamp(timeStamp);
    }
  }

  /**
   * @since 2.0
   */
  public synchronized void subscribe(CDOID id)
  {
    checkOpen();
    changeSubscriptionIDs.add(id);
  }

  /**
   * @since 2.0
   */
  public synchronized void unsubscribe(CDOID id)
  {
    checkOpen();
    changeSubscriptionIDs.remove(id);
  }

  /**
   * @since 2.0
   */
  public synchronized boolean hasSubscription(CDOID id)
  {
    checkOpen();
    return changeSubscriptionIDs.contains(id);
  }

  /**
   * @since 2.0
   */
  public synchronized void clearChangeSubscription()
  {
    checkOpen();
    changeSubscriptionIDs.clear();
  }

  public int compareTo(CDOBranchPoint o)
  {
    return branchPoint.compareTo(o);
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
  public void doClose()
  {
    clearChangeSubscription();
    changeSubscriptionIDs = null;
    session = null;
    repository = null;
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return repository == null;
  }

  private void checkOpen()
  {
    if (isClosed())
    {
      throw new IllegalStateException("View closed"); //$NON-NLS-1$
    }
  }
}
