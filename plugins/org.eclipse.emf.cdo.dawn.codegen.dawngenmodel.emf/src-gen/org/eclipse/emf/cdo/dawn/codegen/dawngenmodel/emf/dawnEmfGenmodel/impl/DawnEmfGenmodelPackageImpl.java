/*
 * Copyright (c) 2011, 2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelFactory;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelPackage;

import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.impl.GenModelPackageImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 *
 * @generated
 * @author Martin Fluegge
 */
public class DawnEmfGenmodelPackageImpl extends EPackageImpl implements DawnEmfGenmodelPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass dawnEMFGeneratorEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private DawnEmfGenmodelPackageImpl()
  {
    super(eNS_URI, DawnEmfGenmodelFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * <p>
   * This method is used to initialize {@link DawnEmfGenmodelPackage#eINSTANCE} when that field is accessed. Clients
   * should not invoke it directly. Instead, they should simply access that field to obtain the package. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static DawnEmfGenmodelPackage init()
  {
    if (isInited)
    {
      return (DawnEmfGenmodelPackage)EPackage.Registry.INSTANCE.getEPackage(DawnEmfGenmodelPackage.eNS_URI);
    }

    // Obtain or create and register package
    DawnEmfGenmodelPackageImpl theDawnEmfGenmodelPackage = (DawnEmfGenmodelPackageImpl)(EPackage.Registry.INSTANCE
        .get(eNS_URI) instanceof DawnEmfGenmodelPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new DawnEmfGenmodelPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    DawngenmodelPackage.eINSTANCE.eClass();

    // Obtain or create and register interdependencies
    GenModelPackageImpl theGenmodelPackage = (GenModelPackageImpl)(EPackage.Registry.INSTANCE
        .getEPackage(GenModelPackage.eNS_URI) instanceof GenModelPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(GenModelPackage.eNS_URI)
            : GenModelPackage.eINSTANCE);

    // Create package meta-data objects
    theDawnEmfGenmodelPackage.createPackageContents();
    theGenmodelPackage.createPackageContents();

    // Initialize created meta-data
    theDawnEmfGenmodelPackage.initializePackageContents();
    theGenmodelPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theDawnEmfGenmodelPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(DawnEmfGenmodelPackage.eNS_URI, theDawnEmfGenmodelPackage);
    return theDawnEmfGenmodelPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EClass getDawnEMFGenerator()
  {
    return dawnEMFGeneratorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getDawnEMFGenerator_EmfGenModel()
  {
    return (EReference)dawnEMFGeneratorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DawnEmfGenmodelFactory getDawnEmfGenmodelFactory()
  {
    return (DawnEmfGenmodelFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
   * first. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
    dawnEMFGeneratorEClass = createEClass(DAWN_EMF_GENERATOR);
    createEReference(dawnEMFGeneratorEClass, DAWN_EMF_GENERATOR__EMF_GEN_MODEL);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
   * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
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
    DawngenmodelPackage theDawngenmodelPackage = (DawngenmodelPackage)EPackage.Registry.INSTANCE.getEPackage(DawngenmodelPackage.eNS_URI);
    GenModelPackage theGenmodelPackage = (GenModelPackage)EPackage.Registry.INSTANCE.getEPackage(GenModelPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    dawnEMFGeneratorEClass.getESuperTypes().add(theDawngenmodelPackage.getDawnFragmentGenerator());

    // Initialize classes and features; add operations and parameters
    initEClass(dawnEMFGeneratorEClass, DawnEMFGenerator.class, "DawnEMFGenerator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDawnEMFGenerator_EmfGenModel(), theGenmodelPackage.getGenModel(), null, "emfGenModel", null, 0, 1, DawnEMFGenerator.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // DawnEmfGenmodelPackageImpl
