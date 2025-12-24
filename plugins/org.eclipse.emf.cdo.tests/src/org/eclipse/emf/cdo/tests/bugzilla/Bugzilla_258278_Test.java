/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * : Multiple resources creation bug
 * <p>
 * See bug 258278
 *
 * @author Simon McDuff
 */
public class Bugzilla_258278_Test extends AbstractCDOTest
{
  public void testBugzilla_258278() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource(getResourcePath("/root/folder1/resource1"));
      transaction.createResource(getResourcePath("/root/folder1/resource2"));
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.getResource(getResourcePath("/root/folder1/resource1"));
    assertNotNull(resource1);
    CDOResource resource2 = transaction.getResource(getResourcePath("/root/folder1/resource2"));
    assertNotNull(resource2);
  }
}
