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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bz397682 C</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToP <em>Ref To P</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToC <em>Ref To C</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getDbId <em>Db Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz397682C()
 * @model
 * @generated
 */
public interface Bz397682C extends EObject
{
  /**
   * Returns the value of the '<em><b>Ref To P</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P#getListOfC <em>List Of C</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ref To P</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ref To P</em>' container reference.
   * @see #setRefToP(Bz397682P)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz397682C_RefToP()
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P#getListOfC
   * @model opposite="listOfC" transient="false"
   * @generated
   */
  Bz397682P getRefToP();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToP <em>Ref To P</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ref To P</em>' container reference.
   * @see #getRefToP()
   * @generated
   */
  void setRefToP(Bz397682P value);

  /**
   * Returns the value of the '<em><b>Ref To C</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ref To C</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ref To C</em>' reference.
   * @see #setRefToC(Bz397682C)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz397682C_RefToC()
   * @model
   * @generated
   */
  Bz397682C getRefToC();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToC <em>Ref To C</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ref To C</em>' reference.
   * @see #getRefToC()
   * @generated
   */
  void setRefToC(Bz397682C value);

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
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz397682C_DbId()
   * @model required="true"
   *        annotation="teneo.jpa value='@Id'"
   * @generated
   */
  String getDbId();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getDbId <em>Db Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Db Id</em>' attribute.
   * @see #getDbId()
   * @generated
   */
  void setDbId(String value);

} // Bz397682C
