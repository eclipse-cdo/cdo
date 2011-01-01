/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Package Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.defs.EPackageDef#getNsURI <em>Ns URI</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getEPackageDef()
 * @model abstract="true"
 * @generated
 */
public interface EPackageDef extends Def
{
  /**
   * Returns the value of the '<em><b>Ns URI</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ns URI</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Ns URI</em>' attribute.
   * @see #setNsURI(String)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getEPackageDef_NsURI()
   * @model required="true"
   * @generated
   */
  String getNsURI();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.EPackageDef#getNsURI <em>Ns URI</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Ns URI</em>' attribute.
   * @see #getNsURI()
   * @generated
   */
  void setNsURI(String value);

} // EPackageDef
