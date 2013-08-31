/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import static org.eclipse.emf.cdo.eresource.EresourcePackage.Literals.CDO_RESOURCE_NODE__PATH;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Bug 416298: Tests that resource nodes provide proper reflective access to the derived 'path' attribute.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class Bugzilla_416298_Test extends AbstractCDOTest
{
  public void testGetSetResourcePath() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    CDOResource res = transaction.getOrCreateResource("/path/to/resource.model1");

    assertEquals("/path/to/resource.model1", res.eGet(CDO_RESOURCE_NODE__PATH));

    res.eSet(CDO_RESOURCE_NODE__PATH, "/new/folder/resource.model1");

    assertNotNull(res.getFolder());
    assertEquals("/new/folder", res.getFolder().getPath());
  }

  public void testGetSetFolderPath() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    CDOResourceFolder folder = transaction.getOrCreateResourceFolder("/path/to/folder");

    assertEquals("/path/to/folder", folder.eGet(CDO_RESOURCE_NODE__PATH));

    folder.eSet(CDO_RESOURCE_NODE__PATH, "/atRoot");

    assertNull(folder.getFolder());
    assertEquals("atRoot", folder.getName());
  }

}
