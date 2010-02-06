/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.UserManager;

/**
 * @author Andre Dietisheim
 */
public class CommitInfoTest extends AbstractCDOTest
{
  private static final String REPO_NAME = "authrepo";

  private static final String USER_ID = "stepper";

  private static final char[] PASSWORD = "eike2010".toCharArray();

  private static final String RESOURCE_PATH = "/res";

  public void testTimestampIsValid() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    long timeStamp = commitInfo.getTimeStamp();
    assertEquals(true, timeStamp > CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public void testBranchIsStored() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(transaction.getBranch(), commitInfo.getBranch());
  }

  public void testSubBranchIsStored() throws Exception
  {
    skipTest(!getRepository().isSupportingBranches());

    CDOSession session = openSession();
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(transaction.getBranch(), commitInfo.getBranch());
  }

  public void testUserIsStored() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(USER_ID, commitInfo.getUserID());
  }

  public void testCommentIsStored() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

    String comment = "Andre";
    transaction.setCommitComment(comment);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(comment, commitInfo.getComment());
  }
}
