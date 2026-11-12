/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.model.EMFPackageRegistryInitializer;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * Registers generated EMF packages contributed by classpath extension metadata.
 *
 * @author Eike Stepper
 */
public final class DefaultEMFPackageRegistryInitializer implements EMFPackageRegistryInitializer
{
  public DefaultEMFPackageRegistryInitializer()
  {
  }

  @Override
  public void initializePackageRegistry()
  {
    EcorePlugin.ExtensionProcessor.process(null);
  }

  /**
   * Creates default package registry initializers.
   *
   * @author Eike Stepper
   */
  public static final class Factory extends EMFPackageRegistryInitializer.Factory
  {
    public static final String TYPE = "default"; //$NON-NLS-1$

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public EMFPackageRegistryInitializer create(String description)
    {
      return new DefaultEMFPackageRegistryInitializer();
    }
  }
}
