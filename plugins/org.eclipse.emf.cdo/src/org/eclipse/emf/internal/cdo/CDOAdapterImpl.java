/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.emf.common.notify.Notification;

/**
 * @author Eike Stepper
 */
public class CDOAdapterImpl extends CDOLegacyImpl
{
  public CDOAdapterImpl()
  {
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type == CDOAdapterImpl.class;
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
