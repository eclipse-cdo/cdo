/*
 * Copyright (c) 2010-2012, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Bugzilla_320690_Test extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");
  }

  public void testLockRefTargets() throws Exception
  {
    skipStoreWithoutQueryXRefs();

    CDOSession session1 = openSession();
    session1.options().setPassiveUpdateEnabled(false);
    CDOTransaction transaction1 = session1.openTransaction();

    msg("Creating test data");
    CDOResource resource1 = transaction1.createResource(getResourcePath("/test"));
    RefSingleNonContainedNPL nonContainer1 = getModel4Factory().createRefSingleNonContainedNPL();
    RefSingleContainedNPL container1 = getModel4Factory().createRefSingleContainedNPL();
    ContainedElementNoOpposite contained1 = getModel4Factory().createContainedElementNoOpposite();
    container1.setElement(contained1);

    resource1.getContents().add(container1);
    resource1.getContents().add(nonContainer1);
    transaction1.commit();
    dumpAllRevisions(getRepository().getStore());

    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateEnabled(false);
    CDOTransaction transaction2 = session2.openTransaction();
    RefSingleNonContainedNPL nonContainer2 = transaction2.getObject(nonContainer1);
    ContainedElementNoOpposite contained2 = transaction2.getObject(contained1);

    nonContainer2.setElement(contained2);

    // Transaction 1
    container1.setElement(null);
    transaction1.commit();
    dumpAllRevisions(getRepository().getStore());

    try
    {
      // This commit should throw an exception because contained_2 is already detached on the repository.
      transaction2.commit();
      dumpAllRevisions(getRepository().getStore());

      fail("Exception expected");
    }
    catch (Exception ex)
    {
      msg("Caught expected exception: " + ex.getMessage());
    }

    dumpAllRevisions(getRepository().getStore());
  }

  public void testDeleteTarget() throws Exception
  {
    NodeA a = getModel3Factory().createNodeA();
    NodeA b = getModel3Factory().createNodeA();
    NodeA c = getModel3Factory().createNodeA();

    a.getChildren().add(c);
    b.getOtherNodes().add(c);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(a);
    resource.getContents().add(b);
    transaction.commit();

    a.getChildren().remove(c);

    try
    {
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException ex)
    {
      // SUCCESS
    }
  }

  public void testDeleteTargetAndReferenceAtOnce() throws Exception
  {
    skipStoreWithoutQueryXRefs();
    NodeA a = getModel3Factory().createNodeA();
    NodeA b = getModel3Factory().createNodeA();
    NodeA c = getModel3Factory().createNodeA();

    a.getChildren().add(c);
    b.getOtherNodes().add(c);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(a);
    resource.getContents().add(b);
    transaction.commit();
    dumpAllRevisions(getRepository().getStore());

    a.getChildren().remove(c);
    b.getOtherNodes().remove(c);

    // Must not fail:
    transaction.commit();
  }

  public void testDeleteTargetRemoveAndAddReference() throws Exception
  {
    NodeA a = getModel3Factory().createNodeA();
    NodeA b = getModel3Factory().createNodeA();
    NodeA c = getModel3Factory().createNodeA();

    a.getChildren().add(c);
    b.getOtherNodes().add(c);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(a);
    resource.getContents().add(b);
    transaction.commit();
    dumpAllRevisions(getRepository().getStore());

    a.getChildren().remove(c);
    b.getOtherNodes().remove(c);
    b.getOtherNodes().add(c);

    try
    {
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException ex)
    {
      // SUCCESS
    }
  }
}
