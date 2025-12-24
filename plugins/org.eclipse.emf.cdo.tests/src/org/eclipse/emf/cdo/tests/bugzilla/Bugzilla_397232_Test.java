/*
 * Copyright (c) 2012, 2013 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.G;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

/**
 * Bug 397232: Load notification missing for initial load.
 *
 * @author Eike Stepper
 */
public class Bugzilla_397232_Test extends AbstractCDOTest
{
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testLoadNotification() throws Exception
  {
    CDOSession session = openSession();

    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      G g = getModel6Factory().createG();
      BaseObject bo = getModel6Factory().createBaseObject();
      bo.setAttributeRequired("required");
      g.setDummy("g");
      g.setReference(bo);
      resource.getContents().add(g);
      resource.getContents().add(bo);
      transaction.commit();
      transaction.close();
    }

    CDOView view = session.openView();
    view.options().setLoadNotificationEnabled(true);
    CDOResource resource2 = view.getResource(getResourcePath("/test1"));

    // Load initially
    G g = (G)resource2.getContents().get(0);
    assertEquals(3, g.getNotifications().size());

    // Simulate GC
    InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(g);
    cdoObject.cdoInternalSetRevision(null);
    cdoObject.cdoInternalSetState(CDOState.PROXY);

    // Re-load
    g.getDummy();
    assertEquals(6, g.getNotifications().size());
  }
}
