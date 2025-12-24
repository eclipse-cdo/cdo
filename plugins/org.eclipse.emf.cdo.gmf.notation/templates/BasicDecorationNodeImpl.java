/******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation 
 *    Eike Stepper - Migration to CDO 
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.gmf.runtime.notation.BasicDecorationNode;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
%%IMPORTS%%

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Basic Decoration Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated NOT
 * @since 1.2
 */
public class BasicDecorationNodeImpl extends org.eclipse.emf.cdo.ecore.impl.EModelElementImpl implements BasicDecorationNode {
%%FIELDS%%

	protected BasicDecorationNodeImpl() {
		super();
		setVisible(VISIBLE_EDEFAULT);
	}

  protected EClass eStaticClass() {
    return NotationPackage.Literals.BASIC_DECORATION_NODE;
  }
  
  public EList getSourceEdges() {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_SourceEdges(), 0, null);
  }
  
  public EList getTargetEdges() {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_TargetEdges(), 0, null);
  }
  
  public EList getPersistedChildren() {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_PersistedChildren(), 0, null);
  }
  
  public EList getTransientChildren() {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_TransientChildren(), 0, null);
  }
  
  public EList getChildren() {
    return ECollections.emptyEList();
  }
  
  public EList getStyles() {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_TransientChildren(), 0, null);
  }

  public Style getStyle(EClass eClass) {
    if (eClass != null && NotationPackage.eINSTANCE.getStyle().isSuperTypeOf(eClass)) {
      EClass thisEClass = eClass(); 
      if (eClass.isSuperTypeOf(thisEClass) || eClass == thisEClass) {
        return (Style) this;
      }
      if (eIsSet(NotationPackage.Literals.VIEW__STYLES)) {
        for (Iterator i = getStyles().iterator(); i.hasNext();) {
          Style style = (Style) i.next();
          if (style.eClass() == eClass || eClass.isInstance(style))
            return style;
        }
      }       
    }
    return null;
  }

  public NamedStyle getNamedStyle(EClass eClass, String name) {
    if (eClass != null
        && eIsSet(NotationPackage.Literals.VIEW__STYLES)
        && NotationPackage.eINSTANCE.getNamedStyle().isSuperTypeOf(eClass)) {
      for (Iterator i = getStyles().iterator(); i.hasNext();) {
        Style style = (Style) i.next();
        if (style.eClass() == eClass || eClass.isInstance(style))
          if (style.eGet(NotationPackage.eINSTANCE.getNamedStyle_Name()).equals(name))
            return (NamedStyle)style;
      }
    }
    return null;
  }
  
  public LayoutConstraint createLayoutConstraint(EClass eClass) {
    LayoutConstraint newLayoutConstraint = (LayoutConstraint) eClass.getEPackage().getEFactoryInstance().create(eClass);
    setLayoutConstraint(newLayoutConstraint);
    return newLayoutConstraint;
  }
  
  public void setLayoutConstraint(LayoutConstraint newLayoutConstraint) {
    throw new UnsupportedOperationException("BasicDecorationNodeImpl#setLayoutConstraint(LayoutConstraint newLayoutConstraint)"); //$NON-NLS-1$
  }

  public NotificationChain basicSetLayoutConstraint(LayoutConstraint newLayoutConstraint, NotificationChain msgs) {
    return msgs;
  }

  public LayoutConstraint getLayoutConstraint() {
    return null;
  }

  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case NotationPackage.BASIC_DECORATION_NODE__EANNOTATIONS:
        return ((InternalEList)getEAnnotations()).basicAdd(otherEnd, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }

  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case NotationPackage.BASIC_DECORATION_NODE__EANNOTATIONS:
        return ((InternalEList)getEAnnotations()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case NotationPackage.BASIC_DECORATION_NODE__EANNOTATIONS:
        return getEAnnotations();
      case NotationPackage.BASIC_DECORATION_NODE__VISIBLE:
        return isVisible() ? Boolean.TRUE : Boolean.FALSE;
      case NotationPackage.BASIC_DECORATION_NODE__TYPE:
        return getType();
      case NotationPackage.BASIC_DECORATION_NODE__MUTABLE:
        return isMutable() ? Boolean.TRUE : Boolean.FALSE;
      case NotationPackage.BASIC_DECORATION_NODE__SOURCE_EDGES:
        return getSourceEdges();
      case NotationPackage.BASIC_DECORATION_NODE__TARGET_EDGES:
        return getTargetEdges();
      case NotationPackage.BASIC_DECORATION_NODE__PERSISTED_CHILDREN:
        return getPersistedChildren();
      case NotationPackage.BASIC_DECORATION_NODE__STYLES:
        return getStyles();
      case NotationPackage.BASIC_DECORATION_NODE__ELEMENT:
        if (resolve) return getElement();
        return basicGetElement();
      case NotationPackage.BASIC_DECORATION_NODE__DIAGRAM:
        if (resolve) return getDiagram();
        return basicGetDiagram();
      case NotationPackage.BASIC_DECORATION_NODE__TRANSIENT_CHILDREN:
        return getTransientChildren();
      case NotationPackage.BASIC_DECORATION_NODE__LAYOUT_CONSTRAINT:
        return getLayoutConstraint();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case NotationPackage.BASIC_DECORATION_NODE__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection)newValue);
        return;
      case NotationPackage.BASIC_DECORATION_NODE__VISIBLE:
        setVisible(((Boolean)newValue).booleanValue());
        return;
      case NotationPackage.BASIC_DECORATION_NODE__TYPE:
        setType((String)newValue);
        return;
      case NotationPackage.BASIC_DECORATION_NODE__MUTABLE:
        setMutable(((Boolean)newValue).booleanValue());
        return;
      case NotationPackage.BASIC_DECORATION_NODE__SOURCE_EDGES:
      case NotationPackage.BASIC_DECORATION_NODE__TARGET_EDGES:
      case NotationPackage.BASIC_DECORATION_NODE__PERSISTED_CHILDREN:
      case NotationPackage.BASIC_DECORATION_NODE__STYLES:
      case NotationPackage.BASIC_DECORATION_NODE__TRANSIENT_CHILDREN:
      case NotationPackage.BASIC_DECORATION_NODE__LAYOUT_CONSTRAINT:
        return;
      case NotationPackage.BASIC_DECORATION_NODE__ELEMENT:
        setElement((EObject)newValue);
        return;
    }
    eDynamicSet(featureID, newValue);
  }

  public void eUnset(int featureID) {
    switch (featureID) {
      case NotationPackage.BASIC_DECORATION_NODE__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case NotationPackage.BASIC_DECORATION_NODE__VISIBLE:
        setVisible(VISIBLE_EDEFAULT);
        return;
      case NotationPackage.BASIC_DECORATION_NODE__TYPE:
        setType(TYPE_EDEFAULT);
        return;
      case NotationPackage.BASIC_DECORATION_NODE__MUTABLE:
        setMutable(MUTABLE_EDEFAULT);
        return;
      case NotationPackage.BASIC_DECORATION_NODE__SOURCE_EDGES:
      case NotationPackage.BASIC_DECORATION_NODE__TARGET_EDGES:
      case NotationPackage.BASIC_DECORATION_NODE__PERSISTED_CHILDREN:
      case NotationPackage.BASIC_DECORATION_NODE__STYLES:
      case NotationPackage.BASIC_DECORATION_NODE__TRANSIENT_CHILDREN:
      case NotationPackage.BASIC_DECORATION_NODE__LAYOUT_CONSTRAINT:
        return;
      case NotationPackage.BASIC_DECORATION_NODE__ELEMENT:
        unsetElement();
        return;
    }
    eDynamicUnset(featureID);
  }

  public boolean eIsSet(int featureID) {
    switch (featureID) {
      case NotationPackage.BASIC_DECORATION_NODE__EANNOTATIONS:
        return !getEAnnotations().isEmpty();
      case NotationPackage.BASIC_DECORATION_NODE__VISIBLE:
        return isVisible() != VISIBLE_EDEFAULT;
      case NotationPackage.BASIC_DECORATION_NODE__TYPE:
        return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
      case NotationPackage.BASIC_DECORATION_NODE__MUTABLE:
        return isMutable() != MUTABLE_EDEFAULT;
      case NotationPackage.BASIC_DECORATION_NODE__SOURCE_EDGES:
      case NotationPackage.BASIC_DECORATION_NODE__TARGET_EDGES:
      case NotationPackage.BASIC_DECORATION_NODE__PERSISTED_CHILDREN:
      case NotationPackage.BASIC_DECORATION_NODE__STYLES:
      case NotationPackage.BASIC_DECORATION_NODE__TRANSIENT_CHILDREN:
      case NotationPackage.BASIC_DECORATION_NODE__LAYOUT_CONSTRAINT:
        return false;
      case NotationPackage.BASIC_DECORATION_NODE__ELEMENT:
        return isSetElement();
      case NotationPackage.BASIC_DECORATION_NODE__DIAGRAM:
        return basicGetDiagram() != null;
    }
    return eDynamicIsSet(featureID);
  }

%%METHODS%%

} //BasicDecorationNodeImpl
