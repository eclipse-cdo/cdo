/**
 * <copyright>
 * </copyright>
 *
 * $Id: ChallengeNegotiatorDefImpl.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.impl;

import org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef;
import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.net4jdefs.RandomizerDef;
import org.eclipse.net4j.net4jdefs.UserManagerDef;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.security.ChallengeNegotiator;
import org.eclipse.net4j.util.security.IRandomizer;
import org.eclipse.net4j.util.security.IUserManager;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Challenge Negotiator Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.ChallengeNegotiatorDefImpl#getUserManager <em>User Manager</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.ChallengeNegotiatorDefImpl#getRandomizer <em>Randomizer</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ChallengeNegotiatorDefImpl extends NegotiatorDefImpl implements ChallengeNegotiatorDef
{

  /**
   * The cached value of the '{@link #getUserManager() <em>User Manager</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getUserManager()
   * @generated
   * @ordered
   */
  protected UserManagerDef userManager;

  /**
   * The cached value of the '{@link #getRandomizer() <em>Randomizer</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getRandomizer()
   * @generated
   * @ordered
   */
  protected RandomizerDef randomizer;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ChallengeNegotiatorDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jDefsPackage.Literals.CHALLENGE_NEGOTIATOR_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public UserManagerDef getUserManager()
  {
    if (userManager != null && userManager.eIsProxy())
    {
      InternalEObject oldUserManager = (InternalEObject)userManager;
      userManager = (UserManagerDef)eResolveProxy(oldUserManager);
      if (userManager != oldUserManager)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER, oldUserManager, userManager));
        }
      }
    }
    return userManager;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public UserManagerDef basicGetUserManager()
  {
    return userManager;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setUserManager(UserManagerDef newUserManager)
  {
    UserManagerDef oldUserManager = userManager;
    userManager = newUserManager;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER,
          oldUserManager, userManager));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public RandomizerDef getRandomizer()
  {
    if (randomizer != null && randomizer.eIsProxy())
    {
      InternalEObject oldRandomizer = (InternalEObject)randomizer;
      randomizer = (RandomizerDef)eResolveProxy(oldRandomizer);
      if (randomizer != oldRandomizer)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER, oldRandomizer, randomizer));
        }
      }
    }
    return randomizer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public RandomizerDef basicGetRandomizer()
  {
    return randomizer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setRandomizer(RandomizerDef newRandomizer)
  {
    RandomizerDef oldRandomizer = randomizer;
    randomizer = newRandomizer;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER,
          oldRandomizer, randomizer));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER:
      if (resolve)
      {
        return getUserManager();
      }
      return basicGetUserManager();
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER:
      if (resolve)
      {
        return getRandomizer();
      }
      return basicGetRandomizer();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER:
      setUserManager((UserManagerDef)newValue);
      return;
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER:
      setRandomizer((RandomizerDef)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER:
      setUserManager((UserManagerDef)null);
      return;
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER:
      setRandomizer((RandomizerDef)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER:
      return userManager != null;
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER:
      return randomizer != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * @ADDED
   */
  @Override
  protected Object createInstance()
  {
    ChallengeNegotiator challengeNegotiator = new ChallengeNegotiator();
    challengeNegotiator.setRandomizer((IRandomizer)getRandomizer().getInstance());
    challengeNegotiator.setUserManager((IUserManager)getUserManager().getInstance());

    return challengeNegotiator;
  }

  /**
   * @ADDED
   */
  @Override
  protected void validateDefinition()
  {
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER), "randomizer is not set!");
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER), "user manager is not set!");
  }

} // ChallengeNegotiatorDefImpl
