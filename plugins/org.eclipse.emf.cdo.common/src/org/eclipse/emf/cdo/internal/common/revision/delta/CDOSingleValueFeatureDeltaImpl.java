/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public abstract class CDOSingleValueFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOFeatureDelta
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
    newValue = in.readCDOFeatureValue(getFeature());
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    super.write(out, eClass);
    out.writeInt(index);
    Object valueToWrite = newValue;
    EStructuralFeature feature = getFeature();
    if (valueToWrite != null && feature instanceof EReference)
    {
      valueToWrite = out.getIDProvider().provideCDOID(newValue);
    }

    out.writeCDOFeatureValue(valueToWrite, feature);
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
}
