/*
 * Copyright (c) 2010-2012, 2016, 2019, 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * Internal lifecycle and repository-binding contract for a storage backend. The repository uses it to configure the
 * store's revision behavior and to maintain branch and commit timestamp state across activation.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalStore extends IStore, ILifecycle
{
  @Override
  public InternalRepository getRepository();

  public void setRepository(IRepository repository);

  public void setRevisionTemporality(RevisionTemporality revisionTemporality);

  public void setRevisionParallelism(RevisionParallelism revisionParallelism);

  public int getNextBranchID();

  public int getNextLocalBranchID();

  public void setLastBranchID(int lastBranchID);

  public void setLastLocalBranchID(int lastLocalBranchID);

  public void setLastCommitTime(long lastCommitTime);

  public void setLastNonLocalCommitTime(long lastNonLocalCommitTime);

  /**
   * @deprecated Not used anymore.
   */
  @Deprecated
  public boolean isLocal(CDOID id);

  /**
   * @since 4.0
   */
  public boolean isDropAllDataOnActivate();

  /**
   * @since 4.0
   */
  public void setDropAllDataOnActivate(boolean dropAllDataOnActivate);

  /**
   * @since 4.0
   */
  public void setCreationTime(long creationTime);

  /**
   * Legacy capability marker for stores whose data model permits no references that point outside the repository. New
   * repositories should express this capability through {@code IRepositoryConfig.CAPABILITY_EXTERNAL_REFS}.
   *
   * @author Eike Stepper
   * @since 4.0
   * @deprecated As of 4.6 use IRepositoryConfig.CAPABILITY_EXTERNAL_REFS.
   */
  @Deprecated
  public interface NoExternalReferences
  {
  }

  /**
   * Capability marker for stores that cannot answer cross-reference queries. Repository query support can use this to
   * avoid exposing queries that require those references.
   *
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoQueryXRefs
  {
  }

  /**
   * Capability marker for stores that do not persist or retrieve CDO large objects such as blobs and character objects.
   *
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoLargeObjects
  {
  }

  /**
   * Capability marker for stores that do not support persisted EMF feature maps. The feature-map model representation is
   * no longer supported by current CDO revisions.
   *
   * @author Eike Stepper
   * @since 4.0
   * @deprecated As of 4.5 {@link org.eclipse.emf.ecore.util.FeatureMap feature maps} are no longer supported.
   */
  @Deprecated
  public interface NoFeatureMaps
  {
  }

  /**
   * Capability marker for stores that cannot enumerate revisions through the revision-handler API. Repository
   * services use it when determining which history operations the backend can support.
   *
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoHandleRevisions
  {
  }

  /**
   * Capability marker for stores that cannot expose raw storage data for replication. Such stores must use the
   * callback-based replication path instead.
   *
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoRawAccess
  {
  }

  /**
   * Capability marker for stores that cannot produce change-set data for a time interval. Change-set based history
   * operations require this capability from the backend.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  public interface NoChangeSets
  {
  }

  /**
   * Capability marker for stores that do not retain commit-info history. The repository can use it to identify that
   * commit-history lookup is unavailable from this backend.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  public interface NoCommitInfos
  {
  }

  /**
   * Capability marker for stores that do not support durable lock areas, so a client cannot resume locks through this
   * backend after its session is closed.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  public interface NoDurableLocking
  {
  }
}
