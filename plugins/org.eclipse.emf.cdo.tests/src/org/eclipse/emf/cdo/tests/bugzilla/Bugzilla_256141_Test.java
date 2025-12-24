/*
 * Copyright (c) 2008-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Lazy packageRegistry fail when adding instance with Eclass in a subpackage See bug 256141
 *
 * @author Simon McDuff
 */
public class Bugzilla_256141_Test extends AbstractCDOTest
{
  public void testBugzilla_256141() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("test1"));

    Class2 class2 = getModel3SubpackageFactory().createClass2();
    resource1.getContents().add(class2);

    transaction.commit();
  }
}
