/*
 * Copyright (c) 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.internal.common.util.URIHandlerRegistryImpl;

import org.eclipse.emf.ecore.resource.URIHandler;

/**
 * A {@link #INSTANCE global} URI handler registry.
 *
 * @author Eike Stepper
 * @since 4.13
 */
public interface URIHandlerRegistry
{
  public static final URIHandlerRegistry INSTANCE = URIHandlerRegistryImpl.INSTANCE;

  public URIHandler addURIHandler(String scheme, URIHandler handler);

  public URIHandler removeURIHandler(String scheme);

  public URIHandler getURIHandler(String scheme);
}
