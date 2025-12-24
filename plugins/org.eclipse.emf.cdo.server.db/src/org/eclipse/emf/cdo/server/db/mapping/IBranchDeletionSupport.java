/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

import org.eclipse.net4j.db.Batch;

/**
 * Extension interface to {@link IListMapping} and {@link IClassMapping}.
 *
 * @author Eike Stepper
 * @since 4.11
 */
public interface IBranchDeletionSupport
{
  public void deleteBranches(IDBStoreAccessor accessor, Batch batch, String idList);
}
