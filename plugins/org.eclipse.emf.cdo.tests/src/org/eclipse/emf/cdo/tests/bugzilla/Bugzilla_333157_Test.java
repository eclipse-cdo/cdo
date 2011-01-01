/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
    createResource("/testFolder/t1");

    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    CDOView view = session.openView();

    // Testing if resource was created
    assertNotNull(view.getResource("/testFolder/t1"));

    createResource("/testFolder/t2");
    session.refresh();

    // Getting created resource - "testFolder" is retrieved from local cache
    CDOResource t2 = view.getResource("/testFolder/t2");
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
