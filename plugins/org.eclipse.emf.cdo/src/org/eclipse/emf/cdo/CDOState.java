/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2014, 2016, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo;

/**
 * Enumerates the possible states of <b>local</b> {@link CDOObject objects}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public enum CDOState
{
  /**
   * @since 1.0
   */
  TRANSIENT(true, false, false),

  /**
   * @since 1.0
   */
  NEW(true, false, false),

  /**
   * @since 1.0
   */
  CLEAN(false, true, true),

  /**
   * @since 1.0
   */
  DIRTY(true, true, true),

  /**
   * @since 1.0
   */
  PROXY(false, true, false),

  /**
   * @since 1.0
   */
  CONFLICT(true, true, true),

  /**
   * @since 2.0
   */
  INVALID(false, false, false),

  /**
   * @since 2.0
   */
  INVALID_CONFLICT(false, false, false),

  /**
   * An intermediary state for internal use only. This state marks the first of two phases during an attach operation.
   */
  PREPARED(true, false, false);

  private final boolean local;

  private final boolean remote;

  private final boolean tracked;

  private CDOState(boolean local, boolean remote, boolean tracked)
  {
    this.local = local;
    this.remote = remote;
    this.tracked = tracked;
  }

  /**
   * Indicates whether the state has a local component, including locally created objects and uncommitted changes.
   *
   * @since 4.6
   */
  public final boolean isLocal()
  {
    return local;
  }

  /**
   * Indicates whether the state has a remote component. Repository-backed objects can be remote even before their
   * revisions have been loaded.
   *
   * @since 4.6
   */
  public final boolean isRemote()
  {
    return remote;
  }

  /**
   * Indicates whether revisions in this state are eligible for view-level permission tracking. CLEAN, DIRTY, and
   * CONFLICT revisions participate so permission updates can reach revisions retained by a view, including revisions
   * no longer present in the revision cache. This describes tracking eligibility only: <code>true</code> does not
   * imply that an entry currently exists in a tracking map. Tracking is enabled only for views of repositories whose
   * {@code isAuthorizing()} capability is true, and an eligible object without a retained revision has nothing to
   * track.
   *
   * @since 4.31
   */
  public final boolean isTracked()
  {
    return tracked;
  }
}
