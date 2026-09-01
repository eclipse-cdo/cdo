/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Optional bulk-write extension for {@link IMappingStrategy}.
 * <p>
 * A strategy implementing this interface receives one complete DBStore write phase. It may partition the work among
 * class mappings. Implementations that do not provide this extension retain the existing single-item behavior.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public interface IMappingStrategyBatchingSupport
{
  /**
   * Writes a group of revisions belonging to one DBStore write phase.
   */
  public void writeRevisions(IDBStoreAccessor accessor, InternalCDORevision[] revisions, boolean firstRevision, boolean revise, OMMonitor monitor);

  /**
   * Writes a group of revision deltas belonging to one DBStore write phase.
   */
  public void writeRevisionDeltas(IDBStoreAccessor accessor, InternalCDORevisionDelta[] deltas, long created, OMMonitor monitor);
}
