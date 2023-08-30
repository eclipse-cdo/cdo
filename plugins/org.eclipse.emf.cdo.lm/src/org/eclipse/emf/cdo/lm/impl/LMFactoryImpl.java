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
package org.eclipse.emf.cdo.lm.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.Impact;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamMode;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * @generated
 */
public class LMFactoryImpl extends EFactoryImpl implements LMFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public static LMFactory init()
  {
    try
    {
      LMFactory theLMFactory = (LMFactory)EPackage.Registry.INSTANCE.getEFactory(LMPackage.eNS_URI);
      if (theLMFactory != null)
      {
        return theLMFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new LMFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   */
  public LMFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case LMPackage.SYSTEM:
      return createSystem();
    case LMPackage.PROCESS:
      return createProcess();
    case LMPackage.MODULE_TYPE:
      return createModuleType();
    case LMPackage.DROP_TYPE:
      return createDropType();
    case LMPackage.MODULE:
      return createModule();
    case LMPackage.STREAM:
      return createStream();
    case LMPackage.CHANGE:
      return createChange();
    case LMPackage.DELIVERY:
      return createDelivery();
    case LMPackage.DROP:
      return createDrop();
    case LMPackage.DEPENDENCY:
      return createDependency();
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
    case LMPackage.IMPACT:
      return createImpactFromString(eDataType, initialValue);
    case LMPackage.STREAM_MODE:
      return createStreamModeFromString(eDataType, initialValue);
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
    case LMPackage.IMPACT:
      return convertImpactToString(eDataType, instanceValue);
    case LMPackage.STREAM_MODE:
      return convertStreamModeToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.System createSystem()
  {
    SystemImpl system = new SystemImpl();
    return system;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.Process createProcess()
  {
    ProcessImpl process = new ProcessImpl();
    return process;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DropType createDropType()
  {
    DropTypeImpl dropType = new DropTypeImpl();
    return dropType;
  }

  @Override
  public DropType createDropType(String name, boolean release)
  {

    DropType dropType = createDropType();
    dropType.setName(name);
    dropType.setRelease(release);

    return dropType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.Module createModule()
  {
    ModuleImpl module = new ModuleImpl();
    return module;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Dependency createDependency()
  {
    DependencyImpl dependency = new DependencyImpl();
    return dependency;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModuleType createModuleType()
  {
    ModuleTypeImpl moduleType = new ModuleTypeImpl();
    return moduleType;
  }

  @Override
  public ModuleType createModuleType(String name)
  {
    ModuleType moduleType = createModuleType();
    moduleType.setName(name);
    return moduleType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Stream createStream()
  {
    StreamImpl stream = new StreamImpl();
    return stream;
  }

  @Override
  public Stream createStream(int majorVersion, int minorVersion, String codeName)
  {
    Stream stream = createStream();
    stream.setMajorVersion(majorVersion);
    stream.setMinorVersion(minorVersion);
    stream.setDevelopmentBranch(CDOBranchRef.MAIN);

    if (!StringUtil.isEmpty(codeName))
    {
      stream.setCodeName(codeName);
    }

    return stream;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Change createChange()
  {
    ChangeImpl change = new ChangeImpl();
    return change;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Delivery createDelivery()
  {
    DeliveryImpl delivery = new DeliveryImpl();
    return delivery;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Drop createDrop()
  {
    DropImpl drop = new DropImpl();
    return drop;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public Impact createImpactFromString(EDataType eDataType, String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }
    Impact result = Impact.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertImpactToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public StreamMode createStreamModeFromString(EDataType eDataType, String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }
    StreamMode result = StreamMode.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertStreamModeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public LMPackage getLMPackage()
  {
    return (LMPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static LMPackage getPackage()
  {
    return LMPackage.eINSTANCE;
  }

} // LMFactoryImpl
