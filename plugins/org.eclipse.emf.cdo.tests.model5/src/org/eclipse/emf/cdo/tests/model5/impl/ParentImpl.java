/*
 * Copyright (c) 2012, 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5.impl;

import org.eclipse.emf.cdo.tests.model5.Child;
import org.eclipse.emf.cdo.tests.model5.Model5Package;
import org.eclipse.emf.cdo.tests.model5.Parent;
import org.eclipse.emf.cdo.tests.model5.util.IsLoadingTestFixture;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parent</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.impl.ParentImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.impl.ParentImpl#getFavourite <em>Favourite</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.impl.ParentImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParentImpl extends CDOObjectImpl implements Parent
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ParentImpl()
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
    return Model5Package.eINSTANCE.getParent();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Child> getChildren()
  {
    return (EList<Child>)eGet(Model5Package.eINSTANCE.getParent_Children(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Child getFavourite()
  {
    return (Child)eGet(Model5Package.eINSTANCE.getParent_Favourite(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFavourite(Child newFavourite)
  {
    eSet(Model5Package.eINSTANCE.getParent_Favourite(), newFavourite);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(Model5Package.eINSTANCE.getParent_Name(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNameGen(String newName)
  {
    eSet(Model5Package.eINSTANCE.getParent_Name(), newName);
  }

  @Override
  public void setName(String newName)
  {
    IsLoadingTestFixture.reportLoading(eResource(), this);
    setNameGen(newName);
  }
} // ParentImpl
