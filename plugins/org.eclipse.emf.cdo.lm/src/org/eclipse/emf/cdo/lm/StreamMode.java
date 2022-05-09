/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration
 * '<em><b>Stream Mode</b></em>', and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.LMPackage#getStreamMode()
 * @model
 * @generated
 */
public enum StreamMode implements Enumerator
{
  /**
   * The '<em><b>Development</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #DEVELOPMENT_VALUE
   * @generated
   * @ordered
   */
  DEVELOPMENT(0, "Development", "Development"),

  /**
   * The '<em><b>Maintenance</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #MAINTENANCE_VALUE
   * @generated
   * @ordered
   */
  MAINTENANCE(1, "Maintenance", "Maintenance"),

  /**
   * The '<em><b>Closed</b></em>' literal object.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #CLOSED_VALUE
   * @generated
   * @ordered
   */
  CLOSED(2, "Closed", "Closed");

  /**
   * The '<em><b>Development</b></em>' literal value.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #DEVELOPMENT
   * @model name="Development"
   * @generated
   * @ordered
   */
  public static final int DEVELOPMENT_VALUE = 0;

  /**
   * The '<em><b>Maintenance</b></em>' literal value.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #MAINTENANCE
   * @model name="Maintenance"
   * @generated
   * @ordered
   */
  public static final int MAINTENANCE_VALUE = 1;

  /**
   * The '<em><b>Closed</b></em>' literal value.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #CLOSED
   * @model name="Closed"
   * @generated
   * @ordered
   */
  public static final int CLOSED_VALUE = 2;

  /**
   * An array of all the '<em><b>Stream Mode</b></em>' enumerators. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static final StreamMode[] VALUES_ARRAY = new StreamMode[] { DEVELOPMENT, MAINTENANCE, CLOSED, };

  /**
   * A public read-only list of all the '<em><b>Stream Mode</b></em>' enumerators.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public static final List<StreamMode> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Stream Mode</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param literal the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static StreamMode get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      StreamMode result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Stream Mode</b></em>' literal with the specified name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param name the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static StreamMode getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      StreamMode result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Stream Mode</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static StreamMode get(int value)
  {
    switch (value)
    {
    case DEVELOPMENT_VALUE:
      return DEVELOPMENT;
    case MAINTENANCE_VALUE:
      return MAINTENANCE;
    case CLOSED_VALUE:
      return CLOSED;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  private StreamMode(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // StreamMode
