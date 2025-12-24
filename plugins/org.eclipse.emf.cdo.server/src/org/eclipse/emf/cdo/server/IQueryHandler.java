/*
 * Copyright (c) 2008-2012, 2015, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.view.CDOQuery;

/**
 * A query language handler that is capable of executing a {@link CDOQuery query}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public interface IQueryHandler
{
  /**
   * Executes the {@link CDOQuery query} represented by the specified {@link CDOQueryInfo query info} by
   * {@link IQueryContext#addResult(Object) passing} the query results to the query execution engine represented by the
   * specified {@link IQueryContext execution context}.
   *
   * @since 3.0
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context);

  /**
   * @author Eike Stepper
   * @since 4.18
   */
  public interface PotentiallySlow extends IQueryHandler
  {
    public boolean isSlow(CDOQueryInfo info);
  }
}
