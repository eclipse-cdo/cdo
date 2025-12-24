/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
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

/**
 * An optional extension of the {@link IAppExtension} interface for {@link #getName() named} app extensions.
 *
 * @author Eike Stepper
 * @since 4.18
 */
public interface IAppExtension5 extends IAppExtension
{
  public String getName();

  public boolean startBeforeRepositories();
}
