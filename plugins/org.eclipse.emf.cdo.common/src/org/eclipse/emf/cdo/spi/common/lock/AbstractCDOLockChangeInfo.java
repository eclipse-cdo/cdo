/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.lock;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.INotifier;

import java.util.HashSet;
import java.util.Set;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.15
 */
public abstract class AbstractCDOLockChangeInfo extends Event implements CDOLockChangeInfo
{
  private static final long serialVersionUID = 1L;

  public AbstractCDOLockChangeInfo(INotifier notifier)
  {
    super(notifier);
  }

  @Override
  public final Set<Operation> getOperations()
  {
    Set<Operation> result = new HashSet<>();

    for (CDOLockDelta delta : getLockDeltas())
    {
      CDOLockDelta.Kind kind = delta.getKind();

      switch (kind)
      {
      case ADDED:
        result.add(Operation.LOCK);
        break;

      case REMOVED:
        result.add(Operation.UNLOCK);
        break;

      case REMAPPED:
        // Do nothing.
        break;

      default:
        throw new AssertionError("Invalid kind: " + kind);
      }
    }

    return result;
  }

  @Override
  public final Set<LockType> getLockTypes()
  {
    Set<LockType> result = new HashSet<>();

    for (CDOLockDelta delta : getLockDeltas())
    {
      LockType type = delta.getType();
      result.add(type);
    }

    return result;
  }

  @Override
  public final Set<CDOID> getAffectedIDs()
  {
    Set<CDOID> ids = new HashSet<>();

    for (CDOLockDelta delta : getLockDeltas())
    {
      CDOID id = delta.getID();
      ids.add(id);
    }

    return ids;
  }

  @Override
  @Deprecated
  public final Operation getOperation()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final LockType getLockType()
  {
    throw new UnsupportedOperationException();
  }
}
