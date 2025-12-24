/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.tests.model3.Edge;
import org.eclipse.emf.cdo.tests.model3.EdgeTarget;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Edge Target</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.EdgeTargetImpl#getOutgoingEdges <em>Outgoing Edges</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.EdgeTargetImpl#getIncomingEdges <em>Incoming Edges</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EdgeTargetImpl extends EObjectImpl implements EdgeTarget
{
  /**
   * The cached value of the '{@link #getOutgoingEdges() <em>Outgoing Edges</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOutgoingEdges()
   * @generated
   * @ordered
   */
  protected EList<Edge> outgoingEdges;

  /**
   * The cached value of the '{@link #getIncomingEdges() <em>Incoming Edges</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIncomingEdges()
   * @generated
   * @ordered
   */
  protected EList<Edge> incomingEdges;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EdgeTargetImpl()
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
    return Model3Package.eINSTANCE.getEdgeTarget();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Edge> getOutgoingEdges()
  {
    if (outgoingEdges == null)
    {
      outgoingEdges = new EObjectWithInverseResolvingEList<>(Edge.class, this, Model3Package.EDGE_TARGET__OUTGOING_EDGES, Model3Package.EDGE__SOURCE_NODE);
    }
    return outgoingEdges;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Edge> getIncomingEdges()
  {
    if (incomingEdges == null)
    {
      incomingEdges = new EObjectWithInverseResolvingEList<>(Edge.class, this, Model3Package.EDGE_TARGET__INCOMING_EDGES, Model3Package.EDGE__TARGET_NODE);
    }
    return incomingEdges;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model3Package.EDGE_TARGET__OUTGOING_EDGES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutgoingEdges()).basicAdd(otherEnd, msgs);
    case Model3Package.EDGE_TARGET__INCOMING_EDGES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getIncomingEdges()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case Model3Package.EDGE_TARGET__OUTGOING_EDGES:
      return ((InternalEList<?>)getOutgoingEdges()).basicRemove(otherEnd, msgs);
    case Model3Package.EDGE_TARGET__INCOMING_EDGES:
      return ((InternalEList<?>)getIncomingEdges()).basicRemove(otherEnd, msgs);
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
    case Model3Package.EDGE_TARGET__OUTGOING_EDGES:
      return getOutgoingEdges();
    case Model3Package.EDGE_TARGET__INCOMING_EDGES:
      return getIncomingEdges();
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
    case Model3Package.EDGE_TARGET__OUTGOING_EDGES:
      getOutgoingEdges().clear();
      getOutgoingEdges().addAll((Collection<? extends Edge>)newValue);
      return;
    case Model3Package.EDGE_TARGET__INCOMING_EDGES:
      getIncomingEdges().clear();
      getIncomingEdges().addAll((Collection<? extends Edge>)newValue);
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
    case Model3Package.EDGE_TARGET__OUTGOING_EDGES:
      getOutgoingEdges().clear();
      return;
    case Model3Package.EDGE_TARGET__INCOMING_EDGES:
      getIncomingEdges().clear();
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
    case Model3Package.EDGE_TARGET__OUTGOING_EDGES:
      return outgoingEdges != null && !outgoingEdges.isEmpty();
    case Model3Package.EDGE_TARGET__INCOMING_EDGES:
      return incomingEdges != null && !incomingEdges.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // EdgeTargetImpl
