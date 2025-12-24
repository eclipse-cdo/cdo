/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.cdo.common.branch.CDOBranchRef;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Change</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.Change#getBase <em>Base</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Change#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Change#getImpact <em>Impact</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Change#getBranch <em>Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Change#getDeliveries <em>Deliveries</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getChange()
 * @model
 * @generated
 */
public interface Change extends FloatingBaseline
{
  /**
   * Returns the value of the '<em><b>Base</b></em>' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Base</em>' reference.
   * @see #setBase(FixedBaseline)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getChange_Base()
   * @model
   * @generated
   */
  @Override
  FixedBaseline getBase();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Change#getBase <em>Base</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Base</em>' reference.
   * @see #getBase()
   * @generated
   */
  void setBase(FixedBaseline value);

  /**
   * Returns the value of the '<em><b>Label</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Label</em>' attribute.
   * @see #setLabel(String)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getChange_Label()
   * @model
   * @generated
   */
  String getLabel();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Change#getLabel <em>Label</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Label</em>' attribute.
   * @see #getLabel()
   * @generated
   */
  void setLabel(String value);

  /**
   * Returns the value of the '<em><b>Impact</b></em>' attribute. The literals are
   * from the enumeration {@link org.eclipse.emf.cdo.lm.Impact}. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Impact</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.Impact
   * @see #setImpact(Impact)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getChange_Impact()
   * @model required="true"
   * @generated
   */
  Impact getImpact();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Change#getImpact <em>Impact</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Impact</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.Impact
   * @see #getImpact()
   * @generated
   */
  void setImpact(Impact value);

  /**
   * Returns the value of the '<em><b>Branch</b></em>' attribute.
   * The default value is <code>""</code>.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Branch</em>' attribute.
   * @see #setBranch(CDOBranchRef)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getChange_Branch()
   * @model default="" dataType="org.eclipse.emf.cdo.etypes.BranchRef" required="true"
   * @generated
   */
  @Override
  CDOBranchRef getBranch();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Change#getBranch <em>Branch</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Branch</em>' attribute.
   * @see #getBranch()
   * @generated
   */
  void setBranch(CDOBranchRef value);

  /**
   * Returns the value of the '<em><b>Deliveries</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.Delivery}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Delivery#getChange <em>Change</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Deliveries</em>' reference list.
   * @see org.eclipse.emf.cdo.lm.LMPackage#getChange_Deliveries()
   * @see org.eclipse.emf.cdo.lm.Delivery#getChange
   * @model opposite="change"
   * @generated
   */
  @Override
  EList<Delivery> getDeliveries();

  public EList<Stream> getDeliveryStreams();

  public Delivery getDelivery(Stream stream);

  public BasePoint getDeliveryPoint(Stream stream);

  public EList<Stream> getDeliveryCandidateStreams();

  public boolean isDeliverable();

} // Change
