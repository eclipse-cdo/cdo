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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Dawn Fragment Generator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getFragmentName <em>Fragment Name
 * </em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getDawnEditorClassName <em>Dawn Editor
 * Class Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getDawnGenerator <em>Dawn Generator
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnFragmentGenerator()
 * @model
 * @generated
 * @since 1.0
 */
public interface DawnFragmentGenerator extends EObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Fragment Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Fragment Name</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Fragment Name</em>' attribute.
   * @see #setFragmentName(String)
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnFragmentGenerator_FragmentName()
   * @model
   * @generated
   */
  String getFragmentName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getFragmentName
   * <em>Fragment Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Fragment Name</em>' attribute.
   * @see #getFragmentName()
   * @generated
   */
  void setFragmentName(String value);

  /**
   * Returns the value of the '<em><b>Dawn Editor Class Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Dawn Editor Class Name</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Dawn Editor Class Name</em>' attribute.
   * @see #setDawnEditorClassName(String)
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnFragmentGenerator_DawnEditorClassName()
   * @model
   * @generated
   */
  String getDawnEditorClassName();

  /**
   * Sets the value of the '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getDawnEditorClassName
   * <em>Dawn Editor Class Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Dawn Editor Class Name</em>' attribute.
   * @see #getDawnEditorClassName()
   * @generated
   */
  void setDawnEditorClassName(String value);

  /**
   * Returns the value of the '<em><b>Dawn Generator</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Dawn Generator</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Dawn Generator</em>' reference.
   * @see #setDawnGenerator(DawnGenerator)
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnFragmentGenerator_DawnGenerator()
   * @model
   * @generated
   */
  DawnGenerator getDawnGenerator();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getDawnGenerator
   * <em>Dawn Generator</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Dawn Generator</em>' reference.
   * @see #getDawnGenerator()
   * @generated
   */
  void setDawnGenerator(DawnGenerator value);

} // DawnFragmentGenerator
