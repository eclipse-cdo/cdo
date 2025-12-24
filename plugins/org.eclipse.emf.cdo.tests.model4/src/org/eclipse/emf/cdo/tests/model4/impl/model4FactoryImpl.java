/*
 * Copyright (c) 2008-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4.impl;

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
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import java.util.Map;

//import org.eclipse.emf.cdo.tests.model4.*;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class model4FactoryImpl extends EFactoryImpl implements model4Factory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public static model4Factory init()
  {
    try
    {
      model4Factory themodel4Factory = (model4Factory)EPackage.Registry.INSTANCE.getEFactory(model4Package.eNS_URI);
      if (themodel4Factory != null)
      {
        return themodel4Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new model4FactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public model4FactoryImpl()
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
    case model4Package.REF_SINGLE_CONTAINED:
      return createRefSingleContained();
    case model4Package.SINGLE_CONTAINED_ELEMENT:
      return createSingleContainedElement();
    case model4Package.REF_SINGLE_NON_CONTAINED:
      return createRefSingleNonContained();
    case model4Package.SINGLE_NON_CONTAINED_ELEMENT:
      return createSingleNonContainedElement();
    case model4Package.REF_MULTI_CONTAINED:
      return createRefMultiContained();
    case model4Package.MULTI_CONTAINED_ELEMENT:
      return createMultiContainedElement();
    case model4Package.REF_MULTI_NON_CONTAINED:
      return createRefMultiNonContained();
    case model4Package.MULTI_NON_CONTAINED_ELEMENT:
      return createMultiNonContainedElement();
    case model4Package.REF_MULTI_NON_CONTAINED_UNSETTABLE:
      return createRefMultiNonContainedUnsettable();
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT:
      return createMultiNonContainedUnsettableElement();
    case model4Package.REF_SINGLE_CONTAINED_NPL:
      return createRefSingleContainedNPL();
    case model4Package.REF_SINGLE_NON_CONTAINED_NPL:
      return createRefSingleNonContainedNPL();
    case model4Package.REF_MULTI_CONTAINED_NPL:
      return createRefMultiContainedNPL();
    case model4Package.REF_MULTI_NON_CONTAINED_NPL:
      return createRefMultiNonContainedNPL();
    case model4Package.CONTAINED_ELEMENT_NO_OPPOSITE:
      return createContainedElementNoOpposite();
    case model4Package.GEN_REF_SINGLE_CONTAINED:
      return createGenRefSingleContained();
    case model4Package.GEN_REF_SINGLE_NON_CONTAINED:
      return createGenRefSingleNonContained();
    case model4Package.GEN_REF_MULTI_CONTAINED:
      return createGenRefMultiContained();
    case model4Package.GEN_REF_MULTI_NON_CONTAINED:
      return createGenRefMultiNonContained();
    case model4Package.IMPL_SINGLE_REF_CONTAINER:
      return createImplSingleRefContainer();
    case model4Package.IMPL_SINGLE_REF_CONTAINED_ELEMENT:
      return createImplSingleRefContainedElement();
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER:
      return createImplSingleRefNonContainer();
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT:
      return createImplSingleRefNonContainedElement();
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER:
      return createImplMultiRefNonContainer();
    case model4Package.IMPL_MULTI_REF_NON_CONTAINED_ELEMENT:
      return createImplMultiRefNonContainedElement();
    case model4Package.IMPL_MULTI_REF_CONTAINER:
      return createImplMultiRefContainer();
    case model4Package.IMPL_MULTI_REF_CONTAINED_ELEMENT:
      return createImplMultiRefContainedElement();
    case model4Package.IMPL_SINGLE_REF_CONTAINER_NPL:
      return createImplSingleRefContainerNPL();
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER_NPL:
      return createImplSingleRefNonContainerNPL();
    case model4Package.IMPL_MULTI_REF_CONTAINER_NPL:
      return createImplMultiRefContainerNPL();
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER_NPL:
      return createImplMultiRefNonContainerNPL();
    case model4Package.IMPL_CONTAINED_ELEMENT_NPL:
      return createImplContainedElementNPL();
    case model4Package.GEN_REF_MULTI_NU_NON_CONTAINED:
      return createGenRefMultiNUNonContained();
    case model4Package.GEN_REF_MAP_NON_CONTAINED:
      return createGenRefMapNonContained();
    case model4Package.STRING_TO_EOBJECT:
      return (EObject)createStringToEObject();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefSingleContained createRefSingleContained()
  {
    RefSingleContainedImpl refSingleContained = new RefSingleContainedImpl();
    return refSingleContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SingleContainedElement createSingleContainedElement()
  {
    SingleContainedElementImpl singleContainedElement = new SingleContainedElementImpl();
    return singleContainedElement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefSingleNonContained createRefSingleNonContained()
  {
    RefSingleNonContainedImpl refSingleNonContained = new RefSingleNonContainedImpl();
    return refSingleNonContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SingleNonContainedElement createSingleNonContainedElement()
  {
    SingleNonContainedElementImpl singleNonContainedElement = new SingleNonContainedElementImpl();
    return singleNonContainedElement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefMultiContained createRefMultiContained()
  {
    RefMultiContainedImpl refMultiContained = new RefMultiContainedImpl();
    return refMultiContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MultiContainedElement createMultiContainedElement()
  {
    MultiContainedElementImpl multiContainedElement = new MultiContainedElementImpl();
    return multiContainedElement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefMultiNonContained createRefMultiNonContained()
  {
    RefMultiNonContainedImpl refMultiNonContained = new RefMultiNonContainedImpl();
    return refMultiNonContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MultiNonContainedElement createMultiNonContainedElement()
  {
    MultiNonContainedElementImpl multiNonContainedElement = new MultiNonContainedElementImpl();
    return multiNonContainedElement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefMultiNonContainedUnsettable createRefMultiNonContainedUnsettable()
  {
    RefMultiNonContainedUnsettableImpl refMultiNonContainedUnsettable = new RefMultiNonContainedUnsettableImpl();
    return refMultiNonContainedUnsettable;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MultiNonContainedUnsettableElement createMultiNonContainedUnsettableElement()
  {
    MultiNonContainedUnsettableElementImpl multiNonContainedUnsettableElement = new MultiNonContainedUnsettableElementImpl();
    return multiNonContainedUnsettableElement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefSingleContainedNPL createRefSingleContainedNPL()
  {
    RefSingleContainedNPLImpl refSingleContainedNPL = new RefSingleContainedNPLImpl();
    return refSingleContainedNPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefSingleNonContainedNPL createRefSingleNonContainedNPL()
  {
    RefSingleNonContainedNPLImpl refSingleNonContainedNPL = new RefSingleNonContainedNPLImpl();
    return refSingleNonContainedNPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefMultiContainedNPL createRefMultiContainedNPL()
  {
    RefMultiContainedNPLImpl refMultiContainedNPL = new RefMultiContainedNPLImpl();
    return refMultiContainedNPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefMultiNonContainedNPL createRefMultiNonContainedNPL()
  {
    RefMultiNonContainedNPLImpl refMultiNonContainedNPL = new RefMultiNonContainedNPLImpl();
    return refMultiNonContainedNPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ContainedElementNoOpposite createContainedElementNoOpposite()
  {
    ContainedElementNoOppositeImpl containedElementNoOpposite = new ContainedElementNoOppositeImpl();
    return containedElementNoOpposite;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public GenRefSingleContained createGenRefSingleContained()
  {
    GenRefSingleContainedImpl genRefSingleContained = new GenRefSingleContainedImpl();
    return genRefSingleContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public GenRefSingleNonContained createGenRefSingleNonContained()
  {
    GenRefSingleNonContainedImpl genRefSingleNonContained = new GenRefSingleNonContainedImpl();
    return genRefSingleNonContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public GenRefMultiContained createGenRefMultiContained()
  {
    GenRefMultiContainedImpl genRefMultiContained = new GenRefMultiContainedImpl();
    return genRefMultiContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public GenRefMultiNonContained createGenRefMultiNonContained()
  {
    GenRefMultiNonContainedImpl genRefMultiNonContained = new GenRefMultiNonContainedImpl();
    return genRefMultiNonContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplSingleRefContainer createImplSingleRefContainer()
  {
    ImplSingleRefContainerImpl implSingleRefContainer = new ImplSingleRefContainerImpl();
    return implSingleRefContainer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplSingleRefContainedElement createImplSingleRefContainedElement()
  {
    ImplSingleRefContainedElementImpl implSingleRefContainedElement = new ImplSingleRefContainedElementImpl();
    return implSingleRefContainedElement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplSingleRefNonContainer createImplSingleRefNonContainer()
  {
    ImplSingleRefNonContainerImpl implSingleRefNonContainer = new ImplSingleRefNonContainerImpl();
    return implSingleRefNonContainer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplSingleRefNonContainedElement createImplSingleRefNonContainedElement()
  {
    ImplSingleRefNonContainedElementImpl implSingleRefNonContainedElement = new ImplSingleRefNonContainedElementImpl();
    return implSingleRefNonContainedElement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplMultiRefNonContainer createImplMultiRefNonContainer()
  {
    ImplMultiRefNonContainerImpl implMultiRefNonContainer = new ImplMultiRefNonContainerImpl();
    return implMultiRefNonContainer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplMultiRefNonContainedElement createImplMultiRefNonContainedElement()
  {
    ImplMultiRefNonContainedElementImpl implMultiRefNonContainedElement = new ImplMultiRefNonContainedElementImpl();
    return implMultiRefNonContainedElement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplMultiRefContainer createImplMultiRefContainer()
  {
    ImplMultiRefContainerImpl implMultiRefContainer = new ImplMultiRefContainerImpl();
    return implMultiRefContainer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplMultiRefContainedElement createImplMultiRefContainedElement()
  {
    ImplMultiRefContainedElementImpl implMultiRefContainedElement = new ImplMultiRefContainedElementImpl();
    return implMultiRefContainedElement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplSingleRefContainerNPL createImplSingleRefContainerNPL()
  {
    ImplSingleRefContainerNPLImpl implSingleRefContainerNPL = new ImplSingleRefContainerNPLImpl();
    return implSingleRefContainerNPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplSingleRefNonContainerNPL createImplSingleRefNonContainerNPL()
  {
    ImplSingleRefNonContainerNPLImpl implSingleRefNonContainerNPL = new ImplSingleRefNonContainerNPLImpl();
    return implSingleRefNonContainerNPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplMultiRefContainerNPL createImplMultiRefContainerNPL()
  {
    ImplMultiRefContainerNPLImpl implMultiRefContainerNPL = new ImplMultiRefContainerNPLImpl();
    return implMultiRefContainerNPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplMultiRefNonContainerNPL createImplMultiRefNonContainerNPL()
  {
    ImplMultiRefNonContainerNPLImpl implMultiRefNonContainerNPL = new ImplMultiRefNonContainerNPLImpl();
    return implMultiRefNonContainerNPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImplContainedElementNPL createImplContainedElementNPL()
  {
    ImplContainedElementNPLImpl implContainedElementNPL = new ImplContainedElementNPLImpl();
    return implContainedElementNPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public GenRefMultiNUNonContained createGenRefMultiNUNonContained()
  {
    GenRefMultiNUNonContainedImpl genRefMultiNUNonContained = new GenRefMultiNUNonContainedImpl();
    return genRefMultiNUNonContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public GenRefMapNonContained createGenRefMapNonContained()
  {
    GenRefMapNonContainedImpl genRefMapNonContained = new GenRefMapNonContainedImpl();
    return genRefMapNonContained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<String, EObject> createStringToEObject()
  {
    StringToEObjectImpl stringToEObject = new StringToEObjectImpl();
    return stringToEObject;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public model4Package getmodel4Package()
  {
    return (model4Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static model4Package getPackage()
  {
    return model4Package.eINSTANCE;
  }

} // model4FactoryImpl
