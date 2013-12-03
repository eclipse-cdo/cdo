/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Branch</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Branch#getProject <em>Project</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Branch#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Branch#getRestrictions <em>Restrictions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBranch()
 * @model
 * @generated
 */
public interface Branch extends ConfigurableItem
{
  /**
   * Returns the value of the '<em><b>Project</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.Project#getBranches <em>Branches</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Project</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Project</em>' container reference.
   * @see #setProject(Project)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBranch_Project()
   * @see org.eclipse.emf.cdo.releng.setup.Project#getBranches
   * @model opposite="branches" transient="false"
   * @generated
   */
  Project getProject();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Branch#getProject <em>Project</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Project</em>' container reference.
   * @see #getProject()
   * @generated
   */
  void setProject(Project value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBranch_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Branch#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Restrictions</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.Eclipse}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Restrictions</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Restrictions</em>' reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBranch_Restrictions()
   * @model
   * @generated
   */
  EList<Eclipse> getRestrictions();

} // Branch
