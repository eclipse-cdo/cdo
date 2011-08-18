/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Caspar De Groot
 */
public class CDOLockOwnerImpl implements CDOLockOwner
{
  private final int sessionID;

  private final int viewID;

  public CDOLockOwnerImpl(int sessionID, int viewID)
  {
    this.sessionID = sessionID;
    this.viewID = viewID;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public int getViewID()
  {
    return viewID;
  }

  @Override
  public int hashCode()
  {
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
      return sessionID == that.getSessionID() && viewID == that.getViewID();
    }

    return false;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("CDOLockOwner[");
    builder.append("session=");
    builder.append(sessionID);
    builder.append(", view=");
    builder.append(viewID);
    builder.append(']');
    return builder.toString();
  }
}
