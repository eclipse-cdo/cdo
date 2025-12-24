/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Christian W. Damus (CEA) - bug 376620: notifications for primitive-valued attributes
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureDeltaVisitorImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ReflectUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDONotificationBuilder extends CDOFeatureDeltaVisitorImpl
{
  private CDOView view;

  private InternalEObject object;

  private CDORevisionDelta revisionDelta;

  private NotificationChainImpl notification;

  private Set<CDOObject> detachedObjects;

  private InternalCDORevision oldRevision;

  /**
   * @since 3.0
   */
  public CDONotificationBuilder(CDOView view)
  {
    this.view = view;
  }

  /**
   * @since 3.0
   */
  public CDOView getView()
  {
    return view;
  }

  /**
   * @since 3.0
   */
  public synchronized NotificationChain buildNotification(InternalEObject object, InternalCDORevision oldRevision, CDORevisionDelta revisionDelta,
      Set<CDOObject> detachedObjects)
  {
    notification = new NotificationChainImpl();

    this.object = object;
    this.revisionDelta = revisionDelta;
    this.detachedObjects = detachedObjects;
    this.oldRevision = oldRevision;
    revisionDelta.accept(this);
    return notification;
  }

  public synchronized NotificationChain buildNotification(InternalCDOObject object, InternalCDORevision newRevision)
  {
    InternalCDORevision oldRevision = (InternalCDORevision)CDORevisionFactory.DEFAULT.createRevision(object.eClass());
    CDORevisionDelta revisionDelta = newRevision.compare(oldRevision);
    return buildNotification(object, oldRevision, revisionDelta, null);
  }

  @Override
  public void visit(CDOMoveFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    int oldPosition = delta.getOldPosition();
    int newPosition = delta.getNewPosition();
    Object oldValue = delta.getValue();
    if (oldValue instanceof CDOID)
    {
      CDOID oldID = (CDOID)oldValue;
      CDOObject object = findObjectByID(oldID);
      if (object != null)
      {
        oldValue = object;
      }
    }

    add(new CDODeltaNotificationImpl(object, Notification.MOVE, feature, Integer.valueOf(oldPosition), oldValue, newPosition));
  }

  @Override
  public void visit(CDOAddFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    add(new CDODeltaNotificationImpl(object, Notification.ADD, feature, getOldValue(feature), delta.getValue(), delta.getIndex()));
  }

  @Override
  public void visit(CDORemoveFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    int index = delta.getIndex();

    Object oldValue = delta.getValue();
    if (oldValue instanceof CDOID)
    {
      CDOID oldID = (CDOID)oldValue;
      CDOObject object = findObjectByID(oldID);
      if (object != null)
      {
        oldValue = object;
      }
    }

    add(new CDODeltaNotificationImpl(object, Notification.REMOVE, feature, oldValue, null, index));
  }

  @Override
  public void visit(CDOSetFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();

    Object oldValue = delta.getOldValue();
    if (oldValue == null || oldValue == CDOFeatureDelta.UNKNOWN_VALUE)
    {
      oldValue = getOldValue(feature);
    }

    if (oldValue instanceof CDOID)
    {
      CDOID oldID = (CDOID)oldValue;
      CDOObject object = findObjectByID(oldID);
      if (object != null)
      {
        oldValue = object;
      }
    }

    Object newValue = delta.getValue();
    add(createNotification(Notification.SET, feature, oldValue, newValue, delta.getIndex()));
  }

  @Override
  public void visit(CDOUnsetFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    Object oldValue = getOldValue(feature);
    if (oldValue instanceof CDOID)
    {
      CDOID oldID = (CDOID)oldValue;
      CDOObject object = findObjectByID(oldID);
      if (object != null)
      {
        oldValue = object;
      }
    }

    add(createNotification(Notification.UNSET, feature, oldValue, null, Notification.NO_INDEX));
  }

  @Override
  public void visit(CDOClearFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    Object oldValue = getOldValue(feature);
    if (oldValue instanceof List<?>)
    {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>)oldValue;
      if (!list.isEmpty())
      {
        list = new ArrayList<>(list); // Copy the list so that it.set() does not change the frozen oldRevision
        boolean changed = false;

        for (ListIterator<Object> it = list.listIterator(); it.hasNext();)
        {
          Object element = it.next();
          if (element instanceof CDOID)
          {
            CDOID id = (CDOID)element;
            CDOObject oldObject = findObjectByID(id);
            if (oldObject != null)
            {
              it.set(oldObject);
              changed = true;
            }
          }
        }

        if (changed)
        {
          oldValue = list;
        }
      }
    }

    add(new CDODeltaNotificationImpl(object, Notification.REMOVE_MANY, feature, oldValue, null));
  }

  @Override
  public void visit(CDOContainerFeatureDelta delta)
  {
    Object oldValue = null;
    if (oldRevision != null)
    {
      oldValue = oldRevision.getContainerID();

      if (oldValue instanceof CDOID)
      {
        CDOID oldID = (CDOID)oldValue;
        CDOObject object = findObjectByID(oldID);
        if (object != null)
        {
          oldValue = object;
        }
      }

    }

    add(new CDODeltaNotificationImpl(object, Notification.SET, EcorePackage.eINSTANCE.eContainmentFeature(), oldValue, delta.getContainerID()));
  }

  private CDOObject findObjectByID(CDOID id)
  {
    CDOObject object = view.getObject(id, false);
    if (object == null)
    {
      object = findDetachedObjectByID(id);
    }

    return object;
  }

  private CDOObject findDetachedObjectByID(CDOID id)
  {
    if (detachedObjects != null)
    {
      for (CDOObject object : detachedObjects)
      {
        if (id == object.cdoID())
        {
          return object;
        }
      }
    }

    return null;
  }

  private Object getOldValue(EStructuralFeature feature)
  {
    if (oldRevision == null)
    {
      return null;
    }

    return oldRevision.getValue(feature);
  }

  private CDODeltaNotificationImpl createNotification(int eventType, EStructuralFeature feature, Object oldValue, Object newValue, int position)
  {
    EClassifier eType = feature.getEType();

    if (oldValue != null || newValue != null)
    {
      CDOType type = CDOModelUtil.getType(eType);
      if (type != null)
      {
        if (oldValue != null)
        {
          oldValue = type.convertToEMF(eType, oldValue);
        }

        if (newValue != null)
        {
          newValue = type.convertToEMF(eType, newValue);
        }
      }
    }

    Class<?> instanceClass = eType.getInstanceClass();
    if (instanceClass.isPrimitive())
    {
      Object defaultValue = null;
      if (oldValue == null)
      {
        defaultValue = feature.getDefaultValue();
        oldValue = defaultValue;
      }

      if (newValue == null)
      {
        if (defaultValue == null)
        {
          defaultValue = feature.getDefaultValue();
        }

        newValue = defaultValue;
      }

      // There cannot be a position if it's a primitive value because primitives cannot be in lists
      return createPrimitiveNotification(eventType, feature, instanceClass, oldValue, newValue);
    }

    return new CDODeltaNotificationImpl(object, eventType, feature, oldValue, newValue, position);
  }

  private CDODeltaNotificationImpl createPrimitiveNotification(int eventType, EStructuralFeature feature, Class<?> instanceClass, Object oldValue,
      Object newValue)
  {
    switch (ReflectUtil.PrimitiveType.forClass(instanceClass))
    {
    case BOOLEAN:
      return new CDODeltaNotificationImpl(object, eventType, feature, ((Boolean)oldValue).booleanValue(), ((Boolean)newValue).booleanValue());

    case BYTE:
      return new CDODeltaNotificationImpl(object, eventType, feature, ((Number)oldValue).byteValue(), ((Number)newValue).byteValue());

    case CHAR:
      return new CDODeltaNotificationImpl(object, eventType, feature, ((Character)oldValue).charValue(), ((Character)newValue).charValue());

    case SHORT:
      return new CDODeltaNotificationImpl(object, eventType, feature, ((Number)oldValue).shortValue(), ((Number)newValue).shortValue());

    case INT:
      return new CDODeltaNotificationImpl(object, eventType, feature, ((Number)oldValue).intValue(), ((Number)newValue).intValue());

    case LONG:
      return new CDODeltaNotificationImpl(object, eventType, feature, ((Number)oldValue).longValue(), ((Number)newValue).longValue());

    case FLOAT:
      return new CDODeltaNotificationImpl(object, eventType, feature, ((Number)oldValue).floatValue(), ((Number)newValue).floatValue());

    case DOUBLE:
      return new CDODeltaNotificationImpl(object, eventType, feature, ((Number)oldValue).doubleValue(), ((Number)newValue).doubleValue());

    case VOID:
      throw new IllegalArgumentException("Feature of void type not supported: " + feature); //$NON-NLS-1$

    default:
      throw new IllegalArgumentException("Not a primitive type: " + instanceClass); //$NON-NLS-1$
    }
  }

  private void add(CDODeltaNotificationImpl newNotificaton)
  {
    newNotificaton.setRevisionDelta(revisionDelta);
    if (notification.add(newNotificaton))
    {
      int size = notification.size();
      if (size > 1)
      {
        CDODeltaNotificationImpl previousNotification = (CDODeltaNotificationImpl)notification.get(size - 2);

        // Ensure that previousNotification.next is set
        previousNotification.add(newNotificaton);
      }
    }
  }
}
