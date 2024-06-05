/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.lm;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Process;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamMode;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.Updates;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class LMVersionEvolutionTest extends AbstractLMTest
{
  private static final String INITIAL_STREAM = "InitialStream";

  private static final String CLIENT_MODULE = "ClientModule";

  private static final String CLIENT_RESOURCE = "ClientResource";

  private static final String SUPPLIER_MODULE = "SupplierModule";

  private static final String SUPPLIER_RESOURCE = "SupplierResource";

  private static final String SUPPLIER_DROP_TYPE = "SupplierDropType";

  private static final String TAG1_0 = "Tag1.0";

  public void testVersionEvolution() throws Exception
  {
    ISystemDescriptor systemDescriptor = createSystemRepository();
    System system = systemDescriptor.getSystem();

    // Add SupplierModule.
    ModuleCreationResult supplierModuleCreationResult = createModule(systemDescriptor, system, SUPPLIER_MODULE, INITIAL_STREAM, 0, 1);
    IAssemblyDescriptor supplierStreamDescriptor = supplierModuleCreationResult.assemblyDescriptor;
    Module supplierModule = supplierModuleCreationResult.module;
    Stream supplierStream = supplierModuleCreationResult.stream;

    editStream(supplierStreamDescriptor, transaction -> {
      CDOResource resource = transaction.createResource(SUPPLIER_RESOURCE);
      DropType dropType = LMFactory.eINSTANCE.createDropType();
      dropType.setName(SUPPLIER_DROP_TYPE);
      System sys = LMFactory.eINSTANCE.createSystem();
      sys.setName("SupplierSystem");
      Process process = LMFactory.eINSTANCE.createProcess();
      process.getDropTypes().add(dropType);
      sys.setProcess(process);
      resource.getContents().add(sys);
    });

    publishTag(systemDescriptor, system, supplierStream, "Tag0.1");

    // Add ClientModule.
    ModuleCreationResult clientModuleCreationResult = createModule(systemDescriptor, system, CLIENT_MODULE, INITIAL_STREAM, 0, 1);
    IAssemblyDescriptor clientStreamDescriptor = clientModuleCreationResult.assemblyDescriptor;

    // Create dependency on SupplierModule to ClientModule.
    createDependency(clientStreamDescriptor, SUPPLIER_MODULE, new VersionRange("[0.0.0,1.0.0)"));

    // Update the ClientModule descriptor.
    updateAssemblyDescriptor(clientStreamDescriptor);

    // Modify ClientModule.
    editStream(clientStreamDescriptor, transaction -> {
      CDOResource resourceClient = transaction.createResource(CLIENT_RESOURCE);

      CDOView view = clientStreamDescriptor.getCheckout().openView();
      ResourceSet resourceSet = view.getResourceSet();

      // Retrieve drop from supplier.
      URI uri = createModuleResourceURI(SUPPLIER_MODULE, SUPPLIER_RESOURCE);
      CDOResource resourceSupplier = (CDOResource)resourceSet.getResource(uri, true);
      assertThat(resourceSupplier, notNullValue());
      EObject eObject = resourceSupplier.getContents().get(0);
      assertThat(eObject, instanceOf(System.class));
      DropType supplierDropType = ((System)resourceSupplier.getContents().get(0)).getProcess().getDropTypes().get(0);

      System clientSystem = LMFactory.eINSTANCE.createSystem();
      clientSystem.setName("ClientStream");

      Module clientModule = LMFactory.eINSTANCE.createModule();
      clientModule.setName("SampleClientModule");

      clientSystem.getModules().add(clientModule);

      Stream stream = LMFactory.eINSTANCE.createStream();
      stream.setCodeName("ClientStream");

      clientModule.getStreams().add(stream);

      Drop drop = LMFactory.eINSTANCE.createDrop();
      drop.setLabel("ClientDrop");

      drop.setType(supplierDropType);

      stream.getContents().add(drop);
      resourceClient.getContents().add(clientSystem);
    });

    // Check client refers to supplier.
    retrieveValueAndTest(clientStreamDescriptor, //
        resourceSupplier -> assertNotEquals(0, resourceSupplier.getContents().size()), //
        element -> assertNotEquals(null, element));

    // Try to get a version not published yet.
    updateDependency(clientStreamDescriptor, SUPPLIER_MODULE, new VersionRange("[1.0.0,2.0.0)"));

    try
    {
      waitForUpdates(clientStreamDescriptor);
      fail("InterruptedException expected");
    }
    catch (InterruptedException expected)
    {
      // Success
    }

    // Make changes on supplier.
    editStream(supplierStreamDescriptor, transaction -> {
      CDOResource resource = transaction.getResource(SUPPLIER_RESOURCE);
      resource.getContents().clear();
    });

    // Create release from initial stream of supplier.
    publishRelease(systemDescriptor, system, supplierStream, "Release0.1");
    supplierStream.getLastRelease();

    // Create a new stream for 1.0.
    Stream stream1_0 = createStream(systemDescriptor, supplierModule, supplierStream.getLastRelease(), 1, 0, "Stream1.0");

    // Check that initialStream is now maintenance.
    assertThat(supplierStream.getMode(), is(StreamMode.MAINTENANCE));

    // Try to create tag from 1.0 stream of supplier.
    try
    {
      publishTag(systemDescriptor, system, stream1_0, TAG1_0);
      fail("CDOException expected");
    }
    catch (CDOException expected)
    {
      // Success:
      // Module definition version 0.1.0 is inconsistent with the stream version 1.0
    }

    setModuleVersion(stream1_0, SUPPLIER_MODULE, 1, 0, 0);

    // Create tag from 1.0 stream of supplier.
    publishTag(systemDescriptor, system, stream1_0, TAG1_0);

    Baseline baseline = stream1_0.getContents().stream().filter(b -> b.getName().equals(TAG1_0)).findAny().orElse(null);
    assertThat(baseline, instanceOf(Drop.class));
    if (baseline instanceof Drop)
    {
      Drop tag1_0 = (Drop)baseline;
      assertTrue(tag1_0.getVersion().compareTo(Version.createOSGi(1, 0, 0)) > 0);
    }

    // Update dependency in client.
    updateDependency(clientStreamDescriptor, SUPPLIER_MODULE, new VersionRange("[1.0.0,2.0.0)"));

    Updates updates = waitForUpdates(clientStreamDescriptor);
    assertEquals(2, updates.getModifications().size());

    // Update the client.
    updateAssemblyDescriptor(clientStreamDescriptor);

    // Check that client model cannot resolve the supplier model element (because deleted).
    retrieveValueAndTest(clientStreamDescriptor, //
        resourceSupplier -> assertEquals(0, resourceSupplier.getContents().size()), //
        element -> assertEquals(null, element));
  }

  private void retrieveValueAndTest(IAssemblyDescriptor clientStreamDescriptor, Consumer<Resource> testResource, Consumer<DropType> testDropType)
  {
    CDOTransaction transaction = clientStreamDescriptor.getCheckout().openTransaction();
    URI uriSupplier = createModuleResourceURI(SUPPLIER_MODULE, SUPPLIER_RESOURCE);

    CDOResource resourceSupplier = (CDOResource)transaction.getResourceSet().getResource(uriSupplier, true);
    testResource.accept(resourceSupplier);

    CDOResource clientResource = transaction.getResource(CLIENT_RESOURCE);

    System sampleClientSystem = (System)clientResource.getContents().get(0);
    Module sampleClientModule = sampleClientSystem.getModules().get(0);
    Stream sampleClientStream = sampleClientModule.getStreams().get(0);
    Baseline sampleClientbaseline = sampleClientStream.getContents().get(0);
    assertThat(sampleClientbaseline, instanceOf(Drop.class));
    DropType sampleClientDropType = ((Drop)sampleClientbaseline).getType();

    testDropType.accept(sampleClientDropType);

    transaction.close();
  }

  private static void assertNotEquals(Object unexpected, Object actual)
  {
    if (objectsAreEqual(unexpected, actual))
    {
      fail("expected: not equal but was: <" + actual + ">");
    }
  }

  private static void assertNotEquals(int unexpected, int actual)
  {
    if (unexpected == actual)
    {
      fail("expected: not equal but was: <" + actual + ">");
    }
  }

  private static boolean objectsAreEqual(Object obj1, Object obj2)
  {
    if (obj1 == null)
    {
      return obj2 == null;
    }

    return obj1.equals(obj2);
  }
}
