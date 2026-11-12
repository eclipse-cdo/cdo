/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.common.model.EMFPackageRegistryInitializer;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.internal.common.model.DefaultEMFPackageRegistryInitializer;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.EcorePackage;

/**
 * Initializes the global EMF package registry for standalone CDO applications.
 * <p>
 * The initialization is triggered by both client sessions and server repositories,
 * but is performed only once per class loader through Java class initialization.
 * The initializer implementation is selected through the
 * {@link EMFPackageRegistryInitializer} factory mechanism.
 * <p>
 * Initialization is skipped when OSGi is running. Failures are logged without
 * preventing subsequent CDO operations.
 *
 * @author Eike Stepper
 */
public final class EMFPackageRegistryBootstrap
{
  static
  {
    try
    {
      if (!OMPlatform.INSTANCE.isOSGiRunning())
      {
        initializeStandalonePackageRegistry();
      }
    }
    catch (Throwable ex)
    {
      OM.LOG.error("Failed to initialize the global EMF package registry", ex);
    }
  }

  private EMFPackageRegistryBootstrap()
  {
  }

  private static void initializeStandalonePackageRegistry()
  {
    // Ensure that these 3 packages are registered with the global package registry in stand-alone
    EcorePackage.eINSTANCE.getClass();
    EtypesPackage.eINSTANCE.getClass();
    EresourcePackage.eINSTANCE.getClass();

    String initializerType = OMPlatform.INSTANCE.getProperty( //
        EMFPackageRegistryInitializer.Factory.PRODUCT_GROUP, //
        DefaultEMFPackageRegistryInitializer.Factory.TYPE);

    if (EMFPackageRegistryInitializer.Factory.NONE.equals(initializerType))
    {
      return;
    }

    try
    {
      EMFPackageRegistryInitializer initializer = IManagedContainer.INSTANCE.createElement( //
          EMFPackageRegistryInitializer.Factory.PRODUCT_GROUP, //
          initializerType, //
          (String)null);

      initializer.initializePackageRegistry();
    }
    catch (Exception ex)
    {
      OM.LOG.error("Failed to initialize the global EMF package registry using factory type '" + initializerType + "'", ex); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * Ensures that the global EMF package registry initialization has been attempted.
   * <p>
   * This method is intentionally empty. Invoking it triggers initialization of this
   * class, which executes the static initializer exactly once per class loader.
   * Subsequent invocations have no effect, even if initialization failed.
   */
  public static void init()
  {
    // Intentionally empty. Trigger class initialization.
  }
}
