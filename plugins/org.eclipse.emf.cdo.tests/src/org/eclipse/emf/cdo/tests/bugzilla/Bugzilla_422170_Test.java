/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentials;

import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collections;

/**
 * Bug 422170 - [Security] NPE on Permissions update for detached objects.
 *
 * @author Esteban Dugueperoux
 */
@CleanRepositoriesBefore(reason = "TEST_SECURITY_MANAGER")
@CleanRepositoriesAfter(reason = "TEST_SECURITY_MANAGER")
public class Bugzilla_422170_Test extends AbstractCDOTest
{
  private static final String SHARED_RESOURCE_NAME = "sharedResource.model1";

  private static final String RENAMED_SHARED_RESOURCE_NAME = "sharedResourceRenamed.model1";

  private static final IPasswordCredentials USER_1_CREDENTIALS = new PasswordCredentials("user1", "12345");

  private static final IPasswordCredentials USER_2_CREDENTIALS = new PasswordCredentials("user2", "54321");

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    ISecurityManager securityManager = SecurityManagerUtil.createSecurityManager("/security", getServerContainer());

    // Start repository
    getTestProperties().put(RepositoryConfig.PROP_TEST_SECURITY_MANAGER, securityManager);
    getRepository();

    ISecurityManager.RealmOperation realmOperation = new ISecurityManager.RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        User user1 = realm.addUser(USER_1_CREDENTIALS);
        user1.setDefaultAccessOverride(Access.READ);
        Role user1Role = realm.addRole("user1Role");
        FilterPermission filterPermission = SecurityFactory.eINSTANCE.createFilterPermission(Access.WRITE,
            SecurityFactory.eINSTANCE.createOrFilter(SecurityFactory.eINSTANCE.createPackageFilter(getModel1Package()),
                SecurityFactory.eINSTANCE.createPackageFilter(EresourcePackage.eINSTANCE),
                SecurityFactory.eINSTANCE.createResourceFilter(getResourcePath(SHARED_RESOURCE_NAME)),
                SecurityFactory.eINSTANCE.createResourceFilter(getResourcePath(RENAMED_SHARED_RESOURCE_NAME))));
        user1Role.getPermissions().add(filterPermission);
        user1.getRoles().add(user1Role);

        User user2 = realm.addUser(USER_2_CREDENTIALS);
        user2.setDefaultAccessOverride(Access.READ);
        Role user2Role = realm.addRole("user2Role");
        filterPermission = SecurityFactory.eINSTANCE.createFilterPermission(Access.WRITE,
            SecurityFactory.eINSTANCE.createOrFilter(SecurityFactory.eINSTANCE.createPackageFilter(getModel1Package()),
                SecurityFactory.eINSTANCE.createPackageFilter(EresourcePackage.eINSTANCE),
                SecurityFactory.eINSTANCE.createResourceFilter(getResourcePath(SHARED_RESOURCE_NAME)),
                SecurityFactory.eINSTANCE.createResourceFilter(getResourcePath(RENAMED_SHARED_RESOURCE_NAME))));
        user2Role.getPermissions().add(filterPermission);
        user2.getRoles().add(user2Role);
      }
    };

    securityManager.modify(realmOperation);
  }

  public void testPermissionsWithDetachedObject() throws Exception
  {
    Company obeoCompany = getModel1Factory().createCompany();
    Category productsCategory = getModel1Factory().createCategory();
    obeoCompany.getCategories().add(productsCategory);
    Product1 smartEAProduct = getModel1Factory().createProduct1();
    productsCategory.getProducts().add(smartEAProduct);

    CDOSession session1 = openSession(USER_1_CREDENTIALS);
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource sharedResourceForUser1 = transaction1.createResource(getResourcePath(SHARED_RESOURCE_NAME));
    sharedResourceForUser1.getContents().add(obeoCompany);
    sharedResourceForUser1.save(Collections.emptyMap());

    CDOSession session2 = openSession(USER_2_CREDENTIALS);
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource sharedResourceForUser2 = transaction2.getResource(getResourcePath(SHARED_RESOURCE_NAME));
    Company obeoCompanyForUser2 = (Company)sharedResourceForUser2.getContents().get(0);
    Category categoryForUser2 = obeoCompanyForUser2.getCategories().get(0);

    EcoreUtil.delete(categoryForUser2);
    sharedResourceForUser2.setName(RENAMED_SHARED_RESOURCE_NAME);
    commitAndSync(transaction2, transaction1);

    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(productsCategory).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(smartEAProduct).cdoState());
  }
}
