/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.revision.cache.InternalCDORevisionCache;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.two.TwoLevelRevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

/**
 * @author Simon McDuff
 * @see bug 246456
 */
public class Bugzilla_246456_Test extends AbstractCDOTest
{
  public void testBugzilla_246456() throws Exception
  {
    msg("Opening session");
    InternalCDOSession session = (InternalCDOSession)openModel1Session();
    InternalCDORevisionManager revisionManager = session.getRevisionManager();
    InternalCDORevisionCache cache = revisionManager.getCache();
    if (cache instanceof TwoLevelRevisionCache)
    {
      TwoLevelRevisionCache twoLevel = (TwoLevelRevisionCache)cache;
      InternalCDORevisionCache level1 = twoLevel.getLevel1();
      if (level1 instanceof LRURevisionCache)
      {
        LRURevisionCache lruCache = (LRURevisionCache)level1;

        msg("Opening transaction");
        InternalCDOTransaction transaction = (InternalCDOTransaction)session.openTransaction();
        lruCache.setCapacityRevised(10);
        lruCache.setCapacityCurrent(10);

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
  }
}
