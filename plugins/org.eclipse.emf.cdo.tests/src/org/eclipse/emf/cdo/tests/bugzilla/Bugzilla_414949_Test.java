/*
 * Copyright (c) 2013, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.tests.model3.NodeE;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.File;
import java.util.Collections;

/**
 * Bug 414949 - ArrayIndexOutOfBoundsException with CDOLegacyWrapper and CDOIDExternal
 *
 * @author Esteban Dugueperoux
 */
@Requires(IModelConfig.CAPABILITY_LEGACY)
public class Bugzilla_414949_Test extends AbstractCDOTest
{
  private static final String SHARED_RESOURCE_NAME = "sharedResource.aird";

  private static final String LOCAL_RESOURCE_NAME = "localResource.xmi";

  private Resource localResource;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    NodeE dDiagram = getModel3Factory().createNodeE();

    NodeA diagramDescription = getModel3Factory().createNodeA();
    NodeA concernSet = getModel3Factory().createNodeA();
    NodeA concernDescription = getModel3Factory().createNodeA();
    CDOUtil.getCDOObject(concernDescription);
    NodeA filterDescription1 = getModel3Factory().createNodeA();
    CDOUtil.getCDOObject(filterDescription1);
    concernDescription.getOtherNodes().add(filterDescription1);
    diagramDescription.getChildren().add(filterDescription1);
    concernSet.getChildren().add(concernDescription);
    diagramDescription.getChildren().add(concernSet);

    dDiagram.setMainNode(concernDescription);
    dDiagram.getOtherNodes().add(filterDescription1);

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
    String path = new File("./" + LOCAL_RESOURCE_NAME).getCanonicalPath();
    URI localResourceURI = URI.createFileURI(path);
    localResource = resourceSet.createResource(localResourceURI);
    localResource.getContents().add(diagramDescription);
    localResource.save(Collections.emptyMap());

    CDOSession session = openSession();
    CDOTransaction cdoTransaction = session.openTransaction(resourceSet);
    Resource sharedResource = cdoTransaction.createResource(getResourcePath(SHARED_RESOURCE_NAME));
    sharedResource.getContents().add(dDiagram);
    sharedResource.save(Collections.emptyMap());
  }

  @Override
  protected void doTearDown() throws Exception
  {
    localResource.delete(Collections.emptyMap());
    localResource = null;
    super.doTearDown();
  }

  public void testEcoreUtilGetURIWithCDOLegacyWrapperBug() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.getResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

    CDOResource resource = transaction.getResource(getResourcePath(SHARED_RESOURCE_NAME));
    EList<EObject> contents = resource.getContents();
    contents.get(0);
  }
}
