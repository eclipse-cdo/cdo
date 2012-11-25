/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.legacy.model1.Model1Package;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Martin Taal
 */
@CleanRepositoriesBefore
public class HibernateBugzilla_392653_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    getRepository("repo2", false).setInitialPackages(Model1Package.eINSTANCE);
    getRepository("repo2", true);
  }

  public void testBugzilla() throws Exception
  {
    InternalRepository repo2 = getRepository("repo2");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo2);
    exporter.exportRepository(baos);
    System.out.println(baos.toString());

    InternalRepository repo3 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo3);
    importer.importRepository(bais);

    useAfterImport("repo2");
  }

  private void useAfterImport(String repoName) throws CommitException
  {
    CDOSession session2 = openSession(repoName);
    CDOTransaction transaction2 = session2.openTransaction();

    // Read all repo contents
    TreeIterator<EObject> iter = transaction2.getRootResource().getAllContents();
    while (iter.hasNext())
    {
      iter.next();
    }
    transaction2.commit();

    session2.close();
  }

}
