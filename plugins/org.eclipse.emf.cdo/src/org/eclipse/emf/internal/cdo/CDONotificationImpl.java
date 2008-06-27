/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * @author Simon McDuff
 */
public class CDONotificationImpl extends ENotificationImpl implements CDONotification
{
  private CDORevisionDelta revisionDelta = null;
  

  public CDONotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue,
      Object newValue, boolean isSetChange)
  {
    super(notifier, eventType, feature, oldValue, newValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue,
      Object newValue, int position, boolean wasSet)
  {
    super(notifier, eventType, feature, oldValue, newValue, position, wasSet);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue,
      Object newValue, int position)
  {
    super(notifier, eventType, feature, oldValue, newValue, position);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue,
      Object newValue)
  {
    super(notifier, eventType, feature, oldValue, newValue);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, boolean oldBooleanValue,
      boolean newBooleanValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldBooleanValue, newBooleanValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, boolean oldBooleanValue,
      boolean newBooleanValue)
  {
    super(notifier, eventType, featureID, oldBooleanValue, newBooleanValue);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, byte oldByteValue,
      byte newByteValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldByteValue, newByteValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, byte oldByteValue,
      byte newByteValue)
  {
    super(notifier, eventType, featureID, oldByteValue, newByteValue);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, char oldCharValue,
      char newCharValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldCharValue, newCharValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, char oldCharValue,
      char newCharValue)
  {
    super(notifier, eventType, featureID, oldCharValue, newCharValue);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, double oldDoubleValue,
      double newDoubleValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldDoubleValue, newDoubleValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, double oldDoubleValue,
      double newDoubleValue)
  {
    super(notifier, eventType, featureID, oldDoubleValue, newDoubleValue);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, float oldFloatValue,
      float newFloatValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldFloatValue, newFloatValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, float oldFloatValue,
      float newFloatValue)
  {
    super(notifier, eventType, featureID, oldFloatValue, newFloatValue);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, int oldIntValue, int newIntValue,
      boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldIntValue, newIntValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, int oldIntValue, int newIntValue)
  {
    super(notifier, eventType, featureID, oldIntValue, newIntValue);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, long oldLongValue,
      long newLongValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldLongValue, newLongValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, long oldLongValue,
      long newLongValue)
  {
    super(notifier, eventType, featureID, oldLongValue, newLongValue);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue, Object newValue,
      boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldValue, newValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue, Object newValue,
      int position, boolean wasSet)
  {
    super(notifier, eventType, featureID, oldValue, newValue, position, wasSet);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue, Object newValue,
      int position)
  {
    super(notifier, eventType, featureID, oldValue, newValue, position);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue, Object newValue)
  {
    super(notifier, eventType, featureID, oldValue, newValue);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, short oldShortValue,
      short newShortValue, boolean isSetChange)
  {
    super(notifier, eventType, featureID, oldShortValue, newShortValue, isSetChange);
  }

  public CDONotificationImpl(InternalEObject notifier, int eventType, int featureID, short oldShortValue,
      short newShortValue)
  {
    super(notifier, eventType, featureID, oldShortValue, newShortValue);
  }
  private InternalCDOObject getCDOObject()
  {
    return (InternalCDOObject) getNotifier();
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
    return adapt(super.getOldValue());
  }

  public Object adapt(Object object)
  {
    if (object instanceof CDOID)
    {
      object = getCDOObject().cdoView().getObject((CDOID)object, true);
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
  
}
