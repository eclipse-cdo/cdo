/******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.PropertyValue;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Property Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.PropertyValueImpl#getRawValue <em>Raw Value</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.PropertyValueImpl#getInstanceType <em>Instance Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PropertyValueImpl extends CDOObjectImpl implements PropertyValue
{
  /**
   * The default value of the '{@link #getRawValue() <em>Raw Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRawValue()
   * @generated
   * @ordered
   */
  protected static final String RAW_VALUE_EDEFAULT = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PropertyValueImpl()
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
    return NotationPackage.Literals.PROPERTY_VALUE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getRawValue()
  {
    return (String)eDynamicGet(NotationPackage.PROPERTY_VALUE__RAW_VALUE, NotationPackage.Literals.PROPERTY_VALUE__RAW_VALUE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void setRawValue(String newRawValue)
  {
    if (getInstanceType() != null)
    {
      try
      {
        getObjectFromString(newRawValue);
      }
      catch (Exception e)
      {
        throw new IllegalArgumentException("Value <" + newRawValue//$NON-NLS-1$
            + "> cannot be associated with Data type <"//$NON-NLS-1$
            + getInstanceType().toString() + ">: " + e.getMessage()); //$NON-NLS-1$
      }
    }
    setRawValueGen(newRawValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRawValueGen(String newRawValue)
  {
    eDynamicSet(NotationPackage.PROPERTY_VALUE__RAW_VALUE, NotationPackage.Literals.PROPERTY_VALUE__RAW_VALUE, newRawValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getInstanceType()
  {
    return (EDataType)eDynamicGet(NotationPackage.PROPERTY_VALUE__INSTANCE_TYPE, NotationPackage.Literals.PROPERTY_VALUE__INSTANCE_TYPE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType basicGetInstanceType()
  {
    return (EDataType)eDynamicGet(NotationPackage.PROPERTY_VALUE__INSTANCE_TYPE, NotationPackage.Literals.PROPERTY_VALUE__INSTANCE_TYPE, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setInstanceType(EDataType newInstanceType)
  {
    eDynamicSet(NotationPackage.PROPERTY_VALUE__INSTANCE_TYPE, NotationPackage.Literals.PROPERTY_VALUE__INSTANCE_TYPE, newInstanceType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object getValue()
  {
    return getObjectFromString(getRawValue());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void setValue(Object newValue)
  {
    setRawValue(getStringFromObject(newValue));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String getStringFromObject(Object objectValue)
  {
    if (getInstanceType() == null && objectValue instanceof String)
    {
      return (String)objectValue;
    }
    return EcoreUtil.convertToString(getInstanceType(), objectValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object getObjectFromString(String stringValue)
  {
    if (getInstanceType() == null)
    {
      return stringValue;
    }
    return EcoreUtil.createFromString(getInstanceType(), stringValue);
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
    case NotationPackage.PROPERTY_VALUE__RAW_VALUE:
      return getRawValue();
    case NotationPackage.PROPERTY_VALUE__INSTANCE_TYPE:
      if (resolve)
      {
        return getInstanceType();
      }
      return basicGetInstanceType();
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
    case NotationPackage.PROPERTY_VALUE__RAW_VALUE:
      setRawValue((String)newValue);
      return;
    case NotationPackage.PROPERTY_VALUE__INSTANCE_TYPE:
      setInstanceType((EDataType)newValue);
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
    case NotationPackage.PROPERTY_VALUE__RAW_VALUE:
      setRawValue(RAW_VALUE_EDEFAULT);
      return;
    case NotationPackage.PROPERTY_VALUE__INSTANCE_TYPE:
      setInstanceType((EDataType)null);
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
    case NotationPackage.PROPERTY_VALUE__RAW_VALUE:
      return RAW_VALUE_EDEFAULT == null ? getRawValue() != null : !RAW_VALUE_EDEFAULT.equals(getRawValue());
    case NotationPackage.PROPERTY_VALUE__INSTANCE_TYPE:
      return basicGetInstanceType() != null;
    }
    return eDynamicIsSet(featureID);
  }

} // PropertyValueImpl
