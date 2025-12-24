/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
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
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Node E</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.NodeE#getMainNode <em>Main Node</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.NodeE#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.NodeE#getOtherNodes <em>Other Nodes</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeE()
 * @model
 * @generated
 */
public interface NodeE extends EObject
{
  /**
   * Returns the value of the '<em><b>Main Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Main Node</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Main Node</em>' reference.
   * @see #setMainNode(NodeA)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeE_MainNode()
   * @model
   * @generated
   */
  NodeA getMainNode();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.NodeE#getMainNode <em>Main Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Main Node</em>' reference.
   * @see #getMainNode()
   * @generated
   */
  void setMainNode(NodeA value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeE_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.NodeE#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Other Nodes</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.NodeA}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Other Nodes</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Other Nodes</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getNodeE_OtherNodes()
   * @model
   * @generated
   */
  EList<NodeA> getOtherNodes();

} // NodeE
