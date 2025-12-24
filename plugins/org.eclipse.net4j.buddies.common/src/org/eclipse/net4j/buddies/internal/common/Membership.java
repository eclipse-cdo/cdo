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
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.buddies.common.IMembershipKey;
import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 */
public class Membership extends Lifecycle implements IMembership
{
  private MembershipKey key;

  private long startTime;

  private transient Object[] elements;

  private Membership(IBuddy buddy, ICollaboration collaboration)
  {
    key = new MembershipKey(buddy, collaboration);
    elements = new Object[] { buddy, collaboration };
    startTime = System.currentTimeMillis();
    activate();
  }

  @Override
  public IBuddy getBuddy()
  {
    return key.getBuddy();
  }

  @Override
  public ICollaboration getCollaboration()
  {
    return key.getCollaboration();
  }

  @Override
  public long getStartTime()
  {
    return startTime;
  }

  @Override
  public Object[] getElements()
  {
    return elements;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  public MembershipKey getKey()
  {
    return key;
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
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
    return key.hashCode();
  }

  @Override
  public String toString()
  {
    return key.toString();
  }

  public static IMembership create(IBuddy buddy, ICollaboration collaboration)
  {
    Membership membership = new Membership(buddy, collaboration);
    ((Buddy)buddy).addMembership(membership);
    ((Collaboration)collaboration).addMembership(membership);
    return membership;
  }
}
