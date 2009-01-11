/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff  - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOQueryResultIteratorImpl<T> extends CDOAbstractQueryIteratorImpl<T>
{
  public CDOQueryResultIteratorImpl(CDOView view, CDOQueryInfo queryInfo)
  {
    super(view, queryInfo);
  }

  @Override
  public T next()
  {
    return adapt(super.next());
  }

  @SuppressWarnings("unchecked")
  protected T adapt(Object object)
  {
    if (object instanceof CDOID)
    {
      if (((CDOID)object).isNull())
      {
        return null;
      }

      return (T)getView().getObject((CDOID)object, true);
    }

    return (T)object;
  }

  @Override
  public List<T> asList()
  {
    List<Object> result = new ArrayList<Object>();
    while (super.hasNext())
    {
      result.add(super.next());
    }

    return new CDOEList<T>(getView(), result);
  }
}
