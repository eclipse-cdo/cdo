/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Impl Multi Ref Non Container NPL</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefNonContainerNPLImpl#getElements <em>Elements</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ImplMultiRefNonContainerNPLImpl extends CDOObjectImpl implements ImplMultiRefNonContainerNPL
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImplMultiRefNonContainerNPLImpl()
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
    return model4Package.eINSTANCE.getImplMultiRefNonContainerNPL();
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
  @Override
  @SuppressWarnings("unchecked")
  public EList<IContainedElementNoParentLink> getElements()
  {
    return (EList<IContainedElementNoParentLink>)eGet(model4interfacesPackage.eINSTANCE.getIMultiRefNonContainerNPL_Elements(), true);
  }

} // ImplMultiRefNonContainerNPLImpl
