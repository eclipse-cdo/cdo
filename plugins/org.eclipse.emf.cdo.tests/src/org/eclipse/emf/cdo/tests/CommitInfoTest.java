/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.commit.AsyncCommitInfoHandler;
import org.eclipse.emf.cdo.spi.common.commit.TextCommitInfoLog;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.UserManager;

import java.io.ByteArrayOutputStream;
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
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    long timeStamp = commitInfo.getTimeStamp();
    assertEquals(true, timeStamp > CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public void testLocalBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(USER_ID, commitInfo.getUserID());
  }

  public void testLocalComment() throws Exception
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

  public void testServerTimestamp() throws Exception
  {
    skipMongo();

    CDOSession session = openSession();
    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    StoreThreadLocal.setSession(serverSession);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

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
    skipMongo();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    skipMongo();

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    skipMongo();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

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
    skipMongo();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

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
    skipMongo();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    skipMongo();

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    skipMongo();

    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

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
    resource.getContents().add(getModel1Factory().createProduct1());

    String comment = "Andre";
    transaction.setCommitComment(comment);

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  public void testMultipleEntries() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);

    List<CDOCommitInfo> expected = new ArrayList<CDOCommitInfo>();
    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      expected.add(transaction.commit());
    }

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, handler);

    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(1 + COMMITS, infos.size()); // Initial root resource commit + COMMITS
    for (int i = 0; i < COMMITS; i++)
    {
      assertEquals(expected.get(i), infos.get(1 + i));
    }
  }

  public void testLogThroughClient() throws Exception
  {
    skipUnlessAuditing();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);

    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      transaction.commit();
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    TextCommitInfoLog log = new TextCommitInfoLog(baos);

    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE,
        CDOBranchPoint.UNSPECIFIED_DATE, log);

    System.out.println(baos.toString());
  }

  public void testLogThroughWriteAccessHandler() throws Exception
  {
    skipUnlessAuditing();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final TextCommitInfoLog log = new TextCommitInfoLog(baos);

    getRepository().addHandler(new IRepository.WriteAccessHandler()
    {
      public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext,
          OMMonitor monitor) throws RuntimeException
      {
      }

      public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext,
          OMMonitor monitor)
      {
        log.handleCommitInfo(commitContext.createCommitInfo());
      }
    });

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);

    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      transaction.commit();
    }

    System.out.println(baos.toString());
  }

  public void testLogThroughCommitInfoHandler() throws Exception
  {
    skipUnlessAuditing();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    getRepository().addCommitInfoHandler(new TextCommitInfoLog(baos));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);

    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      transaction.commit();
    }

    System.out.println(baos.toString());
  }

  public void testLogAsync() throws Exception
  {
    skipUnlessAuditing();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AsyncCommitInfoHandler log = new AsyncCommitInfoHandler(new TextCommitInfoLog(baos));
    log.activate();
    getRepository().addCommitInfoHandler(log);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(RESOURCE_PATH);

    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      transaction.commit();
    }

    log.deactivate();
    LifecycleUtil.waitForInactive(log, Long.MAX_VALUE);

    System.out.println(baos.toString());
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
