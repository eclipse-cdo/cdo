/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4.legacy.impl;

import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNUNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.ImplContainedElementNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.MultiContainedElement;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable;
import org.eclipse.emf.cdo.tests.model4.RefSingleContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.SingleContainedElement;
import org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.legacy.model4Factory;
import org.eclipse.emf.cdo.tests.model4.legacy.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.legacy.model4interfacesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class model4PackageImpl extends EPackageImpl implements model4Package
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass refSingleContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass singleContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass refSingleNonContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass singleNonContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass refMultiContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass multiContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass refMultiNonContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass multiNonContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass refMultiNonContainedUnsettableEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass multiNonContainedUnsettableElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass refSingleContainedNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass refSingleNonContainedNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass refMultiContainedNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass refMultiNonContainedNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass containedElementNoOppositeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genRefSingleContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genRefSingleNonContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genRefMultiContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genRefMultiNonContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implSingleRefContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implSingleRefContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implSingleRefNonContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implSingleRefNonContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implMultiRefNonContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implMultiRefNonContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implMultiRefContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implMultiRefContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implSingleRefContainerNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implSingleRefNonContainerNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implMultiRefContainerNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implMultiRefNonContainerNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass implContainedElementNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genRefMultiNUNonContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genRefMapNonContainedEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass stringToEObjectEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.legacy.model4.model4Package#eNS_URI
   * @see #init()
   * @generated
   */
  private model4PackageImpl()
  {
    super(eNS_URI, model4Factory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link model4Package#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static model4Package init()
  {
    if (isInited)
    {
      return (model4Package)EPackage.Registry.INSTANCE.getEPackage(model4Package.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredmodel4Package = EPackage.Registry.INSTANCE.get(eNS_URI);
    model4PackageImpl themodel4Package = registeredmodel4Package instanceof model4PackageImpl ? (model4PackageImpl)registeredmodel4Package
        : new model4PackageImpl();

    isInited = true;

    // Initialize simple dependencies
    model4interfacesPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    themodel4Package.createPackageContents();

    // Initialize created meta-data
    themodel4Package.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    themodel4Package.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(model4Package.eNS_URI, themodel4Package);
    return themodel4Package;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefSingleContained()
  {
    return refSingleContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefSingleContained_Element()
  {
    return (EReference)refSingleContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSingleContainedElement()
  {
    return singleContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSingleContainedElement_Name()
  {
    return (EAttribute)singleContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSingleContainedElement_Parent()
  {
    return (EReference)singleContainedElementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefSingleNonContained()
  {
    return refSingleNonContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefSingleNonContained_Element()
  {
    return (EReference)refSingleNonContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSingleNonContainedElement()
  {
    return singleNonContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSingleNonContainedElement_Name()
  {
    return (EAttribute)singleNonContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSingleNonContainedElement_Parent()
  {
    return (EReference)singleNonContainedElementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefMultiContained()
  {
    return refMultiContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefMultiContained_Elements()
  {
    return (EReference)refMultiContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMultiContainedElement()
  {
    return multiContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getMultiContainedElement_Name()
  {
    return (EAttribute)multiContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMultiContainedElement_Parent()
  {
    return (EReference)multiContainedElementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefMultiNonContained()
  {
    return refMultiNonContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefMultiNonContained_Elements()
  {
    return (EReference)refMultiNonContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMultiNonContainedElement()
  {
    return multiNonContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getMultiNonContainedElement_Name()
  {
    return (EAttribute)multiNonContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMultiNonContainedElement_Parent()
  {
    return (EReference)multiNonContainedElementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefMultiNonContainedUnsettable()
  {
    return refMultiNonContainedUnsettableEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefMultiNonContainedUnsettable_Elements()
  {
    return (EReference)refMultiNonContainedUnsettableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMultiNonContainedUnsettableElement()
  {
    return multiNonContainedUnsettableElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getMultiNonContainedUnsettableElement_Name()
  {
    return (EAttribute)multiNonContainedUnsettableElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMultiNonContainedUnsettableElement_Parent()
  {
    return (EReference)multiNonContainedUnsettableElementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefSingleContainedNPL()
  {
    return refSingleContainedNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefSingleContainedNPL_Element()
  {
    return (EReference)refSingleContainedNPLEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefSingleNonContainedNPL()
  {
    return refSingleNonContainedNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefSingleNonContainedNPL_Element()
  {
    return (EReference)refSingleNonContainedNPLEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefMultiContainedNPL()
  {
    return refMultiContainedNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefMultiContainedNPL_Elements()
  {
    return (EReference)refMultiContainedNPLEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefMultiNonContainedNPL()
  {
    return refMultiNonContainedNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefMultiNonContainedNPL_Elements()
  {
    return (EReference)refMultiNonContainedNPLEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getContainedElementNoOpposite()
  {
    return containedElementNoOppositeEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getContainedElementNoOpposite_Name()
  {
    return (EAttribute)containedElementNoOppositeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getGenRefSingleContained()
  {
    return genRefSingleContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGenRefSingleContained_Element()
  {
    return (EReference)genRefSingleContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getGenRefSingleNonContained()
  {
    return genRefSingleNonContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGenRefSingleNonContained_Element()
  {
    return (EReference)genRefSingleNonContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getGenRefMultiContained()
  {
    return genRefMultiContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGenRefMultiContained_Elements()
  {
    return (EReference)genRefMultiContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getGenRefMultiNonContained()
  {
    return genRefMultiNonContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGenRefMultiNonContained_Elements()
  {
    return (EReference)genRefMultiNonContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplSingleRefContainer()
  {
    return implSingleRefContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplSingleRefContainedElement()
  {
    return implSingleRefContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getImplSingleRefContainedElement_Name()
  {
    return (EAttribute)implSingleRefContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplSingleRefNonContainer()
  {
    return implSingleRefNonContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplSingleRefNonContainedElement()
  {
    return implSingleRefNonContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getImplSingleRefNonContainedElement_Name()
  {
    return (EAttribute)implSingleRefNonContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplMultiRefNonContainer()
  {
    return implMultiRefNonContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplMultiRefNonContainedElement()
  {
    return implMultiRefNonContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getImplMultiRefNonContainedElement_Name()
  {
    return (EAttribute)implMultiRefNonContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplMultiRefContainer()
  {
    return implMultiRefContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplMultiRefContainedElement()
  {
    return implMultiRefContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getImplMultiRefContainedElement_Name()
  {
    return (EAttribute)implMultiRefContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplSingleRefContainerNPL()
  {
    return implSingleRefContainerNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplSingleRefNonContainerNPL()
  {
    return implSingleRefNonContainerNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplMultiRefContainerNPL()
  {
    return implMultiRefContainerNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplMultiRefNonContainerNPL()
  {
    return implMultiRefNonContainerNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getImplContainedElementNPL()
  {
    return implContainedElementNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getGenRefMultiNUNonContained()
  {
    return genRefMultiNUNonContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGenRefMultiNUNonContained_Elements()
  {
    return (EReference)genRefMultiNUNonContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getGenRefMapNonContained()
  {
    return genRefMapNonContainedEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGenRefMapNonContained_Elements()
  {
    return (EReference)genRefMapNonContainedEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStringToEObject()
  {
    return stringToEObjectEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStringToEObject_Key()
  {
    return (EAttribute)stringToEObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getStringToEObject_Value()
  {
    return (EReference)stringToEObjectEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public model4Factory getmodel4Factory()
  {
    return (model4Factory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    refSingleContainedEClass = createEClass(REF_SINGLE_CONTAINED);
    createEReference(refSingleContainedEClass, REF_SINGLE_CONTAINED__ELEMENT);

    singleContainedElementEClass = createEClass(SINGLE_CONTAINED_ELEMENT);
    createEAttribute(singleContainedElementEClass, SINGLE_CONTAINED_ELEMENT__NAME);
    createEReference(singleContainedElementEClass, SINGLE_CONTAINED_ELEMENT__PARENT);

    refSingleNonContainedEClass = createEClass(REF_SINGLE_NON_CONTAINED);
    createEReference(refSingleNonContainedEClass, REF_SINGLE_NON_CONTAINED__ELEMENT);

    singleNonContainedElementEClass = createEClass(SINGLE_NON_CONTAINED_ELEMENT);
    createEAttribute(singleNonContainedElementEClass, SINGLE_NON_CONTAINED_ELEMENT__NAME);
    createEReference(singleNonContainedElementEClass, SINGLE_NON_CONTAINED_ELEMENT__PARENT);

    refMultiContainedEClass = createEClass(REF_MULTI_CONTAINED);
    createEReference(refMultiContainedEClass, REF_MULTI_CONTAINED__ELEMENTS);

    multiContainedElementEClass = createEClass(MULTI_CONTAINED_ELEMENT);
    createEAttribute(multiContainedElementEClass, MULTI_CONTAINED_ELEMENT__NAME);
    createEReference(multiContainedElementEClass, MULTI_CONTAINED_ELEMENT__PARENT);

    refMultiNonContainedEClass = createEClass(REF_MULTI_NON_CONTAINED);
    createEReference(refMultiNonContainedEClass, REF_MULTI_NON_CONTAINED__ELEMENTS);

    multiNonContainedElementEClass = createEClass(MULTI_NON_CONTAINED_ELEMENT);
    createEAttribute(multiNonContainedElementEClass, MULTI_NON_CONTAINED_ELEMENT__NAME);
    createEReference(multiNonContainedElementEClass, MULTI_NON_CONTAINED_ELEMENT__PARENT);

    refMultiNonContainedUnsettableEClass = createEClass(REF_MULTI_NON_CONTAINED_UNSETTABLE);
    createEReference(refMultiNonContainedUnsettableEClass, REF_MULTI_NON_CONTAINED_UNSETTABLE__ELEMENTS);

    multiNonContainedUnsettableElementEClass = createEClass(MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT);
    createEAttribute(multiNonContainedUnsettableElementEClass, MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__NAME);
    createEReference(multiNonContainedUnsettableElementEClass, MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT);

    refSingleContainedNPLEClass = createEClass(REF_SINGLE_CONTAINED_NPL);
    createEReference(refSingleContainedNPLEClass, REF_SINGLE_CONTAINED_NPL__ELEMENT);

    refSingleNonContainedNPLEClass = createEClass(REF_SINGLE_NON_CONTAINED_NPL);
    createEReference(refSingleNonContainedNPLEClass, REF_SINGLE_NON_CONTAINED_NPL__ELEMENT);

    refMultiContainedNPLEClass = createEClass(REF_MULTI_CONTAINED_NPL);
    createEReference(refMultiContainedNPLEClass, REF_MULTI_CONTAINED_NPL__ELEMENTS);

    refMultiNonContainedNPLEClass = createEClass(REF_MULTI_NON_CONTAINED_NPL);
    createEReference(refMultiNonContainedNPLEClass, REF_MULTI_NON_CONTAINED_NPL__ELEMENTS);

    containedElementNoOppositeEClass = createEClass(CONTAINED_ELEMENT_NO_OPPOSITE);
    createEAttribute(containedElementNoOppositeEClass, CONTAINED_ELEMENT_NO_OPPOSITE__NAME);

    genRefSingleContainedEClass = createEClass(GEN_REF_SINGLE_CONTAINED);
    createEReference(genRefSingleContainedEClass, GEN_REF_SINGLE_CONTAINED__ELEMENT);

    genRefSingleNonContainedEClass = createEClass(GEN_REF_SINGLE_NON_CONTAINED);
    createEReference(genRefSingleNonContainedEClass, GEN_REF_SINGLE_NON_CONTAINED__ELEMENT);

    genRefMultiContainedEClass = createEClass(GEN_REF_MULTI_CONTAINED);
    createEReference(genRefMultiContainedEClass, GEN_REF_MULTI_CONTAINED__ELEMENTS);

    genRefMultiNonContainedEClass = createEClass(GEN_REF_MULTI_NON_CONTAINED);
    createEReference(genRefMultiNonContainedEClass, GEN_REF_MULTI_NON_CONTAINED__ELEMENTS);

    implSingleRefContainerEClass = createEClass(IMPL_SINGLE_REF_CONTAINER);

    implSingleRefContainedElementEClass = createEClass(IMPL_SINGLE_REF_CONTAINED_ELEMENT);
    createEAttribute(implSingleRefContainedElementEClass, IMPL_SINGLE_REF_CONTAINED_ELEMENT__NAME);

    implSingleRefNonContainerEClass = createEClass(IMPL_SINGLE_REF_NON_CONTAINER);

    implSingleRefNonContainedElementEClass = createEClass(IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT);
    createEAttribute(implSingleRefNonContainedElementEClass, IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__NAME);

    implMultiRefNonContainerEClass = createEClass(IMPL_MULTI_REF_NON_CONTAINER);

    implMultiRefNonContainedElementEClass = createEClass(IMPL_MULTI_REF_NON_CONTAINED_ELEMENT);
    createEAttribute(implMultiRefNonContainedElementEClass, IMPL_MULTI_REF_NON_CONTAINED_ELEMENT__NAME);

    implMultiRefContainerEClass = createEClass(IMPL_MULTI_REF_CONTAINER);

    implMultiRefContainedElementEClass = createEClass(IMPL_MULTI_REF_CONTAINED_ELEMENT);
    createEAttribute(implMultiRefContainedElementEClass, IMPL_MULTI_REF_CONTAINED_ELEMENT__NAME);

    implSingleRefContainerNPLEClass = createEClass(IMPL_SINGLE_REF_CONTAINER_NPL);

    implSingleRefNonContainerNPLEClass = createEClass(IMPL_SINGLE_REF_NON_CONTAINER_NPL);

    implMultiRefContainerNPLEClass = createEClass(IMPL_MULTI_REF_CONTAINER_NPL);

    implMultiRefNonContainerNPLEClass = createEClass(IMPL_MULTI_REF_NON_CONTAINER_NPL);

    implContainedElementNPLEClass = createEClass(IMPL_CONTAINED_ELEMENT_NPL);

    genRefMultiNUNonContainedEClass = createEClass(GEN_REF_MULTI_NU_NON_CONTAINED);
    createEReference(genRefMultiNUNonContainedEClass, GEN_REF_MULTI_NU_NON_CONTAINED__ELEMENTS);

    genRefMapNonContainedEClass = createEClass(GEN_REF_MAP_NON_CONTAINED);
    createEReference(genRefMapNonContainedEClass, GEN_REF_MAP_NON_CONTAINED__ELEMENTS);

    stringToEObjectEClass = createEClass(STRING_TO_EOBJECT);
    createEAttribute(stringToEObjectEClass, STRING_TO_EOBJECT__KEY);
    createEReference(stringToEObjectEClass, STRING_TO_EOBJECT__VALUE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    model4interfacesPackage themodel4interfacesPackage = (model4interfacesPackage)EPackage.Registry.INSTANCE.getEPackage(model4interfacesPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    implSingleRefContainerEClass.getESuperTypes().add(themodel4interfacesPackage.getISingleRefContainer());
    implSingleRefContainedElementEClass.getESuperTypes().add(themodel4interfacesPackage.getISingleRefContainedElement());
    implSingleRefNonContainerEClass.getESuperTypes().add(themodel4interfacesPackage.getISingleRefNonContainer());
    implSingleRefNonContainedElementEClass.getESuperTypes().add(themodel4interfacesPackage.getISingleRefNonContainedElement());
    implMultiRefNonContainerEClass.getESuperTypes().add(themodel4interfacesPackage.getIMultiRefNonContainer());
    implMultiRefNonContainedElementEClass.getESuperTypes().add(themodel4interfacesPackage.getIMultiRefNonContainedElement());
    implMultiRefContainerEClass.getESuperTypes().add(themodel4interfacesPackage.getIMultiRefContainer());
    implMultiRefContainedElementEClass.getESuperTypes().add(themodel4interfacesPackage.getIMultiRefContainedElement());
    implSingleRefContainerNPLEClass.getESuperTypes().add(themodel4interfacesPackage.getISingleRefContainerNPL());
    implSingleRefNonContainerNPLEClass.getESuperTypes().add(themodel4interfacesPackage.getISingleRefNonContainerNPL());
    implMultiRefContainerNPLEClass.getESuperTypes().add(themodel4interfacesPackage.getIMultiRefContainerNPL());
    implMultiRefNonContainerNPLEClass.getESuperTypes().add(themodel4interfacesPackage.getIMultiRefNonContainerNPL());
    implContainedElementNPLEClass.getESuperTypes().add(themodel4interfacesPackage.getIContainedElementNoParentLink());
    implContainedElementNPLEClass.getESuperTypes().add(themodel4interfacesPackage.getINamedElement());

    // Initialize classes and features; add operations and parameters
    initEClass(refSingleContainedEClass, RefSingleContained.class, "RefSingleContained", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefSingleContained_Element(), getSingleContainedElement(), getSingleContainedElement_Parent(), "element", null, 0, 1,
        RefSingleContained.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(singleContainedElementEClass, SingleContainedElement.class, "SingleContainedElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSingleContainedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, SingleContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSingleContainedElement_Parent(), getRefSingleContained(), getRefSingleContained_Element(), "parent", null, 0, 1,
        SingleContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(refSingleNonContainedEClass, RefSingleNonContained.class, "RefSingleNonContained", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefSingleNonContained_Element(), getSingleNonContainedElement(), getSingleNonContainedElement_Parent(), "element", null, 0, 1,
        RefSingleNonContained.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(singleNonContainedElementEClass, SingleNonContainedElement.class, "SingleNonContainedElement", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSingleNonContainedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, SingleNonContainedElement.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSingleNonContainedElement_Parent(), getRefSingleNonContained(), getRefSingleNonContained_Element(), "parent", null, 0, 1,
        SingleNonContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(refMultiContainedEClass, RefMultiContained.class, "RefMultiContained", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefMultiContained_Elements(), getMultiContainedElement(), getMultiContainedElement_Parent(), "elements", null, 0, -1,
        RefMultiContained.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(multiContainedElementEClass, MultiContainedElement.class, "MultiContainedElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMultiContainedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, MultiContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMultiContainedElement_Parent(), getRefMultiContained(), getRefMultiContained_Elements(), "parent", null, 0, 1,
        MultiContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(refMultiNonContainedEClass, RefMultiNonContained.class, "RefMultiNonContained", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefMultiNonContained_Elements(), getMultiNonContainedElement(), getMultiNonContainedElement_Parent(), "elements", null, 0, -1,
        RefMultiNonContained.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(multiNonContainedElementEClass, MultiNonContainedElement.class, "MultiNonContainedElement", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMultiNonContainedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, MultiNonContainedElement.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMultiNonContainedElement_Parent(), getRefMultiNonContained(), getRefMultiNonContained_Elements(), "parent", null, 0, 1,
        MultiNonContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(refMultiNonContainedUnsettableEClass, RefMultiNonContainedUnsettable.class, "RefMultiNonContainedUnsettable", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefMultiNonContainedUnsettable_Elements(), getMultiNonContainedUnsettableElement(), getMultiNonContainedUnsettableElement_Parent(),
        "elements", null, 0, -1, RefMultiNonContainedUnsettable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(multiNonContainedUnsettableElementEClass, MultiNonContainedUnsettableElement.class, "MultiNonContainedUnsettableElement", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMultiNonContainedUnsettableElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, MultiNonContainedUnsettableElement.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMultiNonContainedUnsettableElement_Parent(), getRefMultiNonContainedUnsettable(), getRefMultiNonContainedUnsettable_Elements(), "parent",
        null, 0, 1, MultiNonContainedUnsettableElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(refSingleContainedNPLEClass, RefSingleContainedNPL.class, "RefSingleContainedNPL", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefSingleContainedNPL_Element(), getContainedElementNoOpposite(), null, "element", null, 0, 1, RefSingleContainedNPL.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(refSingleNonContainedNPLEClass, RefSingleNonContainedNPL.class, "RefSingleNonContainedNPL", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefSingleNonContainedNPL_Element(), getContainedElementNoOpposite(), null, "element", null, 0, 1, RefSingleNonContainedNPL.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(refMultiContainedNPLEClass, RefMultiContainedNPL.class, "RefMultiContainedNPL", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefMultiContainedNPL_Elements(), getContainedElementNoOpposite(), null, "elements", null, 0, -1, RefMultiContainedNPL.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(refMultiNonContainedNPLEClass, RefMultiNonContainedNPL.class, "RefMultiNonContainedNPL", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefMultiNonContainedNPL_Elements(), getContainedElementNoOpposite(), null, "elements", null, 0, -1, RefMultiNonContainedNPL.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(containedElementNoOppositeEClass, ContainedElementNoOpposite.class, "ContainedElementNoOpposite", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getContainedElementNoOpposite_Name(), ecorePackage.getEString(), "name", null, 0, 1, ContainedElementNoOpposite.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genRefSingleContainedEClass, GenRefSingleContained.class, "GenRefSingleContained", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGenRefSingleContained_Element(), ecorePackage.getEObject(), null, "element", null, 0, 1, GenRefSingleContained.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genRefSingleNonContainedEClass, GenRefSingleNonContained.class, "GenRefSingleNonContained", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGenRefSingleNonContained_Element(), ecorePackage.getEObject(), null, "element", null, 0, 1, GenRefSingleNonContained.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genRefMultiContainedEClass, GenRefMultiContained.class, "GenRefMultiContained", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGenRefMultiContained_Elements(), ecorePackage.getEObject(), null, "elements", null, 0, -1, GenRefMultiContained.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genRefMultiNonContainedEClass, GenRefMultiNonContained.class, "GenRefMultiNonContained", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGenRefMultiNonContained_Elements(), ecorePackage.getEObject(), null, "elements", null, 0, -1, GenRefMultiNonContained.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(implSingleRefContainerEClass, ImplSingleRefContainer.class, "ImplSingleRefContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(implSingleRefContainedElementEClass, ImplSingleRefContainedElement.class, "ImplSingleRefContainedElement", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getImplSingleRefContainedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, ImplSingleRefContainedElement.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(implSingleRefNonContainerEClass, ImplSingleRefNonContainer.class, "ImplSingleRefNonContainer", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(implSingleRefNonContainedElementEClass, ImplSingleRefNonContainedElement.class, "ImplSingleRefNonContainedElement", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getImplSingleRefNonContainedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, ImplSingleRefNonContainedElement.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(implMultiRefNonContainerEClass, ImplMultiRefNonContainer.class, "ImplMultiRefNonContainer", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(implMultiRefNonContainedElementEClass, ImplMultiRefNonContainedElement.class, "ImplMultiRefNonContainedElement", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getImplMultiRefNonContainedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, ImplMultiRefNonContainedElement.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(implMultiRefContainerEClass, ImplMultiRefContainer.class, "ImplMultiRefContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(implMultiRefContainedElementEClass, ImplMultiRefContainedElement.class, "ImplMultiRefContainedElement", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getImplMultiRefContainedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, ImplMultiRefContainedElement.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(implSingleRefContainerNPLEClass, ImplSingleRefContainerNPL.class, "ImplSingleRefContainerNPL", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(implSingleRefNonContainerNPLEClass, ImplSingleRefNonContainerNPL.class, "ImplSingleRefNonContainerNPL", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(implMultiRefContainerNPLEClass, ImplMultiRefContainerNPL.class, "ImplMultiRefContainerNPL", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(implMultiRefNonContainerNPLEClass, ImplMultiRefNonContainerNPL.class, "ImplMultiRefNonContainerNPL", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(implContainedElementNPLEClass, ImplContainedElementNPL.class, "ImplContainedElementNPL", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(genRefMultiNUNonContainedEClass, GenRefMultiNUNonContained.class, "GenRefMultiNUNonContained", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGenRefMultiNUNonContained_Elements(), ecorePackage.getEObject(), null, "elements", null, 0, -1, GenRefMultiNUNonContained.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genRefMapNonContainedEClass, GenRefMapNonContained.class, "GenRefMapNonContained", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGenRefMapNonContained_Elements(), getStringToEObject(), null, "elements", null, 0, -1, GenRefMapNonContained.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringToEObjectEClass, Map.Entry.class, "StringToEObject", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringToEObject_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStringToEObject_Value(), ecorePackage.getEObject(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // model4PackageImpl
