/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.Model6Package;
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Reference Object</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl#getReferenceOptional <em>Reference Optional
 * </em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl#getReferenceList <em>Reference List</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReferenceObjectImpl extends BaseObjectImpl implements ReferenceObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ReferenceObjectImpl()
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
    return Model6Package.Literals.REFERENCE_OBJECT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BaseObject getReferenceOptional()
  {
    return (BaseObject)eGet(Model6Package.Literals.REFERENCE_OBJECT__REFERENCE_OPTIONAL, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setReferenceOptional(BaseObject newReferenceOptional)
  {
    eSet(Model6Package.Literals.REFERENCE_OBJECT__REFERENCE_OPTIONAL, newReferenceOptional);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<BaseObject> getReferenceList()
  {
    return (EList<BaseObject>)eGet(Model6Package.Literals.REFERENCE_OBJECT__REFERENCE_LIST, true);
  }

} // ReferenceObjectImpl
