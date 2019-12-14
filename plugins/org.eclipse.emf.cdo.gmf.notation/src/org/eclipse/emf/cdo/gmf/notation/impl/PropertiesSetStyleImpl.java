/******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.PropertiesSetStyle;
import org.eclipse.gmf.runtime.notation.PropertyValue;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Properties Set Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.PropertiesSetStyleImpl#getPropertiesMap <em>Properties Map</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PropertiesSetStyleImpl extends NamedStyleImpl implements PropertiesSetStyle
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PropertiesSetStyleImpl()
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
    return NotationPackage.Literals.PROPERTIES_SET_STYLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap getPropertiesMap()
  {
    return (EMap)eDynamicGet(NotationPackage.PROPERTIES_SET_STYLE__PROPERTIES_MAP, NotationPackage.Literals.PROPERTIES_SET_STYLE__PROPERTIES_MAP, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object getProperty(String propertyName)
  {
    PropertyValue propertyValue = (PropertyValue)getPropertiesMap().get(propertyName);
    if (propertyValue != null)
    {
      return propertyValue.getValue();
    }
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean setProperty(String propertyName, Object newValue)
  {
    PropertyValue propertyValue = (PropertyValue)getPropertiesMap().get(propertyName);
    if (propertyValue != null)
    {
      propertyValue.setValue(newValue);
      return true;
    }
    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean createProperty(String propertyName, EDataType instanceType, Object initialValue)
  {
    if (propertyName != null && !hasProperty(propertyName))
    {
      PropertyValue value = NotationFactory.eINSTANCE.createPropertyValue();
      if (instanceType == null)
      {
        if (initialValue instanceof String)
        {
          value.setRawValue((String)initialValue);
        }
        else
        {
          return false;
        }
      }
      else
      {
        value.setInstanceType(instanceType);
        value.setValue(initialValue);
      }
      return getPropertiesMap().put(propertyName, value) == null;
    }
    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean removeProperty(String propertyName)
  {
    return getPropertiesMap().removeKey(propertyName) != null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean hasProperty(String propertyName)
  {
    return getPropertiesMap().containsKey(propertyName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean createProperty(String propertyName, String initialValue)
  {
    if (!hasProperty(propertyName))
    {
      PropertyValue value = NotationFactory.eINSTANCE.createPropertyValue();
      value.setRawValue(initialValue);
      return getPropertiesMap().put(propertyName, value) == null;
    }
    return false;
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
    case NotationPackage.PROPERTIES_SET_STYLE__PROPERTIES_MAP:
      return ((InternalEList)getPropertiesMap()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
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
    case NotationPackage.PROPERTIES_SET_STYLE__NAME:
      return getName();
    case NotationPackage.PROPERTIES_SET_STYLE__PROPERTIES_MAP:
      if (coreType)
      {
        return getPropertiesMap();
      }
      else
      {
        return getPropertiesMap().map();
      }
    }
    return eDynamicGet(featureID, resolve, coreType);
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
    case NotationPackage.PROPERTIES_SET_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.PROPERTIES_SET_STYLE__PROPERTIES_MAP:
      ((EStructuralFeature.Setting)getPropertiesMap()).set(newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
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
    case NotationPackage.PROPERTIES_SET_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.PROPERTIES_SET_STYLE__PROPERTIES_MAP:
      getPropertiesMap().clear();
      return;
    }
    eDynamicUnset(featureID);
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
    case NotationPackage.PROPERTIES_SET_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.PROPERTIES_SET_STYLE__PROPERTIES_MAP:
      return !getPropertiesMap().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // PropertiesSetStyleImpl
