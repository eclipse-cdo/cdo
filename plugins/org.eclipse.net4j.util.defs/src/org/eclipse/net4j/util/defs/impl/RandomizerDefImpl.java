/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.defs.impl;

import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.defs.RandomizerDef;
import org.eclipse.net4j.util.security.Randomizer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Randomizer Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.defs.impl.RandomizerDefImpl#getAlgorithmName <em>Algorithm Name</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.defs.impl.RandomizerDefImpl#getProviderName <em>Provider Name</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.defs.impl.RandomizerDefImpl#getSeed <em>Seed</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RandomizerDefImpl extends DefImpl implements RandomizerDef
{
  /**
   * The default value of the '{@link #getAlgorithmName() <em>Algorithm Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAlgorithmName()
   * @generated
   * @ordered
   */
  protected static final String ALGORITHM_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAlgorithmName() <em>Algorithm Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAlgorithmName()
   * @generated
   * @ordered
   */
  protected String algorithmName = ALGORITHM_NAME_EDEFAULT;

  /**
   * This is true if the Algorithm Name attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean algorithmNameESet;

  /**
   * The default value of the '{@link #getProviderName() <em>Provider Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProviderName()
   * @generated
   * @ordered
   */
  protected static final String PROVIDER_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getProviderName() <em>Provider Name</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getProviderName()
   * @generated
   * @ordered
   */
  protected String providerName = PROVIDER_NAME_EDEFAULT;

  /**
   * This is true if the Provider Name attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean providerNameESet;

  /**
   * The default value of the '{@link #getSeed() <em>Seed</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getSeed()
   * @generated
   * @ordered
   */
  protected static final byte[] SEED_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getSeed() <em>Seed</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getSeed()
   * @generated
   * @ordered
   */
  protected byte[] seed = SEED_EDEFAULT;

  /**
   * This is true if the Seed attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean seedESet;

  @Override
  protected Object createInstance()
  {
    Randomizer randomizer = new Randomizer();
    if (isSetAlgorithmName())
    {
      randomizer.setAlgorithmName(getAlgorithmName());
    }
    if (isSetProviderName())
    {
      randomizer.setProviderName(getProviderName());
    }
    if (isSetSeed())
    {
      randomizer.setSeed(getSeed());
    }
    return randomizer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected RandomizerDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jUtilDefsPackage.Literals.RANDOMIZER_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String getAlgorithmName()
  {
    return algorithmName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setAlgorithmName(String newAlgorithmName)
  {
    String oldAlgorithmName = algorithmName;
    algorithmName = newAlgorithmName;
    boolean oldAlgorithmNameESet = algorithmNameESet;
    algorithmNameESet = true;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jUtilDefsPackage.RANDOMIZER_DEF__ALGORITHM_NAME,
          oldAlgorithmName, algorithmName, !oldAlgorithmNameESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void unsetAlgorithmName()
  {
    String oldAlgorithmName = algorithmName;
    boolean oldAlgorithmNameESet = algorithmNameESet;
    algorithmName = ALGORITHM_NAME_EDEFAULT;
    algorithmNameESet = false;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.UNSET, Net4jUtilDefsPackage.RANDOMIZER_DEF__ALGORITHM_NAME,
          oldAlgorithmName, ALGORITHM_NAME_EDEFAULT, oldAlgorithmNameESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetAlgorithmName()
  {
    return algorithmNameESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String getProviderName()
  {
    return providerName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setProviderName(String newProviderName)
  {
    String oldProviderName = providerName;
    providerName = newProviderName;
    boolean oldProviderNameESet = providerNameESet;
    providerNameESet = true;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jUtilDefsPackage.RANDOMIZER_DEF__PROVIDER_NAME,
          oldProviderName, providerName, !oldProviderNameESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void unsetProviderName()
  {
    String oldProviderName = providerName;
    boolean oldProviderNameESet = providerNameESet;
    providerName = PROVIDER_NAME_EDEFAULT;
    providerNameESet = false;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.UNSET, Net4jUtilDefsPackage.RANDOMIZER_DEF__PROVIDER_NAME,
          oldProviderName, PROVIDER_NAME_EDEFAULT, oldProviderNameESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetProviderName()
  {
    return providerNameESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public byte[] getSeed()
  {
    return seed;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setSeed(byte[] newSeed)
  {
    byte[] oldSeed = seed;
    seed = newSeed;
    boolean oldSeedESet = seedESet;
    seedESet = true;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jUtilDefsPackage.RANDOMIZER_DEF__SEED, oldSeed, seed,
          !oldSeedESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void unsetSeed()
  {
    byte[] oldSeed = seed;
    boolean oldSeedESet = seedESet;
    seed = SEED_EDEFAULT;
    seedESet = false;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.UNSET, Net4jUtilDefsPackage.RANDOMIZER_DEF__SEED, oldSeed,
          SEED_EDEFAULT, oldSeedESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetSeed()
  {
    return seedESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__ALGORITHM_NAME:
      return getAlgorithmName();
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__PROVIDER_NAME:
      return getProviderName();
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__SEED:
      return getSeed();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__ALGORITHM_NAME:
      setAlgorithmName((String)newValue);
      return;
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__PROVIDER_NAME:
      setProviderName((String)newValue);
      return;
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__SEED:
      setSeed((byte[])newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__ALGORITHM_NAME:
      unsetAlgorithmName();
      return;
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__PROVIDER_NAME:
      unsetProviderName();
      return;
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__SEED:
      unsetSeed();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__ALGORITHM_NAME:
      return isSetAlgorithmName();
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__PROVIDER_NAME:
      return isSetProviderName();
    case Net4jUtilDefsPackage.RANDOMIZER_DEF__SEED:
      return isSetSeed();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (algorithmName: ");
    if (algorithmNameESet)
      result.append(algorithmName);
    else
      result.append("<unset>");
    result.append(", providerName: ");
    if (providerNameESet)
      result.append(providerName);
    else
      result.append("<unset>");
    result.append(", seed: ");
    if (seedESet)
      result.append(seed);
    else
      result.append("<unset>");
    result.append(')');
    return result.toString();
  }

} // RandomizerDefImpl
