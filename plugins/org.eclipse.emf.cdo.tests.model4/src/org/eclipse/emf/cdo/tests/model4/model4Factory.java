/*
 * Copyright (c) 2008-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model4.model4Package
 * @generated
 */
public interface model4Factory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  model4Factory eINSTANCE = org.eclipse.emf.cdo.tests.model4.impl.model4FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Ref Single Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Single Contained</em>'.
   * @generated
   */
  RefSingleContained createRefSingleContained();

  /**
   * Returns a new object of class '<em>Single Contained Element</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Single Contained Element</em>'.
   * @generated
   */
  SingleContainedElement createSingleContainedElement();

  /**
   * Returns a new object of class '<em>Ref Single Non Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Single Non Contained</em>'.
   * @generated
   */
  RefSingleNonContained createRefSingleNonContained();

  /**
   * Returns a new object of class '<em>Single Non Contained Element</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Single Non Contained Element</em>'.
   * @generated
   */
  SingleNonContainedElement createSingleNonContainedElement();

  /**
   * Returns a new object of class '<em>Ref Multi Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Multi Contained</em>'.
   * @generated
   */
  RefMultiContained createRefMultiContained();

  /**
   * Returns a new object of class '<em>Multi Contained Element</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Multi Contained Element</em>'.
   * @generated
   */
  MultiContainedElement createMultiContainedElement();

  /**
   * Returns a new object of class '<em>Ref Multi Non Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Multi Non Contained</em>'.
   * @generated
   */
  RefMultiNonContained createRefMultiNonContained();

  /**
   * Returns a new object of class '<em>Multi Non Contained Element</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Multi Non Contained Element</em>'.
   * @generated
   */
  MultiNonContainedElement createMultiNonContainedElement();

  /**
   * Returns a new object of class '<em>Ref Multi Non Contained Unsettable</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Ref Multi Non Contained Unsettable</em>'.
   * @generated
   */
  RefMultiNonContainedUnsettable createRefMultiNonContainedUnsettable();

  /**
   * Returns a new object of class '<em>Multi Non Contained Unsettable Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Multi Non Contained Unsettable Element</em>'.
   * @generated
   */
  MultiNonContainedUnsettableElement createMultiNonContainedUnsettableElement();

  /**
   * Returns a new object of class '<em>Ref Single Contained NPL</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Single Contained NPL</em>'.
   * @generated
   */
  RefSingleContainedNPL createRefSingleContainedNPL();

  /**
   * Returns a new object of class '<em>Ref Single Non Contained NPL</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Ref Single Non Contained NPL</em>'.
   * @generated
   */
  RefSingleNonContainedNPL createRefSingleNonContainedNPL();

  /**
   * Returns a new object of class '<em>Ref Multi Contained NPL</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Multi Contained NPL</em>'.
   * @generated
   */
  RefMultiContainedNPL createRefMultiContainedNPL();

  /**
   * Returns a new object of class '<em>Ref Multi Non Contained NPL</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Ref Multi Non Contained NPL</em>'.
   * @generated
   */
  RefMultiNonContainedNPL createRefMultiNonContainedNPL();

  /**
   * Returns a new object of class '<em>Contained Element No Opposite</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Contained Element No Opposite</em>'.
   * @generated
   */
  ContainedElementNoOpposite createContainedElementNoOpposite();

  /**
   * Returns a new object of class '<em>Gen Ref Single Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen Ref Single Contained</em>'.
   * @generated
   */
  GenRefSingleContained createGenRefSingleContained();

  /**
   * Returns a new object of class '<em>Gen Ref Single Non Contained</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Gen Ref Single Non Contained</em>'.
   * @generated
   */
  GenRefSingleNonContained createGenRefSingleNonContained();

  /**
   * Returns a new object of class '<em>Gen Ref Multi Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen Ref Multi Contained</em>'.
   * @generated
   */
  GenRefMultiContained createGenRefMultiContained();

  /**
   * Returns a new object of class '<em>Gen Ref Multi Non Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen Ref Multi Non Contained</em>'.
   * @generated
   */
  GenRefMultiNonContained createGenRefMultiNonContained();

  /**
   * Returns a new object of class '<em>Impl Single Ref Container</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Impl Single Ref Container</em>'.
   * @generated
   */
  ImplSingleRefContainer createImplSingleRefContainer();

  /**
   * Returns a new object of class '<em>Impl Single Ref Contained Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Single Ref Contained Element</em>'.
   * @generated
   */
  ImplSingleRefContainedElement createImplSingleRefContainedElement();

  /**
   * Returns a new object of class '<em>Impl Single Ref Non Container</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Impl Single Ref Non Container</em>'.
   * @generated
   */
  ImplSingleRefNonContainer createImplSingleRefNonContainer();

  /**
   * Returns a new object of class '<em>Impl Single Ref Non Contained Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Single Ref Non Contained Element</em>'.
   * @generated
   */
  ImplSingleRefNonContainedElement createImplSingleRefNonContainedElement();

  /**
   * Returns a new object of class '<em>Impl Multi Ref Non Container</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Impl Multi Ref Non Container</em>'.
   * @generated
   */
  ImplMultiRefNonContainer createImplMultiRefNonContainer();

  /**
   * Returns a new object of class '<em>Impl Multi Ref Non Contained Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Multi Ref Non Contained Element</em>'.
   * @generated
   */
  ImplMultiRefNonContainedElement createImplMultiRefNonContainedElement();

  /**
   * Returns a new object of class '<em>Impl Multi Ref Container</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Impl Multi Ref Container</em>'.
   * @generated
   */
  ImplMultiRefContainer createImplMultiRefContainer();

  /**
   * Returns a new object of class '<em>Impl Multi Ref Contained Element</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Multi Ref Contained Element</em>'.
   * @generated
   */
  ImplMultiRefContainedElement createImplMultiRefContainedElement();

  /**
   * Returns a new object of class '<em>Impl Single Ref Container NPL</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Impl Single Ref Container NPL</em>'.
   * @generated
   */
  ImplSingleRefContainerNPL createImplSingleRefContainerNPL();

  /**
   * Returns a new object of class '<em>Impl Single Ref Non Container NPL</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Single Ref Non Container NPL</em>'.
   * @generated
   */
  ImplSingleRefNonContainerNPL createImplSingleRefNonContainerNPL();

  /**
   * Returns a new object of class '<em>Impl Multi Ref Container NPL</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Impl Multi Ref Container NPL</em>'.
   * @generated
   */
  ImplMultiRefContainerNPL createImplMultiRefContainerNPL();

  /**
   * Returns a new object of class '<em>Impl Multi Ref Non Container NPL</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Impl Multi Ref Non Container NPL</em>'.
   * @generated
   */
  ImplMultiRefNonContainerNPL createImplMultiRefNonContainerNPL();

  /**
   * Returns a new object of class '<em>Impl Contained Element NPL</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Impl Contained Element NPL</em>'.
   * @generated
   */
  ImplContainedElementNPL createImplContainedElementNPL();

  /**
   * Returns a new object of class '<em>Gen Ref Multi NU Non Contained</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return a new object of class '<em>Gen Ref Multi NU Non Contained</em>'.
   * @generated
   */
  GenRefMultiNUNonContained createGenRefMultiNUNonContained();

  /**
   * Returns a new object of class '<em>Gen Ref Map Non Contained</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen Ref Map Non Contained</em>'.
   * @generated
   */
  GenRefMapNonContained createGenRefMapNonContained();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  model4Package getmodel4Package();

} // model4Factory
