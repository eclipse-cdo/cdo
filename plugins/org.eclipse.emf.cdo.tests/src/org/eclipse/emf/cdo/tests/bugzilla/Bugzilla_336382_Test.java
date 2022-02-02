/*
 * Copyright (c) 2011, 2012, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * @author Stefan Winkler
 */
public class Bugzilla_336382_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testPromoteToBranch() throws Exception
  {
    skipStoreWithoutChangeSets();

    CDOSession session = openSession();

    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    long timestamp = mainBranch.getBase().getTimeStamp();
    CDOBranch sourceBranch = mainBranch.createBranch(getBranchName("branch1"), timestamp);

    {
      CDOTransaction tx = session.openTransaction(sourceBranch);
      CDOResource res = tx.createResource(getResourcePath("/test"));

      Company company = getModel1Factory().createCompany();
      company.setName("Foo");
      company.setStreet("Bar");
      company.setCity("somewhere");
      res.getContents().add(company);
      tx.commit();
    }

    CDOBranch targetBranch = mainBranch.createBranch(getBranchName("branch2"), timestamp);

    CDOTransaction transaction = session.openTransaction(targetBranch);

    CDOMerger merger = new DefaultCDOMerger.PerFeature.ManyValued();
    transaction.merge(sourceBranch.getHead(), merger);
    transaction.commit();
  }
}
