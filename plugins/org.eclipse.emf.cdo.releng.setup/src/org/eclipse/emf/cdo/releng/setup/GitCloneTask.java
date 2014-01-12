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
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteName <em>Remote Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteURI <em>Remote URI</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getUserID <em>User ID</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getCheckoutBranch <em>Checkout Branch</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getGitCloneTask()
 * @model annotation="http://www.eclipse.org/CDO/releng/setup/enablement variableName='setup.egit.p2' repository='http://download.eclipse.org/egit/updates' installableUnits='org.eclipse.egit.feature.group'"
 * @generated
 */
public interface GitCloneTask extends SetupTask
{
  /**
   * Returns the value of the '<em><b>Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Location</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Location</em>' attribute.
   * @see #setLocation(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getGitCloneTask_Location()
   * @model required="true"
   * @generated
   */
  String getLocation();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getLocation <em>Location</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Location</em>' attribute.
   * @see #getLocation()
   * @generated
   */
  void setLocation(String value);

  public static final String ANONYMOUS = "anonymous";

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
   * @model default="origin" required="true"
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
   * @model required="true"
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
   * @model required="true"
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

  /**
   * Returns the value of the '<em><b>User ID</b></em>' attribute.
   * The default value is <code>"${git.user.id}"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User ID</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>User ID</em>' attribute.
   * @see #setUserID(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getGitCloneTask_UserID()
   * @model default="${git.user.id}"
   * @generated
   */
  String getUserID();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getUserID <em>User ID</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>User ID</em>' attribute.
   * @see #getUserID()
   * @generated
   */
  void setUserID(String value);

} // GitCloneTask
