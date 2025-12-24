/*
 * Copyright (c) 2021, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.util;

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.resource.URIHandler;

/**
 * Creates {@link URIHandler URI handler} instances.
 *
 * @author Eike Stepper
 * @since 4.13
 */
public abstract class URIHandlerFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.common.util.uriHandlers"; //$NON-NLS-1$

  public URIHandlerFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  @Override
  public abstract URIHandler create(String description) throws ProductCreationException;
}
