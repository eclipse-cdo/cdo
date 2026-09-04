/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.server.internal.net4j.protocol.CDOServerProtocolFactory;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.spi.net4j.ClientProtocolFactory;
import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Focused coverage for standalone managed-container provider discovery.
 *
 * @author Eike Stepper
 */
public class StandaloneManagedContainerTest extends AbstractCDOTest
{
  public void testServiceLoaderPreparesCDOContributions() throws Exception
  {
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    File root = findRepositoryRoot();
    URL[] urls = {
        new File(root, "plugins/org.eclipse.net4j.util").toURI().toURL(), //$NON-NLS-1$
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
      container.activate();

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

  public void testManualPreparationDoesNotDuplicateCDOContributions() throws Exception
  {
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    File root = findRepositoryRoot();
    URL[] urls = {
        new File(root, "plugins/org.eclipse.net4j.util").toURI().toURL(), //$NON-NLS-1$
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
      container.activate();
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

    while (root != null && !new File(root, "plugins/org.eclipse.net4j/META-INF/services/org.eclipse.net4j.util.container.IManagedContainerInitializer").isFile()) //$NON-NLS-1$
    {
      root = root.getParentFile();
    }

    assertNotNull(root);
    return root;
  }
}
