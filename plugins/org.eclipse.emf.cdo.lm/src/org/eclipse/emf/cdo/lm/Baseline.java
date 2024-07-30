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
import org.eclipse.emf.cdo.lm.impl.BaselineImpl;

import java.util.Comparator;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Baseline</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.Baseline#getStream <em>Stream</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Baseline#isFloating <em>Floating</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getBaseline()
 * @model abstract="true"
 * @generated
 */
public interface Baseline extends StreamElement
{
  public static final Comparator<Object> COMPARATOR = //
      Comparator.comparingLong(BaselineImpl::time) //
          .thenComparingInt(BaselineImpl::type) //
          .thenComparing(BaselineImpl::name) //
          .reversed();

  /**
   * Returns the value of the '<em><b>Stream</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Stream#getContents <em>Contents</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Stream</em>' container reference.
   * @see #setStream(Stream)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getBaseline_Stream()
   * @see org.eclipse.emf.cdo.lm.Stream#getContents
   * @model opposite="contents" transient="false"
   * @generated
   */
  @Override
  Stream getStream();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Baseline#getStream <em>Stream</em>}' container reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Stream</em>' container reference.
   * @see #getStream()
   * @generated
   */
  void setStream(Stream value);

  /**
   * Returns the value of the '<em><b>Floating</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Floating</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.LMPackage#getBaseline_Floating()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  boolean isFloating();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  String getName();

  String getTypeName();

  String getTypeAndName();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation" dataType="org.eclipse.emf.cdo.etypes.BranchPointRef"
   * @generated
   */
  CDOBranchPointRef getBranchPoint();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  long getBaseTimeStamp();

} // Baseline
