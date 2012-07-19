/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - copied from CDORevisionPropertyHandler and adapted
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;

import org.hibernate.HibernateException;

/**
 * @author Martin Taal
 */
public class CDOReferenceGetter extends CDOPropertyGetter
{
  private static final long serialVersionUID = 1L;

  public CDOReferenceGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public Object get(Object target) throws HibernateException
  {
    final Object o = super.get(target);
    if (o instanceof CDOID && CDOIDUtil.isNull((CDOID)o))
    {
      return null;
    }
    else if (o instanceof CDOIDExternal)
    {
      return o;
    }
    else if (o instanceof CDOID)
    {
      return HibernateUtil.getInstance().getCDORevision((CDOID)o);
    }

    return o;
  }
}
