/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionCache;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.RevisionHolderTest.TestRevision;

/**
 * Exception Holder RevisionHolder[AAA@OID297v1] does not belong to this list
 * <p>
 * See bug 289984
 * 
 * @author Simon McDuff
 */
public class Bugzilla_289984_Test extends AbstractCDOTest
{
  public void testBugzilla_289984() throws Exception
  {
    LRURevisionCache cache = new LRURevisionCache();
    cache.setCapacityCurrent(2);
    cache.setCapacityRevised(4);
    cache.activate();

    TestRevision r1v1 = new TestRevision(1, 1, 1);
    cache.addRevision(r1v1);

    TestRevision r1v3 = new TestRevision(1, 3, 6);
    cache.addRevision(r1v3);
    cache.removeRevision(r1v3.getID(), r1v3.getBranch().getVersion(3));

    TestRevision r1v5 = new TestRevision(1, 5, 6);
    cache.addRevision(r1v5);
  }
}
