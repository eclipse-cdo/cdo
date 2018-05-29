/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
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
 * A representation of the model object '<em><b>Bz397682 P</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P#getDbId <em>Db Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P#getListOfC <em>List Of C</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz397682P()
 * @model annotation="teneo.jpa value='@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)'"
 * @generated
 */
public interface Bz397682P extends EObject
{
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
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz397682P_DbId()
   * @model required="true"
   *        annotation="teneo.jpa value='@Id'"
   * @generated
   */
  String getDbId();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P#getDbId <em>Db Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Db Id</em>' attribute.
   * @see #getDbId()
   * @generated
   */
  void setDbId(String value);

  /**
   * Returns the value of the '<em><b>List Of C</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToP <em>Ref To P</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>List Of C</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>List Of C</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz397682P_ListOfC()
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToP
   * @model opposite="refToP" containment="true"
   * @generated
   */
  EList<Bz397682C> getListOfC();

} // Bz397682P
