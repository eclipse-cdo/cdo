/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server.security;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.impl.ResourceFilterImpl;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.internal.security.SecurityManager;
import org.eclipse.emf.cdo.server.spi.security.PermissionCache;

import org.eclipse.net4j.util.container.IManagedContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

/**
 * Exercises invalidation while a real read authorization computes a baseline in an old generation.
 */
public class PermissionCacheGenerationRaceTest extends TestCase
{
  public void testInvalidatedInFlightAuthorizationRetriesInNewGeneration() throws Exception
  {
    CountDownLatch baselineStarted = new CountDownLatch(1);
    CountDownLatch resumeBaseline = new CountDownLatch(1);
    AtomicInteger baselineEvaluations = new AtomicInteger();
    BlockingFilter filter = new BlockingFilter(baselineStarted, resumeBaseline, baselineEvaluations);
    SecurityFactory factory = SecurityFactory.eINSTANCE;
    Permission permission = factory.createFilterPermission(Access.READ, filter);
    Role role = factory.createRole();
    role.getPermissions().add(permission);
    User user = factory.createUser();
    user.setId("race-user");
    user.getRoles().add(role);

    TestSecurityManager manager = new TestSecurityManager();
    CountingCreator creator = new CountingCreator();
    manager.setPermissionCacheCreator(creator);
    Object userInfo = createUserInfo(manager, user);
    CDOBranch branch = new CDOBranchImpl(null, 1, "race", null);
    putUserInfo(manager, user.getId(), userInfo);
    CDORevision revision = new CDORevisionImpl(EresourcePackage.Literals.CDO_RESOURCE);
    ((CDORevisionImpl)revision).setID(CDOIDUtil.createLong(99));
    CDOBranchPoint branchPoint = branchPoint(branch);
    ISession session = session(user.getId());
    AtomicReference<CDOPermission> authorizationResult = new AtomicReference<>();
    AtomicReference<Throwable> threadFailure = new AtomicReference<>();

    Thread authorization = new Thread(() -> {
      try
      {
        authorizationResult.set(authorize(manager, revision, branchPoint, session));
      }
      catch (Throwable ex)
      {
        threadFailure.set(ex);
      }
    }, "permission-cache-authorization-A");

    authorization.start();
    assertTrue("Authorization A must enter baseline evaluation in generation G", baselineStarted.await(10, TimeUnit.SECONDS));

    PermissionCache oldGeneration = getCache(userInfo, branch, creator);
    assertNotNull(oldGeneration);
    removeCache(userInfo, branch);
    PermissionCache generationB = getCache(userInfo, branch, creator);
    assertNotSame("Invalidation must make generation G unreachable", oldGeneration, generationB);
    assertEquals("The next cache access must create exactly one replacement generation", 2, creator.creations.get());

    resumeBaseline.countDown();
    authorization.join(TimeUnit.SECONDS.toMillis(10));
    assertFalse("Authorization A must finish after its baseline resumes", authorization.isAlive());
    if (threadFailure.get() != null)
    {
      throw new AssertionError("Authorization A failed", threadFailure.get());
    }

    assertEquals(CDOPermission.READ, authorizationResult.get());
    assertEquals("The resource-safe permission is evaluated once for G and again for G2", 2, baselineEvaluations.get());
    assertEquals("Only the new generation contains the final authorization baseline", CDOPermission.READ, generationB.get(revision.getID(), true));
    assertEquals("The completed old computation can only populate G", CDOPermission.READ, oldGeneration.get(revision.getID(), true));
    assertSame("Authorization must finish with G2 still current", generationB, getCache(userInfo, branch, creator));
  }

  private static CDOPermission authorize(SecurityManager manager, CDORevision revision, CDOBranchPoint branchPoint, ISession session) throws Exception
  {
    Method method = SecurityManager.class.getDeclaredMethod("authorizeRead", CDORevision.class, CDORevisionProvider.class, CDOBranchPoint.class,
        ISession.class);
    method.setAccessible(true);
    return (CDOPermission)method.invoke(manager, revision, null, branchPoint, session);
  }

  private static Object createUserInfo(SecurityManager manager, User user) throws Exception
  {
    Class<?> userInfoClass = Class.forName(SecurityManager.class.getName() + "$UserInfo");
    Constructor<?> constructor = userInfoClass.getDeclaredConstructor(SecurityManager.class, User.class);
    constructor.setAccessible(true);
    return constructor.newInstance(manager, user);
  }

  private static void putUserInfo(SecurityManager manager, String userID, Object userInfo) throws Exception
  {
    Field field = SecurityManager.class.getDeclaredField("userInfos");
    field.setAccessible(true);
    @SuppressWarnings("unchecked")
    Map<String, Object> userInfos = (Map<String, Object>)field.get(manager);
    userInfos.put(userID, userInfo);
  }

  private static PermissionCache getCache(Object userInfo, CDOBranch branch, PermissionCache.Creator creator) throws Exception
  {
    Method method = userInfo.getClass().getDeclaredMethod("getPermissionCache", IRepository.class, CDOBranch.class, PermissionCache.Creator.class);
    method.setAccessible(true);
    return (PermissionCache)method.invoke(userInfo, null, branch, creator);
  }

  private static void removeCache(Object userInfo, CDOBranch branch) throws Exception
  {
    Method method = userInfo.getClass().getDeclaredMethod("removePermissionCache", CDOBranch.class);
    method.setAccessible(true);
    method.invoke(userInfo, branch);
  }

  private static CDOBranchPoint branchPoint(CDOBranch branch)
  {
    return (CDOBranchPoint)Proxy.newProxyInstance(CDOBranchPoint.class.getClassLoader(), new Class<?>[] { CDOBranchPoint.class }, (proxy, method, args) -> {
      if ("getBranch".equals(method.getName()))
      {
        return branch;
      }
      if ("getTimeStamp".equals(method.getName()))
      {
        return CDOBranchPoint.UNSPECIFIED_DATE;
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
    });
  }

  private static ISession session(String userID)
  {
    return (ISession)Proxy.newProxyInstance(ISession.class.getClassLoader(), new Class<?>[] { ISession.class }, (proxy, method, args) -> {
      if ("getUserID".equals(method.getName()))
      {
        return userID;
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
    });
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestSecurityManager extends SecurityManager
  {
    public TestSecurityManager()
    {
      super("security", IManagedContainer.INSTANCE);
    }

    @Override
    protected boolean isResourceCacheable(PermissionFilter filter)
    {
      return filter.getClass() == BlockingFilter.class || super.isResourceCacheable(filter);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class BlockingFilter extends ResourceFilterImpl
  {
    private final CountDownLatch baselineStarted;

    private final CountDownLatch resumeBaseline;

    private final AtomicInteger evaluations;

    public BlockingFilter(CountDownLatch baselineStarted, CountDownLatch resumeBaseline, AtomicInteger evaluations)
    {
      this.baselineStarted = baselineStarted;
      this.resumeBaseline = resumeBaseline;
      this.evaluations = evaluations;
    }

    @Override
    protected boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception
    {
      if (evaluations.incrementAndGet() == 1)
      {
        baselineStarted.countDown();

        if (!resumeBaseline.await(10, TimeUnit.SECONDS))
        {
          throw new AssertionError("Timed out waiting to resume baseline evaluation");
        }
      }

      return true;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CountingCreator implements PermissionCache.Creator
  {
    private final AtomicInteger creations = new AtomicInteger();

    public CountingCreator()
    {
    }

    @Override
    public PermissionCache create(IRepository repository, String userID, CDOBranch branch)
    {
      creations.incrementAndGet();
      return new TestCache();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestCache implements PermissionCache
  {
    private final Map<CDOID, CDOPermission> values = new ConcurrentHashMap<>();

    public TestCache()
    {
    }

    @Override
    public CDOPermission get(CDOID resourceNodeID, boolean resourceNode)
    {
      return values.get(resourceNodeID);
    }

    @Override
    public void put(CDOID resourceNodeID, boolean resourceNode, CDOPermission permission)
    {
      values.put(resourceNodeID, permission);
    }
  }
}
