/*
 * Copyright (c) 2013, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5.legacy.impl;

import org.eclipse.emf.cdo.tests.model5.GenListOfDate;
import org.eclipse.emf.cdo.tests.model5.legacy.Model5Package;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

import java.util.Collection;
import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Gen List Of Date</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfDateImpl#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GenListOfDateImpl extends EObjectImpl implements GenListOfDate
{
  /**
   * The cached value of the '{@link #getElements() <em>Elements</em>}' attribute list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getElements()
   * @generated
   * @ordered
   */
  protected EList<Date> elements;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected GenListOfDateImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model5Package.eINSTANCE.getGenListOfDate();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Date> getElements()
  {
    if (elements == null)
    {
      elements = new EDataTypeUniqueEList<Date>(Date.class, this, Model5Package.GEN_LIST_OF_DATE__ELEMENTS);
    }
    return elements;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model5Package.GEN_LIST_OF_DATE__ELEMENTS:
      return getElements();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model5Package.GEN_LIST_OF_DATE__ELEMENTS:
      getElements().clear();
      getElements().addAll((Collection<? extends Date>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model5Package.GEN_LIST_OF_DATE__ELEMENTS:
      getElements().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model5Package.GEN_LIST_OF_DATE__ELEMENTS:
      return elements != null && !elements.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (elements: ");
    result.append(elements);
    result.append(')');
    return result.toString();
  }

} // GenListOfDateImpl
