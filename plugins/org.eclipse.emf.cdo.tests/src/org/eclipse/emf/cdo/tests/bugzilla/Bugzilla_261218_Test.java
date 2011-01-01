/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * See bug 261218
 * 
 * @author Simon McDuff
 */
public class Bugzilla_261218_Test extends AbstractCDOTest
{
  /**
   * CDOListFeatureDeltaImpl.cacheIndices was introduced.
   */
  public void testBugzilla_261218_Containment() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.getOrCreateResource("res1");

    Category folder = getModel1Factory().createCategory();
    resource.getContents().add(folder);

    IOUtil.OUT().println("Adding...");
    for (int i = 0; i < 10000; ++i)
    {
      Category file = getModel1Factory().createCategory();
      folder.getCategories().add(file);
    }

    IOUtil.OUT().println("Committing...");
    transaction.commit();

    IOUtil.OUT().println("Removing...");
    for (int i = 9999; i >= 0; --i)
    {
      folder.getCategories().remove(i);
    }

    // =>90 seconds
    IOUtil.OUT().println("Committing...");
    transaction.commit();
  }

  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    enableConsole();
    super.doTearDown();
  }
}
