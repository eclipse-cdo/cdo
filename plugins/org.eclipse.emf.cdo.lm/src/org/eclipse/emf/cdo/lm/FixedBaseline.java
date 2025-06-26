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

import org.eclipse.emf.common.util.EList;

import org.eclipse.equinox.p2.metadata.Version;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Fixed
 * Baseline</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.FixedBaseline#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.FixedBaseline#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getFixedBaseline()
 * @model abstract="true"
 * @generated
 */
public interface FixedBaseline extends Baseline
{
  /**
   * @since 1.4
   */
  public static final String FINGER_PRINT_ANNOTATION_SOURCE = "http://www.eclipse.org/CDO/LM/FingerPrint";

  /**
   * @since 1.4
   */
  public static final String FINGER_PRINT_ANNOTATION_DETAIL_PARAM = "param";

  /**
   * @since 1.4
   */
  public static final String FINGER_PRINT_ANNOTATION_DETAIL_VALUE = "value";

  /**
   * @since 1.4
   */
  public static final String FINGER_PRINT_ANNOTATION_DETAIL_COUNT = "count";

  /**
   * Returns the value of the '<em><b>Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Version</em>' attribute.
   * @see #setVersion(Version)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getFixedBaseline_Version()
   * @model dataType="org.eclipse.emf.cdo.lm.modules.Version" required="true"
   * @generated
   */
  Version getVersion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.FixedBaseline#getVersion <em>Version</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Version</em>' attribute.
   * @see #getVersion()
   * @generated
   */
  void setVersion(Version value);

  /**
   * Returns the value of the '<em><b>Dependencies</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.Dependency}.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the value of the '<em>Dependencies</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.LMPackage#getFixedBaseline_Dependencies()
   * @model containment="true"
   * @generated
   */
  EList<Dependency> getDependencies();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EList<Change> getBasedChanges();

} // FixedBaseline
