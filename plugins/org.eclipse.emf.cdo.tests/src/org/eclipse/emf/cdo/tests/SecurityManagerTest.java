/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;

/**
 * @author Eike Stepper
 */
@CleanRepositoriesBefore
@CleanRepositoriesAfter
public class SecurityManagerTest extends AbstractCDOTest
{
  private static final String USER_ID = "Stepper";

  private static final String PASSWORD = "12345";

  public void testCommit() throws Exception
  {
    ISecurityManager securityManager = startRepository();
    securityManager.modify(new ISecurityManager.RealmOperation()
    {
      public void execute(Realm realm)
      {
        User user = realm.addUser(USER_ID, PASSWORD);
        user.getGroups().add(realm.getGroup("Users"));
        user.getRoles().add(realm.getRole("All Objects Writer"));
      }
    });

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(getModel1Factory().createProduct1());
    transaction.commit();
  }

  private ISecurityManager startRepository()
  {
    ISecurityManager securityManager = SecurityManagerUtil.createSecurityManager("/security", getServerContainer());
    getTestProperties().put(RepositoryConfig.PROP_TEST_SECURITY_MANAGER, securityManager);

    IPasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider(USER_ID, PASSWORD);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, credentialsProvider);

    getRepository(); // Start repository

    return securityManager;
  }
}
