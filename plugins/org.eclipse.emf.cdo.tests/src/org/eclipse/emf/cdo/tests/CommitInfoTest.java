/*
 * Copyright (c) 2010-2013, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.commit.handler.AsyncCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.handler.TextCommitInfoLog;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.IOUtil;
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
  private static final String REPO_NAME = "commitinforepo";

  // private static final String REPO_NAME = IRepositoryConfig.REPOSITORY_NAME;

  private static final String USER_ID = "stepper";

  private static final char[] PASSWORD = "eike2010".toCharArray();

  private static final String RESOURCE_PATH = "/res";

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    skipStoreWithoutCommitInfos();
  }

  public void testLocalTimestamp() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    long timeStamp = commitInfo.getTimeStamp();
    assertEquals(true, timeStamp > CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public void testLocalBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(transaction.getBranch(), commitInfo.getBranch());
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testLocalSubBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(transaction.getBranch(), commitInfo.getBranch());
  }

  public void testLocalUser() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_AUTHENTICATOR, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(USER_ID, commitInfo.getUserID());
  }

  public void testLocalComment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    String comment = "Andre";
    transaction.setCommitComment(comment);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(comment, commitInfo.getComment());
  }

  @Skips("MongoDB")
  @CleanRepositoriesBefore(reason = "Commit info counting")
  public void testServerTimestamp() throws Exception
  {
    CDOSession session = openSession();
    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    StoreThreadLocal.setSession(serverSession);

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(2, infos.size()); // Initial root resource commit + 1
      assertEquals(commitInfo.getTimeStamp(), infos.get(0).getTimeStamp());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Skips("MongoDB")
  @CleanRepositoriesBefore(reason = "Commit info counting")
  public void testServerBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);

      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(2, infos.size()); // Initial root resource commit + 1
      assertEquals(commitInfo.getBranch().getID(), infos.get(0).getBranch().getID());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  @CleanRepositoriesBefore(reason = "Commit info counting")
  public void testServerSubBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
      CDOTransaction transaction = session.openTransaction(branch);
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(2, infos.size()); // Initial root resource commit + 1
      assertEquals(commitInfo.getBranch().getID(), infos.get(0).getBranch().getID());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Skips("MongoDB")
  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testServerUser() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_AUTHENTICATOR, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    StoreThreadLocal.setSession(getRepository(REPO_NAME).getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository(REPO_NAME).getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(2, infos.size()); // Initial root resource commit + 1
      assertEquals(commitInfo.getUserID(), infos.get(0).getUserID());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Skips("MongoDB")
  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testServerComment() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      String comment = "Andre";
      transaction.setCommitComment(comment);

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(2, infos.size()); // Initial root resource commit + 1
      assertEquals(commitInfo.getComment(), infos.get(0).getComment());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Skips("MongoDB")
  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testServerTimestampWithBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(getRepository().getBranchManager().getBranch(transaction.getBranch().getID()),
          CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(2, infos.size()); // Initial root resource commit + 1
      assertEquals(commitInfo.getTimeStamp(), infos.get(0).getTimeStamp());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Skips("MongoDB")
  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testServerBranchWithBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(getRepository().getBranchManager().getBranch(transaction.getBranch().getID()),
          CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(2, infos.size()); // Initial root resource commit + 1
      assertEquals(commitInfo.getBranch().getID(), infos.get(0).getBranch().getID());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testServerSubBranchWithBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
      CDOTransaction transaction = session.openTransaction(branch);
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(getRepository().getBranchManager().getBranch(transaction.getBranch().getID()),
          CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(1, infos.size());
      assertEquals(commitInfo.getBranch().getID(), infos.get(0).getBranch().getID());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Skips("MongoDB")
  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testServerUserWithBranch() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_AUTHENTICATOR, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    StoreThreadLocal.setSession(getRepository(REPO_NAME).getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository(REPO_NAME).getCommitInfoManager().getCommitInfos(getRepository(REPO_NAME).getBranchManager().getBranch(transaction.getBranch().getID()),
          CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(2, infos.size()); // Initial root resource commit + 1
      assertEquals(commitInfo.getUserID(), infos.get(0).getUserID());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Skips("MongoDB")
  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testServerCommentWithBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      String comment = "Andre";
      transaction.setCommitComment(comment);

      CDOCommitInfo commitInfo = transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(getRepository().getBranchManager().getBranch(transaction.getBranch().getID()),
          CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(2, infos.size()); // Initial root resource commit + 1
      assertEquals(commitInfo.getComment(), infos.get(0).getComment());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testServerTimestampWithWrongBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOBranch wrong = getRepository().getBranchManager().getMainBranch().createBranch("wrong");
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(0, infos.size());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testServerBranchWithWrongBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOBranch wrong = getRepository().getBranchManager().getMainBranch().createBranch("wrong");
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(0, infos.size());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testServerSubBranchWithWrongBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOBranch wrong = getRepository().getBranchManager().getMainBranch().createBranch("wrong");
      CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
      CDOTransaction transaction = session.openTransaction(branch);
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(0, infos.size());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testServerUserWithWrongBranch() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_AUTHENTICATOR, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    StoreThreadLocal.setSession(getRepository(REPO_NAME).getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOBranch wrong = getRepository(REPO_NAME).getBranchManager().getMainBranch().createBranch("wrong");
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      transaction.commit();

      Handler handler = new Handler();
      getRepository(REPO_NAME).getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(0, infos.size());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testServerCommentWithWrongBranch() throws Exception
  {
    CDOSession session = openSession();
    StoreThreadLocal.setSession(getRepository().getSessionManager().getSession(session.getSessionID()));

    try
    {
      CDOBranch wrong = getRepository().getBranchManager().getMainBranch().createBranch("wrong");
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
      resource.getContents().add(getModel1Factory().createProduct1());

      String comment = "Andre";
      transaction.setCommitComment(comment);

      transaction.commit();

      Handler handler = new Handler();
      getRepository().getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
      List<CDOCommitInfo> infos = handler.getInfos();

      assertEquals(0, infos.size());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testClientTimestamp() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getTimeStamp(), infos.get(0).getTimeStamp());
  }

  public void testClientPreviousCommitInfo() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    resource.getContents().add(getModel1Factory().createProduct1());
    CDOCommitInfo commitInfo1 = transaction.commit();

    resource.getContents().add(getModel1Factory().createProduct1());
    CDOCommitInfo commitInfo2 = transaction.commit();
    assertNotSame(commitInfo1, commitInfo2);
    assertEquals(commitInfo1, commitInfo2.getPreviousCommitInfo());

    resource.getContents().add(getModel1Factory().createProduct1());
    CDOCommitInfo commitInfo3 = transaction.commit();
    assertNotSame(commitInfo1, commitInfo3);
    assertNotSame(commitInfo2, commitInfo3);
    assertEquals(commitInfo2, commitInfo3.getPreviousCommitInfo());
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testClientBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getBranch().getID(), infos.get(0).getBranch().getID());
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testClientSubBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getBranch().getID(), infos.get(0).getBranch().getID());
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testClientUser() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_AUTHENTICATOR, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getUserID(), infos.get(0).getUserID());
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testClientComment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    String comment = "Andre";
    transaction.setCommitComment(comment);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getComment(), infos.get(0).getComment());
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testClientTimestampWithBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getTimeStamp(), infos.get(0).getTimeStamp());
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testClientBranchWithBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getBranch().getID(), infos.get(0).getBranch().getID());
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testClientSubBranchWithBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(1, infos.size());
    assertEquals(commitInfo.getBranch().getID(), infos.get(0).getBranch().getID());
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testClientUserWithBranch() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_AUTHENTICATOR, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getUserID(), infos.get(0).getUserID());
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testClientCommentWithBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    String comment = "Andre";
    transaction.setCommitComment(comment);

    CDOCommitInfo commitInfo = transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(transaction.getBranch(), CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(2, infos.size()); // Initial root resource commit + 1
    assertEquals(commitInfo.getComment(), infos.get(0).getComment());
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testClientTimestampWithWrongBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testClientBranchWithWrongBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testClientSubBranchWithWrongBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch("sub");
    CDOTransaction transaction = session.openTransaction(branch);
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testClientUserWithWrongBranch() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    getTestProperties().put(RepositoryConfig.PROP_TEST_AUTHENTICATOR, userManager);
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD)));

    getRepository(REPO_NAME);

    CDOSession session = openSession(REPO_NAME);
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testClientCommentWithWrongBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch wrong = session.getBranchManager().getMainBranch().createBranch("wrong");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(getModel1Factory().createProduct1());

    String comment = "Andre";
    transaction.setCommitComment(comment);

    transaction.commit();

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(wrong, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);
    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(0, infos.size());
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testMultipleEntries() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    List<CDOCommitInfo> expected = new ArrayList<>();
    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      expected.add(transaction.commit());
    }

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, handler);

    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(1 + COMMITS, infos.size()); // Initial root resource commit + COMMITS
    for (int i = 0; i < COMMITS; i++)
    {
      assertEquals(expected.get(COMMITS - i - 1), infos.get(i));
    }
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testMultipleEntriesByCountDown() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    List<CDOCommitInfo> expected = new ArrayList<>();
    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      expected.add(transaction.commit());
    }

    final int LOAD = 10;

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, null, null, -LOAD, handler);

    List<CDOCommitInfo> infos = handler.getInfos();

    assertEquals(LOAD, infos.size()); // Initial root resource commit + COMMITS
    for (int i = 0; i < LOAD; i++)
    {
      assertEquals(expected.get(COMMITS - i - 1), infos.get(i));
    }
  }

  @CleanRepositoriesBefore(reason = "Commit info counting")
  @CleanRepositoriesAfter(reason = "Commit info counting")
  public void testMultipleEntriesByCountUp() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    List<CDOCommitInfo> expected = new ArrayList<>();
    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      CDOCommitInfo commit = transaction.commit();
      expected.add(commit);
      IOUtil.OUT().println(expected.get(i).getTimeStamp());
    }

    final int LOAD = 10;

    Handler handler = new Handler();
    session.getCommitInfoManager().getCommitInfos(null, expected.get(0).getTimeStamp(), null, null, LOAD, handler);

    List<CDOCommitInfo> infos = handler.getInfos();

    for (int i = 0; i < LOAD; i++)
    {
      IOUtil.OUT().println(expected.get(i).getTimeStamp() + " -> " + infos.get(i).getTimeStamp());
    }

    assertEquals(LOAD, infos.size()); // Initial root resource commit + COMMITS
    for (int i = 0; i < LOAD; i++)
    {
      assertEquals(expected.get(i), infos.get(i));
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testLogThroughClient() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      transaction.commit();
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    TextCommitInfoLog log = new TextCommitInfoLog(baos);

    session.getCommitInfoManager().getCommitInfos(null, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, log);

    System.out.println(baos.toString());
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testLogThroughWriteAccessHandler() throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final TextCommitInfoLog log = new TextCommitInfoLog(baos);

    getRepository().addHandler(new IRepository.WriteAccessHandler()
    {
      @Override
      public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
      {
      }

      @Override
      public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
      {
        log.handleCommitInfo(commitContext.createCommitInfo());
      }
    });

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      transaction.commit();
    }

    System.out.println(baos.toString());
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testLogThroughCommitInfoHandler() throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    getRepository().getCommitInfoManager().addCommitInfoHandler(new TextCommitInfoLog(baos));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    final int COMMITS = 20;
    for (int i = 0; i < COMMITS; i++)
    {
      resource.getContents().add(getModel1Factory().createProduct1());
      transaction.setCommitComment("Commit " + i);
      transaction.commit();
    }

    System.out.println(baos.toString());
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testLogAsync() throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AsyncCommitInfoHandler log = new AsyncCommitInfoHandler(new TextCommitInfoLog(baos));
    log.activate();
    getRepository().getCommitInfoManager().addCommitInfoHandler(log);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

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
    private List<CDOCommitInfo> infos = new ArrayList<>();

    public List<CDOCommitInfo> getInfos()
    {
      return infos;
    }

    @Override
    public void handleCommitInfo(CDOCommitInfo commitInfo)
    {
      infos.add(commitInfo);
    }
  }
}
