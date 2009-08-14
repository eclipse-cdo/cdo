/**
 * Copyright (c) 2004 - 2009 Andre Dietisheim (Bern, Switzerland) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * A TestSuite that runs tests in a concurrent manner. Normal Thests are not suited to be run with this test suite as
 * they rely on the sequential calls of each test case (the last test is teared down before the next one is run). In
 * other words tests that shall be run in this suite shall be written to be run concurrently
 * 
 * @author Andre Dietisheim
 */
public class ConcurrentTestSuite extends TestSuite
{
  private static final int MAX_THREADS = 20;

  private ExecutorService executorService;

  private ArrayList<Future<Object>> futureList;

  public ConcurrentTestSuite(String string)
  {
    super(string);
    executorService = Executors.newFixedThreadPool(MAX_THREADS);
    futureList = new ArrayList<Future<Object>>();
  }

  @Override
  public void runTest(Test test, TestResult result)
  {
    test.run(result);
    waitForTests();
  }

  private void waitForTests()
  {
    for (Future<Object> future : futureList)
    {
      try
      {
        future.get();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  /**
   * Adds the tests from the given class to the suite
   */
  @Override
  public void addTestSuite(Class testClass)
  {
    addTest(new TestSuite(testClass)
    {
      @Override
      public void runTest(final Test test, final TestResult result)
      {
        Callable<Object> callable = new Callable<Object>()
        {
          public Object call() throws Exception
          {
            test.run(result);
            return null;
          }
        };
        futureList.add(executorService.submit(callable));
      }
    });
  }
}
