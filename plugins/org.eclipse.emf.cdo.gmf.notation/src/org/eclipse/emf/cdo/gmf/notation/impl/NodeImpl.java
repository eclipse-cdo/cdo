/******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.LayoutConstraint;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.NodeImpl#getLayoutConstraint <em>Layout Constraint</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class NodeImpl extends ViewImpl implements Node
{
  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected NodeImpl()
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
    return NotationPackage.Literals.NODE;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  public LayoutConstraint getLayoutConstraint()
  {
    return (LayoutConstraint)eDynamicGet(NotationPackage.NODE__LAYOUT_CONSTRAINT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.NODE__LAYOUT_CONSTRAINT,
        true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  public NotificationChain basicSetLayoutConstraint(LayoutConstraint newLayoutConstraint, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newLayoutConstraint, NotationPackage.NODE__LAYOUT_CONSTRAINT, msgs);
    return msgs;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  public void setLayoutConstraint(LayoutConstraint newLayoutConstraint)
  {
    eDynamicSet(NotationPackage.NODE__LAYOUT_CONSTRAINT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.NODE__LAYOUT_CONSTRAINT, newLayoutConstraint);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   */
  public LayoutConstraint createLayoutConstraint(EClass eClass)
  {
    LayoutConstraint newLayoutConstraint = (LayoutConstraint)eClass.getEPackage().getEFactoryInstance().create(eClass);
    setLayoutConstraint(newLayoutConstraint);
    return newLayoutConstraint;
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
    case NotationPackage.NODE__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicRemove(otherEnd, msgs);
    case NotationPackage.NODE__SOURCE_EDGES:
      return ((InternalEList)getSourceEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.NODE__TARGET_EDGES:
      return ((InternalEList)getTargetEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.NODE__PERSISTED_CHILDREN:
      return ((InternalEList)getPersistedChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.NODE__STYLES:
      return ((InternalEList)getStyles()).basicRemove(otherEnd, msgs);
    case NotationPackage.NODE__TRANSIENT_CHILDREN:
      return ((InternalEList)getTransientChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.NODE__LAYOUT_CONSTRAINT:
      return basicSetLayoutConstraint(null, msgs);
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
    case NotationPackage.NODE__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.NODE__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.NODE__TYPE:
      return getType();
    case NotationPackage.NODE__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.NODE__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.NODE__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.NODE__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.NODE__STYLES:
      return getStyles();
    case NotationPackage.NODE__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.NODE__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.NODE__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.NODE__LAYOUT_CONSTRAINT:
      return getLayoutConstraint();
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
    case NotationPackage.NODE__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.NODE__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.NODE__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.NODE__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.NODE__SOURCE_EDGES:
      getSourceEdges().clear();
      getSourceEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.NODE__TARGET_EDGES:
      getTargetEdges().clear();
      getTargetEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.NODE__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.NODE__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.NODE__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.NODE__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.NODE__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)newValue);
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
    case NotationPackage.NODE__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.NODE__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.NODE__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.NODE__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.NODE__SOURCE_EDGES:
      getSourceEdges().clear();
      return;
    case NotationPackage.NODE__TARGET_EDGES:
      getTargetEdges().clear();
      return;
    case NotationPackage.NODE__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.NODE__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.NODE__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.NODE__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case NotationPackage.NODE__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)null);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
  * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
  * @generated
  */
  @SuppressWarnings("null")
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.NODE__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.NODE__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.NODE__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.NODE__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.NODE__SOURCE_EDGES:
      return !getSourceEdges().isEmpty();
    case NotationPackage.NODE__TARGET_EDGES:
      return !getTargetEdges().isEmpty();
    case NotationPackage.NODE__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.NODE__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.NODE__ELEMENT:
      return isSetElement();
    case NotationPackage.NODE__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.NODE__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    case NotationPackage.NODE__LAYOUT_CONSTRAINT:
      return getLayoutConstraint() != null;
    }
    return eDynamicIsSet(featureID);
  }

} // NodeImpl
