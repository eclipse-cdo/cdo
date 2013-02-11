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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class CDOClassInfoImpl extends AdapterImpl implements InternalCDOClassInfo
{
  private static final PersistenceFilter[] NO_FILTERS = {};

  private final BitSet persistentBits = new BitSet();

  private PersistenceFilter[] persistenceFilters = NO_FILTERS;

  private EStructuralFeature[] allPersistentFeatures;

  private EReference[] allPersistentReferences;

  private int[] persistentFeatureIndices;

  private int settingsFeatureCount;

  private int[] settingsFeatureIndices;

  /**
   * The number of *extra* features on top of {@link #settingsFeatureCount} when the object is TRANSIENT.
   */
  private int transientFeatureCount;

  /**
   * This is not about transient features! But about indices of all features of TRANSIENT objects.
   */
  private int[] transientFeatureIndices;

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

  public boolean isPersistent(int featureID)
  {
    return persistentBits.get(featureID);
  }

  public EStructuralFeature[] getAllPersistentFeatures()
  {
    return allPersistentFeatures;
  }

  public EReference[] getAllPersistentReferences()
  {
    return allPersistentReferences;
  }

  public int getPersistentFeatureIndex(EStructuralFeature feature) throws IllegalArgumentException
  {
    int featureID = getEClass().getFeatureID(feature);
    return getPersistentFeatureIndex(featureID);
  }

  public int getPersistentFeatureIndex(int featureID) throws IllegalArgumentException
  {
    int index = persistentFeatureIndices[featureID];
    if (index == NO_SLOT)
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

  public int getTransientFeatureCount()
  {
    return transientFeatureCount;
  }

  public int getTransientFeatureIndex(int featureID)
  {
    return transientFeatureIndices[featureID];
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
    EList<EStructuralFeature> allFeatures = eClass.getEAllStructuralFeatures();
    int featureCount = eClass.getFeatureCount();

    List<EStructuralFeature> persistentFeatures = new ArrayList<EStructuralFeature>();
    List<EReference> persistentReferences = new ArrayList<EReference>();
    persistentBits.clear();
    settingsFeatureIndices = new int[featureCount];
    for (int i = 0; i < featureCount; i++)
    {
      EStructuralFeature feature = eClass.getEStructuralFeature(i);
      if (EMFUtil.isPersistent(feature)) // persistentBits is not initialized, yet
      {
        int featureID = eClass.getFeatureID(feature);
        persistentBits.set(featureID);

        persistentFeatures.add(feature);
        if (feature instanceof EReference)
        {
          persistentReferences.add((EReference)feature);
        }

        if (feature.isMany() || FeatureMapUtil.isFeatureMap(feature))
        {
          settingsFeatureIndices[i] = settingsFeatureCount++;
        }
        else
        {
          settingsFeatureIndices[i] = NO_SLOT;
        }
      }
      else
      {
        settingsFeatureIndices[i] = settingsFeatureCount++;
      }
    }

    transientFeatureIndices = new int[featureCount];
    for (int featureID = 0; featureID < featureCount; featureID++)
    {
      if (isPersistent(featureID))
      {
        transientFeatureIndices[featureID] = settingsFeatureCount + transientFeatureCount++;
      }
      else
      {
        // Transient *features* are already allocated to a slot (see above)
        transientFeatureIndices[featureID] = settingsFeatureIndices[featureID];
      }
    }

    allPersistentFeatures = persistentFeatures.toArray(new EStructuralFeature[persistentFeatures.size()]);
    allPersistentReferences = persistentReferences.toArray(new EReference[persistentReferences.size()]);

    persistentFeatureIndices = new int[allFeatures.size()];
    Arrays.fill(persistentFeatureIndices, NO_SLOT);

    for (int i = 0; i < allPersistentFeatures.length; i++)
    {
      EStructuralFeature feature = allPersistentFeatures[i];
      int featureID = eClass.getFeatureID(feature);
      persistentFeatureIndices[featureID] = i;

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

  @Deprecated
  public int getFeatureIndex(EStructuralFeature feature)
  {
    return getPersistentFeatureIndex(feature);
  }

  @Deprecated
  public int getFeatureIndex(int featureID)
  {
    return getPersistentFeatureIndex(featureID);
  }

  @Override
  public String toString()
  {
    return getEClass().toString();
  }
}
