/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.security;

import org.eclipse.emf.cdo.security.PermissionFilter;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>LM Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.security.LMFilter#isRegex <em>Regex</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#getLMFilter()
 * @model abstract="true"
 * @generated
 */
public interface LMFilter extends PermissionFilter
{

  /**
   * Returns the value of the '<em><b>Regex</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Regex</em>' attribute.
   * @see #setRegex(boolean)
   * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#getLMFilter_Regex()
   * @model
   * @generated
   */
  boolean isRegex();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.security.LMFilter#isRegex <em>Regex</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Regex</em>' attribute.
   * @see #isRegex()
   * @generated
   */
  void setRegex(boolean value);
} // LMFilter
