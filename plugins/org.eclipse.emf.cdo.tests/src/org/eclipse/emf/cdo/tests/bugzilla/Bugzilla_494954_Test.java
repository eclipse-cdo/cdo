/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Thorsten Schlathoelter - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceFolderImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;

/**
 * Bug 494954 - URI of CDOFolders is not updated upon name change
 *
 * @author Thorsten Schlathoelter
 */
@CleanRepositoriesBefore(reason = "Instrumented repository")
public class Bugzilla_494954_Test extends AbstractCDOTest
{
  public void testFolderNameChange() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    String resourcePath = "folderA/folderB/resource.xmi";
    CDOResource resource = transaction.createResource(resourcePath);
    transaction.commit();

    CDOResourceFolder folder = resource.getFolder();
    String modifiedFolderName = "folderBMod";

    folder.setName(modifiedFolderName);

    transaction.commit();

    URI uri = resource.getURI();

    // URI of resource should reflect the folder name change
    assertEquals(modifiedFolderName, uri.segment(uri.segmentCount() - 2));
  }

  public void testFolderNameGenChange() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    String resourcePath = "folderA/folderB/resource.xmi";
    CDOResource resource = transaction.createResource(resourcePath);
    transaction.commit();

    CDOResourceFolder folder = resource.getFolder();
    String modifiedFolderName = "folderBMod";
    ((CDOResourceFolderImpl)folder).setNameGen(modifiedFolderName);

    transaction.commit();

    // URI of resource should reflect the folder name change
    URI uri = resource.getURI();
    assertEquals(modifiedFolderName, uri.segment(uri.segmentCount() - 2));
  }

  public void testFolderNameChangeInOtherTransaction() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    String resourcePath = "folderA/folderB/resource.xmi";
    CDOResource resource = transaction.createResource(resourcePath);
    transaction.commit();

    CDOTransaction tx2 = session.openTransaction();
    CDOResource resourceLoadedByTx2 = tx2.getObject(resource);

    CDOResourceFolder folder = resource.getFolder();
    String modifiedFolderName = "folderBMod";
    folder.setName(modifiedFolderName);

    CDOCommitInfo commitInfo = transaction.commit();
    tx2.waitForUpdate(commitInfo.getTimeStamp());

    URI uri = resourceLoadedByTx2.getURI();
    assertEquals(modifiedFolderName, uri.segment(uri.segmentCount() - 2));
  }
}
