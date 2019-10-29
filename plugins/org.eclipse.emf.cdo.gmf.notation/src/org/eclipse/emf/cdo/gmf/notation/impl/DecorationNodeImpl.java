/******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *    Eike Stepper - Migration to CDO
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.DecorationNode;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
import org.eclipse.gmf.runtime.notation.NotationPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Decoration Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated NOT
 * @since 1.2
 */
public class DecorationNodeImpl extends BasicDecorationNodeImpl implements DecorationNode
{

  protected DecorationNodeImpl()
  {
    super();
  }

  @Override
  protected EClass eStaticClass()
  {
    return NotationPackage.Literals.DECORATION_NODE;
  }

  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.DECORATION_NODE__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicRemove(otherEnd, msgs);
    case NotationPackage.DECORATION_NODE__SOURCE_EDGES:
      return ((InternalEList)getSourceEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.DECORATION_NODE__TARGET_EDGES:
      return ((InternalEList)getTargetEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.DECORATION_NODE__PERSISTED_CHILDREN:
      return ((InternalEList)getPersistedChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.DECORATION_NODE__STYLES:
      return ((InternalEList)getStyles()).basicRemove(otherEnd, msgs);
    case NotationPackage.DECORATION_NODE__TRANSIENT_CHILDREN:
      return ((InternalEList)getTransientChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.DECORATION_NODE__LAYOUT_CONSTRAINT:
      return basicSetLayoutConstraint(null, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case NotationPackage.DECORATION_NODE__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.DECORATION_NODE__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.DECORATION_NODE__TYPE:
      return getType();
    case NotationPackage.DECORATION_NODE__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.DECORATION_NODE__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.DECORATION_NODE__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.DECORATION_NODE__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.DECORATION_NODE__STYLES:
      return getStyles();
    case NotationPackage.DECORATION_NODE__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.DECORATION_NODE__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.DECORATION_NODE__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.DECORATION_NODE__LAYOUT_CONSTRAINT:
      return getLayoutConstraint();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.DECORATION_NODE__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.DECORATION_NODE__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.DECORATION_NODE__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.DECORATION_NODE__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.DECORATION_NODE__SOURCE_EDGES:
    case NotationPackage.DECORATION_NODE__TARGET_EDGES:
      return;
    case NotationPackage.DECORATION_NODE__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.DECORATION_NODE__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.DECORATION_NODE__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.DECORATION_NODE__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.DECORATION_NODE__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.DECORATION_NODE__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.DECORATION_NODE__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.DECORATION_NODE__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.DECORATION_NODE__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.DECORATION_NODE__SOURCE_EDGES:
    case NotationPackage.DECORATION_NODE__TARGET_EDGES:
      return;
    case NotationPackage.DECORATION_NODE__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.DECORATION_NODE__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.DECORATION_NODE__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.DECORATION_NODE__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case NotationPackage.DECORATION_NODE__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)null);
      return;
    }
    eDynamicUnset(featureID);
  }

  @SuppressWarnings("null")
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.DECORATION_NODE__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.DECORATION_NODE__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.DECORATION_NODE__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.DECORATION_NODE__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.DECORATION_NODE__SOURCE_EDGES:
      return false;
    case NotationPackage.DECORATION_NODE__TARGET_EDGES:
      return false;
    case NotationPackage.DECORATION_NODE__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.DECORATION_NODE__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.DECORATION_NODE__ELEMENT:
      return isSetElement();
    case NotationPackage.DECORATION_NODE__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.DECORATION_NODE__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    case NotationPackage.DECORATION_NODE__LAYOUT_CONSTRAINT:
      return getLayoutConstraint() != null;
    }
    return eDynamicIsSet(featureID);
  }

  @Override
  public EList getPersistedChildren()
  {
    return (EList)eDynamicGet(NotationPackage.VIEW__PERSISTED_CHILDREN - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__PERSISTED_CHILDREN, true, true);
  }

  @Override
  public EList getChildren()
  {
    return new EContentsEList(this, childrenFeatures);
    // if (allChildren == null) {
    // allChildren = new EContentsEList(this, childrenFeatures);
    // }
    // return allChildren;
  }

  @Override
  public EList getStyles()
  {
    return (EList)eDynamicGet(NotationPackage.VIEW__STYLES - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__STYLES, true, true);
  }

  @Override
  public EList getTransientChildren()
  {
    return (EList)eDynamicGet(NotationPackage.VIEW__TRANSIENT_CHILDREN - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__TRANSIENT_CHILDREN, true, true);
  }

  @Override
  public LayoutConstraint getLayoutConstraint()
  {
    return (LayoutConstraint)eDynamicGet(NotationPackage.NODE__LAYOUT_CONSTRAINT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.NODE__LAYOUT_CONSTRAINT,
        true, true);
  }

  @Override
  public NotificationChain basicSetLayoutConstraint(LayoutConstraint newLayoutConstraint, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newLayoutConstraint, NotationPackage.NODE__LAYOUT_CONSTRAINT, msgs);
    return msgs;
  }

  @Override
  public void setLayoutConstraint(LayoutConstraint newLayoutConstraint)
  {
    eDynamicSet(NotationPackage.NODE__LAYOUT_CONSTRAINT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.NODE__LAYOUT_CONSTRAINT, newLayoutConstraint);
  }

} // DecorationNodeImpl
