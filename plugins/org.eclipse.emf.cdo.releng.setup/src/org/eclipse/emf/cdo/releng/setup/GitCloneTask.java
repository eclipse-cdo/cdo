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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Git Clone Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteName <em>Remote Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteURI <em>Remote URI</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getCheckoutBranch <em>Checkout Branch</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getGitCloneTask()
 * @model
 * @generated
 */
public interface GitCloneTask extends SetupTask
{
  public static final String ANONYMOUS = "anonymous";

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
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getGitCloneTask_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Remote Name</b></em>' attribute.
   * The default value is <code>"origin"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Remote Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Remote Name</em>' attribute.
   * @see #setRemoteName(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getGitCloneTask_RemoteName()
   * @model default="origin"
   * @generated
   */
  String getRemoteName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteName <em>Remote Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Remote Name</em>' attribute.
   * @see #getRemoteName()
   * @generated
   */
  void setRemoteName(String value);

  /**
   * Returns the value of the '<em><b>Remote URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Remote URI</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Remote URI</em>' attribute.
   * @see #setRemoteURI(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getGitCloneTask_RemoteURI()
   * @model
   * @generated
   */
  String getRemoteURI();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteURI <em>Remote URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Remote URI</em>' attribute.
   * @see #getRemoteURI()
   * @generated
   */
  void setRemoteURI(String value);

  /**
   * Returns the value of the '<em><b>Checkout Branch</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Checkout Branch</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Checkout Branch</em>' attribute.
   * @see #setCheckoutBranch(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getGitCloneTask_CheckoutBranch()
   * @model
   * @generated
   */
  String getCheckoutBranch();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getCheckoutBranch <em>Checkout Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Checkout Branch</em>' attribute.
   * @see #getCheckoutBranch()
   * @generated
   */
  void setCheckoutBranch(String value);

} // GitCloneTask
