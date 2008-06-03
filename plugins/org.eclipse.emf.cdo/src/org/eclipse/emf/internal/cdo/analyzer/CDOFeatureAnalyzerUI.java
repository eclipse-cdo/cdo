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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;

import org.eclipse.emf.internal.cdo.InternalCDOObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class CDOFeatureAnalyzerUI extends CDOAbstractFeatureRuleAnalyzer
{
  private static final long ELAPSE_TIME = 400L;

  private Map<CDOClusterOfFetchRule, CDOClusterOfFetchRule> featureRules = new HashMap<CDOClusterOfFetchRule, CDOClusterOfFetchRule>();

  private CDOClusterOfFetchRule currentClusterOfFetchRule;

  private long maxTimeBetweenOperation;

  public CDOFeatureAnalyzerUI()
  {
    this(ELAPSE_TIME);
  }

  public CDOFeatureAnalyzerUI(long maxTimeBetweenOperation)
  {
    this.maxTimeBetweenOperation = maxTimeBetweenOperation;
  }

  public CDOID getContext()
  {
    if (lastTraverseFeature.isMany())
    {
      return CDOID.NULL;
    }

    return lastTraverseCDOObject.cdoID();
  }

  public synchronized CDOClusterOfFetchRule getFeatureRule(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    CDOClusterOfFetchRule search = new CDOClusterOfFetchRule(cdoClass, cdoFeature);
    CDOClusterOfFetchRule featureRule = featureRules.get(search);
    if (featureRule == null)
    {
      featureRule = search;
      featureRules.put(search, featureRule);
    }

    return featureRule;
  }

  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
  {
    boolean addRootFeature = true;
    fetchData();

    if (lastTraverseFeature.isMany())
    {
      addRootFeature = false;
    }

    CDOClusterOfFetchRule search = new CDOClusterOfFetchRule(lastTraverseCDOObject.cdoClass(), lastTraverseFeature);
    CDOClusterOfFetchRule fetchOfRule = featureRules.get(search);
    if (fetchOfRule == null)
    {
      return null;
    }

    Collection<CDOFetchRule> fetchRules = fetchOfRule.getFeatureInfo().getRules(null, null);
    List<CDOFetchRule> list = new ArrayList<CDOFetchRule>();
    for (CDOFetchRule fetchRule : fetchRules)
    {
      if (addRootFeature == true || lastTraverseCDOObject.cdoClass() != fetchRule.getCDOClass())
      {
        list.add(fetchRule);
      }
    }

    return list;
  }

  @Override
  protected void doPreTraverseFeature(InternalCDOObject cdoObject, CDOFeature feature, int index)
  {
    // Don`t handle containment relationship
    if (!feature.isReference())
    {
      return;
    }

    if (lastElapseTimeBetweenOperations > maxTimeBetweenOperation || currentClusterOfFetchRule == null)
    {
      // The user interacted with the UI. Restart a new ClusterOfFetchRule
      currentClusterOfFetchRule = getFeatureRule(cdoObject.cdoClass(), feature);
    }
  }

  @Override
  protected void doPostTraverseFeature(InternalCDOObject cdoObject, CDOFeature feature, int index, Object object)
  {
    if (didFetch())
    {
      currentClusterOfFetchRule.getFeatureInfo().activate(cdoObject.cdoClass(), feature);
    }
  }
}
