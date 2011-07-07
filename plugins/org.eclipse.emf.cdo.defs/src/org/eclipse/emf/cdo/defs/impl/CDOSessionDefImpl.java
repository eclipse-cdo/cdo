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
package org.eclipse.emf.cdo.defs.impl;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.defs.CDODefsPackage;
import org.eclipse.emf.cdo.defs.CDOPackageRegistryDef;
import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.defs.ConnectorDef;
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
 * <li>{@link org.eclipse.emf.cdo.defs.impl.CDOSessionDefImpl#getRepositoryName <em>CDORepositoryInfo Name</em>}</li>
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
   * begin-user-doc -->
   * 
   * @since 3.0 <!-- end-user-doc -->
   * @see #getCdoPackageRegistryDef()
   * @generated
   * @ordered
   */
  protected CDOPackageRegistryDef cdoPackageRegistryDef;

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
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF,
              oldConnectorDef, connectorDef));
        }
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
    setConnectorDefGen(newConnectorDef);
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
    {
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF,
          oldConnectorDef, connectorDef, !oldConnectorDefESet));
    }
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
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF,
          oldConnectorDef, null, oldConnectorDefESet));
    }
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
    {
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SESSION_DEF__REPOSITORY_NAME,
          oldRepositoryName, repositoryName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDOPackageRegistryDef getCdoPackageRegistryDef()
  {
    if (cdoPackageRegistryDef != null && cdoPackageRegistryDef.eIsProxy())
    {
      InternalEObject oldCdoPackageRegistryDef = (InternalEObject)cdoPackageRegistryDef;
      cdoPackageRegistryDef = (CDOPackageRegistryDef)eResolveProxy(oldCdoPackageRegistryDef);
      if (cdoPackageRegistryDef != oldCdoPackageRegistryDef)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF, oldCdoPackageRegistryDef, cdoPackageRegistryDef));
        }
      }
    }
    return cdoPackageRegistryDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDOPackageRegistryDef basicGetCdoPackageRegistryDef()
  {
    return cdoPackageRegistryDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setCdoPackageRegistryDef(CDOPackageRegistryDef newCdoPackageRegistryDef)
  {
    CDOPackageRegistryDef oldCdoPackageRegistryDef = cdoPackageRegistryDef;
    cdoPackageRegistryDef = newCdoPackageRegistryDef;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF,
          oldCdoPackageRegistryDef, cdoPackageRegistryDef));
    }
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
    {
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED,
          oldLegacySupportEnabled, legacySupportEnabled));
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
    case CDODefsPackage.CDO_SESSION_DEF__CONNECTOR_DEF:
      if (resolve)
      {
        return getConnectorDef();
      }
      return basicGetConnectorDef();
    case CDODefsPackage.CDO_SESSION_DEF__REPOSITORY_NAME:
      return getRepositoryName();
    case CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF:
      if (resolve)
      {
        return getCdoPackageRegistryDef();
      }
      return basicGetCdoPackageRegistryDef();
    case CDODefsPackage.CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED:
      return isLegacySupportEnabled();
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
      setLegacySupportEnabled((Boolean)newValue);
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
      return cdoPackageRegistryDef != null;
    case CDODefsPackage.CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED:
      return legacySupportEnabled != LEGACY_SUPPORT_ENABLED_EDEFAULT;
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
    result.append(" (repositoryName: ");
    result.append(repositoryName);
    result.append(", legacySupportEnabled: ");
    result.append(legacySupportEnabled);
    result.append(')');
    return result.toString();
  }

  @Override
  @SuppressWarnings("deprecation")
  protected CDOSession createInstance()
  {
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();

    if (isSetConnectorDef())
    {
      configuration.setConnector((IConnector)getConnectorDef().getInstance());
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
    CheckUtil.checkState(isSetConnectorDef(), "connector not set");
    // CheckUtil.checkState(getCdoPackageRegistryDef() != null,
    // "package registry definition is not set!");
    CheckUtil.checkState(eIsSet(CDODefsPackage.CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF),
        "package registry definition is not set!");
  }

} // CDOSessionDefImpl
