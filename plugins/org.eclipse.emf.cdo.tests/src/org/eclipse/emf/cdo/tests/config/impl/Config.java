/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.tests.config.IConfig;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.ThreadPool;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public abstract class Config implements IConfig
{
  private static final long serialVersionUID = 1L;

  protected static final int MAX_THREADS_PER_POOL = 10000;

  protected static ExecutorService executorService = ThreadPool.create("test", 10, MAX_THREADS_PER_POOL, 10);

  private String name;

  private transient ConfigTest currentTest;

  public Config(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    return getName();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof IConfig)
    {
      IConfig that = (IConfig)obj;
      return ObjectUtil.equals(name, that.getName());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(name);
  }

  public ConfigTest getCurrentTest()
  {
    return currentTest;
  }

  public void setCurrentTest(ConfigTest currentTest)
  {
    this.currentTest = currentTest;
  }

  public Map<String, Object> getTestProperties()
  {
    return currentTest.getTestProperties();
  }

  public Object getTestProperty(String key)
  {
    Map<String, Object> testProperties = getTestProperties();
    if (testProperties != null)
    {
      return testProperties.get(key);
    }

    return null;
  }

  public boolean isValid(Set<IConfig> configs)
  {
    return true;
  }

  public void setUp() throws Exception
  {
    if (currentTest == null)
    {
      throw new IllegalStateException("currentTest == null");
    }
  }

  public void tearDown() throws Exception
  {
  }

  public void mainSuiteFinished() throws Exception
  {
  }

  public static ExecutorService getExecutorService()
  {
    return executorService;
  }
}
