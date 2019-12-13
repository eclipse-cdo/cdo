/*
 * Copyright (c) 2011, 2012, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOInvalidationNotification;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Simon McDuff
 */
public class CDOInvalidationNotificationImpl implements CDOInvalidationNotification
{
  private EObject eObject;

  public CDOInvalidationNotificationImpl(EObject eObject)
  {
    this.eObject = CDOUtil.getEObject(eObject);
  }

  @Override
  public Object getNotifier()
  {
    return eObject;
  }

  @Override
  public int getEventType()
  {
    return INVALIDATE;
  }

  @Override
  public Object getFeature()
  {
    return null;
  }

  @Override
  public int getFeatureID(Class<?> expectedClass)
  {
    return NO_FEATURE_ID;
  }

  @Override
  public int getPosition()
  {
    return NO_INDEX;
  }

  @Override
  public boolean wasSet()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isReset()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isTouch()
  {
    return false;
  }

  @Override
  public boolean merge(Notification notification)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean getNewBooleanValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte getNewByteValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public char getNewCharValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public double getNewDoubleValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float getNewFloatValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getNewIntValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getNewLongValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public short getNewShortValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getNewStringValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getNewValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean getOldBooleanValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte getOldByteValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public char getOldCharValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public double getOldDoubleValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float getOldFloatValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getOldIntValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getOldLongValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public short getOldShortValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getOldStringValue()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getOldValue()
  {
    throw new UnsupportedOperationException();
  }
}
