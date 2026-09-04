/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.internal.db.bundle;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerInitializer;

/**
 * Prepares the declarative managed-container contributions of the CDO server DB bundle.
 *
 * @author Eike Stepper
 */
public final class ManagedContainerInitializer implements IManagedContainerInitializer
{
  @Override
  public void initialize(IManagedContainer container)
  {
    OM.BUNDLE.prepareContainer(container);
  }
}

