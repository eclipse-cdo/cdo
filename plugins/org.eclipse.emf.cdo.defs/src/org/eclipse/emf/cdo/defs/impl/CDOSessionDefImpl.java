/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    André Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs.impl;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.defs.CDODefsPackage;
import org.eclipse.emf.cdo.defs.CDOPackageRegistryDef;
import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.defs.FailOverStrategyDef;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.internal.cdo.session.CDOSessionConfigurationImpl;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.defs.ConnectorDef;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.defs.impl.DefImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>CDO Session Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.defs.impl.CDOSessionDefImpl#getConnectorDef <em>Connector Def</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.defs.impl.CDOSessionDefImpl#getRepositoryName <em>Repository Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.defs.impl.CDOSessionDefImpl#getCdoPackageRegistryDef <em>Cdo Package Registry Def
 * </em>}</li>
 * <li>{@link org.eclipse.emf.cdo.defs.impl.CDOSessionDefImpl#isLegacySupportEnabled <em>Legacy Support Enabled</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.defs.impl.CDOSessionDefImpl#getFailOverStrategyDef <em>Fail Over Strategy Def</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CDOSessionDefImpl extends DefImpl implements CDOSessionDef
{
  /**
   * The cached value of the '{@link #getConnectorDef() <em>Connector Def</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getConnectorDef()
   * @generated
   * @ordered
   */
  protected ConnectorDef connectorDef;

  /**
   * This is true if the Connector Def reference has been set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  protected boolean connectorDefESet;

  /**
   * The default value of the '{@link #getRepositoryName() <em>Repository Name</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getRepositoryName()
   * @generated
   * @ordered
   */
  protected static final String REPOSITORY_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRepositoryName() <em>Repository Name</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getRepositoryName()
   * @generated
   * @ordered
   */
  protected String repositoryName = REPOSITORY_NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getCdoPackageRegistryDef() <em>Cdo Package Registry Def</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getCdoPackageRegistryDef()
   * @generated
   * @ordered
   */
  protected CDOPackageRegistryDef ePackageRegistryDef;

  /**
   * The default value of the '{@link #isLegacySupportEnabled() <em>Legacy Support Enabled</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isLegacySupportEnabled()
   * @generated
   * @ordered
   */
  protected static final boolean LEGACY_SUPPORT_ENABLED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isLegacySupportEnabled() <em>Legacy Support Enabled</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isLegacySupportEnabled()
   * @generated
   * @ordered
   */
  protected boolean legacySupportEnabled = LEGACY_SUPPORT_ENABLED_EDEFAULT;

  /**
   * The cached value of the '{@link #getFailOverStrategyDef() <em>Fail Over Strategy Def</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getFailOverStrategyDef()
   * @generated
   * @ordered
   */
  protected FailOverStrategyDef failOverStrategyDef;

  /**
   * This is true if the Fail Over Strategy Def reference has been set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  protected boolean failOverStrategyDefESet;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CDOSessionDefImpl()
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
    return CDODefsPackage.Literals.CDO_SESSION_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ConnectorDef getConnectorDef()
  {
    if (connectorDef != null && connectorDef.eIsProxy())
    {
      InternalEObject oldConnectorDef = (InternalEObject)connectorDef;
      connectorDef = (ConnectorDef)eResolveProxy(oldConnectorDef);
      if (connectorDef != oldConnectorDef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF,
              oldConnectorDef, connectorDef));
      }
    }
    return connectorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ConnectorDef basicGetConnectorDef()
  {
    return connectorDef;
  }

  /**
   * <!-- begin-user-doc --> Sets the {@link ConnectorDef} for this {@link CDOSessionDef}. If a
   * {@link FailOverStrategyDef} was already set, an IllegalStateException is thrown. ConnectorDef and
   * FailOverStrategyDef are mutually exclusive.
   * 
   * @throws IllegalStateException
   *           if a {@link FailOverStrategyDef} was set before
   * @see CDOSessionConfigurationImpl#openSession() <!-- end-user-doc -->
   * @generated NOT
   */
  public void setConnectorDef(ConnectorDef newConnectorDef)
  {
    if (isSetFailOverStrategyDef())
    {
      throw new IllegalStateException("connector and failover strategy are mutually exclusive!");
    }
    else
    {
      setConnectorDefGen(newConnectorDef);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setConnectorDefGen(ConnectorDef newConnectorDef)
  {
    ConnectorDef oldConnectorDef = connectorDef;
    connectorDef = newConnectorDef;
    boolean oldConnectorDefESet = connectorDefESet;
    connectorDefESet = true;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF,
          oldConnectorDef, connectorDef, !oldConnectorDefESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void unsetConnectorDef()
  {
    ConnectorDef oldConnectorDef = connectorDef;
    boolean oldConnectorDefESet = connectorDefESet;
    connectorDef = null;
    connectorDefESet = false;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.UNSET, CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF,
          oldConnectorDef, null, oldConnectorDefESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isSetConnectorDef()
  {
    return connectorDefESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getRepositoryName()
  {
    return repositoryName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setRepositoryName(String newRepositoryName)
  {
    String oldRepositoryName = repositoryName;
    repositoryName = newRepositoryName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SESSION_DEF__REPOSITORY_NAME,
          oldRepositoryName, repositoryName));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDOPackageRegistryDef getCdoPackageRegistryDef()
  {
    if (ePackageRegistryDef != null && ePackageRegistryDef.eIsProxy())
    {
      InternalEObject oldCdoPackageRegistryDef = (InternalEObject)ePackageRegistryDef;
      ePackageRegistryDef = (CDOPackageRegistryDef)eResolveProxy(oldCdoPackageRegistryDef);
      if (ePackageRegistryDef != oldCdoPackageRegistryDef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF, oldCdoPackageRegistryDef, ePackageRegistryDef));
      }
    }
    return ePackageRegistryDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDOPackageRegistryDef basicGetCdoPackageRegistryDef()
  {
    return ePackageRegistryDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setCdoPackageRegistryDef(CDOPackageRegistryDef newCdoPackageRegistryDef)
  {
    CDOPackageRegistryDef oldCdoPackageRegistryDef = ePackageRegistryDef;
    ePackageRegistryDef = newCdoPackageRegistryDef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF,
          oldCdoPackageRegistryDef, ePackageRegistryDef));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isLegacySupportEnabled()
  {
    return legacySupportEnabled;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setLegacySupportEnabled(boolean newLegacySupportEnabled)
  {
    boolean oldLegacySupportEnabled = legacySupportEnabled;
    legacySupportEnabled = newLegacySupportEnabled;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED,
          oldLegacySupportEnabled, legacySupportEnabled));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public FailOverStrategyDef getFailOverStrategyDef()
  {
    if (failOverStrategyDef != null && failOverStrategyDef.eIsProxy())
    {
      InternalEObject oldFailOverStrategyDef = (InternalEObject)failOverStrategyDef;
      failOverStrategyDef = (FailOverStrategyDef)eResolveProxy(oldFailOverStrategyDef);
      if (failOverStrategyDef != oldFailOverStrategyDef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              CDODefsPackage.CDO_SESSION_DEF__FAIL_OVER_STRATEGY_DEF, oldFailOverStrategyDef, failOverStrategyDef));
      }
    }
    return failOverStrategyDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public FailOverStrategyDef basicGetFailOverStrategyDef()
  {
    return failOverStrategyDef;
  }

  /**
   * <!-- begin-user-doc --> Sets the {@link FailOverStrategyDef} for this {@link CDOSessionDef}. If a ConnectorDef was
   * already set, an <b>IllegalStateException<b> is thrown. ConnectorDef and FailOverStrategyDef are mutually exclusive.
   * 
   * @throws IllegalStateException
   *           if a {@link FailOverStrategyDef} was set before
   * @see CDOSessionConfigurationImpl#openSession() <!-- end-user-doc -->
   * @generated NOT
   */
  public void setFailOverStrategyDef(FailOverStrategyDef newFailOverStrategyDef)
  {
    if (isSetConnectorDef())
    {
      throw new IllegalStateException("connector and failover strategy are mutually exclusive!");
    }
    else
    {
      setFailOverStrategyDefGen(newFailOverStrategyDef);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setFailOverStrategyDefGen(FailOverStrategyDef newFailOverStrategyDef)
  {
    FailOverStrategyDef oldFailOverStrategyDef = failOverStrategyDef;
    failOverStrategyDef = newFailOverStrategyDef;
    boolean oldFailOverStrategyDefESet = failOverStrategyDefESet;
    failOverStrategyDefESet = true;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SESSION_DEF__FAIL_OVER_STRATEGY_DEF,
          oldFailOverStrategyDef, failOverStrategyDef, !oldFailOverStrategyDefESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void unsetFailOverStrategyDef()
  {
    FailOverStrategyDef oldFailOverStrategyDef = failOverStrategyDef;
    boolean oldFailOverStrategyDefESet = failOverStrategyDefESet;
    failOverStrategyDef = null;
    failOverStrategyDefESet = false;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.UNSET, CDODefsPackage.CDO_SESSION_DEF__FAIL_OVER_STRATEGY_DEF,
          oldFailOverStrategyDef, null, oldFailOverStrategyDefESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isSetFailOverStrategyDef()
  {
    return failOverStrategyDefESet;
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
    case CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF:
      if (resolve)
        return getConnectorDef();
      return basicGetConnectorDef();
    case CDODefsPackage.CDO_SESSION_DEF__REPOSITORY_NAME:
      return getRepositoryName();
    case CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF:
      if (resolve)
        return getCdoPackageRegistryDef();
      return basicGetCdoPackageRegistryDef();
    case CDODefsPackage.CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED:
      return isLegacySupportEnabled() ? Boolean.TRUE : Boolean.FALSE;
    case CDODefsPackage.CDO_SESSION_DEF__FAIL_OVER_STRATEGY_DEF:
      if (resolve)
        return getFailOverStrategyDef();
      return basicGetFailOverStrategyDef();
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
    case CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF:
      setConnectorDef((ConnectorDef)newValue);
      return;
    case CDODefsPackage.CDO_SESSION_DEF__REPOSITORY_NAME:
      setRepositoryName((String)newValue);
      return;
    case CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF:
      setCdoPackageRegistryDef((CDOPackageRegistryDef)newValue);
      return;
    case CDODefsPackage.CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED:
      setLegacySupportEnabled(((Boolean)newValue).booleanValue());
      return;
    case CDODefsPackage.CDO_SESSION_DEF__FAIL_OVER_STRATEGY_DEF:
      setFailOverStrategyDef((FailOverStrategyDef)newValue);
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
    case CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF:
      unsetConnectorDef();
      return;
    case CDODefsPackage.CDO_SESSION_DEF__REPOSITORY_NAME:
      setRepositoryName(REPOSITORY_NAME_EDEFAULT);
      return;
    case CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF:
      setCdoPackageRegistryDef((CDOPackageRegistryDef)null);
      return;
    case CDODefsPackage.CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED:
      setLegacySupportEnabled(LEGACY_SUPPORT_ENABLED_EDEFAULT);
      return;
    case CDODefsPackage.CDO_SESSION_DEF__FAIL_OVER_STRATEGY_DEF:
      unsetFailOverStrategyDef();
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
    case CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF:
      return isSetConnectorDef();
    case CDODefsPackage.CDO_SESSION_DEF__REPOSITORY_NAME:
      return REPOSITORY_NAME_EDEFAULT == null ? repositoryName != null : !REPOSITORY_NAME_EDEFAULT
          .equals(repositoryName);
    case CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF:
      return ePackageRegistryDef != null;
    case CDODefsPackage.CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED:
      return legacySupportEnabled != LEGACY_SUPPORT_ENABLED_EDEFAULT;
    case CDODefsPackage.CDO_SESSION_DEF__FAIL_OVER_STRATEGY_DEF:
      return isSetFailOverStrategyDef();
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
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (repositoryName: ");
    result.append(repositoryName);
    result.append(", legacySupportEnabled: ");
    result.append(legacySupportEnabled);
    result.append(')');
    return result.toString();
  }

  @Override
  protected CDOSession createInstance()
  {
    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();

    if (isSetConnectorDef())
    {
      configuration.setConnector((IConnector)getConnectorDef().getInstance());
    }
    if (isSetFailOverStrategyDef())
    {
      configuration.setFailOverStrategy((IFailOverStrategy)getFailOverStrategyDef().getInstance());
    }
    configuration.setPackageRegistry((CDOPackageRegistry)getCdoPackageRegistryDef().getInstance());
    configuration.setRepositoryName(getRepositoryName());
    return configuration.openSession();
  }

  @Override
  protected void validateDefinition()
  {
    CheckUtil.checkState //
        (eIsSet(CDODefsPackage.CDO_SESSION_DEF__REPOSITORY_NAME), "repository name not set yet!");
    /*
     * failoverStrategy and connector are mutually exclusive
     * @see CDOSessionConfiguration#openSession
     */
    CheckUtil.checkState(isSetConnectorDef() ^ isSetFailOverStrategyDef(),
        "fail over strategy and connector are mutually exclusive!");
    // CheckUtil.checkState(getCdoPackageRegistryDef() != null,
    // "package registry definition is not set!");
    CheckUtil.checkState(eIsSet(CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF),
        "package registry definition is not set!");
  }

} // CDOSessionDefImpl
