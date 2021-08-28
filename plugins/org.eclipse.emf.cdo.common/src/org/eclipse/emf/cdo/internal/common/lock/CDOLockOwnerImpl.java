/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.lock;

import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;

import org.eclipse.net4j.util.ObjectUtil;

import java.util.Objects;

/**
 * @author Caspar De Groot
 */
public class CDOLockOwnerImpl implements CDOLockOwner
{
  private final int sessionID;

  private final int viewID;

  private final String durableLockingID;

  public CDOLockOwnerImpl(int sessionID, int viewID, String durableLockingID)
  {
    this.sessionID = sessionID;
    this.viewID = viewID;
    this.durableLockingID = durableLockingID;
  }

  @Override
  public int getSessionID()
  {
    return sessionID;
  }

  @Override
  public int getViewID()
  {
    return viewID;
  }

  @Override
  public String getDurableLockingID()
  {
    return durableLockingID;
  }

  @Override
  public boolean isDurableView()
  {
    return sessionID == CDOLockUtil.DURABLE_SESSION_ID;
  }

  @Override
  public int hashCode()
  {
    if (isDurableView())
    {
      return ObjectUtil.hashCode(durableLockingID);
    }

    return ObjectUtil.hashCode(sessionID, viewID);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOLockOwner)
    {
      CDOLockOwner that = (CDOLockOwner)obj;

      if (isDurableView())
      {
        return that.isDurableView() && Objects.equals(durableLockingID, that.getDurableLockingID());
      }

      return !that.isDurableView() && sessionID == that.getSessionID() && viewID == that.getViewID();
    }

    return false;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("CDOLockOwner[");

    if (isDurableView())
    {
      builder.append("durable:");
      builder.append(durableLockingID);
    }
    else
    {
      builder.append(sessionID);
      builder.append(':');
      builder.append(viewID);
    }

    builder.append(']');
    return builder.toString();
  }
}
