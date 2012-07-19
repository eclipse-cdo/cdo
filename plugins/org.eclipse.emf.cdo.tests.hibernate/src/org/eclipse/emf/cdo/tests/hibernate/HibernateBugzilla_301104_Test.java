/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.legacy.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * Allow application to set generated id.
 * <p>
 * See bug 301104
 * 
 * @author Martin Taal
 */
public class HibernateBugzilla_301104_Test extends AbstractCDOTest
{
  public void testSetId() throws Exception
  {
    final String uuid = "MyUUID";

    // add the id and generated value annotations
    final EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
    eAnnotation.setSource("teneo.jpa");
    eAnnotation.getDetails().put("value", "@Id @GeneratedValue(generator=\"system-uuid\")");
    Model1Package.eINSTANCE.getCategory_Name().getEAnnotations().add(eAnnotation);

    Category category1;
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();

      final CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      category1 = getModel1Factory().createCategory();
      category1.setName(uuid);
      resource.getContents().add(category1);

      transaction.commit();
    }
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      final CDOResource resource = transaction.getResource(getResourcePath("/test1"));
      final Category category2 = (Category)resource.getContents().get(0);
      assertNotSame(category1, category2);
      assertEquals(uuid, category2.getName());
      transaction.commit();
    }
  }
}
