/*
 * Copyright (c) 2009-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Bug 272861
 *
 * @author Simon McDuff
 */
public class Bugzilla_272861_Test extends AbstractCDOTest
{
  public void test_Bugzilla_271861_Case1() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction trans = session.openTransaction();
    CDOResource res = trans.createResource(getResourcePath("/test/RESOURCE"));
    trans.commit();

    res.delete(null);
    res = trans.createResource(getResourcePath("/test/RESOURCE"));
    trans.commit();

    trans.close();
    session.close();
  }
}
