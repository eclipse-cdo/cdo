/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/

package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.common.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class CDOAnalyzerFeatureInfo
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOAnalyzerFeatureInfo.class);

  private Map<CDOFetchFeatureInfo, CDOFetchFeatureInfo> featureStats = new HashMap<CDOFetchFeatureInfo, CDOFetchFeatureInfo>();

  private Map<CDOClass, CDOFetchRule> fetchRules = new HashMap<CDOClass, CDOFetchRule>();

  public CDOAnalyzerFeatureInfo()
  {
  }

  public Collection<CDOFetchRule> getRules(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    return fetchRules.values();
  }

  public synchronized CDOFetchFeatureInfo getFeatureStat(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    CDOFetchFeatureInfo search = new CDOFetchFeatureInfo(cdoClass, cdoFeature);
    CDOFetchFeatureInfo featureRule = featureStats.get(search);
    if (featureRule == null)
    {
      featureRule = search;
      featureStats.put(search, featureRule);
    }

    return featureRule;
  }

  public boolean isActive(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    CDOFetchFeatureInfo search = new CDOFetchFeatureInfo(cdoClass, cdoFeature);
    CDOFetchFeatureInfo featureRule = featureStats.get(search);
    return featureRule != null && featureRule.isActive();
  }

  public void activate(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    CDOFetchFeatureInfo info = getFeatureStat(cdoClass, cdoFeature);
    if (!info.isActive())
    {
      info.setActive(true);
      addRule(cdoClass, cdoFeature);
    }
  }

  public void deactivate(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    CDOFetchFeatureInfo info = getFeatureStat(cdoClass, cdoFeature);
    if (info.isActive())
    {
      info.setActive(false);
      removeRule(cdoClass, cdoFeature);
    }
  }

  private void addRule(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding rule : {0}.{1}", cdoClass.getName(), cdoFeature.getName());
    }

    CDOFetchRule fetchRule = fetchRules.get(cdoClass);
    if (fetchRule == null)
    {
      fetchRule = new CDOFetchRule(cdoClass);
      fetchRules.put(cdoClass, fetchRule);
    }

    fetchRule.addFeature(cdoFeature);
  }

  private void removeRule(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Removing rule : {0}.{1}", cdoClass.getName(), cdoFeature.getName());
    }

    CDOFetchRule fetchRule = fetchRules.get(cdoClass);
    if (fetchRule == null)
    {
      return;
    }

    fetchRule.removeFeature(cdoFeature);
  }
}
