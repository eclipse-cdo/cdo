/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.MemberAccess;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Member Access</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.impl.MemberAccessImpl#getObject <em>Object</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MemberAccessImpl extends AccessImpl implements MemberAccess
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MemberAccessImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ExpressionsPackage.Literals.MEMBER_ACCESS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expression getObject()
  {
    return (Expression)eDynamicGet(ExpressionsPackage.MEMBER_ACCESS__OBJECT, ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetObject(Expression newObject, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newObject, ExpressionsPackage.MEMBER_ACCESS__OBJECT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setObject(Expression newObject)
  {
    eDynamicSet(ExpressionsPackage.MEMBER_ACCESS__OBJECT, ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, newObject);
  }

  @Override
  protected Object evaluate(EvaluationContext context, String name)
  {
    Object object = getObject().evaluate(context);

    if (object instanceof EObject)
    {
      EObject eObject = (EObject)object;
      EStructuralFeature feature = eObject.eClass().getEStructuralFeature(name);
      if (feature != null)
      {
        return eObject.eGet(feature);
      }
    }

    try
    {
      Field field = object.getClass().getField(name);
      if (Modifier.isStatic(field.getModifiers()))
      {
        throw new IllegalStateException("Field is static: " + name);
      }

      return field.get(object);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      return basicSetObject(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      return getObject();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      setObject((Expression)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      setObject((Expression)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      return getObject() != null;
    }
    return super.eIsSet(featureID);
  }

} // MemberAccessImpl
