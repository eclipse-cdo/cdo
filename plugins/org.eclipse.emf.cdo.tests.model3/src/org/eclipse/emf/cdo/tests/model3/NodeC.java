/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Node C</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.NodeC#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.NodeC#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.NodeC#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.NodeC#getOtherNodes <em>Other Nodes</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.NodeC#getOppositeNodes <em>Opposite Nodes</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeC()
 * @model
 * @generated
 */
public interface NodeC extends EObject
{
  /**
   * Returns the value of the '<em><b>Children</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.NodeC}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Children</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Children</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeC_Children()
   * @see org.eclipse.emf.cdo.tests.model3.NodeC#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  EList<NodeC> getChildren();

  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(NodeC)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeC_Parent()
   * @see org.eclipse.emf.cdo.tests.model3.NodeC#getChildren
   * @model opposite="children" transient="false"
   * @generated
   */
  NodeC getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getParent <em>Parent</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(NodeC value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeC_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getName <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Other Nodes</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.NodeC}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getOppositeNodes <em>Opposite Nodes</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Other Nodes</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Other Nodes</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeC_OtherNodes()
   * @see org.eclipse.emf.cdo.tests.model3.NodeC#getOppositeNodes
   * @model opposite="oppositeNodes"
   * @generated
   */
  EList<NodeC> getOtherNodes();

  /**
   * Returns the value of the '<em><b>Opposite Nodes</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.NodeC}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getOtherNodes <em>Other Nodes</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Opposite Nodes</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Opposite Nodes</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeC_OppositeNodes()
   * @see org.eclipse.emf.cdo.tests.model3.NodeC#getOtherNodes
   * @model opposite="otherNodes"
   * @generated
   */
  EList<NodeC> getOppositeNodes();

} // NodeC
