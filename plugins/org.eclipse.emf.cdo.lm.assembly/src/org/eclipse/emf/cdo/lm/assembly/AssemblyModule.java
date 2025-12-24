/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.assembly;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl;

import org.eclipse.equinox.p2.metadata.Version;

import java.util.Comparator;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Module</b></em>'.
 *
 * @extends Comparable<AssemblyModule>
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getAssembly <em>Assembly</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getBranchPoint <em>Branch Point</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#isRoot <em>Root</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#getAssemblyModule()
 * @model
 * @generated
 */
public interface AssemblyModule extends ModelElement, Comparable<AssemblyModule>
{
  public static final Comparator<AssemblyModule> COMPARATOR = //
      Comparator.comparingInt(AssemblyModuleImpl::root)//
          .reversed() //
          .thenComparing(AssemblyModule::getName);

  /**
   * Returns the value of the '<em><b>Assembly</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.assembly.Assembly#getModules <em>Modules</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Assembly</em>' container reference.
   * @see #setAssembly(Assembly)
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#getAssemblyModule_Assembly()
   * @see org.eclipse.emf.cdo.lm.assembly.Assembly#getModules
   * @model opposite="modules" required="true" transient="false"
   * @generated
   */
  Assembly getAssembly();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getAssembly <em>Assembly</em>}' container reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Assembly</em>' container reference.
   * @see #getAssembly()
   * @generated
   */
  void setAssembly(Assembly value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#getAssemblyModule_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Version</em>' attribute.
   * @see #setVersion(Version)
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#getAssemblyModule_Version()
   * @model dataType="org.eclipse.emf.cdo.lm.modules.Version" required="true"
   * @generated
   */
  Version getVersion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getVersion <em>Version</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Version</em>' attribute.
   * @see #getVersion()
   * @generated
   */
  void setVersion(Version value);

  /**
   * Returns the value of the '<em><b>Branch Point</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Branch Point</em>' attribute.
   * @see #setBranchPoint(CDOBranchPointRef)
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#getAssemblyModule_BranchPoint()
   * @model dataType="org.eclipse.emf.cdo.etypes.BranchPointRef" required="true"
   * @generated
   */
  CDOBranchPointRef getBranchPoint();

  /**
   * Sets the value of the
   * '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getBranchPoint
   * <em>Branch Point</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value the new value of the '<em>Branch Point</em>' attribute.
   * @see #getBranchPoint()
   * @generated
   */
  void setBranchPoint(CDOBranchPointRef value);

  /**
   * Returns the value of the '<em><b>Root</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Root</em>' attribute.
   * @see #setRoot(boolean)
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#getAssemblyModule_Root()
   * @model
   * @generated
   */
  boolean isRoot();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#isRoot <em>Root</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Root</em>' attribute.
   * @see #isRoot()
   * @generated
   */
  void setRoot(boolean value);

} // AssemblyModule
