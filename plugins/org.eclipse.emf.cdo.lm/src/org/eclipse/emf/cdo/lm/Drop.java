/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Drop</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.Drop#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Drop#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Drop#getBranchPoint <em>Branch Point</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getDrop()
 * @model
 * @generated
 */
public interface Drop extends FixedBaseline
{
  /**
   * Returns the value of the '<em><b>Type</b></em>' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Type</em>' reference.
   * @see #setType(DropType)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDrop_Type()
   * @model required="true"
   * @generated
   */
  DropType getType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Drop#getType <em>Type</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' reference.
   * @see #getType()
   * @generated
   */
  void setType(DropType value);

  /**
   * Returns the value of the '<em><b>Label</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Label</em>' attribute.
   * @see #setLabel(String)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDrop_Label()
   * @model
   * @generated
   */
  String getLabel();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Drop#getLabel <em>Label</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Label</em>' attribute.
   * @see #getLabel()
   * @generated
   */
  void setLabel(String value);

  /**
   * Returns the value of the '<em><b>Branch Point</b></em>' attribute. The
   * default value is <code>""</code>. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the value of the '<em>Branch Point</em>' attribute.
   * @see #setBranchPoint(CDOBranchPointRef)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDrop_BranchPoint()
   * @model default="" dataType="org.eclipse.emf.cdo.etypes.BranchPointRef"
   *        required="true"
   * @generated
   */
  @Override
  CDOBranchPointRef getBranchPoint();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Drop#getBranchPoint <em>Branch Point</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Branch Point</em>' attribute.
   * @see #getBranchPoint()
   * @generated
   */
  void setBranchPoint(CDOBranchPointRef value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  boolean isRelease();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EList<Stream> getBasedStreams();

} // Drop
