/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.testbed;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface RepositoryProvider
{
  public static final String REPOSITORY_NAME = "repo1";

  public Map<String, String> getRepositoryProperties();

  public IRepository getRepository();

  public IStore getStore();
}
