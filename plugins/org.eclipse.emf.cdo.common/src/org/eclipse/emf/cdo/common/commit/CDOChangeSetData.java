/*
 * Copyright (c) 2010-2012, 2015, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link CDOChangeKindProvider change kind provider} with detailed information about {@link #getNewObjects() new},
 * {@link #getChangedObjects() changed} and {@link #getDetachedObjects() detached} objects.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOChangeSetData extends CDOChangeKindProvider
{
  /**
   * Returns <code>true</code>, if this change set data does not contain any changes, <code>false</code> otherwise.
   */
  public boolean isEmpty();

  /**
   * Returns a deep copy of this change set data.
   *
   * @since 4.0
   */
  public CDOChangeSetData copy();

  /**
   * Changes the internal state of this change set data by adding the changes of the given change set data.
   *
   * @since 4.0
   */
  public void merge(CDOChangeSetData changeSetData);

  /**
   * Returns a list of keys denoting which revisions have been added in the context of a commit operation.
   * Depending on various conditions like change subscriptions particular elements can also be full {@link CDORevision
   * revisions}.
   */
  public List<CDOIDAndVersion> getNewObjects();

  /**
   * Returns a list of revision keys denoting which (original) revisions have been changed in the context of a
   * commit operation. Depending on various conditions like change subscriptions particular elements can also be full
   * {@link CDORevisionDelta revision deltas}.
   */
  public List<CDORevisionKey> getChangedObjects();

  /**
   * Returns a list of keys denoting which revisions have been revised (corresponds to detached objects) in the
   * context of a commit operation. Depending on various conditions the version part of particular elements can be
   * {@link CDOBranchVersion#UNSPECIFIED_VERSION unspecified}.
   */
  public List<CDOIDAndVersion> getDetachedObjects();

  /**
   * @since 4.26
   */
  public default CDOIDAndVersion getNewObject(CDOID id)
  {
    return CDOIDUtil.getKey(getNewObjects(), id);
  }

  /**
   * @since 4.26
   */
  public default CDORevisionKey getChangedObject(CDOID id)
  {
    return CDOIDUtil.getKey(getChangedObjects(), id);
  }

  /**
   * @since 4.26
   */
  public default CDOIDAndVersion getDetachedObject(CDOID id)
  {
    return CDOIDUtil.getKey(getDetachedObjects(), id);
  }

  /**
   * Returns a list of ids denoting which revisions have been added, changed, or revised in the context of a commit operation.
   *
   * @since 4.15
   */
  public List<CDOID> getAffectedIDs();

  /**
   * @since 4.1
   */
  public Map<CDOID, CDOChangeKind> getChangeKinds();

  /**
   * @since 4.12
   */
  default void forEachRevision(Consumer<CDORevision> consumer)
  {
    for (CDOIDAndVersion idAndVersion : getNewObjects())
    {
      if (idAndVersion instanceof CDORevision)
      {
        consumer.accept((CDORevision)idAndVersion);
      }
    }
  }

  /**
   * @since 4.12
   */
  default void forEachRevisionDelta(Consumer<CDORevisionDelta> consumer)
  {
    for (CDORevisionKey revisionKey : getChangedObjects())
    {
      if (revisionKey instanceof CDORevisionDelta)
      {
        consumer.accept((CDORevisionDelta)revisionKey);
      }
    }
  }
}
