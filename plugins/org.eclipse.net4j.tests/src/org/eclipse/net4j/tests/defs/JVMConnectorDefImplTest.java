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
import org.eclipse.net4j.defs.JVMConnectorDef;
import org.eclipse.net4j.defs.Net4jDefsFactory;
import org.eclipse.net4j.internal.jvm.JVMAcceptor;
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
public class JVMConnectorDefImplTest extends AbstractOMTest
{
  private static final String NAME = "test1"; //$NON-NLS-1$

  public void testIncompleteInitializationThrowsIllegalArgumentException()
  {
    try
    {
      Net4jDefsFactory.eINSTANCE.createJVMConnectorDef().getInstance();
      fail("IllegalStateException expected!"); //$NON-NLS-1$
    }
    catch (IllegalStateException e)
    {
    }
  }

  public void testConnectorLaunchableOpensConnection()
  {
    IJVMAcceptor jvmAcceptor = createJVMAcceptor();

    JVMConnectorDef jvmConnectorDef = Net4jDefsFactory.eINSTANCE.createJVMConnectorDef();

    jvmConnectorDef.setBufferProvider(Net4jDefsFactory.eINSTANCE.createBufferPoolDef());
    jvmConnectorDef.setExecutorService(Net4jUtilDefsFactory.eINSTANCE.createThreadPoolDef());
    jvmConnectorDef.setName(NAME);
    IJVMConnector jvmConnector = (IJVMConnector)jvmConnectorDef.getInstance();
    jvmConnector.connect(500L);
    assertEquals(true, jvmConnector.isConnected());

    LifecycleUtil.deactivate(jvmConnector);
    LifecycleUtil.deactivate(jvmAcceptor);
  }

  private IJVMAcceptor createJVMAcceptor()
  {
    ExecutorService threadPool = Executors.newCachedThreadPool();
    LifecycleUtil.activate(threadPool);

    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    JVMAcceptor jvmAcceptor = new JVMAcceptor();
    jvmAcceptor.setName(NAME);
    jvmAcceptor.getConfig().setBufferProvider(bufferPool);
    jvmAcceptor.getConfig().setReceiveExecutor(threadPool);
    LifecycleUtil.activate(jvmAcceptor);

    return jvmAcceptor;
  }
}
