/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Resource Mode</b></em>', and
 * utility methods for working with them. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getResourceMode()
 * @model
 * @generated
 */
public enum ResourceMode implements Enumerator
{
  /**
   * The '<em><b>GET</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #GET_VALUE
   * @generated NOT
   * @ordered
   */
  GET(0, "GET", "GET")
  {
    @Override
    public CDOResource getResource(String path, CDOTransaction transaction)
    {
      return transaction.getResource(path);
      // return transaction.getResource(path, false);
    }
  },

  /**
   * The '<em><b>CREATE</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #CREATE_VALUE
   * @generated NOT
   * @ordered
   */
  CREATE(0, "CREATE", "CREATE")
  {
    @Override
    public CDOResource getResource(String path, CDOTransaction transaction)
    {
      return transaction.createResource(path);
    }
  },

  /**
   * The '<em><b>GET OR CREATE</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #GET_OR_CREATE_VALUE
   * @generated NOT
   * @ordered
   */
  GET_OR_CREATE(0, "GET_OR_CREATE", "GET_OR_CREATE")
  {
    @Override
    public CDOResource getResource(String path, CDOTransaction transaction)
    {
      return transaction.getOrCreateResource(path);
    }
  };

  /**
   * The '<em><b>GET</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>GET</b></em>' literal object isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #GET
   * @model
   * @generated
   * @ordered
   */
  public static final int GET_VALUE = 0;

  /**
   * The '<em><b>CREATE</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>CREATE</b></em>' literal object isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #CREATE
   * @model
   * @generated
   * @ordered
   */
  public static final int CREATE_VALUE = 0;

  /**
   * The '<em><b>GET OR CREATE</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>GET OR CREATE</b></em>' literal object isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #GET_OR_CREATE
   * @model
   * @generated
   * @ordered
   */
  public static final int GET_OR_CREATE_VALUE = 0;

  /**
   * An array of all the '<em><b>Resource Mode</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static final ResourceMode[] VALUES_ARRAY = new ResourceMode[] { GET, CREATE, GET_OR_CREATE, };

  /**
   * A public read-only list of all the '<em><b>Resource Mode</b></em>' enumerators. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  public static final List<ResourceMode> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Resource Mode</b></em>' literal with the specified literal value. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  public static ResourceMode get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      ResourceMode result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Resource Mode</b></em>' literal with the specified name. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  public static ResourceMode getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      ResourceMode result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Resource Mode</b></em>' literal with the specified integer value. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  public static ResourceMode get(int value)
  {
    switch (value)
    {
    case GET_VALUE:
      return GET;
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
  private ResourceMode(int value, String name, String literal)
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

  public CDOResource getResource(String path, CDOTransaction cdoTransaction)
  {
    throw new UnsupportedOperationException("use a subclass!");
  }

} // ResourceMode
