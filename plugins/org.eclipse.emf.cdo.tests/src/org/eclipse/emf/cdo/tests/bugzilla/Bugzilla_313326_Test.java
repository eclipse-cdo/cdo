/*
 * Copyright (c) 2010-2013, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model4.RefSingleContained;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * NullPointerException in CDONotificationBuilder during Branch Merge
 * <p>
 * See bug 313326
 */
public class Bugzilla_313326_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testNotificationBuilderBranch() throws Exception
  {
    skipStoreWithoutChangeSets();

    // setup transaction.
    final CDOSession session = openSession();
    final CDOTransaction tr1 = session.openTransaction();
    tr1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    // init model with a container.
    final CDOResource resource = tr1.createResource(getResourcePath("/test1"));
    RefSingleContained container = getModel4Factory().createRefSingleContained();
    container.setElement(getModel4Factory().createSingleContainedElement());

    // attach adapter to get notifications.
    container.eAdapters().add(new TestAdapter());
    resource.getContents().add(container);
    tr1.commit();

    // create another branch
    final CDOBranch mainBranch = tr1.getBranch();
    final CDOBranch otherBranch = mainBranch.createBranch(getBranchName("other"));
    final CDOTransaction tr2 = session.openTransaction(otherBranch);

    // get container in other branch and remove containment.
    RefSingleContained otherContainer = tr2.getObject(container);
    assertNotNull(otherContainer);
    otherContainer.setElement(null);
    tr2.commit();

    // sleep to have the merger see the changes.
    sleep(500);
    // tr1.waitForUpdate(tr2.getLastCommitTime(), DEFAULT_TIMEOUT);

    // merge the other branch to main.
    tr1.merge(tr2.getBranch().getHead(), new DefaultCDOMerger.PerFeature());

    tr1.commit();
    assertEquals(false, tr1.isDirty());
  }
}
