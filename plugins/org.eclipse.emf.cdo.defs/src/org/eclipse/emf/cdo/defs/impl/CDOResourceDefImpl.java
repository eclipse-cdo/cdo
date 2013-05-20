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
import org.eclipse.emf.cdo.defs.CDOResourceDef;
import org.eclipse.emf.cdo.defs.CDOTransactionDef;
import org.eclipse.emf.cdo.defs.ResourceMode;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.defs.impl.DefImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Resource Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.defs.impl.CDOResourceDefImpl#getCdoTransaction <em>Cdo Transaction</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CDOResourceDefImpl extends DefImpl implements CDOResourceDef
{

  /**
   * The cached value of the '{@link #getCdoTransaction() <em>Cdo Transaction</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCdoTransaction()
   * @generated
   * @ordered
   */
  protected CDOTransactionDef cdoTransaction;

  /**
   * The default value of the '{@link #getResourceMode() <em>Resource Mode</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getResourceMode()
   * @generated NOT
   * @ordered
   */
  protected static final ResourceMode RESOURCE_MODE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getResourceMode() <em>Resource Mode</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getResourceMode()
   * @generated
   * @ordered
   */
  protected ResourceMode resourceMode = RESOURCE_MODE_EDEFAULT;

  /**
   * The default value of the '{@link #getPath() <em>Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getPath()
   * @generated
   * @ordered
   */
  protected static final String PATH_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getPath() <em>Path</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getPath()
   * @generated
   * @ordered
   */
  protected String path = PATH_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOResourceDefImpl()
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
    return CDODefsPackage.Literals.CDO_RESOURCE_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOTransactionDef getCdoTransaction()
  {
    if (cdoTransaction != null && cdoTransaction.eIsProxy())
    {
      InternalEObject oldCdoTransaction = (InternalEObject)cdoTransaction;
      cdoTransaction = (CDOTransactionDef)eResolveProxy(oldCdoTransaction);
      if (cdoTransaction != oldCdoTransaction)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CDODefsPackage.CDO_RESOURCE_DEF__CDO_TRANSACTION,
              oldCdoTransaction, cdoTransaction));
      }
    }
    return cdoTransaction;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOTransactionDef basicGetCdoTransaction()
  {
    return cdoTransaction;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setCdoTransaction(CDOTransactionDef newCdoTransaction)
  {
    CDOTransactionDef oldCdoTransaction = cdoTransaction;
    cdoTransaction = newCdoTransaction;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_RESOURCE_DEF__CDO_TRANSACTION,
          oldCdoTransaction, cdoTransaction));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ResourceMode getResourceMode()
  {
    return resourceMode;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setResourceMode(ResourceMode newResourceMode)
  {
    ResourceMode oldResourceMode = resourceMode;
    resourceMode = newResourceMode == null ? RESOURCE_MODE_EDEFAULT : newResourceMode;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_RESOURCE_DEF__RESOURCE_MODE,
          oldResourceMode, resourceMode));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String getPath()
  {
    return path;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setPath(String newPath)
  {
    String oldPath = path;
    path = newPath;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_RESOURCE_DEF__PATH, oldPath, path));
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
    case CDODefsPackage.CDO_RESOURCE_DEF__CDO_TRANSACTION:
      if (resolve)
        return getCdoTransaction();
      return basicGetCdoTransaction();
    case CDODefsPackage.CDO_RESOURCE_DEF__RESOURCE_MODE:
      return getResourceMode();
    case CDODefsPackage.CDO_RESOURCE_DEF__PATH:
      return getPath();
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
    case CDODefsPackage.CDO_RESOURCE_DEF__CDO_TRANSACTION:
      setCdoTransaction((CDOTransactionDef)newValue);
      return;
    case CDODefsPackage.CDO_RESOURCE_DEF__RESOURCE_MODE:
      setResourceMode((ResourceMode)newValue);
      return;
    case CDODefsPackage.CDO_RESOURCE_DEF__PATH:
      setPath((String)newValue);
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
    case CDODefsPackage.CDO_RESOURCE_DEF__CDO_TRANSACTION:
      setCdoTransaction((CDOTransactionDef)null);
      return;
    case CDODefsPackage.CDO_RESOURCE_DEF__RESOURCE_MODE:
      setResourceMode(RESOURCE_MODE_EDEFAULT);
      return;
    case CDODefsPackage.CDO_RESOURCE_DEF__PATH:
      setPath(PATH_EDEFAULT);
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
    case CDODefsPackage.CDO_RESOURCE_DEF__CDO_TRANSACTION:
      return cdoTransaction != null;
    case CDODefsPackage.CDO_RESOURCE_DEF__RESOURCE_MODE:
      return resourceMode != RESOURCE_MODE_EDEFAULT;
    case CDODefsPackage.CDO_RESOURCE_DEF__PATH:
      return PATH_EDEFAULT == null ? path != null : !PATH_EDEFAULT.equals(path);
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
    result.append(" (resourceMode: ");
    result.append(resourceMode);
    result.append(", path: ");
    result.append(path);
    result.append(')');
    return result.toString();
  }

  @Override
  protected Object createInstance()
  {
    CDOTransaction cdoTransaction = (CDOTransaction)getCdoTransaction().getInstance();
    CDOResource cdoResource = getResourceMode().getResource(getPath(), cdoTransaction);

    try
    {
      cdoTransaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }

    return cdoResource;

  }

  @Override
  protected void validateDefinition()
  {
    CheckUtil.checkState(eIsSet(CDODefsPackage.CDO_RESOURCE_DEF__PATH), "path is not set!");
    CheckUtil.checkState(eIsSet(CDODefsPackage.CDO_RESOURCE_DEF__CDO_TRANSACTION), "cdo transaction is not set!");
    CheckUtil.checkState(eIsSet(CDODefsPackage.CDO_RESOURCE_DEF__RESOURCE_MODE), "resourceMode is not set!");
  }

} // CDOResourceDefImpl
