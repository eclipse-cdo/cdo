/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Dawn Generator</b></em>'.
 * 
 * @since 1.0 <!-- end-user-doc -->
 *        <p>
 *        The following features are supported:
 *        <ul>
 *        <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getEmfFragmentgenerator <em>Emf
 *        Fragmentgenerator</em>}</li>
 *        <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getGmfFragmentgenerator <em>Gmf
 *        Fragmentgenerator</em>}</li>
 *        </ul>
 *        </p>
 * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnGenerator()
 * @model
 * @generated
 */
public interface DawnGenerator extends EObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Emf Fragmentgenerator</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Emf Fragmentgenerator</em>' containment reference isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Emf Fragmentgenerator</em>' containment reference.
   * @see #setEmfFragmentgenerator(DawnEMFGenerator)
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnGenerator_EmfFragmentgenerator()
   * @model containment="true"
   * @generated
   */
  DawnEMFGenerator getEmfFragmentgenerator();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getEmfFragmentgenerator
   * <em>Emf Fragmentgenerator</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Emf Fragmentgenerator</em>' containment reference.
   * @see #getEmfFragmentgenerator()
   * @generated
   */
  void setEmfFragmentgenerator(DawnEMFGenerator value);

  /**
   * Returns the value of the '<em><b>Gmf Fragmentgenerator</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Gmf Fragmentgenerator</em>' containment reference isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Gmf Fragmentgenerator</em>' containment reference.
   * @see #setGmfFragmentgenerator(DawnGMFGenerator)
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnGenerator_GmfFragmentgenerator()
   * @model containment="true"
   * @generated
   */
  DawnGMFGenerator getGmfFragmentgenerator();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getGmfFragmentgenerator
   * <em>Gmf Fragmentgenerator</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Gmf Fragmentgenerator</em>' containment reference.
   * @see #getGmfFragmentgenerator()
   * @generated
   */
  void setGmfFragmentgenerator(DawnGMFGenerator value);

} // DawnGenerator
