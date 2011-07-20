/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IRepositoryConfig extends IConfig
{
  public static final String REPOSITORY_NAME = "repo1";

  public Map<String, String> getRepositoryProperties();

  public InternalRepository getRepository(String name, boolean activate);

  public InternalRepository getRepository(String name);

  public void registerRepository(InternalRepository repository);

  public void setRestarting(boolean b);

  public IStore createStore(String repoName);
}
