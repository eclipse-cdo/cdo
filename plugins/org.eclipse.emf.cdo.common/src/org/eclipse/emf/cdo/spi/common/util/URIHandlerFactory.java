/*
 * Copyright (c) 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
