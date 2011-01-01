/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.legacy.model1.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * Bug 328790 - CDOResource.isLoaded returns false after adding/clearing contents of new resource
 * 
 * @author Caspar De Groot
 */
public class Bugzilla_328790_Test extends AbstractCDOTest
{
  public void test_newThenAdd1() throws CommitException
  {
    CDOSession session = openSession();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction tx = session.openTransaction(resourceSet);

    Resource resource = resourceSet.createResource(URI.createURI("cdo://myUri"));
    EObject object = Model1Factory.eINSTANCE.createAddress();
    resource.getContents().add(object);

    assertTrue(resource.isLoaded());

    tx.commit();

    assertTrue(resource.isLoaded());

    tx.close();
    session.close();
  }

  public void test_newThenAdd2() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();

    CDOResource resource = tx.createResource("/myUri");
    EObject object = Model1Factory.eINSTANCE.createAddress();
    resource.getContents().add(object);

    assertTrue(resource.isLoaded());

    tx.commit();

    assertTrue(resource.isLoaded());

    tx.close();
    session.close();
  }

  public void test_newThenClear() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();

    CDOResource resource = tx.createResource("/myUri");
    resource.getContents().clear();

    assertTrue(resource.isLoaded());

    tx.commit();

    assertTrue(resource.isLoaded());

    tx.close();
    session.close();
  }
}
