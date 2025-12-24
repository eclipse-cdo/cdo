/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleContained;
import org.eclipse.emf.cdo.tests.model4.ImplContainedElementNPL;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Martin Fluegge
 */
public class Bugzilla_299190_Test extends AbstractCDOTest
{
  private static long uniqueCounter = System.currentTimeMillis();

  private model4Factory factory;

  private CDOResource resource1;

  private CDOTransaction transaction;

  private CDOSession session;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    String path1 = getResourcePath("/resources/folder1/" + uniqueCounter);
    ++uniqueCounter;

    init(path1);
    commit();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    session = null;
    transaction = null;
    resource1 = null;
    factory = null;
    super.doTearDown();
  }

  private void purgeCaches()
  {
    // according to Eike's comment at Bug 249681, client caches are
    // ignored, if a new session is opened.
    // server caches are wiped by the clearCache call.
    String path1 = resource1.getPath();

    transaction.close();
    session.close();

    clearCache(getRepository().getRevisionManager());
    init(path1);
  }

  private void init(String path1)
  {
    factory = getModel4Factory();

    session = openSession();
    session.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
    session.getPackageRegistry().putEPackage(getModel4Package());

    transaction = session.openTransaction();

    resource1 = transaction.getOrCreateResource(path1);
  }

  private void commit() throws CommitException
  {
    transaction.commit();
  }

  public void testGenRefSingleContainedUnidirectionalCDO() throws CommitException
  {
    GenRefSingleContained container = factory.createGenRefSingleContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefSingleContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);
    resource1.getContents().add(element0); // here the element should be removed from the container

    check1(container, element0, resource1);

    commit();
    purgeCaches();

    container = (GenRefSingleContained)resource1.getContents().get(0);
    element0 = (ImplContainedElementNPL)container.getElement();

    // check after load
    check2(container, element0, resource1);
  }

  public void testGenRefSingleContainedUnidirectionalResourceFirstCDO() throws CommitException
  {
    GenRefSingleContained container = factory.createGenRefSingleContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefSingleContainedUnidirectional-Element-0");

    resource1.getContents().add(container);
    container.setElement(element0);

    check3(resource1, container, element0);

    commit();
    purgeCaches();

    // now only one Object should be contained in the resource because element is only referenced be container
    assertEquals(1, resource1.getContents().size());

    container = (GenRefSingleContained)resource1.getContents().get(0);
    element0 = (ImplContainedElementNPL)container.getElement();

    check3(resource1, container, element0);
  }

  private void check1(GenRefSingleContained container, ImplContainedElementNPL element0, Resource res)
  {
    assertNull(container.getElement()); // element should be null here
    assertEquals(res, container.eResource());
    assertEquals(res, element0.eResource());
    assertNotSame(container, element0.eContainer());
    assertNull(element0.eContainer());
    assertEquals(2, res.getContents().size());
  }

  private void check2(GenRefSingleContained container, ImplContainedElementNPL element0, Resource res)
  {
    assertNull(element0); // element should be null here
    assertEquals(res, container.eResource());
    assertEquals(2, res.getContents().size());
  }

  private void check3(Resource resource1, GenRefSingleContained container, ImplContainedElementNPL element0)
  {
    assertNotNull(container.getElement()); // element should be null here
    assertEquals(resource1, container.eResource());
    assertEquals(container, element0.eContainer());
    assertNotSame(resource1, element0.eContainer());
    assertEquals(1, resource1.getContents().size());
  }
}
