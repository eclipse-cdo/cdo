/*
 * Copyright (c) 2008, 2011-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IConfig extends ITestLifecycle, Serializable
{
  public static final String CAPABILITY_ALL = "___ALL___";

  public static final String CAPABILITY_UNAVAILABLE = "___UNAVAILABLE___";

  public static final String CAPABILITY_SANITIZE_TIMEOUT = "___SANITIZE_TIMEOUT___";

  public String getName();

  public Map<String, Object> getTestProperties();

  public Object getTestProperty(String key);

  public boolean isValid(Set<IConfig> configs);

  public void initCapabilities(Set<String> capabilities);
}
