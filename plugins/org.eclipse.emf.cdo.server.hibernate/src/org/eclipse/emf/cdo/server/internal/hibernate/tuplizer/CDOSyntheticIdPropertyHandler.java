/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateCommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Is only used for synthetic id's.
 * 
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 */
public class CDOSyntheticIdPropertyHandler implements Getter, Setter, PropertyAccessor
{
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("rawtypes")
  public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException
  {
    return this;
  }

  @SuppressWarnings("rawtypes")
  public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException
  {
    return this;
  }

  public Member getMember()
  {
    return null;
  }

  public Object get(Object owner) throws HibernateException
  {
    InternalCDORevision revision = HibernateUtil.getInstance().getCDORevision(owner);
    if (revision == null)
    {
      return null;
    }

    if (!HibernateUtil.getInstance().isStoreCreatedID(HibernateUtil.getInstance().getCDOID(revision)))
    {
      return null;
    }

    return HibernateUtil.getInstance().getIdValue(HibernateUtil.getInstance().getCDOID(revision));
  }

  @SuppressWarnings("rawtypes")
  public Object getForInsert(Object arg0, Map arg1, SessionImplementor arg2) throws HibernateException
  {
    return get(arg0);
  }

  public Method getMethod()
  {
    return null;
  }

  public String getMethodName()
  {
    return null;
  }

  @SuppressWarnings("rawtypes")
  public Class getReturnType()
  {
    return null;
  }

  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    if (value == null)
    {
      return;
    }

    final InternalCDORevision revision = HibernateUtil.getInstance().getCDORevision(target);
    final CDOID id = HibernateUtil.getInstance().getCDOID(revision);
    if (id == null || id instanceof CDOIDTemp)
    {
      final CDOID newCDOID = HibernateUtil.getInstance().createCDOID(new CDOClassifierRef(revision.getEClass()), value);
      revision.setID(newCDOID);
      if (HibernateThreadContext.isCommitContextSet())
      {
        final HibernateCommitContext commitContext = HibernateThreadContext.getCommitContext();
        commitContext.setNewID(id, newCDOID);
        if (id instanceof CDOIDTemp)
        {
          commitContext.getCommitContext().addIDMapping(id, newCDOID);
        }
      }
    }
    else
    {
      final Serializable idValue = HibernateUtil.getInstance().getIdValue(id);
      if (!idValue.equals(value))
      {
        throw new IllegalStateException("Current id and new id are different " + value + "/" + idValue); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }
}
