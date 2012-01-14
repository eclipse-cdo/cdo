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
package org.eclipse.emf.cdo.defs;

import java.util.Date;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Audit Def</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.defs.CDOAuditDef#getTimeStamp <em>Time Stamp</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOAuditDef()
 * @model
 * @generated
 */
public interface CDOAuditDef extends CDOViewDef
{
  /**
   * Returns the value of the '<em><b>Time Stamp</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Time Stamp</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Time Stamp</em>' attribute.
   * @see #setTimeStamp(Date)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOAuditDef_TimeStamp()
   * @model required="true"
   * @generated
   */
  Date getTimeStamp();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.CDOAuditDef#getTimeStamp <em>Time Stamp</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Time Stamp</em>' attribute.
   * @see #getTimeStamp()
   * @generated
   */
  void setTimeStamp(Date value);

} // CDOAuditDef
