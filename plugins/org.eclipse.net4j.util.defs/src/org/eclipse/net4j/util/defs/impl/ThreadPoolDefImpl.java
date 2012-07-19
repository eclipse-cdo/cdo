/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.defs.ThreadPoolDef;

import org.eclipse.emf.ecore.EClass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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

  private static final String THREADGROUP_IDENTIFIER = "net4j"; //$NON-NLS-1$

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
    ExecutorService executorService = Executors.newCachedThreadPool(new DaemonThreadFactory(THREADGROUP_IDENTIFIER));
    return executorService;
  }

  private static class DaemonThreadFactory implements ThreadFactory
  {
    private ThreadGroup threadGroup;

    public DaemonThreadFactory(String threadGroupIdentifier)
    {
      super();
      threadGroup = new ThreadGroup(threadGroupIdentifier);
    }

    public Thread newThread(Runnable r)
    {
      Thread thread = new Thread(threadGroup, r);
      thread.setDaemon(true);
      return thread;
    }
  }

} // ThreadPoolDefImpl
