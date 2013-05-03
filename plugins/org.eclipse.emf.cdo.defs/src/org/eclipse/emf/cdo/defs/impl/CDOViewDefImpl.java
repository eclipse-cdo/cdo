/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.defs.CDOViewDef;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.defs.impl.DefImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>CDO View Definition</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.defs.impl.CDOViewDefImpl#getCdoSessionDef <em>Cdo Session Def</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CDOViewDefImpl extends DefImpl implements CDOViewDef
{

  /**
   * The cached value of the '{@link #getCdoSessionDef() <em>Cdo Session Def</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCdoSessionDef()
   * @generated
   * @ordered
   */
  protected CDOSessionDef cdoSessionDef;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOViewDefImpl()
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
    return CDODefsPackage.Literals.CDO_VIEW_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOSessionDef getCdoSessionDef()
  {
    if (cdoSessionDef != null && cdoSessionDef.eIsProxy())
    {
      InternalEObject oldCdoSessionDef = (InternalEObject)cdoSessionDef;
      cdoSessionDef = (CDOSessionDef)eResolveProxy(oldCdoSessionDef);
      if (cdoSessionDef != oldCdoSessionDef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF,
              oldCdoSessionDef, cdoSessionDef));
      }
    }
    return cdoSessionDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOSessionDef basicGetCdoSessionDef()
  {
    return cdoSessionDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setCdoSessionDef(CDOSessionDef newCdoSessionDef)
  {
    CDOSessionDef oldCdoSessionDef = cdoSessionDef;
    cdoSessionDef = newCdoSessionDef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF,
          oldCdoSessionDef, cdoSessionDef));
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
    case CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF:
      if (resolve)
        return getCdoSessionDef();
      return basicGetCdoSessionDef();
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
    case CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF:
      setCdoSessionDef((CDOSessionDef)newValue);
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
    case CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF:
      setCdoSessionDef((CDOSessionDef)null);
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
    case CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF:
      return cdoSessionDef != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * @ADDED
   */
  @Override
  protected Object createInstance()
  {
    CDOSession cdoSession = (CDOSession)getCdoSessionDef().getInstance();
    return cdoSession.openView();
  }

  /**
   * @ADDED
   */
  @Override
  protected void validateDefinition()
  {
    CheckUtil.checkState(eIsSet(CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF), "no session definition set yet!");
  }
} // CDOViewDefImpl
