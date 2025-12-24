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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Egidijus Vaisnora
 */
public class Bugzilla_333157_Test extends AbstractCDOTest
{
  public void testResourceFolderUpdate() throws CommitException
  {
    createResource(getResourcePath("/testFolder/t1"));

    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    CDOView view = session.openView();

    // Testing if resource was created
    assertNotNull(view.getResource(getResourcePath("/testFolder/t1")));

    createResource(getResourcePath("/testFolder/t2"));
    session.refresh();

    // Getting created resource - "testFolder" is retrieved from local cache
    CDOResource t2 = view.getResource(getResourcePath("/testFolder/t2"));
    assertNotNull(t2);
  }

  private void createResource(String resourcePath) throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.createResource(resourcePath);
    transaction.commit();
    session.close();
  }
}
