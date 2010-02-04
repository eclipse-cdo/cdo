/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Simon McDuff
 */
public abstract class CDOSingleValueFeatureDeltaImpl extends CDOFeatureDeltaImpl
{
  private int index;

  private Object newValue;

  public CDOSingleValueFeatureDeltaImpl(EStructuralFeature feature, int index, Object value)
  {
    super(feature);
    this.index = index;
    newValue = value;
  }

  public CDOSingleValueFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
    index = in.readInt();
    EStructuralFeature feature = getFeature();
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      int featureID = in.readInt();
      feature = eClass.getEStructuralFeature(featureID);
      Object innerValue = in.readCDOFeatureValue(feature);
      newValue = CDORevisionUtil.createFeatureMapEntry(feature, innerValue);
    }
    else
    {
      newValue = in.readCDOFeatureValue(feature);
    }
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    super.write(out, eClass);
    out.writeInt(index);
    Object valueToWrite = newValue;
    EStructuralFeature feature = getFeature();
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      FeatureMap.Entry entry = (Entry)valueToWrite;
      feature = entry.getEStructuralFeature();
      valueToWrite = entry.getValue();

      int featureID = eClass.getFeatureID(feature);
      out.writeInt(featureID);
    }

    if (valueToWrite != null && feature instanceof EReference)
    {
      valueToWrite = out.getIDProvider().provideCDOID(newValue);
    }

    out.writeCDOFeatureValue(feature, valueToWrite);
  }

  public int getIndex()
  {
    return index;
  }

  public Object getValue()
  {
    return newValue;
  }

  protected void setValue(Object value)
  {
    newValue = value;
  }

  @Override
  public void adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    newValue = referenceAdjuster.adjustReference(newValue);
  }

  public void clear()
  {
    setValue(CDOID.NULL);
  }

  @Override
  protected String toStringAdditional()
  {
    if (index == Notification.NO_INDEX)
    {
      return MessageFormat.format("value={0}", newValue);
    }

    return MessageFormat.format("value={0}, index={1}", newValue, index);
  }
}
