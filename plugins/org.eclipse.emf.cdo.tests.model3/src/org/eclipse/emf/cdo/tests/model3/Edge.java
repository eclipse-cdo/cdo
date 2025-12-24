/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.Edge#getSourceNode <em>Source Node</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.Edge#getTargetNode <em>Target Node</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getEdge()
 * @model
 * @generated
 */
public interface Edge extends EObject
{
  /**
   * Returns the value of the '<em><b>Source Node</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.EdgeTarget#getOutgoingEdges <em>Outgoing Edges</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Node</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Node</em>' reference.
   * @see #setSourceNode(EdgeTarget)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getEdge_SourceNode()
   * @see org.eclipse.emf.cdo.tests.model3.EdgeTarget#getOutgoingEdges
   * @model opposite="outgoingEdges" required="true"
   * @generated
   */
  EdgeTarget getSourceNode();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.Edge#getSourceNode <em>Source Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source Node</em>' reference.
   * @see #getSourceNode()
   * @generated
   */
  void setSourceNode(EdgeTarget value);

  /**
   * Returns the value of the '<em><b>Target Node</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.EdgeTarget#getIncomingEdges <em>Incoming Edges</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target Node</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Node</em>' reference.
   * @see #setTargetNode(EdgeTarget)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getEdge_TargetNode()
   * @see org.eclipse.emf.cdo.tests.model3.EdgeTarget#getIncomingEdges
   * @model opposite="incomingEdges" required="true"
   * @generated
   */
  EdgeTarget getTargetNode();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.Edge#getTargetNode <em>Target Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target Node</em>' reference.
   * @see #getTargetNode()
   * @generated
   */
  void setTargetNode(EdgeTarget value);

} // Edge
