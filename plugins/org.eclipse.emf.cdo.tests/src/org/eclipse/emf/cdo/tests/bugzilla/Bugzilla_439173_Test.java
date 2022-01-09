/*
 * Copyright (c) 2014, 2015 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * See bug 439173.
 *
 * @author Leonid Ripeynih
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_439173_Test extends AbstractCDOTest
{
  public void testSwitchTargetVersion() throws Exception
  {
    CDOSession session = openSession();

    // Create an object in main branch
    //
    CDOTransaction mainTx = session.openTransaction();
    String path = getResourcePath("test");
    CDOResource resource = mainTx.createResource(path);
    resource.getContents().add(createCategory("cat1"));
    mainTx.commit();
    mainTx.close();

    // Create a branch and change object
    //
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch(getBranchName("branch"));
    CDOTransaction branchTx = session.openTransaction(branch);
    resource = branchTx.getResource(path);

    // Change resource in branch twice (so it's version becomes 2)
    //
    resource.getContents().add(createCategory("cat2"));
    branchTx.commit();
    resource.getContents().add(createCategory("cat3"));
    branchTx.commit();
    branchTx.close();

    // Open another session to invalidate cache
    //
    session.close();
    CDOSession cleanSession = openSession();

    // Open view on new session and switch target
    //
    CDOView view = cleanSession.openView();
    resource = view.getResource(path);
    resource.getContents().size();

    assertEquals(1, resource.cdoRevision().getVersion());
    view.setBranch(branch);
    assertEquals(2, resource.cdoRevision().getVersion());
  }

  public void testSwitchTargetVersionResourceID() throws Exception
  {
    CDOSession session = openSession();

    // Create an object in main branch
    //
    CDOTransaction mainTx = session.openTransaction();
    String sourcePath = getResourcePath("source");
    String targetPath = getResourcePath("target");
    CDOResource resource = mainTx.createResource(sourcePath);
    Category category = createCategory("cat1");
    resource.getContents().add(category);

    mainTx.commit();
    CDOID sourceResourceID = resource.cdoID();
    mainTx.close();

    // Create a branch and change object
    //
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch(getBranchName("branch"));
    CDOTransaction branchTx = session.openTransaction(branch);
    resource = branchTx.getResource(sourcePath);

    // Change category resource
    //
    CDOResource targetResource = branchTx.createResource(targetPath);
    targetResource.getContents().addAll(resource.getContents());

    branchTx.commit();
    CDOID targetResourceID = targetResource.cdoID();
    branchTx.close();

    // Open another session to invalidate cache
    //
    session.close();
    CDOSession cleanSession = openSession();

    // Open view on new session and switch target
    //
    CDOView view = cleanSession.openView();
    resource = view.getResource(sourcePath);
    category = (Category)resource.getContents().get(0);
    category.getName();

    assertEquals(sourceResourceID, ((InternalCDORevision)CDOUtil.getCDOObject(category).cdoRevision()).getResourceID());
    view.setBranch(branch);
    assertEquals(targetResourceID, ((InternalCDORevision)CDOUtil.getCDOObject(category).cdoRevision()).getResourceID());
  }

  private Category createCategory(String name)
  {
    Category category = getModel1Factory().createCategory();
    category.setName(name);
    return category;
  }
}
