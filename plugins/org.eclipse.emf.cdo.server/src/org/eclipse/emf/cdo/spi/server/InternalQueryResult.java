/*
 * Copyright (c) 2009-2012, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.util.BlockingCloseableIterator;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.server.IQueryHandler;

/**
 * Server-side result stream for a query executing against a view. The query manager exposes it as a blocking iterator
 * backed by a queue, while retaining the handler and query metadata needed for cancellation and lifecycle management.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalQueryResult extends BlockingCloseableIterator<Object>
{
  public int getQueryID();

  public CDOQueryInfo getQueryInfo();

  public InternalView getView();

  public CDOQueryQueue<Object> getQueue();

  /**
   * @since 4.18
   */
  public IQueryHandler getQueryHandler();
}
