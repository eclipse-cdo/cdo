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
 * A representation of the model object '<em><b>Basic Materialization Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask#getTargetPlatform <em>Target Platform</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask#getBundlePool <em>Bundle Pool</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBasicMaterializationTask()
 * @model abstract="true"
 *        annotation="http://www.eclipse.org/CDO/releng/setup/enablement variableName='setup.buckminster.p2' repository='http://download.eclipse.org/tools/buckminster/updates-4.3' installableUnits='org.eclipse.buckminster.ant org.eclipse.buckminster.core org.eclipse.buckminster.sax org.eclipse.buckminster.download org.eclipse.buckminster.generic org.eclipse.buckminster.executor org.eclipse.buckminster.fetcher org.eclipse.buckminster.osgi.filter org.eclipse.buckminster.jarprocessor org.eclipse.buckminster.jdt org.eclipse.buckminster.junit org.eclipse.buckminster.pde org.eclipse.buckminster.git org.eclipse.buckminster.mspec org.eclipse.buckminster.rmap'"
 * @generated
 */
public interface BasicMaterializationTask extends SetupTask
{
  /**
   * Returns the value of the '<em><b>Target Platform</b></em>' attribute.
   * The default value is <code>"${setup.branch.dir/tp}"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target Platform</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Platform</em>' attribute.
   * @see #setTargetPlatform(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBasicMaterializationTask_TargetPlatform()
   * @model default="${setup.branch.dir/tp}" required="true"
   * @generated
   */
  String getTargetPlatform();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask#getTargetPlatform <em>Target Platform</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target Platform</em>' attribute.
   * @see #getTargetPlatform()
   * @generated
   */
  void setTargetPlatform(String value);

  /**
   * Returns the value of the '<em><b>Bundle Pool</b></em>' attribute.
   * The default value is <code>"${setup.state.dir/buckminster/pool}"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Bundle Pool</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Bundle Pool</em>' attribute.
   * @see #setBundlePool(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBasicMaterializationTask_BundlePool()
   * @model default="${setup.state.dir/buckminster/pool}"
   * @generated
   */
  String getBundlePool();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask#getBundlePool <em>Bundle Pool</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Bundle Pool</em>' attribute.
   * @see #getBundlePool()
   * @generated
   */
  void setBundlePool(String value);

} // BasicMaterializationTask
