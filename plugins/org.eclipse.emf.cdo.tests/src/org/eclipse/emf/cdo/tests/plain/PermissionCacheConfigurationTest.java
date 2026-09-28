/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.server.internal.security.DefaultPermissionCacheCreator;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.server.spi.security.PermissionCache;
import org.eclipse.emf.cdo.server.spi.security.PermissionCacheFactory;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests central permission-cache creator resolution for programmatically-created security managers.
 *
 * @author Eike Stepper
 */
public class PermissionCacheConfigurationTest extends PlainTest
{
  public void testMissingAndExplicitDefaultType()
  {
    assertTrue(resolve(new HashMap<>(), FactoryContainer.proxy(new PermissionCacheFactory.Default()))
        .getPermissionCacheCreator() instanceof DefaultPermissionCacheCreator);

    Map<String, String> properties = new HashMap<>();
    properties.put(PermissionCacheFactory.PROP_TYPE, PermissionCacheFactory.Default.TYPE);
    assertTrue(
        resolve(properties, FactoryContainer.proxy(new PermissionCacheFactory.Default())).getPermissionCacheCreator() instanceof DefaultPermissionCacheCreator);
  }

  public void testCustomFactoryReceivesDescription()
  {
    Map<String, String> properties = new HashMap<>();
    properties.put(PermissionCacheFactory.PROP_TYPE, "custom");
    properties.put(PermissionCacheFactory.PROP_DESCRIPTION, "foo");
    RecordingFactory factory = new RecordingFactory();
    InternalSecurityManager securityManager = resolve(properties, FactoryContainer.proxy(factory));

    assertSame(factory.creator, securityManager.getPermissionCacheCreator());
    assertEquals("repository", factory.repositoryName);
    assertEquals("foo", factory.description);
  }

  public void testUnknownTypeFails()
  {
    Map<String, String> properties = new HashMap<>();
    properties.put(PermissionCacheFactory.PROP_TYPE, "missing");

    try
    {
      resolve(properties, FactoryContainer.proxy(null));
      fail("Expected an unknown explicit cache type to fail");
    }
    catch (IllegalStateException expected)
    {
      assertTrue(expected.getMessage().contains("missing"));
    }
  }

  public void testExplicitCreatorTakesPrecedence()
  {
    Map<String, String> properties = new HashMap<>();
    properties.put(PermissionCacheFactory.PROP_TYPE, "unknown");
    PermissionCache.Creator explicit = (repository, userID, branch) -> new EmptyCache();
    InternalSecurityManager manager = (InternalSecurityManager)SecurityManagerUtil.createSecurityManager("security", FactoryContainer.proxy(null));
    manager.setPermissionCacheCreator(explicit);
    manager.setRepository(repository(properties));

    assertSame(explicit, manager.getPermissionCacheCreator());
  }

  public void testInvalidDefaultCapacitiesFail()
  {
    PermissionCacheFactory.Default factory = new PermissionCacheFactory.Default();
    for (String capacity : new String[] { "0", "-1", "bad" })
    {
      try
      {
        factory.create("repository:|capacity=" + capacity);
        fail("Expected invalid capacity to fail: " + capacity);
      }
      catch (ProductCreationException expected)
      {
        // Expected.
      }
    }
  }

  private static InternalSecurityManager resolve(Map<String, String> properties, IManagedContainer container)
  {
    InternalSecurityManager manager = (InternalSecurityManager)SecurityManagerUtil.createSecurityManager("security", container);
    manager.setRepository(repository(properties));
    return manager;
  }

  private static InternalRepository repository(Map<String, String> properties)
  {
    InvocationHandler handler = (proxy, method, args) -> {
      if ("getProperties".equals(method.getName()))
      {
        return properties;
      }
      if ("getName".equals(method.getName()))
      {
        return "repository";
      }
      return defaultValue(method.getReturnType());
    };
    return (InternalRepository)Proxy.newProxyInstance(InternalRepository.class.getClassLoader(), new Class<?>[] { InternalRepository.class }, handler);
  }

  private static Object defaultValue(Class<?> type)
  {
    if (!type.isPrimitive())
    {
      return null;
    }
    if (type == boolean.class)
    {
      return false;
    }
    if (type == char.class)
    {
      return '\0';
    }
    return 0;
  }

  /**
   * @author Eike Stepper
   */
  private static class FactoryContainer implements InvocationHandler
  {
    private final PermissionCacheFactory factory;

    public FactoryContainer(PermissionCacheFactory factory)
    {
      this.factory = factory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      if ("getElement".equals(method.getName()))
      {
        String type = (String)args[1];
        if (factory == null || !factory.getType().equals(type))
        {
          return null;
        }
        return factory.create((String)args[2]);
      }
      return defaultValue(method.getReturnType());
    }

    public static IManagedContainer proxy(PermissionCacheFactory factory)
    {
      return (IManagedContainer)Proxy.newProxyInstance(IManagedContainer.class.getClassLoader(), new Class<?>[] { IManagedContainer.class },
          new FactoryContainer(factory));
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class RecordingFactory extends PermissionCacheFactory
  {
    private String repositoryName;

    private String description;

    private PermissionCache.Creator creator;

    public RecordingFactory()
    {
      super("custom");
    }

    @Override
    protected PermissionCache.Creator create(String repositoryName, String description)
    {
      this.repositoryName = repositoryName;
      this.description = description;
      creator = (repository, userID, branch) -> new EmptyCache();
      return creator;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class EmptyCache implements PermissionCache
  {
    public EmptyCache()
    {
    }

    @Override
    public CDOPermission get(CDOID resourceNodeID, boolean resourceNode)
    {
      return null;
    }

    @Override
    public void put(CDOID resourceNodeID, boolean resourceNode, CDOPermission permission)
    {
    }
  }
}
