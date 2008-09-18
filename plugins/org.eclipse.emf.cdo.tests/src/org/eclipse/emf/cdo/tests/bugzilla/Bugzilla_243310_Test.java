/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * NPE during Transaction.getObject(id, false)
 * <p>
 * See https://bugs.eclipse.org/243310
 * 
 * @author Eike Stepper
 */
public class Bugzilla_243310_Test extends AbstractCDOTest
{
  public void testBugzilla_243310() throws Exception
  {
    CDOSession session = openModel1Session();

    CDOTransaction transaction1 = session.openTransaction();
    CDOResource res = transaction1.createResource("/test1");
    Company companyTx1 = getModel1Factory().createCompany();
    companyTx1.setName("Company ABC");
    res.getContents().add(companyTx1);
    transaction1.commit();

    boolean loadOnDemand = false;

    CDOTransaction transaction2 = session.openTransaction();
    CDOID cdoID = CDOUtil.getCDOObject(companyTx1).cdoID();
    Company companyTx2 = (Company)transaction2.getObject(cdoID, loadOnDemand);
    assertNull(companyTx2);
  }
}
