/**
 * <copyright>
 * </copyright>
 *
 * $Id: PasswordCredentialsProviderDefImpl.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.impl;

import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.net4jdefs.PasswordCredentialsProviderDef;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Password Credentials Provider Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.PasswordCredentialsProviderDefImpl#getPassword <em>Password</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class PasswordCredentialsProviderDefImpl extends CredentialsProviderDefImpl implements
    PasswordCredentialsProviderDef
{

  /**
   * The default value of the '{@link #getPassword() <em>Password</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getPassword()
   * @generated
   * @ordered
   */
  protected static final String PASSWORD_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getPassword() <em>Password</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getPassword()
   * @generated
   * @ordered
   */
  protected String password = PASSWORD_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected PasswordCredentialsProviderDefImpl()
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
    return Net4jDefsPackage.Literals.PASSWORD_CREDENTIALS_PROVIDER_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setPassword(String newPassword)
  {
    String oldPassword = password;
    password = newPassword;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          Net4jDefsPackage.PASSWORD_CREDENTIALS_PROVIDER_DEF__PASSWORD, oldPassword, password));
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
    case Net4jDefsPackage.PASSWORD_CREDENTIALS_PROVIDER_DEF__PASSWORD:
      return getPassword();
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
    case Net4jDefsPackage.PASSWORD_CREDENTIALS_PROVIDER_DEF__PASSWORD:
      setPassword((String)newValue);
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
    case Net4jDefsPackage.PASSWORD_CREDENTIALS_PROVIDER_DEF__PASSWORD:
      setPassword(PASSWORD_EDEFAULT);
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
    case Net4jDefsPackage.PASSWORD_CREDENTIALS_PROVIDER_DEF__PASSWORD:
      return PASSWORD_EDEFAULT == null ? password != null : !PASSWORD_EDEFAULT.equals(password);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (password: ");
    result.append(password);
    result.append(')');
    return result.toString();
  }

  /**
   * @ADDED
   */
  @Override
  protected Object createInstance()
  {
    IPasswordCredentials passwordCredential = new PasswordCredentials(getUserID(), getPassword().toCharArray());
    PasswordCredentialsProvider passwordCredentialsProvider = new PasswordCredentialsProvider(passwordCredential);
    return passwordCredentialsProvider;
  }

  /**
   * @ADDED
   */
  @Override
  protected void validateDefinition()
  {
    CheckUtil.checkState(getUserID() != null && getUserID().length() > 0, "userID is not set!");
    CheckUtil.checkState(getPassword() != null && getPassword().length() > 0, "password is not set!");
    super.validateDefinition();
  }

} // PasswordCredentialsProviderDefImpl
