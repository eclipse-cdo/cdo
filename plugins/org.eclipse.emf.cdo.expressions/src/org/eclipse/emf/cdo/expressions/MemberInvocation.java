/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Member Invocation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.MemberInvocation#getObject <em>Object</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getMemberInvocation()
 * @model
 * @generated
 */
public interface MemberInvocation extends Invocation
{
  /**
   * Returns the value of the '<em><b>Object</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Object</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Object</em>' containment reference.
   * @see #setObject(Expression)
   * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getMemberInvocation_Object()
   * @model containment="true" required="true"
   * @generated
   */
  Expression getObject();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.expressions.MemberInvocation#getObject <em>Object</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Object</em>' containment reference.
   * @see #getObject()
   * @generated
   */
  void setObject(Expression value);

} // MemberInvocation
