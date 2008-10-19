/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff  - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOQueryCDOIDIteratorImpl<CDOID> extends CDOAbstractQueryIteratorImpl<CDOID>
{
  public CDOQueryCDOIDIteratorImpl(CDOView view, CDOQueryInfo queryInfo)
  {
    super(view, queryInfo);
  }

  @Override
  public List<CDOID> asList()
  {
    ArrayList<CDOID> result = new ArrayList<CDOID>();
    while (hasNext())
    {
      result.add(next());
    }

    return result;
  }
}
