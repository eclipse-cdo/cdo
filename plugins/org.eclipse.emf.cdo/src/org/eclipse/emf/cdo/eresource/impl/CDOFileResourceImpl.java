/*
 * Copyright (c) 2011, 2012, 2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.IOException;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO File Resource</b></em>'.
 *
 * @since 4.1
 * @noextend This class is not intended to be subclassed by clients. <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class CDOFileResourceImpl<IO> extends CDOResourceLeafImpl implements CDOFileResource<IO>
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOFileResourceImpl()
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
    return EresourcePackage.Literals.CDO_FILE_RESOURCE;
  }

  /**
   * @ADDED
   */
  @Override
  public boolean isRoot()
  {
    return false;
  }

  /**
   * @ADDED
   */
  @Override
  public void delete(Map<?, ?> options) throws IOException
  {
    if (!FSMUtil.isTransient(this))
    {
      if (getFolder() == null)
      {
        InternalCDOView view = cdoView();
        view.getRootResource().getContents().remove(this);
      }
      else
      {
        basicSetFolder(null, false);
      }
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public abstract CDOLob<IO> getContents();

} // CDOFileResourceImpl
