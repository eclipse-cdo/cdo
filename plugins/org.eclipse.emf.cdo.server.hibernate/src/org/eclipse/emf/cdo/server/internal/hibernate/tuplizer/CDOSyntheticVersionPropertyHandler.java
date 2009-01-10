/**
 * Copyright (c) 2005, 2006, 2007, 2008 Springsite BV (The Netherlands) and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Martin Taal
 *
 * $Id: CDOSyntheticVersionPropertyHandler.java,v 1.5 2009-01-10 07:08:55 estepper Exp $
 */

package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Reads the version from the internal version cache.
 * 
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 * @version $Revision: 1.5 $
 */
@SuppressWarnings("unchecked")
public class CDOSyntheticVersionPropertyHandler implements Getter, Setter, PropertyAccessor
{
  private static final long serialVersionUID = 1L;

  public CDOSyntheticVersionPropertyHandler()
  {
  }

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

    return revision.getVersion();
  }

  /**
   * Reads the version from the versioncache
   */
  public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) throws HibernateException
  {
    return get(owner);
  }

  public Method getMethod()
  {
    return null;
  }

  public String getMethodName()
  {
    return null;
  }

  /** Returns Integer.class */
  public Class getReturnType()
  {
    return Integer.class;
  }

  /** Sets the version in the internal version cache */
  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    InternalCDORevision revision = HibernateUtil.getInstance().getCDORevision(target);
    revision.setVersion(((Number)value).intValue());
  }
}
