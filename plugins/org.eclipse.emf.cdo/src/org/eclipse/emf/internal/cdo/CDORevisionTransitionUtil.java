/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0.
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.emf.internal.cdo.session.CDOCollectionLoadingResolver;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.HashSet;
import java.util.Set;

/**
 * Semantic helpers for protecting partially loaded list representations while revisions are structurally derived.
 *
 * @author Eike Stepper
 * @since 4.31
 */
public final class CDORevisionTransitionUtil
{
  private CDORevisionTransitionUtil()
  {
  }

  /**
   * Returns the many-valued features whose unresolved positions can change coordinates when the delta is applied.
   *
   * @param delta
   *          the revision delta to inspect
   * @return the affected features, never {@code null}
   */
  public static Set<EStructuralFeature> getCoordinateChangingFeatures(CDORevisionDelta delta)
  {
    Set<EStructuralFeature> result = new HashSet<>();
    for (CDOFeatureDelta featureDelta : delta.getFeatureDeltas())
    {
      result.addAll(getCoordinateChangingFeatures(featureDelta));
    }

    return result;
  }

  /**
   * Ensures that every many-valued feature whose list coordinates are changed by the given delta is fully loaded.
   * Features that are only assigned, cleared, or unset are not materialized for representation validity.
   *
   * @param revision
   *          the revision that will receive the delta
   * @param delta
   *          the feature delta to be applied
   */
  public static void ensureCoordinateChangingFeaturesFullyLoaded(InternalCDORevision revision, CDORevisionDelta delta, CDOSession session)
  {
    if (delta != null)
    {
      ensureFeaturesFullyLoaded(revision, getCoordinateChangingFeatures(delta), session);
    }
  }

  /**
   * Ensures that every many-valued feature whose list coordinates are changed by the given delta is fully loaded.
   *
   * @param revision
   *          the revision that will receive the delta
   * @param delta
   *          the feature delta to be applied
   */
  public static void ensureCoordinateChangingFeaturesFullyLoaded(InternalCDORevision revision, CDOFeatureDelta delta, CDOSession session)
  {
    if (delta != null)
    {
      ensureFeaturesFullyLoaded(revision, getCoordinateChangingFeatures(delta), session);
    }
  }

  /**
   * Ensures that the specified many-valued features are fully loaded.
   *
   * @param revision
   *          the revision whose features are to be loaded
   * @param features
   *          the exact features to load
   */
  public static void ensureFeaturesFullyLoaded(InternalCDORevision revision, Set<EStructuralFeature> features, CDOSession session)
  {
    if (revision.isUnchunked() || features.isEmpty())
    {
      return;
    }

    for (EStructuralFeature feature : features)
    {
      if (feature.isMany())
      {
        CDOList list = revision.getListOrNull(feature);
        if (list != null && !list.isFullyLoaded())
        {
          new CDOCollectionLoadingResolver(session).resolveAllProxies(revision, feature);
        }
      }
    }
  }

  /**
   * Compares exactly the supplied features after loading only their partially loaded list values.
   * <p>
   * The ordinary revision comparison remains intentionally complete. This operation is for callers that already have
   * a feature-scoped change set and must derive a target-relative delta without turning unrelated partial features into
   * complete revisions.
   *
   * @param sourceRevision
   *          the source revision
   * @param targetRevision
   *          the target revision
   * @param features
   *          the features whose values are relevant
   * @return the feature-scoped revision delta
   */
  public static InternalCDORevisionDelta compareFeatures(InternalCDORevision sourceRevision, InternalCDORevision targetRevision,
      Set<EStructuralFeature> features, CDOSession session)
  {
    ensureFeaturesFullyLoaded(sourceRevision, features, session);
    ensureFeaturesFullyLoaded(targetRevision, features, session);
    return new CDORevisionDeltaImpl(sourceRevision, targetRevision, features);
  }

  private static boolean isCoordinateChanging(CDOFeatureDelta.Type type)
  {
    return type == CDOFeatureDelta.Type.ADD || type == CDOFeatureDelta.Type.REMOVE || type == CDOFeatureDelta.Type.MOVE;
  }

  private static Set<EStructuralFeature> getCoordinateChangingFeatures(CDOFeatureDelta delta)
  {
    Set<EStructuralFeature> result = new HashSet<>();

    if (delta instanceof CDOListFeatureDelta)
    {
      for (CDOFeatureDelta listDelta : ((CDOListFeatureDelta)delta).getListChanges())
      {
        if (isCoordinateChanging(listDelta.getType()))
        {
          EStructuralFeature feature = listDelta.getFeature();
          if (feature != null && feature.isMany())
          {
            result.add(feature);
          }
        }
      }
    }
    else if (isCoordinateChanging(delta.getType()))
    {
      EStructuralFeature feature = delta.getFeature();
      if (feature != null && feature.isMany())
      {
        result.add(feature);
      }
    }

    return result;
  }
}
