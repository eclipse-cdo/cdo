/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.examples.bundle;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.connector.IConnector;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public class Examples
{
  public static void commonPattern()
  {
    IConnector connector = getConnector();

    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName("my-repo");

    CDOSession session = configuration.openSession();
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource("/path/to/my/resource");

    EObject object = Model1Factory.eINSTANCE.createCompany();
    resource.getContents().add(object);

    transaction.commit();
    session.close();
  }

  private static IConnector getConnector()
  {
    throw new UnsupportedOperationException();
  }
}
