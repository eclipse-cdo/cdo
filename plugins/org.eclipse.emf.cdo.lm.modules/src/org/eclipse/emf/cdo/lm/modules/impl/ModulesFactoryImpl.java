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
package org.eclipse.emf.cdo.lm.modules.impl;

import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModulesFactoryImpl extends EFactoryImpl implements ModulesFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public static ModulesFactory init()
  {
    try
    {
      ModulesFactory theModulesFactory = (ModulesFactory)EPackage.Registry.INSTANCE.getEFactory(ModulesPackage.eNS_URI);
      if (theModulesFactory != null)
      {
        return theModulesFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new ModulesFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   */
  public ModulesFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case ModulesPackage.MODULE_DEFINITION:
      return createModuleDefinition();
    case ModulesPackage.DEPENDENCY_DEFINITION:
      return createDependencyDefinition();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case ModulesPackage.VERSION:
      return createVersionFromString(eDataType, initialValue);
    case ModulesPackage.VERSION_RANGE:
      return createVersionRangeFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case ModulesPackage.VERSION:
      return convertVersionToString(eDataType, instanceValue);
    case ModulesPackage.VERSION_RANGE:
      return convertVersionRangeToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModuleDefinition createModuleDefinition()
  {
    ModuleDefinitionImpl moduleDefinition = new ModuleDefinitionImpl();
    return moduleDefinition;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DependencyDefinition createDependencyDefinition()
  {
    DependencyDefinitionImpl dependencyDefinition = new DependencyDefinitionImpl();
    return dependencyDefinition;
  }

  @Override
  public DependencyDefinition createDependencyDefinition(String targetName)
  {
    return createDependencyDefinition(targetName, null);
  }

  @Override
  public DependencyDefinition createDependencyDefinition(String targetName, VersionRange versionRange)
  {
    DependencyDefinition dependencyDefinition = createDependencyDefinition();
    dependencyDefinition.setTargetName(targetName);
    dependencyDefinition.setVersionRange(versionRange);
    return dependencyDefinition;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public Version createVersionFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : Version.create(initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public String convertVersionToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : ((Version)instanceValue).toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public VersionRange createVersionRangeFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : new VersionRange(initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public String convertVersionRangeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : ((VersionRange)instanceValue).toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModulesPackage getModulesPackage()
  {
    return (ModulesPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static ModulesPackage getPackage()
  {
    return ModulesPackage.eINSTANCE;
  }

} // ModulesFactoryImpl
