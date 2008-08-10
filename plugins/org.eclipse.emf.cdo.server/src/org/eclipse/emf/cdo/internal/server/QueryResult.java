/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.common.query.AbstractQueryResult;
import org.eclipse.emf.cdo.server.IView;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class QueryResult extends AbstractQueryResult<Object>
{
  public QueryResult(IView view, CDOQueryInfo queryInfo, int queryID)
  {
    super(view, queryInfo, queryID);
  }

  @Override
  public IView getView()
  {
    return (IView)super.getView();
  }
}
