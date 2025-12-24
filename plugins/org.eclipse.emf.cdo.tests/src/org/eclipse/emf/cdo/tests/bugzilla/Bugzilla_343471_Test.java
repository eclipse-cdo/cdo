/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EObject;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_343471_Test extends AbstractCDOTest
{
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void test() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();

    CDOResource resource1 = tx.createResource(getResourcePath("test1"));
    CDOResource resource2 = tx.createResource(getResourcePath("test2"));

    Category c1 = getModel1Factory().createCategory();
    resource1.getContents().add(c1);

    Category c2 = getModel1Factory().createCategory();
    c1.getCategories().add(c2);

    // c3 serves only to make the later commit partial
    Category c3 = getModel1Factory().createCategory();
    resource2.getContents().add(c3);

    tx.commit();

    msg("c1 = " + c1);
    msg("c2 = " + c2);
    msg("c3 = " + c3);

    msg("resource1 = " + resource1);
    msg("resource2 = " + resource2);

    msg("");
    msg("c1's container? " + c1.eContainer());
    msg("c2 contained in c1? " + c1.getCategories().contains(c2));
    msg("c2 contained in c1? " + (c2.eContainer() == c1));
    msg("c2's resource is r2? " + (c2.eResource() == resource2));

    // Move c2 from resource1 to resource2
    resource2.getContents().add(c2);

    // assertSame(c1, c2.eContainer());

    msg("");
    msg("c1's container? " + c1.eContainer());
    msg("c2 contained in c1? " + c1.getCategories().contains(c2));
    msg("c2 contained in c1? " + (c2.eContainer() == c1));
    msg("c2's resource is r2? " + (c2.eResource() == resource2));

    // Make c3 dirty so that we can create a partial commit
    c3.setName("X");

    Set<EObject> committables = new HashSet<>();
    committables.add(c2);
    committables.add(resource2);

    tx.setCommittables(committables);
    tx.commit();

    session.close();
  }
}
