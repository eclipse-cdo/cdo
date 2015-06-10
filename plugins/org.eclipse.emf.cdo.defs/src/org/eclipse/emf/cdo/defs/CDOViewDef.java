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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO View Def</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.defs.CDOViewDef#getCdoSessionDef <em>Cdo Session Def</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOViewDef()
 * @model
 * @generated
 */
public interface CDOViewDef extends Def
{
  /**
   * Returns the value of the '<em><b>Cdo Session Def</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cdo Session Def</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cdo Session Def</em>' reference.
   * @see #setCdoSessionDef(CDOSessionDef)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOViewDef_CdoSessionDef()
   * @model required="true"
   * @generated
   */
  CDOSessionDef getCdoSessionDef();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.CDOViewDef#getCdoSessionDef <em>Cdo Session Def</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Cdo Session Def</em>' reference.
   * @see #getCdoSessionDef()
   * @generated
   */
  void setCdoSessionDef(CDOSessionDef value);

} // CDOViewDef
