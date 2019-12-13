/*
 * Copyright (c) 2010-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.NodeD;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Node D</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getOtherNodes <em>Other Nodes</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getOppositeNode <em>Opposite Node</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NodeDImpl extends CDOObjectImpl implements NodeD
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected NodeDImpl()
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
    return Model3Package.eINSTANCE.getNodeD();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<NodeD> getChildren()
  {
    return (EList<NodeD>)eGet(Model3Package.eINSTANCE.getNodeD_Children(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NodeD getParent()
  {
    return (NodeD)eGet(Model3Package.eINSTANCE.getNodeD_Parent(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setParent(NodeD newParent)
  {
    eSet(Model3Package.eINSTANCE.getNodeD_Parent(), newParent);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(Model3Package.eINSTANCE.getNodeD_Name(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(Model3Package.eINSTANCE.getNodeD_Name(), newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<NodeD> getOtherNodes()
  {
    return (EList<NodeD>)eGet(Model3Package.eINSTANCE.getNodeD_OtherNodes(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NodeD getOppositeNode()
  {
    return (NodeD)eGet(Model3Package.eINSTANCE.getNodeD_OppositeNode(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setOppositeNode(NodeD newOppositeNode)
  {
    eSet(Model3Package.eINSTANCE.getNodeD_OppositeNode(), newOppositeNode);
  }

} // NodeDImpl
