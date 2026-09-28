/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.security.CustomResourceFilter;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.impl.ResourceFilterImpl;
import org.eclipse.emf.cdo.security.util.AuthorizationContext;
import org.eclipse.emf.cdo.server.internal.security.SecurityManager;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.ecore.EcoreFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Verifies the conservative built-in cacheability classification and subclass opt-in hook.
 *
 * @author Eike Stepper
 */
public class PermissionClassificationTest extends PlainTest
{
  private static final SecurityFactory SF = SecurityFactory.eINSTANCE;

  @SuppressWarnings("deprecation")
  public void testBuiltInResourceFiltersAndPermissions()
  {
    ExposedSecurityManager manager = new ExposedSecurityManager(false);
    Permission resourcePermission = SF.createResourcePermission("/data", Access.READ);
    PermissionFilter resourceFilter = SF.createResourceFilter("/data");
    Permission filterPermission = SF.createFilterPermission(Access.READ, resourceFilter);

    assertTrue(manager.isResourceCacheable(resourcePermission));
    assertTrue(manager.isResourceCacheable(filterPermission));
    assertTrue(manager.isResourceCacheable(SF.createAndFilter(resourceFilter, SF.createOrFilter(resourceFilter))));
    assertTrue(manager.isResourceCacheable(SF.createNotFilter(resourceFilter)));
    assertFalse(manager.isResourceCacheable(SF.createExpressionFilter()));
  }

  public void testEresourcePackageAndClassFilters()
  {
    ExposedSecurityManager manager = new ExposedSecurityManager(false);
    assertTrue(manager.isResourceCacheable(SF.createPackageFilter(EresourcePackage.eINSTANCE)));
    assertTrue(manager.isResourceCacheable(SF.createClassFilter(EresourcePackage.Literals.CDO_RESOURCE)));

    assertFalse(manager.isResourceCacheable(SF.createPackageFilter(EcoreFactory.eINSTANCE.createEPackage())));
    assertFalse(manager.isResourceCacheable(SF.createClassFilter(EcoreFactory.eINSTANCE.createEClass())));
  }

  public void testSubclassIsDynamicUnlessOptedIn()
  {
    ExposedSecurityManager manager = new ExposedSecurityManager(false);
    CustomResourceFilter filter = new CustomResourceFilter();
    Permission permission = SF.createFilterPermission(Access.READ, filter);

    assertFalse(manager.isResourceCacheable(filter));
    assertFalse(manager.isResourceCacheable(permission));

    ExposedSecurityManager optedInManager = new ExposedSecurityManager(true);
    assertTrue(optedInManager.isResourceCacheable(filter));
    assertTrue(optedInManager.isResourceCacheable(permission));
  }

  public void testAuthorizationContextRemainsDynamicBesideCachedBaseline() throws Exception
  {
    ExposedSecurityManager manager = new ExposedSecurityManager(false);
    AtomicInteger evaluations = new AtomicInteger();
    AtomicBoolean allowWrite = new AtomicBoolean(true);
    ContextFilter contextFilter = new ContextFilter(evaluations);
    Permission dynamicPermission = SF.createFilterPermission(Access.WRITE, contextFilter);
    assertFalse("A custom context-sensitive filter must remain dynamic by default", manager.isResourceCacheable(dynamicPermission));

    CDORevision revision = new CDORevisionImpl(EcoreFactory.eINSTANCE.createEClass());
    Map<String, CDOPermission> baselines = new HashMap<>();
    String cacheKey = CDOIDUtil.createLong(1) + ":false";
    baselines.put(cacheKey, CDOPermission.READ);

    try
    {
      AuthorizationContext.set(Collections.singletonMap("allowWrite", allowWrite));
      CDOPermission baselineA = baselines.get(cacheKey);
      assertEquals(CDOPermission.WRITE, applyDynamic(baselineA, dynamicPermission, revision));
      assertEquals(CDOPermission.READ, baselines.get(cacheKey));

      allowWrite.set(false);
      CDOPermission baselineB = baselines.get(cacheKey);
      assertEquals(CDOPermission.READ, applyDynamic(baselineB, dynamicPermission, revision));
      assertEquals(CDOPermission.READ, baselines.get(cacheKey));
      assertEquals("The same cached baseline was reused under both contexts", 2, evaluations.get());
    }
    finally
    {
      AuthorizationContext.set(null);
    }
  }

  private static CDOPermission applyDynamic(CDOPermission baseline, Permission permission, CDORevision revision)
  {
    return permission.isApplicable(revision, id -> null, null) ? CDOPermission.WRITE : baseline;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ContextFilter extends ResourceFilterImpl
  {
    private final AtomicInteger evaluations;

    public ContextFilter(AtomicInteger evaluations)
    {
      this.evaluations = evaluations;
    }

    @Override
    protected boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level)
    {
      evaluations.incrementAndGet();
      Map<String, Object> context = AuthorizationContext.get();
      Object value = context == null ? null : context.get("allowWrite");
      return value instanceof AtomicBoolean && ((AtomicBoolean)value).get();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ExposedSecurityManager extends SecurityManager
  {
    private final boolean optIn;

    public ExposedSecurityManager(boolean optIn)
    {
      super("security", IManagedContainer.INSTANCE);
      this.optIn = optIn;
    }

    /**
     * Must be elevated to public for the tests above!
     */
    @Override
    public boolean isResourceCacheable(Permission permission)
    {
      return super.isResourceCacheable(permission);
    }

    @Override
    protected boolean isResourceCacheable(PermissionFilter filter)
    {
      return optIn && filter.getClass() == CustomResourceFilter.class || super.isResourceCacheable(filter);
    }
  }
}
