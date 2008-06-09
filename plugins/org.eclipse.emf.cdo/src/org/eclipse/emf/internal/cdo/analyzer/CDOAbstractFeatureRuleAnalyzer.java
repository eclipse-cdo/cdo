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

import org.eclipse.emf.cdo.analyzer.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.analyzer.CDOFetchRuleManager;
import org.eclipse.emf.cdo.common.model.CDOFeature;

import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Simon McDuff
 */
public abstract class CDOAbstractFeatureRuleAnalyzer implements CDOFeatureAnalyzer, CDOFetchRuleManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOAbstractFeatureRuleAnalyzer.class);

  protected CDOFeature lastTraverseFeature;

  protected int lastTraverseIndex;

  protected long lastAccessTime;

  protected long lastElapseTimeBetweenOperations;

  protected InternalCDOObject lastTraverseCDOObject;

  protected long lastLatencyTime;

  protected int loadRevisionCollectionChunkSize;

  private boolean didFetch;

  private int fetchCount = 0;

  public CDOAbstractFeatureRuleAnalyzer()
  {
  }

  public int getFetchCount()
  {
    return fetchCount;
  }

  public int getLoadRevisionCollectionChunkSize()
  {
    return loadRevisionCollectionChunkSize;
  }

  public void preTraverseFeature(InternalCDOObject cdoObject, CDOFeature feature, int index)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("preTraverseFeature : {0}.{1}", cdoObject.cdoClass(), feature.getName());
    }

    loadRevisionCollectionChunkSize = cdoObject.cdoView().getLoadRevisionCollectionChunkSize();
    lastTraverseFeature = feature;
    lastTraverseCDOObject = cdoObject;
    lastTraverseIndex = index;
    lastElapseTimeBetweenOperations = System.currentTimeMillis() - lastAccessTime;
    lastAccessTime = System.currentTimeMillis();
    didFetch = false;

    CDOFetchRuleManagerThreadLocal.join(this);
    doPreTraverseFeature(cdoObject, feature, index);
  }

  public void postTraverseFeature(InternalCDOObject cdoObject, CDOFeature feature, int index, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("postTraverseFeature : {0}.{1}", cdoObject.cdoClass(), feature.getName());
    }

    try
    {
      doPostTraverseFeature(cdoObject, feature, index, value);
    }
    finally
    {
      CDOFetchRuleManagerThreadLocal.leave();
      lastAccessTime = System.currentTimeMillis();
    }
  }

  protected void doPreTraverseFeature(InternalCDOObject cdoObject, CDOFeature feature, int index)
  {
  }

  protected void doPostTraverseFeature(InternalCDOObject cdoObject, CDOFeature feature, int index, Object value)
  {
  }

  protected void fetchData()
  {
    didFetch = true;
    fetchCount++;
  }

  protected boolean didFetch()
  {
    return didFetch;
  }
}
