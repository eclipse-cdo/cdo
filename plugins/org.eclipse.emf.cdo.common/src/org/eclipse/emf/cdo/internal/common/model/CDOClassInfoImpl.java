/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - support partially persistent features
 */
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClassInfo;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOClassInfoImpl extends AdapterImpl implements InternalCDOClassInfo
{
  private static final int NOT_MAPPED = -1;

  private static final PersistenceFilter[] NO_FILTERS = {};

  private EStructuralFeature[] allPersistentFeatures;

  private PersistenceFilter[] persistenceFilters = NO_FILTERS;

  private int[] featureIDMappings;

  private int settingsFeatureCount;

  private int[] settingsFeatureIndices;

  public CDOClassInfoImpl()
  {
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type == CDOClassInfo.class;
  }

  @Override
  public void setTarget(Notifier newTarget)
  {
    init((EClass)newTarget);
    super.setTarget(newTarget);
  }

  public EClass getEClass()
  {
    return (EClass)getTarget();
  }

  public boolean isResource()
  {
    return CDOModelUtil.isResource(getEClass());
  }

  public boolean isResourceFolder()
  {
    return CDOModelUtil.isResourceFolder(getEClass());
  }

  public boolean isResourceNode()
  {
    return CDOModelUtil.isResourceNode(getEClass());
  }

  public EStructuralFeature[] getAllPersistentFeatures()
  {
    return allPersistentFeatures;
  }

  public int getFeatureIndex(EStructuralFeature feature)
  {
    int featureID = getEClass().getFeatureID(feature);
    return getFeatureIndex(featureID);
  }

  public int getFeatureIndex(int featureID)
  {
    int index = featureIDMappings[featureID];
    if (index == NOT_MAPPED)
    {
      throw new IllegalArgumentException("Feature not mapped: " + getEClass().getEStructuralFeature(featureID)); //$NON-NLS-1$
    }

    return index;
  }

  public int getSettingsFeatureCount()
  {
    return settingsFeatureCount;
  }

  public int getSettingsFeatureIndex(int featureID)
  {
    return settingsFeatureIndices[featureID];
  }

  public PersistenceFilter getPersistenceFilter(EStructuralFeature feature)
  {
    if (persistenceFilters == NO_FILTERS)
    {
      return null;
    }

    int featureID = getEClass().getFeatureID(feature);
    return persistenceFilters[featureID];
  }

  private PersistenceFilter initPersistenceFilter(EStructuralFeature feature)
  {
    CDOPersistenceFilterImpl result = null;
    String filter = EcoreUtil.getAnnotation(feature, EMFUtil.CDO_ANNOTATION_SOURCE, "filter");

    if (filter != null)
    {
      EStructuralFeature dependency = feature.getEContainingClass().getEStructuralFeature(filter);
      if (dependency != null)
      {
        result = new CDOPersistenceFilterImpl(dependency);
      }
      else
      {
        OM.LOG.warn("Persistence filter '" + filter + "' not found for " + feature);
      }
    }

    return result;
  }

  private void init(EClass eClass)
  {
    List<EStructuralFeature> persistentFeatures = new ArrayList<EStructuralFeature>();
    EList<EStructuralFeature> allFeatures = eClass.getEAllStructuralFeatures();

    int featureCount = eClass.getFeatureCount();
    settingsFeatureIndices = new int[featureCount];
    for (int i = 0; i < featureCount; i++)
    {
      EStructuralFeature feature = eClass.getEStructuralFeature(i);
      if (EMFUtil.isPersistent(feature))
      {
        persistentFeatures.add(feature);
        if (feature.isMany() || FeatureMapUtil.isFeatureMap(feature))
        {
          settingsFeatureIndices[i] = settingsFeatureCount++;
        }
        else
        {
          settingsFeatureIndices[i] = NO_SETTING;
        }
      }
      else
      {
        settingsFeatureIndices[i] = settingsFeatureCount++;
      }
    }

    allPersistentFeatures = persistentFeatures.toArray(new EStructuralFeature[persistentFeatures.size()]);
    featureIDMappings = new int[allFeatures.size()];
    Arrays.fill(featureIDMappings, NOT_MAPPED);

    for (int i = 0; i < allPersistentFeatures.length; i++)
    {
      EStructuralFeature feature = allPersistentFeatures[i];
      int featureID = eClass.getFeatureID(feature);
      featureIDMappings[featureID] = i;

      PersistenceFilter persistenceFilter = initPersistenceFilter(feature);
      if (persistenceFilter != null)
      {
        if (persistenceFilters == NO_FILTERS)
        {
          persistenceFilters = new PersistenceFilter[allFeatures.size()];
        }

        persistenceFilters[featureID] = persistenceFilter;
      }
    }
  }

  @Override
  public String toString()
  {
    return getEClass().toString();
  }
}
