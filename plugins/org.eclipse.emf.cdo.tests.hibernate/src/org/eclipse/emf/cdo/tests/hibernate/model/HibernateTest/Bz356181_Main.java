/*
 * Copyright (c) 2012, 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bz356181 Main</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransient <em>Transient</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getNonTransient <em>Non Transient</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientRef <em>Transient Ref</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientOtherRef <em>Transient Other Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz356181_Main()
 * @model
 * @generated
 */
public interface Bz356181_Main extends EObject
{
  /**
   * Returns the value of the '<em><b>Transient</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transient</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transient</em>' attribute.
   * @see #setTransient(String)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz356181_Main_Transient()
   * @model annotation="teneo.jpa value='@Transient'"
   * @generated
   */
  String getTransient();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransient <em>Transient</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Transient</em>' attribute.
   * @see #getTransient()
   * @generated
   */
  void setTransient(String value);

  /**
   * Returns the value of the '<em><b>Non Transient</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Non Transient</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Non Transient</em>' attribute.
   * @see #setNonTransient(String)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz356181_Main_NonTransient()
   * @model
   * @generated
   */
  String getNonTransient();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getNonTransient <em>Non Transient</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Non Transient</em>' attribute.
   * @see #getNonTransient()
   * @generated
   */
  void setNonTransient(String value);

  /**
   * Returns the value of the '<em><b>Transient Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transient Ref</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transient Ref</em>' reference.
   * @see #setTransientRef(Bz356181_Transient)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz356181_Main_TransientRef()
   * @model transient="true"
   * @generated
   */
  Bz356181_Transient getTransientRef();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientRef <em>Transient Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Transient Ref</em>' reference.
   * @see #getTransientRef()
   * @generated
   */
  void setTransientRef(Bz356181_Transient value);

  /**
   * Returns the value of the '<em><b>Transient Other Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transient Other Ref</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transient Other Ref</em>' reference.
   * @see #setTransientOtherRef(Bz356181_NonTransient)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz356181_Main_TransientOtherRef()
   * @model transient="true"
   * @generated
   */
  Bz356181_NonTransient getTransientOtherRef();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientOtherRef <em>Transient Other Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Transient Other Ref</em>' reference.
   * @see #getTransientOtherRef()
   * @generated
   */
  void setTransientOtherRef(Bz356181_NonTransient value);

} // Bz356181_Main
