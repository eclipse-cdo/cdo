/*
 * Copyright (c) 2008-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff  - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.spi.cdo.AbstractQueryIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOQueryCDOIDIteratorImpl<CDOID> extends AbstractQueryIterator<CDOID>
{
  public CDOQueryCDOIDIteratorImpl(CDOView view, CDOQueryInfo queryInfo)
  {
    super(view, queryInfo);
  }

  @Override
  public List<CDOID> asList()
  {
    ArrayList<CDOID> result = new ArrayList<>();
    while (hasNext())
    {
      result.add(next());
    }

    return result;
  }

  @Override
  public CDOID asValue()
  {
    if (hasNext())
    {
      return next();
    }

    return null;
  }
}
