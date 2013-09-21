/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.io.ByteArrayOutputStream;

/**
 * @author Martin Taal
 */
public class HibernateBugzilla_381013_Test extends AbstractCDOTest
{
  public void testExport() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    assertNotNull(resource);
    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
    final String result = baos.toString();
    final String searchString = "id=\"lhttp://www.eclipse.org/emf/CDO/Eresource/4.0.0#CDOResource#3\"";
    final int index = result.indexOf(searchString);
    // at most one occurence
    assertEquals(true, index != -1);
    assertEquals(-1, result.indexOf(searchString, index + 1));
    // System.out.println(result);
  }
}
