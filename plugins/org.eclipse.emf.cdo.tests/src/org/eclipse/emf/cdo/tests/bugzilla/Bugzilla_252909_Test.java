/***************************************************************************
 * Copyright (c) 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/

package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;

/**
 * [DBStore] NPE when trying to update objects
 * <p>
 * See https://bugs.eclipse.org/252909
 * 
 * @author Simon McDuff
 */
public class Bugzilla_252909_Test extends AbstractCDOTest
{

  public void testBugzilla_252909() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    for (int i = 0; i < 10; i++)
    {
      Company company = getModel1Factory().createCompany();
      company.setName("Okidoo");
      resource.getContents().add(company);
      transaction.commit();

      clearCache(getRepository().getRevisionManager());
    }
  }

}
