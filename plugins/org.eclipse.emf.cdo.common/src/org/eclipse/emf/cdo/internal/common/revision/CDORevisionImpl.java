/*
 * Copyright (c) 2008-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 212958
 *    Simon McDuff - bug 213402
 *    Caspar De Groot - bug 341081
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.spi.common.revision.BaseCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class CDORevisionImpl extends BaseCDORevision
{
  private Object[] values;

  public CDORevisionImpl(EClass eClass)
  {
    super(eClass);
  }

  protected CDORevisionImpl(CDORevisionImpl source)
  {
    super(source);
    boolean bypassPermissionChecks = bypassPermissionChecks(true);

    try
    {
      EStructuralFeature[] features = clearValues();

      int length = features.length;
      for (int i = 0; i < length; i++)
      {
        EStructuralFeature feature = features[i];
        if (feature.isMany())
        {
          InternalCDOList sourceList = (InternalCDOList)source.values[i];
          if (sourceList != null)
          {
            EClassifier classifier = feature.getEType();
            setValue(i, sourceList.clone(classifier));
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature);
          setValue(i, type.copyValue(source.values[i]));
        }
      }
    }
    finally
    {
      bypassPermissionChecks(bypassPermissionChecks);
    }
  }

  @Override
  public InternalCDORevision copy()
  {
    return new CDORevisionImpl(this);
  }

  @Override
  protected void initValues(EStructuralFeature[] allPersistentFeatures)
  {
    values = new Object[allPersistentFeatures.length];
  }

  @Override
  protected Object doGetValue(int featureIndex)
  {
    if (values != null) // Can be null if READ permission is missing
    {
      return values[featureIndex];
    }

    return null;
  }

  @Override
  protected void doSetValue(int featureIndex, Object value)
  {
    if (values != null) // Can be null if READ permission is missing
    {
      values[featureIndex] = value;
    }
  }
}
