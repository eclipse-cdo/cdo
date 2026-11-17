/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.XMLConfiguration;

/**
 * Optional extension callback for consumers of the server's centrally prepared XML configuration. Implementations may
 * override the ordinary or repository-aware start method. Unimplemented callbacks fail explicitly so a configuration
 * lifecycle cannot silently skip an extension.
 *
 * @author Eike Stepper
 * @since 4.27
 */
public interface IAppExtension6 extends IAppExtension
{
  /**
   * Starts an ordinary extension with the prepared configuration.
   *
   * @param configuration the immutable prepared configuration snapshot
   * @throws Exception if startup fails
   * @throws UnsupportedOperationException if the extension does not implement prepared-configuration startup
   */
  public default void start(XMLConfiguration configuration) throws Exception
  {
    throw new UnsupportedOperationException("The extension does not support prepared XML configurations"); //$NON-NLS-1$
  }

  /**
   * Starts a repository-aware extension with repositories and the prepared configuration.
   *
   * @param repositories configured repositories
   * @param configuration the immutable prepared configuration snapshot
   * @throws Exception if startup fails
   * @throws UnsupportedOperationException if neither prepared-configuration callback is implemented
   */
  public default void start(IRepository[] repositories, XMLConfiguration configuration) throws Exception
  {
    // Ordinary extensions inherit this delegation; repository-aware extensions can override it.
    start(configuration);
  }
}
