/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTestSuite implements IConstants
{
  public ConfigTestSuite()
  {
  }

  protected Test getTestSuite(String name)
  {
    TestSuite suite = new TestSuite(name);
    initConfigSuites(suite);
    return suite;
  }

  public void addScenario(TestSuite parent, ContainerConfig containerConfig, RepositoryConfig repositoryConfig,
      SessionConfig sessionConfig, ModelConfig modelConfig)
  {
    IScenario scenario = new Scenario();
    scenario.setContainerConfig(containerConfig);
    scenario.setRepositoryConfig(repositoryConfig);
    scenario.setSessionConfig(sessionConfig);
    scenario.setModelConfig(modelConfig);

    if (scenario.isValid())
    {
      TestSuite suite = new TestSuite(scenario.toString());

      List<Class<? extends ConfigTest>> testClasses = new ArrayList<Class<? extends ConfigTest>>();
      initTestClasses(testClasses, scenario);

      for (Class<? extends ConfigTest> testClass : testClasses)
      {
        try
        {
          TestWrapper wrapper = new TestWrapper(testClass, scenario);
          if (wrapper.testCount() != 0)
          {
            suite.addTest(wrapper);
          }
        }
        catch (ConstraintsViolatedException ex)
        {
          //$FALL-THROUGH$
        }
      }

      parent.addTest(suite);
    }
  }

  protected abstract void initConfigSuites(TestSuite parent);

  protected abstract void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario);

  /**
   * @author Eike Stepper
   */
  private static final class ConstraintsViolatedException extends Exception
  {
    private static final long serialVersionUID = 1L;
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestWrapper extends TestSuite
  {
    private IScenario scenario;

    public TestWrapper(Class<? extends ConfigTest> testClass, IScenario scenario) throws ConstraintsViolatedException
    {
      // super(testClass, testClass.getName()); // Important for the UI to set the *qualified* class name!
      this.scenario = scenario;
      addTestsFromTestCase(testClass);
    }

    @Override
    public void runTest(Test test, TestResult result)
    {
      if (test instanceof ConfigTest)
      {
        scenario.save();

        ConfigTest configTest = (ConfigTest)test;
        configTest.setScenario(scenario);

        if (configTest.isValid())
        {
          super.runTest(configTest, result);
        }
      }
      else
      {
        super.runTest(test, result);
      }
    }

    private void addTestsFromTestCase(final Class<?> theClass) throws ConstraintsViolatedException
    {
      setName(theClass.getName());

      try
      {
        getTestConstructor(theClass); // Avoid generating multiple error messages
      }
      catch (NoSuchMethodException e)
      {
        addTest(warning("Class " + theClass.getName()
            + " has no public constructor TestCase(String name) or TestCase()"));
        return;
      }

      if (!Modifier.isPublic(theClass.getModifiers()))
      {
        addTest(warning("Class " + theClass.getName() + " is not public"));
        return;
      }

      Set<String> capabilities = scenario.getCapabilities();

      Class<?> superClass = theClass;
      while (Test.class.isAssignableFrom(superClass))
      {
        if (!validateConstraints(superClass, capabilities))
        {
          throw new ConstraintsViolatedException();
        }

        superClass = superClass.getSuperclass();
      }

      List<String> names = new ArrayList<String>();

      superClass = theClass;
      while (Test.class.isAssignableFrom(superClass))
      {
        for (Method method : superClass.getDeclaredMethods())
        {
          if (validateConstraints(method, capabilities))
          {
            addTestMethod(method, names, theClass);
          }
        }

        superClass = superClass.getSuperclass();
      }
    }

    private boolean validateConstraints(AnnotatedElement element, Set<String> capabilities)
    {
      Requires requires = element.getAnnotation(Requires.class);
      if (requires != null)
      {
        for (String require : requires.value())
        {
          if (!capabilities.contains(require))
          {
            return false;
          }
        }
      }

      Skips skips = element.getAnnotation(Skips.class);
      if (skips != null)
      {
        for (String skip : skips.value())
        {
          if (capabilities.contains(skip))
          {
            return false;
          }
        }
      }

      return true;
    }

    private void addTestMethod(Method m, List<String> names, Class<?> theClass)
    {
      String name = m.getName();
      if (names.contains(name))
      {
        return;
      }

      if (!isPublicTestMethod(m))
      {
        if (isTestMethod(m))
        {
          addTest(warning("Test method isn't public: " + m.getName() + "(" + theClass.getCanonicalName() + ")"));
        }

        return;
      }

      names.add(name);
      addTest(createTest(theClass, name));
    }

    private boolean isPublicTestMethod(Method m)
    {
      return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
    }

    private boolean isTestMethod(Method m)
    {
      return m.getParameterTypes().length == 0 && m.getName().startsWith("test") && m.getReturnType().equals(Void.TYPE);
    }
  }
}
