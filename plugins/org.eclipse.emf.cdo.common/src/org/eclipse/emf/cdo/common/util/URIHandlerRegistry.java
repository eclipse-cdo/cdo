/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.internal.common.util.URIHandlerRegistryImpl;

import org.eclipse.emf.ecore.resource.URIHandler;

/**
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
