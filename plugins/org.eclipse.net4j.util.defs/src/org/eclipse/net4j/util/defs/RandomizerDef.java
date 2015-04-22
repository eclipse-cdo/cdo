/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.defs;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Randomizer Def</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.defs.RandomizerDef#getAlgorithmName <em>Algorithm Name</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.defs.RandomizerDef#getProviderName <em>Provider Name</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.defs.RandomizerDef#getSeed <em>Seed</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getRandomizerDef()
 * @model
 * @generated
 */
public interface RandomizerDef extends Def
{
  /**
   * Returns the value of the '<em><b>Algorithm Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Algorithm Name</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Algorithm Name</em>' attribute.
   * @see #isSetAlgorithmName()
   * @see #unsetAlgorithmName()
   * @see #setAlgorithmName(String)
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getRandomizerDef_AlgorithmName()
   * @model unsettable="true"
   * @generated
   */
  String getAlgorithmName();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.defs.RandomizerDef#getAlgorithmName <em>Algorithm Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Algorithm Name</em>' attribute.
   * @see #isSetAlgorithmName()
   * @see #unsetAlgorithmName()
   * @see #getAlgorithmName()
   * @generated
   */
  void setAlgorithmName(String value);

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.util.defs.RandomizerDef#getAlgorithmName <em>Algorithm Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetAlgorithmName()
   * @see #getAlgorithmName()
   * @see #setAlgorithmName(String)
   * @generated
   */
  void unsetAlgorithmName();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.util.defs.RandomizerDef#getAlgorithmName <em>Algorithm Name</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Algorithm Name</em>' attribute is set.
   * @see #unsetAlgorithmName()
   * @see #getAlgorithmName()
   * @see #setAlgorithmName(String)
   * @generated
   */
  boolean isSetAlgorithmName();

  /**
   * Returns the value of the '<em><b>Provider Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Provider Name</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Provider Name</em>' attribute.
   * @see #isSetProviderName()
   * @see #unsetProviderName()
   * @see #setProviderName(String)
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getRandomizerDef_ProviderName()
   * @model unsettable="true"
   * @generated
   */
  String getProviderName();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.defs.RandomizerDef#getProviderName <em>Provider Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Provider Name</em>' attribute.
   * @see #isSetProviderName()
   * @see #unsetProviderName()
   * @see #getProviderName()
   * @generated
   */
  void setProviderName(String value);

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.util.defs.RandomizerDef#getProviderName <em>Provider Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetProviderName()
   * @see #getProviderName()
   * @see #setProviderName(String)
   * @generated
   */
  void unsetProviderName();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.util.defs.RandomizerDef#getProviderName <em>Provider Name</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Provider Name</em>' attribute is set.
   * @see #unsetProviderName()
   * @see #getProviderName()
   * @see #setProviderName(String)
   * @generated
   */
  boolean isSetProviderName();

  /**
   * Returns the value of the '<em><b>Seed</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Seed</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Seed</em>' attribute.
   * @see #isSetSeed()
   * @see #unsetSeed()
   * @see #setSeed(byte[])
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getRandomizerDef_Seed()
   * @model unsettable="true"
   * @generated
   */
  byte[] getSeed();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.defs.RandomizerDef#getSeed <em>Seed</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Seed</em>' attribute.
   * @see #isSetSeed()
   * @see #unsetSeed()
   * @see #getSeed()
   * @generated
   */
  void setSeed(byte[] value);

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.util.defs.RandomizerDef#getSeed <em>Seed</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isSetSeed()
   * @see #getSeed()
   * @see #setSeed(byte[])
   * @generated
   */
  void unsetSeed();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.util.defs.RandomizerDef#getSeed <em>Seed</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Seed</em>' attribute is set.
   * @see #unsetSeed()
   * @see #getSeed()
   * @see #setSeed(byte[])
   * @generated
   */
  boolean isSetSeed();

} // RandomizerDef
