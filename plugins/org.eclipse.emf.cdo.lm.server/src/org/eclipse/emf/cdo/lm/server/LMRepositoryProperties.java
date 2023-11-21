/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.server;

/**
 * @author Eike Stepper
 * @since 1.3
 */
public interface LMRepositoryProperties
{
  public static final String LIFECYCLE_MANAGER = "cdo.lm.server.lifecycle_manager";

  public static final String REPOSITORY_TYPE = "cdo.lm.server.repository_type";

  public static final String REPOSITORY_TYPE_SYSTEM = "system";

  public static final String REPOSITORY_TYPE_MODULE = "module";

  public static final String SYSTEM_NAME = "cdo.lm.server.system_name";

  public static final String MODULE_NAME = "cdo.lm.server.module_name";

  public static final String MODULE_TYPE_NAME = "cdo.lm.server.module_type_name";
}
