/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelPackage;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnFragmentGeneratorImpl;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Dawn EMF Generator</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl.DawnEMFGeneratorImpl#getEmfGenModel
 * <em>Emf Gen Model</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 * @author Martin Fluegge
 */
public class DawnEMFGeneratorImpl extends DawnFragmentGeneratorImpl implements DawnEMFGenerator
{
  /**
   * The cached value of the '{@link #getEmfGenModel() <em>Emf Gen Model</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getEmfGenModel()
   * @generated
   * @ordered
   */
  protected GenModel emfGenModel;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected DawnEMFGeneratorImpl()
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
    return DawnEmfGenmodelPackage.Literals.DAWN_EMF_GENERATOR;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public GenModel getEmfGenModel()
  {
    if (emfGenModel != null && emfGenModel.eIsProxy())
    {
      InternalEObject oldEmfGenModel = (InternalEObject)emfGenModel;
      emfGenModel = (GenModel)eResolveProxy(oldEmfGenModel);
      if (emfGenModel != oldEmfGenModel)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, DawnEmfGenmodelPackage.DAWN_EMF_GENERATOR__EMF_GEN_MODEL, oldEmfGenModel, emfGenModel));
        }
      }
    }
    return emfGenModel;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public GenModel basicGetEmfGenModel()
  {
    return emfGenModel;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setEmfGenModel(GenModel newEmfGenModel)
  {
    GenModel oldEmfGenModel = emfGenModel;
    emfGenModel = newEmfGenModel;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DawnEmfGenmodelPackage.DAWN_EMF_GENERATOR__EMF_GEN_MODEL, oldEmfGenModel, emfGenModel));
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
    case DawnEmfGenmodelPackage.DAWN_EMF_GENERATOR__EMF_GEN_MODEL:
      if (resolve)
      {
        return getEmfGenModel();
      }
      return basicGetEmfGenModel();
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
    case DawnEmfGenmodelPackage.DAWN_EMF_GENERATOR__EMF_GEN_MODEL:
      setEmfGenModel((GenModel)newValue);
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
    case DawnEmfGenmodelPackage.DAWN_EMF_GENERATOR__EMF_GEN_MODEL:
      setEmfGenModel((GenModel)null);
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
    case DawnEmfGenmodelPackage.DAWN_EMF_GENERATOR__EMF_GEN_MODEL:
      return emfGenModel != null;
    }
    return super.eIsSet(featureID);
  }

} // DawnEMFGeneratorImpl
