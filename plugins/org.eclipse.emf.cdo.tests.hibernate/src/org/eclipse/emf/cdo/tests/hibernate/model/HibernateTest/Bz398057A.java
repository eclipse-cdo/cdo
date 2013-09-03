/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bz398057 A</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A#getListOfB <em>List Of B</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A#getDbId <em>Db Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz398057A()
 * @model annotation="teneo.jpa value='@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)'"
 * @generated
 */
public interface Bz398057A extends EObject
{
  /**
   * Returns the value of the '<em><b>List Of B</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B#getRefToClassA <em>Ref To Class A</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>List Of B</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>List Of B</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz398057A_ListOfB()
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B#getRefToClassA
   * @model opposite="refToClassA" containment="true"
   * @generated
   */
  EList<Bz398057B> getListOfB();

  /**
   * Returns the value of the '<em><b>Db Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Db Id</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Db Id</em>' attribute.
   * @see #setDbId(String)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz398057A_DbId()
   * @model required="true"
   *        annotation="teneo.jpa value='@Id'"
   * @generated
   */
  String getDbId();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A#getDbId <em>Db Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Db Id</em>' attribute.
   * @see #getDbId()
   * @generated
   */
  void setDbId(String value);

} // Bz398057A
