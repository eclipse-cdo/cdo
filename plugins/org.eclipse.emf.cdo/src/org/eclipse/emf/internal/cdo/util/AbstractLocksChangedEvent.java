/*
 * Copyright (c) 2011, 2012, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.spi.common.lock.AbstractCDOLockChangeInfo;

import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * @author Caspar De Groot
 * @since 4.1
 */
public abstract class AbstractLocksChangedEvent extends AbstractCDOLockChangeInfo
{
  private static final long serialVersionUID = 1L;

  private final InternalCDOView sender;

  private final CDOLockChangeInfo lockChangeInfo;

  public AbstractLocksChangedEvent(INotifier notifier, InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
  {
    super(notifier);
    this.sender = sender;
    this.lockChangeInfo = lockChangeInfo;
  }

  public final InternalCDOView getSender()
  {
    return sender;
  }

  @Override
  public final CDOBranch getBranch()
  {
    return lockChangeInfo.getBranch();
  }

  @Override
  public final long getTimeStamp()
  {
    return lockChangeInfo.getTimeStamp();
  }

  @Override
  public CDOLockOwner getLockOwner()
  {
    return lockChangeInfo.getLockOwner();
  }

  @Override
  public final CDOLockDelta[] getLockDeltas()
  {
    return lockChangeInfo.getLockDeltas();
  }

  @Override
  public CDOLockState[] getLockStates()
  {
    return lockChangeInfo.getLockStates();
  }

  @Override
  public final boolean isInvalidateAll()
  {
    return lockChangeInfo.isInvalidateAll();
  }

  @Override
  protected String formatAdditionalParameters()
  {
    return "sender=" + sender + ", " + lockChangeInfo;
  }

  protected abstract InternalCDOSession getSession();
}
