/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Retry Fail Over Strategy</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.defs.RetryFailOverStrategyDef#getRetries <em>Retries</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getRetryFailOverStrategyDef()
 * @model
 * @generated
 */
public interface RetryFailOverStrategyDef extends FailOverStrategyDef
{
  /**
   * Returns the value of the '<em><b>Retries</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Retries</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Retries</em>' attribute.
   * @see #isSetRetries()
   * @see #unsetRetries()
   * @see #setRetries(int)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getRetryFailOverStrategyDef_Retries()
   * @model unsettable="true"
   * @generated
   */
  int getRetries();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.RetryFailOverStrategyDef#getRetries <em>Retries</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Retries</em>' attribute.
   * @see #isSetRetries()
   * @see #unsetRetries()
   * @see #getRetries()
   * @generated
   */
  void setRetries(int value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.defs.RetryFailOverStrategyDef#getRetries <em>Retries</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetRetries()
   * @see #getRetries()
   * @see #setRetries(int)
   * @generated
   */
  void unsetRetries();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.defs.RetryFailOverStrategyDef#getRetries <em>Retries</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Retries</em>' attribute is set.
   * @see #unsetRetries()
   * @see #getRetries()
   * @see #setRetries(int)
   * @generated
   */
  boolean isSetRetries();

} // RetryFailOverStrategy
