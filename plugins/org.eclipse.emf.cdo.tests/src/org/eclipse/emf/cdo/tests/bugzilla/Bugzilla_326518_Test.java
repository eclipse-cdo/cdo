/*
 * Copyright (c) 2010-2013, 2016, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Lehmann - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.util.Map;

/**
 * List index problem during merge can cause a StaleReference in database even with XRef checking enabled.
 * <p>
 * See bug 326518
 *
 * @author Pascal Lehmann
 */
public class Bugzilla_326518_Test extends AbstractCDOTest
{
  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> map = super.getTestProperties();
    map.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");
    return map;
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testIndexBranchMerge() throws Exception
  {
    skipStoreWithoutQueryXRefs();

    // setup transaction.
    final CDOSession session = openSession();
    final CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    // create resource, and container using transaction 1.
    final CDOResource resource = transaction1.createResource(getResourcePath("/test1"));
    GenRefMultiContained container = getModel4Factory().createGenRefMultiContained();
    resource.getContents().add(container);
    ContainedElementNoOpposite element1 = getModel4Factory().createContainedElementNoOpposite();
    ContainedElementNoOpposite element2 = getModel4Factory().createContainedElementNoOpposite();
    container.getElements().add(element1);
    container.getElements().add(element2);

    transaction1.commit();
    sleep(1000L);

    // setup another branch.
    final CDOBranch branch2 = transaction1.getBranch().createBranch(getBranchName("branch2"));
    final CDOTransaction transaction2 = session.openTransaction(branch2);

    CDOResource branchResource = transaction2.getObject(resource);
    assertNotSame(null, branchResource);
    GenRefMultiContained otherContainer = (GenRefMultiContained)branchResource.getContents().get(0);
    assertNotSame(null, otherContainer);

    // detach the first element on branch.
    otherContainer.getElements().remove(0);
    // add a new element on branch at index0.
    otherContainer.getElements().add(0, getModel4Factory().createContainedElementNoOpposite());

    transaction2.commit();
    assertEquals(false, transaction2.isDirty());

    // detach the second element on main.
    container.getElements().remove(1);
    // add a new element on main at index0.
    container.getElements().add(0, getModel4Factory().createContainedElementNoOpposite());

    transaction1.commit();
    assertEquals(false, transaction1.isDirty());

    // merge the other branch to main.
    sleep(1000L);
    CDOChangeSetData changes = transaction1.merge(transaction2.getBranch().getHead(), new DefaultCDOMerger.PerFeature.ManyValued());

    printChangeSetData(changes);

    try
    {
      transaction1.commit();
    }
    catch (CommitException expected)
    {
      fail("No CommitException expected");
    }

    // access the container's elements.
    for (Object object : container.getElements()) // <-- will cause an ObjectNotFoundException.
    {
      System.out.println(object);
    }
  }

  public static void printChangeSetData(CDOChangeSetData changes)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("## ChangeSet:\n");
    sb.append(" * New objects: ").append(changes.getNewObjects().size()).append("\n");
    for (CDOIDAndVersion idVersion : changes.getNewObjects())
    {
      sb.append("   - ").append(idVersion).append("\n");
    }

    sb.append(" * Changed objects: ").append(changes.getChangedObjects().size()).append("\n");
    for (CDORevisionKey revKey : changes.getChangedObjects())
    {
      sb.append("   - ").append(revKey).append("\n");
    }

    sb.append(" * Detached objects: ").append(changes.getNewObjects().size()).append("\n");
    for (CDOIDAndVersion idVersion : changes.getNewObjects())
    {
      sb.append("   - ").append(idVersion).append("\n");
    }

    System.out.println(sb);
  }
}
