/*
 * Copyright (c) 2008-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
 * Provides the consumer with {@link IQueryHandler query handlers} that are capable of executing {@link CDOQuery
 * queries} represented by specific {@link CDOQueryInfo query infos}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public interface IQueryHandlerProvider
{
  /**
   * @since 3.0
   */
  public IQueryHandler getQueryHandler(CDOQueryInfo info);
}
