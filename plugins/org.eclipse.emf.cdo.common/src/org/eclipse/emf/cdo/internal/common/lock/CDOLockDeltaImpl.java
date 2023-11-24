/*
 * Copyright (c) 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.lock;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.spi.common.lock.AbstractCDOLockState;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

/**
 * @author Eike Stepper
 */
public abstract class CDOLockDeltaImpl implements CDOLockDelta
{
  protected final Object target;

  protected final CDOLockOwner oldOwner;

  protected final CDOLockOwner newOwner;

  private CDOLockDeltaImpl(Object target, CDOLockOwner oldOwner, CDOLockOwner newOwner)
  {
    CheckUtil.checkArg(target, "target"); //$NON-NLS-1$
    CheckUtil.checkArg(oldOwner != null || newOwner != null, "oldOwner != null || newOwner != null"); //$NON-NLS-1$

    this.target = target;
    this.oldOwner = oldOwner;
    this.newOwner = newOwner;
  }

  @Override
  public final Object getTarget()
  {
    return target;
  }

  @Override
  public final CDOID getID()
  {
    return CDOLockUtil.getLockedObjectID(target);
  }

  @Override
  public final CDOBranch getBranch()
  {
    return CDOLockUtil.getLockedObjectBranch(target);
  }

  @Override
  public final CDOLockOwner getOldOwner()
  {
    return oldOwner;
  }

  @Override
  public final CDOLockOwner getNewOwner()
  {
    return newOwner;
  }

  @Override
  public final Kind getKind()
  {
    if (oldOwner == null)
    {
      return Kind.ADDED;
    }

    if (newOwner == null)
    {
      return Kind.REMOVED;
    }

    return Kind.REMAPPED;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(getKind());
    builder.append('_');
    builder.append(getType());
    builder.append('[');
    builder.append(target);

    if (oldOwner != null)
    {
      builder.append(" - ");
      AbstractCDOLockState.appendLockOwner(builder, oldOwner);
    }

    if (newOwner != null)
    {
      builder.append(" + ");
      AbstractCDOLockState.appendLockOwner(builder, newOwner);
    }

    builder.append(']');
    return builder.toString();
  }

  public static CDOLockDelta create(Object target, LockType type, CDOLockOwner oldOwner, CDOLockOwner newOwner)
  {
    switch (type)
    {
    case READ:
      return new Read(target, oldOwner, newOwner);

    case WRITE:
      return new Write(target, oldOwner, newOwner);

    case OPTION:
      return new Option(target, oldOwner, newOwner);

    default:
      throw new IllegalArgumentException("Illegal type: " + type);
    }
  }

  public static CDOLockDelta createNull(Object target)
  {
    return new Null(target);
  }

  /**
   * @author Eike Stepper
   */
  private static final class Null implements CDOLockDelta
  {
    private final Object target;

    public Null(Object target)
    {
      CheckUtil.checkArg(target, "target"); //$NON-NLS-1$
      this.target = target;
    }

    @Override
    public Object getTarget()
    {
      return target;
    }

    @Override
    public final CDOID getID()
    {
      return CDOLockUtil.getLockedObjectID(target);
    }

    @Override
    public final CDOBranch getBranch()
    {
      return CDOLockUtil.getLockedObjectBranch(target);
    }

    @Override
    public LockType getType()
    {
      return null;
    }

    @Override
    public CDOLockOwner getOldOwner()
    {
      return null;
    }

    @Override
    public CDOLockOwner getNewOwner()
    {
      return null;
    }

    @Override
    public Kind getKind()
    {
      return null;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("NULL[");
      builder.append(target);
      builder.append(']');
      return builder.toString();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Read extends CDOLockDeltaImpl
  {
    public Read(Object target, CDOLockOwner oldOwner, CDOLockOwner newOwner)
    {
      super(target, oldOwner, newOwner);
    }

    @Override
    public LockType getType()
    {
      return LockType.READ;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Write extends CDOLockDeltaImpl
  {
    public Write(Object target, CDOLockOwner oldOwner, CDOLockOwner newOwner)
    {
      super(target, oldOwner, newOwner);
    }

    @Override
    public LockType getType()
    {
      return LockType.WRITE;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Option extends CDOLockDeltaImpl
  {
    public Option(Object target, CDOLockOwner oldOwner, CDOLockOwner newOwner)
    {
      super(target, oldOwner, newOwner);
    }

    @Override
    public LockType getType()
    {
      return LockType.OPTION;
    }
  }
}
