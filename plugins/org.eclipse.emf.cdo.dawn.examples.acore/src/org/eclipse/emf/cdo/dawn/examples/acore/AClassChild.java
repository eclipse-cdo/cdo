/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>AClass Child</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getAccessright <em>Accessright</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getDataType <em>Data Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClassChild()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface AClassChild extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. The default value is <code>""</code>. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClassChild_Name()
   * @model default=""
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getName <em>Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Accessright</b></em>' attribute. The default value is <code>"public"</code>. The
   * literals are from the enumeration {@link org.eclipse.emf.cdo.dawn.examples.acore.AccessType}. <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Accessright</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Accessright</em>' attribute.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AccessType
   * @see #isSetAccessright()
   * @see #unsetAccessright()
   * @see #setAccessright(AccessType)
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClassChild_Accessright()
   * @model default="public" unique="false" unsettable="true"
   * @generated
   */
  AccessType getAccessright();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getAccessright
   * <em>Accessright</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Accessright</em>' attribute.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AccessType
   * @see #isSetAccessright()
   * @see #unsetAccessright()
   * @see #getAccessright()
   * @generated
   */
  void setAccessright(AccessType value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getAccessright
   * <em>Accessright</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isSetAccessright()
   * @see #getAccessright()
   * @see #setAccessright(AccessType)
   * @generated
   */
  void unsetAccessright();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getAccessright
   * <em>Accessright</em>}' attribute is set. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return whether the value of the '<em>Accessright</em>' attribute is set.
   * @see #unsetAccessright()
   * @see #getAccessright()
   * @see #setAccessright(AccessType)
   * @generated
   */
  boolean isSetAccessright();

  /**
   * Returns the value of the '<em><b>Data Type</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data Type</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Data Type</em>' attribute.
   * @see #setDataType(String)
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClassChild_DataType()
   * @model
   * @generated
   */
  String getDataType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getDataType <em>Data Type</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Data Type</em>' attribute.
   * @see #getDataType()
   * @generated
   */
  void setDataType(String value);

} // AClassChild
