/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Dawn EMF Generator</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator#getEmfGenModel <em>Emf
 * Gen Model</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelPackage#getDawnEMFGenerator()
 * @model
 * @generated
 * @author Martin Fluegge
 */
public interface DawnEMFGenerator extends DawnFragmentGenerator
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Emf Gen Model</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Emf Gen Model</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Emf Gen Model</em>' reference.
   * @see #setEmfGenModel(GenModel)
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelPackage#getDawnEMFGenerator_EmfGenModel()
   * @model
   * @generated
   */
  GenModel getEmfGenModel();

  /**
   * Sets the value of the '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator#getEmfGenModel
   * <em>Emf Gen Model</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Emf Gen Model</em>' reference.
   * @see #getEmfGenModel()
   * @generated
   */
  void setEmfGenModel(GenModel value);

} // DawnEMFGenerator
