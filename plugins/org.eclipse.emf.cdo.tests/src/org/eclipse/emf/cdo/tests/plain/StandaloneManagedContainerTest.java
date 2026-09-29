/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.admin.CDOAdminServerUtil;
import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServer;
import org.eclipse.emf.cdo.server.internal.admin.protocol.CDOAdminServerProtocol;
import org.eclipse.emf.cdo.server.internal.net4j.protocol.CDOServerProtocolFactory;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerFactory;
import org.eclipse.net4j.util.container.IManagedContainerInitializer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.ClientProtocolFactory;
import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Focused coverage for standalone managed-container provider discovery.
 *
 * @author Eike Stepper
 */
public class StandaloneManagedContainerTest extends PlainTest
{
  public void testNormalClassLoaderPreparesJVMAndCDOContributions()
  {
    Set<String> initializerTypes = new HashSet<>();
    for (IManagedContainerInitializer initializer : ServiceLoader.load(IManagedContainerInitializer.class))
    {
      assertTrue("Duplicate initializer: " + initializer.getClass().getName(), initializerTypes.add(initializer.getClass().getName())); //$NON-NLS-1$
    }

    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      assertNotNull(container.getFactory(AcceptorFactory.PRODUCT_GROUP, "jvm")); //$NON-NLS-1$
      assertNotNull(container.getFactory(ClientProtocolFactory.PRODUCT_GROUP, "cdo")); //$NON-NLS-1$
      assertNotNull(container.getFactory(ServerProtocolFactory.PRODUCT_GROUP, "cdo")); //$NON-NLS-1$
      assertNotNull(((CDOServerProtocolFactory)container.getFactory(ServerProtocolFactory.PRODUCT_GROUP, "cdo")).getRepositoryProvider()); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  @SuppressWarnings("restriction")
  public void testAutomaticallyInitializedPrivateContainerOwnsAdminFactories()
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      IManagedContainerFactory serverFactory = (IManagedContainerFactory)container.getFactory(CDOAdminServer.Factory.PRODUCT_GROUP,
          CDOAdminServer.Factory.TYPE);
      IManagedContainerFactory protocolFactory = (IManagedContainerFactory)container.getFactory(ServerProtocolFactory.PRODUCT_GROUP,
          CDOAdminServerProtocol.Factory.TYPE);

      assertNotNull(serverFactory);
      assertNotNull(protocolFactory);
      assertSame(container, serverFactory.getManagedContainer());
      assertSame(container, protocolFactory.getManagedContainer());
      assertSame(container, ((CDOAdminServer.Factory)serverFactory).getContainer());
      assertSame(container, ((CDOAdminServerProtocol.Factory)protocolFactory).getContainer());

      CDOAdminServer admin = ((CDOAdminServer.Factory)serverFactory).create(null);
      assertSame(container, admin.getContainer());

      CDOAdminServerProtocol protocol = ((CDOAdminServerProtocol.Factory)protocolFactory).create(null);
      assertSame(container, protocol.getContainer());

      CDOAdminServer.Factory unregisteredServerFactory = new CDOAdminServer.Factory();
      CDOAdminServerProtocol.Factory unregisteredProtocolFactory = new CDOAdminServerProtocol.Factory();
      assertSame(IManagedContainer.INSTANCE, unregisteredServerFactory.getManagedContainer());
      assertSame(IManagedContainer.INSTANCE, unregisteredProtocolFactory.getManagedContainer());
      assertSame(IManagedContainer.INSTANCE, unregisteredServerFactory.getContainer());
      assertSame(IManagedContainer.INSTANCE, unregisteredProtocolFactory.getContainer());

      assertGlobalFactoryBinding(new CDOAdminServer.Factory(), CDOAdminServer.Factory.PRODUCT_GROUP, CDOAdminServer.Factory.TYPE);
      assertGlobalFactoryBinding(new CDOAdminServerProtocol.Factory(), ServerProtocolFactory.PRODUCT_GROUP, CDOAdminServerProtocol.Factory.TYPE);
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  private void assertGlobalFactoryBinding(IManagedContainerFactory factory, String productGroup, String type)
  {
    IManagedContainer globalContainer = IManagedContainer.INSTANCE;
    IFactory registeredFactory;

    try
    {
      registeredFactory = globalContainer.getFactory(productGroup, type);
    }
    catch (FactoryNotFoundException ex)
    {
      registeredFactory = null;
    }

    boolean registeredHere = registeredFactory == null;

    if (registeredHere)
    {
      globalContainer.registerFactory(factory);
      registeredFactory = globalContainer.getFactory(productGroup, type);
    }

    try
    {
      assertTrue(registeredFactory instanceof IManagedContainerFactory);
      assertSame(globalContainer, ((IManagedContainerFactory)registeredFactory).getManagedContainer());
    }
    finally
    {
      if (registeredHere)
      {
        globalContainer.unregisterFactory(factory);
      }
    }
  }

  @SuppressWarnings("restriction")
  public void testAdminFactoriesSeparateOwningAndRepositoriesContainers()
  {
    IManagedContainer repositoriesContainer = ContainerUtil.createInitializedContainer();
    IManagedContainer owningContainer = ContainerUtil.createInitializedContainer();

    try
    {
      CDOAdminServerUtil.prepareContainer(owningContainer, repositoriesContainer);
      CDOAdminServer.Factory serverFactory = (CDOAdminServer.Factory)owningContainer.getFactory(CDOAdminServer.Factory.PRODUCT_GROUP,
          CDOAdminServer.Factory.TYPE);
      CDOAdminServerProtocol.Factory protocolFactory = (CDOAdminServerProtocol.Factory)owningContainer.getFactory(ServerProtocolFactory.PRODUCT_GROUP,
          CDOAdminServerProtocol.Factory.TYPE);

      assertNotNull(serverFactory);
      assertNotNull(protocolFactory);
      assertSame(owningContainer, serverFactory.getManagedContainer());
      assertSame(owningContainer, protocolFactory.getManagedContainer());
      assertSame(repositoriesContainer, serverFactory.getContainer());
      assertSame(repositoriesContainer, protocolFactory.getContainer());

      CDOAdminServer admin = serverFactory.create(null);
      assertSame(repositoriesContainer, admin.getContainer());

      repositoriesContainer.registerFactory(new CDOAdminServer.Factory());
      CDOAdminServer.Factory.get(repositoriesContainer, null);

      CDOAdminServerProtocol protocol = protocolFactory.create(null);
      assertSame(repositoriesContainer, protocol.getContainer());

      owningContainer.unregisterFactory(serverFactory);
      owningContainer.unregisterFactory(protocolFactory);

      assertNull(serverFactory.getManagedContainer());
      assertNull(protocolFactory.getManagedContainer());
      assertSame(repositoriesContainer, serverFactory.getContainer());
      assertSame(repositoriesContainer, protocolFactory.getContainer());

      owningContainer.registerFactory(serverFactory);
      owningContainer.registerFactory(protocolFactory);

      assertSame(owningContainer, serverFactory.getManagedContainer());
      assertSame(owningContainer, protocolFactory.getManagedContainer());
      assertSame(repositoriesContainer, serverFactory.getContainer());
      assertSame(repositoriesContainer, protocolFactory.getContainer());

      owningContainer.unregisterFactory(serverFactory);
      owningContainer.unregisterFactory(protocolFactory);
    }
    finally
    {
      LifecycleUtil.deactivate(owningContainer);
      LifecycleUtil.deactivate(repositoriesContainer);
    }
  }

  public void testExplicitURLClassLoaderPreparesCDOContributions() throws Exception
  {
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    File root = findRepositoryRoot();
    URL[] urls = { new File(root, "plugins/org.eclipse.net4j.util").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.net4j.util/bin").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.net4j").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.net4j/bin").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.emf.cdo.net4j").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.emf.cdo.net4j/bin").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.emf.cdo.server.net4j").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.emf.cdo.server.net4j/bin").toURI().toURL() //$NON-NLS-1$
    };

    IManagedContainer container = null;

    try (URLClassLoader classLoader = new URLClassLoader(urls, oldClassLoader))
    {
      Thread.currentThread().setContextClassLoader(classLoader);
      assertNotNull(classLoader.getResource("META-INF/services/org.eclipse.net4j.util.container.IManagedContainerInitializer")); //$NON-NLS-1$
      container = OMPlatform.INSTANCE.createManagedContainer();

      assertNotNull(container.getFactory(ClientProtocolFactory.PRODUCT_GROUP, "cdo")); //$NON-NLS-1$
      assertNotNull(container.getFactory(ServerProtocolFactory.PRODUCT_GROUP, "cdo")); //$NON-NLS-1$
      assertNotNull(((CDOServerProtocolFactory)container.getFactory(ServerProtocolFactory.PRODUCT_GROUP, "cdo")).getRepositoryProvider()); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(container);
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
  }

  @SuppressWarnings("deprecation")
  public void testExplicitURLClassLoaderManualPreparationDoesNotDuplicateCDOContributions() throws Exception
  {
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    File root = findRepositoryRoot();
    URL[] urls = { new File(root, "plugins/org.eclipse.net4j.util").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.net4j.util/bin").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.net4j").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.net4j/bin").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.emf.cdo.net4j").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.emf.cdo.net4j/bin").toURI().toURL() //$NON-NLS-1$
    };

    IManagedContainer container = null;

    try (URLClassLoader classLoader = new URLClassLoader(urls, oldClassLoader))
    {
      Thread.currentThread().setContextClassLoader(classLoader);
      assertNotNull(classLoader.getResource("META-INF/services/org.eclipse.net4j.util.container.IManagedContainerInitializer")); //$NON-NLS-1$
      container = OMPlatform.INSTANCE.createManagedContainer();
      int factoryCount = container.getFactoryRegistry().size();
      CDONet4jUtil.prepareContainer(container);

      assertNotNull(container.getFactory(ClientProtocolFactory.PRODUCT_GROUP, "cdo")); //$NON-NLS-1$
      assertEquals(factoryCount, container.getFactoryRegistry().size());
    }
    finally
    {
      LifecycleUtil.deactivate(container);
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
  }

  private static File findRepositoryRoot() throws Exception
  {
    File root = new File(StandaloneManagedContainerTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();

    while (root != null
        && !new File(root, "plugins/org.eclipse.net4j/src/META-INF/services/org.eclipse.net4j.util.container.IManagedContainerInitializer").isFile()) //$NON-NLS-1$
    {
      root = root.getParentFile();
    }

    assertNotNull(root);
    return root;
  }
}
