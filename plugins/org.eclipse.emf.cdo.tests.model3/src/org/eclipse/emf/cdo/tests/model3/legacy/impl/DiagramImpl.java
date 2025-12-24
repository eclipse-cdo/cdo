/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

import org.eclipse.emf.cdo.tests.model3.Diagram;
import org.eclipse.emf.cdo.tests.model3.Edge;
import org.eclipse.emf.cdo.tests.model3.EdgeTarget;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Diagram</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.DiagramImpl#getEdges <em>Edges</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.DiagramImpl#getEdgeTargets <em>Edge Targets</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DiagramImpl extends EObjectImpl implements Diagram
{
  /**
   * The cached value of the '{@link #getEdges() <em>Edges</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEdges()
   * @generated
   * @ordered
   */
  protected EList<Edge> edges;

  /**
   * The cached value of the '{@link #getEdgeTargets() <em>Edge Targets</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEdgeTargets()
   * @generated
   * @ordered
   */
  protected EList<EdgeTarget> edgeTargets;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DiagramImpl()
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
    return Model3Package.eINSTANCE.getDiagram();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Edge> getEdges()
  {
    if (edges == null)
    {
      edges = new EObjectContainmentEList<>(Edge.class, this, Model3Package.DIAGRAM__EDGES);
    }
    return edges;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<EdgeTarget> getEdgeTargets()
  {
    if (edgeTargets == null)
    {
      edgeTargets = new EObjectContainmentEList<>(EdgeTarget.class, this, Model3Package.DIAGRAM__EDGE_TARGETS);
    }
    return edgeTargets;
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
    case Model3Package.DIAGRAM__EDGES:
      return ((InternalEList<?>)getEdges()).basicRemove(otherEnd, msgs);
    case Model3Package.DIAGRAM__EDGE_TARGETS:
      return ((InternalEList<?>)getEdgeTargets()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case Model3Package.DIAGRAM__EDGES:
      return getEdges();
    case Model3Package.DIAGRAM__EDGE_TARGETS:
      return getEdgeTargets();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model3Package.DIAGRAM__EDGES:
      getEdges().clear();
      getEdges().addAll((Collection<? extends Edge>)newValue);
      return;
    case Model3Package.DIAGRAM__EDGE_TARGETS:
      getEdgeTargets().clear();
      getEdgeTargets().addAll((Collection<? extends EdgeTarget>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
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
    case Model3Package.DIAGRAM__EDGES:
      getEdges().clear();
      return;
    case Model3Package.DIAGRAM__EDGE_TARGETS:
      getEdgeTargets().clear();
      return;
    }
    super.eUnset(featureID);
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
    case Model3Package.DIAGRAM__EDGES:
      return edges != null && !edges.isEmpty();
    case Model3Package.DIAGRAM__EDGE_TARGETS:
      return edgeTargets != null && !edgeTargets.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // DiagramImpl
