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

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.NamedStyle;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Style;
import org.eclipse.gmf.runtime.notation.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>View</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#isVisible <em>Visible</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#isMutable <em>Mutable</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#getSourceEdges <em>Source Edges</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#getTargetEdges <em>Target Edges</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#getPersistedChildren <em>Persisted Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#getStyles <em>Styles</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#getElement <em>Element</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#getDiagram <em>Diagram</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ViewImpl#getTransientChildren <em>Transient Children</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy org.eclipse.gmf.runtime.notation.*
 */
public abstract class ViewImpl extends org.eclipse.emf.cdo.ecore.impl.EModelElementImpl implements View
{

  /**
   * The default value of the '{@link #isVisible() <em>Visible</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isVisible()
   * @generated
   * @ordered
   */
  protected static final boolean VISIBLE_EDEFAULT = true;

  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getType()
   * @generated
   * @ordered
   */
  protected static final String TYPE_EDEFAULT = ""; //$NON-NLS-1$

  /**
   * The default value of the '{@link #isMutable() <em>Mutable</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isMutable()
   * @generated
   * @ordered
   */
  protected static final boolean MUTABLE_EDEFAULT = false;

  // EATM
  // private EContentsEList allChildren = null;

  protected static final EStructuralFeature[] childrenFeatures = new EStructuralFeature[] { NotationPackage.Literals.VIEW__PERSISTED_CHILDREN,
      NotationPackage.Literals.VIEW__TRANSIENT_CHILDREN };

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ViewImpl()
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
    return NotationPackage.Literals.VIEW;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static final int ESTATIC_FEATURE_COUNT = 0;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return ESTATIC_FEATURE_COUNT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isVisible()
  {
    return ((Boolean)eDynamicGet(NotationPackage.VIEW__VISIBLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__VISIBLE, true, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVisible(boolean newVisible)
  {
    eDynamicSet(NotationPackage.VIEW__VISIBLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__VISIBLE, new Boolean(newVisible));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getType()
  {
    return (String)eDynamicGet(NotationPackage.VIEW__TYPE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__TYPE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public void setType(String newType)
  {
    setTypeGen(newType == null ? null : newType.intern());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setTypeGen(String newType)
  {
    eDynamicSet(NotationPackage.VIEW__TYPE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__TYPE, newType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isMutable()
  {
    return ((Boolean)eDynamicGet(NotationPackage.VIEW__MUTABLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__MUTABLE, true, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMutable(boolean newMutable)
  {
    eDynamicSet(NotationPackage.VIEW__MUTABLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__MUTABLE, new Boolean(newMutable));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getSourceEdges()
  {
    return (EList)eDynamicGet(NotationPackage.VIEW__SOURCE_EDGES - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__SOURCE_EDGES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getTargetEdges()
  {
    return (EList)eDynamicGet(NotationPackage.VIEW__TARGET_EDGES - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__TARGET_EDGES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getPersistedChildren()
  {
    return (EList)eDynamicGet(NotationPackage.VIEW__PERSISTED_CHILDREN - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__PERSISTED_CHILDREN, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public EList getChildren()
  {
    return new EContentsEList(this, childrenFeatures);
    // if (allChildren == null) {
    // allChildren = new EContentsEList(this, childrenFeatures);
    // }
    // return allChildren;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getStyles()
  {
    return (EList)eDynamicGet(NotationPackage.VIEW__STYLES - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__STYLES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public EObject getElement()
  {
    if (!isSetElement())
    {
      EObject container = eContainer();
      if (container instanceof View)
      {
        View view = (View)container;
        return view.getElement();
      }
    }

    return getElementGen();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EObject getElementGen()
  {
    return (EObject)eDynamicGet(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EObject basicGetElement()
  {
    return (EObject)eDynamicGet(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setElement(EObject newElement)
  {
    eDynamicSet(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT, newElement);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetElement()
  {
    eDynamicUnset(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetElement()
  {
    return eDynamicIsSet(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Diagram getDiagram()
  {
    Diagram diagram = basicGetDiagram();
    return diagram != null && diagram.eIsProxy() ? (Diagram)eResolveProxy((InternalEObject)diagram) : diagram;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  public Diagram basicGetDiagram()
  {
    for (EObject parent = this; parent != null; parent = ((InternalEObject)parent).eInternalContainer())
    {
      if (NotationPackage.Literals.DIAGRAM.isSuperTypeOf(parent.eClass()))
      {
        return (Diagram)parent;
      }
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getTransientChildren()
  {
    return (EList)eDynamicGet(NotationPackage.VIEW__TRANSIENT_CHILDREN - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__TRANSIENT_CHILDREN, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public Style getStyle(EClass eClass)
  {
    if (eClass != null && NotationPackage.eINSTANCE.getStyle().isSuperTypeOf(eClass))
    {
      if (eClass.isSuperTypeOf(eClass()))
      {
        return (Style)this;
      }
      if (eIsSet(NotationPackage.Literals.VIEW__STYLES))
      {
        for (Iterator i = getStyles().iterator(); i.hasNext();)
        {
          Style style = (Style)i.next();
          if (style.eClass() == eClass || eClass.isInstance(style))
          {
            return style;
          }
        }
      }
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public Node createChild(EClass eClass)
  {
    Node newChild = (Node)eClass.getEPackage().getEFactoryInstance().create(eClass);
    getPersistedChildren().add(newChild);
    return newChild;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public Style createStyle(EClass eClass)
  {
    Style newStyle = (Style)eClass.getEPackage().getEFactoryInstance().create(eClass);
    getStyles().add(newStyle);
    return newStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public NamedStyle getNamedStyle(EClass eClass, String name)
  {
    if (eClass != null && eIsSet(NotationPackage.Literals.VIEW__STYLES) && NotationPackage.eINSTANCE.getNamedStyle().isSuperTypeOf(eClass))
    {
      for (Iterator i = getStyles().iterator(); i.hasNext();)
      {
        Style style = (Style)i.next();
        if (style.eClass() == eClass || eClass.isInstance(style))
        {
          if (style.eGet(NotationPackage.eINSTANCE.getNamedStyle_Name()).equals(name))
          {
            return (NamedStyle)style;
          }
        }
      }
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.VIEW__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicAdd(otherEnd, msgs);
    case NotationPackage.VIEW__SOURCE_EDGES:
      return ((InternalEList)getSourceEdges()).basicAdd(otherEnd, msgs);
    case NotationPackage.VIEW__TARGET_EDGES:
      return ((InternalEList)getTargetEdges()).basicAdd(otherEnd, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.VIEW__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicRemove(otherEnd, msgs);
    case NotationPackage.VIEW__SOURCE_EDGES:
      return ((InternalEList)getSourceEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.VIEW__TARGET_EDGES:
      return ((InternalEList)getTargetEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.VIEW__PERSISTED_CHILDREN:
      return ((InternalEList)getPersistedChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.VIEW__STYLES:
      return ((InternalEList)getStyles()).basicRemove(otherEnd, msgs);
    case NotationPackage.VIEW__TRANSIENT_CHILDREN:
      return ((InternalEList)getTransientChildren()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
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
    case NotationPackage.VIEW__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.VIEW__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.VIEW__TYPE:
      return getType();
    case NotationPackage.VIEW__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.VIEW__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.VIEW__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.VIEW__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.VIEW__STYLES:
      return getStyles();
    case NotationPackage.VIEW__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.VIEW__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.VIEW__TRANSIENT_CHILDREN:
      return getTransientChildren();
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
    case NotationPackage.VIEW__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.VIEW__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.VIEW__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.VIEW__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.VIEW__SOURCE_EDGES:
      getSourceEdges().clear();
      getSourceEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.VIEW__TARGET_EDGES:
      getTargetEdges().clear();
      getTargetEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.VIEW__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.VIEW__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.VIEW__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.VIEW__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
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
    case NotationPackage.VIEW__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.VIEW__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.VIEW__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.VIEW__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.VIEW__SOURCE_EDGES:
      getSourceEdges().clear();
      return;
    case NotationPackage.VIEW__TARGET_EDGES:
      getTargetEdges().clear();
      return;
    case NotationPackage.VIEW__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.VIEW__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.VIEW__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.VIEW__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
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
    case NotationPackage.VIEW__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.VIEW__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.VIEW__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.VIEW__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.VIEW__SOURCE_EDGES:
      return !getSourceEdges().isEmpty();
    case NotationPackage.VIEW__TARGET_EDGES:
      return !getTargetEdges().isEmpty();
    case NotationPackage.VIEW__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.VIEW__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.VIEW__ELEMENT:
      return isSetElement();
    case NotationPackage.VIEW__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.VIEW__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void persistChildren()
  {
    if (eIsSet(NotationPackage.VIEW__TRANSIENT_CHILDREN))
    {
      getPersistedChildren().addAll(getTransientChildren());
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void insertChild(View child)
  {
    persistChildren();
    getPersistedChildren().add(child);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void insertChildAt(View child, int index)
  {
    persistChildren();
    if (getPersistedChildren().size() >= index)
    {
      getPersistedChildren().add(index, child);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void insertChild(View child, boolean persisted)
  {
    List children = null;
    if (persisted)
    {
      children = getPersistedChildren();
    }
    else
    {
      children = getTransientChildren();
    }
    children.add(child);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void removeChild(View child)
  {
    if (child.eContainer() == this)
    {
      EStructuralFeature eContainingFeature = child.eContainingFeature();
      if (eContainingFeature == NotationPackage.Literals.VIEW__TRANSIENT_CHILDREN)
      {
        getTransientChildren().remove(child);
      }
      else if (eContainingFeature == NotationPackage.Literals.VIEW__PERSISTED_CHILDREN)
      {
        getPersistedChildren().remove(child);
      }
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void persist()
  {
    EObject eContainer = eContainer();
    if (eContainer instanceof View)
    {
      EStructuralFeature eContaingFeature = eContainingFeature();
      if (eContaingFeature != null && eContaingFeature.isTransient())
      {
        View vContainer = (View)eContainer;
        vContainer.persistChildren();
      }
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public EList getVisibleChildren()
  {
    boolean persistedChildrenSet = eIsSet(NotationPackage.VIEW__PERSISTED_CHILDREN);
    boolean transientChildrenSet = eIsSet(NotationPackage.VIEW__TRANSIENT_CHILDREN);

    if (!persistedChildrenSet && !transientChildrenSet)
    {
      return ECollections.EMPTY_ELIST;
    }

    List _children = new ArrayList();
    if (persistedChildrenSet)
    {
      EList persistedChildren = getPersistedChildren();
      for (Iterator iter = persistedChildren.iterator(); iter.hasNext();)
      {
        View view = (View)iter.next();
        if (view.isVisible())
        {
          _children.add(view);
        }
      }
    }

    if (transientChildrenSet)
    {
      EList transientChildren = getTransientChildren();
      for (Iterator iter = transientChildren.iterator(); iter.hasNext();)
      {
        View view = (View)iter.next();
        if (view.isVisible())
        {
          _children.add(view);
        }
      }
    }

    return new BasicEList.UnmodifiableEList(_children.size(), _children.toArray());
  }

} // ViewImpl
