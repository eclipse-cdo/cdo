/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * An optional mapping-strategy extension for preparing the physical database schema of a commit before transactional
 * CDO data is written.
 * <p>
 * A {@link IMappingStrategy mapping strategy} that implements this interface is called by the store accessor during
 * the schema-preparation phase of a commit. The call takes place after mapping objects for newly encountered package
 * units have been created and before commit metadata, revisions, attributes, or list values are written. Implementors
 * can therefore create all class tables, list tables, indexes, and other mapping-specific schema objects required by
 * the supplied write set while the accessor's JDBC connection contains no pending transactional CDO data.
 * <p>
 * This phase boundary is important for database adapters whose DDL commits implicitly or for the schema transaction
 * implementation that explicitly commits its connection. Once transactional data writing has started, mapping code
 * must not defer required schema creation to a later write operation. A successful return from this method is the
 * strategy's declaration that all schema needed by its normal write path has been prepared. Schema objects may remain
 * in the database when the subsequent CDO transaction is cancelled; the CDO data itself remains subject to the normal
 * commit or rollback semantics.
 * <p>
 * Mapping-strategy decorators should expose this capability through their effective delegate so that the store can
 * discover it with {@link IMappingStrategy#getEffectiveMappingStrategy(IMappingStrategy)}.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public interface IMappingStrategySchemaPreflight
{
  /**
   * Prepares every physical schema object that may be accessed while writing the supplied commit.
   * <p>
   * Implementations should use the given accessor for schema creation and report progress through the supplied
   * monitor. The method must finish all required schema work before returning. It must not write transactional CDO
   * data and should leave the mapping objects in the same initialized state that the subsequent write methods expect.
   *
   * @param accessor
   *          the store accessor and JDBC connection that will write the commit data; schema preparation must use this
   *          accessor so that all mapping state is initialized for the following write phase.
   * @param newObjects
   *          the complete revisions of objects newly attached by the commit.
   * @param dirtyObjects
   *          the complete revisions of existing objects revised by the commit.
   * @param dirtyObjectDeltas
   *          the deltas of existing objects written by a delta-capable mapping strategy.
   * @param monitor
   *          the monitor used to report preparation progress and cancellation.
   */
  public void prepareSchema(IDBStoreAccessor accessor, CDORevision[] newObjects, CDORevision[] dirtyObjects, CDORevisionDelta[] dirtyObjectDeltas,
      OMMonitor monitor);
}
