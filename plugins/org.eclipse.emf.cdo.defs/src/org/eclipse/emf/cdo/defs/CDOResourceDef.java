/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.util.defs.Def;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Resource Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.defs.CDOResourceDef#getCdoTransaction <em>Cdo Transaction</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.defs.CDOResourceDef#getResourceMode <em>Resource Mode</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.defs.CDOResourceDef#getPath <em>Path</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOResourceDef()
 * @model
 * @generated
 */
public interface CDOResourceDef extends Def
{
  /**
   * Returns the value of the '<em><b>Cdo Transaction</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cdo Transaction</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cdo Transaction</em>' reference.
   * @see #setCdoTransaction(CDOTransactionDef)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOResourceDef_CdoTransaction()
   * @model required="true"
   * @generated
   */
  CDOTransactionDef getCdoTransaction();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.CDOResourceDef#getCdoTransaction <em>Cdo Transaction</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Cdo Transaction</em>' reference.
   * @see #getCdoTransaction()
   * @generated
   */
  void setCdoTransaction(CDOTransactionDef value);

  /**
   * Returns the value of the '<em><b>Resource Mode</b></em>' attribute.
   * The default value is <code>"null"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.defs.ResourceMode}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Resource Mode</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Resource Mode</em>' attribute.
   * @see org.eclipse.emf.cdo.defs.ResourceMode
   * @see #setResourceMode(ResourceMode)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOResourceDef_ResourceMode()
   * @model default="null" required="true"
   * @generated
   */
  ResourceMode getResourceMode();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.CDOResourceDef#getResourceMode <em>Resource Mode</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Resource Mode</em>' attribute.
   * @see org.eclipse.emf.cdo.defs.ResourceMode
   * @see #getResourceMode()
   * @generated
   */
  void setResourceMode(ResourceMode value);

  /**
   * Returns the value of the '<em><b>Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Path</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Path</em>' attribute.
   * @see #setPath(String)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOResourceDef_Path()
   * @model required="true"
   * @generated
   */
  String getPath();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.CDOResourceDef#getPath <em>Path</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Path</em>' attribute.
   * @see #getPath()
   * @generated
   */
  void setPath(String value);

} // CDOResourceDef
