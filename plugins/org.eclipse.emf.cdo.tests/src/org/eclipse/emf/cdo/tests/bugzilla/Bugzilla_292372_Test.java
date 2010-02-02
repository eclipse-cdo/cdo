/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.mem.MEMRevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.two.TwoLevelRevisionCache;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.RevisionHolderTest.TestRevision;

/**
 * @author Eike Stepper
 */
public class Bugzilla_292372_Test extends AbstractCDOTest
{
  public void test()
  {
    LRURevisionCache lruCache = new LRURevisionCache();
    lruCache.setCapacityCurrent(2);
    lruCache.setCapacityRevised(2);

    MEMRevisionCache memCache = new MEMRevisionCache();

    TwoLevelRevisionCache twoLevelCache = new TwoLevelRevisionCache();
    twoLevelCache.setLevel1(lruCache);
    twoLevelCache.setLevel2(memCache);
    twoLevelCache.activate();

    TestRevision r1v1 = new TestRevision(9, 1, 10);

    TestRevision r2v1 = new TestRevision(1, 1, 10);
    TestRevision r2v2 = new TestRevision(1, 2, 20);
    TestRevision r2v3 = new TestRevision(1, 3, 30);
    TestRevision r2v4 = new TestRevision(1, 4, 40);

    // First we add the revision that will cause the problem later
    twoLevelCache.addRevision(r1v1);

    // Then we push other revisions to force r1v1 into the level-2 cache
    twoLevelCache.addRevision(r2v1);
    assertEquals(false, r2v1.isHistorical());
    twoLevelCache.addRevision(r2v2);
    assertEquals(true, r2v1.isHistorical());
    assertEquals(false, r2v2.isHistorical());
    twoLevelCache.addRevision(r2v3);
    assertEquals(true, r2v2.isHistorical());
    assertEquals(false, r2v3.isHistorical());
    twoLevelCache.addRevision(r2v4);
    assertEquals(true, r2v3.isHistorical());
    assertEquals(false, r2v4.isHistorical());

    // Now we add a revision r1v2 that SHOULD cause r1v1.revised to get set
    TestRevision r1v2 = new TestRevision(9, 2, 20);
    twoLevelCache.addRevision(r1v2); // Into L1 cache
    CDORevision r1v = twoLevelCache.getRevisionByVersion(r1v1.getID(), r1v1.getBranch().getVersion(1)); // From L2 cache

    // But it doesn't, cause twoLevelCache forgot to delegate to the L2,
    // so this will fail:
    assertEquals(true, r1v.isHistorical());
  }
}
