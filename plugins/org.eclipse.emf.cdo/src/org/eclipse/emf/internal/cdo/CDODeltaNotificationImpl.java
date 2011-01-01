/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDODeltaNotificationImpl extends ENotificationImpl implements CDODeltaNotification
{
  private CDORevisionDelta revisionDelta;

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue,
      Object newValue, boolean isSetChange)
  {
    super(notifier, eventType, feature, oldValue, newValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue,
      Object newValue, int position, boolean wasSet)
  {
    super(notifier, eventType, feature, oldValue, newValue, position, wasSet);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue,
      Object newValue, int position)
  {
    super(notifier, eventType, feature, oldValue, newValue, position);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue,
      Object newValue)
  {
    super(notifier, eventType, feature, oldValue, newValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, boolean oldBooleanValue,
      boolean newBooleanValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldBooleanValue, newBooleanValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, boolean oldBooleanValue,
      boolean newBooleanValue)
  {
    super(notifier, eventType, featureID, oldBooleanValue, newBooleanValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, byte oldByteValue,
      byte newByteValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldByteValue, newByteValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, byte oldByteValue,
      byte newByteValue)
  {
    super(notifier, eventType, featureID, oldByteValue, newByteValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, char oldCharValue,
      char newCharValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldCharValue, newCharValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, char oldCharValue,
      char newCharValue)
  {
    super(notifier, eventType, featureID, oldCharValue, newCharValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, double oldDoubleValue,
      double newDoubleValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldDoubleValue, newDoubleValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, double oldDoubleValue,
      double newDoubleValue)
  {
    super(notifier, eventType, featureID, oldDoubleValue, newDoubleValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, float oldFloatValue,
      float newFloatValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldFloatValue, newFloatValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, float oldFloatValue,
      float newFloatValue)
  {
    super(notifier, eventType, featureID, oldFloatValue, newFloatValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, int oldIntValue,
      int newIntValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldIntValue, newIntValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, int oldIntValue,
      int newIntValue)
  {
    super(notifier, eventType, featureID, oldIntValue, newIntValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, long oldLongValue,
      long newLongValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldLongValue, newLongValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, long oldLongValue,
      long newLongValue)
  {
    super(notifier, eventType, featureID, oldLongValue, newLongValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
      Object newValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldValue, newValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
      Object newValue, int position, boolean wasSet)
  {
    super(notifier, eventType, featureID, oldValue, newValue, position, wasSet);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
      Object newValue, int position)
  {
    super(notifier, eventType, featureID, oldValue, newValue, position);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
      Object newValue)
  {
    super(notifier, eventType, featureID, oldValue, newValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, short oldShortValue,
      short newShortValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldShortValue, newShortValue, isSetChange);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, short oldShortValue,
      short newShortValue)
  {
    super(notifier, eventType, featureID, oldShortValue, newShortValue);
  }

  private InternalCDOObject getCDOObject()
  {
    return (InternalCDOObject)getNotifier();
  }

  @Override
  public Object getNewValue()
  {
    Object object = super.getNewValue();
    return adapt(object);
  }

  @Override
  public Object getOldValue()
  {
    Object oldValue = super.getOldValue();
    if (oldValue == null && getEventType() == Notification.REMOVE_MANY)
    {
      Object feature = getFeature();
      if (feature instanceof EStructuralFeature)
      {
        EStructuralFeature structuralFeature = (EStructuralFeature)feature;
        if (structuralFeature.isMany())
        {
          return ECollections.emptyEList();
        }
      }
    }

    return adapt(oldValue);
  }

  public Object adapt(Object object)
  {
    if (object instanceof CDOID)
    {
      CDOID id = (CDOID)object;

      try
      {
        InternalCDOView view = getCDOObject().cdoView();
        object = view.getObject(id, true);
      }
      catch (ObjectNotFoundException ex)
      {
        // Do nothing
      }
    }

    return object;
  }

  public boolean hasNext()
  {
    return next != null;
  }

  public CDORevisionDelta getRevisionDelta()
  {
    return revisionDelta;
  }

  public void setRevisionDelta(CDORevisionDelta revisionDelta)
  {
    this.revisionDelta = revisionDelta;
  }

  @Override
  public boolean merge(Notification notification)
  {
    // Do not merge at all. See bug 317144.
    return false;

    // if (eventType == REMOVE_MANY && newValue == null)
    // {
    // // Means that clear all was executed and no merging can appear
    // return false;
    // }
    //
    // return super.merge(notification);
  }
}
