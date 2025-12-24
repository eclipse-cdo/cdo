/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.PatternStyle;
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
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentials;

import org.eclipse.emf.ecore.EObject;

/**
 * Bug 418267 - [Security] Cached permissions are not always properly updated after commits.
 *
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "TEST_SECURITY_MANAGER")
@CleanRepositoriesAfter(reason = "TEST_SECURITY_MANAGER")
public class Bugzilla_418267_Test extends AbstractCDOTest
{
  private static final SecurityFactory SF = SecurityFactory.eINSTANCE;

  private static final IPasswordCredentials CREDENTIALS = new PasswordCredentials("user", "password");

  private static final IPasswordCredentials CREDENTIALS_WRITER = new PasswordCredentials("writer", "password");

  public void testMoveFromNoneToNone() throws Exception
  {
    move(CDOPermission.NONE, CDOPermission.NONE);
  }

  public void testMoveFromNoneToRead() throws Exception
  {
    move(CDOPermission.NONE, CDOPermission.READ);
  }

  public void testMoveFromNoneToWrite() throws Exception
  {
    move(CDOPermission.NONE, CDOPermission.WRITE);
  }

  public void testMoveFromReadToNone() throws Exception
  {
    move(CDOPermission.READ, CDOPermission.NONE);
  }

  public void testMoveFromReadToRead() throws Exception
  {
    move(CDOPermission.READ, CDOPermission.READ);
  }

  public void testMoveFromReadToWrite() throws Exception
  {
    move(CDOPermission.READ, CDOPermission.WRITE);
  }

  public void testMoveFromWriteToNone() throws Exception
  {
    move(CDOPermission.WRITE, CDOPermission.NONE);
  }

  public void testMoveFromWriteToRead() throws Exception
  {
    move(CDOPermission.WRITE, CDOPermission.READ);
  }

  public void testMoveFromWriteToWrite() throws Exception
  {
    move(CDOPermission.WRITE, CDOPermission.WRITE);
  }

  private void move(final CDOPermission from, final CDOPermission to) throws Exception
  {
    final String pathFolder2 = getResourcePath("folder2");
    final String pathFolder1 = getResourcePath("folder1");
    final String pathResource1 = pathFolder1 + "/res";

    startSecureRepository(new ISecurityManager.RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        CDOTransaction transaction = (CDOTransaction)realm.cdoView();
        transaction.createResourceFolder(pathFolder2);

        CDOResource resource = transaction.createResource(pathResource1);
        resource.getContents().add(getModel1Factory().createCompany());

        Role role = realm.addRole("Test Role");

        Access accessFrom = getAccess(from);
        if (accessFrom != null)
        {
          role.getPermissions().add(SF.createFilterPermission(accessFrom, //
              SF.createResourceFilter(pathFolder1, PatternStyle.TREE, false)));
          role.getPermissions().add(SF.createFilterPermission(Access.READ, //
              SF.createResourceFilter(pathFolder1, PatternStyle.EXACT, true).setModelObjects(false)));
        }

        Access accessTo = getAccess(to);
        if (accessTo != null)
        {
          role.getPermissions().add(SF.createFilterPermission(accessTo, //
              SF.createResourceFilter(pathFolder2, PatternStyle.TREE, false)));
          role.getPermissions().add(SF.createFilterPermission(Access.READ, //
              SF.createResourceFilter(pathFolder2, PatternStyle.EXACT, true).setModelObjects(false)));
        }

        User user = realm.addUser(CREDENTIALS);
        user.getRoles().add(role);

        User writer = realm.addUser(CREDENTIALS_WRITER);
        writer.getRoles().add(realm.getRole(Role.ALL_OBJECTS_WRITER));
      }
    });

    CDOSession sessionWriter = openSession(CREDENTIALS_WRITER);
    CDOTransaction transactionWriter = sessionWriter.openTransaction();
    CDOResource resourceWriter = transactionWriter.getResource(pathResource1);
    Company companyWriter = (Company)resourceWriter.getContents().get(0);
    CDOResourceFolder targetFolderWriter = transactionWriter.getResourceFolder(pathFolder2);

    CDOSession session = openSession(CREDENTIALS);
    session.options().setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);

    CDOTransaction transaction = session.openTransaction();
    assertPermissions(from, transaction, resourceWriter, companyWriter); // Pre check

    targetFolderWriter.getNodes().add(resourceWriter); // Move

    // int xxx;
    // transactionWriter.commit();
    // sleep(1000000000);

    commitAndSync(transactionWriter, transaction); // Commit + invalidate
    assertPermissions(to, transaction, resourceWriter, companyWriter); // Post check
  }

  private static Access getAccess(CDOPermission permission)
  {
    switch (permission)
    {
    case READ:
      return Access.READ;

    case WRITE:
      return Access.WRITE;

    default:
      return null;
    }
  }

  private static void assertPermissions(CDOPermission expected, CDOTransaction transaction, EObject... objectsWriter)
  {
    for (EObject objectWriter : objectsWriter)
    {
      EObject object = transaction.getObject(objectWriter);
      CDOPermission permission = CDOUtil.getCDOObject(object).cdoPermission();
      assertEquals(expected, permission);
    }

    // if (expected == CDOPermission.NONE)
    // {
    // try
    // {
    // CDOResource resource = transaction.getObject(resourceWriter);
    // fail("Exception expected");
    // }
    // catch (Exception ex)
    // {
    // // SUCCESS
    // }
    // }
    // else
    // {
    // CDOResource resource = transaction.getObject(resourceWriter);
    // assertEquals(expected, resource.cdoPermission());
    //
    // if (expected != CDOPermission.NONE)
    // {
    // Company company = (Company)resource.getContents().get(0);
    // assertEquals(expected, CDOUtil.getCDOObject(company).cdoPermission());
    // }
    // }
  }

  private ISecurityManager startSecureRepository(ISecurityManager.RealmOperation operation)
  {
    ISecurityManager securityManager = SecurityManagerUtil.createSecurityManager("/security", getServerContainer());

    // Start repository
    getTestProperties().put(RepositoryConfig.PROP_TEST_SECURITY_MANAGER, securityManager);
    getRepository();

    securityManager.modify(operation);
    return securityManager;
  }
}
