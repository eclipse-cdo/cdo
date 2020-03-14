/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.util.CDOURIData;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.junit.Assert;

/**
 * Bug 437817: "Only one view per repository..." RuntimeException using connection aware URI
 *
 * @author Esteban Dugueperoux
 */
@Requires(ISessionConfig.CAPABILITY_NET4J_TCP)
public class Bugzilla_437817_Test extends AbstractCDOTest
{
  public void testConnectionAwareURITwiceCDOResourceCreation() throws Exception
  {
    getRepository();

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(CDONet4jUtil.PROTOCOL_TCP, CDOResourceFactory.INSTANCE);

    URI sharedResource1URI = URI
        .createURI(CDONet4jUtil.PROTOCOL_TCP + "://localhost:2036/" + RepositoryConfig.REPOSITORY_NAME + getResourcePath("/sharedResource1"))
        .appendQuery(CDOURIData.TRANSACTIONAL_PARAMETER + "=true");
    URI sharedResource2URI = URI
        .createURI(CDONet4jUtil.PROTOCOL_TCP + "://localhost:2036/" + RepositoryConfig.REPOSITORY_NAME + getResourcePath("/sharedResource2"))
        .appendQuery(CDOURIData.TRANSACTIONAL_PARAMETER + "=true");
    Resource sharedResource1 = resourceSet.createResource(sharedResource1URI);
    Resource sharedResource2 = resourceSet.createResource(sharedResource2URI);

    Assert.assertTrue(sharedResource1 instanceof CDOResource);
    Assert.assertTrue(sharedResource2 instanceof CDOResource);
    CDOResource sharedCDOResource1 = (CDOResource)sharedResource1;
    CDOResource sharedCDOResource2 = (CDOResource)sharedResource2;
    assertEquals("Both CDOResources should have the same CDOView as they have the same ResourceSet and use content from the same repository",
        sharedCDOResource1.cdoView(), sharedCDOResource2.cdoView());
  }

  public void testConnectionAwareURITwiceCDOResourceCreationWithUUID() throws Exception
  {
    getTestProperties().put(Props.OVERRIDE_UUID, RepositoryConfig.REPOSITORY_NAME + "UUID");
    testConnectionAwareURITwiceCDOResourceCreation();
  }
}
