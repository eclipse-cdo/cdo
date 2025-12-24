/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2014, 2016 Eike Stepper (Loehne, Germany) and others.
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
  TRANSIENT(true, false),

  /**
   * @since 1.0
   */
  NEW(true, false),

  /**
   * @since 1.0
   */
  CLEAN(false, true),

  /**
   * @since 1.0
   */
  DIRTY(true, true),

  /**
   * @since 1.0
   */
  PROXY(false, true),

  /**
   * @since 1.0
   */
  CONFLICT(true, true),

  /**
   * @since 2.0
   */
  INVALID(false, false),

  /**
   * @since 2.0
   */
  INVALID_CONFLICT(false, false),

  /**
   * An intermediary state for internal use only. This state marks the first of two phases during an attach operation.
   */
  PREPARED(true, false);

  private final boolean local;

  private final boolean remote;

  private CDOState(boolean local, boolean remote)
  {
    this.local = local;
    this.remote = remote;
  }

  /**
   * @since 4.6
   */
  public final boolean isLocal()
  {
    return local;
  }

  /**
   * @since 4.6
   */
  public final boolean isRemote()
  {
    return remote;
  }
}
