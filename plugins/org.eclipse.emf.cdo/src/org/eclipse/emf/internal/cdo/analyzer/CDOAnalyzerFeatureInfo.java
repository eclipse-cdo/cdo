/*
 * Copyright (c) 2007-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.common.util.CDOFetchRule;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public final class CDOAnalyzerFeatureInfo
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOAnalyzerFeatureInfo.class);

  private final Map<CDOFetchFeatureInfo, CDOFetchFeatureInfo> featureStats = new HashMap<>();

  private final Map<EClass, CDOFetchRule> fetchRules = new HashMap<>();

  public CDOAnalyzerFeatureInfo()
  {
  }

  public Collection<CDOFetchRule> getFetchRules(EClass eClass, EStructuralFeature feature)
  {
    return fetchRules.values();
  }

  public synchronized CDOFetchFeatureInfo getFeatureInfo(EClass eClass, EStructuralFeature feature)
  {
    CDOFetchFeatureInfo search = new CDOFetchFeatureInfo(eClass, feature);

    CDOFetchFeatureInfo featureRule = featureStats.get(search);
    if (featureRule != null)
    {
      return featureRule;
    }

    featureStats.put(search, search);
    return search;
  }

  public synchronized boolean isActive(EClass eClass, EStructuralFeature feature)
  {
    CDOFetchFeatureInfo search = new CDOFetchFeatureInfo(eClass, feature);
    CDOFetchFeatureInfo featureRule = featureStats.get(search);
    return featureRule != null && featureRule.isActive();
  }

  public synchronized void activate(EClass eClass, EStructuralFeature feature)
  {
    CDOFetchFeatureInfo info = getFeatureInfo(eClass, feature);
    if (!info.isActive())
    {
      info.setActive(true);
      addFetchRule(eClass, feature);
    }
  }

  public synchronized void deactivate(EClass eClass, EStructuralFeature feature)
  {
    CDOFetchFeatureInfo info = getFeatureInfo(eClass, feature);
    if (info.isActive())
    {
      info.setActive(false);
      removeFetchRule(eClass, feature);
    }
  }

  private void addFetchRule(EClass eClass, EStructuralFeature feature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding fetch rule: {0}.{1}", eClass.getName(), feature.getName()); //$NON-NLS-1$
    }

    fetchRules.computeIfAbsent(eClass, CDOFetchRule::new).addFeature(feature);
  }

  private void removeFetchRule(EClass eClass, EStructuralFeature feature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Removing fetch rule: {0}.{1}", eClass.getName(), feature.getName()); //$NON-NLS-1$
    }

    CDOFetchRule fetchRule = fetchRules.get(eClass);
    if (fetchRule != null)
    {
      fetchRule.removeFeature(feature);
    }
  }
}
