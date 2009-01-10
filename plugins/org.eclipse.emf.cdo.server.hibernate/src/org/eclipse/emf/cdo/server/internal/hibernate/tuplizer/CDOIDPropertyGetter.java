/***************************************************************************
 * Copyright (c) 2004 - 2008 Martin Taal
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - copied from CDORevisionPropertyHandler and adapted
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;

/**
 * @author Martin Taal
 */
public class CDOIDPropertyGetter extends CDOPropertyGetter
{
  private static final long serialVersionUID = 1L;

  public CDOIDPropertyGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public Object get(Object target) throws HibernateException
  {
    InternalCDORevision revision = (InternalCDORevision)target;
    if (!(revision.getID() instanceof CDOIDHibernate))
    {
      return null;
    }

    CDOIDHibernate cdoID = (CDOIDHibernate)revision.getID();
    if (isVirtualProperty())
    {
      return cdoID.getId();
    }
    else
    {
      Object id = super.get(target);
      // TODO: does this make sense?
      // if (cdoID.getId() == null)
      // {
      // cdoID.setId((Serializable)id);
      // }
      return id;
    }
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
