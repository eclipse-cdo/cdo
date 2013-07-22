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
import org.eclipse.emf.common.util.URI;

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
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Branch#getGitClones <em>Git Clones</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Branch#getApiBaseline <em>Api Baseline</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Branch#getMspecFilePath <em>Mspec File Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Branch#getCloneVariableName <em>Clone Variable Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBranch()
 * @model
 * @generated
 */
public interface Branch extends ToolInstallation
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
   * @model
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
   * Returns the value of the '<em><b>Git Clones</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.GitClone}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.GitClone#getBranch <em>Branch</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Git Clones</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Git Clones</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBranch_GitClones()
   * @see org.eclipse.emf.cdo.releng.setup.GitClone#getBranch
   * @model opposite="branch" containment="true" required="true"
   * @generated
   */
  EList<GitClone> getGitClones();

  /**
   * Returns the value of the '<em><b>Api Baseline</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Api Baseline</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Api Baseline</em>' reference.
   * @see #setApiBaseline(ApiBaseline)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBranch_ApiBaseline()
   * @model
   * @generated
   */
  ApiBaseline getApiBaseline();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Branch#getApiBaseline <em>Api Baseline</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Api Baseline</em>' reference.
   * @see #getApiBaseline()
   * @generated
   */
  void setApiBaseline(ApiBaseline value);

  /**
   * Returns the value of the '<em><b>Mspec File Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Mspec File Path</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Mspec File Path</em>' attribute.
   * @see #setMspecFilePath(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBranch_MspecFilePath()
   * @model
   * @generated
   */
  String getMspecFilePath();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Branch#getMspecFilePath <em>Mspec File Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Mspec File Path</em>' attribute.
   * @see #getMspecFilePath()
   * @generated
   */
  void setMspecFilePath(String value);

  /**
   * Returns the value of the '<em><b>Clone Variable Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Clone Variable Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Clone Variable Name</em>' attribute.
   * @see #setCloneVariableName(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBranch_CloneVariableName()
   * @model
   * @generated
   */
  String getCloneVariableName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Branch#getCloneVariableName <em>Clone Variable Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Clone Variable Name</em>' attribute.
   * @see #getCloneVariableName()
   * @generated
   */
  void setCloneVariableName(String value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean isInstalled(String installFolder);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model dataType="org.eclipse.emf.cdo.releng.setup.URI"
   * @generated
   */
  URI getURI(String installFolder);

} // Branch
