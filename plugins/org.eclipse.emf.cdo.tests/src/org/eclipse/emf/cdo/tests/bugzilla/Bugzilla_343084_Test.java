/*
 * Copyright (c) 2012, 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.security.NoPermissionException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.UserManager;

import org.eclipse.emf.ecore.EClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "TEST_AUTHENTICATOR")
@CleanRepositoriesAfter(reason = "TEST_AUTHENTICATOR")
public class Bugzilla_343084_Test extends AbstractCDOTest
{
  private static final String REPO_NAME = "protectedrepo";

  private static final String USER_ID = "stepper";

  private static final char[] PASSWORD = "eike2010".toCharArray();

  private Map<EClass, CDOPermission> permissions = new HashMap<>();

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    IPermissionManager permissionManager = new IPermissionManager()
    {
      @Override
      public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, ISession session)
      {
        EClass eClass = revision.getEClass();
        CDOPermission permission = permissions.get(eClass);
        if (permission != null)
        {
          return permission;
        }

        return CDOPermission.WRITE;
      }

      @Override
      @Deprecated
      public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, String userID)
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasAnyRule(ISession session, Set<? extends Object> permissions)
      {
        return false;
      }
    };

    getTestProperties().put(RepositoryConfig.PROP_TEST_AUTHENTICATOR, userManager);
    getTestProperties().put(RepositoryConfig.PROP_TEST_PERMISSION_MANAGER, permissionManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);
  }

  public void testPermissionManagerWRITE() throws Exception
  {
    {
      CDOSession session = openSession(REPO_NAME);
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("res"));

      Category category = getModel1Factory().createCategory();
      category.getCategories().add(getModel1Factory().createCategory());

      resource.getContents().add(category);
      transaction.commit();
      session.close();
    }

    permissions.put(getModel1Package().getCategory(), CDOPermission.WRITE);

    CDOSession session = openSession(REPO_NAME);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("res"));

    Category category = (Category)resource.getContents().get(0);
    CDORevision revision = CDOUtil.getCDOObject(category).cdoRevision();
    assertEquals(CDOPermission.WRITE, revision.getPermission());
    assertEquals(true, revision.isReadable());
    assertEquals(true, revision.isWritable());

    category.getName();
    category.setName("HW");

    category.getCategories().get(0);
    category.getCategories().add(getModel1Factory().createCategory());
  }

  public void testPermissionManagerREAD() throws Exception
  {
    {
      CDOSession session = openSession(REPO_NAME);
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("res"));

      Category category = getModel1Factory().createCategory();
      category.getCategories().add(getModel1Factory().createCategory());

      resource.getContents().add(category);
      transaction.commit();
      session.close();
    }

    permissions.put(getModel1Package().getCategory(), CDOPermission.READ);

    CDOSession session = openSession(REPO_NAME);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("res"));

    Category category = (Category)resource.getContents().get(0);
    CDORevision revision = CDOUtil.getCDOObject(category).cdoRevision();
    assertEquals(CDOPermission.READ, revision.getPermission());
    assertEquals(true, revision.isReadable());
    assertEquals(false, revision.isWritable());

    category.getName();

    try
    {
      category.setName("HW");
      fail("NoPermissionException expected");
    }
    catch (NoPermissionException expected)
    {
      // SUCCESS
    }

    category.getCategories().get(0);

    try
    {
      category.getCategories().add(getModel1Factory().createCategory());
      fail("NoPermissionException expected");
    }
    catch (NoPermissionException expected)
    {
      // SUCCESS
    }
  }

  @Skips(ModelConfig.CAPABILITY_LEGACY)
  public void testPermissionManagerNONE() throws Exception
  {
    {
      CDOSession session = openSession(REPO_NAME);
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("res"));

      Category category = getModel1Factory().createCategory();
      category.getCategories().add(getModel1Factory().createCategory());

      resource.getContents().add(category);
      transaction.commit();
      session.close();
    }

    permissions.put(getModel1Package().getCategory(), CDOPermission.NONE);

    CDOSession session = openSession(REPO_NAME);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("res"));

    Category category = (Category)resource.getContents().get(0);
    CDORevision revision = CDOUtil.getCDOObject(category).cdoRevision();
    assertEquals(CDOPermission.NONE, revision.getPermission());
    assertEquals(false, revision.isReadable());
    assertEquals(false, revision.isWritable());

    try
    {
      category.getName();
      fail("NoPermissionException expected");
    }
    catch (NoPermissionException expected)
    {
      // SUCCESS
    }

    try
    {
      category.setName("HW");
      fail("NoPermissionException expected");
    }
    catch (NoPermissionException expected)
    {
      // SUCCESS
    }

    try
    {
      category.getCategories().get(0);
      fail("NoPermissionException expected");
    }
    catch (NoPermissionException expected)
    {
      // SUCCESS
    }

    try
    {
      category.getCategories().add(getModel1Factory().createCategory());
      fail("NoPermissionException expected");
    }
    catch (NoPermissionException expected)
    {
      // SUCCESS
    }
  }
}
