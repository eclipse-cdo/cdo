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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andre Dietisheim
 */
public class CommitInfoTest extends AbstractCDOTest
{
  private static final String REPO_NAME = "authrepo";

  private static final String USER_ID = "stepper";

  private static final char[] PASSWORD = "eike2010".toCharArray();

  private static final String RESOURCE_PATH = "/res";

  public void testLocalTimestamp() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();
    long timeStamp = commitInfo.getTimeStamp();
    assertEquals(true, timeStamp > CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public void testLocalBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(transaction.getBranch(), commitInfo.getBranch());
  }

  public void testLocalSubBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(transaction.getBranch(), commitInfo.getBranch());
  }

  public void testLocalUser() throws Exception
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
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(USER_ID, commitInfo.getUserID());
  }

  public void testLocalComment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    String comment = "Andre";
    transaction.setCommitComment(comment);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(comment, commitInfo.getComment());
  }

  public void testServerTimestamp() throws Exception
  {
    CDOSession session = openSession();
    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    StoreThreadLocal.setSession(serverSession);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getTimeStamp(), infos.get(1).getTimeStamp());
  }

  public void testServerBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getBranch(), infos.get(1).getBranch());
  }

  public void testServerSubBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getBranch(), infos.get(1).getBranch());
  }

  public void testServerUser() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    StoreThreadLocal.setSession(getRepository(REPO_NAME).getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository(REPO_NAME).getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getUserID(), infos.get(1).getUserID());
  }

  public void testServerComment() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    String comment = "Andre";
    transaction.setCommitComment(comment);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getComment(), infos.get(1).getComment());
  }

  public void testServerTimestampWithBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(
        getRepository().getBranchManager().getBranch(transaction.getBranch().getID()), CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getTimeStamp(), infos.get(1).getTimeStamp());
  }

  public void testServerBranchWithBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(
        getRepository().getBranchManager().getBranch(transaction.getBranch().getID()), CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getBranch(), infos.get(1).getBranch());
  }

  public void testServerSubBranchWithBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(
        getRepository().getBranchManager().getBranch(transaction.getBranch().getID()), CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(1, infos.size());
    assertEquals(commitInfo.getBranch(), infos.get(0).getBranch());
  }

  public void testServerUserWithBranch() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    StoreThreadLocal.setSession(getRepository(REPO_NAME).getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository(REPO_NAME).getCommitInfoManager().getCommitInfos(
        getRepository(REPO_NAME).getBranchManager().getBranch(transaction.getBranch().getID()),
        CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getUserID(), infos.get(1).getUserID());
  }

  public void testServerCommentWithBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    String comment = "Andre";
    transaction.setCommitComment(comment);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(
        getRepository().getBranchManager().getBranch(transaction.getBranch().getID()), CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getComment(), infos.get(1).getComment());
  }

  public void testServerTimestampWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOBranch wrong = getRepository().getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testServerBranchWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOBranch wrong = getRepository().getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testServerSubBranchWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOBranch wrong = getRepository().getBranchManager().getMainBranch().createBranch("wrong");
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testServerUserWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    StoreThreadLocal.setSession(getRepository(REPO_NAME).getSessionManager().getSession(session.getSessionID()));

    CDOBranch wrong = getRepository(REPO_NAME).getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    transaction.commit();

    Handler handler = new Handler();
    getRepository(REPO_NAME).getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testServerCommentWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOBranch wrong = getRepository().getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    String comment = "Andre";
    transaction.setCommitComment(comment);

    transaction.commit();

    Handler handler = new Handler();
    getRepository().getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testClientTimestamp() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getTimeStamp(), infos.get(1).getTimeStamp());
  }

  public void testClientBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getBranch(), infos.get(1).getBranch());
  }

  public void testClientSubBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getBranch(), infos.get(1).getBranch());
  }

  public void testClientUser() throws Exception
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
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getUserID(), infos.get(1).getUserID());
  }

  public void testClientComment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    String comment = "Andre";
    transaction.setCommitComment(comment);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getComment(), infos.get(1).getComment());
  }

  public void testClientTimestampWithBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getTimeStamp(), infos.get(1).getTimeStamp());
  }

  public void testClientBranchWithBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getBranch(), infos.get(1).getBranch());
  }

  public void testClientSubBranchWithBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(1, infos.size());
    assertEquals(commitInfo.getBranch(), infos.get(0).getBranch());
  }

  public void testClientUserWithBranch() throws Exception
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
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getUserID(), infos.get(1).getUserID());
  }

  public void testClientCommentWithBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    String comment = "Andre";
    transaction.setCommitComment(comment);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getComment(), infos.get(1).getComment());
  }

  public void testClientTimestampWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testClientBranchWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testClientSubBranchWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testClientUserWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testClientCommentWithWrongBranch() throws Exception
  {
    skipUnlessBranching();

    CDOSession session = openSession();
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    final Product1 product = getModel1Factory().createProduct1();
    product.setName("cdo");
    resource.getContents().add(product);

    String comment = "Andre";
    transaction.setCommitComment(comment);

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  /**
   * @author Eike Stepper
   */
  private static final class Handler implements CDOCommitInfoHandler
  {
    private List<CDOCommitInfo> infos = new ArrayList<CDOCommitInfo>();

    public List<CDOCommitInfo> getInfos()
    {
      return infos;
    }

    public void handleCommitInfo(CDOCommitInfo commitInfo)
    {
      infos.add(commitInfo);
    }
  }
}
