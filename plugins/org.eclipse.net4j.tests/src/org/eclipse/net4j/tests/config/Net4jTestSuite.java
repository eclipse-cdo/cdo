/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.config;

import org.eclipse.net4j.tests.config.TestConfig.Factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class Net4jTestSuite extends TestSuite
{
  private final Class<? extends Factory>[] configTypes;

  public Net4jTestSuite(String name, @SuppressWarnings("unchecked") Class<? extends Factory>... configTypes)
  {
    super(name);
    this.configTypes = configTypes;
  }

  @Override
  public void addTestSuite(Class<? extends TestCase> testClass)
  {
    if (AbstractConfigTest.class.isAssignableFrom(testClass))
    {
      Set<Class<? extends Factory>> excluded = getExcludedConfigs(testClass);
      for (Class<? extends Factory> configType : configTypes)
      {
        if (excluded.contains(configType))
        {
          continue;
        }

        Constructor<? extends Factory> constructor;

        try
        {
          constructor = configType.getConstructor();
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          continue;
        }

        TestConfig.Factory factory;

        try
        {
          factory = constructor.newInstance();
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          continue;
        }

        addTest(new ConfigTestSuite(testClass, factory));
      }
    }
    else
    {
      addTest(new TestSuite(testClass));
    }
  }

  public static Set<Class<? extends Factory>> getExcludedConfigs(Class<?> theClass)
  {
    Set<Class<? extends Factory>> result = new HashSet<>();
    Net4jTestSuite.ExcludedConfig[] annotations = theClass.getAnnotationsByType(Net4jTestSuite.ExcludedConfig.class);
    if (annotations.length > 0)
    {
      for (Net4jTestSuite.ExcludedConfig annotation : annotations)
      {
        Class<? extends Factory>[] types = annotation.value();
        for (Class<? extends Factory> type : types)
        {
          result.add(type);
        }
      }
    }

    return result;
  }

  /**
   * @author Eike Stepper
   */
  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface ExcludedConfig
  {
    Class<? extends TestConfig.Factory>[] value();
  }

  /**
   * @author Eike Stepper
   */
  public static class ConfigTestSuite extends TestSuite
  {
    private final TestConfig.Factory configFactory;

    public ConfigTestSuite(Class<?> theClass, TestConfig.Factory configFactory)
    {
      super(theClass);
      this.configFactory = configFactory;
      setName(theClass.getName() + " [" + configFactory.getClass().getSimpleName() + "]");
    }

    @Override
    public void runTest(Test test, TestResult result)
    {
      if (test instanceof AbstractConfigTest)
      {
        ((AbstractConfigTest)test).setConfig(configFactory.createConfig());
      }

      super.runTest(test, result);
    }
  }
}
