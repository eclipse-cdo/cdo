/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge Target</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.EdgeTarget#getOutgoingEdges <em>Outgoing Edges</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.EdgeTarget#getIncomingEdges <em>Incoming Edges</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getEdgeTarget()
 * @model
 * @generated
 */
public interface EdgeTarget extends EObject
{
  /**
   * Returns the value of the '<em><b>Outgoing Edges</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.Edge}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.Edge#getSourceNode <em>Source Node</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Outgoing Edges</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Outgoing Edges</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getEdgeTarget_OutgoingEdges()
   * @see org.eclipse.emf.cdo.tests.model3.Edge#getSourceNode
   * @model opposite="sourceNode"
   * @generated
   */
  EList<Edge> getOutgoingEdges();

  /**
   * Returns the value of the '<em><b>Incoming Edges</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.Edge}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.Edge#getTargetNode <em>Target Node</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Incoming Edges</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Incoming Edges</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getEdgeTarget_IncomingEdges()
   * @see org.eclipse.emf.cdo.tests.model3.Edge#getTargetNode
   * @model opposite="targetNode"
   * @generated
   */
  EList<Edge> getIncomingEdges();

} // EdgeTarget
