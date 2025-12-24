/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

/**
 * Bug 361819.
 *
 * @author Egidijus Vaisnora
 */
public class Bugzilla_361819_Test extends AbstractCDOTest
{
  public void testDoubleRefreshBug() throws Exception
  {
    createData(getResourcePath("test"));

    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath("test"));
    EObject testingEObject = resource.getContents().get(0);
    CDOObject testingObject = CDOUtil.getCDOObject(testingEObject);

    CDOSession session2 = openSession();
    CDOTransaction transaction = session2.openTransaction();
    CDOResource resource2 = transaction.getResource(getResourcePath("test"));

    Category category = (Category)resource2.getContents().get(0);
    category.setName("v2");
    transaction.commit();

    category.setName("v3");
    transaction.commit();
    assertEquals(3, CDOUtil.getCDOObject(category).cdoRevision().getVersion());

    assertClean(testingObject, testingObject.cdoView());
    session.refresh();
    session.refresh();

    // Load proxy
    ((Category)testingEObject).getName();

    // Version of object should be 3
    assertEquals(3, testingObject.cdoRevision().getVersion());
  }

  private void createData(String resourcePath) throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(resourcePath);
    resource.getContents().add(getModel1Factory().createCategory());
    transaction.commit();
    session.close();
  }
}
