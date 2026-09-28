/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.spi.security;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.server.IRepository;

/**
 * A generation-scoped cache of permissions associated with resource nodes.
 * A {@code null} result from {@link #get(CDOID, boolean)} denotes a cache miss;
 * {@link CDOPermission#NONE} is a valid cached result.
 *
 * @author Eike Stepper
 * @since 4.13
 */
public interface PermissionCache
{
  /**
   * Returns a cached permission, or {@code null} when the selected slot is unknown.
   *
   * @param resourceNodeID the resource node identifying the cache entry
   * @param resourceNode whether the permission applies to the resource node itself
   * @return the cached permission or {@code null}
   */
  public CDOPermission get(CDOID resourceNodeID, boolean resourceNode);

  /**
   * Stores a permission in the selected slot without changing the other slot.
   *
   * @param resourceNodeID the resource node identifying the cache entry
   * @param resourceNode whether the permission applies to the resource node itself
   * @param permission the permission to store
   */
  public void put(CDOID resourceNodeID, boolean resourceNode, CDOPermission permission);

  /**
   * Creates independent logical cache generations.
   */
  @FunctionalInterface
  public interface Creator
  {
    /**
     * Creates an empty generation for a repository, user, and branch context.
     */
    public PermissionCache create(IRepository repository, String userID, CDOBranch branch);
  }
}
