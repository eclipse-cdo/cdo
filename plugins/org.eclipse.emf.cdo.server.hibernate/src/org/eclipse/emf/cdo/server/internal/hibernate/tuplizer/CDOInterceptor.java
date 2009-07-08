/**
 * Copyright (c) 2004 - 2009 Springsite B.V. and others
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

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

/**
 * Resolves entitynames, todo: use entityname strategy!
 * 
 * @author Martin Taal
 */
public class CDOInterceptor extends EmptyInterceptor
{
  private static final long serialVersionUID = 1L;

  public CDOInterceptor()
  {
  }

  @Override
  public String getEntityName(Object object)
  {
    if (!(object instanceof CDORevision))
    {
      return object.getClass().getName();
    }

    final InternalCDORevision revision = HibernateUtil.getInstance().getCDORevision(object);

    final EClass eClass = revision.getEClass();
    return HibernateThreadContext.getCurrentStoreAccessor().getStore().getEntityName(eClass);
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
      String[] propertyNames, Type[] types)
  {
    return false;
  }

}
