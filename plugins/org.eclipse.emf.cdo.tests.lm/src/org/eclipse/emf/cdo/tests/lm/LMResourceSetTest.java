/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.lm;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assume.assumeThat;

import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.internal.client.LMResourceSetConfigurer;
import org.eclipse.emf.cdo.lm.internal.client.LMResourceSetConfigurer.CheckoutResult;
import org.eclipse.emf.cdo.lm.internal.client.LMResourceSetConfigurer.Result;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 */
public class LMResourceSetTest extends AbstractLMTest
{
  private static final String MODULE_A = "A";

  private static final String MODULE_B = "B";

  private static final String INITIAL_STREAM = "InitialStream";

  private static final String TAG1_0 = "Tag1.0";

  public void testResourceSetReconfiguration() throws Exception
  {
    ISystemDescriptor systemDescriptor = createSystemRepository();
    System system = systemDescriptor.getSystem();

    ModuleCreationResult resultA = createModule(systemDescriptor, system, MODULE_A, INITIAL_STREAM, 0, 1);
    assertCheckoutResult(resultA);

    createDependencyModule(resultA, MODULE_B);
    assertCheckoutResult(resultA);
    updateAssemblyDescriptor(resultA.assemblyDescriptor);
    assertCheckoutResult(resultA);
  }

  private ModuleCreationResult createDependencyModule(ModuleCreationResult referer, String moduleName) throws Exception
  {
    ISystemDescriptor systemDescriptor = referer.assemblyDescriptor.getSystemDescriptor();
    System system = systemDescriptor.getSystem();

    ModuleCreationResult result = createModule(systemDescriptor, system, moduleName, INITIAL_STREAM, 0, 1);
    publishTag(systemDescriptor, system, result.stream, TAG1_0);

    editModuleDefinition(referer.assemblyDescriptor, moduleDefinition -> {
      DependencyDefinition dependency = ModulesFactory.eINSTANCE.createDependencyDefinition(moduleName);
      moduleDefinition.getDependencies().add(dependency);
    });

    return result;
  }

  private static void assertCheckoutResult(ModuleCreationResult moduleCreationResult)
  {
    ResourceSet resourceSet = moduleCreationResult.assemblyDescriptor.getCheckout().getView().getResourceSet();
    Result configurerResult = LMResourceSetConfigurer.Result.of(resourceSet);
    assumeThat(configurerResult, instanceOf(CheckoutResult.class));
  }
}
