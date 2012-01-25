/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * @author Stefan Winkler
 */
public class Bugzilla_369646_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testCascadingBranches() throws CommitException
  {
    // set up a resource with 1 object as content
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/test"));

    Category c = getModel1Factory().createCategory();
    c.setName("Test");
    res.getContents().add(c);
    transaction.commit();

    // create two cascading branches
    CDOBranch sub1 = transaction.getBranch().createBranch("sub1");
    CDOBranch sub2 = sub1.createBranch("sub2");

    // now delete the contents in the sub2 branch
    transaction.setBranch(sub2);
    res.getContents().remove(0);
    transaction.commit();

    // and now try to switch the transaction to the parent branch
    transaction.setBranch(sub1);
  }
}
