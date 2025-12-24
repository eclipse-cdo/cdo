/*
 * Copyright (c) 2009-2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_285008_Test extends AbstractCDOTest
{
  private static String RESOURCENAME = "/r1";

  public void testCDO_isSet() throws CommitException
  {
    {
      CDOSession session = openSession();
      model4Package p = model4Package.eINSTANCE;
      session.getPackageRegistry().putEPackage(p);
      session.options().setPassiveUpdateEnabled(false);
      model4Factory f = model4Factory.eINSTANCE;

      // Ensure that model is suitable for this test
      boolean p1 = p.getRefMultiNonContainedUnsettable_Elements().isMany();
      boolean p2 = p.getRefMultiNonContainedUnsettable_Elements().isUnsettable();
      boolean p3 = !p.getMultiNonContainedUnsettableElement_Parent().isMany();
      boolean p4 = p.getMultiNonContainedUnsettableElement_Parent().isUnsettable();
      boolean p5 = p.getRefMultiNonContainedUnsettable_Elements().getEOpposite() == p.getMultiNonContainedUnsettableElement_Parent();
      if (!p1 || !p2 || !p3 || !p4 || !p5)
      {
        throw new RuntimeException("Model does not meet the prerequirements for this test");
      }

      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.createResource(getResourcePath(RESOURCENAME));
      RefMultiNonContainedUnsettable a = f.createRefMultiNonContainedUnsettable();
      MultiNonContainedUnsettableElement b = f.createMultiNonContainedUnsettableElement();
      r1.getContents().add(a);
      r1.getContents().add(b);
      a.getElements().add(b);

      assertEquals(true, a.isSetElements());

      tx.commit();

      assertEquals(true, a.isSetElements());

      tx.close();
      session.close();
    }

    // Same tests on isSetElements, but in a new session
    {
      CDOSession session = openSession();
      model4Package p = model4Package.eINSTANCE;
      session.getPackageRegistry().putEPackage(p);
      session.options().setPassiveUpdateEnabled(false);
      CDOTransaction tx = session.openTransaction();

      CDOResource r1 = tx.getResource(getResourcePath(RESOURCENAME));
      RefMultiNonContainedUnsettable a = (RefMultiNonContainedUnsettable)r1.getContents().get(0);

      assertEquals(true, a.isSetElements());

      tx.close();
      session.close();
    }
  }

  public void testCDO_crossReferences() throws CommitException
  {
    {
      CDOSession session = openSession();
      model4Package p = model4Package.eINSTANCE;
      session.getPackageRegistry().putEPackage(p);
      session.options().setPassiveUpdateEnabled(false);
      model4Factory f = model4Factory.eINSTANCE;

      // Ensure that model is suitable for this test
      boolean p1 = p.getRefMultiNonContainedUnsettable_Elements().isMany();
      boolean p2 = p.getRefMultiNonContainedUnsettable_Elements().isUnsettable();
      boolean p3 = !p.getMultiNonContainedUnsettableElement_Parent().isMany();
      boolean p4 = p.getMultiNonContainedUnsettableElement_Parent().isUnsettable();
      boolean p5 = p.getRefMultiNonContainedUnsettable_Elements().getEOpposite() == p.getMultiNonContainedUnsettableElement_Parent();
      if (!p1 || !p2 || !p3 || !p4 || !p5)
      {
        throw new RuntimeException("Model does not meet the prerequirements for this test");
      }

      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.createResource(getResourcePath(RESOURCENAME));
      RefMultiNonContainedUnsettable a = f.createRefMultiNonContainedUnsettable();
      MultiNonContainedUnsettableElement b = f.createMultiNonContainedUnsettableElement();
      r1.getContents().add(a);
      r1.getContents().add(b);
      a.getElements().add(b);
      tx.commit();

      assertEquals(1, a.eCrossReferences().size());
      assertSame(b, a.eCrossReferences().get(0));

      assertEquals(1, b.eCrossReferences().size());
      assertSame(a, b.eCrossReferences().get(0));

      tx.close();
      session.close();
    }

    // Same tests on eCrossReferences, but in a new session
    {
      CDOSession session = openSession();
      model4Package p = model4Package.eINSTANCE;
      session.getPackageRegistry().putEPackage(p);
      session.options().setPassiveUpdateEnabled(false);
      CDOTransaction tx = session.openTransaction();

      CDOResource r1 = tx.getResource(getResourcePath(RESOURCENAME));
      RefMultiNonContainedUnsettable a = (RefMultiNonContainedUnsettable)r1.getContents().get(0);
      MultiNonContainedUnsettableElement b = (MultiNonContainedUnsettableElement)r1.getContents().get(1);

      assertEquals(1, a.eCrossReferences().size());
      assertSame(b, a.eCrossReferences().get(0));

      assertEquals(1, b.eCrossReferences().size());
      assertSame(a, b.eCrossReferences().get(0));

      tx.close();
      session.close();
    }
  }
}
