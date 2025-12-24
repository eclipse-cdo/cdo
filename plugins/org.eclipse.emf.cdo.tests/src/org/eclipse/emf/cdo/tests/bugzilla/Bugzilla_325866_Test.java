/*
 * Copyright (c) 2010-2013, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Pascal Lehmann - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.util.Map;

/**
 * IllegalStateException when committing a reattached containment of a detached container after branch merge with XRef
 * enabled.
 * <p>
 * See bug 325866
 *
 * @author Pascal Lehmann
 */
public class Bugzilla_325866_Test extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testXRefReattachBranch() throws Exception
  {
    skipStoreWithoutQueryXRefs();

    // setup transaction.
    final CDOSession session1 = openSession();
    final CDOTransaction s1Tr1 = session1.openTransaction();
    s1Tr1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    // create resource, and container tree using transaction 1.
    final CDOResource resource = s1Tr1.createResource(getResourcePath("/test1"));
    GenRefMultiContained container1 = getModel4Factory().createGenRefMultiContained();
    GenRefMultiContained container2 = getModel4Factory().createGenRefMultiContained();
    GenRefMultiContained container3 = getModel4Factory().createGenRefMultiContained();
    resource.getContents().add(container1);
    container1.getElements().add(container2);
    container2.getElements().add(container3);

    s1Tr1.commit();
    sleep(1000L);

    // setup another branch.
    final CDOBranch otherBranch = s1Tr1.getBranch().createBranch(getBranchName("other"));
    final CDOTransaction s1Tr3 = session1.openTransaction(otherBranch);

    CDOResource branchResource = s1Tr3.getObject(resource);
    assertNotSame(null, branchResource);

    GenRefMultiContained otherContainer1 = (GenRefMultiContained)branchResource.getContents().get(0);
    assertNotSame(null, otherContainer1);

    GenRefMultiContained otherContainer2 = (GenRefMultiContained)otherContainer1.getElements().get(0);
    assertNotSame(null, otherContainer2);

    GenRefMultiContained otherContainer3 = (GenRefMultiContained)otherContainer2.getElements().get(0);
    assertNotSame(null, otherContainer3);

    // detach the middle container on branch.
    otherContainer1.getElements().remove(otherContainer2);

    // re-attach detached's child container.
    otherContainer1.getElements().add(otherContainer3);

    s1Tr3.commit();
    assertEquals(false, s1Tr3.isDirty());

    // merge the other branch to main.
    sleep(1000L);
    s1Tr1.merge(s1Tr3.getBranch().getHead(), new DefaultCDOMerger.PerFeature.ManyValued());

    s1Tr1.commit();
    assertEquals(false, s1Tr1.isDirty());
  }

  @Requires(RepositoryConfig.CAPABILITY_BRANCHING)
  public void testXRefReattachBranchLevel2() throws Exception
  {
    // setup transaction.
    final CDOSession session1 = openSession();
    final CDOTransaction s1Tr1 = session1.openTransaction();
    s1Tr1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    // create resource, and container tree using transaction 1.
    final CDOResource resource = s1Tr1.createResource(getResourcePath("/test1"));
    GenRefMultiContained container1 = getModel4Factory().createGenRefMultiContained();
    GenRefMultiContained container2 = getModel4Factory().createGenRefMultiContained();
    GenRefMultiContained container3 = getModel4Factory().createGenRefMultiContained();
    GenRefMultiContained container4 = getModel4Factory().createGenRefMultiContained();
    GenRefMultiContained container5 = getModel4Factory().createGenRefMultiContained();
    resource.getContents().add(container1);
    resource.getContents().add(container2);
    resource.getContents().add(container3);
    container1.getElements().add(container4);
    container4.getElements().add(container5);

    s1Tr1.commit();
    sleep(1000L);

    // setup another branch.
    final CDOBranch otherBranch = s1Tr1.getBranch().createBranch(getBranchName("other"));
    final CDOTransaction s1Tr3 = session1.openTransaction(otherBranch);

    CDOResource branchResource = s1Tr3.getObject(resource);
    assertNotSame(null, branchResource);

    GenRefMultiContained otherContainer1 = (GenRefMultiContained)branchResource.getContents().get(0);
    assertNotSame(null, otherContainer1);
    GenRefMultiContained otherContainer2 = (GenRefMultiContained)branchResource.getContents().get(1);
    assertNotSame(null, otherContainer2);
    GenRefMultiContained otherContainer3 = (GenRefMultiContained)branchResource.getContents().get(2);
    assertNotSame(null, otherContainer3);
    GenRefMultiContained otherContainer4 = (GenRefMultiContained)otherContainer1.getElements().get(0);
    assertNotSame(null, otherContainer4);
    GenRefMultiContained otherContainer5 = (GenRefMultiContained)otherContainer4.getElements().get(0);
    assertNotSame(null, otherContainer5);

    // detach the container1 on branch.
    branchResource.getContents().remove(otherContainer1);

    // re-attach container1 child.
    otherContainer2.getElements().add(otherContainer4);

    // re-attach container1 child.
    otherContainer3.getElements().add(otherContainer5);

    s1Tr3.commit();
    assertEquals(false, s1Tr3.isDirty());

    sleep(1000L);

    // merge the other branch to main.
    CDOBranchPoint head = s1Tr3.getBranch().getHead();
    CDOMerger merger = new DefaultCDOMerger.PerFeature.ManyValued();
    s1Tr1.merge(head, merger);

    s1Tr1.commit();
    assertEquals(false, s1Tr1.isDirty());
  }
}
