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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;

import java.util.Map;

/**
 * Implements the getter/setter for a wild card EAttribute property. This type of property is used in a feature map
 * created for wild cards. This class implements both the getter, setter and propertyaccessor interfaces. When the
 * getGetter and getSetter methods are called it returns itself.
 * 
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 */
public class WildCardAttributePropertyHandler extends FeatureMapEntryPropertyHandler
{
  private static final long serialVersionUID = -2659637883475733107L;

  @Override
  public Object get(Object owner) throws HibernateException
  {
    final FeatureMap.Entry fme = (FeatureMap.Entry)owner;
    final Object value = fme.getValue();
    final EStructuralFeature eFeature = fme.getEStructuralFeature();
    // not handled by this one
    if (value instanceof EObject)
    {
      return null;
    }

    if (value == null)
    {
      return null;
    }

    final EAttribute eAttribute = (EAttribute)eFeature;
    final EDataType eDataType = eAttribute.getEAttributeType();
    final String valueString = eDataType.getEPackage().getEFactoryInstance().convertToString(eDataType, value);
    return valueString;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) throws HibernateException
  {
    final Object value = get(owner);
    return value;
  }
}
