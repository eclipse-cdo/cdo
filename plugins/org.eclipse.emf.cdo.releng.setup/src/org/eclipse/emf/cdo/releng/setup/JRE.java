/**
 */
package org.eclipse.emf.cdo.releng.setup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>JRE</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getJRE()
 * @model
 * @generated
 */
public enum JRE implements Enumerator
{
  /**
   * The '<em><b>JRE 13</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #JRE_13_VALUE
   * @generated
   * @ordered
   */
  JRE_13(0, "JRE_13", "JRE_13"),

  /**
   * The '<em><b>JRE 14</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #JRE_14_VALUE
   * @generated
   * @ordered
   */
  JRE_14(1, "JRE_14", "JRE_14"),

  /**
   * The '<em><b>JRE 15</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #JRE_15_VALUE
   * @generated
   * @ordered
   */
  JRE_15(2, "JRE_15", "JRE_15"),

  /**
   * The '<em><b>JRE 16</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #JRE_16_VALUE
   * @generated
   * @ordered
   */
  JRE_16(3, "JRE_16", "JRE_16"),

  /**
   * The '<em><b>JRE 17</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #JRE_17_VALUE
   * @generated
   * @ordered
   */
  JRE_17(4, "JRE_17", "JRE_17"),

  /**
   * The '<em><b>JRE 18</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #JRE_18_VALUE
   * @generated
   * @ordered
   */
  JRE_18(5, "JRE_18", "JRE_18");

  /**
   * The '<em><b>JRE 13</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>JRE 13</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #JRE_13
   * @model
   * @generated
   * @ordered
   */
  public static final int JRE_13_VALUE = 0;

  /**
   * The '<em><b>JRE 14</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>JRE 14</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #JRE_14
   * @model
   * @generated
   * @ordered
   */
  public static final int JRE_14_VALUE = 1;

  /**
   * The '<em><b>JRE 15</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>JRE 15</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #JRE_15
   * @model
   * @generated
   * @ordered
   */
  public static final int JRE_15_VALUE = 2;

  /**
   * The '<em><b>JRE 16</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>JRE 16</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #JRE_16
   * @model
   * @generated
   * @ordered
   */
  public static final int JRE_16_VALUE = 3;

  /**
   * The '<em><b>JRE 17</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>JRE 17</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #JRE_17
   * @model
   * @generated
   * @ordered
   */
  public static final int JRE_17_VALUE = 4;

  /**
   * The '<em><b>JRE 18</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>JRE 18</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #JRE_18
   * @model
   * @generated
   * @ordered
   */
  public static final int JRE_18_VALUE = 5;

  /**
   * An array of all the '<em><b>JRE</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final JRE[] VALUES_ARRAY = new JRE[] { JRE_13, JRE_14, JRE_15, JRE_16, JRE_17, JRE_18, };

  /**
   * A public read-only list of all the '<em><b>JRE</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<JRE> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>JRE</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static JRE get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      JRE result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>JRE</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static JRE getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      JRE result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>JRE</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static JRE get(int value)
  {
    switch (value)
    {
    case JRE_13_VALUE:
      return JRE_13;
    case JRE_14_VALUE:
      return JRE_14;
    case JRE_15_VALUE:
      return JRE_15;
    case JRE_16_VALUE:
      return JRE_16;
    case JRE_17_VALUE:
      return JRE_17;
    case JRE_18_VALUE:
      return JRE_18;
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
  private JRE(int value, String name, String literal)
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

} //JRE
