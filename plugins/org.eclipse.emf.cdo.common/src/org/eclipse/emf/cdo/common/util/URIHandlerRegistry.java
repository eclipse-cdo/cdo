/*
 * Copyright (c) 2021, 2024-2026 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.spi.common.util.URIHandlerFactory;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;

/**
 * A {@link #INSTANCE global} URI handler registry.
 * <p>
 * Handlers registered here are available through this registry. They are not automatically available to arbitrary EMF
 * URI converters. Use {@link #installTo(URIConverter)} or {@link #installTo(ResourceSet)} when a particular converter should
 * consult this registry. For example:
 * <pre>
 * ResourceSet resourceSet = new ResourceSetImpl();
 * URIHandlerRegistry.INSTANCE.install(resourceSet);
 * URI uri = URI.createURI("cdo.checkout.text://my-checkout/path/to/file.txt");
 * InputStream in = resourceSet.getURIConverter().createInputStream(uri);
 * </pre>
 * <p>
 * The schemes available at runtime depend on the {@link URIHandlerFactory URIHandlerFactory} contributions.
 *
 * @author Eike Stepper
 * @since 4.13
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface URIHandlerRegistry
{
  public static final URIHandlerRegistry INSTANCE = URIHandlerRegistryImpl.INSTANCE;

  /**
   * Adds a URI handler for the given scheme to this registry.
   * <p>
   * If a handler was already registered for the scheme, it is replaced and returned.
   */
  public URIHandler addURIHandler(String scheme, URIHandler handler);

  /**
   * Removes the URI handler for the given scheme from this registry and returns it.
   */
  public URIHandler removeURIHandler(String scheme);

  /**
   * Returns the URI handler for the given scheme, or {@code null} if none is registered.
   */
  public URIHandler getURIHandler(String scheme);

  /**
   * Installs this registry into a URI converter, if it is not already present.
   *
   * @return {@code true} if the converter was changed, or {@code false} if this registry was already installed.
   * @since 4.28
   */
  public boolean installTo(URIConverter uriConverter);

  /**
   * Installs this registry into a resource set's URI converter, if it is not already present.
   *
   * @return {@code true} if the resource set was changed, or {@code false} if this registry was already installed.
   * @since 4.28
   */
  public boolean installTo(ResourceSet resourceSet);

  /**
   * Removes this exact registry instance from a URI converter.
   *
   * @return {@code true} if the converter was changed, or {@code false} if this registry was not installed.
   * @since 4.28
   */
  public boolean uninstallFrom(URIConverter uriConverter);

  /**
   * Removes this exact registry instance from a resource set's URI converter.
   *
   * @return {@code true} if the resource set was changed, or {@code false} if this registry was not installed.
   * @since 4.28
   */
  public boolean uninstallFrom(ResourceSet resourceSet);
}
