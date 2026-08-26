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

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Optional bulk-write extension for {@link IListMapping}.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public interface IListMappingBatchingSupport
{
  /**
   * Writes complete list values for the supplied revisions.
   */
  public void writeValues(IDBStoreAccessor accessor, InternalCDORevision[] revisions, boolean firstRevision, boolean raw, OMMonitor monitor);

  /**
   * Processes list deltas in their supplied order.
   */
  public void processDeltas(IDBStoreAccessor accessor, ListDeltaWork[] work, OMMonitor monitor);
}
