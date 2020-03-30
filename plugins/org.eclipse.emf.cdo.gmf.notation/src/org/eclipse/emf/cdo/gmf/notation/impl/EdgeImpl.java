/******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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

import org.eclipse.gmf.runtime.notation.Anchor;
import org.eclipse.gmf.runtime.notation.Bendpoints;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.EdgeImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.EdgeImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.EdgeImpl#getBendpoints <em>Bendpoints</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.EdgeImpl#getSourceAnchor <em>Source Anchor</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.EdgeImpl#getTargetAnchor <em>Target Anchor</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class EdgeImpl extends ViewImpl implements Edge
{
  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected EdgeImpl()
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
    return NotationPackage.Literals.EDGE;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public View getSource()
  {
    return (View)eDynamicGet(NotationPackage.EDGE__SOURCE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__SOURCE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
    * @generated NOT
   */
  public NotificationChain basicSetSource(View newSource, NotificationChain msgs)
  {
    if (eContainingFeature() == NotationPackage.eINSTANCE.getDiagram_PersistedEdges())
    {
      if (newSource != null && newSource.eContainingFeature() == NotationPackage.eINSTANCE.getView_TransientChildren())
      {
        EObject container = newSource.eContainer();
        if (container != null && container instanceof View)
        {
          View parent = (View)container;
          parent.persistChildren();
        }
      }
    }
    return basicSetSourceGen(newSource, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSourceGen(View newSource, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newSource, NotationPackage.EDGE__SOURCE, msgs);
    return msgs;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setSource(View newSource)
  {
    eDynamicSet(NotationPackage.EDGE__SOURCE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__SOURCE, newSource);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public View getTarget()
  {
    return (View)eDynamicGet(NotationPackage.EDGE__TARGET - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__TARGET, true, true);
  }

  /**
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @generated NOT
   */
  public NotificationChain basicSetTarget(View newTarget, NotificationChain msgs)
  {
    if (eContainingFeature() == NotationPackage.eINSTANCE.getDiagram_PersistedEdges())
    {
      if (newTarget != null && newTarget.eContainingFeature() == NotationPackage.eINSTANCE.getView_TransientChildren())
      {
        EObject container = newTarget.eContainer();
        if (container != null && container instanceof View)
        {
          View parent = (View)container;
          parent.persistChildren();
        }
      }
    }
    return basicSetTargetGen(newTarget, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTargetGen(View newTarget, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newTarget, NotationPackage.EDGE__TARGET, msgs);
    return msgs;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setTarget(View newTarget)
  {
    eDynamicSet(NotationPackage.EDGE__TARGET - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__TARGET, newTarget);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public Bendpoints getBendpoints()
  {
    return (Bendpoints)eDynamicGet(NotationPackage.EDGE__BENDPOINTS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__BENDPOINTS, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  public NotificationChain basicSetBendpoints(Bendpoints newBendpoints, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newBendpoints, NotationPackage.EDGE__BENDPOINTS, msgs);
    return msgs;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setBendpoints(Bendpoints newBendpoints)
  {
    eDynamicSet(NotationPackage.EDGE__BENDPOINTS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__BENDPOINTS, newBendpoints);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public Anchor getSourceAnchor()
  {
    return (Anchor)eDynamicGet(NotationPackage.EDGE__SOURCE_ANCHOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__SOURCE_ANCHOR, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  public NotificationChain basicSetSourceAnchor(Anchor newSourceAnchor, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newSourceAnchor, NotationPackage.EDGE__SOURCE_ANCHOR, msgs);
    return msgs;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setSourceAnchor(Anchor newSourceAnchor)
  {
    eDynamicSet(NotationPackage.EDGE__SOURCE_ANCHOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__SOURCE_ANCHOR, newSourceAnchor);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public Anchor getTargetAnchor()
  {
    return (Anchor)eDynamicGet(NotationPackage.EDGE__TARGET_ANCHOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__TARGET_ANCHOR, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  public NotificationChain basicSetTargetAnchor(Anchor newTargetAnchor, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newTargetAnchor, NotationPackage.EDGE__TARGET_ANCHOR, msgs);
    return msgs;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setTargetAnchor(Anchor newTargetAnchor)
  {
    eDynamicSet(NotationPackage.EDGE__TARGET_ANCHOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.EDGE__TARGET_ANCHOR, newTargetAnchor);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Bendpoints createBendpoints(EClass eClass)
  {
    Bendpoints newBendpoints = (Bendpoints)eClass.getEPackage().getEFactoryInstance().create(eClass);
    setBendpoints(newBendpoints);
    return newBendpoints;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   */
  @Override
  public Anchor createSourceAnchor(EClass eClass)
  {
    Anchor newAnchor = (Anchor)eClass.getEPackage().getEFactoryInstance().create(eClass);
    setSourceAnchor(newAnchor);
    return newAnchor;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   */
  @Override
  public Anchor createTargetAnchor(EClass eClass)
  {
    Anchor newAnchor = (Anchor)eClass.getEPackage().getEFactoryInstance().create(eClass);
    setTargetAnchor(newAnchor);
    return newAnchor;
  }

  /**
  * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.EDGE__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicAdd(otherEnd, msgs);
    case NotationPackage.EDGE__SOURCE_EDGES:
      return ((InternalEList)getSourceEdges()).basicAdd(otherEnd, msgs);
    case NotationPackage.EDGE__TARGET_EDGES:
      return ((InternalEList)getTargetEdges()).basicAdd(otherEnd, msgs);
    case NotationPackage.EDGE__SOURCE:
      View source = getSource();
      if (source != null)
      {
        msgs = ((InternalEObject)source).eInverseRemove(this, NotationPackage.VIEW__SOURCE_EDGES, View.class, msgs);
      }
      return basicSetSource((View)otherEnd, msgs);
    case NotationPackage.EDGE__TARGET:
      View target = getTarget();
      if (target != null)
      {
        msgs = ((InternalEObject)target).eInverseRemove(this, NotationPackage.VIEW__TARGET_EDGES, View.class, msgs);
      }
      return basicSetTarget((View)otherEnd, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
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
    case NotationPackage.EDGE__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicRemove(otherEnd, msgs);
    case NotationPackage.EDGE__SOURCE_EDGES:
      return ((InternalEList)getSourceEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.EDGE__TARGET_EDGES:
      return ((InternalEList)getTargetEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.EDGE__PERSISTED_CHILDREN:
      return ((InternalEList)getPersistedChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.EDGE__STYLES:
      return ((InternalEList)getStyles()).basicRemove(otherEnd, msgs);
    case NotationPackage.EDGE__TRANSIENT_CHILDREN:
      return ((InternalEList)getTransientChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.EDGE__SOURCE:
      return basicSetSource(null, msgs);
    case NotationPackage.EDGE__TARGET:
      return basicSetTarget(null, msgs);
    case NotationPackage.EDGE__BENDPOINTS:
      return basicSetBendpoints(null, msgs);
    case NotationPackage.EDGE__SOURCE_ANCHOR:
      return basicSetSourceAnchor(null, msgs);
    case NotationPackage.EDGE__TARGET_ANCHOR:
      return basicSetTargetAnchor(null, msgs);
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
    case NotationPackage.EDGE__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.EDGE__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.EDGE__TYPE:
      return getType();
    case NotationPackage.EDGE__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.EDGE__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.EDGE__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.EDGE__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.EDGE__STYLES:
      return getStyles();
    case NotationPackage.EDGE__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.EDGE__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.EDGE__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.EDGE__SOURCE:
      return getSource();
    case NotationPackage.EDGE__TARGET:
      return getTarget();
    case NotationPackage.EDGE__BENDPOINTS:
      return getBendpoints();
    case NotationPackage.EDGE__SOURCE_ANCHOR:
      return getSourceAnchor();
    case NotationPackage.EDGE__TARGET_ANCHOR:
      return getTargetAnchor();
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
    case NotationPackage.EDGE__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.EDGE__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.EDGE__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.EDGE__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.EDGE__SOURCE_EDGES:
      getSourceEdges().clear();
      getSourceEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.EDGE__TARGET_EDGES:
      getTargetEdges().clear();
      getTargetEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.EDGE__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.EDGE__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.EDGE__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.EDGE__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.EDGE__SOURCE:
      setSource((View)newValue);
      return;
    case NotationPackage.EDGE__TARGET:
      setTarget((View)newValue);
      return;
    case NotationPackage.EDGE__BENDPOINTS:
      setBendpoints((Bendpoints)newValue);
      return;
    case NotationPackage.EDGE__SOURCE_ANCHOR:
      setSourceAnchor((Anchor)newValue);
      return;
    case NotationPackage.EDGE__TARGET_ANCHOR:
      setTargetAnchor((Anchor)newValue);
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
    case NotationPackage.EDGE__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.EDGE__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.EDGE__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.EDGE__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.EDGE__SOURCE_EDGES:
      getSourceEdges().clear();
      return;
    case NotationPackage.EDGE__TARGET_EDGES:
      getTargetEdges().clear();
      return;
    case NotationPackage.EDGE__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.EDGE__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.EDGE__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.EDGE__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case NotationPackage.EDGE__SOURCE:
      setSource((View)null);
      return;
    case NotationPackage.EDGE__TARGET:
      setTarget((View)null);
      return;
    case NotationPackage.EDGE__BENDPOINTS:
      setBendpoints((Bendpoints)null);
      return;
    case NotationPackage.EDGE__SOURCE_ANCHOR:
      setSourceAnchor((Anchor)null);
      return;
    case NotationPackage.EDGE__TARGET_ANCHOR:
      setTargetAnchor((Anchor)null);
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
    case NotationPackage.EDGE__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.EDGE__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.EDGE__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.EDGE__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.EDGE__SOURCE_EDGES:
      return !getSourceEdges().isEmpty();
    case NotationPackage.EDGE__TARGET_EDGES:
      return !getTargetEdges().isEmpty();
    case NotationPackage.EDGE__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.EDGE__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.EDGE__ELEMENT:
      return isSetElement();
    case NotationPackage.EDGE__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.EDGE__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    case NotationPackage.EDGE__SOURCE:
      return getSource() != null;
    case NotationPackage.EDGE__TARGET:
      return getTarget() != null;
    case NotationPackage.EDGE__BENDPOINTS:
      return getBendpoints() != null;
    case NotationPackage.EDGE__SOURCE_ANCHOR:
      return getSourceAnchor() != null;
    case NotationPackage.EDGE__TARGET_ANCHOR:
      return getTargetAnchor() != null;
    }
    return eDynamicIsSet(featureID);
  }

} // EdgeImpl
