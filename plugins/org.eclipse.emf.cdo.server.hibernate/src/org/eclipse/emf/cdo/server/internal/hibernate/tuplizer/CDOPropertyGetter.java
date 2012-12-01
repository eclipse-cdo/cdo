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

import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDORevisionPropertyAccessor.CDORevisionSetter;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.teneo.PersistenceOptions;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.property.Getter;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * TODO How does this differ from {@link CDORevisionSetter}? Both needed?
 * 
 * @author Martin Taal
 */
public class CDOPropertyGetter extends CDOPropertyHandler implements Getter
{
  private static final long serialVersionUID = 1L;

  private final boolean isEEnum;

  private final EEnum eEnum;

  private boolean handleUnsetAsNull = false;

  public CDOPropertyGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
    if (getEStructuralFeature() != null)
    {
      isEEnum = getEStructuralFeature().getEType() instanceof EEnum;
      if (isEEnum)
      {
        eEnum = (EEnum)getEStructuralFeature().getEType();
      }
      else
      {
        eEnum = null;
      }
    }
    else
    {
      isEEnum = false;
      eEnum = null;
    }
  }

  public Object get(Object target) throws HibernateException
  {
    InternalCDORevision revision = (InternalCDORevision)target;
    Object value = revision.getValue(getEStructuralFeature());
    if (value == CDORevisionData.NIL)
    {
      // explicitly set to null
      return null;
    }

    final EStructuralFeature eFeature = getEStructuralFeature();
    if (value == null)
    {
      if (eFeature.getDefaultValue() == null)
      {
        return null;
      }

      // this happens when you don't set a value explicitly in CDO
      // then null is passed while the user may expect the default
      // value to be set.
      if (useDefaultValue())
      {
        value = eFeature.getDefaultValue();
      }
    }

    // hibernate sees eenums, CDO sees int
    if (isEEnum && value != null)
    {
      if (value instanceof Enumerator)
      {
        return value;
      }

      return eEnum.getEEnumLiteral((Integer)value);
    }

    return value;
  }

  // see CDOPropertyGetter#useDefaultValue
  private boolean useDefaultValue()
  {
    final EStructuralFeature eFeature = getEStructuralFeature();
    return eFeature.isRequired() || !handleUnsetAsNull && eFeature.isUnsettable();
  }

  @SuppressWarnings("rawtypes")
  public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) throws HibernateException
  {
    return get(target);
  }

  public Method getMethod()
  {
    return null;
  }

  public String getMethodName()
  {
    return null;
  }

  public Member getMember()
  {
    return null;
  }

  @SuppressWarnings("rawtypes")
  public Class getReturnType()
  {
    return Object.class;
  }

  @Override
  public void setPersistenceOptions(PersistenceOptions persistenceOptions)
  {
    super.setPersistenceOptions(persistenceOptions);
    handleUnsetAsNull = persistenceOptions.getHandleUnsetAsNull();
  }
}
