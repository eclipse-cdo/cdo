/*
 * Copyright (c) 2010-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.tests.model3.Model3Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Image</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.ImageImpl#getWidth <em>Width</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.ImageImpl#getHeight <em>Height</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.ImageImpl#getData <em>Data</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ImageImpl extends CDOObjectImpl implements Image
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImageImpl()
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
    return Model3Package.eINSTANCE.getImage();
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
  public int getWidth()
  {
    return (Integer)eGet(Model3Package.eINSTANCE.getImage_Width(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setWidth(int newWidth)
  {
    eSet(Model3Package.eINSTANCE.getImage_Width(), newWidth);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getHeight()
  {
    return (Integer)eGet(Model3Package.eINSTANCE.getImage_Height(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setHeight(int newHeight)
  {
    eSet(Model3Package.eINSTANCE.getImage_Height(), newHeight);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBlob getData()
  {
    return (CDOBlob)eGet(Model3Package.eINSTANCE.getImage_Data(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setData(CDOBlob newData)
  {
    eSet(Model3Package.eINSTANCE.getImage_Data(), newData);
  }

} // ImageImpl
