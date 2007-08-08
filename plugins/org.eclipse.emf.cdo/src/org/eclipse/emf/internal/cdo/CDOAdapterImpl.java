/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.internal.cdo.bundle.OM;

/**
 * @author Eike Stepper
 */
public class CDOAdapterImpl extends CDOLegacyImpl implements Adapter.Internal
{
  public CDOAdapterImpl()
  {
  }

  public boolean isAdapterForType(Object type)
  {
    return type == CDOAdapterImpl.class;
  }

  public InternalEObject getTarget()
  {
    return instance;
  }

  public void setTarget(Notifier newTarget)
  {
    if (newTarget instanceof InternalEObject)
    {
      instance = (InternalEObject)newTarget;
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + newTarget.getClass().getName());
    }
  }

  public void unsetTarget(Notifier oldTarget)
  {
    if (oldTarget instanceof InternalEObject)
    {
      if (instance == oldTarget)
      {
        instance = null;
      }
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + oldTarget.getClass().getName());
    }
  }

  public void notifyChanged(Notification msg)
  {
    try
    {
      if (msg.getNotifier() == instance)
      {
        switch (msg.getEventType())
        {
        case Notification.ADD:
        case Notification.ADD_MANY:
        case Notification.REMOVE:
        case Notification.REMOVE_MANY:
        case Notification.MOVE:
        case Notification.SET:
        case Notification.UNSET:
          if (!instance.eIsProxy())
          {
            CDOStateMachine.INSTANCE.write(this);
          }
        }
      }
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
    }
  }
}
