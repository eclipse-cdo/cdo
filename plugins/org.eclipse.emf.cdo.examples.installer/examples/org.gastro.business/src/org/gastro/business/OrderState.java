/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.business;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Order State</b></em>', and
 * utility methods for working with them. <!-- end-user-doc -->
 * @see org.gastro.business.BusinessPackage#getOrderState()
 * @model
 * @generated
 */
public enum OrderState implements Enumerator
{
  /**
   * The '<em><b>Ordered</b></em>' literal object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #ORDERED_VALUE
   * @generated
   * @ordered
   */
  ORDERED(0, "Ordered", "Ordered"),

  /**
   * The '<em><b>Acknowledged</b></em>' literal object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #ACKNOWLEDGED_VALUE
   * @generated
   * @ordered
   */
  ACKNOWLEDGED(2, "Acknowledged", "Acknowledged"),

  /**
   * The '<em><b>Prepared</b></em>' literal object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #PREPARED_VALUE
   * @generated
   * @ordered
   */
  PREPARED(1, "Prepared", "Prepared"),

  /**
   * The '<em><b>Served</b></em>' literal object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #SERVED_VALUE
   * @generated
   * @ordered
   */
  SERVED(3, "Served", "Served"),

  /**
   * The '<em><b>Paid</b></em>' literal object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #PAID_VALUE
   * @generated
   * @ordered
   */
  PAID(4, "Paid", "Paid");

  /**
   * The '<em><b>Ordered</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Ordered</b></em>' literal object isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #ORDERED
   * @model name="Ordered"
   * @generated
   * @ordered
   */
  public static final int ORDERED_VALUE = 0;

  /**
   * The '<em><b>Acknowledged</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Acknowledged</b></em>' literal object isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #ACKNOWLEDGED
   * @model name="Acknowledged"
   * @generated
   * @ordered
   */
  public static final int ACKNOWLEDGED_VALUE = 2;

  /**
   * The '<em><b>Prepared</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Prepared</b></em>' literal object isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #PREPARED
   * @model name="Prepared"
   * @generated
   * @ordered
   */
  public static final int PREPARED_VALUE = 1;

  /**
   * The '<em><b>Served</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Served</b></em>' literal object isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @see #SERVED
   * @model name="Served"
   * @generated
   * @ordered
   */
  public static final int SERVED_VALUE = 3;

  /**
   * The '<em><b>Paid</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Paid</b></em>' literal object isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @see #PAID
   * @model name="Paid"
   * @generated
   * @ordered
   */
  public static final int PAID_VALUE = 4;

  /**
   * An array of all the '<em><b>Order State</b></em>' enumerators.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static final OrderState[] VALUES_ARRAY = new OrderState[] { ORDERED, ACKNOWLEDGED, PREPARED, SERVED, PAID, };

  /**
   * A public read-only list of all the '<em><b>Order State</b></em>' enumerators.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public static final List<OrderState> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Order State</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public static OrderState get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      OrderState result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Order State</b></em>' literal with the specified name.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public static OrderState getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      OrderState result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Order State</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public static OrderState get(int value)
  {
    switch (value)
    {
    case ORDERED_VALUE:
      return ORDERED;
    case ACKNOWLEDGED_VALUE:
      return ACKNOWLEDGED;
    case PREPARED_VALUE:
      return PREPARED;
    case SERVED_VALUE:
      return SERVED;
    case PAID_VALUE:
      return PAID;
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private OrderState(int value, String name, String literal)
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
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // OrderState
