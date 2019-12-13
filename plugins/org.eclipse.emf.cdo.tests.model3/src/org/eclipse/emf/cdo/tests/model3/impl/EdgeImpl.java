/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.tests.model3.Edge;
import org.eclipse.emf.cdo.tests.model3.EdgeTarget;
import org.eclipse.emf.cdo.tests.model3.Model3Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.EdgeImpl#getSourceNode <em>Source Node</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.EdgeImpl#getTargetNode <em>Target Node</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EdgeImpl extends CDOObjectImpl implements Edge
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
    return Model3Package.eINSTANCE.getEdge();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EdgeTarget getSourceNode()
  {
    return (EdgeTarget)eGet(Model3Package.eINSTANCE.getEdge_SourceNode(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSourceNode(EdgeTarget newSourceNode)
  {
    eSet(Model3Package.eINSTANCE.getEdge_SourceNode(), newSourceNode);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EdgeTarget getTargetNode()
  {
    return (EdgeTarget)eGet(Model3Package.eINSTANCE.getEdge_TargetNode(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTargetNode(EdgeTarget newTargetNode)
  {
    eSet(Model3Package.eINSTANCE.getEdge_TargetNode(), newTargetNode);
  }

} // EdgeImpl
