/*
 * Copyright (c) 2014, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitNotificationIndication;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Test that {@link CommitNotificationIndication} {@link CDOCommitInfo} decoding with {@link PointerCDORevision} on branch.
 *
 * @author Esteban Dugueperoux
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore(reason = "to not be disturb by branches created by others tests")
public class Bugzilla_449171_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  public void testCommitNotificationIndicationCDOCommitInfoDecoding() throws Exception
  {
    CDOSession session1 = openSession();
    String subBranchName = getBranchName("b1");
    CDOSession session2 = openSession();
    CDOTransaction txOfSession1 = session1.openTransaction();
    CDOResource resource = txOfSession1.getOrCreateResource(getResourcePath(RESOURCE_NAME));
    resource.getContents().add(getModel1Factory().createCompany());
    txOfSession1.commit();
    txOfSession1.close();
    session1.close();
    session1 = openSession();
    CDOBranch subBranch = session1.getBranchManager().getMainBranch().createBranch(subBranchName);
    txOfSession1 = session1.openTransaction(subBranch);
    resource = txOfSession1.getResource(getResourcePath(RESOURCE_NAME));
    resource.getContents().remove(0);

    CDOTransaction txOfSession2 = session2.openTransaction(subBranch);

    commitAndSync(txOfSession1, txOfSession2);
  }
}
