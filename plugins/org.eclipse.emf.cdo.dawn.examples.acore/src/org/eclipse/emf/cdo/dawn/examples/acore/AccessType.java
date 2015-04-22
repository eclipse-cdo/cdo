/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Access Type</b></em>', and
 * utility methods for working with them. <!-- end-user-doc -->
 *
 * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAccessType()
 * @model extendedMetaData="name='AccessType'"
 * @generated
 */
public enum AccessType implements Enumerator
{
  /**
   * The '<em><b>PUBLIC</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #PUBLIC_VALUE
   * @generated
   * @ordered
   */
  PUBLIC(0, "PUBLIC", "public"),

  /**
   * The '<em><b>PRIVATE</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #PRIVATE_VALUE
   * @generated
   * @ordered
   */
  PRIVATE(1, "PRIVATE", "private"),

  /**
   * The '<em><b>PROECTED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #PROECTED_VALUE
   * @generated
   * @ordered
   */
  PROECTED(2, "PROECTED", "protected"),

  /**
   * The '<em><b>PACKAGE</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #PACKAGE_VALUE
   * @generated
   * @ordered
   */
  PACKAGE(3, "PACKAGE", "package");

  /**
   * The '<em><b>PUBLIC</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>PUBLIC</b></em>' literal object isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #PUBLIC
   * @model literal="public"
   * @generated
   * @ordered
   */
  public static final int PUBLIC_VALUE = 0;

  /**
   * The '<em><b>PRIVATE</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>PRIVATE</b></em>' literal object isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #PRIVATE
   * @model literal="private"
   * @generated
   * @ordered
   */
  public static final int PRIVATE_VALUE = 1;

  /**
   * The '<em><b>PROECTED</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>PROECTED</b></em>' literal object isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #PROECTED
   * @model literal="protected"
   * @generated
   * @ordered
   */
  public static final int PROECTED_VALUE = 2;

  /**
   * The '<em><b>PACKAGE</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>PACKAGE</b></em>' literal object isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #PACKAGE
   * @model literal="package"
   * @generated
   * @ordered
   */
  public static final int PACKAGE_VALUE = 3;

  /**
   * An array of all the '<em><b>Access Type</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static final AccessType[] VALUES_ARRAY = new AccessType[] { PUBLIC, PRIVATE, PROECTED, PACKAGE, };

  /**
   * A public read-only list of all the '<em><b>Access Type</b></em>' enumerators. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public static final List<AccessType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Access Type</b></em>' literal with the specified literal value. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public static AccessType get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      AccessType result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Access Type</b></em>' literal with the specified name. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public static AccessType getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      AccessType result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Access Type</b></em>' literal with the specified integer value. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public static AccessType get(int value)
  {
    switch (value)
    {
    case PUBLIC_VALUE:
      return PUBLIC;
    case PRIVATE_VALUE:
      return PRIVATE;
    case PROECTED_VALUE:
      return PROECTED;
    case PACKAGE_VALUE:
      return PACKAGE;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private AccessType(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // AccessType
