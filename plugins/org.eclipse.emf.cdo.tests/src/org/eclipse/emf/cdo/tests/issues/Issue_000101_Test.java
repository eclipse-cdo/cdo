/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.issues;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOUnit;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.Map;

/**
 * DBFields are not initialized on reActivate for UnitMappingTable #101
 * <p>
 * See https://github.com/eclipse-cdo/cdo/issues/101
 *
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_AUDITING, IRepositoryConfig.CAPABILITY_RESTARTABLE })
@Skips(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore(reason = "Instrumented repository")
@CleanRepositoriesAfter(reason = "Instrumented repository")
public class Issue_000101_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    Map<String, Object> map = getTestProperties();
    map.put(Props.SUPPORTING_UNITS, "true");
    map.put(Props.CHECK_UNIT_MOVES, "true");

    super.doSetUp();
  }

  public void testRestartUnitEnabledRepo() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();

    CDOUnit unit = transaction.getUnitManager().createUnit(resource, false, null);
    CDOID rootID = CDOUtil.getCDOObject(unit.getRoot()).cdoID();

    CDOView view1 = openSession().openView();
    CDOObject root1 = view1.getObject(rootID);
    view1.getUnitManager().openUnit(root1, false, null);

    restartRepository();

    CDOView view2 = openSession().openView();
    CDOObject root2 = view2.getObject(rootID);
    view2.getUnitManager().openUnit(root2, false, null);
  }
}
