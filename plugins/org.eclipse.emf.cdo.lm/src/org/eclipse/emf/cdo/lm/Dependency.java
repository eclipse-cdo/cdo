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

import org.eclipse.equinox.p2.metadata.VersionRange;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Dependency</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.Dependency#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Dependency#getVersionRange <em>Version Range</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getDependency()
 * @model
 * @generated
 */
public interface Dependency extends StreamElement
{
  /**
   * Returns the value of the '<em><b>Target</b></em>' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Target</em>' reference.
   * @see #setTarget(org.eclipse.emf.cdo.lm.Module)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDependency_Target()
   * @model required="true"
   * @generated
   */
  org.eclipse.emf.cdo.lm.Module getTarget();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Dependency#getTarget <em>Target</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Target</em>' reference.
   * @see #getTarget()
   * @generated
   */
  void setTarget(org.eclipse.emf.cdo.lm.Module value);

  /**
   * Returns the value of the '<em><b>Version Range</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Version Range</em>' attribute.
   * @see #setVersionRange(VersionRange)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDependency_VersionRange()
   * @model dataType="org.eclipse.emf.cdo.lm.modules.VersionRange"
   *        required="true"
   * @generated
   */
  VersionRange getVersionRange();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Dependency#getVersionRange <em>Version Range</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Version Range</em>' attribute.
   * @see #getVersionRange()
   * @generated
   */
  void setVersionRange(VersionRange value);

} // Dependency
