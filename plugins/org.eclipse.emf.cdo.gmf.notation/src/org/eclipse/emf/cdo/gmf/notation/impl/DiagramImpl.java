/******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.MeasurementUnit;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Diagram</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DiagramImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DiagramImpl#getMeasurementUnit <em>Measurement Unit</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DiagramImpl#getPersistedEdges <em>Persisted Edges</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DiagramImpl#getTransientEdges <em>Transient Edges</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class DiagramImpl extends ViewImpl implements Diagram
{

  /**
  * The default value of the '{@link #getName() <em>Name</em>}' attribute.
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  * @see #getName()
  * @generated
  * @ordered
  */
  protected static final String NAME_EDEFAULT = ""; //$NON-NLS-1$

  /**
  * The default value of the '{@link #getMeasurementUnit() <em>Measurement Unit</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getMeasurementUnit()
  * @generated
  * @ordered
  */
  protected static final MeasurementUnit MEASUREMENT_UNIT_EDEFAULT = MeasurementUnit.HIMETRIC_LITERAL;

  private EContentsEList allEdges = null;

  protected static final EStructuralFeature[] edgesFeatures = new EStructuralFeature[] { NotationPackage.Literals.DIAGRAM__PERSISTED_EDGES,
      NotationPackage.Literals.DIAGRAM__TRANSIENT_EDGES };

  /**
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  * @generated
  */
  protected DiagramImpl()
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
    return NotationPackage.Literals.DIAGRAM;
  }

  /**
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  * @generated
  */
  @Override
  public String getName()
  {
    return (String)eDynamicGet(NotationPackage.DIAGRAM__NAME - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DIAGRAM__NAME, true, true);
  }

  /**
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setName(String newName)
  {
    eDynamicSet(NotationPackage.DIAGRAM__NAME - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DIAGRAM__NAME, newName);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public MeasurementUnit getMeasurementUnit()
  {
    return (MeasurementUnit)eDynamicGet(NotationPackage.DIAGRAM__MEASUREMENT_UNIT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DIAGRAM__MEASUREMENT_UNIT,
        true, true);
  }

  /**
   * <!-- begin-user-doc --> Set the Measurement Unit for this Diagram, the
   * Measurement unit can be set only once, the set method will not set the
   * value if it was already set <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void setMeasurementUnit(MeasurementUnit newMeasurementUnit)
  {
    if (!isSetMeasurementUnit())
    {
      setMeasurementUnitGen(newMeasurementUnit);
    } /*
       * else { throw new UnsupportedOperationException(); }
       */
  }

  /**
   * <!-- begin-user-doc --> Set the Measurement Unit for this Diagram, the
     * Measurement unit can be set only once, the set method will not set the
     * value if it was already set <!-- end-user-doc -->
   * @generated
   */
  public void setMeasurementUnitGen(MeasurementUnit newMeasurementUnit)
  {
    eDynamicSet(NotationPackage.DIAGRAM__MEASUREMENT_UNIT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DIAGRAM__MEASUREMENT_UNIT, newMeasurementUnit);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void unsetMeasurementUnit()
  {
    /* throw new UnsupportedOperationException(); */
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public boolean isSetMeasurementUnit()
  {
    return eDynamicIsSet(NotationPackage.DIAGRAM__MEASUREMENT_UNIT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DIAGRAM__MEASUREMENT_UNIT);
  }

  /**
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  */
  @Override
  public EList getEdges()
  {
    if (allEdges == null)
    {
      allEdges = new EContentsEList(this, edgesFeatures);
    }
    return allEdges;
  }

  /**
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  * @generated
  */
  @Override
  public EList getPersistedEdges()
  {
    return (EList)eDynamicGet(NotationPackage.DIAGRAM__PERSISTED_EDGES - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DIAGRAM__PERSISTED_EDGES, true, true);
  }

  /**
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  * @generated
  */
  @Override
  public EList getTransientEdges()
  {
    return (EList)eDynamicGet(NotationPackage.DIAGRAM__TRANSIENT_EDGES - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DIAGRAM__TRANSIENT_EDGES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public Edge createEdge(EClass eClass)
  {
    Edge newEdge = (Edge)eClass.getEPackage().getEFactoryInstance().create(eClass);
    getPersistedEdges().add(newEdge);
    return newEdge;
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
    case NotationPackage.DIAGRAM__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicRemove(otherEnd, msgs);
    case NotationPackage.DIAGRAM__SOURCE_EDGES:
      return ((InternalEList)getSourceEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.DIAGRAM__TARGET_EDGES:
      return ((InternalEList)getTargetEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.DIAGRAM__PERSISTED_CHILDREN:
      return ((InternalEList)getPersistedChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.DIAGRAM__STYLES:
      return ((InternalEList)getStyles()).basicRemove(otherEnd, msgs);
    case NotationPackage.DIAGRAM__TRANSIENT_CHILDREN:
      return ((InternalEList)getTransientChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.DIAGRAM__PERSISTED_EDGES:
      return ((InternalEList)getPersistedEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.DIAGRAM__TRANSIENT_EDGES:
      return ((InternalEList)getTransientEdges()).basicRemove(otherEnd, msgs);
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
    case NotationPackage.DIAGRAM__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.DIAGRAM__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.DIAGRAM__TYPE:
      return getType();
    case NotationPackage.DIAGRAM__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.DIAGRAM__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.DIAGRAM__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.DIAGRAM__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.DIAGRAM__STYLES:
      return getStyles();
    case NotationPackage.DIAGRAM__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.DIAGRAM__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.DIAGRAM__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.DIAGRAM__NAME:
      return getName();
    case NotationPackage.DIAGRAM__MEASUREMENT_UNIT:
      return getMeasurementUnit();
    case NotationPackage.DIAGRAM__PERSISTED_EDGES:
      return getPersistedEdges();
    case NotationPackage.DIAGRAM__TRANSIENT_EDGES:
      return getTransientEdges();
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
    case NotationPackage.DIAGRAM__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.DIAGRAM__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.DIAGRAM__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.DIAGRAM__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.DIAGRAM__SOURCE_EDGES:
      getSourceEdges().clear();
      getSourceEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.DIAGRAM__TARGET_EDGES:
      getTargetEdges().clear();
      getTargetEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.DIAGRAM__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.DIAGRAM__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.DIAGRAM__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.DIAGRAM__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.DIAGRAM__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.DIAGRAM__MEASUREMENT_UNIT:
      setMeasurementUnit((MeasurementUnit)newValue);
      return;
    case NotationPackage.DIAGRAM__PERSISTED_EDGES:
      getPersistedEdges().clear();
      getPersistedEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.DIAGRAM__TRANSIENT_EDGES:
      getTransientEdges().clear();
      getTransientEdges().addAll((Collection)newValue);
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
    case NotationPackage.DIAGRAM__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.DIAGRAM__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.DIAGRAM__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.DIAGRAM__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.DIAGRAM__SOURCE_EDGES:
      getSourceEdges().clear();
      return;
    case NotationPackage.DIAGRAM__TARGET_EDGES:
      getTargetEdges().clear();
      return;
    case NotationPackage.DIAGRAM__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.DIAGRAM__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.DIAGRAM__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.DIAGRAM__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case NotationPackage.DIAGRAM__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.DIAGRAM__MEASUREMENT_UNIT:
      unsetMeasurementUnit();
      return;
    case NotationPackage.DIAGRAM__PERSISTED_EDGES:
      getPersistedEdges().clear();
      return;
    case NotationPackage.DIAGRAM__TRANSIENT_EDGES:
      getTransientEdges().clear();
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
    case NotationPackage.DIAGRAM__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.DIAGRAM__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.DIAGRAM__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.DIAGRAM__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.DIAGRAM__SOURCE_EDGES:
      return !getSourceEdges().isEmpty();
    case NotationPackage.DIAGRAM__TARGET_EDGES:
      return !getTargetEdges().isEmpty();
    case NotationPackage.DIAGRAM__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.DIAGRAM__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.DIAGRAM__ELEMENT:
      return isSetElement();
    case NotationPackage.DIAGRAM__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.DIAGRAM__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    case NotationPackage.DIAGRAM__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.DIAGRAM__MEASUREMENT_UNIT:
      return isSetMeasurementUnit();
    case NotationPackage.DIAGRAM__PERSISTED_EDGES:
      return !getPersistedEdges().isEmpty();
    case NotationPackage.DIAGRAM__TRANSIENT_EDGES:
      return !getTransientEdges().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void insertEdge(Edge edge)
  {
    persistEdges();
    getPersistedEdges().add(edge);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void insertEdgeAt(Edge edge, int index)
  {
    persistEdges();
    if (getPersistedEdges().size() >= index)
    {
      getPersistedEdges().add(index, edge);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void insertEdge(Edge edge, boolean persisted)
  {
    List edges = null;
    if (persisted)
    {
      edges = getPersistedEdges();
    }
    else
    {
      edges = getTransientEdges();
    }
    edges.add(edge);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void persistEdges()
  {
    if (eIsSet(NotationPackage.DIAGRAM__TRANSIENT_EDGES))
    {
      List edges = new ArrayList(getTransientEdges());
      getPersistedEdges().addAll(edges);
      for (Iterator iterator = edges.iterator(); iterator.hasNext();)
      {
        Edge edge = (Edge)iterator.next();
        View sourceView = edge.getSource();
        View targetView = edge.getTarget();
        if (sourceView != null)
        {
          persistCompleteHierarchy(sourceView);
        }
        if (targetView != null)
        {
          persistCompleteHierarchy(targetView);
        }
      }
    }
  }

  /**
   * Persist the view passed in and all its parent views if they are
   * transient.
   * 
   * @param view
   *            the view to persist
   */
  private void persistCompleteHierarchy(View view)
  {
    view.persist();
    EObject container = view.eContainer();
    while (container instanceof View)
    {
      // if already persisted, quit
      EStructuralFeature sFeature = container.eContainingFeature();
      if (sFeature != null && !sFeature.isTransient())
      {
        break;
      }

      ((View)container).persist();
      container = container.eContainer();
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public void removeEdge(Edge edge)
  {
    if (edge.eContainer() == this)
    {
      EStructuralFeature eContainingFeature = edge.eContainingFeature();
      if (eContainingFeature == NotationPackage.Literals.DIAGRAM__TRANSIENT_EDGES)
      {
        getTransientEdges().remove(edge);
      }
      else if (eContainingFeature == NotationPackage.Literals.DIAGRAM__PERSISTED_EDGES)
      {
        getPersistedEdges().remove(edge);
      }
    }
  }

} // DiagramImpl
