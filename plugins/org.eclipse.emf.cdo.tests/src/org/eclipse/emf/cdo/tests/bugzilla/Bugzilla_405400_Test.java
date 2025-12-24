/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stefan Schedl - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * Bug 405400.
 *
 * @author Stefan Schedl
 */
public class Bugzilla_405400_Test extends AbstractCDOTest
{
  public void testRecreateCDOResource() throws Exception
  {
    String resourcePath = getResourcePath("/test1.abc");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(resourcePath);
    transaction.commit();

    // Access the created CDOResource using another CDOView.
    // If the CDOResource is not accessed by others CDOViews before deleting, the bug will not occur.
    CDOView view = session.openView();
    assertEquals(true, view.hasResource(resourcePath));

    // Deletes the created CDOResource
    resource.delete(null);
    commitAndSync(transaction, view);

    // Recreates the CDOResource with the same name "test1.abc"
    CDOResource recreatedResource = transaction.createResource(resourcePath);
    commitAndSync(transaction, view);

    CDOResource resourceFromCDOView = view.getResource(resourcePath);

    // Fetches the CDOID of the CDOResouce which comes from the CDOTransition
    CDOID idRecreated = recreatedResource.cdoID();

    // Fetches the CDOID of the CDOResouce which comes from the CDOView
    CDOID idFromCDOView = resourceFromCDOView.cdoID();

    // Compares the CDOIDs
    assertEquals(idRecreated, idFromCDOView);
  }

  public void testRecreateCDOResourceSeparateSession() throws Exception
  {
    String resourcePath = getResourcePath("/test1.abc");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(resourcePath);
    transaction.commit();

    // Access the created CDOResource using another CDOView.
    // If the CDOResource is not accessed by others CDOViews before deleting, the bug will not occur.
    CDOView view = openSession().openView();
    assertEquals(true, view.hasResource(resourcePath));

    // Deletes the created CDOResource
    resource.delete(null);
    commitAndSync(transaction, view);

    // Recreates the CDOResource with the same name "test1.abc"
    CDOResource recreatedResource = transaction.createResource(resourcePath);
    commitAndSync(transaction, view);

    CDOResource resourceFromCDOView = view.getResource(resourcePath);

    // Fetches the CDOID of the CDOResouce which comes from the CDOTransition
    CDOID idRecreated = recreatedResource.cdoID();

    // Fetches the CDOID of the CDOResouce which comes from the CDOView
    CDOID idFromCDOView = resourceFromCDOView.cdoID();

    // Compares the CDOIDs
    assertEquals(idRecreated, idFromCDOView);
  }
}
