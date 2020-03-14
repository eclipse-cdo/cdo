/*
 * Copyright (c) 2013, 2016 Esteban Dugueperoux (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.Collections;

/**
 * @author Esteban Dugueperoux
 */
@Requires(ISessionConfig.CAPABILITY_NET4J_TCP)
public class Bugzilla_400128_Test extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "cdo.resource";

  private URI xmiURI;

  public void testUnload() throws Exception
  {
    initModelWithCrossReferenceFromCDO2XMI();

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model1", new XMIResourceFactoryImpl());
    resourceSet.eAdapters().add(new ECrossReferenceAdapter());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction(resourceSet);
    CDOResource cdoResource = transaction.getResource(getResourcePath(RESOURCE_PATH));

    Resource xmiResource = resourceSet.getResource(xmiURI, true);

    cdoResource.cdoView().close(); // Closes the transaction
    xmiResource.unload();
  }

  private void initModelWithCrossReferenceFromCDO2XMI() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model1", new XMIResourceFactoryImpl());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction(resourceSet);
    CDOResource cdoResource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    xmiURI = URI.createFileURI(createTempFile(getName(), ".model1").getCanonicalPath());
    Resource xmiResource = resourceSet.createResource(xmiURI);

    ContainedElementNoOpposite xmiElement = getModel4Factory().createContainedElementNoOpposite();
    xmiResource.getContents().add(xmiElement);

    RefSingleNonContainedNPL cdoElement = getModel4Factory().createRefSingleNonContainedNPL();
    cdoElement.setElement(xmiElement); // Create a cross reference from CDO to XMI
    cdoResource.getContents().add(cdoElement);

    cdoResource.save(Collections.emptyMap());
    xmiResource.save(Collections.emptyMap());

    session.close();
  }
}
