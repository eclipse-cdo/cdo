/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
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
 * A representation of the model object '<em><b>Mylyn Query Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getConnectorKind <em>Connector Kind</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getRepositoryURL <em>Repository URL</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getRelativeURL <em>Relative URL</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMylynQueryTask()
 * @model annotation="http://www.eclipse.org/CDO/releng/setup/enablement variableName='setup.mylyn.p2' repository='http://download.eclipse.org/mylyn/releases/latest' installableUnits='org.eclipse.mylyn.tasks.core org.eclipse.mylyn.tasks.ui'"
 * @generated
 */
@Deprecated
public interface MylynQueryTask extends SetupTask
{
  /**
   * Returns the value of the '<em><b>Connector Kind</b></em>' attribute.
   * The default value is <code>"bugzilla"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Connector Kind</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Connector Kind</em>' attribute.
   * @see #setConnectorKind(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMylynQueryTask_ConnectorKind()
   * @model default="bugzilla" required="true"
   * @generated
   */
  String getConnectorKind();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getConnectorKind <em>Connector Kind</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Connector Kind</em>' attribute.
   * @see #getConnectorKind()
   * @generated
   */
  void setConnectorKind(String value);

  /**
   * Returns the value of the '<em><b>Summary</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Summary</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Summary</em>' attribute.
   * @see #setSummary(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMylynQueryTask_Summary()
   * @model required="true"
   * @generated
   */
  String getSummary();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getSummary <em>Summary</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Summary</em>' attribute.
   * @see #getSummary()
   * @generated
   */
  void setSummary(String value);

  /**
   * Returns the value of the '<em><b>Repository URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Repository URL</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Repository URL</em>' attribute.
   * @see #setRepositoryURL(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMylynQueryTask_RepositoryURL()
   * @model required="true"
   * @generated
   */
  String getRepositoryURL();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getRepositoryURL <em>Repository URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Repository URL</em>' attribute.
   * @see #getRepositoryURL()
   * @generated
   */
  void setRepositoryURL(String value);

  /**
   * Returns the value of the '<em><b>Relative URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Relative URL</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Relative URL</em>' attribute.
   * @see #setRelativeURL(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMylynQueryTask_RelativeURL()
   * @model required="true"
   * @generated
   */
  String getRelativeURL();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getRelativeURL <em>Relative URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Relative URL</em>' attribute.
   * @see #getRelativeURL()
   * @generated
   */
  void setRelativeURL(String value);

} // MylynQueryTask
