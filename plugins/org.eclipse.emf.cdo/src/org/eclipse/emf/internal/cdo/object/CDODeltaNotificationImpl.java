/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDODeltaNotificationImpl extends ENotificationImpl implements CDODeltaNotification
{
  private CDORevisionDelta revisionDelta;

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue, Object newValue)
  {
    super(getEObject(notifier), eventType, feature, oldValue, newValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue, Object newValue, int position)
  {
    super(getEObject(notifier), eventType, feature, oldValue, newValue, position);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, boolean oldBooleanValue, boolean newBooleanValue)
  {
    super(getEObject(notifier), eventType, feature.getFeatureID(), oldBooleanValue, newBooleanValue);
    this.feature = feature;
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, byte oldByteValue, byte newByteValue)
  {
    super(getEObject(notifier), eventType, feature.getFeatureID(), oldByteValue, newByteValue);
    this.feature = feature;
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, char oldCharValue, char newCharValue)
  {
    super(getEObject(notifier), eventType, feature.getFeatureID(), oldCharValue, newCharValue);
    this.feature = feature;
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, double oldDoubleValue, double newDoubleValue)
  {
    super(getEObject(notifier), eventType, feature.getFeatureID(), oldDoubleValue, newDoubleValue);
    this.feature = feature;
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, float oldFloatValue, float newFloatValue)
  {
    super(getEObject(notifier), eventType, feature.getFeatureID(), oldFloatValue, newFloatValue);
    this.feature = feature;
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, int oldIntValue, int newIntValue)
  {
    super(getEObject(notifier), eventType, feature.getFeatureID(), oldIntValue, newIntValue);
    this.feature = feature;
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, long oldLongValue, long newLongValue)
  {
    super(getEObject(notifier), eventType, feature.getFeatureID(), oldLongValue, newLongValue);
    this.feature = feature;
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, short oldShortValue, short newShortValue)
  {
    super(getEObject(notifier), eventType, feature.getFeatureID(), oldShortValue, newShortValue);
    this.feature = feature;
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

      if (id instanceof CDOIDExternal)
      {
        CDOIDExternal idExternal = (CDOIDExternal)id;
        Resource resource = notifier.eResource();
        if (resource != null)
        {
          String uriString = idExternal.getURI();
          URI uri = URI.createURI(uriString);
          object = resource.getResourceSet().getEObject(uri, false);
        }
      }
      else
      {
        try
        {
          InternalCDOView view = getCDOObject().cdoView();
          object = view.getObject(id, true);
        }
        catch (ObjectNotFoundException ex)
        {
          object = null;
        }
      }
    }

    if (object instanceof CDOObject)
    {
      object = CDOUtil.getEObject((EObject)object);
    }

    if (feature instanceof EAttribute)
    {
      if (object == null)
      {
        object = feature.getDefaultValue();
      }
      else
      {
        EDataType eAttributeType = ((EAttribute)feature).getEAttributeType();
        if (eAttributeType != null)
        {
          if (eAttributeType instanceof EEnum && object instanceof Integer)
          {
            EEnumLiteral literal = ((EEnum)eAttributeType).getEEnumLiteral((Integer)object);
            String stringValue = literal.getLiteral();
            object = eAttributeType.getEPackage().getEFactoryInstance().createFromString(eAttributeType, stringValue);
          }
        }
      }
    }

    return object;
  }

  @Override
  public boolean hasNext()
  {
    return next != null;
  }

  @Override
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
  }

  private InternalCDOObject getCDOObject()
  {
    return (InternalCDOObject)CDOUtil.getCDOObject((EObject)getNotifier());
  }

  private static InternalEObject getEObject(InternalEObject notifier)
  {
    return (InternalEObject)CDOUtil.getEObject(notifier);
  }
}
