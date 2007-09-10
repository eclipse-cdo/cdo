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
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CDOGraph
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOGraph.class);

  private CDOGraphAnalyzer analyzer;

  private CDOClusterOfFetchRule featureRule;

  private List<CDOID> clusterOfObjects = new ArrayList<CDOID>();

  private long lastUpdated;

  private long creationTime;

  public CDOGraph(CDOGraphAnalyzer analyzer, CDOID rootID, CDOClusterOfFetchRule featureRule)
  {
    this.analyzer = analyzer;
    this.featureRule = featureRule;
    // Adding the root element
    add(rootID, null, rootID);
    creationTime = System.currentTimeMillis();
  }

  public List<CDOID> getObjects()
  {
    return clusterOfObjects;
  }

  public CDOClusterOfFetchRule getFeatureRule()
  {
    return featureRule;
  }

  public long getLastUpdated()
  {
    return lastUpdated;
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public CDOID getRoot()
  {
    return getObjects().get(0);
  }

  public void clean()
  {
    Iterator<CDOID> itr = clusterOfObjects.iterator();
    itr.next(); // Skip the Root!
    while (itr.hasNext())
    {
      analyzer.removeTrackingID(itr.next());
    }

    creationTime = System.currentTimeMillis();
  }

  public void update()
  {
    lastUpdated = System.currentTimeMillis();
  }

  public void add(CDOID fromID, CDOFeature feature, CDOID id)
  {
    // For now .. we do not need to calculate the path.. later we will do!!
    clusterOfObjects.add(id);
    analyzer.addTrackingID(id, this);
  }

  public void remove()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Removing CDOGraph {0}", this);
    }

    for (CDOID id : clusterOfObjects)
    {
      analyzer.removeTrackingID(id);
    }

    analyzer.removeTracking(this);
  }
}
