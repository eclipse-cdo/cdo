/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.lm;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ChangeDeletionException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Eike Stepper
 */
public class LMFlowsTest extends AbstractLMTest
{
  private static final String INITIAL_STREAM = "InitialStream";

  private static final String CLIENT_MODULE = "ClientModule";

  private static final String SUPPLIER_MODULE = "SupplierModule";

  private static final String SUPPLIER_RESOURCE_A = "SupplierResourceA";

  private static final String SUPPLIER_RESOURCE_B = "SupplierResourceB";

  private static final String CHANGE_1 = "Change1";

  public void testEntireNormalFlow() throws Exception
  {
    ISystemDescriptor systemDescriptor = createSystemRepository();
    System system = systemDescriptor.getSystem();

    // Add SupplierModule.
    ModuleCreationResult supplierModuleCreationResult = createModule(systemDescriptor, system, SUPPLIER_MODULE, INITIAL_STREAM, 0, 1);
    IAssemblyDescriptor supplierStreamDescriptor = supplierModuleCreationResult.assemblyDescriptor;
    Stream supplierStream = supplierModuleCreationResult.stream;

    // Create change1 in SupplierModule.
    Change change1 = createChange(systemDescriptor, supplierStream, CHANGE_1, SUPPLIER_MODULE + " - " + CHANGE_1, transaction -> {
      transaction.createResource(SUPPLIER_RESOURCE_A);
    });

    // Deliver change1 to supplierStream.
    deliverChange(systemDescriptor, supplierStream, change1);

    // Test delivery.
    {
      CDOView view = supplierStreamDescriptor.getCheckout().openView();
      assertThat(view.hasResource(SUPPLIER_RESOURCE_A), is(true));
      view.close();
    }

    // Add ClientModule.
    ModuleCreationResult clientModuleCreationResult = createModule(systemDescriptor, system, CLIENT_MODULE, INITIAL_STREAM, 0, 1);
    IAssemblyDescriptor clientStreamDescriptor = clientModuleCreationResult.assemblyDescriptor;

    // Add dependency on SupplierModule to ClientModule.
    createDependencyAndUpdate(clientStreamDescriptor, SUPPLIER_MODULE);

    // Modify stream of SupplierModule.
    editStream(supplierStreamDescriptor, transaction -> {
      transaction.createResource(SUPPLIER_RESOURCE_B);
    });

    // Publish a Tag for SupplierModule and update ClientModule.
    URI uri = createModuleResourceURI(SUPPLIER_MODULE, SUPPLIER_RESOURCE_B);
    publishTagUpdateClientAndCheckUri(systemDescriptor, system, supplierStream, clientStreamDescriptor, uri);

    // Remove dependency on SupplierModule from ClientModule and update ClientModule.
    removeDependencies(clientStreamDescriptor);

    // Add dependency on SupplierModule back to ClientModule and update ClientModule.
    createDependencyAndUpdate(clientStreamDescriptor, SUPPLIER_MODULE);

    // Create a new change that can be deleted because it was never delivered.
    Change change2 = createChange(systemDescriptor, supplierStream, "Change2", SUPPLIER_MODULE + " - Change2", transaction -> {
      transaction.createResource("To Be Deleted");
    });

    systemDescriptor.deleteChange(change2, new NullProgressMonitor());

    // Create another new change that can not be deleted because it has been delivered already.
    Change change3 = createChange(systemDescriptor, supplierStream, "Change3", SUPPLIER_MODULE + " - Change3", transaction -> {
      transaction.createResource("Not Deleteable");
    });

    deliverChange(systemDescriptor, supplierStream, change3);

    try
    {
      systemDescriptor.deleteChange(change3, new NullProgressMonitor());
      fail("ChangeDeletionException expected");
    }
    catch (ChangeDeletionException expected)
    {
      // SUCCESS.
    }
  }

  // public void testDelivery() throws Exception
  // {
  // ISystemDescriptor systemDescriptor = createSystemRepository();
  // System system = systemDescriptor.getSystem();
  //
  // // Add supplier Module
  // ModuleCreationResult supplierModuleCreationResult = createModule(systemDescriptor, system, MODULE_SUPPLIER,
  // INITIAL_STREAM, 0, 1);
  //
  // // Module supplierModule = supplierModuleCreationResult.module;
  // Stream supplierStream = supplierModuleCreationResult.stream;
  // IAssemblyDescriptor supplierStreamDescriptor = supplierModuleCreationResult.assemblyDescriptor;
  //
  // Change change1 = createChange(systemDescriptor, supplierStream, CHANGE_1, MODULE_SUPPLIER + " - " + CHANGE_1,
  // transaction -> {
  // transaction.createResource(MODULE_SUPPLIER_A);
  // });
  //
  // // Deliver change1 to supplierStream.
  // deliverChange(systemDescriptor, supplierStream, change1);
  //
  // // Test delivery
  // {
  // CDOView view = supplierStreamDescriptor.getCheckout().openView();
  // assertThat(view.hasResource(MODULE_SUPPLIER_A), is(true));
  // view.close();
  // }
  // }
}
