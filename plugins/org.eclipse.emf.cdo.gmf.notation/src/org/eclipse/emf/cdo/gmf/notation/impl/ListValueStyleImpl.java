/******************************************************************************
 * Copyright (c) 2018-2020 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.DelegatingEcoreEList;

import org.eclipse.gmf.runtime.notation.ListValueStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

import java.util.Collection;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>List
 * Value Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ListValueStyleImpl#getRawValuesList <em>Raw Values List</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ListValueStyleImpl extends DataTypeStyleImpl implements ListValueStyle
{
  private EList rawValuesList;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ListValueStyleImpl()
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
    return NotationPackage.Literals.LIST_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public EList getRawValuesList()
  {
    if (rawValuesList instanceof DelegatingEcoreEList)
    {
      return rawValuesList;
    }
    final EList delegate = getRawValuesListGen();
    rawValuesList = new DelegatingEcoreEList(this)
    {
      private static final long serialVersionUID = -7769354624338385073L;

      /**
       * Overridden as per JavaDoc of {@link DelegatingEcoreEList}.
       */
      @Override
      public int getFeatureID()
      {
        return NotationPackage.LIST_VALUE_STYLE__RAW_VALUES_LIST;
      }

      @Override
      protected boolean isNotificationRequired()
      {
        // Never notify. The delegate list does so already.
        return false;
      }

      @Override
      protected List delegateList()
      {
        return delegate;
      }

      @Override
      protected Object validate(int index, Object object)
      {
        Object validated = super.validate(index, object);
        if (validated != null && !isInstance(validated))
        {
          throw new ArrayStoreException();
        }
        try
        {
          getObjectFromString((String)validated);
        }
        catch (Exception e)
        {
          throw new IllegalArgumentException("Value <" + validated//$NON-NLS-1$
              + "> cannot be associated with Data type <"//$NON-NLS-1$
              + getInstanceType().toString() + ">: " + e.getMessage());//$NON-NLS-1$
        }
        return validated;
      }
    };
    return rawValuesList;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EList getRawValuesListGen()
  {
    return (EList)eDynamicGet(NotationPackage.LIST_VALUE_STYLE__RAW_VALUES_LIST, NotationPackage.Literals.LIST_VALUE_STYLE__RAW_VALUES_LIST, true, true);
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
    case NotationPackage.LIST_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.LIST_VALUE_STYLE__INSTANCE_TYPE:
      if (resolve)
      {
        return getInstanceType();
      }
      return basicGetInstanceType();
    case NotationPackage.LIST_VALUE_STYLE__RAW_VALUES_LIST:
      return getRawValuesList();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.LIST_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.LIST_VALUE_STYLE__INSTANCE_TYPE:
      setInstanceType((EDataType)newValue);
      return;
    case NotationPackage.LIST_VALUE_STYLE__RAW_VALUES_LIST:
      getRawValuesList().clear();
      getRawValuesList().addAll((Collection)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
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
    case NotationPackage.LIST_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.LIST_VALUE_STYLE__INSTANCE_TYPE:
      setInstanceType((EDataType)null);
      return;
    case NotationPackage.LIST_VALUE_STYLE__RAW_VALUES_LIST:
      getRawValuesList().clear();
      return;
    }
    eDynamicUnset(featureID);
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
    case NotationPackage.LIST_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.LIST_VALUE_STYLE__INSTANCE_TYPE:
      return basicGetInstanceType() != null;
    case NotationPackage.LIST_VALUE_STYLE__RAW_VALUES_LIST:
      return !getRawValuesList().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // ListValueStyleImpl
