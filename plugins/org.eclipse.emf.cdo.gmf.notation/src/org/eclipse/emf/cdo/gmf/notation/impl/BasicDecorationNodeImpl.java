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
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.BasicDecorationNode;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
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
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Basic Decoration Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated NOT
 * @since 1.2
 */
public class BasicDecorationNodeImpl extends org.eclipse.emf.cdo.ecore.impl.EModelElementImpl implements BasicDecorationNode
{
  protected static final boolean VISIBLE_EDEFAULT = true;

  protected static final String TYPE_EDEFAULT = "";

  protected static final boolean MUTABLE_EDEFAULT = false;

  protected static final EStructuralFeature[] childrenFeatures = new EStructuralFeature[] { NotationPackage.Literals.VIEW__PERSISTED_CHILDREN,
      NotationPackage.Literals.VIEW__TRANSIENT_CHILDREN };

  protected static final int ESTATIC_FEATURE_COUNT = 0;

  protected BasicDecorationNodeImpl()
  {
    super();
    setVisible(VISIBLE_EDEFAULT);
  }

  @Override
  protected EClass eStaticClass()
  {
    return NotationPackage.Literals.BASIC_DECORATION_NODE;
  }

  public EList getSourceEdges()
  {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_SourceEdges(), 0, null);
  }

  public EList getTargetEdges()
  {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_TargetEdges(), 0, null);
  }

  public EList getPersistedChildren()
  {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_PersistedChildren(), 0, null);
  }

  public EList getTransientChildren()
  {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_TransientChildren(), 0, null);
  }

  public EList getChildren()
  {
    return ECollections.emptyEList();
  }

  public EList getStyles()
  {
    return new EcoreEList.UnmodifiableEList(this, NotationPackage.eINSTANCE.getView_TransientChildren(), 0, null);
  }

  public Style getStyle(EClass eClass)
  {
    if (eClass != null && NotationPackage.eINSTANCE.getStyle().isSuperTypeOf(eClass))
    {
      EClass thisEClass = eClass();
      if (eClass.isSuperTypeOf(thisEClass) || eClass == thisEClass)
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

  public LayoutConstraint createLayoutConstraint(EClass eClass)
  {
    LayoutConstraint newLayoutConstraint = (LayoutConstraint)eClass.getEPackage().getEFactoryInstance().create(eClass);
    setLayoutConstraint(newLayoutConstraint);
    return newLayoutConstraint;
  }

  public void setLayoutConstraint(LayoutConstraint newLayoutConstraint)
  {
    throw new UnsupportedOperationException("BasicDecorationNodeImpl#setLayoutConstraint(LayoutConstraint newLayoutConstraint)"); //$NON-NLS-1$
  }

  public NotificationChain basicSetLayoutConstraint(LayoutConstraint newLayoutConstraint, NotificationChain msgs)
  {
    return msgs;
  }

  public LayoutConstraint getLayoutConstraint()
  {
    return null;
  }

  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.BASIC_DECORATION_NODE__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicAdd(otherEnd, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }

  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.BASIC_DECORATION_NODE__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
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
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.BASIC_DECORATION_NODE__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.BASIC_DECORATION_NODE__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.BASIC_DECORATION_NODE__LAYOUT_CONSTRAINT:
      return getLayoutConstraint();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
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

  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
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

  @SuppressWarnings("null")
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
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

  public boolean isVisible()
  {
    return ((Boolean)eDynamicGet(NotationPackage.VIEW__VISIBLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__VISIBLE, true, true)).booleanValue();
  }

  public void setVisible(boolean newVisible)
  {
    eDynamicSet(NotationPackage.VIEW__VISIBLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__VISIBLE, new Boolean(newVisible));
  }

  public String getType()
  {
    return (String)eDynamicGet(NotationPackage.VIEW__TYPE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__TYPE, true, true);
  }

  public void setType(String newType)
  {
    setTypeGen(newType == null ? null : newType.intern());
  }

  public void setTypeGen(String newType)
  {
    eDynamicSet(NotationPackage.VIEW__TYPE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__TYPE, newType);
  }

  public boolean isMutable()
  {
    return ((Boolean)eDynamicGet(NotationPackage.VIEW__MUTABLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__MUTABLE, true, true)).booleanValue();
  }

  public void setMutable(boolean newMutable)
  {
    eDynamicSet(NotationPackage.VIEW__MUTABLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__MUTABLE, new Boolean(newMutable));
  }

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

  public EObject getElementGen()
  {
    return (EObject)eDynamicGet(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT, true, true);
  }

  public EObject basicGetElement()
  {
    return (EObject)eDynamicGet(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT, false, true);
  }

  public void setElement(EObject newElement)
  {
    eDynamicSet(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT, newElement);
  }

  public void unsetElement()
  {
    eDynamicUnset(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT);
  }

  public boolean isSetElement()
  {
    return eDynamicIsSet(NotationPackage.VIEW__ELEMENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.VIEW__ELEMENT);
  }

  public Diagram getDiagram()
  {
    Diagram diagram = basicGetDiagram();
    return diagram != null && diagram.eIsProxy() ? (Diagram)eResolveProxy((InternalEObject)diagram) : diagram;
  }

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

  public Node createChild(EClass eClass)
  {
    Node newChild = (Node)eClass.getEPackage().getEFactoryInstance().create(eClass);
    getPersistedChildren().add(newChild);
    return newChild;
  }

  public Style createStyle(EClass eClass)
  {
    Style newStyle = (Style)eClass.getEPackage().getEFactoryInstance().create(eClass);
    getStyles().add(newStyle);
    return newStyle;
  }

  public void persistChildren()
  {
    if (eIsSet(NotationPackage.VIEW__TRANSIENT_CHILDREN))
    {
      getPersistedChildren().addAll(getTransientChildren());
    }
  }

  public void insertChild(View child)
  {
    persistChildren();
    getPersistedChildren().add(child);
  }

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

  public void insertChildAt(View child, int index)
  {
    persistChildren();
    if (getPersistedChildren().size() >= index)
    {
      getPersistedChildren().add(index, child);
    }
  }

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

} // BasicDecorationNodeImpl
