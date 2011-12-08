/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 212958
 *    Simon McDuff - bug 213402
 *    Caspar De Groot - bug 341081
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.spi.common.revision.BaseCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class CDORevisionImpl extends BaseCDORevision
{
  private Object[] values;

  private transient boolean frozen;

  public CDORevisionImpl(EClass eClass)
  {
    super(eClass);
  }

  protected CDORevisionImpl(CDORevisionImpl source)
  {
    super(source);
    EStructuralFeature[] features = CDOModelUtil.getAllPersistentFeatures(getEClass());
    initValues(features);
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      EClassifier classifier = feature.getEType();
      if (feature.isMany())
      {
        InternalCDOList sourceList = (InternalCDOList)source.values[i];
        if (sourceList != null)
        {
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
  protected Object getValue(int featureIndex)
  {
    return values[featureIndex];
  }

  @Override
  protected void setValue(int featureIndex, Object value)
  {
    checkFrozen(featureIndex, value);
    values[featureIndex] = value;
  }

  public void freeze()
  {
    if (getEClass().getName().equals("Node"))
    {
      System.out.println();
    }

    frozen = true;

    EStructuralFeature[] features = CDOModelUtil.getAllPersistentFeatures(getEClass());
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature.isMany())
      {
        InternalCDOList list = (InternalCDOList)values[i];
        if (list != null)
        {
          list.freeze();
        }
      }
    }
  }

  private void checkFrozen(int featureIndex, Object value)
  {
    if (frozen)
    {
      Object oldValue = values[featureIndex];

      // Exception 1: Setting an empty list as the value for an isMany feature, is
      // allowed if the old value is null. This is a case of lazy initialization.
      boolean newIsEmptyList = value instanceof EList<?> && ((EList<?>)value).size() == 0;
      if (newIsEmptyList && oldValue == null)
      {
        return;
      }

      // Exception 2a: Replacing a temp ID with a regular ID is allowed (happens during
      // postCommit of new objects)
      // Exception 2b: Replacing a temp ID with another temp ID is also allowed (happens
      // when changes are imported in a PushTx).
      if (oldValue instanceof CDOIDTemp && value instanceof CDOID)
      {
        return;
      }

      throw new IllegalStateException("Cannot modify a frozen revision");
    }
  }
}
