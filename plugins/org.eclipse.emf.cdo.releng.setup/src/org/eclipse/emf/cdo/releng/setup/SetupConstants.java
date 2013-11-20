/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup;

/**
 * @author Eike Stepper
 */
public interface SetupConstants
{
  public static final String PROP_INSTALL_DIR = "setup.install.dir";

  public static final String PROP_PROJECT_DIR = "setup.project.dir";

  public static final String PROP_BRANCH_DIR = "setup.branch.dir";

  public static final String PROP_ECLIPSE_DIR = "setup.eclipse.dir";

  public static final String PROP_WS_DIR = "setup.ws.dir";

  public static final String PROP_PROJECT_NAME = "setup.project.name";

  public static final String PROP_PROJECT_LABEL = "setup.project.label";

  public static final String PROP_BRANCH_NAME = "setup.branch.name";

  public static final String PROP_BRANCH_LABEL = "setup.branch.label";

  public static final String PROP_OS = "os";

  public static final String PROP_ARCH = "os.arch";

  public static final String PROP_WS = "ws";

  public static final String PROP_RELENG_URL = "releng.url";

  public static final String RELENG_URL = System.getProperty(PROP_RELENG_URL,
      "http://download.eclipse.org/modeling/emf/cdo/updates/integration").replace('\\', '/');
}
