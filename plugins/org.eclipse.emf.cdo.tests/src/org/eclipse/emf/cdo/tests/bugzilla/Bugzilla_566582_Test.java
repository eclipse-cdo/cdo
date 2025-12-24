/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Bug 566582 - IllegalStateException: Duplicate resource node in folder OIDnnn: xyz.
 *
 * @author Eike Stepper
 */
public class Bugzilla_566582_Test extends AbstractCDOTest
{
  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testSubFolderDelete() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("top/sub/resource");
    CDOResourceFolder folder = resource.getFolder();
    transaction.commit();

    CDOID folderID = folder.cdoID();
    transaction.close();
    transaction = session.openTransaction();

    folder = (CDOResourceFolder)transaction.getObject(folderID);
    // folder = transaction.getResourceFolder("top/sub");
    EcoreUtil.remove(folder);
  }
}
