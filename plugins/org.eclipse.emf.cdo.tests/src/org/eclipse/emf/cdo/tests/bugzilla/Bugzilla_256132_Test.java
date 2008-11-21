/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * 256132: CDO : ClassRef unresolveable
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=256132
 * 
 * @author Simon McDuff
 */
public class Bugzilla_256132_Test extends AbstractCDOTest
{
  public void testBugzilla_256132() throws InterruptedException
  {

    // Create resource in session 1
    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(getConnector());
    configuration.setRepositoryName(REPOSITORY_NAME);
    configuration.setLegacySupportEnabled(false);
    configuration.setDemandPopulatingPackageRegistry();

    CDOSession session = configuration.openSession();

    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("test1");

    Class2 class2 = SubpackageFactory.eINSTANCE.createClass2();
    resource1.getContents().add(class2);

    transaction.commit();
  }
  
  public void testBugzilla_256132_RegisterTopLevel() throws InterruptedException
  {

    // Create resource in session 1
    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(getConnector());
    configuration.setRepositoryName(REPOSITORY_NAME);
    configuration.setLegacySupportEnabled(false);
    configuration.setDemandPopulatingPackageRegistry();

    CDOSession session = configuration.openSession();
    session.getPackageRegistry().putEPackage(Model3Package.eINSTANCE);
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("test1");

    Class2 class2 = SubpackageFactory.eINSTANCE.createClass2();
    resource1.getContents().add(class2);

    transaction.commit();

  }
}
