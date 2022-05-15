/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.lm;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;

import org.junit.jupiter.api.Test;

/**
 * @author Eike Stepper
 */
public class LMIntegrationTest extends AbstractIntegrationTest
{
  private static final String INITIAL_STREAM = "InitialStream";

  private static final String MODULE_CLIENT = "ModuleClient";

  private static final String MODULE_SUPPLIER = "ModuleSupplier";

  private static final String MODULE_SUPPLIER_A = "ModuleSupplierA";

  private static final String MODULE_SUPPLIER_B = "ModuleSupplierB";

  private static final String CHANGE_1 = "Change1";

  @Test
  protected void entireNormalFlow() throws Exception
  {
    ISystemDescriptor systemDescriptor = createSystemRepository();
    System system = systemDescriptor.getSystem();

    // Add supplier Module
    ModuleCreationResult supplierModuleCreationResult = createModule(systemDescriptor, system, MODULE_SUPPLIER, INITIAL_STREAM, 0, 1);

    // Module supplierModule = supplierModuleCreationResult.module;
    Stream supplierStream = supplierModuleCreationResult.stream;
    IAssemblyDescriptor supplierStreamDescriptor = supplierModuleCreationResult.assemblyDescriptor;

    Change change1 = createChange(systemDescriptor, supplierStream, CHANGE_1, MODULE_SUPPLIER + " - " + CHANGE_1, transaction -> {
      transaction.createResource(MODULE_SUPPLIER_A);
    });

    // Deliver change1 to supplierStream.
    deliverChange(systemDescriptor, supplierStream, change1);

    // Test delivery
    {
      CDOView view = supplierStreamDescriptor.getCheckout().openView();
      assertThat(view.hasResource(MODULE_SUPPLIER_A), is(true));
      view.close();
    }

    // Add ModuleClient
    ModuleCreationResult clientModuleCreationResult = createModule(systemDescriptor, system, MODULE_CLIENT, INITIAL_STREAM, 0, 1);
    // Module clientModule = clientModuleCreationResult.module;
    // Stream clientStream = clientModuleCreationResult.stream;

    IAssemblyDescriptor clientStreamDescriptor = clientModuleCreationResult.assemblyDescriptor;

    // Create dependency from clientModule to supplierModule
    createDependencyAndUpdate(clientStreamDescriptor, MODULE_SUPPLIER);

    // Modify ModuleClient
    editStream(supplierStreamDescriptor, transaction -> {
      transaction.createResource(MODULE_SUPPLIER_B);
    });

    // Publish a Tag for ModuleSupplier and update ModuleClient.
    URI uri = URI.createURI(CDOProtocolConstants.PROTOCOL_NAME + "://" + MODULE_SUPPLIER + "/" + MODULE_SUPPLIER_B);
    publishTagUpdateClientAndCheckUri(systemDescriptor, system, supplierStream, clientStreamDescriptor, uri);

    // Remove dependency on ModuleSupplier from ModuleClient and update ModuleClient.
    removeDependencies(clientStreamDescriptor);

    // Add dependency on ModuleSupplier back to ModuleClient and update ModuleClient.
    createDependencyAndUpdate(clientStreamDescriptor, MODULE_SUPPLIER);
  }
}
