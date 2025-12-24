/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.common;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.IMembershipKey;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 */
public class MembershipKey implements IMembershipKey
{
  private IBuddy buddy;

  private ICollaboration collaboration;

  public MembershipKey(IBuddy buddy, ICollaboration collaboration)
  {
    this.buddy = buddy;
    this.collaboration = collaboration;
  }

  @Override
  public IBuddy getBuddy()
  {
    return buddy;
  }

  @Override
  public ICollaboration getCollaboration()
  {
    return collaboration;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof IMembershipKey)
    {
      IMembershipKey key = (IMembershipKey)obj;
      return ObjectUtil.equals(getBuddy(), key.getBuddy()) && ObjectUtil.equals(getCollaboration(), key.getCollaboration());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(buddy) ^ ObjectUtil.hashCode(collaboration);
  }

  @Override
  public String toString()
  {
    return buddy + "(" + collaboration + ")"; //$NON-NLS-1$ //$NON-NLS-2$
  }
}
