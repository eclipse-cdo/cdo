/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.cdo.internal.protocol.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CDOClusterOfFetchRule
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOClusterOfFetchRule.class);

  private Map<CDOFeature, CDOFetchFeatureInfo> featureStats = new HashMap<CDOFeature, CDOFetchFeatureInfo>();

  private Map<CDOClass, CDOFetchRule> fetchRules = new HashMap<CDOClass, CDOFetchRule>();

  private CDOFeature rootFeature;

  private long lastUpdate;

  public CDOClusterOfFetchRule(CDOFeature rootFeature)
  {
    this.rootFeature = rootFeature;
    lastUpdate = System.currentTimeMillis();
  }

  public boolean isActive()
  {
    return !fetchRules.isEmpty();
  }

  public Iterator<CDOFetchRule> iterator()
  {
    return fetchRules.values().iterator();
  }

  public synchronized CDOFetchFeatureInfo getFeatureStat(CDOFeature cdoFeature)
  {
    CDOFetchFeatureInfo featureRule = featureStats.get(cdoFeature);
    if (featureRule == null)
    {
      featureRule = new CDOFetchFeatureInfo();
      featureStats.put(cdoFeature, featureRule);
    }

    return featureRule;
  }

  public synchronized CDOFetchRule addFeatureRule(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding new fetch rule : {0}.{1} from root feature {2}", cdoClass.getName(), cdoFeature.getName(),
          rootFeature);
    }

    lastUpdate = System.currentTimeMillis();
    CDOFetchRule fetchRule = getFetchRule(cdoClass);
    fetchRule.addFeature(cdoFeature);
    return fetchRule;
  }

  public synchronized CDOFetchRule removeFeatureRule(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Removing fetch rule : {0}.{1} from root feature {2}", cdoClass.getName(), cdoFeature.getName(),
          rootFeature);
    }

    lastUpdate = System.currentTimeMillis();

    CDOFetchRule fetchRule = getFetchRule(cdoClass);
    fetchRule.removeFeature(cdoFeature);
    if (fetchRule.isEmpty())
    {
      fetchRules.remove(cdoClass);
    }

    return fetchRule;
  }

  public synchronized CDOFetchRule getFetchRule(CDOClass cdoFeature)
  {
    CDOFetchRule featureRule = fetchRules.get(cdoFeature);
    if (featureRule == null)
    {
      featureRule = new CDOFetchRule(cdoFeature);
      fetchRules.put(cdoFeature, featureRule);
    }

    return featureRule;
  }

  public long getLastUpdate()
  {
    return lastUpdate;
  }

  public CDOFeature getRootFeature()
  {
    return rootFeature;
  }
}
