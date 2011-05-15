/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
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

    if (value == null)
    {
      if (getEStructuralFeature().getDefaultValue() == null)
      {
        return null;
      }

      if (getEStructuralFeature().isUnsettable())
      {
        return null;
      }

      if (isEEnum)
      {
        // handle it a few lines lower
        value = getEStructuralFeature().getDefaultValue();
      }
      else
      {
        return null;
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
}
