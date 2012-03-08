/**
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.A;
import org.eclipse.emf.cdo.tests.model6.E;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>E</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.EImpl#getOwnedAs <em>Owned As</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EImpl extends CDOObjectImpl implements E
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected EImpl()
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
    return Model6Package.Literals.E;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<A> getOwnedAs()
  {
    return (EList<A>)eGet(Model6Package.Literals.E__OWNED_AS, true);
  }

} // EImpl
