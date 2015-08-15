/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.defs.impl;

import org.eclipse.net4j.internal.util.concurrent.ThreadPool;
import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.defs.ThreadPoolDef;

import org.eclipse.emf.ecore.EClass;

import java.util.concurrent.ExecutorService;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Thread Pool Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ThreadPoolDefImpl extends ExecutorServiceDefImpl implements ThreadPoolDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ThreadPoolDefImpl()
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
    return Net4jUtilDefsPackage.Literals.THREAD_POOL_DEF;
  }

  /**
   * Gets a executor service instance. The current implementation does not reuse an instance created in a former call
   * TODO: reuse instances
   *
   * @return the instance
   */
  @Override
  protected Object createInstance()
  {
    ExecutorService executorService = ThreadPool.create();
    return executorService;
  }

} // ThreadPoolDefImpl
