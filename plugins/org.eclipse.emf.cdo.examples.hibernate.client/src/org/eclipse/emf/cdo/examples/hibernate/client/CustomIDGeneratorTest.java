/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.hibernate.client;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.examples.company.Category;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * This test shows that a custom ID Generator is used. You can find the annotation for the id generator in the ecore
 * model, at EPackage level and in the Category.name EAttribute.
 * <p/>
 * The ID generator is implemented in the org.eclipse.emf.cdo.examples.hibernate.server (see the
 * CDOExampleUUIDHexGenerator class).
 * <p/>
 * The custom id generator ensures that if an object has already an id that it is being re-used.
 * <p/>
 * Note this testcase will work with the hibernate mapping created using the latest version of the ecore file. When
 * using the generated hbm file then it does not work (as the generated hbm file does not necessarily contain the
 * required generator definitions).
 *
 * @author Martin Taal
 */
public class CustomIDGeneratorTest extends BaseTest
{

  /**
   * Create 2 categories, after persisting they should both have an id. One has a pre-set id, the other one a generated
   * id (as it was not pre-set).
   */
  public void testSetId() throws Exception
  {

    Category category1;
    Category category2;
    final String uuid = "MyUUID"; //$NON-NLS-1$
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      // get/create a resource
      CDOResource resource = transaction.getOrCreateResource("/res1"); //$NON-NLS-1$

      // clear any previous data
      resource.getContents().clear();

      category1 = CompanyFactory.eINSTANCE.createCategory();
      category1.setName(uuid);
      category2 = CompanyFactory.eINSTANCE.createCategory();

      resource.getContents().add(category1);
      resource.getContents().add(category2);

      transaction.commit();
      session.close();
    }

    // read back and test that the uuid is the same for one
    // and generated for the other
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/res1"); //$NON-NLS-1$
      final Category category1_read = (Category)resource.getContents().get(0);
      final Category category2_read = (Category)resource.getContents().get(1);

      // test that we really have different objects
      assertNotSame(category1, category1_read);
      assertNotSame(category2, category2_read);

      // but that the uuid for one is equal but not the same object
      assertEquals(uuid, category1_read.getName());
      assertNotSame(uuid, category1_read.getName());

      // and the other uuid has been set
      assertNotNull(category2_read.getName());
      assertEquals(false, uuid.equals(category2_read.getName()));
      transaction.commit();
      session.close();
    }
  }
}
