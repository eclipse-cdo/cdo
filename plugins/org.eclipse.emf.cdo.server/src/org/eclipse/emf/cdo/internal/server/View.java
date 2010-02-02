/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class View implements InternalView
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
    repository = session.getManager().getRepository();

    this.viewID = viewID;
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

  public boolean[] changeTarget(CDOBranchPoint branchPoint, List<CDOID> invalidObjects)
  {
    checkOpen();
    setBranchPoint(branchPoint);
    List<CDORevision> revisions = repository.getRevisionManager().getRevisions(invalidObjects, branchPoint, 0,
        CDORevision.DEPTH_NONE, false);
    boolean[] existanceFlags = new boolean[revisions.size()];
    for (int i = 0; i < existanceFlags.length; i++)
    {
      existanceFlags[i] = revisions.get(i) != null;
    }

    return existanceFlags;
  }

  private void setBranchPoint(CDOBranchPoint branchPoint)
  {
    long timeStamp = branchPoint.getTimeStamp();
    branchPoint = CDOBranchUtil.createBranchPoint(branchPoint.getBranch(), timeStamp);
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
    return MessageFormat.format("View[{0}]", viewID); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  public void close()
  {
    if (!isClosed())
    {
      session.viewClosed(this);
    }
  }

  /**
   * @since 2.0
   */
  public void doClose()
  {
    clearChangeSubscription();
    session = null;
    repository = null;
    changeSubscriptionIDs = null;
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return session == null;
  }

  private void checkOpen()
  {
    if (isClosed())
    {
      throw new IllegalStateException("View closed"); //$NON-NLS-1$
    }
  }
}
