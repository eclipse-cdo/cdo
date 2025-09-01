/*
 * Copyright (c) 2022, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.assembly;

import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.lm.assembly.impl.AssemblyImpl;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

import java.util.function.Consumer;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Assembly</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.Assembly#getSystemName <em>System Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.Assembly#getModules <em>Modules</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#getAssembly()
 * @model
 * @generated
 */
public interface Assembly extends ModelElement
{
  /**
   * Returns the value of the '<em><b>System Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>System Name</em>' attribute.
   * @see #setSystemName(String)
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#getAssembly_SystemName()
   * @model required="true"
   * @generated
   */
  String getSystemName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.assembly.Assembly#getSystemName <em>System Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>System Name</em>' attribute.
   * @see #getSystemName()
   * @generated
   */
  void setSystemName(String value);

  /**
   * Returns the value of the '<em><b>Modules</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getAssembly <em>Assembly</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Modules</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#getAssembly_Modules()
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getAssembly
   * @model opposite="assembly" containment="true"
   *        extendedMetaData="name='module' kind='element'"
   * @generated
   */
  EList<AssemblyModule> getModules();

  public AssemblyModule getRootModule();

  public AssemblyModule getModule(String moduleName);

  public void forEachDependency(Consumer<AssemblyModule> consumer);

  public boolean compareTo(Assembly newAssembly, DeltaHandler handler);

  /**
   * @since 1.1
   */
  public ModuleDefinition toModuleDefinition();

  default void sortModules()
  {
    ECollections.sort(getModules(), AssemblyModule.COMPARATOR);
  }

  /**
   * @since 1.1
   */
  public static Assembly of(CDOView view)
  {
    return view == null ? null : (Assembly)view.properties().get(AssemblyImpl.PROP_ASSEMBLY);
  }

  /**
   * A callback interface to be used with {@link Assembly#compareTo(Assembly, DeltaHandler) Assembly.compareTo()}.
   *
   * @author Eike Stepper
   */
  public interface DeltaHandler
  {
    public void handleAddition(AssemblyModule newModule);

    public void handleRemoval(AssemblyModule oldModule);

    public void handleModification(AssemblyModule oldModule, AssemblyModule newModule);
  }

} // Assembly
