/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;

import org.eclipse.emf.teneo.hibernate.EMFInterceptor;

import org.hibernate.type.Type;

import java.io.Serializable;

/**
 * Resolves entitynames, todo: use entityname strategy!
 *
 * @author Martin Taal
 */
public class CDOInterceptor extends EMFInterceptor
{
  private static final long serialVersionUID = 1L;

  public CDOInterceptor()
  {
  }

  @Override
  public Boolean isTransient(Object entity)
  {
    if (!(entity instanceof CDORevision))
    {
      return super.isTransient(entity);
    }

    final CDORevision revision = (CDORevision)entity;
    final CDOID id = HibernateUtil.getInstance().getCDOID(revision);
    if (id.isNull() || id.isTemporary())
    {
      return true;
    }

    return null;
  }

  @Override
  public String getEntityName(Object object)
  {
    if (!(object instanceof CDORevision))
    {
      return super.getEntityName(object);
    }

    return HibernateUtil.getInstance().getEntityName(object);
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
      String[] propertyNames, Type[] types)
  {
    return false;
  }
}
