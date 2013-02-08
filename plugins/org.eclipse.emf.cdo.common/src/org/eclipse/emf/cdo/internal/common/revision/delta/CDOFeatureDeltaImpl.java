/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta;

import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Simon McDuff
 */
public abstract class CDOFeatureDeltaImpl implements InternalCDOFeatureDelta
{
  private EStructuralFeature feature;

  protected CDOFeatureDeltaImpl(EStructuralFeature feature)
  {
    CheckUtil.checkArg(feature, "feature");
    this.feature = feature;
  }

  public CDOFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    int featureID = in.readInt();
    feature = eClass.getEStructuralFeature(featureID);
    CheckUtil.checkState(feature, "feature");
  }

  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    out.writeInt(getType().ordinal());
    out.writeInt(eClass.getFeatureID(feature));
  }

  protected void writeValue(CDODataOutput out, EClass eClass, Object value) throws IOException
  {
    Object valueToWrite = value;

    EStructuralFeature feature = getFeature();
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      FeatureMap.Entry entry = (Entry)valueToWrite;
      feature = entry.getEStructuralFeature();
      valueToWrite = entry.getValue();

      int featureID = eClass.getFeatureID(feature);
      out.writeInt(featureID);
    }

    if (valueToWrite == UNKNOWN_VALUE)
    {
      out.writeBoolean(false);
    }
    else
    {
      out.writeBoolean(true);
      if (valueToWrite != null && feature instanceof EReference)
      {
        valueToWrite = out.getIDProvider().provideCDOID(value);
      }

      out.writeCDOFeatureValue(feature, valueToWrite);
    }
  }

  protected Object readValue(CDODataInput in, EClass eClass) throws IOException
  {
    EStructuralFeature feature = getFeature();
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      int featureID = in.readInt();
      feature = eClass.getEStructuralFeature(featureID);
      Object innerValue = in.readCDOFeatureValue(feature);
      return CDORevisionUtil.createFeatureMapEntry(feature, innerValue);
    }

    if (in.readBoolean())
    {
      return in.readCDOFeatureValue(feature);
    }

    return UNKNOWN_VALUE;
  }

  public EStructuralFeature getFeature()
  {
    return feature;
  }

  public boolean isStructurallyEqual(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOFeatureDelta)
    {
      CDOFeatureDelta that = (CDOFeatureDelta)obj;
      return feature.equals(that.getFeature()) && getType().equals(that.getType());
    }

    return false;
  }

  @Override
  public String toString()
  {
    String additional = toStringAdditional();
    if (additional == null)
    {
      return MessageFormat.format("CDOFeatureDelta[{0}, {1}]", feature.getName(), getType());
    }

    return MessageFormat.format("CDOFeatureDelta[{0}, {1}, {2}]", feature.getName(), getType(), additional);
  }

  public abstract boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster);

  protected abstract String toStringAdditional();
}
