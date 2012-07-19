/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>AOperation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.AOperation#getParameters <em>Parameters</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAOperation()
 * @model
 * @generated
 */
public interface AOperation extends AClassChild
{
  /**
   * Returns the value of the '<em><b>Parameters</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AParameter}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Parameters</em>' containment reference list.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAOperation_Parameters()
   * @model containment="true"
   * @generated
   */
  EList<AParameter> getParameters();

} // AOperation
