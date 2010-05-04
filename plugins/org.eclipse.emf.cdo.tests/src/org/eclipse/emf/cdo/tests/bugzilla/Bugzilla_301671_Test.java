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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.cache.mem.MEMRevisionCache;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.TestRevision;

/**
 * MEMRevisionCache does not revise
 * <p>
 * See https://bugs.eclipse.org/301671
 * 
 * @author Caspar De Groot
 */
public class Bugzilla_301671_Test extends AbstractCDOTest
{
  private MEMRevisionCache cache;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    cache = new MEMRevisionCache();
    cache.activate();
  }

  @Override
  public void tearDown() throws Exception
  {
    cache.deactivate();
    cache = null;
    super.tearDown();
  }

  public void test_revise() throws Exception
  {
    TestRevision r1v1 = new TestRevision(1, 1, 10);
    cache.addRevision(r1v1);

    TestRevision r1v2 = new TestRevision(1, 2, 20);
    cache.addRevision(r1v2);

    assertEquals(19, r1v1.getRevised());
  }

  public void test_toss() throws Exception
  {
    TestRevision r1v1 = new TestRevision(1, 1, 10);
    cache.addRevision(r1v1);

    TestRevision r1v3 = new TestRevision(1, 3, 30);
    cache.addRevision(r1v3);

    assertNull(cache.getRevisionByVersion(r1v1.getID(), 1));
  }

  public void test_noTossNoRevise() throws Exception
  {
    TestRevision r1v1 = new TestRevision(1, 1, 10, 19);
    cache.addRevision(r1v1);

    TestRevision r1v3 = new TestRevision(1, 3, 30);
    cache.addRevision(r1v3);

    assertEquals(19, r1v1.getRevised());
  }

  public void test_revise2()
  {
    final long a = 10;
    TestRevision av1 = new TestRevision(a, 1, 100);
    TestRevision av2 = new TestRevision(a, 2, 200);

    cache.addRevision(av1);
    assertTrue(av1.isCurrent());
    assertTrue(av2.isCurrent());

    cache.addRevision(av2);
    assertFalse(av1.isCurrent());
    assertEquals(199, av1.getRevised());
    assertTrue(av2.isCurrent());
  }

  public void test_toss2()
  {
    final long b = 20;
    TestRevision bv3 = new TestRevision(b, 3, 100);
    TestRevision bv5 = new TestRevision(b, 5, 300);
    cache.addRevision(bv3);
    cache.addRevision(bv5);
    CDOID id = CDOIDUtil.createLong(b);
    CDORevision rev = cache.getRevisionByVersion(id, 3);
    assertNull(rev);
    rev = cache.getRevision(id);
    assertSame(bv5, rev);
  }

  public void test_maintainOrdering()
  {
    final long c = 30;
    TestRevision[] crevisions = new TestRevision[8];
    for (int i = 0; i < crevisions.length; i++)
    {
      crevisions[i] = new TestRevision(c, i, 100 * i);
    }

    for (int i = 0; i < crevisions.length; i++)
    {
      if (i != 0 && i != 4)
      {
        cache.addRevision(crevisions[i]);
      }
    }

    CDOID id = CDOIDUtil.createLong(c);
    for (int i = 0; i < crevisions.length; i++)
    {
      CDORevision rev;
      switch (i)
      {
      case 0: // was never put
      case 3: // should have been evicted
      case 4: // was never put
        assertNull(cache.getRevisionByVersion(id, i));
        break;

      // 1, 2, 5 and 6 should have been revised
      case 1:
      case 2:
      case 5:
      case 6:
        rev = cache.getRevisionByVersion(id, i);
        assertFalse(rev.isCurrent());
        assertEquals(rev.getCreated() + 99, rev.getRevised());
        break;

      // 7 should be current
      case 7:
        rev = cache.getRevisionByVersion(id, i);
        assertTrue(rev.isCurrent());
        break;
      }
    }
  }
}
