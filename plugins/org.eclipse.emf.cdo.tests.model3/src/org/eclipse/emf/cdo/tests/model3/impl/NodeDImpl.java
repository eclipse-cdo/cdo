/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getChildren <em>Children</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getParent <em>Parent</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getOtherNodes <em>Other Nodes</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model3.impl.NodeDImpl#getOppositeNode <em>Opposite Node</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class NodeDImpl extends CDOObjectImpl implements NodeD
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected NodeDImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model3Package.Literals.NODE_D;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<NodeD> getChildren()
  {
    return (EList<NodeD>)eGet(Model3Package.Literals.NODE_D__CHILDREN, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NodeD getParent()
  {
    return (NodeD)eGet(Model3Package.Literals.NODE_D__PARENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setParent(NodeD newParent)
  {
    eSet(Model3Package.Literals.NODE_D__PARENT, newParent);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    return (String)eGet(Model3Package.Literals.NODE_D__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eSet(Model3Package.Literals.NODE_D__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<NodeD> getOtherNodes()
  {
    return (EList<NodeD>)eGet(Model3Package.Literals.NODE_D__OTHER_NODES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NodeD getOppositeNode()
  {
    return (NodeD)eGet(Model3Package.Literals.NODE_D__OPPOSITE_NODE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setOppositeNode(NodeD newOppositeNode)
  {
    eSet(Model3Package.Literals.NODE_D__OPPOSITE_NODE, newOppositeNode);
  }

} // NodeDImpl
