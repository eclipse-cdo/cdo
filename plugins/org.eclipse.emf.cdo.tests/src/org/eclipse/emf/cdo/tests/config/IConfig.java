/*
 * Copyright (c) 2008, 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

  public static final String EFFORT_MERGING = "___MERGING___";

  public String getName();

  public Map<String, Object> getTestProperties();

  public Object getTestProperty(String key);

  public boolean isValid(Set<IConfig> configs);

  public void initCapabilities(Set<String> capabilities);
}
