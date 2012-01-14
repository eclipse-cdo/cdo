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
package org.eclipse.net4j.tests.defs;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.defs.JVMAcceptorDef;
import org.eclipse.net4j.defs.Net4jDefsFactory;
import org.eclipse.net4j.internal.jvm.JVMClientConnector;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.util.defs.Net4jUtilDefsFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Andre Dietisheim
 */
public class JVMAcceptorDefImplTest extends AbstractOMTest
{
  private static final String NAME = "JVMConnector1"; //$NON-NLS-1$

  private static final long TIMEOUT = 10000l;

  private static final long DELAY = 500l;

  private IJVMConnector jvmConnector;

  @Override
  protected void doSetUp() throws Exception
  {
    jvmConnector = createJVMClientConnector();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    LifecycleUtil.deactivate(jvmConnector);
  }

  public void testAcceptorDefOpensConnection()
  {
    JVMAcceptorDef jvmAcceptorDef = Net4jDefsFactory.eINSTANCE.createJVMAcceptorDef();
    jvmAcceptorDef.setBufferProvider(Net4jDefsFactory.eINSTANCE.createBufferPoolDef());
    jvmAcceptorDef.setExecutorService(Net4jUtilDefsFactory.eINSTANCE.createThreadPoolDef());
    jvmAcceptorDef.setName(NAME);

    IJVMAcceptor jvmAcceptor = (IJVMAcceptor)jvmAcceptorDef.getInstance();

    assertEquals(true, LifecycleUtil.isActive(jvmAcceptor));

    LifecycleUtil.activate(jvmConnector);
    jvmConnector.waitForConnection(DELAY + TIMEOUT);

    assertEquals(true, LifecycleUtil.isActive(jvmConnector));

    LifecycleUtil.deactivate(jvmAcceptor);
  }

  protected IJVMConnector createJVMClientConnector()
  {
    JVMClientConnector jvmClientConnector = new JVMClientConnector();
    jvmClientConnector.getConfig().setBufferProvider(createBufferPool());
    jvmClientConnector.getConfig().setReceiveExecutor(createThreadPool());
    jvmClientConnector.setName(NAME);

    return jvmClientConnector;
  }

  private IBufferPool createBufferPool()
  {
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);
    return bufferPool;
  }

  private ExecutorService createThreadPool()
  {
    ExecutorService threadPool = Executors.newCachedThreadPool();
    LifecycleUtil.activate(threadPool);
    return threadPool;
  }
}
