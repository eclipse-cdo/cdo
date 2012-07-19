/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.defs;

import org.eclipse.net4j.util.defs.Def;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Editor Def</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ui.defs.EditorDef#getEditorID <em>Editor ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.ui.defs.CDOUIDefsPackage#getEditorDef()
 * @model
 * @generated
 */
public interface EditorDef extends Def
{
  /**
   * Returns the value of the '<em><b>Editor ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Editor ID</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Editor ID</em>' attribute.
   * @see #setEditorID(String)
   * @see org.eclipse.emf.cdo.ui.defs.CDOUIDefsPackage#getEditorDef_EditorID()
   * @model required="true"
   * @generated
   */
  String getEditorID();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ui.defs.EditorDef#getEditorID <em>Editor ID</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Editor ID</em>' attribute.
   * @see #getEditorID()
   * @generated
   */
  void setEditorID(String value);

} // EditorDef
