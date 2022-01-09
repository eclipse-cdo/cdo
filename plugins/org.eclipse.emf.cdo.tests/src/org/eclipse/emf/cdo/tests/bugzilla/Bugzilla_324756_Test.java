/*
 * Copyright (c) 2010-2012, 2016 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.MultiContainedElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiContained;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

/**
 * NPE in TransactionCommitContext with re-attached object on branch.
 * <p>
 * See bug 324756
 *
 * @author Pascal Lehmann
 */
public class Bugzilla_324756_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testReattachBranchVersion() throws Exception
  {
    // setup transaction.
    final CDOSession session1 = openSession();
    final CDOTransaction s1Tr1 = session1.openTransaction();
    s1Tr1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    // create resource, container using transaction 1.
    final CDOResource resource = s1Tr1.createResource(getResourcePath("/test1"));
    RefMultiContained container = getModel4Factory().createRefMultiContained();
    resource.getContents().add(container);

    s1Tr1.commit();

    // do a couple of changes to have the version increase.
    MultiContainedElement element1 = getModel4Factory().createMultiContainedElement();
    container.getElements().add(element1);

    s1Tr1.commit();

    container.getElements().remove(0);

    s1Tr1.commit();

    // setup another branch.
    final CDOBranch otherBranch = s1Tr1.getBranch().createBranch(getBranchName("other"));
    final CDOTransaction s1Tr3 = session1.openTransaction(otherBranch);

    CDOResource branchResource = s1Tr3.getObject(resource);
    assertNotSame(null, branchResource);

    // detach container on branch.
    RefMultiContained otherContainer = (RefMultiContained)branchResource.getContents().remove(0);
    assertNotSame(null, otherContainer);

    // re-attach container.

    branchResource.getContents().add(otherContainer);

    s1Tr3.commit(); // <--- This will store the revision with the wrong version.

    // do a change to create a RevisionDelta with wrong version.
    MultiContainedElement element2 = getModel4Factory().createMultiContainedElement();
    otherContainer.getElements().add(element2);

    s1Tr3.commit(); // <--- This will throw the exception:
    // java.lang.NullPointerException at
    // org.eclipse.emf.cdo.internal.server.TransactionCommitContext.isContainerLocked(TransactionCommitContext.java:712)

    assertEquals(false, s1Tr3.isDirty());
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testReattachBranchVersion2() throws Exception
  {
    // setup transaction.
    final CDOSession session1 = openSession();
    final CDOTransaction s1Tr1 = session1.openTransaction();
    s1Tr1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    // create resource, element using transaction 1.
    final CDOResource resource = s1Tr1.createResource(getResourcePath("/test1"));
    ContainedElementNoOpposite element = getModel4Factory().createContainedElementNoOpposite();
    element.setName("Version1");
    resource.getContents().add(element);

    s1Tr1.commit();

    // do a couple of changes to have the version increase.
    element.setName("Version2");

    s1Tr1.commit();

    element.setName("Version3");

    s1Tr1.commit();

    // setup another branch.
    final CDOBranch otherBranch = s1Tr1.getBranch().createBranch(getBranchName("other"));
    final CDOTransaction s1Tr3 = session1.openTransaction(otherBranch);

    CDOResource branchResource = s1Tr3.getObject(resource);
    assertNotSame(null, branchResource);

    // detach container on branch.
    ContainedElementNoOpposite otherElement = (ContainedElementNoOpposite)branchResource.getContents().remove(0);
    assertNotSame(null, otherElement);

    // re-attach container.

    branchResource.getContents().add(otherElement);

    s1Tr3.commit(); // <--- This will store the revision with the wrong version.

    // do a change to create a RevisionDelta with wrong version.
    otherElement.setName("BranchVersion2");

    s1Tr3.commit(); // <--- This will throw the exception:
    // java.lang.IllegalStateException: Origin revision not found for
    // CDORevisionDelta[ContainedElementNoOpposite@OID2:1v4 --> [CDOFeatureDelta[name, SET, value=BranchVersion2]]]

    assertEquals(false, s1Tr3.isDirty());
  }
}
