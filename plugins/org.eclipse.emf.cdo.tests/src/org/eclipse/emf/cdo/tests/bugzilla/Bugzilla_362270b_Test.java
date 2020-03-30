/*
 * Copyright (c) 2013, 2016, 2020 Esteban Dugueperoux and others.
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
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.object.CDOLegacyAdapter;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.Collections;

/**
 * @author Esteban Dugueperoux
 */
public class Bugzilla_362270b_Test extends AbstractCDOTest
{
  private static final String EXTENSION = "model1";

  private static final String REMOTE_RESOURCE_PATH = "sharedResource." + EXTENSION;

  private CDOTransaction cdoTransaction;

  private ContainedElementNoOpposite containedElementNoOpposite1;

  private ContainedElementNoOpposite containedElementNoOpposite2;

  private RefSingleNonContainedNPL refSingleNonContainedNPL1;

  private RefSingleNonContainedNPL refSingleNonContainedNPL2;

  private void registerResourceFactories(ResourceSet resourceSet)
  {
    Resource.Factory.Registry registry = resourceSet.getResourceFactoryRegistry();
    registry.getExtensionToFactoryMap().put(EXTENSION, new XMIResourceFactoryImpl());
  }

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    CDOSession session = openSession();
    cdoTransaction = session.openTransaction();
    registerResourceFactories(cdoTransaction.getResourceSet());

    URI localResourceURI = URI.createFileURI(createTempFile(getName(), "." + EXTENSION).getCanonicalPath());
    Resource remoteResource = cdoTransaction.createResource(getResourcePath(REMOTE_RESOURCE_PATH));
    Resource localResource = cdoTransaction.getResourceSet().createResource(localResourceURI);

    GenRefMultiContained localGenRefMultiContained = getModel4Factory().createGenRefMultiContained();
    containedElementNoOpposite1 = getModel4Factory().createContainedElementNoOpposite();
    containedElementNoOpposite2 = getModel4Factory().createContainedElementNoOpposite();
    localGenRefMultiContained.getElements().add(containedElementNoOpposite1);
    localGenRefMultiContained.getElements().add(containedElementNoOpposite2);

    localResource.getContents().add(localGenRefMultiContained);
    localResource.save(Collections.emptyMap());

    GenRefMultiContained genRefMultiContained = getModel4Factory().createGenRefMultiContained();
    refSingleNonContainedNPL1 = getModel4Factory().createRefSingleNonContainedNPL();
    refSingleNonContainedNPL2 = getModel4Factory().createRefSingleNonContainedNPL();
    refSingleNonContainedNPL1.setElement(containedElementNoOpposite1);
    refSingleNonContainedNPL2.setElement(containedElementNoOpposite1);
    genRefMultiContained.getElements().add(refSingleNonContainedNPL1);
    genRefMultiContained.getElements().add(refSingleNonContainedNPL2);
    remoteResource.getContents().add(genRefMultiContained);
    remoteResource.save(Collections.emptyMap());

    genRefMultiContained.eAdapters().add(new ECrossReferenceAdapter());
  }

  @Override
  public void tearDown() throws Exception
  {
    cdoTransaction = null;
    containedElementNoOpposite1 = null;
    containedElementNoOpposite2 = null;
    refSingleNonContainedNPL1 = null;
    refSingleNonContainedNPL2 = null;
    super.tearDown();
  }

  public void testRollback() throws Exception
  {
    RemoteUser remoteUser = new RemoteUser();
    remoteUser.accessContents();

    CDOSavepoint savepoint = cdoTransaction.setSavepoint();
    refSingleNonContainedNPL1.setElement(containedElementNoOpposite2);
    refSingleNonContainedNPL1.setElement(containedElementNoOpposite1);
    savepoint.rollback();
    refSingleNonContainedNPL2.setElement(containedElementNoOpposite2);
    refSingleNonContainedNPL2.setElement(containedElementNoOpposite1);

    cdoTransaction.commit();

    Adapter adapter = EcoreUtil.getAdapter(containedElementNoOpposite1.eAdapters(), CDOLegacyAdapter.class);
    assertNull("A legacy adapter should NOT be attached to an external object", adapter);
  }

  /**
   * @author Esteban Dugueperoux
   */
  private final class RemoteUser
  {
    private CDOTransaction transaction;

    private CDOResource sharedResource;

    private GenRefMultiContained genRefMultiContained;

    public RemoteUser()
    {
      CDOSession session = openSession();
      transaction = session.openTransaction();
      registerResourceFactories(transaction.getResourceSet());
      sharedResource = transaction.getResource(getResourcePath(REMOTE_RESOURCE_PATH));
    }

    public void accessContents()
    {
      genRefMultiContained = (GenRefMultiContained)sharedResource.getContents().get(0);
      genRefMultiContained.eAdapters().add(new ECrossReferenceAdapter());
    }
  }
}
