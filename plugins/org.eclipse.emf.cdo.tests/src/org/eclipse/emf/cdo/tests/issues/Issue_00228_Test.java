/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.issues;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig.ChunkConfig;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.security.PasswordCredentials;

import java.util.Collections;

/**
 * Verifies that server-side security realm validation sees fully loaded lists,
 * even when the server revision cache contains partially loaded revisions.
 *
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_CHUNKING, "DB" })
@CleanRepositoriesBefore(reason = "TEST_SECURITY_MANAGER")
@CleanRepositoriesAfter(reason = "TEST_SECURITY_MANAGER")
public class Issue_00228_Test extends AbstractCDOTest
{
  private static final String REALM_PATH = "/security";

  private static final PasswordCredentials ADMINISTRATOR = new PasswordCredentials(User.ADMINISTRATOR, User.ADMINISTRATOR_DEFAULT_PASSWORD);

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    ISecurityManager securityManager = SecurityManagerUtil.createSecurityManager(REALM_PATH, getServerContainer());

    getTestProperties().put(RepositoryConfig.PROP_TEST_SECURITY_MANAGER, securityManager);
    getRepository();

    securityManager.modify(realm -> {
      Role administration = realm.getRole(Role.ADMINISTRATION);
      administration.getPermissions().add(0,
          SecurityFactory.eINSTANCE.createFilterPermission(Access.WRITE, SecurityFactory.eINSTANCE.createClassFilter(SecurityPackage.Literals.REALM),
              SecurityFactory.eINSTANCE.createResourceFilter(REALM_PATH, PatternStyle.EXACT, false)));
    });
  }

  @SuppressWarnings("deprecation")
  public void testRealmValidationWithCollectionLoadingPolicy() throws Exception
  {
    CDOSession session = openSession(ADMINISTRATOR);
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));
    commitRealmChange(session);
  }

  public void testRealmValidationWithCollectionLoadingConfig() throws Exception
  {
    CDOSession session = openSession(ADMINISTRATOR);
    session.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(1, 1), Collections.emptyMap()));
    commitRealmChange(session);
  }

  private void commitRealmChange(CDOSession session) throws Exception
  {
    InternalCDORevisionManager serverRevisionManager = getRepository().getRevisionManager();
    clearCache(serverRevisionManager);

    CDOTransaction transaction = session.openTransaction();
    Realm realm = (Realm)transaction.getResource(REALM_PATH).getContents().get(0);
    User administrator = realm.getUser(User.ADMINISTRATOR);

    FilterPermission permission = (FilterPermission)administrator.getAllPermissions().get(0);
    CDORevision clientRevision = CDOUtil.getCDOObject(permission).cdoRevision();

    CDOBranch serverBranch = getRepository().getBranchManager().getBranch(clientRevision.getBranch().getID());
    CDOBranchVersion serverVersion = serverBranch.getVersion(clientRevision.getVersion());

    InternalCDORevision serverRevision = (InternalCDORevision)serverRevisionManager.getCache().getRevisionByVersion(clientRevision.getID(), serverVersion);

    CDOList serverFilters = serverRevision.getListOrNull(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS);

    assertEquals(2, serverFilters.size());
    assertFalse(serverFilters.isLoadedAt(1));

    realm.setName("Changed Realm");
    transaction.commit();
  }
}
