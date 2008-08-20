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

import org.eclipse.emf.cdo.CDOView;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.CDOAware;
import org.eclipse.emf.ecore.impl.CDOCallback;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOCallbackImpl extends CDOLegacyImpl implements CDOCallback
{
  public CDOCallbackImpl(InternalEObject instance)
  {
    this.instance = instance;
  }

  public void beforeRead(EObjectImpl instance)
  {
    CDOStateMachine.INSTANCE.read(this);
  }

  public void beforeWrite(EObjectImpl instance)
  {
    CDOStateMachine.INSTANCE.write(this);
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type == CDOCallbackImpl.class;
  }

  public void notifyChanged(Notification msg)
  {
    try
    {
      if (msg.getNotifier() == instance)
      {
        Object feature = msg.getFeature();
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          if (reference.isContainment() && !reference.isTransient())
          {
            switch (msg.getEventType())
            {
            case Notification.ADD:
              notifyAdd(msg.getNewValue());
              break;

            case Notification.ADD_MANY:
              notifyAddMany(msg);
              break;

            case Notification.REMOVE:
              notifyRemove(msg.getOldValue());
              break;

            case Notification.REMOVE_MANY:
              notifyRemoveMany(msg);
              break;
            }
          }
        }
      }
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
    }
  }

  @Override
  protected void adjustEProxy()
  {
    // Do nothing
  }

  private void notifyAddMany(Notification msg)
  {
    List<?> newValues = (List<?>)msg.getNewValue();
    List<?> oldValues = (List<?>)msg.getOldValue();
    for (Object newValue : newValues)
    {
      if (!oldValues.contains(newValue))
      {
        notifyAdd(newValue);
      }
    }
  }

  private void notifyAdd(Object instance)
  {
    if (instance instanceof InternalEObject)
    {
      if (((InternalEObject)instance).eDeliver())
      {
        InternalCDOObject object = FSMUtil.adapt(instance, view);
        CDOStateMachine.INSTANCE.attach(object, cdoResource(), view);
      }
    }
  }

  private void notifyRemoveMany(Notification msg)
  {
    List<?> newValues = (List<?>)msg.getNewValue();
    List<?> oldValues = (List<?>)msg.getOldValue();
    for (Object oldValue : oldValues)
    {
      if (!newValues.contains(oldValue))
      {
        notifyRemove(oldValue);
      }
    }
  }

  private void notifyRemove(Object instance)
  {
    if (instance instanceof InternalEObject)
    {
      if (((InternalEObject)instance).eDeliver())
      {
        InternalCDOObject object = FSMUtil.adapt(instance, view);
        CDOStateMachine.INSTANCE.detach(object);
      }
    }
  }

  public static InternalCDOObject adapt(Object object, CDOView view) throws Throwable
  {
    if (object instanceof CDOAware)
    {
      CDOAware aware = (CDOAware)object;
      CDOCallbackImpl callback = (CDOCallbackImpl)aware.getCDOCallback();
      if (callback == null)
      {
        InternalEObject instance = (InternalEObject)aware;
        if (instance.eIsProxy())
        {
          instance = (InternalEObject)EcoreUtil.resolve(instance, view.getResourceSet());
        }

        callback = new CDOCallbackImpl(instance);
        aware.setCDOCallback(callback);
        instance.eAdapters().add(callback);
      }

      return callback;
    }

    return null;
  }
}
