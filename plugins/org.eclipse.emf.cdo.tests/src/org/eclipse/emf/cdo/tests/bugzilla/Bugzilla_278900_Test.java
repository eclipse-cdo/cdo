/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * EObjectValidator.validate_UniqueID broken for CDOObjects
 * <p>
 * See https://bugs.eclipse.org/278900
 * 
 * @author Simon McDuff
 */
public class Bugzilla_278900_Test extends AbstractCDOTest
{
  public void testBugzilla_278900() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction tx = session.openTransaction();
    CDOResource res = tx.getOrCreateResource("/resource1");
    tx.commit();

    assertNull(res.getEObject("ABBSBD"));
  }
}
