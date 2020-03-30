/******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.gmf.runtime.notation.BasicCompartment;
import org.eclipse.gmf.runtime.notation.DrawerStyle;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Style;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Basic
 * Compartment</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.BasicCompartmentImpl#isCollapsed <em>Collapsed</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BasicCompartmentImpl extends DecorationNodeImpl implements BasicCompartment
{
  /**
   * The default value of the '{@link #isCollapsed() <em>Collapsed</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isCollapsed()
   * @generated
   * @ordered
   */
  protected static final boolean COLLAPSED_EDEFAULT = false;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected BasicCompartmentImpl()
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
    return NotationPackage.Literals.BASIC_COMPARTMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isCollapsed()
  {
    return ((Boolean)eDynamicGet(NotationPackage.BASIC_COMPARTMENT__COLLAPSED - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DRAWER_STYLE__COLLAPSED, true,
        true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCollapsed(boolean newCollapsed)
  {
    eDynamicSet(NotationPackage.BASIC_COMPARTMENT__COLLAPSED - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DRAWER_STYLE__COLLAPSED,
        new Boolean(newCollapsed));
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
    case NotationPackage.BASIC_COMPARTMENT__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.BASIC_COMPARTMENT__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.BASIC_COMPARTMENT__TYPE:
      return getType();
    case NotationPackage.BASIC_COMPARTMENT__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.BASIC_COMPARTMENT__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.BASIC_COMPARTMENT__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.BASIC_COMPARTMENT__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.BASIC_COMPARTMENT__STYLES:
      return getStyles();
    case NotationPackage.BASIC_COMPARTMENT__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.BASIC_COMPARTMENT__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.BASIC_COMPARTMENT__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.BASIC_COMPARTMENT__LAYOUT_CONSTRAINT:
      return getLayoutConstraint();
    case NotationPackage.BASIC_COMPARTMENT__COLLAPSED:
      return isCollapsed() ? Boolean.TRUE : Boolean.FALSE;
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.BASIC_COMPARTMENT__SOURCE_EDGES:
    case NotationPackage.BASIC_COMPARTMENT__TARGET_EDGES:
      return;
    default:
      eSetGen(featureID, newValue);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void eSetGen(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.BASIC_COMPARTMENT__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.BASIC_COMPARTMENT__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.BASIC_COMPARTMENT__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.BASIC_COMPARTMENT__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.BASIC_COMPARTMENT__SOURCE_EDGES:
      getSourceEdges().clear();
      getSourceEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.BASIC_COMPARTMENT__TARGET_EDGES:
      getTargetEdges().clear();
      getTargetEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.BASIC_COMPARTMENT__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.BASIC_COMPARTMENT__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.BASIC_COMPARTMENT__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.BASIC_COMPARTMENT__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.BASIC_COMPARTMENT__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)newValue);
      return;
    case NotationPackage.BASIC_COMPARTMENT__COLLAPSED:
      setCollapsed(((Boolean)newValue).booleanValue());
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.BASIC_COMPARTMENT__SOURCE_EDGES:
    case NotationPackage.BASIC_COMPARTMENT__TARGET_EDGES:
      return;
    default:
      eUnsetGen(featureID);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void eUnsetGen(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.BASIC_COMPARTMENT__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.BASIC_COMPARTMENT__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.BASIC_COMPARTMENT__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.BASIC_COMPARTMENT__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.BASIC_COMPARTMENT__SOURCE_EDGES:
      getSourceEdges().clear();
      return;
    case NotationPackage.BASIC_COMPARTMENT__TARGET_EDGES:
      getTargetEdges().clear();
      return;
    case NotationPackage.BASIC_COMPARTMENT__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.BASIC_COMPARTMENT__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.BASIC_COMPARTMENT__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.BASIC_COMPARTMENT__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case NotationPackage.BASIC_COMPARTMENT__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)null);
      return;
    case NotationPackage.BASIC_COMPARTMENT__COLLAPSED:
      setCollapsed(COLLAPSED_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.BASIC_COMPARTMENT__SOURCE_EDGES:
    case NotationPackage.BASIC_COMPARTMENT__TARGET_EDGES:
      return false;
    default:
      return eIsSetGen(featureID);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSetGen(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.BASIC_COMPARTMENT__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.BASIC_COMPARTMENT__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.BASIC_COMPARTMENT__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.BASIC_COMPARTMENT__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.BASIC_COMPARTMENT__SOURCE_EDGES:
      return !getSourceEdges().isEmpty();
    case NotationPackage.BASIC_COMPARTMENT__TARGET_EDGES:
      return !getTargetEdges().isEmpty();
    case NotationPackage.BASIC_COMPARTMENT__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.BASIC_COMPARTMENT__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.BASIC_COMPARTMENT__ELEMENT:
      return isSetElement();
    case NotationPackage.BASIC_COMPARTMENT__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.BASIC_COMPARTMENT__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    case NotationPackage.BASIC_COMPARTMENT__LAYOUT_CONSTRAINT:
      return getLayoutConstraint() != null;
    case NotationPackage.BASIC_COMPARTMENT__COLLAPSED:
      return isCollapsed() != COLLAPSED_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass)
  {
    if (baseClass == Style.class)
    {
      switch (derivedFeatureID)
      {
      default:
        return -1;
      }
    }
    if (baseClass == DrawerStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.BASIC_COMPARTMENT__COLLAPSED:
        return NotationPackage.DRAWER_STYLE__COLLAPSED;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass)
  {
    if (baseClass == Style.class)
    {
      switch (baseFeatureID)
      {
      default:
        return -1;
      }
    }
    if (baseClass == DrawerStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.DRAWER_STYLE__COLLAPSED:
        return NotationPackage.BASIC_COMPARTMENT__COLLAPSED;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // BasicCompartmentImpl
