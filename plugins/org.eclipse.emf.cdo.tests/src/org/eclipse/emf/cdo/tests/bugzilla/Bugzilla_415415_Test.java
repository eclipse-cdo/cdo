/*
 * Copyright (c) 2013, 2014, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * Bug 415415: Stale reference not removed between locally detached object and remotely changed ones
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_415415_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "res1";

  private CDOTransaction transaction;

  private NodeA root;

  private NodeA child1;

  private NodeA child2;

  private CDOObject rootCDO;

  private CDOObject child1CDO;

  private CDOObject child2CDO;

  private CDOID rootID;

  private CDOID child1ID;

  private CDOID child2ID;

  private RemoteUser remoteUser;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    root = getModel3Factory().createNodeA();
    root.setName("root");

    child1 = getModel3Factory().createNodeA();
    child1.setName("child1");
    root.getChildren().add(child1);

    child2 = getModel3Factory().createNodeA();
    child2.setName("child2");
    root.getChildren().add(child2);

    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(true);
    session.options().setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);

    transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_NAME));
    resource.getContents().add(root);
    transaction.commit();

    rootCDO = CDOUtil.getCDOObject(root);
    rootID = rootCDO.cdoID();

    child1CDO = CDOUtil.getCDOObject(child1);
    child1ID = child1CDO.cdoID();

    child2CDO = CDOUtil.getCDOObject(child2);
    child2ID = child2CDO.cdoID();

    remoteUser = new RemoteUser();
  }

  @Override
  public void tearDown() throws Exception
  {
    root = null;
    child1 = null;
    child2 = null;

    rootCDO = null;
    child1CDO = null;
    child2CDO = null;

    rootID = null;
    child1ID = null;
    child2ID = null;

    remoteUser = null;
    super.tearDown();
  }

  public void testStaleReferenceRemove_FromLocallyDirtyObjectToRemotelyDetachedObject() throws Exception
  {
    child2.getOtherNodes().add(child1);
    remoteUser.removeNodeA1ChildToNodeAroot();
    CDOCommitInfo commit = remoteUser.commit();
    transaction.waitForUpdate(commit.getTimeStamp(), DEFAULT_TIMEOUT);

    assertEquals("As child1 is remotely detached, the stale reference child2.otherNodes:child1 should be removed", 0, child2.getOtherNodes().size());

    String assertMessage = "As the stale reference child2.otherNodes:child1 has been removed the transaction should be clean";
    assertEquals(assertMessage, false, transaction.isDirty());
    assertEquals(assertMessage, 0, transaction.getDirtyObjects().size());

    transaction.commit();
    remoteUser.assertNotStaleReference();
  }

  public void testStaleReferenceRemove_FromRemotelyDirtyObjectToLocallyDetachedObject() throws Exception
  {
    root.getChildren().remove(child1);
    remoteUser.createReferenceFromNodeA2ToNodeA1();
    CDOCommitInfo commit = remoteUser.commit();
    transaction.waitForUpdate(commit.getTimeStamp(), DEFAULT_TIMEOUT);

    assertEquals(1, transaction.getDetachedObjects().size());
    assertEquals(child1CDO, transaction.getDetachedObjects().get(child1ID));
    assertEquals("As child1 is locally detached, the stale reference child2.otherNodes:child1 should be removed", 0, child2.getOtherNodes().size());

    String assertMessage = "As the stale reference child2.otherNodes:child1 has been removed, child2 and nodeARoot should be the only dirty objects";
    assertEquals(assertMessage, 2, transaction.getDirtyObjects().size());
    assertEquals(assertMessage, child2CDO, transaction.getDirtyObjects().get(child2ID));
    assertEquals(assertMessage, rootCDO, transaction.getDirtyObjects().get(rootID));

    commit = transaction.commit();
    remoteUser.waitForUpdate(commit.getTimeStamp(), DEFAULT_TIMEOUT);
    remoteUser.assertNotStaleReference();
  }

  /**
   * @author Esteban Dugueperoux
   */
  private final class RemoteUser
  {
    private CDOTransaction transaction;

    private NodeA root;

    private NodeA child1;

    private NodeA child2;

    public RemoteUser()
    {
      CDOSession session = openSession();
      session.options().setPassiveUpdateEnabled(true);
      session.options().setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);

      transaction = session.openTransaction();

      CDOResource resource = transaction.getResource(getResourcePath(RESOURCE_NAME));

      root = (NodeA)resource.getContents().get(0);
      child1 = root.getChildren().get(0);
      child2 = root.getChildren().get(1);
    }

    public void removeNodeA1ChildToNodeAroot()
    {
      root.getChildren().remove(child1);
    }

    public void createReferenceFromNodeA2ToNodeA1()
    {
      child2.getOtherNodes().add(child1);
    }

    public CDOCommitInfo commit() throws Exception
    {
      return transaction.commit();
    }

    public void waitForUpdate(long timeStamp, long defaultTimeout)
    {
      transaction.waitForUpdate(timeStamp, defaultTimeout);
    }

    public void assertNotStaleReference()
    {
      assertEquals(0, transaction.getDetachedObjects().size());
      assertEquals(0, transaction.getDirtyObjects().size());
      assertEquals(0, child2.getOtherNodes().size());
    }
  }
}
