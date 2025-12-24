/*
 * Copyright (c) 2010-2012, 2016, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * InvalidObjectException when switching branch
 * <p>
 * See bug 303807
 *
 * @author Victor Roldan Betancort
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_303807_Test extends AbstractCDOTest
{
  public void testBugzilla_303807() throws Exception
  {
    CDOSession session = openSession();

    // Commit to main branch a new resource
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    transaction.createResource(getResourcePath("/test1"));
    transaction.commit();

    // Switch transaction to a new branch
    CDOBranch newBranch = mainBranch.createBranch(getBranchName("foobar"));
    transaction.setBranch(newBranch);

    transaction.getRootResource().getContents().size();
  }
}
