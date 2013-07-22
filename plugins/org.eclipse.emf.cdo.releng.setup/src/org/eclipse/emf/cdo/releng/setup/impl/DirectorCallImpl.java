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

import org.eclipse.emf.cdo.releng.setup.DirectorCall;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Director Call</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.DirectorCallImpl#getInstallableUnits <em>Installable Units</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.DirectorCallImpl#getP2Repositories <em>P2 Repositories</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DirectorCallImpl extends MinimalEObjectImpl.Container implements DirectorCall
{
  /**
   * The cached value of the '{@link #getInstallableUnits() <em>Installable Units</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstallableUnits()
   * @generated
   * @ordered
   */
  protected EList<InstallableUnit> installableUnits;

  /**
   * The cached value of the '{@link #getP2Repositories() <em>P2 Repositories</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getP2Repositories()
   * @generated
   * @ordered
   */
  protected EList<P2Repository> p2Repositories;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DirectorCallImpl()
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
    return SetupPackage.Literals.DIRECTOR_CALL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<InstallableUnit> getInstallableUnits()
  {
    if (installableUnits == null)
    {
      installableUnits = new EObjectContainmentWithInverseEList<InstallableUnit>(InstallableUnit.class, this,
          SetupPackage.DIRECTOR_CALL__INSTALLABLE_UNITS, SetupPackage.INSTALLABLE_UNIT__DIRECTOR_CALL);
    }
    return installableUnits;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<P2Repository> getP2Repositories()
  {
    if (p2Repositories == null)
    {
      p2Repositories = new EObjectContainmentWithInverseEList<P2Repository>(P2Repository.class, this,
          SetupPackage.DIRECTOR_CALL__P2_REPOSITORIES, SetupPackage.P2_REPOSITORY__DIRECTOR_CALL);
    }
    return p2Repositories;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.DIRECTOR_CALL__INSTALLABLE_UNITS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getInstallableUnits()).basicAdd(otherEnd, msgs);
    case SetupPackage.DIRECTOR_CALL__P2_REPOSITORIES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getP2Repositories()).basicAdd(otherEnd, msgs);
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
    case SetupPackage.DIRECTOR_CALL__INSTALLABLE_UNITS:
      return ((InternalEList<?>)getInstallableUnits()).basicRemove(otherEnd, msgs);
    case SetupPackage.DIRECTOR_CALL__P2_REPOSITORIES:
      return ((InternalEList<?>)getP2Repositories()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case SetupPackage.DIRECTOR_CALL__INSTALLABLE_UNITS:
      return getInstallableUnits();
    case SetupPackage.DIRECTOR_CALL__P2_REPOSITORIES:
      return getP2Repositories();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.DIRECTOR_CALL__INSTALLABLE_UNITS:
      getInstallableUnits().clear();
      getInstallableUnits().addAll((Collection<? extends InstallableUnit>)newValue);
      return;
    case SetupPackage.DIRECTOR_CALL__P2_REPOSITORIES:
      getP2Repositories().clear();
      getP2Repositories().addAll((Collection<? extends P2Repository>)newValue);
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
    case SetupPackage.DIRECTOR_CALL__INSTALLABLE_UNITS:
      getInstallableUnits().clear();
      return;
    case SetupPackage.DIRECTOR_CALL__P2_REPOSITORIES:
      getP2Repositories().clear();
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
    case SetupPackage.DIRECTOR_CALL__INSTALLABLE_UNITS:
      return installableUnits != null && !installableUnits.isEmpty();
    case SetupPackage.DIRECTOR_CALL__P2_REPOSITORIES:
      return p2Repositories != null && !p2Repositories.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // DirectorCallImpl
