/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.Holdable;
import org.eclipse.emf.cdo.tests.model6.Holder;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Holder</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.HolderImpl#getHeld <em>Held</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.HolderImpl#getOwned <em>Owned</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HolderImpl extends HoldableImpl implements Holder
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HolderImpl()
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
    return Model6Package.Literals.HOLDER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Holdable> getHeld()
  {
    return (EList<Holdable>)eGet(Model6Package.Literals.HOLDER__HELD, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Holdable> getOwned()
  {
    return (EList<Holdable>)eGet(Model6Package.Literals.HOLDER__OWNED, true);
  }

} // HolderImpl
