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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.analyzer.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.analyzer.CDOFetchRuleManager;
import org.eclipse.emf.cdo.internal.protocol.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CDODynamicFetchRuleAnalyzer implements CDOFeatureAnalyzer, CDOFetchRuleManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDODynamicFetchRuleAnalyzer.class);

  private static final long ELAPSE_TIME = 400L;

  private Map<CDOFeature, CDOClusterOfFetchRule> featureRules = new HashMap<CDOFeature, CDOClusterOfFetchRule>();

  private CDOGraphAnalyzer graphAnalyzer = new CDOGraphAnalyzer();

  private CDOGraph currentGraph;

  private long lastAccessTime;

  private boolean inUse;

  private InternalCDOObject lastRevision;

  private CDOView view;

  private boolean doesFetch;

  public CDODynamicFetchRuleAnalyzer(CDOView view)
  {
    this.view = view;
  }

  public CDOClusterOfFetchRule getCurrentFeatureRule()
  {
    return currentGraph.getFeatureRule();
  }

  public CDOGraph getCurrentGraph()
  {
    return currentGraph;
  }

  public void preTraverseFeature(InternalCDOObject cdoObject, CDOFeature feature, int index)
  {
    CDOClass cdoClass = cdoObject.cdoClass();
    doesFetch = false;
    CDODynamicFetchRuleManager.join(this);
    lastRevision = cdoObject;
    if (TRACER.isEnabled())
    {
      TRACER.format("preTraverseFeature : {0}.{1}", cdoClass.getName(), feature.getName());
    }

    // Do not handle these cases
    long currentTime = System.currentTimeMillis();
    long elapseTimeBeforeLastRequest = currentTime - lastAccessTime;
    lastAccessTime = currentTime;

    // Don`t handle containment relationship
    if (!feature.isReference())
    {
      return;
    }

    if (elapseTimeBeforeLastRequest > ELAPSE_TIME)
    {
      graphAnalyzer.clear();
    }

    // Purge old graph that we didn't update for specific elapse time
    graphAnalyzer.purge(ELAPSE_TIME * 2, ELAPSE_TIME * 2);

    CDOClusterOfFetchRule featureRule = getFeatureRule(cdoClass, feature);

    // Get the graph <cdoObject> belongs to
    currentGraph = graphAnalyzer.getGraph(cdoObject.cdoID(), featureRule);
    lastAccessTime = System.currentTimeMillis();

    if (currentGraph.getFeatureRule().getRootFeature() == feature)
    {
      if (graphAnalyzer.isTimeToRemove(currentGraph, lastAccessTime, ELAPSE_TIME))
      {
        // Clean it!!
        currentGraph.clean();
        return;
      }
    }

    // need to calculate currentFeature to calculate the connected graph
    if (feature.isMany() && index != 0)
    {
      return;
    }

    // Get the cluster of rule for that feature
    CDOClusterOfFetchRule currentFeatureRule = currentGraph.getFeatureRule();
    CDOFetchFeatureInfo infoOfFeature = currentFeatureRule.getFeatureStat(feature);
    infoOfFeature.updateTimeInfo(elapseTimeBeforeLastRequest);

    // Detect a new rule
    if (!infoOfFeature.isActive() && infoOfFeature.getTimeBeforeUsed() < ELAPSE_TIME)
    {
      currentFeatureRule.addFeatureRule(cdoClass, feature);
      infoOfFeature.setActive(true);
    }

    // Detect if the rule is still good!
    else if (infoOfFeature.isActive() && infoOfFeature.getTimeBeforeUsed() > ELAPSE_TIME)
    {
      // Unregister rules
      currentFeatureRule.removeFeatureRule(cdoClass, feature);
      infoOfFeature.setActive(false);
    }

    lastAccessTime = currentTime;
    inUse = true;
  }

  public void postTraverseFeature(InternalCDOObject cdoObject, CDOFeature feature, int index, Object object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("postTraverseFeature : {0}.{1}", cdoObject.cdoClass(), feature.getName());
    }

    if (currentGraph != null)
    {
      // Adding return object to the link graph
      InternalCDOObject destObject = FSMUtil.adapt(object, view);
      currentGraph.add(cdoObject.cdoID(), feature, destObject.cdoID());

      // calculate latency time
      if (doesFetch == true)
      {
        CDOClusterOfFetchRule currentFeatureRule = currentGraph.getFeatureRule();
        CDOFetchFeatureInfo infoOfFeature = currentFeatureRule.getFeatureStat(feature);
        infoOfFeature.updateLatencyTime(System.currentTimeMillis() - lastAccessTime);
      }
    }

    lastAccessTime = System.currentTimeMillis();
    inUse = false;
    CDODynamicFetchRuleManager.leave();
  }

  public CDOID getContext()
  {
    return lastRevision.cdoID();
  }

  public synchronized CDOClusterOfFetchRule getFeatureRule(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    CDOClusterOfFetchRule featureRule = featureRules.get(cdoFeature);
    if (featureRule == null)
    {
      featureRule = new CDOClusterOfFetchRule(cdoFeature);
      featureRules.put(cdoFeature, featureRule);
    }

    return featureRule;
  }

  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
  {
    doesFetch = true;
    if (!inUse || currentGraph == null)
    {
      return null;
    }

    List<CDOFetchRule> list = new ArrayList<CDOFetchRule>();
    for (Iterator<CDOFetchRule> it = currentGraph.getFeatureRule().iterator(); it.hasNext();)
    {
      CDOFetchRule fetchRule = it.next();
      list.add(fetchRule);
    }

    return list;
  }
}
