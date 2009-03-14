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

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public abstract class CDOFeatureDeltaImpl implements InternalCDOFeatureDelta
{
  public static final int NO_INDEX = -1;

  private EStructuralFeature feature;

  protected CDOFeatureDeltaImpl(EStructuralFeature feature)
  {
    this.feature = feature;
  }

  public CDOFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    int featureID = in.readInt();
    feature = eClass.getEStructuralFeature(featureID);
  }

  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    out.writeInt(getType().ordinal());
    out.writeInt(eClass.getFeatureID(feature));
  }

  public EStructuralFeature getFeature()
  {
    return feature;
  }

  public CDOFeatureDelta copy()
  {
    return this;
  }

  public abstract void adjustReferences(CDOReferenceAdjuster referenceAdjuster);
}
