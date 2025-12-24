/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Bug 580974 - ConcurrentModificationException in CDOViewImpl.clearAdapters().
 *
 * @author Eike Stepper
 */
public class Bugzilla_580974_Test extends AbstractCDOTest
{
  public void testCacheAdapter() throws Exception
  {
    Category root = getModel1Factory().createCategory();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setClearAdapterPolicy((eObject, adapter) -> adapter instanceof CacheAdapter);

    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(root);
    transaction.commit();

    CDOID resourceID = resource.cdoID();
    resource = null;
    ((CDOTransactionImpl)transaction).removeObject(resourceID);

    root.eAdapters().add(new CacheAdapter());

    transaction.close();
  }

  /**
   * @author Eike Stepper
   */
  private static final class CacheAdapter extends AdapterImpl
  {
    @Override
    public void unsetTarget(Notifier oldTarget)
    {
      super.unsetTarget(oldTarget);

      EList<Adapter> eAdapters = oldTarget.eAdapters();
      eAdapters.add(this);

      Resource resource = ((Category)oldTarget).eResource();
      System.out.println("### Resource: " + resource);
    }
  }
}
