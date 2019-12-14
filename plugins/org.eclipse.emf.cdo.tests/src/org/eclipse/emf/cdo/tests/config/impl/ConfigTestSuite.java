/*
 * Copyright (c) 2008, 2011-2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTestSuite implements IConstants
{
  private static final boolean DISABLE_MAIN_SUITE_FINISHED = OMPlatform.INSTANCE.isProperty("disable.main.suite.finished");

  private final List<IScenario> scenarios = new ArrayList<>();

  public ConfigTestSuite()
  {
  }

  public Test getTestSuite()
  {
    return getTestSuite(getClass().getName());
  }

  public Test getTestSuite(String name)
  {
    TestSuite suite = new MainSuite(name);
    initConfigSuites(suite);
    return suite;
  }

  public void addScenario(TestSuite parent, RepositoryConfig repositoryConfig, SessionConfig sessionConfig, ModelConfig modelConfig)
  {
    IScenario scenario = new Scenario();
    scenario.setRepositoryConfig(repositoryConfig);
    scenario.setSessionConfig(sessionConfig);
    scenario.setModelConfig(modelConfig);

    if (scenario.isValid())
    {
      TestSuite scenarioSuite = new TestSuite(scenario.toString());

      List<Class<? extends ConfigTest>> testClasses = new ArrayList<>();
      initTestClasses(testClasses, scenario);

      for (Class<? extends ConfigTest> testClass : testClasses)
      {
        try
        {
          TestClassWrapper testClassWrapper = new TestClassWrapper(testClass, scenario, this);
          if (testClassWrapper.testCount() != 0)
          {
            scenarioSuite.addTest(testClassWrapper);
          }
        }
        catch (ConstraintsViolatedException ex)
        {
          //$FALL-THROUGH$
        }
      }

      parent.addTest(scenarioSuite);
      scenarios.add(scenario);
    }
  }

  protected List<Class<? extends ConfigTest>> getTestClasses(OMBundle bundle, String packageName)
  {
    List<Class<? extends ConfigTest>> result = new ArrayList<>();

    for (Iterator<Class<?>> it = bundle.getClasses(); it.hasNext();)
    {
      Class<?> c = it.next();
      if (ConfigTest.class.isAssignableFrom(c) && c.getName().startsWith(packageName))
      {
        @SuppressWarnings("unchecked")
        Class<? extends ConfigTest> configTest = (Class<? extends ConfigTest>)c;
        result.add(configTest);
      }
    }

    Collections.sort(result, new Comparator<Class<? extends ConfigTest>>()
    {
      @Override
      public int compare(Class<? extends ConfigTest> c1, Class<? extends ConfigTest> c2)
      {
        return c1.getName().compareTo(c2.getName());
      }
    });

    return result;
  }

  protected abstract void initConfigSuites(TestSuite parent);

  protected abstract void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario);

  /**
   * Can be overridden by subclasses.
   */
  protected void prepareTest(ConfigTest configTest)
  {
  }

  protected void mainSuiteFinished()
  {
    if (!DISABLE_MAIN_SUITE_FINISHED)
    {
      for (IScenario scenario : scenarios)
      {
        try
        {
          scenario.mainSuiteFinished();
        }
        catch (Exception ex)
        {
          IOUtil.print(ex);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class MainSuite extends TestSuite
  {
    public MainSuite(String name)
    {
      super(name);
    }

    @Override
    public void run(TestResult result)
    {
      super.run(result);
      mainSuiteFinished();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestClassWrapper extends TestSuite
  {
    private IScenario scenario;

    public TestClassWrapper(Class<? extends ConfigTest> testClass, IScenario scenario, ConfigTestSuite suite) throws ConstraintsViolatedException
    {
      // super(testClass, testClass.getName()); // Important for the UI to set the *qualified* class name!
      this.scenario = scenario;

      List<Test> tests = new ArrayList<>();
      addTestsFromTestCase(testClass, suite, tests);

      Collections.sort(tests, new Comparator<Test>()
      {
        @Override
        public int compare(Test t1, Test t2)
        {
          String n1 = getName(t1);
          String n2 = getName(t2);
          return n1.compareTo(n2);
        }

        private String getName(Test test)
        {
          if (test instanceof TestCase)
          {
            return ((TestCase)test).getName();
          }

          if (test instanceof TestSuite)
          {
            return ((TestSuite)test).getName();
          }

          return "";
        }
      });

      for (Test test : tests)
      {
        // System.err.println(test);
        addTest(test);
      }
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

    private void addTestsFromTestCase(final Class<?> theClass, ConfigTestSuite suite, List<Test> tests) throws ConstraintsViolatedException
    {
      setName(theClass.getName());

      try
      {
        getTestConstructor(theClass); // Avoid generating multiple error messages
      }
      catch (NoSuchMethodException e)
      {
        tests.add(warning("Class " + theClass.getName() + " has no public constructor TestCase(String name) or TestCase()"));
        return;
      }

      if (!Modifier.isPublic(theClass.getModifiers()))
      {
        tests.add(warning("Class " + theClass.getName() + " is not public"));
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

      List<String> names = new ArrayList<>();

      superClass = theClass;
      while (Test.class.isAssignableFrom(superClass))
      {
        for (Method method : superClass.getDeclaredMethods())
        {
          if (validateConstraints(method, capabilities))
          {
            addTestMethod(method, names, theClass, suite, tests);
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

    private void addTestMethod(Method m, List<String> names, Class<?> theClass, ConfigTestSuite suite, List<Test> tests)
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
          tests.add(warning("Test method isn't public: " + name + "(" + theClass.getCanonicalName() + ")"));
        }

        return;
      }

      names.add(name);
      Test test = createTest(theClass, name);
      if (test instanceof ConfigTest)
      {
        ConfigTest configTest = (ConfigTest)test;
        suite.prepareTest(configTest);
      }

      tests.add(test);
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

  /**
   * @author Eike Stepper
   */
  private static final class ConstraintsViolatedException extends Exception
  {
    private static final long serialVersionUID = 1L;
  }
}
