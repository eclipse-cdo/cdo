/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.ScopeRoot;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskScope;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Eclipse Version</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseImpl#getConfiguration <em>Configuration</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseImpl#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EclipseImpl extends ConfigurableItemImpl implements Eclipse
{
  /**
   * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVersion()
   * @generated
   * @ordered
   */
  protected static final String VERSION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVersion()
   * @generated
   * @ordered
   */
  protected String version = VERSION_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EclipseImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.ECLIPSE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Configuration getConfiguration()
  {
    if (eContainerFeatureID() != SetupPackage.ECLIPSE__CONFIGURATION)
    {
      return null;
    }
    return (Configuration)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Configuration basicGetConfiguration()
  {
    if (eContainerFeatureID() != SetupPackage.ECLIPSE__CONFIGURATION)
    {
      return null;
    }
    return (Configuration)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetConfiguration(Configuration newConfiguration, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newConfiguration, SetupPackage.ECLIPSE__CONFIGURATION, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConfiguration(Configuration newConfiguration)
  {
    if (newConfiguration != eInternalContainer() || eContainerFeatureID() != SetupPackage.ECLIPSE__CONFIGURATION
        && newConfiguration != null)
    {
      if (EcoreUtil.isAncestor(this, newConfiguration))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newConfiguration != null)
      {
        msgs = ((InternalEObject)newConfiguration).eInverseAdd(this, SetupPackage.CONFIGURATION__ECLIPSE_VERSIONS,
            Configuration.class, msgs);
      }
      msgs = basicSetConfiguration(newConfiguration, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.ECLIPSE__CONFIGURATION, newConfiguration,
          newConfiguration));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getVersion()
  {
    return version;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setVersion(String newVersion)
  {
    String oldVersion = version;
    version = newVersion;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.ECLIPSE__VERSION, oldVersion, version));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE__CONFIGURATION:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetConfiguration((Configuration)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE__CONFIGURATION:
      return basicSetConfiguration(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case SetupPackage.ECLIPSE__CONFIGURATION:
      return eInternalContainer().eInverseRemove(this, SetupPackage.CONFIGURATION__ECLIPSE_VERSIONS,
          Configuration.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE__CONFIGURATION:
      if (resolve)
      {
        return getConfiguration();
      }
      return basicGetConfiguration();
    case SetupPackage.ECLIPSE__VERSION:
      return getVersion();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE__CONFIGURATION:
      setConfiguration((Configuration)newValue);
      return;
    case SetupPackage.ECLIPSE__VERSION:
      setVersion((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE__CONFIGURATION:
      setConfiguration((Configuration)null);
      return;
    case SetupPackage.ECLIPSE__VERSION:
      setVersion(VERSION_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.ECLIPSE__CONFIGURATION:
      return basicGetConfiguration() != null;
    case SetupPackage.ECLIPSE__VERSION:
      return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
    result.append(" (version: ");
    result.append(version);
    result.append(')');
    return result.toString();
  }

  @Override
  public SetupTaskScope getScope()
  {
    return SetupTaskScope.ECLIPSE;
  }

  @Override
  public ScopeRoot getParentScopeRoot()
  {
    EObject container = eContainer();
    if (container instanceof ScopeRoot)
    {
      return (ScopeRoot)container;
    }

    return null;
  }

} // EclipseImpl
