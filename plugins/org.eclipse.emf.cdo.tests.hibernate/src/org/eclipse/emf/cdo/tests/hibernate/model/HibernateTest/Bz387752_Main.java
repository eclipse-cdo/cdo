/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
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
 * A representation of the model object '<em><b>Bz387752 Main</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrUnsettable <em>Str Unsettable</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrSettable <em>Str Settable</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumSettable <em>Enum Settable</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumUnsettable <em>Enum Unsettable</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz387752_Main()
 * @model
 * @generated
 */
public interface Bz387752_Main extends EObject
{
  /**
   * Returns the value of the '<em><b>Str Unsettable</b></em>' attribute.
   * The default value is <code>"def_value"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Str Unsettable</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Str Unsettable</em>' attribute.
   * @see #isSetStrUnsettable()
   * @see #unsetStrUnsettable()
   * @see #setStrUnsettable(String)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz387752_Main_StrUnsettable()
   * @model default="def_value" unsettable="true"
   * @generated
   */
  String getStrUnsettable();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrUnsettable <em>Str Unsettable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Str Unsettable</em>' attribute.
   * @see #isSetStrUnsettable()
   * @see #unsetStrUnsettable()
   * @see #getStrUnsettable()
   * @generated
   */
  void setStrUnsettable(String value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrUnsettable <em>Str Unsettable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetStrUnsettable()
   * @see #getStrUnsettable()
   * @see #setStrUnsettable(String)
   * @generated
   */
  void unsetStrUnsettable();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrUnsettable <em>Str Unsettable</em>}' attribute is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Str Unsettable</em>' attribute is set.
   * @see #unsetStrUnsettable()
   * @see #getStrUnsettable()
   * @see #setStrUnsettable(String)
   * @generated
   */
  boolean isSetStrUnsettable();

  /**
   * Returns the value of the '<em><b>Str Settable</b></em>' attribute.
   * The default value is <code>"value"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Str Settable</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Str Settable</em>' attribute.
   * @see #setStrSettable(String)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz387752_Main_StrSettable()
   * @model default="value"
   * @generated
   */
  String getStrSettable();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrSettable <em>Str Settable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Str Settable</em>' attribute.
   * @see #getStrSettable()
   * @generated
   */
  void setStrSettable(String value);

  /**
   * Returns the value of the '<em><b>Enum Settable</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Settable</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Settable</em>' attribute.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum
   * @see #setEnumSettable(Bz387752_Enum)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz387752_Main_EnumSettable()
   * @model
   * @generated
   */
  Bz387752_Enum getEnumSettable();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumSettable <em>Enum Settable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Enum Settable</em>' attribute.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum
   * @see #getEnumSettable()
   * @generated
   */
  void setEnumSettable(Bz387752_Enum value);

  /**
   * Returns the value of the '<em><b>Enum Unsettable</b></em>' attribute.
   * The default value is <code>"VAL1"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum Unsettable</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum Unsettable</em>' attribute.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum
   * @see #isSetEnumUnsettable()
   * @see #unsetEnumUnsettable()
   * @see #setEnumUnsettable(Bz387752_Enum)
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz387752_Main_EnumUnsettable()
   * @model default="VAL1" unsettable="true"
   * @generated
   */
  Bz387752_Enum getEnumUnsettable();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumUnsettable <em>Enum Unsettable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Enum Unsettable</em>' attribute.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum
   * @see #isSetEnumUnsettable()
   * @see #unsetEnumUnsettable()
   * @see #getEnumUnsettable()
   * @generated
   */
  void setEnumUnsettable(Bz387752_Enum value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumUnsettable <em>Enum Unsettable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetEnumUnsettable()
   * @see #getEnumUnsettable()
   * @see #setEnumUnsettable(Bz387752_Enum)
   * @generated
   */
  void unsetEnumUnsettable();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumUnsettable <em>Enum Unsettable</em>}' attribute is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Enum Unsettable</em>' attribute is set.
   * @see #unsetEnumUnsettable()
   * @see #getEnumUnsettable()
   * @see #setEnumUnsettable(Bz387752_Enum)
   * @generated
   */
  boolean isSetEnumUnsettable();

} // Bz387752_Main
