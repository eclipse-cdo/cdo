/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Review Status</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReviewStatus()
 * @model
 * @generated
 */
public enum ReviewStatus implements Enumerator
{
  /**
   * The '<em><b>New</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #NEW_VALUE
   * @generated
   * @ordered
   */
  NEW(0, "New", "New"),
  /**
   * The '<em><b>Source Outdated</b></em>' literal object.
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @see #SOURCE_OUTDATED_VALUE
   * @generated
   * @ordered
   */
  SOURCE_OUTDATED(1, "SourceOutdated", "SourceOutdated"),
  /**
   * The '<em><b>Target Outdated</b></em>' literal object.
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @see #TARGET_OUTDATED_VALUE
   * @generated
   * @ordered
   */
  TARGET_OUTDATED(2, "TargetOutdated", "TargetOutdated"),
  /**
   * The '<em><b>Outdated</b></em>' literal object.
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @see #OUTDATED_VALUE
   * @generated
   * @ordered
   */
  OUTDATED(3, "Outdated", "Outdated"),
  /**
   * The '<em><b>Submitted</b></em>' literal object.
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @see #SUBMITTED_VALUE
   * @generated
   * @ordered
   */
  SUBMITTED(4, "Submitted", "Submitted"),
  /**
   * The '<em><b>Abandoned</b></em>' literal object.
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @see #ABANDONED_VALUE
   * @generated
   * @ordered
   */
  ABANDONED(5, "Abandoned", "Abandoned"),
  /**
   * The '<em><b>Restoring</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #RESTORING_VALUE
   * @generated
   * @ordered
   */
  RESTORING(6, "Restoring", "Restoring"),
  /**
   * The '<em><b>Deleted</b></em>' literal object.
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @see #DELETED_VALUE
   * @generated
   * @ordered
   */
  DELETED(7, "Deleted", "Deleted");

  /**
   * The '<em><b>New</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #NEW
   * @model name="New"
   * @generated
   * @ordered
   */
  public static final int NEW_VALUE = 0;

  /**
   * The '<em><b>Source Outdated</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #SOURCE_OUTDATED
   * @model name="SourceOutdated"
   * @generated
   * @ordered
   */
  public static final int SOURCE_OUTDATED_VALUE = 1;

  /**
   * The '<em><b>Target Outdated</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TARGET_OUTDATED
   * @model name="TargetOutdated"
   * @generated
   * @ordered
   */
  public static final int TARGET_OUTDATED_VALUE = 2;

  /**
   * The '<em><b>Outdated</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #OUTDATED
   * @model name="Outdated"
   * @generated
   * @ordered
   */
  public static final int OUTDATED_VALUE = 3;

  /**
   * The '<em><b>Submitted</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #SUBMITTED
   * @model name="Submitted"
   * @generated
   * @ordered
   */
  public static final int SUBMITTED_VALUE = 4;

  /**
   * The '<em><b>Abandoned</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #ABANDONED
   * @model name="Abandoned"
   * @generated
   * @ordered
   */
  public static final int ABANDONED_VALUE = 5;

  /**
   * The '<em><b>Restoring</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #RESTORING
   * @model name="Restoring"
   * @generated
   * @ordered
   */
  public static final int RESTORING_VALUE = 6;

  /**
   * The '<em><b>Deleted</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #DELETED
   * @model name="Deleted"
   * @generated
   * @ordered
   */
  public static final int DELETED_VALUE = 7;

  /**
   * An array of all the '<em><b>Review Status</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final ReviewStatus[] VALUES_ARRAY = new ReviewStatus[] { NEW, SOURCE_OUTDATED, TARGET_OUTDATED, OUTDATED, SUBMITTED, ABANDONED, RESTORING,
      DELETED, };

  /**
   * A public read-only list of all the '<em><b>Review Status</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<ReviewStatus> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Review Status</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static ReviewStatus get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      ReviewStatus result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Review Status</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param name the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static ReviewStatus getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      ReviewStatus result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Review Status</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static ReviewStatus get(int value)
  {
    switch (value)
    {
    case NEW_VALUE:
      return NEW;
    case SOURCE_OUTDATED_VALUE:
      return SOURCE_OUTDATED;
    case TARGET_OUTDATED_VALUE:
      return TARGET_OUTDATED;
    case OUTDATED_VALUE:
      return OUTDATED;
    case SUBMITTED_VALUE:
      return SUBMITTED;
    case ABANDONED_VALUE:
      return ABANDONED;
    case RESTORING_VALUE:
      return RESTORING;
    case DELETED_VALUE:
      return DELETED;
    }
    return null;
  }

  public static ReviewStatus getOutdated(boolean sourceOutdated, boolean targetOutdated)
  {
    if (sourceOutdated)
    {
      if (targetOutdated)
      {
        return OUTDATED;
      }

      return SOURCE_OUTDATED;
    }

    if (targetOutdated)
    {
      return TARGET_OUTDATED;
    }

    return NEW;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private ReviewStatus(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

  public boolean isOpen()
  {
    return value < SUBMITTED_VALUE;
  }

  public boolean isClosed()
  {
    return !isOpen();
  }

} // ReviewStatus
