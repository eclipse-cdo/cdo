/*
 * Copyright (c) 2010-2012, 2015, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a subset of the {@link EStructuralFeature features} of a {@link EClass class}.
 *
 * @author Simon McDuff
 * @since 3.0
 */
public final class CDOFetchRule
{
  private final EClass eClass;

  private final List<EStructuralFeature> features = new ArrayList<>(0);

  public CDOFetchRule(EClass eClass)
  {
    this.eClass = eClass;
  }

  public CDOFetchRule(CDODataInput in, CDOPackageRegistry packageManager) throws IOException
  {
    eClass = (EClass)in.readCDOClassifierRefAndResolve();
    int size = in.readXInt();

    for (int i = 0; i < size; i++)
    {
      int featureID = in.readXInt();
      EStructuralFeature feature = eClass.getEStructuralFeature(featureID);
      features.add(feature);
    }
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOClassifierRef(eClass);
    out.writeXInt(features.size());

    for (EStructuralFeature feature : features)
    {
      out.writeXInt(feature.getFeatureID());
    }
  }

  public EClass getEClass()
  {
    return eClass;
  }

  public List<EStructuralFeature> getFeatures()
  {
    return features;
  }

  public void addFeature(EStructuralFeature feature)
  {
    features.add(feature);
  }

  public void removeFeature(EStructuralFeature feature)
  {
    features.remove(feature);
  }

  public boolean isEmpty()
  {
    return features.isEmpty();
  }
}
