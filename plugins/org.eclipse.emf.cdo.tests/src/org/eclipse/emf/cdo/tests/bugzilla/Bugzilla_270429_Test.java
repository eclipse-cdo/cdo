/*
 * Copyright (c) 2009-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.MultiContainedElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiContained;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Stefan Winkler
 */
public class Bugzilla_270429_Test extends AbstractCDOTest
{
  public void testTwoWayReferenceDeletion() throws CommitException
  {
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      session.getPackageRegistry().putEPackage(model4interfacesPackage.eINSTANCE);

      CDOTransaction tx = session.openTransaction();
      CDOResource res = tx.createResource(getResourcePath("/test/1"));

      RefMultiContained parent = model4Factory.eINSTANCE.createRefMultiContained();

      MultiContainedElement child1 = model4Factory.eINSTANCE.createMultiContainedElement();
      child1.setName("Element1");
      child1.setParent(parent);

      MultiContainedElement child2 = model4Factory.eINSTANCE.createMultiContainedElement();
      child2.setName("Element2");
      parent.getElements().add(child2);

      res.getContents().add(parent);

      tx.commit();

      tx.close();
      session.close();
      clearCache(getRepository().getRevisionManager());
    }

    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      session.getPackageRegistry().putEPackage(model4interfacesPackage.eINSTANCE);

      CDOTransaction tx = session.openTransaction();
      CDOResource res = tx.getResource(getResourcePath("/test/1"));

      RefMultiContained parent = (RefMultiContained)res.getContents().get(0);
      MultiContainedElement child1 = parent.getElements().get(0);
      MultiContainedElement child2 = parent.getElements().get(1);

      assertEquals("Element1", child1.getName());
      assertEquals("Element2", child2.getName());

      parent.getElements().remove(child1);
      child2.setParent(null);

      tx.commit();
      tx.close();
      session.close();
      clearCache(getRepository().getRevisionManager());
    }

    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      session.getPackageRegistry().putEPackage(model4interfacesPackage.eINSTANCE);

      CDOView tx = session.openView();
      CDOResource res = tx.getResource(getResourcePath("/test/1"));

      RefMultiContained parent = (RefMultiContained)res.getContents().get(0);
      assertEquals(0, parent.getElements().size());

      tx.close();
      session.close();
      clearCache(getRepository().getRevisionManager());
    }
  }
}
