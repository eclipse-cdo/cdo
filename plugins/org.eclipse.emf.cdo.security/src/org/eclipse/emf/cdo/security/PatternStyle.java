/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>PatternStyle</b></em>',
 * and utility methods for working with them.
 * @since 4.3
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getPatternStyle()
 * @model
 * @generated
 */
public enum PatternStyle implements Enumerator
{
  /**
   * The '<em><b>EXACT</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #EXACT_VALUE
   * @generated
   * @ordered
   */
  EXACT(0, "EXACT", "EXACT"),
  /**
   * The '<em><b>TREE</b></em>' literal object.
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @see #TREE_VALUE
   * @generated
   * @ordered
   */
  TREE(1, "TREE", "TREE"),
  /**
   * The '<em><b>ANT</b></em>' literal object.
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @see #ANT_VALUE
   * @generated
   * @ordered
   */
  ANT(2, "ANT", "ANT"),
  /**
   * The '<em><b>REGEX</b></em>' literal object.
   * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
   * @see #REGEX_VALUE
   * @generated
   * @ordered
   */
  REGEX(3, "REGEX", "REGEX"); //$NON-NLS-1$ //$NON-NLS-2$

  /**
   * The '<em><b>EXACT</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>EXACT</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #EXACT
   * @model
   * @generated
   * @ordered
   */
  public static final int EXACT_VALUE = 0;

  /**
   * The '<em><b>TREE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>TREE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #TREE
   * @model
   * @generated
   * @ordered
   */
  public static final int TREE_VALUE = 1;

  /**
   * The '<em><b>ANT</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>ANT</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #ANT
   * @model
   * @generated
   * @ordered
   */
  public static final int ANT_VALUE = 2;

  /**
   * The '<em><b>REGEX</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Regex</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #REGEX
   * @model
   * @generated
   * @ordered
   */
  public static final int REGEX_VALUE = 3;

  /**
   * An array of all the '<em><b>Pattern Style</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final PatternStyle[] VALUES_ARRAY = new PatternStyle[] { EXACT, TREE, ANT, REGEX, };

  /**
   * A public read-only list of all the '<em><b>Pattern Style</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<PatternStyle> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Pattern Style</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static PatternStyle get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      PatternStyle result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Pattern Style</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param name the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static PatternStyle getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      PatternStyle result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Pattern Style</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static PatternStyle get(int value)
  {
    switch (value)
    {
    case EXACT_VALUE:
      return EXACT;
    case TREE_VALUE:
      return TREE;
    case ANT_VALUE:
      return ANT;
    case REGEX_VALUE:
      return REGEX;
    }
    return null;
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
  private PatternStyle(int value, String name, String literal)
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

} // PatternStyle
