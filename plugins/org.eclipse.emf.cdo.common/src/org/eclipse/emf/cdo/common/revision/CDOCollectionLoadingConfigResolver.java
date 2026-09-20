/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig.ChunkConfig;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Resolves effective collection-loading configuration for concrete model features.
 * <p>
 * Annotation lookup is lazy and limited to the feature, its declaring class, and its containing package. This class
 * has no dependency on a package registry and owns its annotation cache for use by both clients and servers.
 *
 * @author Eike Stepper
 * @since 4.38
 */
public class CDOCollectionLoadingConfigResolver
{
  private static final Object NO_ANNOTATION = new Object();

  private final Map<EModelElement, Object> annotationConfigs = Collections.synchronizedMap(new IdentityHashMap<>());

  public CDOCollectionLoadingConfigResolver()
  {
    // Do nothing.
  }

  /**
   * Resolves the effective configuration for a feature.
   * <p>
   * A {@code null} session configuration disables modern partial collection loading. Otherwise the initial and resolve
   * dimensions are resolved independently using session, repository, annotation, and default values in that order.
   *
   * @param sessionConfig the session configuration, or {@code null} to disable modern partial collection loading
   * @param repositoryConfig the repository-wide configuration, or {@code null}
   * @param feature the concrete structural feature to resolve
   * @return the fully resolved configuration, or {@code null} when the session configuration is {@code null}
   */
  public ChunkConfig resolve(CDOCollectionLoadingConfig sessionConfig, CDOCollectionLoadingConfig repositoryConfig, EStructuralFeature feature)
  {
    if (sessionConfig == null)
    {
      return null;
    }

    Objects.requireNonNull(feature, "feature");
    EClass eClass = feature.getEContainingClass();
    EPackage ePackage = eClass.getEPackage();

    synchronized (annotationConfigs)
    {
      int initialChunkSize = resolveDimension(sessionConfig, repositoryConfig, feature, eClass, ePackage, true);
      int resolveChunkSize = resolveDimension(sessionConfig, repositoryConfig, feature, eClass, ePackage, false);

      return new ChunkConfig(initialChunkSize, resolveChunkSize);
    }
  }

  /**
   * Clears all lazily cached annotation results.
   * <p>
   * Call this when model/package definitions are replaced, because a new package instance may use the same symbolic
   * names with different annotations.
   */
  public void clearModelCache()
  {
    synchronized (annotationConfigs)
    {
      annotationConfigs.clear();
    }
  }

  private int resolveDimension(CDOCollectionLoadingConfig sessionConfig, CDOCollectionLoadingConfig repositoryConfig, EStructuralFeature feature, EClass eClass,
      EPackage ePackage, boolean initial)
  {
    int value = getDimension(sessionConfig, feature, eClass, ePackage, initial);
    if (value != ChunkConfig.INHERIT)
    {
      return value;
    }

    value = getDimension(repositoryConfig, feature, eClass, ePackage, initial);
    if (value != ChunkConfig.INHERIT)
    {
      return value;
    }

    value = getDimension(getAnnotationConfig(feature), initial);
    if (value != ChunkConfig.INHERIT)
    {
      return value;
    }

    value = getDimension(getAnnotationConfig(eClass), initial);
    if (value != ChunkConfig.INHERIT)
    {
      return value;
    }

    value = getDimension(getAnnotationConfig(ePackage), initial);
    if (value != ChunkConfig.INHERIT)
    {
      return value;
    }

    value = getDimension(sessionConfig.getDefaultChunkConfig(), initial);
    if (value != ChunkConfig.INHERIT)
    {
      return value;
    }

    value = getDimension(repositoryConfig == null ? null : repositoryConfig.getDefaultChunkConfig(), initial);
    return value == ChunkConfig.INHERIT ? ChunkConfig.ALL : value;
  }

  private ChunkConfig getAnnotationConfig(EModelElement modelElement)
  {
    Object cached = annotationConfigs.get(modelElement);
    if (cached != null)
    {
      return cached == NO_ANNOTATION ? null : (ChunkConfig)cached;
    }

    ChunkConfig config = EMFUtil.getCollectionLoadingChunkConfig(modelElement);
    annotationConfigs.put(modelElement, config == null ? NO_ANNOTATION : config);
    return config;
  }

  private static int getDimension(CDOCollectionLoadingConfig config, EStructuralFeature feature, EClass eClass, EPackage ePackage, boolean initial)
  {
    if (config == null)
    {
      return ChunkConfig.INHERIT;
    }

    int value = getDimension(config.get(feature), initial);
    if (value != ChunkConfig.INHERIT)
    {
      return value;
    }

    value = getDimension(config.get(eClass), initial);
    if (value != ChunkConfig.INHERIT)
    {
      return value;
    }

    return getDimension(config.get(ePackage), initial);
  }

  private static int getDimension(ChunkConfig config, boolean initial)
  {
    if (config == null)
    {
      return ChunkConfig.INHERIT;
    }

    return initial ? config.getInitialChunkSize() : config.getResolveChunkSize();
  }
}
