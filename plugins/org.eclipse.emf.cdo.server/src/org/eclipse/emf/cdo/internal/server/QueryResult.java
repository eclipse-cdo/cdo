/*
 * Copyright (c) 2008-2012, 2015, 2016, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.spi.common.AbstractQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalView;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class QueryResult extends AbstractQueryResult<Object> implements InternalQueryResult
{
  private final IQueryHandler handler;

  public QueryResult(InternalView view, CDOQueryInfo queryInfo, int queryID, IQueryHandler handler)
  {
    super(view, queryInfo, queryID);
    this.handler = handler;
  }

  @Override
  public InternalView getView()
  {
    return (InternalView)super.getView();
  }

  @Override
  public IQueryHandler getQueryHandler()
  {
    return handler;
  }
}
