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
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Dawn Generator</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getConflictColor <em>Conflict Color</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getLocalLockColor <em>Local Lock Color</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getRemoteLockColor <em>Remote Lock Color</em>}
 * </li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnGenerator()
 * @model
 * @generated
 * @since 1.0
 */
public interface DawnGenerator extends EObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Conflict Color</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Conflict Color</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Conflict Color</em>' attribute.
   * @see #setConflictColor(String)
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnGenerator_ConflictColor()
   * @model
   * @generated
   */
  String getConflictColor();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getConflictColor
   * <em>Conflict Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Conflict Color</em>' attribute.
   * @see #getConflictColor()
   * @generated
   */
  void setConflictColor(String value);

  /**
   * Returns the value of the '<em><b>Local Lock Color</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Local Lock Color</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Local Lock Color</em>' attribute.
   * @see #setLocalLockColor(String)
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnGenerator_LocalLockColor()
   * @model
   * @generated
   */
  String getLocalLockColor();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getLocalLockColor
   * <em>Local Lock Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Local Lock Color</em>' attribute.
   * @see #getLocalLockColor()
   * @generated
   */
  void setLocalLockColor(String value);

  /**
   * Returns the value of the '<em><b>Remote Lock Color</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Remote Lock Color</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Remote Lock Color</em>' attribute.
   * @see #setRemoteLockColor(String)
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage#getDawnGenerator_RemoteLockColor()
   * @model
   * @generated
   */
  String getRemoteLockColor();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getRemoteLockColor
   * <em>Remote Lock Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Remote Lock Color</em>' attribute.
   * @see #getRemoteLockColor()
   * @generated
   */
  void setRemoteLockColor(String value);

} // DawnGenerator
