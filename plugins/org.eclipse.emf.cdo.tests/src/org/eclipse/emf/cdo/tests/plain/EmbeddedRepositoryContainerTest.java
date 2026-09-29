/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.embedded.CDOEmbeddedRepositoryConfig;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.ConnectorFactory;
import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.util.Map;

/**
 * Tests standalone container preparation by the embedded repository.
 *
 * @author Eike Stepper
 */
public class EmbeddedRepositoryContainerTest extends PlainTest
{
  public void testRawContainerIsPreparedDuringRepositoryActivation()
  {
    IManagedContainer container = ContainerUtil.createContainer();
    LifecycleUtil.activate(container);

    try
    {
      assertEmbeddedRepositoryWorks(container, "embedded-raw-container"); //$NON-NLS-1$

      assertNotNull(container.getFactory(AcceptorFactory.PRODUCT_GROUP, "jvm")); //$NON-NLS-1$
      assertNotNull(container.getFactory(ConnectorFactory.PRODUCT_GROUP, "jvm")); //$NON-NLS-1$
      assertNotNull(container.getFactory(ServerProtocolFactory.PRODUCT_GROUP, "cdo")); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  public void testInitializedContainerRemainsUsable()
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      assertEmbeddedRepositoryWorks(container, "embedded-initialized-container"); //$NON-NLS-1$
      assertNotNull(container.getFactory(AcceptorFactory.PRODUCT_GROUP, "jvm")); //$NON-NLS-1$
      assertNotNull(container.getFactory(ServerProtocolFactory.PRODUCT_GROUP, "cdo")); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  private void assertEmbeddedRepositoryWorks(IManagedContainer container, String repositoryName)
  {
    TestConfig config = new TestConfig(repositoryName, container);

    try
    {
      LifecycleUtil.activate(config);

      CDONet4jSession session = config.openClientSession();
      try
      {
        assertNotNull(session);
      }
      finally
      {
        LifecycleUtil.deactivate(session);
      }
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
    }
  }

  private static final class TestConfig extends CDOEmbeddedRepositoryConfig
  {
    private final IManagedContainer container;

    private final IManagedContainer clientContainer = ContainerUtil.createInitializedContainer();

    TestConfig(String repositoryName, IManagedContainer container)
    {
      super(repositoryName);
      this.container = container;
    }

    @Override
    public IManagedContainer getContainer()
    {
      return container;
    }

    @Override
    public IStore createStore(IManagedContainer container)
    {
      return MEMStoreUtil.createMEMStore();
    }

    @Override
    public IConnector createConnector(IManagedContainer container)
    {
      return JVMUtil.getConnector(clientContainer, "cdo_embedded_repo_" + getRepositoryName()); //$NON-NLS-1$
    }

    @Override
    public void initProperties(IManagedContainer container, Map<String, String> properties)
    {
      properties.put(IRepository.Props.SUPPORTING_BRANCHES, "false"); //$NON-NLS-1$
      properties.put(IRepository.Props.SUPPORTING_AUDITS, "false"); //$NON-NLS-1$
    }
  }
}
