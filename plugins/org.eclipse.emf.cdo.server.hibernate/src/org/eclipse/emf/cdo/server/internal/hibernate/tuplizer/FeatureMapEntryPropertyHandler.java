/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial api
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry;

import org.eclipse.emf.ecore.EReference;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Implements the Hibernate accessor for mapped feature map properties. The owner object is always an instance of the
 * {@link CDOFeatureMapEntry}. Returns null if the mapped property has a different name then the current feature of the
 * owner (the CDOFeatureMapEntry), calls {@link CDOFeatureMapEntry#getValue()} in all other cases.
 * 
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 */
public class FeatureMapEntryPropertyHandler implements PropertyAccessor, Getter, Setter
{
  private static final long serialVersionUID = 1L;

  private String propertyName;

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

  public String getPropertyName()
  {
    return propertyName;
  }

  public void setPropertyName(String propertyName)
  {
    this.propertyName = propertyName;
  }

  public Object get(Object owner) throws HibernateException
  {
    final CDOFeatureMapEntry cdoFeatureMapEntry = (CDOFeatureMapEntry)owner;
    if (!isApplicable(cdoFeatureMapEntry))
    {
      return null;
    }

    final Object value = cdoFeatureMapEntry.getValue();
    if (cdoFeatureMapEntry.getEStructuralFeature() instanceof EReference)
    {
      if (value instanceof CDOID && CDOIDUtil.isNull((CDOID)value))
      {
        return null;
      }
      else if (value instanceof CDOIDExternal)
      {
        return value;
      }
      else if (value instanceof CDOID)
      {
        return HibernateUtil.getInstance().getCDORevision((CDOID)value);
      }
    }
    return value;
  }

  @SuppressWarnings("rawtypes")
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

  @SuppressWarnings("rawtypes")
  public Class getReturnType()
  {
    return CDOFeatureMapEntry.class;
  }

  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    final CDOFeatureMapEntry cdoFeatureMapEntry = (CDOFeatureMapEntry)target;
    Object localValue;
    if (cdoFeatureMapEntry.getEStructuralFeature() instanceof EReference)
    {
      if (value == null)
      {
        localValue = value;
      }
      else if (value instanceof CDOIDExternal)
      {
        localValue = value;
      }
      else
      {
        localValue = HibernateUtil.getInstance().getCDOID(value);
      }
    }
    else
    {
      localValue = value;
    }
    if (localValue != null)
    {
      cdoFeatureMapEntry.setValue(localValue);
    }
  }

  private boolean isApplicable(CDOFeatureMapEntry cdoFeatureMapEntry)
  {
    if (cdoFeatureMapEntry.getEStructuralFeature() == null)
    {
      return false;
    }
    return cdoFeatureMapEntry.getEStructuralFeature().getName().equals(getPropertyName());
  }
}
