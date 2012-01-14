/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public interface IContainerConfig extends IConfig
{
  public static final String CAPABILITY_COMBINED = "containers.combined";

  public static final String CAPABILITY_SEPARATED = "containers.separated";

  public boolean hasClientContainer();

  public boolean hasServerContainer();

  public IManagedContainer getClientContainer();

  public IManagedContainer getServerContainer();
}
