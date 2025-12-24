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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOUnit;

import java.util.Map;

/**
 * Bug 508261 - Unit select should only select valid revisions.
 *
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_AUDITING, "DB.ranges" })
@Skips(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore(reason = "Instrumented repository")
@CleanRepositoriesAfter(reason = "Instrumented repository")
public class Bugzilla_508261_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    Map<String, Object> map = getTestProperties();
    map.put(Props.SUPPORTING_UNITS, "true");
    map.put(Props.CHECK_UNIT_MOVES, "true");
    map.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");

    super.doSetUp();
  }

  public void testDeleteResourceFromUnit() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResourceFolder folder = transaction.createResourceFolder("UnitFolder");
    CDOResourceFolder subFolder = transaction.createResourceFolder("UnitFolder/SubFolderA");

    CDOResource resource = transaction.createResource("UnitFolder/SubFolderA/ResourceInA");
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);

    transaction.commit();
    transaction.getUnitManager().createUnit(folder, true, null);
    assertTrue(transaction.getUnitManager().isUnit(folder));
    transaction.close();

    CDOTransaction transaction2 = session.openTransaction();
    CDOResourceFolder folder2 = transaction2.getObject(folder);
    assertTrue(transaction2.getUnitManager().isUnit(folder2));

    CDOUnit unit2 = transaction2.getUnitManager().openUnit(folder2, false, null);
    assertNotNull(unit2);

    // Now remove Resource:
    CDOResourceFolder subFolder2 = transaction2.getObject(subFolder);
    CDOResource resource2 = transaction2.getObject(resource);
    assertTrue(subFolder2.getNodes().remove(resource2));
    transaction2.commit();
    transaction2.close();

    CDOTransaction transaction3 = session.openTransaction();
    CDOResourceFolder folder3 = transaction3.getObject(folder);
    CDOUnit unit3 = transaction3.getUnitManager().openUnit(folder3, false, null);
    assertNotNull(unit3);
  }

  public void testDeleteFolderFromUnit() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResourceFolder folder = transaction.createResourceFolder("UnitFolder");
    CDOResourceFolder subFolder = transaction.createResourceFolder("UnitFolder/SubFolderA");

    transaction.commit();
    transaction.getUnitManager().createUnit(folder, true, null);
    assertTrue(transaction.getUnitManager().isUnit(folder));
    transaction.close();

    CDOTransaction transaction2 = session.openTransaction();
    CDOResourceFolder folder2 = transaction2.getObject(folder);
    assertTrue(transaction2.getUnitManager().isUnit(folder2));

    CDOUnit unit2 = transaction2.getUnitManager().openUnit(folder2, false, null);
    assertNotNull(unit2);

    // Now remove SubFolderA:
    CDOResourceFolder subFolder2 = transaction2.getObject(subFolder);
    assertTrue(folder2.getNodes().remove(subFolder2));
    transaction2.commit();
    transaction2.close();

    CDOTransaction transaction3 = session.openTransaction();
    CDOResourceFolder folder3 = transaction3.getObject(folder);
    CDOUnit unit3 = transaction3.getUnitManager().openUnit(folder3, false, null);
    assertNotNull(unit3);
  }
}
