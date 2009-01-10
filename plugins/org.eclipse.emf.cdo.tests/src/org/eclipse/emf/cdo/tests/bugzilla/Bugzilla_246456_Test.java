/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.internal.cdo.session.CDORevisionManagerImpl;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

/**
 * @author Simon McDuff
 */
public class Bugzilla_246456_Test extends AbstractCDOTest
{
  public void testBugzilla_246456() throws Exception
  {
    msg("Opening session");
    InternalCDOSession session = (InternalCDOSession)openModel1Session();

    msg("Opening transaction");
    InternalCDOTransaction transaction = (InternalCDOTransaction)session.openTransaction();
    CDORevisionManagerImpl revisionManager = (CDORevisionManagerImpl)transaction.getSession().getRevisionManager();
    ((LRURevisionCache)((TwoLevelRevisionCache)revisionManager.getCache()).getLevel1()).setCapacityRevised(10);
    ((LRURevisionCache)((TwoLevelRevisionCache)revisionManager.getCache()).getLevel1()).setCapacityCurrent(10);

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
