/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Component Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getComponentType()
 * @model
 * @generated
 */
public enum ComponentType implements Enumerator
{
  /**
   * The '<em><b>ECLIPSE FEATURE</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #ECLIPSE_FEATURE_VALUE
   * @generated
   * @ordered
   */
  ECLIPSE_FEATURE(0, "ECLIPSE_FEATURE", "eclipse.feature"),

  /**
   * The '<em><b>OSGI BUNDLE</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #OSGI_BUNDLE_VALUE
   * @generated
   * @ordered
   */
  OSGI_BUNDLE(1, "OSGI_BUNDLE", "osgi.bundle"),

  /**
   * The '<em><b>BUCKMINSTER</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #BUCKMINSTER_VALUE
   * @generated
   * @ordered
   */
  BUCKMINSTER(3, "BUCKMINSTER", "buckminster");

  /**
   * The '<em><b>ECLIPSE FEATURE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>ECLIPSE FEATURE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #ECLIPSE_FEATURE
   * @model literal="eclipse.feature"
   * @generated
   * @ordered
   */
  public static final int ECLIPSE_FEATURE_VALUE = 0;

  /**
   * The '<em><b>OSGI BUNDLE</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>OSGI BUNDLE</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #OSGI_BUNDLE
   * @model literal="osgi.bundle"
   * @generated
   * @ordered
   */
  public static final int OSGI_BUNDLE_VALUE = 1;

  /**
   * The '<em><b>BUCKMINSTER</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>BUCKMINSTER</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #BUCKMINSTER
   * @model literal="buckminster"
   * @generated
   * @ordered
   */
  public static final int BUCKMINSTER_VALUE = 3;

  /**
   * An array of all the '<em><b>Component Type</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final ComponentType[] VALUES_ARRAY = new ComponentType[] { ECLIPSE_FEATURE, OSGI_BUNDLE, BUCKMINSTER, };

  /**
   * A public read-only list of all the '<em><b>Component Type</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<ComponentType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  public static final String ALL;

  static
  {
    StringBuilder all = new StringBuilder();
    for (ComponentType componentType : VALUES)
    {
      if (all.length() != 0)
      {
        all.append(',');
      }
      all.append(componentType.literal);
    }
    ALL = all.toString();
  }

  /**
   * Returns the '<em><b>Component Type</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static ComponentType get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      ComponentType result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Component Type</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static ComponentType getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      ComponentType result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Component Type</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static ComponentType get(int value)
  {
    switch (value)
    {
    case ECLIPSE_FEATURE_VALUE:
      return ECLIPSE_FEATURE;
    case OSGI_BUNDLE_VALUE:
      return OSGI_BUNDLE;
    case BUCKMINSTER_VALUE:
      return BUCKMINSTER;
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
  private ComponentType(int value, String name, String literal)
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

} // ComponentType
