/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Task Scope</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetupTaskScope()
 * @model
 * @generated
 */
public enum SetupTaskScope implements Enumerator
{
  /**
   * The '<em><b>None</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #NONE_VALUE
   * @generated
   * @ordered
   */
  NONE(0, "None", "None"),

  /**
   * The '<em><b>Eclipse</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #ECLIPSE_VALUE
   * @generated
   * @ordered
   */
  ECLIPSE(2, "Eclipse", "Eclipse"), /**
                                    * The '<em><b>Project</b></em>' literal object.
                                    * <!-- begin-user-doc -->
                                    * <!-- end-user-doc -->
                                    * @see #PROJECT_VALUE
                                    * @generated
                                    * @ordered
                                    */
  PROJECT(3, "Project", "Project"),

  /**
   * The '<em><b>Branch</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #BRANCH_VALUE
   * @generated
   * @ordered
   */
  BRANCH(4, "Branch", "Branch"),

  /**
   * The '<em><b>User</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #USER_VALUE
   * @generated
   * @ordered
   */
  USER(5, "User", "User"), /**
                           * The '<em><b>Configuration</b></em>' literal object.
                           * <!-- begin-user-doc -->
                           * <!-- end-user-doc -->
                           * @see #CONFIGURATION_VALUE
                           * @generated
                           * @ordered
                           */
  CONFIGURATION(1, "Configuration", "Configuration");

  /**
   * The '<em><b>None</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>None</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #NONE
   * @model name="None"
   * @generated
   * @ordered
   */
  public static final int NONE_VALUE = 0;

  /**
   * The '<em><b>Eclipse</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Eclipse</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #ECLIPSE
   * @model name="Eclipse"
   * @generated
   * @ordered
   */
  public static final int ECLIPSE_VALUE = 2;

  /**
   * The '<em><b>Project</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Project</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #PROJECT
   * @model name="Project"
   * @generated
   * @ordered
   */
  public static final int PROJECT_VALUE = 3;

  /**
   * The '<em><b>Branch</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Branch</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #BRANCH
   * @model name="Branch"
   * @generated
   * @ordered
   */
  public static final int BRANCH_VALUE = 4;

  /**
   * The '<em><b>User</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>User</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #USER
   * @model name="User"
   * @generated
   * @ordered
   */
  public static final int USER_VALUE = 5;

  /**
   * The '<em><b>Configuration</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Configuration</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #CONFIGURATION
   * @model name="Configuration"
   * @generated
   * @ordered
   */
  public static final int CONFIGURATION_VALUE = 1;

  /**
   * An array of all the '<em><b>Task Scope</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final SetupTaskScope[] VALUES_ARRAY = new SetupTaskScope[] { NONE, ECLIPSE, PROJECT, BRANCH, USER,
      CONFIGURATION, };

  /**
   * A public read-only list of all the '<em><b>Task Scope</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<SetupTaskScope> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Task Scope</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static SetupTaskScope get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      SetupTaskScope result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Task Scope</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static SetupTaskScope getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      SetupTaskScope result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Task Scope</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static SetupTaskScope get(int value)
  {
    switch (value)
    {
    case NONE_VALUE:
      return NONE;
    case ECLIPSE_VALUE:
      return ECLIPSE;
    case PROJECT_VALUE:
      return PROJECT;
    case BRANCH_VALUE:
      return BRANCH;
    case USER_VALUE:
      return USER;
    case CONFIGURATION_VALUE:
      return CONFIGURATION;
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
  private SetupTaskScope(int value, String name, String literal)
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
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
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

} // SetupTaskScope
