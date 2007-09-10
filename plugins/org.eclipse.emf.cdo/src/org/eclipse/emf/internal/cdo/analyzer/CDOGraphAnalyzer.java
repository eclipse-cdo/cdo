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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CDOGraphAnalyzer
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOGraphAnalyzer.class);

  private Map<CDOID, CDOGraph> idToGraphMap = new HashMap<CDOID, CDOGraph>();

  private HashSet<CDOGraph> uniqueGraphs = new HashSet<CDOGraph>();

  private long lastPurgeOperation;

  public CDOGraphAnalyzer()
  {
    lastPurgeOperation = System.currentTimeMillis();
  }

  public CDOGraph getGraph(CDOID fromID, CDOClusterOfFetchRule featureRule)
  {
    CDOGraph graph = idToGraphMap.get(fromID);
    if (graph == null)
    {
      graph = new CDOGraph(this, fromID, featureRule);
      uniqueGraphs.add(graph);
    }

    // Update is time value
    graph.update();
    return graph;
  }

  public void purge(long elapseTimeForPurging, long elapseTimeToClear)
  {
    long time = System.currentTimeMillis();
    if (time - lastPurgeOperation > elapseTimeForPurging)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Purging graph {0}", this);
      }

      ArrayList<CDOGraph> listToRemove = new ArrayList<CDOGraph>();
      for (CDOGraph graph : uniqueGraphs)
      {
        if (isTimeToRemove(graph, time, elapseTimeToClear))
        {
          listToRemove.add(graph);
        }
      }

      for (CDOGraph graph : listToRemove)
      {
        graph.remove();
      }

      lastPurgeOperation = System.currentTimeMillis();
    }
  }

  public boolean isTimeToRemove(CDOGraph graph, long currentTimestamp, long elapseTimeToClear)
  {
    return currentTimestamp - graph.getLastUpdated() > elapseTimeToClear
        || currentTimestamp - graph.getFeatureRule().getLastUpdate() > elapseTimeToClear
        && currentTimestamp - graph.getCreationTime() > elapseTimeToClear;
  }

  public void addTrackingID(CDOID id, CDOGraph graph)
  {
    idToGraphMap.put(id, graph);
    graph.update();
  }

  public void removeTrackingID(CDOID id)
  {
    idToGraphMap.remove(id);
  }

  public void removeTracking(CDOGraph graph)
  {
    uniqueGraphs.remove(graph);
  }

  public void clear()
  {
    idToGraphMap.clear();
    uniqueGraphs.clear();
  }
}
