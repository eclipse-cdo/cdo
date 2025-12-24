/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Egidijus Vaisnora - initial API and implementation
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler1;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.util.EcoreUtil;

import java.text.MessageFormat;

/**
 * @author Egidijus Vaisnora, Caspar De Groot
 */
public class Bugzilla_343332_Test extends AbstractCDOTest
{
  public void test() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      Category category1 = getModel1Factory().createCategory();
      resource.getContents().add(category1);

      category1.getCategories().add(getModel1Factory().createCategory());

      Category category2 = getModel1Factory().createCategory();
      resource.getContents().add(category2);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      final CDOObject[] attachedObject = new CDOObject[1];
      transaction.addTransactionHandler(new CDOTransactionHandler1()
      {
        @Override
        public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
        {
        }

        @Override
        public void detachingObject(CDOTransaction transaction, CDOObject object)
        {
        }

        @Override
        public void attachingObject(CDOTransaction transaction, CDOObject object)
        {
          attachedObject[0] = object;
        }
      });

      CDOResource resource = transaction.getResource(getResourcePath("test"));
      Category c1 = (Category)resource.getContents().get(0);
      Category nestedCategory = c1.getCategories().get(0);
      CDOObject cdoCategory = CDOUtil.getCDOObject(nestedCategory);

      // Detach
      EcoreUtil.remove(nestedCategory);

      // Re-attach
      attachedObject[0] = null;
      ((Category)resource.getContents().get(1)).getCategories().add(nestedCategory);

      assertNotNull("CDOTransactionHandler1.attachingObject was not called", attachedObject[0]);
      assertEquals(MessageFormat.format("Re-attached object was not the expected object {0}", cdoCategory), cdoCategory, attachedObject[0]);
    }
  }
}
