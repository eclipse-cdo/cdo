/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.two.TwoLevelRevisionCache;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOTransactionImpl;

/**
 * @author Simon McDuff
 */
public class Bugzilla_246456_Test extends AbstractCDOTest
{
  public void testBugzilla_246456() throws Exception
  {
    msg("Opening session");
    CDOSessionImpl session = (CDOSessionImpl)openModel1Session();

    msg("Opening transaction");
    CDOTransactionImpl transaction = session.openTransaction();
    ((LRURevisionCache)((TwoLevelRevisionCache)transaction.getSession().getRevisionManager().getCache()).getLevel1())
        .setCapacityRevised(10);
    ((LRURevisionCache)((TwoLevelRevisionCache)transaction.getSession().getRevisionManager().getCache()).getLevel1())
        .setCapacityCurrent(10);
    transaction.setUniqueResourceContents(false);

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();

    msg("Adding supplier");
    resource.getContents().add(supplier);

    msg("Committing");
    transaction.commit();
    for (int i = 0; i < 10; i++)
    {
      Supplier supplier2 = getModel1Factory().createSupplier();
      resource.getContents().add(supplier2);
      transaction.commit();
    }

    Supplier supplier2 = getModel1Factory().createSupplier();
    resource.getContents().add(supplier2);

    msg("Committing");
    transaction.commit();
  }
}
