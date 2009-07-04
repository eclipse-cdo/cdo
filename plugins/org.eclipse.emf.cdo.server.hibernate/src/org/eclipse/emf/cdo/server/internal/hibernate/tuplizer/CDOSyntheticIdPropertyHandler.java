/**
 * Copyright (c) 2005, 2006, 2007, 2008 Springsite BV (The Netherlands) and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Martin Taal - initial API and implementation
 *   Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateCommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Is only used for synthetic id's.
 * 
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 * @version $Revision: 1.12 $
 */
@SuppressWarnings("unchecked")
public class CDOSyntheticIdPropertyHandler implements Getter, Setter, PropertyAccessor
{
  private static final long serialVersionUID = 1L;

  public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException
  {
    return this;
  }

  public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException
  {
    return this;
  }

  public Object get(Object owner) throws HibernateException
  {
    InternalCDORevision revision = HibernateUtil.getInstance().getCDORevision(owner);
    if (revision == null)
    {
      return null;
    }

    if (!(revision.getID() instanceof CDOIDHibernate))
    {
      return null;
    }

    CDOIDHibernate cdoID = (CDOIDHibernate)revision.getID();
    return cdoID.getId();
  }

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

    HibernateCommitContext commitContext = null;
    if (HibernateThreadContext.isCommitContextSet())
    {
      commitContext = HibernateThreadContext.getCommitContext();
    }

    InternalCDORevision revision = HibernateUtil.getInstance().getCDORevision(target);
    CDOID cdoID = revision.getID();
    if (cdoID == null)
    {
      CDOIDHibernate newCDOID = CDOIDHibernateFactoryImpl.getInstance().createCDOID((Serializable)value,
          revision.getEClass().getName());
      revision.setID(newCDOID);
      if (commitContext != null)
      {
        commitContext.setNewID(cdoID, newCDOID);
      }
    }
    else if (cdoID instanceof CDOIDTemp)
    {
      CDOIDHibernate newCDOID = CDOIDHibernateFactoryImpl.getInstance().createCDOID((Serializable)value,
          revision.getEClass().getName());
      revision.setID(newCDOID);
      if (commitContext != null)
      {
        commitContext.getCommitContext().addIDMapping((CDOIDTemp)cdoID, newCDOID);
        commitContext.setNewID(cdoID, newCDOID);
      }
    }
    else
    {
      CDOIDHibernate hbCDOID = (CDOIDHibernate)revision.getID();
      if (!hbCDOID.getId().equals(value))
      {
        throw new IllegalStateException("Current id and new id are different " + value + "/" + hbCDOID.getId());
      }
    }
  }
}
