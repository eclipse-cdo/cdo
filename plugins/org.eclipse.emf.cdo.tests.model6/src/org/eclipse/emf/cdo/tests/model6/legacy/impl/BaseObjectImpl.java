/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy.impl;

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Base Object</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.BaseObjectImpl#getAttributeOptional <em>Attribute Optional</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.BaseObjectImpl#getAttributeRequired <em>Attribute Required</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.BaseObjectImpl#getAttributeList <em>Attribute List</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BaseObjectImpl extends EObjectImpl implements BaseObject
{
  /**
   * The default value of the '{@link #getAttributeOptional() <em>Attribute Optional</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getAttributeOptional()
   * @generated
   * @ordered
   */
  protected static final String ATTRIBUTE_OPTIONAL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttributeOptional() <em>Attribute Optional</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getAttributeOptional()
   * @generated
   * @ordered
   */
  protected String attributeOptional = ATTRIBUTE_OPTIONAL_EDEFAULT;

  /**
   * The default value of the '{@link #getAttributeRequired() <em>Attribute Required</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getAttributeRequired()
   * @generated
   * @ordered
   */
  protected static final String ATTRIBUTE_REQUIRED_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttributeRequired() <em>Attribute Required</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getAttributeRequired()
   * @generated
   * @ordered
   */
  protected String attributeRequired = ATTRIBUTE_REQUIRED_EDEFAULT;

  /**
   * The cached value of the '{@link #getAttributeList() <em>Attribute List</em>}' attribute list.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getAttributeList()
   * @generated
   * @ordered
   */
  protected EList<String> attributeList;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected BaseObjectImpl()
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
    return Model6Package.eINSTANCE.getBaseObject();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAttributeOptional()
  {
    return attributeOptional;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttributeOptional(String newAttributeOptional)
  {
    String oldAttributeOptional = attributeOptional;
    attributeOptional = newAttributeOptional;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.BASE_OBJECT__ATTRIBUTE_OPTIONAL, oldAttributeOptional, attributeOptional));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAttributeRequired()
  {
    return attributeRequired;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttributeRequired(String newAttributeRequired)
  {
    String oldAttributeRequired = attributeRequired;
    attributeRequired = newAttributeRequired;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.BASE_OBJECT__ATTRIBUTE_REQUIRED, oldAttributeRequired, attributeRequired));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<String> getAttributeList()
  {
    if (attributeList == null)
    {
      attributeList = new EDataTypeUniqueEList<>(String.class, this, Model6Package.BASE_OBJECT__ATTRIBUTE_LIST);
    }
    return attributeList;
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
    case Model6Package.BASE_OBJECT__ATTRIBUTE_OPTIONAL:
      return getAttributeOptional();
    case Model6Package.BASE_OBJECT__ATTRIBUTE_REQUIRED:
      return getAttributeRequired();
    case Model6Package.BASE_OBJECT__ATTRIBUTE_LIST:
      return getAttributeList();
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
    case Model6Package.BASE_OBJECT__ATTRIBUTE_OPTIONAL:
      setAttributeOptional((String)newValue);
      return;
    case Model6Package.BASE_OBJECT__ATTRIBUTE_REQUIRED:
      setAttributeRequired((String)newValue);
      return;
    case Model6Package.BASE_OBJECT__ATTRIBUTE_LIST:
      getAttributeList().clear();
      getAttributeList().addAll((Collection<? extends String>)newValue);
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
    case Model6Package.BASE_OBJECT__ATTRIBUTE_OPTIONAL:
      setAttributeOptional(ATTRIBUTE_OPTIONAL_EDEFAULT);
      return;
    case Model6Package.BASE_OBJECT__ATTRIBUTE_REQUIRED:
      setAttributeRequired(ATTRIBUTE_REQUIRED_EDEFAULT);
      return;
    case Model6Package.BASE_OBJECT__ATTRIBUTE_LIST:
      getAttributeList().clear();
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
    case Model6Package.BASE_OBJECT__ATTRIBUTE_OPTIONAL:
      return ATTRIBUTE_OPTIONAL_EDEFAULT == null ? attributeOptional != null : !ATTRIBUTE_OPTIONAL_EDEFAULT.equals(attributeOptional);
    case Model6Package.BASE_OBJECT__ATTRIBUTE_REQUIRED:
      return ATTRIBUTE_REQUIRED_EDEFAULT == null ? attributeRequired != null : !ATTRIBUTE_REQUIRED_EDEFAULT.equals(attributeRequired);
    case Model6Package.BASE_OBJECT__ATTRIBUTE_LIST:
      return attributeList != null && !attributeList.isEmpty();
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
    result.append(" (attributeOptional: ");
    result.append(attributeOptional);
    result.append(", attributeRequired: ");
    result.append(attributeRequired);
    result.append(", attributeList: ");
    result.append(attributeList);
    result.append(')');
    return result.toString();
  }

} // BaseObjectImpl
