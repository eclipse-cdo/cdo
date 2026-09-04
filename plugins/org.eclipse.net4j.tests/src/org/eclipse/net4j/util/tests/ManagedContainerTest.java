/*
 * Copyright (c) 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.internal.util.container.PluginContainer;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.security.RandomizerFactory;
import org.eclipse.internal.net4j.buffer.BufferPoolFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Eike Stepper
 */
public class ManagedContainerTest extends AbstractOMTest
{
  public void testCanonicalGlobalContainer()
  {
    assertNotNull(IManagedContainer.INSTANCE);
    assertSame(IManagedContainer.INSTANCE, IManagedContainer.INSTANCE);
    assertTrue(IManagedContainer.INSTANCE.isActive());
  }

  public void testCountElements() throws Exception
  {
    IManagedContainer container = ContainerUtil.createContainer();
    ContainerUtil.prepareContainer(container);
    container.activate();
    assertEquals(0, container.countElements(RandomizerFactory.PRODUCT_GROUP));
    assertEquals(0, container.countElements(RandomizerFactory.PRODUCT_GROUP, RandomizerFactory.TYPE));

    Object element = container.getElement(RandomizerFactory.PRODUCT_GROUP, RandomizerFactory.TYPE, null);
    assertNotNull(element);
    assertEquals(1, container.countElements(RandomizerFactory.PRODUCT_GROUP));
    assertEquals(1, container.countElements(RandomizerFactory.PRODUCT_GROUP, RandomizerFactory.TYPE));
  }

  public void testPlatformFactoryCreatesIndependentContainers()
  {
    IManagedContainer first = OMPlatform.INSTANCE.createManagedContainer();
    IManagedContainer second = OMPlatform.INSTANCE.createManagedContainer();

    try
    {
      assertNotSame(first, second);
      assertNotSame(IManagedContainer.INSTANCE, first);
      assertNotSame(IManagedContainer.INSTANCE, second);
    }
    finally
    {
      LifecycleUtil.deactivate(first);
      LifecycleUtil.deactivate(second);
    }
  }

  public void testPrivatePluginContainerRemainsIndependent()
  {
    IManagedContainer container = ContainerUtil.createPluginContainer();

    assertNotSame(IManagedContainer.INSTANCE, container);
    LifecycleUtil.deactivate(container);
  }

  public void testStandaloneServiceLoaderPreparesNet4jBundle() throws Exception
  {
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    File root = findRepositoryRoot();
    URL[] urls = {
        new File(root, "plugins/org.eclipse.net4j.util").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.net4j.util/bin").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.net4j").toURI().toURL(), //$NON-NLS-1$
        new File(root, "plugins/org.eclipse.net4j/bin").toURI().toURL() //$NON-NLS-1$
    };
    IManagedContainer container = null;

    try (URLClassLoader classLoader = new URLClassLoader(urls, oldClassLoader))
    {
      Thread.currentThread().setContextClassLoader(classLoader);
      assertNotNull(classLoader.getResource("META-INF/services/org.eclipse.net4j.util.container.IManagedContainerInitializer")); //$NON-NLS-1$
      container = OMPlatform.INSTANCE.createManagedContainer();
      container.activate();
      assertNotNull(container.getFactory(BufferPoolFactory.PRODUCT_GROUP, BufferPoolFactory.TYPE));
      assertNotNull(container.getElement(BufferPoolFactory.PRODUCT_GROUP, BufferPoolFactory.TYPE, null));
      assertEquals(1, container.countElements(BufferPoolFactory.PRODUCT_GROUP, BufferPoolFactory.TYPE));

      Net4jUtil.prepareContainer(container);
      assertEquals(1, container.countElements(BufferPoolFactory.PRODUCT_GROUP, BufferPoolFactory.TYPE));
    }
    finally
    {
      LifecycleUtil.deactivate(container);
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
  }

  private static File findRepositoryRoot() throws Exception
  {
    File root = new File(ManagedContainerTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();

    while (root != null && !new File(root, "plugins/org.eclipse.net4j/META-INF/services/org.eclipse.net4j.util.container.IManagedContainerInitializer").isFile()) //$NON-NLS-1$
    {
      root = root.getParentFile();
    }

    assertNotNull(root);
    return root;
  }

  @SuppressWarnings({ "deprecation", "restriction" })
  public void testPluginContainerCompatibilityAccessor()
  {
    if (OMPlatform.INSTANCE.isOSGiRunning())
    {
      assertSame(IManagedContainer.INSTANCE, PluginContainer.getInstance());
    }
  }
}
