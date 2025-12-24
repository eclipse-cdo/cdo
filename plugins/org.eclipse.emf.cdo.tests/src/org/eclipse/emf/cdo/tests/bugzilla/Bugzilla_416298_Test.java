/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;

import org.eclipse.core.runtime.Path;

/**
 * Bug 416298: CDOResourceNodes do not support reflective access to derived path attribute
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class Bugzilla_416298_Test extends AbstractCDOTest
{
  private static final EAttribute URI_ATTRIBUTE = EresourcePackage.eINSTANCE.getCDOResource_URI();

  private static final EAttribute PATH_ATTRIBUTE = EresourcePackage.eINSTANCE.getCDOResourceNode_Path();

  public void testGetSetResourceURI() throws Exception
  {
    String path = getResourcePath("/path/to/resource");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(path);

    URI uri = (URI)resource.eGet(URI_ATTRIBUTE);
    assertEquals(createURI(path), uri);

    URI newURI = createURI(getParent(path) + "/new-resource");
    resource.eSet(URI_ATTRIBUTE, newURI);
    assertEquals(newURI, resource.getURI());
    assertEquals(newURI, resource.eGet(URI_ATTRIBUTE));

    transaction.commit();
    assertEquals(newURI, resource.getURI());
    assertEquals(newURI, resource.eGet(URI_ATTRIBUTE));

    resource.eSet(URI_ATTRIBUTE, uri);
    assertEquals(uri, resource.getURI());
    assertEquals(uri, resource.eGet(URI_ATTRIBUTE));
  }

  public void testGetSetResourcePath() throws Exception
  {
    String path = getResourcePath("/path/to/resource");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(path);
    assertEquals(path, resource.eGet(PATH_ATTRIBUTE));

    String newPath = getResourcePath("/new/folder/resource");
    resource.eSet(PATH_ATTRIBUTE, newPath);
    assertEquals(newPath, resource.getPath());
    assertEquals(transaction.getResourceFolder(getParent(newPath)), resource.getFolder());

    transaction.commit();
    assertEquals(newPath, resource.getPath());
    assertEquals(transaction.getResourceFolder(getParent(newPath)), resource.getFolder());

    resource.eSet(PATH_ATTRIBUTE, path);
    assertEquals(path, resource.getPath());
    assertEquals(transaction.getResourceFolder(getParent(path)), resource.getFolder());
    assertEquals(path, resource.eGet(PATH_ATTRIBUTE));
  }

  public void testGetSetFolderPath() throws Exception
  {
    String path = getResourcePath("/path/to/folder");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResourceFolder folder = transaction.createResourceFolder(path);
    assertEquals(path, folder.eGet(PATH_ATTRIBUTE));

    String newPath = getResourcePath("/atRoot"); // At root of test case folder namespace!
    folder.eSet(PATH_ATTRIBUTE, newPath);
    assertEquals(newPath, folder.getPath());
    assertEquals(newPath, folder.eGet(PATH_ATTRIBUTE));
    assertEquals(transaction.getResourceFolder(getParent(newPath)), folder.getFolder());

    transaction.commit();
    assertEquals(newPath, folder.getPath());
    assertEquals(newPath, folder.eGet(PATH_ATTRIBUTE));
    assertEquals(transaction.getResourceFolder(getParent(newPath)), folder.getFolder());

    folder.eSet(PATH_ATTRIBUTE, path);
    assertEquals(path, folder.getPath());
    assertEquals(path, folder.eGet(PATH_ATTRIBUTE));
    assertEquals(transaction.getResourceFolder(getParent(path)), folder.getFolder());
  }

  private URI createURI(String path)
  {
    return URI.createURI("cdo://" + getRepository().getUUID() + path);
  }

  private String getParent(String path)
  {
    return new Path(path).removeLastSegments(1).toString();
  }
}
