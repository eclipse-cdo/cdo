/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.server.spi.security.HomeFolderHandler;

import org.eclipse.net4j.internal.util.concurrent.IExecutorServiceProvider;

/**
 * @author Eike Stepper
 */
public class HomeFolderHandler2 extends HomeFolderHandler implements IExecutorServiceProvider
{
  public HomeFolderHandler2()
  {
  }

  public HomeFolderHandler2(String homeFolder)
  {
    super(homeFolder);
  }
}
