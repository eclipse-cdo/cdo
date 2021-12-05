/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.handlers;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class LockingHandler extends AbstractBaseHandler<EObject>
{
  private final Operation operation;

  private final boolean recursive;

  public LockingHandler(Operation operation, boolean recursive)
  {
    super(EObject.class, null);
    this.operation = operation;
    this.recursive = recursive;
  }

  @Override
  protected void doExecute(IProgressMonitor monitor) throws Exception
  {
    CDOView view = null;
    Set<CDOObject> objects = new HashSet<>();

    for (EObject element : elements)
    {
      CDOObject object = CDOUtil.getCDOObject(element);
      if (object != null)
      {
        CDOView objectView = object.cdoView();
        if (objectView != null)
        {
          if (view == null)
          {
            view = objectView;
          }
          else if (view != objectView)
          {
            return;
          }

          objects.add(object);
        }
      }
    }

    if (operation == Operation.LOCK)
    {
      long timeout = getTimeout();
      view.lockObjects(objects, LockType.WRITE, timeout, recursive);
    }
    else
    {
      view.unlockObjects(objects, LockType.WRITE, recursive);
    }
  }

  private long getTimeout()
  {
    Long pref = OM.PREF_LOCK_TIMEOUT.getValue();
    if (pref != null)
    {
      return pref;
    }

    return 5000L;
  }

  /**
   * @author Eike Stepper
   */
  public static final class LockObject extends LockingHandler
  {
    public LockObject()
    {
      super(Operation.LOCK, false);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class LockTree extends LockingHandler
  {
    public LockTree()
    {
      super(Operation.LOCK, true);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class UnlockObject extends LockingHandler
  {
    public UnlockObject()
    {
      super(Operation.UNLOCK, false);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class UnlockTree extends LockingHandler
  {
    public UnlockTree()
    {
      super(Operation.UNLOCK, true);
    }
  }
}
