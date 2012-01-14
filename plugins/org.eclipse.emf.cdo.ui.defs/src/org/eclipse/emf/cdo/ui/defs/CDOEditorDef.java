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

import org.eclipse.emf.cdo.defs.CDOViewDef;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Editor Def</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ui.defs.CDOEditorDef#getCdoView <em>Cdo View</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ui.defs.CDOEditorDef#getResourcePath <em>Resource Path</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.ui.defs.CDOUIDefsPackage#getCDOEditorDef()
 * @model
 * @generated
 */
public interface CDOEditorDef extends EditorDef
{
  /**
   * Returns the value of the '<em><b>Cdo View</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cdo View</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cdo View</em>' reference.
   * @see #setCdoView(CDOViewDef)
   * @see org.eclipse.emf.cdo.ui.defs.CDOUIDefsPackage#getCDOEditorDef_CdoView()
   * @model required="true"
   * @generated
   */
  CDOViewDef getCdoView();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ui.defs.CDOEditorDef#getCdoView <em>Cdo View</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Cdo View</em>' reference.
   * @see #getCdoView()
   * @generated
   */
  void setCdoView(CDOViewDef value);

  /**
   * Returns the value of the '<em><b>Resource Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Resource Path</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Resource Path</em>' attribute.
   * @see #setResourcePath(String)
   * @see org.eclipse.emf.cdo.ui.defs.CDOUIDefsPackage#getCDOEditorDef_ResourcePath()
   * @model required="true"
   * @generated
   */
  String getResourcePath();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ui.defs.CDOEditorDef#getResourcePath <em>Resource Path</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Resource Path</em>' attribute.
   * @see #getResourcePath()
   * @generated
   */
  void setResourcePath(String value);

} // CDOEditorDef
