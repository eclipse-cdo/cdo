/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.server.IRepository;

import java.io.File;

/**
 * An optional extension of the {@link IAppExtension} interface for {@link IRepository repository-aware} app extensions.
 *
 * @author Eike Stepper
 * @since 4.10
 */
public interface IAppExtension3 extends IAppExtension
{
  public void start(IRepository[] repositories, File configFile) throws Exception;

  public void stop(IRepository[] repositories) throws Exception;
}
