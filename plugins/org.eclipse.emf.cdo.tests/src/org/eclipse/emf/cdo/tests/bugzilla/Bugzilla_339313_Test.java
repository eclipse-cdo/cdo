/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_339313_Test extends AbstractCDOTest
{
  public void test() throws CommitException
  {
    CDOID topObjectID = null;

    {
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource resource = tx.createResource(getResourcePath("test"));

      Model1Factory factory = getModel1Factory();
      Category c = factory.createCategory();
      c.getCategories().add(factory.createCategory());
      c.getCategories().add(factory.createCategory());
      resource.getContents().add(c);

      tx.commit();

      topObjectID = CDOUtil.getCDOObject(c).cdoID();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOBranch main = session.getBranchManager().getMainBranch();
      CDORevision rev = session.getRevisionManager().getRevision(topObjectID, main.getHead(), 1, 1, true);
      msg(rev);
      session.close();
    }
  }
}
