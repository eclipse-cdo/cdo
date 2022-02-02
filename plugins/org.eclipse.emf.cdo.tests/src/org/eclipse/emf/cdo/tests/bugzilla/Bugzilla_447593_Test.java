/*
 * Copyright (c) 2014, 2015, 2022 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * Test calling to {@link CDOTransaction#setBranch(CDOBranch)} twice. It causes "IllegalStateException : Already in cache".
 *
 * @author Esteban Dugueperoux
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_447593_Test extends AbstractCDOTest
{
  public void testTwiceCDOTransactionSetBranch() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    String resourcePath = getResourcePath("test1.model1");
    CDOResource resource = transaction.createResource(resourcePath);
    Category category = getModel1Factory().createCategory();
    category.setName("categoryFromMainBranch");
    resource.getContents().add(category);
    transaction.commit();

    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOBranch b1ParentBranch = mainBranch.createBranch(getBranchName("b1Parent"));
    CDOBranch b1 = b1ParentBranch.createBranch("b1");

    transaction.enableDurableLocking();
    CDOUtil.getCDOObject(category).cdoWriteLock().lock();
    transaction.setBranch(b1);
    resource = transaction.getResource(resourcePath);
    category = (Category)resource.getContents().get(0);
    category.setName("categoryFromB1Branch");
    resource.getContents().add(getModel1Factory().createCategory());
    transaction.commit();

    CDOBranch b11 = b1.createBranch("b11");

    CDOUtil.getCDOObject(category).cdoWriteLock().lock();
    transaction.setBranch(b11);
    resource = transaction.getResource(resourcePath);
    category = (Category)resource.getContents().get(0);
    category.setName("categoryFromB11Branch");
    resource.getContents().add(getModel1Factory().createCategory());
    transaction.commit();
  }
}
