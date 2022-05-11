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

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.lm.BasePoint;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.Impact;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.ModuleElement;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.ProcessElement;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamElement;
import org.eclipse.emf.cdo.lm.StreamMode;
import org.eclipse.emf.cdo.lm.SystemElement;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * @generated
 */
public class LMPackageImpl extends EPackageImpl implements LMPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass systemElementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass processElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass streamElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass systemEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass processEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass dropTypeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass dependencyEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleTypeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass baselineEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass floatingBaselineEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass fixedBaselineEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass streamEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass changeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass deliveryEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass dropEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EEnum impactEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EEnum streamModeEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EDataType basePointEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.lm.LMPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private LMPackageImpl()
  {
    super(eNS_URI, LMFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link LMPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static LMPackage init()
  {
    if (isInited)
    {
      return (LMPackage)EPackage.Registry.INSTANCE.getEPackage(LMPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredLMPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    LMPackageImpl theLMPackage = registeredLMPackage instanceof LMPackageImpl ? (LMPackageImpl)registeredLMPackage : new LMPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EcorePackage.eINSTANCE.eClass();
    EtypesPackage.eINSTANCE.eClass();
    ModulesPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theLMPackage.createPackageContents();

    // Initialize created meta-data
    theLMPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theLMPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(LMPackage.eNS_URI, theLMPackage);
    return theLMPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSystemElement()
  {
    return systemElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getSystemElement__GetSystem()
  {
    return systemElementEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getProcessElement()
  {
    return processElementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getProcessElement__GetProcess()
  {
    return processElementEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getModuleElement()
  {
    return moduleElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getModuleElement__GetModule()
  {
    return moduleElementEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStreamElement()
  {
    return streamElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getStreamElement__GetStream()
  {
    return streamElementEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSystem()
  {
    return systemEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSystem_Name()
  {
    return (EAttribute)systemEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSystem_Process()
  {
    return (EReference)systemEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSystem_Modules()
  {
    return (EReference)systemEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getSystem__GetModule__String()
  {
    return systemEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getProcess()
  {
    return processEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getProcess_System()
  {
    return (EReference)processEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getProcess_DropTypes()
  {
    return (EReference)processEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getProcess_ModuleDefinitionPath()
  {
    return (EAttribute)processEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getProcess_InitialModuleVersion()
  {
    return (EAttribute)processEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getProcess_ModuleTypes()
  {
    return (EReference)processEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDropType()
  {
    return dropTypeEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDropType_Process()
  {
    return (EReference)dropTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDropType_Name()
  {
    return (EAttribute)dropTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDropType_Release()
  {
    return (EAttribute)dropTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getModule()
  {
    return moduleEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getModule_System()
  {
    return (EReference)moduleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getModule_Name()
  {
    return (EAttribute)moduleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getModule_Type()
  {
    return (EReference)moduleEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getModule_Streams()
  {
    return (EReference)moduleEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDependency()
  {
    return dependencyEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDependency_Target()
  {
    return (EReference)dependencyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDependency_VersionRange()
  {
    return (EAttribute)dependencyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getModuleType()
  {
    return moduleTypeEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getModuleType_Process()
  {
    return (EReference)moduleTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getModuleType_Name()
  {
    return (EAttribute)moduleTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getBaseline()
  {
    return baselineEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getBaseline_Stream()
  {
    return (EReference)baselineEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getBaseline_Floating()
  {
    return (EAttribute)baselineEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getBaseline__GetName()
  {
    return baselineEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getBaseline__GetBranchPoint()
  {
    return baselineEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getBaseline__GetBaseTimeStamp()
  {
    return baselineEClass.getEOperations().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getFloatingBaseline()
  {
    return floatingBaselineEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getFloatingBaseline_Closed()
  {
    return (EAttribute)floatingBaselineEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getFloatingBaseline__GetBase()
  {
    return floatingBaselineEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getFloatingBaseline__GetDeliveries()
  {
    return floatingBaselineEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getFloatingBaseline__GetBranch()
  {
    return floatingBaselineEClass.getEOperations().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getFixedBaseline()
  {
    return fixedBaselineEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getFixedBaseline_Version()
  {
    return (EAttribute)fixedBaselineEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getFixedBaseline_Dependencies()
  {
    return (EReference)fixedBaselineEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getFixedBaseline__GetBasedChanges()
  {
    return fixedBaselineEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStream()
  {
    return streamEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getStream_Module()
  {
    return (EReference)streamEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getStream_Base()
  {
    return (EReference)streamEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStream_StartTimeStamp()
  {
    return (EAttribute)streamEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStream_MajorVersion()
  {
    return (EAttribute)streamEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStream_MinorVersion()
  {
    return (EAttribute)streamEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStream_CodeName()
  {
    return (EAttribute)streamEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStream_AllowedChanges()
  {
    return (EAttribute)streamEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getStream_Contents()
  {
    return (EReference)streamEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStream_MaintenanceTimeStamp()
  {
    return (EAttribute)streamEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getStream__InsertContent__Baseline()
  {
    return streamEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getStream__GetBranchPoint__long()
  {
    return streamEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStream_Mode()
  {
    return (EAttribute)streamEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStream_DevelopmentBranch()
  {
    return (EAttribute)streamEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStream_MaintenanceBranch()
  {
    return (EAttribute)streamEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getStream__GetFirstRelease()
  {
    return streamEClass.getEOperations().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getStream__GetLastRelease()
  {
    return streamEClass.getEOperations().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getStream__GetReleases()
  {
    return streamEClass.getEOperations().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getStream__GetBasedChanges()
  {
    return streamEClass.getEOperations().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getChange()
  {
    return changeEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getChange_Base()
  {
    return (EReference)changeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getChange_Label()
  {
    return (EAttribute)changeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getChange_Impact()
  {
    return (EAttribute)changeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getChange_Branch()
  {
    return (EAttribute)changeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getChange_Deliveries()
  {
    return (EReference)changeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDelivery()
  {
    return deliveryEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDelivery_Change()
  {
    return (EReference)deliveryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDelivery_MergeSource()
  {
    return (EAttribute)deliveryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDelivery_MergeTarget()
  {
    return (EAttribute)deliveryEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDrop()
  {
    return dropEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDrop_Type()
  {
    return (EReference)dropEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDrop_Label()
  {
    return (EAttribute)dropEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDrop_BranchPoint()
  {
    return (EAttribute)dropEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getDrop__IsRelease()
  {
    return dropEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getDrop__GetBasedStreams()
  {
    return dropEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getImpact()
  {
    return impactEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getStreamMode()
  {
    return streamModeEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getBasePoint()
  {
    return basePointEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public LMFactory getLMFactory()
  {
    return (LMFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    systemElementEClass = createEClass(SYSTEM_ELEMENT);
    createEOperation(systemElementEClass, SYSTEM_ELEMENT___GET_SYSTEM);

    processElementEClass = createEClass(PROCESS_ELEMENT);
    createEOperation(processElementEClass, PROCESS_ELEMENT___GET_PROCESS);

    moduleElementEClass = createEClass(MODULE_ELEMENT);
    createEOperation(moduleElementEClass, MODULE_ELEMENT___GET_MODULE);

    streamElementEClass = createEClass(STREAM_ELEMENT);
    createEOperation(streamElementEClass, STREAM_ELEMENT___GET_STREAM);

    systemEClass = createEClass(SYSTEM);
    createEAttribute(systemEClass, SYSTEM__NAME);
    createEReference(systemEClass, SYSTEM__PROCESS);
    createEReference(systemEClass, SYSTEM__MODULES);
    createEOperation(systemEClass, SYSTEM___GET_MODULE__STRING);

    processEClass = createEClass(PROCESS);
    createEReference(processEClass, PROCESS__SYSTEM);
    createEReference(processEClass, PROCESS__MODULE_TYPES);
    createEReference(processEClass, PROCESS__DROP_TYPES);
    createEAttribute(processEClass, PROCESS__MODULE_DEFINITION_PATH);
    createEAttribute(processEClass, PROCESS__INITIAL_MODULE_VERSION);

    moduleTypeEClass = createEClass(MODULE_TYPE);
    createEReference(moduleTypeEClass, MODULE_TYPE__PROCESS);
    createEAttribute(moduleTypeEClass, MODULE_TYPE__NAME);

    dropTypeEClass = createEClass(DROP_TYPE);
    createEReference(dropTypeEClass, DROP_TYPE__PROCESS);
    createEAttribute(dropTypeEClass, DROP_TYPE__NAME);
    createEAttribute(dropTypeEClass, DROP_TYPE__RELEASE);

    moduleEClass = createEClass(MODULE);
    createEReference(moduleEClass, MODULE__SYSTEM);
    createEAttribute(moduleEClass, MODULE__NAME);
    createEReference(moduleEClass, MODULE__TYPE);
    createEReference(moduleEClass, MODULE__STREAMS);

    baselineEClass = createEClass(BASELINE);
    createEReference(baselineEClass, BASELINE__STREAM);
    createEAttribute(baselineEClass, BASELINE__FLOATING);
    createEOperation(baselineEClass, BASELINE___GET_NAME);
    createEOperation(baselineEClass, BASELINE___GET_BRANCH_POINT);
    createEOperation(baselineEClass, BASELINE___GET_BASE_TIME_STAMP);

    floatingBaselineEClass = createEClass(FLOATING_BASELINE);
    createEAttribute(floatingBaselineEClass, FLOATING_BASELINE__CLOSED);
    createEOperation(floatingBaselineEClass, FLOATING_BASELINE___GET_BASE);
    createEOperation(floatingBaselineEClass, FLOATING_BASELINE___GET_DELIVERIES);
    createEOperation(floatingBaselineEClass, FLOATING_BASELINE___GET_BRANCH);

    fixedBaselineEClass = createEClass(FIXED_BASELINE);
    createEAttribute(fixedBaselineEClass, FIXED_BASELINE__VERSION);
    createEReference(fixedBaselineEClass, FIXED_BASELINE__DEPENDENCIES);
    createEOperation(fixedBaselineEClass, FIXED_BASELINE___GET_BASED_CHANGES);

    streamEClass = createEClass(STREAM);
    createEReference(streamEClass, STREAM__MODULE);
    createEReference(streamEClass, STREAM__BASE);
    createEAttribute(streamEClass, STREAM__START_TIME_STAMP);
    createEAttribute(streamEClass, STREAM__MAJOR_VERSION);
    createEAttribute(streamEClass, STREAM__MINOR_VERSION);
    createEAttribute(streamEClass, STREAM__CODE_NAME);
    createEAttribute(streamEClass, STREAM__ALLOWED_CHANGES);
    createEAttribute(streamEClass, STREAM__MODE);
    createEAttribute(streamEClass, STREAM__DEVELOPMENT_BRANCH);
    createEAttribute(streamEClass, STREAM__MAINTENANCE_BRANCH);
    createEReference(streamEClass, STREAM__CONTENTS);
    createEAttribute(streamEClass, STREAM__MAINTENANCE_TIME_STAMP);
    createEOperation(streamEClass, STREAM___INSERT_CONTENT__BASELINE);
    createEOperation(streamEClass, STREAM___GET_BRANCH_POINT__LONG);
    createEOperation(streamEClass, STREAM___GET_FIRST_RELEASE);
    createEOperation(streamEClass, STREAM___GET_LAST_RELEASE);
    createEOperation(streamEClass, STREAM___GET_RELEASES);
    createEOperation(streamEClass, STREAM___GET_BASED_CHANGES);

    changeEClass = createEClass(CHANGE);
    createEReference(changeEClass, CHANGE__BASE);
    createEAttribute(changeEClass, CHANGE__LABEL);
    createEAttribute(changeEClass, CHANGE__IMPACT);
    createEAttribute(changeEClass, CHANGE__BRANCH);
    createEReference(changeEClass, CHANGE__DELIVERIES);

    deliveryEClass = createEClass(DELIVERY);
    createEReference(deliveryEClass, DELIVERY__CHANGE);
    createEAttribute(deliveryEClass, DELIVERY__MERGE_SOURCE);
    createEAttribute(deliveryEClass, DELIVERY__MERGE_TARGET);

    dropEClass = createEClass(DROP);
    createEReference(dropEClass, DROP__TYPE);
    createEAttribute(dropEClass, DROP__LABEL);
    createEAttribute(dropEClass, DROP__BRANCH_POINT);
    createEOperation(dropEClass, DROP___IS_RELEASE);
    createEOperation(dropEClass, DROP___GET_BASED_STREAMS);

    dependencyEClass = createEClass(DEPENDENCY);
    createEReference(dependencyEClass, DEPENDENCY__TARGET);
    createEAttribute(dependencyEClass, DEPENDENCY__VERSION_RANGE);

    // Create enums
    impactEEnum = createEEnum(IMPACT);
    streamModeEEnum = createEEnum(STREAM_MODE);

    // Create data types
    basePointEDataType = createEDataType(BASE_POINT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is
   * guarded to have no affect on any invocation but its first. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    EtypesPackage theEtypesPackage = (EtypesPackage)EPackage.Registry.INSTANCE.getEPackage(EtypesPackage.eNS_URI);
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
    ModulesPackage theModulesPackage = (ModulesPackage)EPackage.Registry.INSTANCE.getEPackage(ModulesPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    systemElementEClass.getESuperTypes().add(theEtypesPackage.getModelElement());
    processElementEClass.getESuperTypes().add(getSystemElement());
    moduleElementEClass.getESuperTypes().add(getSystemElement());
    streamElementEClass.getESuperTypes().add(getModuleElement());
    systemEClass.getESuperTypes().add(theEtypesPackage.getModelElement());
    processEClass.getESuperTypes().add(getSystemElement());
    moduleTypeEClass.getESuperTypes().add(getProcessElement());
    dropTypeEClass.getESuperTypes().add(getProcessElement());
    moduleEClass.getESuperTypes().add(getSystemElement());
    baselineEClass.getESuperTypes().add(getStreamElement());
    floatingBaselineEClass.getESuperTypes().add(getBaseline());
    fixedBaselineEClass.getESuperTypes().add(getBaseline());
    streamEClass.getESuperTypes().add(getFloatingBaseline());
    changeEClass.getESuperTypes().add(getFloatingBaseline());
    deliveryEClass.getESuperTypes().add(getFixedBaseline());
    dropEClass.getESuperTypes().add(getFixedBaseline());
    dependencyEClass.getESuperTypes().add(getStreamElement());

    // Initialize classes, features, and operations; add parameters
    initEClass(systemElementEClass, SystemElement.class, "SystemElement", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEOperation(getSystemElement__GetSystem(), getSystem(), "getSystem", 1, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(processElementEClass, ProcessElement.class, "ProcessElement", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEOperation(getProcessElement__GetProcess(), getProcess(), "getProcess", 1, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(moduleElementEClass, ModuleElement.class, "ModuleElement", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEOperation(getModuleElement__GetModule(), getModule(), "getModule", 1, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(streamElementEClass, StreamElement.class, "StreamElement", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEOperation(getStreamElement__GetStream(), getStream(), "getStream", 1, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(systemEClass, org.eclipse.emf.cdo.lm.System.class, "System", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSystem_Name(), ecorePackage.getEString(), "name", null, 1, 1, org.eclipse.emf.cdo.lm.System.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSystem_Process(), getProcess(), getProcess_System(), "process", null, 1, 1, org.eclipse.emf.cdo.lm.System.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSystem_Modules(), getModule(), getModule_System(), "modules", null, 0, -1, org.eclipse.emf.cdo.lm.System.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    EOperation op = initEOperation(getSystem__GetModule__String(), getModule(), "getModule", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, theEcorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(processEClass, org.eclipse.emf.cdo.lm.Process.class, "Process", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getProcess_System(), getSystem(), getSystem_Process(), "system", null, 1, 1, org.eclipse.emf.cdo.lm.Process.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getProcess_ModuleTypes(), getModuleType(), getModuleType_Process(), "moduleTypes", null, 0, -1, org.eclipse.emf.cdo.lm.Process.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getProcess_DropTypes(), getDropType(), getDropType_Process(), "dropTypes", null, 0, -1, org.eclipse.emf.cdo.lm.Process.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProcess_ModuleDefinitionPath(), theEcorePackage.getEString(), "moduleDefinitionPath", null, 1, 1, org.eclipse.emf.cdo.lm.Process.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProcess_InitialModuleVersion(), theModulesPackage.getVersion(), "initialModuleVersion", "0.1.0", 1, 1,
        org.eclipse.emf.cdo.lm.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(moduleTypeEClass, ModuleType.class, "ModuleType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getModuleType_Process(), getProcess(), getProcess_ModuleTypes(), "process", null, 1, 1, ModuleType.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleType_Name(), ecorePackage.getEString(), "name", null, 1, 1, ModuleType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dropTypeEClass, DropType.class, "DropType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDropType_Process(), getProcess(), getProcess_DropTypes(), "process", null, 1, 1, DropType.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDropType_Name(), ecorePackage.getEString(), "name", null, 1, 1, DropType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDropType_Release(), ecorePackage.getEBoolean(), "release", null, 1, 1, DropType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(moduleEClass, org.eclipse.emf.cdo.lm.Module.class, "Module", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getModule_System(), getSystem(), getSystem_Modules(), "system", null, 1, 1, org.eclipse.emf.cdo.lm.Module.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModule_Name(), ecorePackage.getEString(), "name", null, 1, 1, org.eclipse.emf.cdo.lm.Module.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModule_Type(), getModuleType(), null, "type", null, 0, 1, org.eclipse.emf.cdo.lm.Module.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModule_Streams(), getStream(), getStream_Module(), "streams", null, 0, -1, org.eclipse.emf.cdo.lm.Module.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(baselineEClass, Baseline.class, "Baseline", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBaseline_Stream(), getStream(), getStream_Contents(), "stream", null, 0, 1, Baseline.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBaseline_Floating(), ecorePackage.getEBoolean(), "floating", null, 0, 1, Baseline.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEOperation(getBaseline__GetName(), theEcorePackage.getEString(), "getName", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getBaseline__GetBranchPoint(), theEtypesPackage.getBranchPointRef(), "getBranchPoint", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getBaseline__GetBaseTimeStamp(), theEcorePackage.getELong(), "getBaseTimeStamp", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(floatingBaselineEClass, FloatingBaseline.class, "FloatingBaseline", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFloatingBaseline_Closed(), theEcorePackage.getEBoolean(), "closed", null, 0, 1, FloatingBaseline.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEOperation(getFloatingBaseline__GetBase(), getFixedBaseline(), "getBase", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getFloatingBaseline__GetDeliveries(), getDelivery(), "getDeliveries", 0, -1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getFloatingBaseline__GetBranch(), theEtypesPackage.getBranchRef(), "getBranch", 1, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(fixedBaselineEClass, FixedBaseline.class, "FixedBaseline", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFixedBaseline_Version(), theModulesPackage.getVersion(), "version", null, 1, 1, FixedBaseline.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getFixedBaseline_Dependencies(), getDependency(), null, "dependencies", null, 0, -1, FixedBaseline.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEOperation(getFixedBaseline__GetBasedChanges(), getChange(), "getBasedChanges", 0, -1, IS_UNIQUE, IS_ORDERED);

    initEClass(streamEClass, Stream.class, "Stream", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getStream_Module(), getModule(), getModule_Streams(), "module", null, 1, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStream_Base(), getDrop(), null, "base", null, 0, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStream_StartTimeStamp(), theEcorePackage.getELong(), "startTimeStamp", null, 1, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStream_MajorVersion(), ecorePackage.getEInt(), "majorVersion", null, 1, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStream_MinorVersion(), ecorePackage.getEInt(), "minorVersion", null, 1, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStream_CodeName(), ecorePackage.getEString(), "codeName", null, 0, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStream_AllowedChanges(), getImpact(), "allowedChanges", "Minor", 0, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStream_Mode(), getStreamMode(), "mode", null, 1, 1, Stream.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getStream_DevelopmentBranch(), theEtypesPackage.getBranchRef(), "developmentBranch", null, 1, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStream_MaintenanceBranch(), theEtypesPackage.getBranchRef(), "maintenanceBranch", null, 0, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStream_Contents(), getBaseline(), getBaseline_Stream(), "contents", null, 0, -1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStream_MaintenanceTimeStamp(), theEcorePackage.getELong(), "maintenanceTimeStamp", null, 0, 1, Stream.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    op = initEOperation(getStream__InsertContent__Baseline(), theEcorePackage.getEInt(), "insertContent", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, getBaseline(), "baseline", 1, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getStream__GetBranchPoint__long(), theEtypesPackage.getBranchPointRef(), "getBranchPoint", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, theEcorePackage.getELong(), "timeStamp", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getStream__GetFirstRelease(), getDrop(), "getFirstRelease", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getStream__GetLastRelease(), getDrop(), "getLastRelease", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getStream__GetReleases(), getDrop(), "getReleases", 0, -1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getStream__GetBasedChanges(), getChange(), "getBasedChanges", 0, -1, IS_UNIQUE, IS_ORDERED);

    initEClass(changeEClass, Change.class, "Change", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getChange_Base(), getFixedBaseline(), null, "base", null, 0, 1, Change.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getChange_Label(), theEcorePackage.getEString(), "label", null, 0, 1, Change.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getChange_Impact(), getImpact(), "impact", null, 1, 1, Change.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getChange_Branch(), theEtypesPackage.getBranchRef(), "branch", "", 1, 1, Change.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getChange_Deliveries(), getDelivery(), getDelivery_Change(), "deliveries", null, 0, -1, Change.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(deliveryEClass, Delivery.class, "Delivery", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDelivery_Change(), getChange(), getChange_Deliveries(), "change", null, 1, 1, Delivery.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDelivery_MergeSource(), theEtypesPackage.getBranchPointRef(), "mergeSource", "", 1, 1, Delivery.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDelivery_MergeTarget(), theEtypesPackage.getBranchPointRef(), "mergeTarget", "", 1, 1, Delivery.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dropEClass, Drop.class, "Drop", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDrop_Type(), getDropType(), null, "type", null, 1, 1, Drop.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDrop_Label(), ecorePackage.getEString(), "label", null, 0, 1, Drop.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDrop_BranchPoint(), theEtypesPackage.getBranchPointRef(), "branchPoint", "", 1, 1, Drop.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEOperation(getDrop__IsRelease(), theEcorePackage.getEBoolean(), "isRelease", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getDrop__GetBasedStreams(), getStream(), "getBasedStreams", 0, -1, IS_UNIQUE, IS_ORDERED);

    initEClass(dependencyEClass, Dependency.class, "Dependency", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDependency_Target(), getModule(), null, "target", null, 1, 1, Dependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDependency_VersionRange(), theModulesPackage.getVersionRange(), "versionRange", null, 1, 1, Dependency.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(impactEEnum, Impact.class, "Impact");
    addEEnumLiteral(impactEEnum, Impact.MICRO);
    addEEnumLiteral(impactEEnum, Impact.MINOR);
    addEEnumLiteral(impactEEnum, Impact.MAJOR);

    initEEnum(streamModeEEnum, StreamMode.class, "StreamMode");
    addEEnumLiteral(streamModeEEnum, StreamMode.DEVELOPMENT);
    addEEnumLiteral(streamModeEEnum, StreamMode.MAINTENANCE);
    addEEnumLiteral(streamModeEEnum, StreamMode.CLOSED);

    // Initialize data types
    initEDataType(basePointEDataType, BasePoint.class, "BasePoint", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} // LMPackageImpl
