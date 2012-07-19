/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Andre Dietisheim - initial API and implementation
 *   Eike Stepper - maintenance
 *
 */
package org.eclipse.net4j.defs.impl;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.defs.BufferPoolDef;
import org.eclipse.net4j.defs.Net4jDefsPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Buffer Pool Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class BufferPoolDefImpl extends BufferProviderDefImpl implements BufferPoolDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected BufferPoolDefImpl()
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
    return Net4jDefsPackage.Literals.BUFFER_POOL_DEF;
  }

  /**
   * creates and returns a buffer provider.
   * 
   * @generated NOT
   */
  @Override
  protected Object createInstance()
  {
    return Net4jUtil.createBufferPool();
  }
} // BufferPoolDefImpl
