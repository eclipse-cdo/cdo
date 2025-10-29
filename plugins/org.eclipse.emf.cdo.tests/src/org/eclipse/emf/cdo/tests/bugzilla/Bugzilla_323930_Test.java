/*
 * Copyright (c) 2010-2012, 2016 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Bugzilla_323930_Test extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
  }

  public void testChangeIndexesInTargetList() throws Exception
  {
    NodeA a = getModel3Factory().createNodeA();
    NodeA b = getModel3Factory().createNodeA();
    NodeA c = getModel3Factory().createNodeA();

    NodeA x = getModel3Factory().createNodeA();
    NodeA y = getModel3Factory().createNodeA();
    NodeA z = getModel3Factory().createNodeA();

    a.getOtherNodes().add(x);
    a.getOtherNodes().add(y);
    a.getOtherNodes().add(z);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(a);
    resource.getContents().add(b);
    resource.getContents().add(c);
    resource.getContents().add(x);
    resource.getContents().add(y);
    resource.getContents().add(z);
    transaction.commit();
    dumpAllRevisions(getRepository().getStore());

    resource.getContents().remove(b);
    resource.getContents().remove(c);
    resource.getContents().remove(z);
    a.getOtherNodes().remove(z);

    // Must not fail:
    transaction.commit();
  }

  public void testChangeIndexesInSourceList() throws Exception
  {
    NodeA a = getModel3Factory().createNodeA();
    NodeA b = getModel3Factory().createNodeA();
    NodeA c = getModel3Factory().createNodeA();

    NodeA x = getModel3Factory().createNodeA();
    NodeA y = getModel3Factory().createNodeA();
    NodeA z = getModel3Factory().createNodeA();

    a.getOtherNodes().add(x);
    a.getOtherNodes().add(y);
    a.getOtherNodes().add(z);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(a);
    resource.getContents().add(b);
    resource.getContents().add(c);
    resource.getContents().add(x);
    resource.getContents().add(y);
    resource.getContents().add(z);
    transaction.commit();
    dumpAllRevisions(getRepository().getStore());

    a.getOtherNodes().add(0, b);
    a.getOtherNodes().add(1, c);
    a.getOtherNodes().remove(z); // Remove xref from index 4
    resource.getContents().remove(z);

    // Must not fail:
    transaction.commit();
  }

  public void testRemoveXRefByReplace() throws Exception
  {
    NodeA a = getModel3Factory().createNodeA();
    NodeA b = getModel3Factory().createNodeA();
    NodeA c = getModel3Factory().createNodeA();

    NodeA x = getModel3Factory().createNodeA();
    NodeA y = getModel3Factory().createNodeA();
    NodeA z = getModel3Factory().createNodeA();

    a.getOtherNodes().add(x);
    a.getOtherNodes().add(y);
    a.getOtherNodes().add(z);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(a);
    resource.getContents().add(b);
    resource.getContents().add(c);
    resource.getContents().add(x);
    resource.getContents().add(y);
    resource.getContents().add(z);
    transaction.commit();
    dumpAllRevisions(getRepository().getStore());

    a.getOtherNodes().set(2, b); // Replace z with b
    resource.getContents().remove(z);

    // Must not fail:
    transaction.commit();
  }

  public void testRemoveXRefByReplace_PrecedingAdd() throws Exception
  {
    NodeA a = getModel3Factory().createNodeA();
    NodeA b = getModel3Factory().createNodeA();
    NodeA c = getModel3Factory().createNodeA();

    NodeA x = getModel3Factory().createNodeA();
    NodeA y = getModel3Factory().createNodeA();
    NodeA z = getModel3Factory().createNodeA();

    a.getOtherNodes().add(x);
    a.getOtherNodes().add(y);
    a.getOtherNodes().add(z);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(a);
    resource.getContents().add(b);
    resource.getContents().add(c);
    resource.getContents().add(x);
    resource.getContents().add(y);
    resource.getContents().add(z);
    transaction.commit();
    dumpAllRevisions(getRepository().getStore());

    a.getOtherNodes().add(0, b);
    a.getOtherNodes().set(3, c); // Replace z with c
    resource.getContents().remove(z);

    // Must not fail:
    transaction.commit();
  }

  public void testAddXRefByReplace() throws Exception
  {
    NodeA a = getModel3Factory().createNodeA();
    NodeA b = getModel3Factory().createNodeA();
    NodeA c = getModel3Factory().createNodeA();

    NodeA x = getModel3Factory().createNodeA();
    NodeA y = getModel3Factory().createNodeA();
    NodeA z = getModel3Factory().createNodeA();

    a.getOtherNodes().add(x);
    a.getOtherNodes().add(y);
    a.getOtherNodes().add(b);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(a);
    resource.getContents().add(b);
    resource.getContents().add(c);
    resource.getContents().add(x);
    resource.getContents().add(y);
    resource.getContents().add(z);
    transaction.commit();
    dumpAllRevisions(getRepository().getStore());

    a.getOtherNodes().set(2, z); // Replace b with z
    resource.getContents().remove(z);

    try
    {
      transaction.commit();
      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // SUCCESS
    }
  }

  public void testAddXRefByReplace_PrecedingAdd() throws Exception
  {
    NodeA a = getModel3Factory().createNodeA();
    NodeA b = getModel3Factory().createNodeA();
    NodeA c = getModel3Factory().createNodeA();

    NodeA x = getModel3Factory().createNodeA();
    NodeA y = getModel3Factory().createNodeA();
    NodeA z = getModel3Factory().createNodeA();

    a.getOtherNodes().add(x);
    a.getOtherNodes().add(y);
    a.getOtherNodes().add(b);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(a);
    resource.getContents().add(b);
    resource.getContents().add(c);
    resource.getContents().add(x);
    resource.getContents().add(y);
    resource.getContents().add(z);
    transaction.commit();
    dumpAllRevisions(getRepository().getStore());

    a.getOtherNodes().add(0, c);
    a.getOtherNodes().set(3, z); // Replace b with z
    resource.getContents().remove(z);

    try
    {
      transaction.commit();
      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // SUCCESS
    }
  }
}
