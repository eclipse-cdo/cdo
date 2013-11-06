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
 * A representation of the model object '<em><b>Materialization Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MaterializationTask#getRootComponents <em>Root Components</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MaterializationTask#getSourceLocators <em>Source Locators</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MaterializationTask#getP2Repositories <em>P2 Repositories</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMaterializationTask()
 * @model
 * @generated
 */
public interface MaterializationTask extends BasicMaterializationTask
{
  /**
   * Returns the value of the '<em><b>Source Locators</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.SourceLocator}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Locators</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Locators</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMaterializationTask_SourceLocators()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<SourceLocator> getSourceLocators();

  /**
   * Returns the value of the '<em><b>Root Components</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.Component}.
   * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Root Components</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
   * @return the value of the '<em>Root Components</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMaterializationTask_RootComponents()
   * @model containment="true" resolveProxies="true" required="true"
   * @generated
   */
  EList<Component> getRootComponents();

  /**
   * Returns the value of the '<em><b>P2 Repositories</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.P2Repository}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>P2 Repositories</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>P2 Repositories</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMaterializationTask_P2Repositories()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<P2Repository> getP2Repositories();

} // MaterializationTask
