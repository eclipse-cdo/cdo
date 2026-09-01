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
 * Optional bulk-write extension for {@link IClassMapping}.
 * <p>
 * Every revision or revision delta passed to a method belongs to this class mapping. Relative order within the
 * supplied array must be preserved unless the implementation has a demonstrated semantic reason to change it.
 * Implementations must not resolve the class mapping again. Mappings without this extension continue to use the
 * existing single-item methods.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public interface IClassMappingBatchingSupport
{
  /**
   * Writes revisions that all belong to this class mapping.
   */
  public void writeRevisions(IDBStoreAccessor accessor, InternalCDORevision[] revisions, boolean firstRevision, boolean revise, OMMonitor monitor);

  /**
   * Writes revision deltas that all belong to this class mapping.
   */
  public void writeRevisionDeltas(IDBStoreAccessor accessor, InternalCDORevisionDelta[] deltas, long created, OMMonitor monitor);
}
