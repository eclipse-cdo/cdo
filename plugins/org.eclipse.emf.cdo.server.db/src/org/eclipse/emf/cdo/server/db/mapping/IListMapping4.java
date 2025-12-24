/*
 * Copyright (c) 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor.Raw;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

/**
 * Extension interface to {@link IListMapping}.
 *
 * @author Eike Stepper
 * @since 4.7
 */
public interface IListMapping4
{
  /**
   * Write a complete list of values to the database.
   *
   * @param accessor
   *          the accessor to use.
   * @param revision
   *          the revision containing the list to be written.
   * @param firstRevision
   *          <code>true</code> if the type of the object is supposed to be mapped, <code>false</code> otherwise.
   * @param raw
   *          <code>true</code> if this method is called  as part of the
   *          {@link Raw#rawStore(InternalCDORevision, org.eclipse.net4j.util.om.monitor.OMMonitor) raw} storage,
   *          <code>false</code> otherwise.
   */
  public void writeValues(IDBStoreAccessor accessor, CDORevision revision, boolean firstRevision, boolean raw);
}
