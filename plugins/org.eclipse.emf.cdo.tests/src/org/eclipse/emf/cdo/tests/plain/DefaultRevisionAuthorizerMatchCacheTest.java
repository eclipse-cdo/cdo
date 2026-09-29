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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.DefaultMatchCache;
import org.eclipse.emf.cdo.internal.server.DefaultRepositoryProtector;
import org.eclipse.emf.cdo.internal.server.DefaultRevisionAuthorizer;
import org.eclipse.emf.cdo.internal.server.DefaultRevisionAuthorizer.KeyValueMatcher.RevisionFeature;
import org.eclipse.emf.cdo.internal.server.DefaultRevisionAuthorizer.Matcher;
import org.eclipse.emf.cdo.internal.server.DefaultRevisionAuthorizer.OperationMatcher.And;
import org.eclipse.emf.cdo.internal.server.DefaultRevisionAuthorizer.RefMatcher;
import org.eclipse.emf.cdo.internal.server.DefaultRevisionAuthorizer.ValueMatcher.RevisionClass;
import org.eclipse.emf.cdo.internal.server.DefaultRevisionAuthorizer.ValueMatcher.RevisionPackage;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserInfo;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.server.MatchCache;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.util.StringTester;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests caching of model-stable matcher results.
 *
 * @author Eike Stepper
 */
public class DefaultRevisionAuthorizerMatchCacheTest extends PlainTest
{
  public void testPositiveResultIsCached()
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingRevisionClass matcher = new CountingRevisionClass();
    matcher.setTest(StringTester.EQ);
    matcher.setValue(EcoreUtil.getURI(eClass).toString());
    DefaultRevisionAuthorizer authorizer = authorizer(matcher);

    assertEquals(CDOPermission.READ, authorizer.authorizeRevision(null, (UserInfo)null, null, null, revision(eClass)));
    assertEquals(CDOPermission.READ, authorizer.authorizeRevision(null, (UserInfo)null, null, null, revision(eClass)));
    assertEquals(1, matcher.evaluations.get());
  }

  public void testNegativeResultIsCached()
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingRevisionClass matcher = new CountingRevisionClass();
    matcher.setTest(StringTester.EQ);
    matcher.setValue("no-such-class"); //$NON-NLS-1$
    DefaultRevisionAuthorizer authorizer = authorizer(matcher);

    assertNull(authorizer.authorizeRevision(null, (UserInfo)null, null, null, revision(eClass)));
    assertNull(authorizer.authorizeRevision(null, (UserInfo)null, null, null, revision(eClass)));
    assertEquals(1, matcher.evaluations.get());
  }

  public void testDifferentClassesHaveDifferentEntries()
  {
    EClass classA = createEClass("ClassA"); //$NON-NLS-1$
    EClass classB = createEClass("ClassB"); //$NON-NLS-1$
    CountingRevisionClass matcher = new CountingRevisionClass();
    matcher.setTest(StringTester.EQ);
    matcher.setValue(EcoreUtil.getURI(classA).toString());
    DefaultRevisionAuthorizer authorizer = authorizer(matcher);

    assertTrue(matches(authorizer, classA));
    assertFalse(matches(authorizer, classB));
    assertFalse(matches(authorizer, classB));
    assertEquals(2, matcher.evaluations.get());
  }

  public void testDifferentMatcherInstancesHaveDifferentEntries()
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingRevisionClass matching = new CountingRevisionClass();
    matching.setTest(StringTester.EQ);
    matching.setValue(EcoreUtil.getURI(eClass).toString());
    CountingRevisionClass nonMatching = new CountingRevisionClass();
    nonMatching.setTest(StringTester.EQ);
    nonMatching.setValue("no-such-class"); //$NON-NLS-1$
    And and = new And();
    and.addArgument(matching);
    and.addArgument(nonMatching);
    DefaultRevisionAuthorizer authorizer = authorizer(and);
    matching.setRevisionAuthorizer(authorizer);
    nonMatching.setRevisionAuthorizer(authorizer);

    assertFalse(matches(authorizer, eClass));
    assertFalse(matches(authorizer, eClass));
    assertEquals(1, matching.evaluations.get());
    assertEquals(1, nonMatching.evaluations.get());
  }

  public void testPackageResultIsCachedByPackageIdentity()
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setNsURI("urn:test"); //$NON-NLS-1$
    EClass classA = createEClass("ClassA", ePackage); //$NON-NLS-1$
    EClass classB = createEClass("ClassB", ePackage); //$NON-NLS-1$
    CountingRevisionPackage matcher = new CountingRevisionPackage();
    matcher.setTest(StringTester.EQ);
    matcher.setValue("urn:test"); //$NON-NLS-1$
    DefaultRevisionAuthorizer authorizer = authorizer(matcher);

    assertTrue(matches(authorizer, classA));
    assertTrue(matches(authorizer, classB));
    assertEquals(1, matcher.evaluations.get());
  }

  public void testCompositeCachesStableChildrenAndEvaluatesDynamicChildren()
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingRevisionClass stableMatcher = new CountingRevisionClass();
    stableMatcher.setTest(StringTester.EQ);
    stableMatcher.setValue(EcoreUtil.getURI(eClass).toString());
    CountingDynamicMatcher dynamicMatcher = new CountingDynamicMatcher();
    And and = new And();
    and.addArgument(stableMatcher);
    and.addArgument(dynamicMatcher);
    DefaultRevisionAuthorizer authorizer = authorizer(and);
    stableMatcher.setRevisionAuthorizer(authorizer);
    dynamicMatcher.setRevisionAuthorizer(authorizer);

    assertTrue(matches(authorizer, eClass));
    assertTrue(matches(authorizer, eClass));
    assertEquals(1, stableMatcher.evaluations.get());
    assertEquals(2, dynamicMatcher.evaluations.get());
  }

  public void testRevisionFeatureRemainsUncached()
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingRevisionFeature matcher = new CountingRevisionFeature();
    matcher.setKey("missing"); //$NON-NLS-1$
    matcher.setTest(StringTester.EQ);
    matcher.setValue("value"); //$NON-NLS-1$
    DefaultRevisionAuthorizer authorizer = authorizer(matcher);

    assertFalse(matches(authorizer, eClass));
    assertFalse(matches(authorizer, eClass));
    assertEquals(2, matcher.evaluations.get());
  }

  public void testCacheEntriesDoNotCrossAuthorizerActivations() throws Exception
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingRevisionClass matcher = new CountingRevisionClass();
    matcher.setTest(StringTester.EQ);
    matcher.setValue(EcoreUtil.getURI(eClass).toString());
    CountingMatchCache cache = new CountingMatchCache();
    DefaultRevisionAuthorizer authorizer = authorizer(protector(cache), matcher);

    LifecycleUtil.activate(authorizer);
    assertTrue(matches(authorizer, eClass));
    assertEquals(1, matcher.evaluations.get());
    LifecycleUtil.deactivate(authorizer);

    matcher.setValue("no-such-class"); //$NON-NLS-1$
    LifecycleUtil.activate(authorizer);
    assertFalse(matches(authorizer, eClass));
    assertEquals(2, matcher.evaluations.get());
    assertEquals(2, cache.puts.get());
    LifecycleUtil.deactivate(authorizer);
  }

  public void testAuthorizersShareTheProtectorCache()
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingMatchCache cache = new CountingMatchCache();
    DefaultRepositoryProtector protector = protector(cache);
    CountingRevisionClass firstMatcher = revisionClassMatcher(eClass);
    CountingRevisionClass secondMatcher = revisionClassMatcher(eClass);
    DefaultRevisionAuthorizer first = authorizer(protector, firstMatcher);
    DefaultRevisionAuthorizer second = authorizer(protector, secondMatcher);

    assertTrue(matches(first, eClass));
    assertTrue(matches(first, eClass));
    assertTrue(matches(second, eClass));
    assertTrue(matches(second, eClass));
    assertEquals(1, firstMatcher.evaluations.get());
    assertEquals(1, secondMatcher.evaluations.get());
    assertEquals(2, cache.puts.get());
    assertEquals(4, cache.gets.get());
  }

  public void testRefMatcherUsesReferencedMatcherCache() throws Exception
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingMatchCache cache = new CountingMatchCache();
    DefaultRepositoryProtector protector = protector(cache);
    CountingRevisionClass targetMatcher = revisionClassMatcher(eClass);
    targetMatcher.setID("target"); //$NON-NLS-1$
    DefaultRevisionAuthorizer targetAuthorizer = authorizer(protector, targetMatcher);
    RefMatcher refMatcher = new RefMatcher();
    refMatcher.setRef("target"); //$NON-NLS-1$
    DefaultRevisionAuthorizer refAuthorizer = authorizer(protector, refMatcher);
    protector.addRevisionAuthorizer(targetAuthorizer);
    protector.addRevisionAuthorizer(refAuthorizer);

    try
    {
      LifecycleUtil.activate(targetAuthorizer);
      LifecycleUtil.activate(refAuthorizer);

      assertTrue(matches(refAuthorizer, eClass));
      assertTrue(matches(refAuthorizer, eClass));
      assertEquals(1, targetMatcher.evaluations.get());
      assertEquals(1, cache.puts.get());
    }
    finally
    {
      LifecycleUtil.deactivate(refAuthorizer);
      LifecycleUtil.deactivate(targetAuthorizer);
    }
  }

  public void testRevisionFeatureCachesNestedInstanceOfMatcher() throws Exception
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setNsURI("urn:test"); //$NON-NLS-1$
    EClass eClass = createEClass("ClassA", ePackage); //$NON-NLS-1$
    EAttribute feature = EcoreFactory.eINSTANCE.createEAttribute();
    feature.setName("name"); //$NON-NLS-1$
    feature.setEType(EcorePackage.Literals.ESTRING);
    eClass.getEStructuralFeatures().add(feature);

    CountingRevisionFeature matcher = new CountingRevisionFeature();
    matcher.setKey("name"); //$NON-NLS-1$
    matcher.setTest(StringTester.EQ);
    matcher.setValue("expected"); //$NON-NLS-1$
    matcher.setInstanceOf(EcoreUtil.getURI(eClass).toString());
    CountingMatchCache cache = new CountingMatchCache();
    DefaultRevisionAuthorizer authorizer = authorizer(protector(cache), matcher);
    CDORevision revision = revisionWithValue(eClass, feature, "expected"); //$NON-NLS-1$

    LifecycleUtil.activate(authorizer);
    try
    {
      assertTrue(authorizer.authorizeRevision(null, (UserInfo)null, null, null, revision) != null);
      assertTrue(authorizer.authorizeRevision(null, (UserInfo)null, null, null, revision) != null);
      assertEquals(2, matcher.evaluations.get());
      assertEquals(1, cache.puts.get());
      assertEquals(2, cache.gets.get());
    }
    finally
    {
      LifecycleUtil.deactivate(authorizer);
    }
  }

  public void testConfiguredCacheIsUsed()
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingRevisionClass matcher = new CountingRevisionClass();
    matcher.setTest(StringTester.EQ);
    matcher.setValue(EcoreUtil.getURI(eClass).toString());
    CountingMatchCache cache = new CountingMatchCache();
    DefaultRevisionAuthorizer authorizer = authorizer(protector(cache), matcher);

    assertTrue(matches(authorizer, eClass));
    assertTrue(matches(authorizer, eClass));
    assertEquals(1, matcher.evaluations.get());
    assertEquals(1, cache.puts.get());
    assertEquals(2, cache.gets.get());
  }

  public void testMissingCacheDoesNotBreakAuthorization()
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingRevisionClass matcher = new CountingRevisionClass();
    matcher.setTest(StringTester.EQ);
    matcher.setValue(EcoreUtil.getURI(eClass).toString());
    IRepositoryProtector protector = (IRepositoryProtector)Proxy.newProxyInstance(IRepositoryProtector.class.getClassLoader(),
        new Class<?>[] { IRepositoryProtector.class }, (proxy, method, arguments) -> null);
    DefaultRevisionAuthorizer authorizer = authorizer(protector, matcher);

    assertTrue(matches(authorizer, eClass));
    assertTrue(matches(authorizer, eClass));
    assertEquals(2, matcher.evaluations.get());
  }

  public void testDefaultCacheIsInstalledWhenProtectorActivates() throws Exception
  {
    TestRepositoryProtector protector = new TestRepositoryProtector();
    protector.setRepository(
        (IRepository)Proxy.newProxyInstance(IRepository.class.getClassLoader(), new Class<?>[] { IRepository.class }, (proxy, method, arguments) -> null));
    protector.setUserAuthenticator(new TestUserAuthenticator());
    protector.initialize();

    assertEquals(1, protector.properties().values().stream().filter(value -> value instanceof MatchCache).count());
    assertTrue(protector.properties().values().stream().anyMatch(value -> value instanceof DefaultMatchCache));
  }

  public void testMatcherCacheAwareDispatchIsPubliclyCallable()
  {
    EClass eClass = createEClass("ClassA"); //$NON-NLS-1$
    CountingRevisionClass matcher = new CountingRevisionClass();
    matcher.setTest(StringTester.EQ);
    matcher.setValue(EcoreUtil.getURI(eClass).toString());
    authorizer(matcher);

    assertTrue(matcher.matchesCached(null, null, null, null, revision(eClass)));
  }

  private static boolean matches(DefaultRevisionAuthorizer authorizer, EClass eClass)
  {
    return authorizer.authorizeRevision(null, (UserInfo)null, null, null, revision(eClass)) != null;
  }

  private static DefaultRevisionAuthorizer authorizer(Matcher matcher)
  {
    return authorizer(protector(new DefaultMatchCache()), matcher);
  }

  private static DefaultRevisionAuthorizer authorizer(IRepositoryProtector protector, Matcher matcher)
  {
    DefaultRevisionAuthorizer authorizer = new DefaultRevisionAuthorizer();
    authorizer.setPermission(CDOPermission.READ);
    authorizer.setRepositoryProtector(protector);
    authorizer.setMatcher(matcher);
    matcher.setRevisionAuthorizer(authorizer);
    return authorizer;
  }

  private static CountingRevisionClass revisionClassMatcher(EClass eClass)
  {
    CountingRevisionClass matcher = new CountingRevisionClass();
    matcher.setTest(StringTester.EQ);
    matcher.setValue(EcoreUtil.getURI(eClass).toString());
    return matcher;
  }

  private static DefaultRepositoryProtector protector(MatchCache matchCache)
  {
    DefaultRepositoryProtector protector = new DefaultRepositoryProtector();
    protector.setMatchCache(matchCache);
    return protector;
  }

  private static CDORevision revision(EClass eClass)
  {
    return new CDORevisionImpl(eClass);
  }

  private static CDORevision revisionWithValue(EClass eClass, EAttribute feature, Object value)
  {
    CDORevisionImpl revision = new CDORevisionImpl(eClass);
    revision.setValue(feature, value);
    return revision;
  }

  private static EClass createEClass(String name)
  {
    return createEClass(name, EcoreFactory.eINSTANCE.createEPackage());
  }

  private static EClass createEClass(String name, EPackage ePackage)
  {
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName(name);
    ePackage.getEClassifiers().add(eClass);
    return eClass;
  }

  /**
   * @author Eike Stepper
   */
  private static class CountingRevisionClass extends RevisionClass
  {
    private final AtomicInteger evaluations = new AtomicInteger();

    public CountingRevisionClass()
    {
    }

    @Override
    public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
    {
      evaluations.incrementAndGet();
      return super.matches(session, userInfo, securityContext, revisionProvider, revision);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CountingRevisionPackage extends RevisionPackage
  {
    private final AtomicInteger evaluations = new AtomicInteger();

    public CountingRevisionPackage()
    {
    }

    @Override
    public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
    {
      evaluations.incrementAndGet();
      return super.matches(session, userInfo, securityContext, revisionProvider, revision);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CountingDynamicMatcher extends Matcher
  {
    private final AtomicInteger evaluations = new AtomicInteger();

    public CountingDynamicMatcher()
    {
    }

    @Override
    public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
    {
      evaluations.incrementAndGet();
      return true;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CountingRevisionFeature extends RevisionFeature
  {
    private final AtomicInteger evaluations = new AtomicInteger();

    public CountingRevisionFeature()
    {
    }

    @Override
    public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
    {
      evaluations.incrementAndGet();
      return super.matches(session, userInfo, securityContext, revisionProvider, revision);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CountingMatchCache implements MatchCache
  {
    private final Map<Object, Boolean> values = new HashMap<>();

    private final AtomicInteger gets = new AtomicInteger();

    private final AtomicInteger puts = new AtomicInteger();

    public CountingMatchCache()
    {
    }

    @Override
    public synchronized Boolean get(Object key)
    {
      gets.incrementAndGet();
      return values.get(key);
    }

    @Override
    public synchronized void put(Object key, boolean match)
    {
      puts.incrementAndGet();
      values.put(key, Boolean.valueOf(match));
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestRepositoryProtector extends DefaultRepositoryProtector
  {
    public TestRepositoryProtector()
    {
    }

    public void initialize() throws Exception
    {
      doBeforeActivate();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestUserAuthenticator extends IRepositoryProtector.UserAuthenticator
  {
    public TestUserAuthenticator()
    {
    }

    @Override
    public UserInfo authenticateUser(String userID, char[] password)
    {
      return null;
    }
  }
}
