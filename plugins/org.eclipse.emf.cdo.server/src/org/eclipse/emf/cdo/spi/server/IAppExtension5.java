/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
