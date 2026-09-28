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
 * A thread-safe, generation-scoped cache of permissions associated with resource nodes.
 * <p>
 * Each instance represents one logical cache generation. For a resource-node ID, implementations maintain two
 * independent semantic slots: one for the permission of the resource node revision itself, and one for the
 * resource-based baseline permission of ordinary objects contained in that resource node or resource. Updating one
 * slot must not change the other. Permissions stored in the cache are non-null {@link CDOPermission} values;
 * {@code null} is reserved to indicate a cache miss.
 * <p>
 * Authorization may access a cache concurrently, so implementations must be thread-safe. An obsolete generation
 * may continue to receive calls from authorizations that already captured it, but its entries must not become visible
 * through a newer generation created by {@link Creator}.
 *
 * @author Eike Stepper
 * @since 4.13
 */
public interface PermissionCache
{
  /**
   * Returns the permission in the selected semantic slot, or {@code null} when that slot is unknown.
   * <p>
   * A returned {@link CDOPermission#NONE} is a cache hit and must not be confused with {@code null}, which means
   * that no permission is cached for the slot.
   *
   * @param resourceNodeID the resource node identifying the cache entry
   * @param resourceNode {@code true} to select the permission for the resource-node revision itself, or
   *          {@code false} to select the resource-based baseline used for ordinary objects in that resource
   * @return the cached permission, including {@link CDOPermission#NONE}, or {@code null} on a cache miss
   */
  public CDOPermission get(CDOID resourceNodeID, boolean resourceNode);

  /**
   * Stores a non-null permission in the selected semantic slot without changing the other slot for the same resource
   * node ID.
   *
   * @param resourceNodeID the resource node identifying the cache entry
   * @param resourceNode {@code true} to select the permission for the resource-node revision itself, or
   *          {@code false} to select the resource-based baseline used for ordinary objects in that resource
   * @param permission the permission to store; {@code null} is reserved for cache misses and is not a stored value
   */
  public void put(CDOID resourceNodeID, boolean resourceNode, CDOPermission permission);

  /**
   * Creates independent logical cache generations for repository, user, and branch scopes.
   */
  @FunctionalInterface
  public interface Creator
  {
    /**
     * Creates a non-null, logically empty cache generation for exactly the supplied repository, user, and branch.
     * <p>
     * Each invocation creates a generation independent of generations returned by earlier invocations. A newly
     * created generation must not expose their entries. An authorization that already captured an older generation
     * may still call {@link PermissionCache#get(CDOID, boolean)} or {@link PermissionCache#put(CDOID, boolean,
     * CDOPermission)} on it after a newer generation has been created. Such late writes must remain isolated in the
     * obsolete generation and must never become visible through the newer one.
     *
     * @param repository the repository whose authorization uses the generation
     * @param userID the ID of the user whose permissions are cached
     * @param branch the branch whose current-head resource permissions are cached
     * @return a new, non-null permission-cache generation for the supplied scope
     */
    public PermissionCache create(IRepository repository, String userID, CDOBranch branch);
  }
}
