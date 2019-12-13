/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

/**
 * Bug 528129: Transient objects are attached to CDOTransaction.
 *
 * @author Eike Stepper
 */
public class Bugzilla_528129_Test extends AbstractCDOTest
{
  public void testTransientContainment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    ClassWithTransientContainment root = createObject("root");
    new StateChangeDetector(transaction, root);
    resource.getContents().add(root);
    assertNew(root, transaction);

    ClassWithTransientContainment child = createObject("child");
    root.getTransientChildren().add(child);
    assertTransient(child);
  }

  public void testTransientContainmentTree() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    ClassWithTransientContainment root = createObject("root");
    resource.getContents().add(root);
    assertNew(root, transaction);

    ClassWithTransientContainment child = createObject("child");
    child.getTransientChildren().add(createObject("subchild1"));
    child.getTransientChildren().add(createObject("subchild2"));
    child.getTransientChildren().add(createObject("subchild3"));
    root.getTransientChildren().add(child);

    for (ClassWithTransientContainment c : child.getTransientChildren())
    {
      assertTransient(c);
    }
  }

  public void testPersistentContainment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    ClassWithTransientContainment root = Model3Factory.eINSTANCE.createClassWithTransientContainment();
    resource.getContents().add(root);
    assertNew(root, transaction);

    ClassWithTransientContainment child = createObject("child");
    root.getPersistentChildren().add(child);
    assertNew(child, transaction);
  }

  public void testPersistentContainmentTree() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    ClassWithTransientContainment root = createObject("root");
    resource.getContents().add(root);
    assertNew(root, transaction);

    ClassWithTransientContainment child = createObject("child");
    child.getPersistentChildren().add(createObject("subchild1"));
    child.getPersistentChildren().add(createObject("subchild2"));
    child.getPersistentChildren().add(createObject("subchild3"));
    root.getPersistentChildren().add(child);

    for (ClassWithTransientContainment c : child.getTransientChildren())
    {
      assertNew(c, transaction);
    }
  }

  public void testMoveToTransientContainment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    ClassWithTransientContainment root = createObject("root");
    resource.getContents().add(root);
    assertNew(root, transaction);

    ClassWithTransientContainment child = createObject("child");
    root.getPersistentChildren().add(child);
    assertNew(child, transaction);

    root.getTransientChildren().add(child);
    assertTransient(child);
  }

  public void testMoveToPersistentContainment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    ClassWithTransientContainment root = createObject("root");
    resource.getContents().add(root);
    assertNew(root, transaction);

    ClassWithTransientContainment child = createObject("child");
    root.getTransientChildren().add(child);
    assertTransient(child);

    root.getPersistentChildren().add(child);
    assertNew(child, transaction);
  }

  public void testMoveWithinView() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    ClassWithTransientContainment root1 = createObject("root1");
    resource.getContents().add(root1);
    assertNew(root1, transaction);

    ClassWithTransientContainment root2 = createObject("root2");
    resource.getContents().add(root2);
    assertNew(root2, transaction);

    ClassWithTransientContainment child = createObject("child");
    root1.getPersistentChildren().add(child);
    assertNew(child, transaction);

    StateChangeDetector detector = new StateChangeDetector(transaction, child);
    root2.getPersistentChildren().add(child);
    assertNew(child, transaction);

    if (isConfig(LEGACY))
    {
      // NEW --> TRANSIENT
      // TRANSIENT --> PREPARED
      // PREPARED --> NEW
      assertEquals(3, detector.getStateChanges());
    }
    else
    {
      assertEquals(0, detector.getStateChanges());
    }
  }

  private ClassWithTransientContainment createObject(String name)
  {
    ClassWithTransientContainment object = getModel3Factory().createClassWithTransientContainment();
    object.setName(name);
    return object;
  }

  /**
   * @author Eike Stepper
   */
  private static final class StateChangeDetector implements CDOObjectHandler
  {
    private final CDOObject object;

    private int stateChanges;

    public StateChangeDetector(CDOView view, EObject object)
    {
      this.object = CDOUtil.getCDOObject(object);
      view.addObjectHandler(this);
    }

    public int getStateChanges()
    {
      return stateChanges;
    }

    @Override
    public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
    {
      if (object == this.object)
      {
        System.out.println(object + ": " + oldState + " --> " + newState);
        ++stateChanges;
      }
    }
  }
}
