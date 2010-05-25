/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
public class CDOLegacyAdapter extends CDOLegacyWrapper implements Adapter.Internal
{
  public CDOLegacyAdapter()
  {
    super(null);
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
    return type == CDOLegacyAdapter.class;
  }

  public void notifyChanged(Notification msg)
  {
    CDOStore store = view.getStore();
    EStructuralFeature feature = (EStructuralFeature)msg.getFeature();
    switch (msg.getEventType())
    {
    case Notification.SET:
      store.set(instance, feature, msg.getPosition(), msg.getNewValue());
      break;

    case Notification.UNSET:
      store.unset(instance, feature);
      break;

    case Notification.MOVE:
      // TODO Is that correct?
      store.move(instance, feature, msg.getPosition(), (Integer)msg.getOldValue());
      break;

    case Notification.ADD:
      store.add(instance, feature, msg.getPosition(), msg.getNewValue());
      break;

    case Notification.ADD_MANY:
    {
      int pos = msg.getPosition();
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>)msg.getNewValue();
      for (Object object : list)
      {
        // TODO Is that correct?
        store.add(instance, feature, pos++, object);
      }
    }

      break;

    case Notification.REMOVE:
      store.remove(instance, feature, msg.getPosition());
      break;

    case Notification.REMOVE_MANY:
    {
      int pos = msg.getPosition();
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>)msg.getOldValue();
      for (int i = 0; i < list.size(); i++)
      {
        store.remove(instance, feature, pos);
      }
    }

      break;
    }
  }

  @Override
  public void cdoInternalPostInvalidate()
  {
  }
}
