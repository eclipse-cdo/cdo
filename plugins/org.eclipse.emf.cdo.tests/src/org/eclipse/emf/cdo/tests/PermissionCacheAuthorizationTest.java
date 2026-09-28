/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.server.security.CustomResourceFilter;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.impl.ResourceFilterImpl;
import org.eclipse.emf.cdo.security.util.AuthorizationContext;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.server.spi.security.PermissionCache;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.security.PasswordCredentials;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exercises resource permission caching through regular server read authorization.
 *
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "TEST_SECURITY_MANAGER")
@CleanRepositoriesAfter(reason = "TEST_SECURITY_MANAGER")
public class PermissionCacheAuthorizationTest extends AbstractCDOTest
{
  private static final PasswordCredentials CREDENTIALS = new PasswordCredentials("CacheUser", "secret");

  private final CountingCreator creator = new CountingCreator();

  private InternalSecurityManager securityManager;

  private final AtomicInteger commitHandler2Calls = new AtomicInteger();

  private String resourcePath;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    resourcePath = getResourcePath("permission-cache");
    InternalSecurityManager manager = (InternalSecurityManager)SecurityManagerUtil.createSecurityManager("/security", getServerContainer());
    manager.setPermissionCacheCreator(creator);
    securityManager = manager;
    getTestProperties().put(RepositoryConfig.PROP_TEST_SECURITY_MANAGER, manager);
    manager.addCommitHandler(new InternalSecurityManager.CommitHandler2()
    {
      @Override
      public void init(InternalSecurityManager securityManager, boolean firstTime)
      {
      }

      @Override
      public void handleCommit(InternalSecurityManager securityManager, CommitContext commitContext, User user)
      {
      }

      @Override
      public void handleCommitted(InternalSecurityManager securityManager, CommitContext commitContext)
      {
        commitHandler2Calls.incrementAndGet();
      }
    });

    getRepository();

    manager.modify(realm -> {
      Role role = realm.addRole("Resource Access");
      role.getPermissions().add(SecurityFactory.eINSTANCE.createFilterPermission(Access.READ,
          SecurityFactory.eINSTANCE.createResourceFilter(resourcePath, PatternStyle.EXACT, true)));
      role.getPermissions()
          .add(SecurityFactory.eINSTANCE.createFilterPermission(Access.WRITE, SecurityFactory.eINSTANCE.createClassFilter(getModel1Package().getCategory())));
      role.getPermissions()
          .add(SecurityFactory.eINSTANCE.createFilterPermission(Access.WRITE,
              SecurityFactory.eINSTANCE.createAndFilter(SecurityFactory.eINSTANCE.createClassFilter(EresourcePackage.Literals.CDO_RESOURCE_NODE),
                  SecurityFactory.eINSTANCE.createResourceFilter("/", PatternStyle.TREE, true))));
      realm.setDefaultAccess(Access.WRITE);
      User user = realm.addUser(CREDENTIALS);
      user.getRoles().add(role);
    });

  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testMultipleObjectsInOneResourceShareContainedSlot() throws Exception
  {
    try (CDOSession session = openSession(CREDENTIALS))
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(resourcePath);
      Category root = getModel1Factory().createCategory();

      for (int i = 0; i < 20; i++)
      {
        root.getCategories().add(getModel1Factory().createCategory());
      }

      root.getProducts().add(getModel1Factory().createProduct1());
      resource.getContents().add(root);
      CDOCommitInfo dataCommit = transaction.commit();
      transaction.close();

      securityManager.modify(realm -> realm.setDefaultAccess(null));

      CDOTransaction reader = session.openTransaction();
      CDOResource loaded = reader.getResource(resourcePath);
      Category loadedRoot = (Category)loaded.getContents().get(0);

      for (Category child : loadedRoot.getCategories())
      {
        assertEquals(CDOPermission.WRITE, CDOUtil.getCDOObject(child).cdoRevision().getPermission());
      }

      Product1 loadedProduct = loadedRoot.getProducts().get(0);
      assertEquals("A dynamic class rule must not be frozen in the resource baseline", CDOPermission.READ,
          CDOUtil.getCDOObject(loadedProduct).cdoRevision().getPermission());

      CountingCache cache = creator.lastCache;
      assertNotNull(cache);
      assertEquals("One user/branch cache generation is expected", 1, creator.generations.get());
      assertTrue("Expected many permission-cache accesses", cache.gets.get() >= 20);
      assertTrue("Repeated model revisions should hit the contained-resource slot", cache.hits.get() >= 19);
      assertTrue("A baseline miss must populate the cache", cache.puts.get() > 0);
      reader.close();

      int headGets = cache.gets.get();

      try (CDOView historical = session.openView(dataCommit))
      {
        CDOResource historicalResource = historical.getResource(resourcePath);
        Category historicalRoot = (Category)historicalResource.getContents().get(0);
        CDOUtil.getCDOObject(historicalRoot.getCategories().get(0)).cdoRevision().getPermission();
      }

      assertEquals("Historical reads must bypass the permission cache", headGets, cache.gets.get());

      CDOBranch secondaryBranch = session.getBranchManager().getMainBranch().createBranch(getBranchName("permission-cache"));

      try (CDOSession branchSession = openSession(CREDENTIALS))
      {
        CDOBranch branch = branchSession.getBranchManager().getBranch(secondaryBranch.getID());

        try (CDOView branchView = branchSession.openView(branch))
        {
          CDOResource branchResource = branchView.getResource(resourcePath);
          Category branchRoot = (Category)branchResource.getContents().get(0);
          CDOUtil.getCDOObject(branchRoot.getCategories().get(0)).cdoRevision().getPermission();
        }
      }

      assertEquals("A distinct branch must receive an independent cache generation", 2, creator.generations.get());

      try (CDOSession primaryBranchSession = openSession(CREDENTIALS))
      {
        try (CDOTransaction mainTransaction = primaryBranchSession.openTransaction())
        {
          CDOResource mainResource = mainTransaction.getResource(resourcePath);
          Category mainRoot = (Category)mainResource.getContents().get(0);
          CDOUtil.getCDOObject(mainRoot).cdoRevision().getPermission();
        }
      }

      assertEquals("Invalidating a non-main branch must retain the main cache generation", 2, creator.generations.get());
    }
  }

  public void testResourceRenameCommitWithoutCachedPermission() throws Exception
  {
    String renamedPath = resourcePath.substring(0, resourcePath.lastIndexOf('/')) + "/permission-cache-renamed";

    try (CDOSession session = openSession(CREDENTIALS))
    {
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource(resourcePath);
      transaction.commit();
      transaction.close();

      CDOTransaction rename = session.openTransaction();
      rename.getResource(resourcePath).setName("permission-cache-renamed");
      rename.commit();
      rename.close();

      try (CDOTransaction reader = session.openTransaction())
      {
        assertTrue(reader.hasResource(renamedPath));
      }
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testResourceRenameCommitAfterPermissionCachePopulation() throws Exception
  {
    String renamedPath = resourcePath.substring(0, resourcePath.lastIndexOf('/')) + "/permission-cache-renamed";
    CDOID productID;

    try (CDOSession session = openSession(CREDENTIALS))
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(resourcePath);
      Category root = getModel1Factory().createCategory();
      Product1 product = getModel1Factory().createProduct1();
      root.getProducts().add(product);
      resource.getContents().add(root);
      transaction.commit();
      transaction.close();

      securityManager.modify(realm -> realm.setDefaultAccess(null));

      try (CDOTransaction reader = session.openTransaction())
      {
        CDOResource loadedResource = reader.getResource(resourcePath);
        Category loadedRoot = (Category)loadedResource.getContents().get(0);
        Product1 loadedProduct = loadedRoot.getProducts().get(0);
        productID = CDOUtil.getCDOObject(loadedProduct).cdoID();
        assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(loadedProduct).cdoRevision().getPermission());
      }

      assertTrue("The read should have populated the cache", creator.generations.get() > 0);

      CDOBranch branch = session.getBranchManager().getMainBranch().createBranch(getBranchName("rename-cache"));

      try (CDOSession branchSession = openSession(CREDENTIALS))
      {
        CDOBranch branchInSession = branchSession.getBranchManager().getBranch(branch.getID());
        try (CDOView branchView = branchSession.openView(branchInSession))
        {
          CDOResource branchResource = branchView.getResource(resourcePath);
          Category branchRoot = (Category)branchResource.getContents().get(0);
          Product1 branchProduct = branchRoot.getProducts().get(0);
          assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(branchProduct).cdoRevision().getPermission());
        }
      }

      assertEquals("Main and branch views must have independent cache generations", 2, creator.generations.get());

      try (CDOTransaction rename = session.openTransaction())
      {
        rename.getResource(resourcePath).setName("permission-cache-renamed");
        rename.commit();
      }

      try (CDOTransaction reader = session.openTransaction())
      {
        Product1 movedProduct = (Product1)CDOUtil.getEObject(reader.getObject(productID));
        assertEquals("The renamed path must not reuse the old resource-safe baseline", CDOPermission.NONE,
            CDOUtil.getCDOObject(movedProduct).cdoRevision().getPermission());
      }

      assertEquals("Rename must create a new logical generation only for the affected branch", 3, creator.generations.get());

      try (CDOSession branchSession = openSession(CREDENTIALS))
      {
        CDOBranch branchInSession = branchSession.getBranchManager().getBranch(branch.getID());

        try (CDOView branchView = branchSession.openView(branchInSession))
        {
          CDOResource branchResource = branchView.getResource(resourcePath);
          Category branchRoot = (Category)branchResource.getContents().get(0);
          Product1 branchProduct = branchRoot.getProducts().get(0);
          assertEquals("The unaffected branch retains its old path permission", CDOPermission.READ,
              CDOUtil.getCDOObject(branchProduct).cdoRevision().getPermission());
        }
      }

      assertEquals("The unaffected branch cache must remain installed", 3, creator.generations.get());

      try (CDOTransaction reader = session.openTransaction())
      {
        assertTrue(reader.hasResource(renamedPath));
      }
    }
  }

  public void testResourceMoveInvalidatesPathBaselineForDescendant() throws Exception
  {
    String folderPath = getResourcePath("permission-cache-target");
    String movedPath = folderPath + "/" + resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
    CDOID productID;

    try (CDOSession session = openSession(CREDENTIALS))
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(resourcePath);
      transaction.createResourceFolder(folderPath);
      Category root = getModel1Factory().createCategory();
      root.getProducts().add(getModel1Factory().createProduct1());
      resource.getContents().add(root);
      transaction.commit();
      transaction.close();

      securityManager.modify(realm -> realm.setDefaultAccess(null));

      try (CDOTransaction reader = session.openTransaction())
      {
        Category rootInView = (Category)reader.getResource(resourcePath).getContents().get(0);
        Product1 product = rootInView.getProducts().get(0);
        productID = CDOUtil.getCDOObject(product).cdoID();
        assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
      }

      assertEquals(1, creator.generations.get());

      try (CDOTransaction move = session.openTransaction())
      {
        CDOResource movedResource = move.getResource(resourcePath);
        CDOResourceFolder folder = move.getResourceFolder(folderPath);
        folder.getNodes().add(movedResource);
        move.commit();
      }

      try (CDOTransaction reader = session.openTransaction())
      {
        Product1 product = (Product1)CDOUtil.getEObject(reader.getObject(productID));
        assertEquals("Moving an ancestor ResourceNode must recompute the descendant's path permission", CDOPermission.NONE,
            CDOUtil.getCDOObject(product).cdoRevision().getPermission());
        assertTrue(reader.hasResource(movedPath));
      }

      assertEquals("A ResourceNode move must replace the affected branch generation", 2, creator.generations.get());
    }
  }

  public void testResourceNodeCreationAndDeletionRetainGeneration() throws Exception
  {
    String dataPath = resourcePath;
    String outerFolderPath = getResourcePath("permission-cache-folder");
    String innerFolderPath = outerFolderPath + "/child";
    CDOID productID;

    try (CDOSession session = openSession(CREDENTIALS))
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(dataPath);
      Category root = getModel1Factory().createCategory();
      root.getProducts().add(getModel1Factory().createProduct1());
      resource.getContents().add(root);
      transaction.commit();
      transaction.close();
      securityManager.modify(realm -> realm.setDefaultAccess(null));

      try (CDOTransaction reader = session.openTransaction())
      {
        Category loadedRoot = (Category)reader.getResource(dataPath).getContents().get(0);
        Product1 product = loadedRoot.getProducts().get(0);
        productID = CDOUtil.getCDOObject(product).cdoID();
        assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
      }

      assertEquals("The initial authorization creates one branch generation", 1, creator.generations.get());

      try (CDOTransaction create = session.openTransaction())
      {
        create.createResourceFolder(outerFolderPath);
        create.createResourceFolder(innerFolderPath);
        create.commit();
      }

      assertEquals("Creating ResourceNodes must retain the branch generation", 1, creator.generations.get());

      try (CDOTransaction delete = session.openTransaction())
      {
        CDOResourceFolder outerFolder = delete.getResourceFolder(outerFolderPath);
        CDOResourceFolder innerFolder = delete.getResourceFolder(innerFolderPath);
        outerFolder.getNodes().remove(innerFolder);
        delete.commit();
      }

      assertEquals("Detaching a ResourceNode must retain the branch generation", 1, creator.generations.get());

      try (CDOTransaction reader = session.openTransaction())
      {
        Product1 product = (Product1)CDOUtil.getEObject(reader.getObject(productID));
        assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
      }

      assertEquals("The original cache remains usable after create and delete", 1, creator.generations.get());
    }
  }

  @SuppressWarnings("deprecation")
  public void testWriteBaselineSkipsDynamicPermissionEvaluation() throws Exception
  {
    AtomicInteger evaluations = new AtomicInteger();
    CustomResourceFilter dynamicFilter = new CustomResourceFilter(evaluations);
    dynamicFilter.setPath("/");
    dynamicFilter.setPatternStyle(PatternStyle.TREE);

    securityManager.modify(realm -> {
      for (Role role : realm.getAllRoles())
      {
        if ("Resource Access".equals(role.getId()))
        {
          role.getPermissions().add(SecurityFactory.eINSTANCE.createResourcePermission(resourcePath, Access.WRITE));
          role.getPermissions().add(SecurityFactory.eINSTANCE.createFilterPermission(Access.WRITE, dynamicFilter));
          break;
        }
      }

      realm.setDefaultAccess(null);
    });

    try (CDOSession session = openSession(CREDENTIALS))
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(resourcePath);
      Category root = getModel1Factory().createCategory();
      root.getProducts().add(getModel1Factory().createProduct1());
      root.getProducts().add(getModel1Factory().createProduct1());
      resource.getContents().add(root);
      transaction.commit();

      try (CDOTransaction reader = session.openTransaction())
      {
        Category loadedRoot = (Category)reader.getResource(resourcePath).getContents().get(0);
        for (Product1 product : loadedRoot.getProducts())
        {
          assertEquals(CDOPermission.WRITE, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
        }
      }

      assertEquals("A WRITE resource baseline must skip dynamic permission evaluation on misses and hits", 0, evaluations.get());
      assertEquals(1, creator.generations.get());
    }

    try (CDOSession secondSession = openSession(CREDENTIALS); CDOTransaction reader = secondSession.openTransaction())
    {
      Category loadedRoot = (Category)reader.getResource(resourcePath).getContents().get(0);
      Product1 product = loadedRoot.getProducts().get(1);
      assertEquals(CDOPermission.WRITE, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
    }

    assertTrue("The next authorization must hit the cached WRITE baseline", creator.lastCache.hits.get() > 0);
    assertEquals("Dynamic permissions remain skipped on cache hits", 0, evaluations.get());
  }

  @SuppressWarnings("deprecation")
  public void testOrdinaryObjectMovesKeepGenerationAndUseNewResourceKey() throws Exception
  {
    String resourceBPath = getResourcePath("permission-cache-b");
    CDOID productAID;

    securityManager.modify(realm -> {
      for (Role role : realm.getAllRoles())
      {
        if ("Resource Access".equals(role.getId()))
        {
          role.getPermissions().add(SecurityFactory.eINSTANCE.createResourcePermission(resourceBPath, Access.WRITE));
          role.getPermissions()
              .add(SecurityFactory.eINSTANCE.createFilterPermission(Access.WRITE,
                  SecurityFactory.eINSTANCE.createAndFilter(SecurityFactory.eINSTANCE.createClassFilter(getModel1Package().getProduct1()),
                      SecurityFactory.eINSTANCE.createResourceFilter(resourcePath, PatternStyle.EXACT, true))));
          break;
        }
      }
    });

    CDOID firstResourceID;

    try (CDOSession session = openSession(CREDENTIALS))
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resourceA = transaction.createResource(resourcePath);
      CDOResource resourceB = transaction.createResource(resourceBPath);
      Category rootA = getModel1Factory().createCategory();
      Category innerA = getModel1Factory().createCategory();
      rootA.getCategories().add(innerA);
      Product1 product = getModel1Factory().createProduct1();
      rootA.getProducts().add(product);
      resourceA.getContents().add(rootA);
      Category rootB = getModel1Factory().createCategory();
      Product1 productInB = getModel1Factory().createProduct1();
      rootB.getProducts().add(productInB);
      resourceB.getContents().add(rootB);
      transaction.commit();
      transaction.close();

      securityManager.modify(realm -> realm.setDefaultAccess(null));

      try (CDOTransaction reader = session.openTransaction())
      {
        Category loadedRoot = (Category)reader.getResource(resourcePath).getContents().get(0);
        Product1 loadedProduct = loadedRoot.getProducts().get(0);
        productAID = CDOUtil.getCDOObject(loadedProduct).cdoID();
        assertEquals("Resource A's dynamic write grant permits the subsequent move", CDOPermission.WRITE,
            CDOUtil.getCDOObject(loadedProduct).cdoRevision().getPermission());
      }

      assertEquals(1, creator.generations.get());
      assertTrue("The permission cache must finish creation before its contents are inspected", creator.cacheCreated.await(10, TimeUnit.SECONDS));
      CountingCache cache = creator.lastCache;
      assertNotNull(cache);
      firstResourceID = cache.lastGetID;
      assertNotNull("The cache lookup must resolve resource A", firstResourceID);
      CDOID resourceAKey = firstResourceID;
      assertEquals(CDOPermission.READ, cache.values.get(CountingCache.key(resourceAKey, false)));

      try (CDOTransaction reader = session.openTransaction())
      {
        Product1 loadedProductB = ((Category)reader.getResource(resourceBPath).getContents().get(0)).getProducts().get(0);
        assertEquals("Resource B supplies the WRITE baseline", CDOPermission.WRITE, CDOUtil.getCDOObject(loadedProductB).cdoRevision().getPermission());
      }

      CDOID resourceBKey = cache.lastGetID;
      assertFalse("The two distinguishable resources must use different cache keys", resourceAKey.equals(resourceBKey));
      assertEquals(CDOPermission.WRITE, cache.values.get(CountingCache.key(resourceBKey, false)));

      try (CDOTransaction moveInside = session.openTransaction())
      {
        Category root = (Category)moveInside.getResource(resourcePath).getContents().get(0);
        Category inner = root.getCategories().get(0);
        Product1 movedProduct = (Product1)CDOUtil.getEObject(moveInside.getObject(productAID));
        inner.getProducts().add(movedProduct);
        moveInside.commit();
      }

      try (CDOTransaction reader = session.openTransaction())
      {
        Product1 movedProduct = (Product1)CDOUtil.getEObject(reader.getObject(productAID));
        assertEquals(CDOPermission.WRITE, CDOUtil.getCDOObject(movedProduct).cdoRevision().getPermission());
      }

      assertEquals("Moving an object within one resource must retain the generation", 1, creator.generations.get());

      try (CDOTransaction moveAcross = session.openTransaction())
      {
        Category targetRootB = (Category)moveAcross.getResource(resourceBPath).getContents().get(0);
        Product1 movedProduct = (Product1)CDOUtil.getEObject(moveAcross.getObject(productAID));
        targetRootB.getProducts().add(movedProduct);
        moveAcross.commit();
      }

      try (CDOTransaction reader = session.openTransaction())
      {
        Product1 movedProduct = (Product1)CDOUtil.getEObject(reader.getObject(productAID));
        assertEquals("After the move, resource B's baseline determines authorization", CDOPermission.WRITE,
            CDOUtil.getCDOObject(movedProduct).cdoRevision().getPermission());
      }

      assertEquals("Moving an ordinary model object across resources must not purge the branch generation", 1, creator.generations.get());
      assertEquals("Authorization after the move must query resource B's established key", resourceBKey, cache.lastGetID);
    }
  }

  public void testSecondaryRepositoryUsesIsolatedPermissionCache() throws Exception
  {
    InternalRepository primaryRepository = getRepository();
    Map<String, Object> testProperties = getTestProperties();
    Object testSecurityManager = testProperties.remove(RepositoryConfig.PROP_TEST_SECURITY_MANAGER);
    InternalRepository secondaryRepository;

    try
    {
      secondaryRepository = getRepository("permission-cache-secondary", true);
    }
    finally
    {
      if (testSecurityManager != null)
      {
        testProperties.put(RepositoryConfig.PROP_TEST_SECURITY_MANAGER, testSecurityManager);
      }
    }

    assertSame("The security manager remains bound to the primary repository", primaryRepository, securityManager.getRepository());
    assertNotSame("The secondary repository must not replace the primary repository", secondaryRepository, securityManager.getRepository());

    Map<String, Object> secondaryContext = new ConcurrentHashMap<>();
    AtomicBoolean allowWrite = new AtomicBoolean(true);
    secondaryContext.put("allowWrite", allowWrite);
    AtomicInteger contextEvaluations = new AtomicInteger();

    try (CDOSession primarySession = openSession(CREDENTIALS))
    {
      CDOTransaction transaction = primarySession.openTransaction();
      CDOResource resource = transaction.createResource(resourcePath);
      Category root = getModel1Factory().createCategory();
      root.getProducts().add(getModel1Factory().createProduct1());
      resource.getContents().add(root);
      transaction.commit();
      transaction.close();
      securityManager.modify(realm -> realm.setDefaultAccess(null));

      try (CDOTransaction reader = primarySession.openTransaction())
      {
        Product1 product = ((Category)reader.getResource(resourcePath).getContents().get(0)).getProducts().get(0);
        assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
      }

      assertEquals(1, creator.generationCount(primaryRepository));

      securityManager.addSecondaryRepository(secondaryRepository, secondaryContext);

      openSession(CREDENTIALS).close(); // Installs the test credentials provider for the named-repository session.

      try (CDOSession secondarySession = openSession("permission-cache-secondary"))
      {
        CDOTransaction secondaryWrite = secondarySession.openTransaction();
        CDOResource secondaryResource = secondaryWrite.createResource(resourcePath);
        Category secondaryRoot = getModel1Factory().createCategory();

        for (int i = 0; i < 45; i++)
        {
          secondaryRoot.getCategories().add(getModel1Factory().createCategory());
        }

        secondaryRoot.getProducts().add(getModel1Factory().createProduct1());
        secondaryRoot.getProducts().add(getModel1Factory().createProduct1());
        secondaryResource.getContents().add(secondaryRoot);
        commitHandler2Calls.set(0);
        secondaryWrite.commit();
        secondaryWrite.close();

        CDOID secondaryProductID;

        try (CDOTransaction reader = secondarySession.openTransaction())
        {
          Category loadedRoot = (Category)reader.getResource(resourcePath).getContents().get(0);
          Product1 product = loadedRoot.getProducts().get(0);
          secondaryProductID = CDOUtil.getCDOObject(product).cdoID();
          assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
          assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(loadedRoot.getProducts().get(1)).cdoRevision().getPermission());

          for (Category category : loadedRoot.getCategories())
          {
            assertEquals(CDOPermission.WRITE, CDOUtil.getCDOObject(category).cdoRevision().getPermission());
          }
        }

        assertEquals("Primary and secondary main branches have the same numeric ID", 0, secondarySession.getBranchManager().getMainBranch().getID());
        assertEquals(1, creator.generationCount(secondaryRepository));
        assertSame(secondaryRepository, creator.createdFor.get(secondaryRepository));

        try (CDOSession repeatedSession = openSession("permission-cache-secondary"); CDOTransaction reader = repeatedSession.openTransaction())
        {
          Product1 product = (Product1)CDOUtil.getEObject(reader.getObject(secondaryProductID));
          assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
        }

        assertTrue("Secondary authorizations populate a repository-specific resource cache",
            creator.cacheFor(secondaryRepository).gets.get() > 0 && creator.cacheFor(secondaryRepository).puts.get() > 0);

        CountingCache secondaryCache = creator.cacheFor(secondaryRepository);
        CDOID cachedResourceID = secondaryCache.lastGetID;
        boolean cachedResourceNode = secondaryCache.lastGetResourceNode;
        CDOPermission cachedBaseline = secondaryCache.values.get(CountingCache.key(cachedResourceID, cachedResourceNode));
        assertNotNull("An authorization must publish its resource baseline", cachedBaseline);
        assertEquals("The same secondary resource key returns its cached baseline", cachedBaseline, secondaryCache.get(cachedResourceID, cachedResourceNode));
        assertTrue("A repeated same-resource lookup hits the secondary baseline", secondaryCache.hits.get() > 0);

        Permission contextPermission = SecurityFactory.eINSTANCE.createFilterPermission(Access.WRITE, new ContextFilter(contextEvaluations));
        installDynamicPermission("CacheUser", contextPermission);

        try (CDOSession contextSession = openSession("permission-cache-secondary"); CDOTransaction reader = contextSession.openTransaction())
        {
          Product1 product = (Product1)CDOUtil.getEObject(reader.getObject(secondaryProductID));
          assertEquals("AuthorizationContext A upgrades the cached baseline", CDOPermission.WRITE, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
        }

        int hitsBeforeContextB = secondaryCache.hits.get();
        allowWrite.set(false);

        try (CDOSession contextSession = openSession("permission-cache-secondary"); CDOTransaction reader = contextSession.openTransaction())
        {
          Product1 product = (Product1)CDOUtil.getEObject(reader.getObject(secondaryProductID));
          assertEquals("AuthorizationContext B is evaluated without replacing the cached baseline", CDOPermission.READ,
              CDOUtil.getCDOObject(product).cdoRevision().getPermission());
        }

        assertEquals("The context-sensitive dynamic permission executes for each authorization", 2, contextEvaluations.get());
        assertTrue("Both context-sensitive authorizations reuse the same resource baseline", secondaryCache.hits.get() > hitsBeforeContextB);
        assertSame("Changing AuthorizationContext does not replace the secondary generation", secondaryCache, creator.cacheFor(secondaryRepository));
        CountingCache oldSecondaryCache = creator.cacheFor(secondaryRepository);

        try (CDOSession renameSession = openSession("permission-cache-secondary"); CDOTransaction rename = renameSession.openTransaction())
        {
          commitHandler2Calls.set(0);
          rename.getResource(resourcePath).setName("secondary-renamed");
          rename.commit();
        }

        assertEquals("Secondary commits do not newly invoke CommitHandler2", 0, commitHandler2Calls.get());

        try (CDOSession renamedSession = openSession("permission-cache-secondary"); CDOTransaction reader = renamedSession.openTransaction())
        {
          Product1 product = (Product1)CDOUtil.getEObject(reader.getObject(secondaryProductID));
          assertEquals(CDOPermission.NONE, CDOUtil.getCDOObject(product).cdoRevision().getPermission());
        }

        assertEquals("Secondary ResourceNode rename replaces only its own generation", 2, creator.generationCount(secondaryRepository));
        assertEquals("The primary cache generation remains installed", 1, creator.generationCount(primaryRepository));
        assertNotSame("The renamed secondary path must use a fresh cache generation", oldSecondaryCache, creator.cacheFor(secondaryRepository));
      }
    }
  }

  private void installDynamicPermission(String userID, Permission permission) throws Exception
  {
    Field userInfosField = securityManager.getClass().getDeclaredField("userInfos");
    userInfosField.setAccessible(true);
    Map<?, ?> userInfos = (Map<?, ?>)userInfosField.get(securityManager);
    Object userInfo = userInfos.get(userID);
    assertNotNull("The secondary read must initialize the user's security state", userInfo);

    Field dynamicPermissionsField = userInfo.getClass().getDeclaredField("dynamicPermissions");
    dynamicPermissionsField.setAccessible(true);
    dynamicPermissionsField.set(userInfo, new Permission[] { permission });
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
  private static final class CountingCreator implements PermissionCache.Creator
  {
    private final AtomicInteger generations = new AtomicInteger();

    private final Map<IRepository, AtomicInteger> repositoryGenerations = new ConcurrentHashMap<>();

    private final Map<IRepository, IRepository> createdFor = new ConcurrentHashMap<>();

    private final Map<IRepository, CountingCache> caches = new ConcurrentHashMap<>();

    private volatile CountingCache lastCache;

    private final CountDownLatch cacheCreated = new CountDownLatch(1);

    public CountingCreator()
    {
    }

    @Override
    public PermissionCache create(IRepository repository, String userID, CDOBranch branch)
    {
      generations.incrementAndGet();
      repositoryGenerations.computeIfAbsent(repository, key -> new AtomicInteger()).incrementAndGet();
      createdFor.put(repository, repository);
      lastCache = new CountingCache();
      caches.put(repository, lastCache);
      cacheCreated.countDown();
      return lastCache;
    }

    public CountingCache cacheFor(IRepository repository)
    {
      return caches.get(repository);
    }

    public int generationCount(IRepository repository)
    {
      AtomicInteger count = repositoryGenerations.get(repository);
      return count == null ? 0 : count.get();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CountingCache implements PermissionCache
  {
    private final Map<String, CDOPermission> values = new ConcurrentHashMap<>();

    private final AtomicInteger gets = new AtomicInteger();

    private final AtomicInteger hits = new AtomicInteger();

    private final AtomicInteger puts = new AtomicInteger();

    private volatile CDOID lastGetID;

    private volatile boolean lastGetResourceNode;

    public CountingCache()
    {
    }

    @Override
    public CDOPermission get(CDOID resourceNodeID, boolean resourceNode)
    {
      gets.incrementAndGet();
      lastGetID = resourceNodeID;
      lastGetResourceNode = resourceNode;
      CDOPermission result = values.get(key(resourceNodeID, resourceNode));
      if (result != null)
      {
        hits.incrementAndGet();
      }
      return result;
    }

    @Override
    public void put(CDOID resourceNodeID, boolean resourceNode, CDOPermission permission)
    {
      puts.incrementAndGet();
      values.put(key(resourceNodeID, resourceNode), permission);
    }

    private static String key(CDOID id, boolean resourceNode)
    {
      return id + ":" + resourceNode;
    }
  }
}
