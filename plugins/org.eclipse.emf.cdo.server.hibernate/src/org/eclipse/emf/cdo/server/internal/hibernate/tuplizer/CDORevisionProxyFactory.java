/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.type.CompositeType;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDORevisionProxyFactory implements ProxyFactory
{
  private String entityName;

  public CDORevisionProxyFactory()
  {
  }

  @SuppressWarnings("rawtypes")
  public void postInstantiate(String entityName, Class persistentClass, Set interfaces, Method getIdentifierMethod,
      Method setIdentifierMethod, CompositeType componentIdType) throws HibernateException
  {
    this.entityName = entityName;
  }

  public HibernateProxy getProxy(Serializable id, SessionImplementor session) throws HibernateException
  {
    return new CDORevisionProxyHibernate(new CDORevisionLazyInitializer(entityName, id, session));
  }

  public String getEntityName()
  {
    return entityName;
  }

  public void setEntityName(String entityName)
  {
    this.entityName = entityName;
  }
}
