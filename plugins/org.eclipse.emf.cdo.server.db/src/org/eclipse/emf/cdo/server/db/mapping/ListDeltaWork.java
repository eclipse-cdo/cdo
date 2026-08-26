/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;

/**
 * The semantic arguments of one {@link IListMappingDeltaSupport#processDelta} invocation.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class ListDeltaWork
{
  public static final int UNSPECIFIED_LIST_SIZE = -1;

  private final CDOID id;

  private final int branchId;

  private final int oldVersion;

  private final int newVersion;

  private final long created;

  private final CDOListFeatureDelta delta;

  private final int newListSize;

  public ListDeltaWork(CDOID id, int branchId, int oldVersion, int newVersion, long created, CDOListFeatureDelta delta)
  {
    this(id, branchId, oldVersion, newVersion, created, delta, UNSPECIFIED_LIST_SIZE);
  }

  public ListDeltaWork(CDOID id, int branchId, int oldVersion, int newVersion, long created, CDOListFeatureDelta delta, int newListSize)
  {
    this.id = id;
    this.branchId = branchId;
    this.oldVersion = oldVersion;
    this.newVersion = newVersion;
    this.created = created;
    this.delta = delta;
    this.newListSize = newListSize;
  }

  public CDOID getID()
  {
    return id;
  }

  public int getBranchId()
  {
    return branchId;
  }

  public int getOldVersion()
  {
    return oldVersion;
  }

  public int getNewVersion()
  {
    return newVersion;
  }

  public long getCreated()
  {
    return created;
  }

  public CDOListFeatureDelta getDelta()
  {
    return delta;
  }

  /**
   * Returns the semantic size after the delta has been planned, or {@link #UNSPECIFIED_LIST_SIZE} if it was not planned.
   */
  public int getNewListSize()
  {
    return newListSize;
  }
}
