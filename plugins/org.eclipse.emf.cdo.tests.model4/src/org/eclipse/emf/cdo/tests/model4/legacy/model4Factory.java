/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4.legacy;

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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * @extends org.eclipse.emf.cdo.tests.model4.model4Factory
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model4.legacy.model4Package
 * @generated
 */
public interface model4Factory extends EFactory, org.eclipse.emf.cdo.tests.model4.model4Factory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  model4Factory eINSTANCE = org.eclipse.emf.cdo.tests.model4.legacy.impl.model4FactoryImpl.init();

  @Override
  /**
   * Returns a new object of class '<em>Ref Single Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Single Contained</em>'.
   * @generated
   */
  RefSingleContained createRefSingleContained();

  @Override
  /**
   * Returns a new object of class '<em>Single Contained Element</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Single Contained Element</em>'.
   * @generated
   */
  SingleContainedElement createSingleContainedElement();

  @Override
  /**
   * Returns a new object of class '<em>Ref Single Non Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Single Non Contained</em>'.
   * @generated
   */
  RefSingleNonContained createRefSingleNonContained();

  @Override
  /**
   * Returns a new object of class '<em>Single Non Contained Element</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Single Non Contained Element</em>'.
   * @generated
   */
  SingleNonContainedElement createSingleNonContainedElement();

  @Override
  /**
   * Returns a new object of class '<em>Ref Multi Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Multi Contained</em>'.
   * @generated
   */
  RefMultiContained createRefMultiContained();

  @Override
  /**
   * Returns a new object of class '<em>Multi Contained Element</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Multi Contained Element</em>'.
   * @generated
   */
  MultiContainedElement createMultiContainedElement();

  @Override
  /**
   * Returns a new object of class '<em>Ref Multi Non Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Multi Non Contained</em>'.
   * @generated
   */
  RefMultiNonContained createRefMultiNonContained();

  @Override
  /**
   * Returns a new object of class '<em>Multi Non Contained Element</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Multi Non Contained Element</em>'.
   * @generated
   */
  MultiNonContainedElement createMultiNonContainedElement();

  @Override
  /**
   * Returns a new object of class '<em>Ref Multi Non Contained Unsettable</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Ref Multi Non Contained Unsettable</em>'.
   * @generated
   */
  RefMultiNonContainedUnsettable createRefMultiNonContainedUnsettable();

  @Override
  /**
   * Returns a new object of class '<em>Multi Non Contained Unsettable Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Multi Non Contained Unsettable Element</em>'.
   * @generated
   */
  MultiNonContainedUnsettableElement createMultiNonContainedUnsettableElement();

  @Override
  /**
   * Returns a new object of class '<em>Ref Single Contained NPL</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Single Contained NPL</em>'.
   * @generated
   */
  RefSingleContainedNPL createRefSingleContainedNPL();

  @Override
  /**
   * Returns a new object of class '<em>Ref Single Non Contained NPL</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Ref Single Non Contained NPL</em>'.
   * @generated
   */
  RefSingleNonContainedNPL createRefSingleNonContainedNPL();

  @Override
  /**
   * Returns a new object of class '<em>Ref Multi Contained NPL</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Multi Contained NPL</em>'.
   * @generated
   */
  RefMultiContainedNPL createRefMultiContainedNPL();

  @Override
  /**
   * Returns a new object of class '<em>Ref Multi Non Contained NPL</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Multi Non Contained NPL</em>'.
   * @generated
   */
  RefMultiNonContainedNPL createRefMultiNonContainedNPL();

  @Override
  /**
   * Returns a new object of class '<em>Contained Element No Opposite</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Contained Element No Opposite</em>'.
   * @generated
   */
  ContainedElementNoOpposite createContainedElementNoOpposite();

  @Override
  /**
   * Returns a new object of class '<em>Gen Ref Single Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen Ref Single Contained</em>'.
   * @generated
   */
  GenRefSingleContained createGenRefSingleContained();

  @Override
  /**
   * Returns a new object of class '<em>Gen Ref Single Non Contained</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Gen Ref Single Non Contained</em>'.
   * @generated
   */
  GenRefSingleNonContained createGenRefSingleNonContained();

  @Override
  /**
   * Returns a new object of class '<em>Gen Ref Multi Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen Ref Multi Contained</em>'.
   * @generated
   */
  GenRefMultiContained createGenRefMultiContained();

  @Override
  /**
   * Returns a new object of class '<em>Gen Ref Multi Non Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen Ref Multi Non Contained</em>'.
   * @generated
   */
  GenRefMultiNonContained createGenRefMultiNonContained();

  @Override
  /**
   * Returns a new object of class '<em>Impl Single Ref Container</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Impl Single Ref Container</em>'.
   * @generated
   */
  ImplSingleRefContainer createImplSingleRefContainer();

  @Override
  /**
   * Returns a new object of class '<em>Impl Single Ref Contained Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Single Ref Contained Element</em>'.
   * @generated
   */
  ImplSingleRefContainedElement createImplSingleRefContainedElement();

  @Override
  /**
   * Returns a new object of class '<em>Impl Single Ref Non Container</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Impl Single Ref Non Container</em>'.
   * @generated
   */
  ImplSingleRefNonContainer createImplSingleRefNonContainer();

  @Override
  /**
   * Returns a new object of class '<em>Impl Single Ref Non Contained Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Single Ref Non Contained Element</em>'.
   * @generated
   */
  ImplSingleRefNonContainedElement createImplSingleRefNonContainedElement();

  @Override
  /**
   * Returns a new object of class '<em>Impl Multi Ref Non Container</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Impl Multi Ref Non Container</em>'.
   * @generated
   */
  ImplMultiRefNonContainer createImplMultiRefNonContainer();

  @Override
  /**
   * Returns a new object of class '<em>Impl Multi Ref Non Contained Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Multi Ref Non Contained Element</em>'.
   * @generated
   */
  ImplMultiRefNonContainedElement createImplMultiRefNonContainedElement();

  @Override
  /**
   * Returns a new object of class '<em>Impl Multi Ref Container</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Impl Multi Ref Container</em>'.
   * @generated
   */
  ImplMultiRefContainer createImplMultiRefContainer();

  @Override
  /**
   * Returns a new object of class '<em>Impl Multi Ref Contained Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Multi Ref Contained Element</em>'.
   * @generated
   */
  ImplMultiRefContainedElement createImplMultiRefContainedElement();

  @Override
  /**
   * Returns a new object of class '<em>Impl Single Ref Container NPL</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Impl Single Ref Container NPL</em>'.
   * @generated
   */
  ImplSingleRefContainerNPL createImplSingleRefContainerNPL();

  @Override
  /**
   * Returns a new object of class '<em>Impl Single Ref Non Container NPL</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Single Ref Non Container NPL</em>'.
   * @generated
   */
  ImplSingleRefNonContainerNPL createImplSingleRefNonContainerNPL();

  @Override
  /**
   * Returns a new object of class '<em>Impl Multi Ref Container NPL</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Impl Multi Ref Container NPL</em>'.
   * @generated
   */
  ImplMultiRefContainerNPL createImplMultiRefContainerNPL();

  @Override
  /**
   * Returns a new object of class '<em>Impl Multi Ref Non Container NPL</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Multi Ref Non Container NPL</em>'.
   * @generated
   */
  ImplMultiRefNonContainerNPL createImplMultiRefNonContainerNPL();

  @Override
  /**
   * Returns a new object of class '<em>Impl Contained Element NPL</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Impl Contained Element NPL</em>'.
   * @generated
   */
  ImplContainedElementNPL createImplContainedElementNPL();

  @Override
  /**
   * Returns a new object of class '<em>Gen Ref Multi NU Non Contained</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Gen Ref Multi NU Non Contained</em>'.
   * @generated
   */
  GenRefMultiNUNonContained createGenRefMultiNUNonContained();

  @Override
  /**
   * Returns a new object of class '<em>Gen Ref Map Non Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen Ref Map Non Contained</em>'.
   * @generated
   */
  GenRefMapNonContained createGenRefMapNonContained();

  @Override
  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  model4Package getmodel4Package();

} // model4Factory
