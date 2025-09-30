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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;

/**
 * @author Simon McDuff
 */
public abstract class CDOAbstractFeatureRuleAnalyzer implements CDOFeatureAnalyzer, CDOFetchRuleManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOAbstractFeatureRuleAnalyzer.class);

  protected EStructuralFeature lastTraverseFeature;

  protected int lastTraverseIndex;

  protected long lastAccessTime;

  protected long lastElapseTimeBetweenOperations;

  protected CDOObject lastTraverseCDOObject;

  protected long lastLatencyTime;

  private CDOCollectionLoadingPolicy loadCollectionPolicy;

  private boolean didFetch;

  private int fetchCount;

  public CDOAbstractFeatureRuleAnalyzer()
  {
  }

  public final int getFetchCount()
  {
    return fetchCount;
  }

  @Override
  public final CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
  {
    return loadCollectionPolicy;
  }

  @Override
  public final void preTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("preTraverseFeature: {0}.{1}", cdoObject.eClass(), feature.getName()); //$NON-NLS-1$
    }

    loadCollectionPolicy = cdoObject.cdoView().getSession().options().getCollectionLoadingPolicy();
    lastTraverseFeature = feature;
    lastTraverseCDOObject = cdoObject;
    lastTraverseIndex = index;

    long now = System.currentTimeMillis();
    lastElapseTimeBetweenOperations = now - lastAccessTime;
    lastAccessTime = now;
    didFetch = false;

    CDOFetchRuleManagerThreadLocal.join(this);
    doPreTraverseFeature(cdoObject, feature, index);
  }

  @Override
  public final void postTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("postTraverseFeature: {0}.{1}", cdoObject.eClass(), feature.getName()); //$NON-NLS-1$
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

  /**
   * Called before a feature is traversed.
   */
  protected abstract void doPreTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index);

  /**
   * Called after a feature has been traversed.
   */
  protected abstract void doPostTraverseFeature(CDOObject cdoObject, EStructuralFeature feature, int index, Object value);

  /**
   * Indicates that a fetch operation occurred during the traversal of the current feature.
   * <p>
   * Subclasses must call this method when a fetch occurs.
   * This is usually done in implementations of {@link CDOFetchRuleManager#getFetchRules(Collection)}, which is called from
   * <code>LoadRevisionsRequest.requesting(CDODataOutput)</code>.
   */
  protected final void fetchData()
  {
    didFetch = true;
    fetchCount++;
  }

  protected final boolean didFetch()
  {
    return didFetch;
  }
}
