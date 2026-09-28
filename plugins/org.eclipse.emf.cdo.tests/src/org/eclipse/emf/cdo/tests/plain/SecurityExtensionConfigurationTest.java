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
import org.eclipse.emf.cdo.server.internal.security.SecurityExtension;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.server.spi.security.PermissionCache;
import org.eclipse.emf.cdo.server.spi.security.PermissionCacheFactory;
import org.eclipse.emf.cdo.server.spi.security.SecurityManagerFactory;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests permission-cache settings through the real SecurityExtension XML configuration path.
 *
 * @author Eike Stepper
 */
public class SecurityExtensionConfigurationTest extends PlainTest
{
  public void testNoChildDefaultsDuringSecurityManagerResolution() throws Exception
  {
    Fixture fixture = configure("<securityManager type=\"default\" description=\"/security\"/>");
    assertFalse(fixture.properties.containsKey(PermissionCacheFactory.PROP_TYPE));
    assertFalse(fixture.properties.containsKey(PermissionCacheFactory.PROP_DESCRIPTION));
    assertTrue(fixture.resolveManager().getPermissionCacheCreator() instanceof DefaultPermissionCacheCreator);
  }

  public void testEmptyChildResolvesDefaultType() throws Exception
  {
    Fixture fixture = configure("<securityManager type=\"default\" description=\"/security\"><permissionCache/></securityManager>");
    assertEquals(PermissionCacheFactory.Default.TYPE, fixture.properties.get(PermissionCacheFactory.PROP_TYPE));
    assertTrue(fixture.resolveManager().getPermissionCacheCreator() instanceof DefaultPermissionCacheCreator);
  }

  public void testExplicitDefaultResolvesDefaultType() throws Exception
  {
    Fixture fixture = configure("<securityManager type=\"default\" description=\"/security\"><permissionCache type=\"default\"/></securityManager>");
    assertEquals(PermissionCacheFactory.Default.TYPE, fixture.properties.get(PermissionCacheFactory.PROP_TYPE));
    assertTrue(fixture.resolveManager().getPermissionCacheCreator() instanceof DefaultPermissionCacheCreator);
  }

  public void testCustomTypeAndDescriptionReachFactory() throws Exception
  {
    Fixture fixture = configure(
        "<securityManager type=\"default\" description=\"/security\"><permissionCache type=\"testCache\" description=\"foo\"/></securityManager>");
    assertEquals("testCache", fixture.properties.get(PermissionCacheFactory.PROP_TYPE));
    assertEquals("foo", fixture.properties.get(PermissionCacheFactory.PROP_DESCRIPTION));
    InternalSecurityManager manager = fixture.resolveManager();
    assertSame(fixture.customFactory.creator, manager.getPermissionCacheCreator());
    assertEquals("repo", fixture.customFactory.repositoryName);
    assertEquals("foo", fixture.customFactory.description);
  }

  public void testDuplicatePermissionCacheChildrenAreRejected() throws Exception
  {
    Fixture fixture = new Fixture();
    try
    {
      fixture.configure("<securityManager type=\"default\" description=\"/security\"><permissionCache/><permissionCache/></securityManager>");
      fail("Expected duplicate permissionCache children to be rejected");
    }
    catch (IllegalStateException expected)
    {
      assertTrue(expected.getMessage().contains("maximum of one permissionCache"));
    }
  }

  public void testLegacySecurityManagerDescriptionIsPassedUnchanged() throws Exception
  {
    Fixture fixture = configure("<securityManager type=\"default\" description=\"/security:annotation:home(/home)\"/>");
    assertFalse(fixture.properties.containsKey(PermissionCacheFactory.PROP_TYPE));
    assertEquals("repo:/security:annotation:home(/home)", fixture.container.securityManagerDescription);
  }

  private static Fixture configure(String securityManager) throws Exception
  {
    Fixture fixture = new Fixture();
    fixture.configure(securityManager);
    return fixture;
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
        return "repo";
      }
      if (method.getReturnType() == boolean.class)
      {
        return false;
      }
      if (method.getReturnType() == int.class)
      {
        return 0;
      }
      if (method.getReturnType() == long.class)
      {
        return 0L;
      }
      return null;
    };
    return (InternalRepository)Proxy.newProxyInstance(InternalRepository.class.getClassLoader(), new Class<?>[] { InternalRepository.class }, handler);
  }

  /**
   * @author Eike Stepper
   */
  private static final class Fixture
  {
    private final Map<String, String> properties = new HashMap<>();

    private final RecordingFactory customFactory = new RecordingFactory();

    private final TestContainer container = new TestContainer(this);

    private final InternalRepository repository = repository(properties);

    public Fixture()
    {
    }

    private void configure(String securityManager) throws Exception
    {
      String xml = "<cdoServer><repository name=\"repo\">" + securityManager + "</repository></cdoServer>";
      TestExtension extension = new TestExtension();
      extension.setManagedContainer(container.proxy());
      extension.configure(xml);
    }

    private InternalSecurityManager resolveManager()
    {
      InternalSecurityManager manager = (InternalSecurityManager)SecurityManagerUtil.createSecurityManager("/security", container.proxy());
      manager.setRepository(repository);
      return manager;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestExtension extends SecurityExtension
  {
    public TestExtension()
    {
    }

    public void configure(String xml) throws Exception
    {
      start(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))));
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestContainer implements InvocationHandler
  {
    private final Fixture fixture;

    private String securityManagerDescription;

    public TestContainer(Fixture fixture)
    {
      this.fixture = fixture;
    }

    public IManagedContainer proxy()
    {
      return (IManagedContainer)Proxy.newProxyInstance(IManagedContainer.class.getClassLoader(), new Class<?>[] { IManagedContainer.class }, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      if ("getElement".equals(method.getName()))
      {
        String group = (String)args[0];
        String type = (String)args[1];
        String description = (String)args[2];

        if (RepositoryFactory.PRODUCT_GROUP.equals(group))
        {
          return fixture.repository;
        }

        if (SecurityManagerFactory.PRODUCT_GROUP.equals(group))
        {
          securityManagerDescription = description;
          return null;
        }

        if (PermissionCacheFactory.PRODUCT_GROUP.equals(group))
        {
          if (PermissionCacheFactory.Default.TYPE.equals(type))
          {
            return new PermissionCacheFactory.Default().create(description);
          }

          if (fixture.customFactory.getType().equals(type))
          {
            return fixture.customFactory.create(description);
          }
        }
      }

      return null;
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
      super("testCache");
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
