/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.mango.MangoValue;
import org.eclipse.emf.cdo.tests.mango.MangoValueList;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Martin Fluegge
 * @since 4.0
 */
public class Bugzilla_305527_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testAvoidReferencingDifferentViews() throws CommitException
  {
    final CDOSession session = openSession();
    long commitTime;

    {
      MangoValue mangoValue = getMangoFactory().createMangoValue();
      mangoValue.setName("1");

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1")); //$NON-NLS-1$
      resource.getContents().add(mangoValue);
      commitTime = transaction.commit().getTimeStamp();

      mangoValue.setName("2");
      transaction.commit();
      transaction.close();
    }

    CDOView audit = session.openView(commitTime);
    CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
    MangoValue mangoValue = (MangoValue)auditResource.getContents().get(0);
    assertEquals("1", mangoValue.getName());

    MangoValueList mangoList = getMangoFactory().createMangoValueList();
    mangoList.getValues().add(mangoValue);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/res1")); //$NON-NLS-1$

    try
    {
      resource.getContents().add(mangoList);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
      expected.printStackTrace();
    }
  }
}
