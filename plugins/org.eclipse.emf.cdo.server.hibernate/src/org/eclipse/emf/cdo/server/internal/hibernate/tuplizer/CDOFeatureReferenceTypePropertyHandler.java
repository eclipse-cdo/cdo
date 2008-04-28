/**
 * <copyright>
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Springsite BV (The Netherlands) and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Martin Taal
 * </copyright>
 *
 * $Id: CDOFeatureReferenceTypePropertyHandler.java,v 1.2 2008-04-28 08:30:39 estepper Exp $
 */

package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOType;

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
 * Is only used for synthetic id's.
 * 
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("unchecked")
public class CDOFeatureReferenceTypePropertyHandler implements Getter, Setter, PropertyAccessor
{
  private static final long serialVersionUID = 1L;

  public CDOFeatureReferenceTypePropertyHandler()
  {
  }

  public Object get(Object owner) throws HibernateException
  {
    CDOFeature cdoFeature = (CDOFeature)owner;
    if (cdoFeature.getType() != CDOType.OBJECT)
    {
      return null;
    }
  
    return cdoFeature.getReferenceType();
  }

  public Object getForInsert(Object arg0, Map arg1, SessionImplementor arg2) throws HibernateException
  {
    return get(arg0);
  }

  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    CDOFeatureImpl cdoFeature = (CDOFeatureImpl)target;
    if (cdoFeature.getType() != CDOType.OBJECT)
    {
      return;
    }
  
    cdoFeature.setReferenceType((CDOClass)value);
  }

  public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException
  {
    return this;
  }

  public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException
  {
    return this;
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
}
