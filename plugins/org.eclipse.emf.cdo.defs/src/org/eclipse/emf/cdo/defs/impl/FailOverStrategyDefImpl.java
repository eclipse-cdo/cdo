/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.defs.CDODefsPackage;
import org.eclipse.emf.cdo.defs.FailOverStrategyDef;

import org.eclipse.net4j.defs.ConnectorDef;
import org.eclipse.net4j.util.defs.impl.DefImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Fail Over Strategy Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.defs.impl.FailOverStrategyDefImpl#getConnectorDef <em>Connector Def</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class FailOverStrategyDefImpl extends DefImpl implements FailOverStrategyDef
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected FailOverStrategyDefImpl()
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
    return CDODefsPackage.Literals.FAIL_OVER_STRATEGY_DEF;
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
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              CDODefsPackage.FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF, oldConnectorDef, connectorDef));
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setConnectorDef(ConnectorDef newConnectorDef)
  {
    ConnectorDef oldConnectorDef = connectorDef;
    connectorDef = newConnectorDef;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF,
          oldConnectorDef, connectorDef));
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
    case CDODefsPackage.FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF:
      if (resolve)
      {
        return getConnectorDef();
      }
      return basicGetConnectorDef();
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
    case CDODefsPackage.FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF:
      setConnectorDef((ConnectorDef)newValue);
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
    case CDODefsPackage.FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF:
      setConnectorDef((ConnectorDef)null);
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
    case CDODefsPackage.FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF:
      return connectorDef != null;
    }
    return super.eIsSet(featureID);
  }

} // FailOverStrategyDefImpl
