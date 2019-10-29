/******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
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

import org.eclipse.gmf.runtime.notation.DecorationNode;
%%IMPORTS%%

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
public class DecorationNodeImpl extends BasicDecorationNodeImpl implements DecorationNode {

  protected DecorationNodeImpl() {
		super();
	}

  protected EClass eStaticClass() {
		return NotationPackage.Literals.DECORATION_NODE;
	}

  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
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

  public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
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
				if (resolve) return getElement();
				return basicGetElement();
			case NotationPackage.DECORATION_NODE__DIAGRAM:
				if (resolve) return getDiagram();
				return basicGetDiagram();
			case NotationPackage.DECORATION_NODE__TRANSIENT_CHILDREN:
				return getTransientChildren();
			case NotationPackage.DECORATION_NODE__LAYOUT_CONSTRAINT:
				return getLayoutConstraint();
		}
		return eDynamicGet(featureID, resolve, coreType);
	}

  public void eSet(int featureID, Object newValue) {
		switch (featureID) {
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

  public void eUnset(int featureID) {
		switch (featureID) {
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

  public boolean eIsSet(int featureID) {
		switch (featureID) {
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

%%METHODS%%

} //DecorationNodeImpl
