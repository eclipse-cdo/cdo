/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOLegacyAdapter extends CDOLegacyWrapper implements Adapter.Internal
{
  public CDOLegacyAdapter(InternalEObject instance)
  {
    super(instance);
  }

  public void setTarget(Notifier newTarget)
  {
    instance = (InternalEObject)newTarget;
  }

  public void unsetTarget(Notifier oldTarget)
  {
    if (instance == oldTarget)
    {
      instance = null;
    }
  }

  public Notifier getTarget()
  {
    return instance;
  }

  public boolean isAdapterForType(Object type)
  {
    return true;
  }

  public void notifyChanged(Notification notification)
  {
    switch (notification.getEventType())
    {
    case Notification.SET:
      view.getStore().set(instance, (EStructuralFeature)notification.getFeature(), notification.getPosition(),
          notification.getNewValue());
      break;

    case Notification.UNSET:
      view.getStore().unset(instance, (EStructuralFeature)notification.getFeature());
      break;

    case Notification.MOVE:
      // TODO Is that correct?
      view.getStore().move(instance, (EStructuralFeature)notification.getFeature(), notification.getPosition(),
          (Integer)notification.getOldValue());
      break;

    case Notification.ADD:
      view.getStore().add(instance, (EStructuralFeature)notification.getFeature(), notification.getPosition(),
          notification.getNewValue());
      break;

    case Notification.ADD_MANY:
    {
      int pos = notification.getPosition();
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>)notification.getNewValue();
      for (Object object : list)
      {
        // TODO Is that correct?
        view.getStore().add(instance, (EStructuralFeature)notification.getFeature(), pos++, object);
      }
    }

      break;

    case Notification.REMOVE:
      view.getStore().remove(instance, (EStructuralFeature)notification.getFeature(), notification.getPosition());
      break;

    case Notification.REMOVE_MANY:
      // TODO: implement CDOLegacyAdapter.notifyChanged(notification)
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public void cdoInternalPostInvalidate()
  {
  }
}
