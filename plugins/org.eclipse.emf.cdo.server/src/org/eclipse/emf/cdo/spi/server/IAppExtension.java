/*
 * Copyright (c) 2009, 2011, 2012, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import java.io.File;

/**
 * Lifecycle hook for server application extensions loaded from an application configuration file. The server starts
 * the extension with that file and later calls it to release any resources it acquired.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public interface IAppExtension
{
  public static final String EXT_POINT = "appExtensions"; //$NON-NLS-1$

  public void start(File configFile) throws Exception;

  public void stop() throws Exception;
}
