/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
