/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.internal.server.bundle.CDOStandaloneServerApplication;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Verifies the shared CDO server lifecycle ordering and failed-start cleanup without starting a repository.
 *
 * @author Eike Stepper
 */
public class CDOServerApplicationLifecycleTest extends PlainTest
{
  public void testStartupAndShutdownOrdering() throws Exception
  {
    TestApplication application = new TestApplication(false);
    application.activateContainer();

    try
    {
      application.activate();
      application.deactivate();

      assertEquals(Arrays.asList("readExtensions", "startEarlyExtensions", "startRepositories", "startBrowser", "startRegularExtensions",
          "stopRegularExtensions", "stopRepositories", "stopEarlyExtensions", "deactivateContainer"), application.events);
    }
    finally
    {
      application.deactivate();
    }
  }

  public void testStartupFailureCleansUpStartedResources() throws Exception
  {
    TestApplication application = new TestApplication(true);
    application.activateContainer();

    try
    {
      try
      {
        application.activate();
        fail("Expected startup to fail"); //$NON-NLS-1$
      }
      catch (RuntimeException expected)
      {
        assertEquals("Expected browser startup failure", expected.getMessage()); //$NON-NLS-1$
      }

      assertEquals(Arrays.asList("readExtensions", "startEarlyExtensions", "startRepositories", "startBrowser", "stopRegularExtensions", "stopRepositories",
          "stopEarlyExtensions", "deactivateContainer"), application.events);
      assertFalse(application.isActive());
    }
    finally
    {
      application.deactivate();
    }
  }

  public void testMissingDefaultConfigurationFailsActivation() throws Exception
  {
    File configDirectory = Files.createTempDirectory("cdo-server-default-config").toFile(); //$NON-NLS-1$
    File missingConfigFile = new File(configDirectory, "cdo-server.xml"); //$NON-NLS-1$
    configDirectory.deleteOnExit();
    TestApplication application = new TestApplication(false, missingConfigFile);
    application.activateContainer();

    try
    {
      assertConfigurationFailure(application, missingConfigFile);
    }
    finally
    {
      application.deactivateContainer();
    }
  }

  public void testExplicitlyConfiguredMissingConfigurationFailsActivation() throws Exception
  {
    File configDirectory = Files.createTempDirectory("cdo-server-explicit-config").toFile(); //$NON-NLS-1$
    File missingConfigFile = new File(configDirectory, "custom-server.xml"); //$NON-NLS-1$
    configDirectory.deleteOnExit();
    TestApplication application = new TestApplication(false, missingConfigFile);
    application.activateContainer();

    try
    {
      assertConfigurationFailure(application, missingConfigFile);
    }
    finally
    {
      application.deactivateContainer();
    }
  }

  public void testDirectoryIsRejectedAsConfigurationFile() throws Exception
  {
    File directory = Files.createTempDirectory("cdo-server-invalid-config").toFile(); //$NON-NLS-1$
    directory.deleteOnExit();
    TestApplication application = new TestApplication(false, directory);
    application.activateContainer();

    try
    {
      assertConfigurationFailure(application, directory);
    }
    finally
    {
      application.deactivateContainer();
    }
  }

  public void testRunDoesNotWaitAfterConfigurationFailure() throws Exception
  {
    File configDirectory = Files.createTempDirectory("cdo-server-run-config").toFile(); //$NON-NLS-1$
    File missingConfigFile = new File(configDirectory, "cdo-server.xml"); //$NON-NLS-1$
    configDirectory.deleteOnExit();

    TestApplication application = new TestApplication(false, missingConfigFile);
    application.activateContainer();

    AtomicReference<Throwable> runFailure = new AtomicReference<>();
    Thread runThread = new Thread(() -> {
      try
      {
        application.run();
      }
      catch (Throwable ex)
      {
        runFailure.set(ex);
      }
    });
    runThread.start();

    try
    {
      runThread.join(5000);
      assertFalse("Failed application run did not terminate", runThread.isAlive()); //$NON-NLS-1$
      assertNotNull(runFailure.get());
      assertTrue(runFailure.get().getMessage().contains(missingConfigFile.getAbsolutePath()));
      assertFalse(application.isActive());
      assertTrue(application.events.isEmpty());
    }
    finally
    {
      application.deactivateContainer();
    }
  }

  public void testProgrammaticShutdownReleasesWaiterAndIsIdempotent() throws Exception
  {
    TestApplication application = new TestApplication(false);
    application.activateContainer();
    application.activate();

    AtomicReference<Throwable> waiterFailure = new AtomicReference<>();
    Thread waiter = new Thread(() -> {
      try
      {
        application.awaitStopped();
      }
      catch (Throwable ex)
      {
        waiterFailure.set(ex);
      }
    });
    waiter.start();

    application.shutdown();
    application.shutdown();
    waiter.join(5000);

    assertFalse("Waiter did not terminate", waiter.isAlive()); //$NON-NLS-1$
    assertNull(waiterFailure.get());
    assertEquals(1, application.countEvents("stopRegularExtensions")); //$NON-NLS-1$
    assertFalse(application.isActive());
  }

  public void testRetainedApplicationInstanceCanStopRun() throws Exception
  {
    TestApplication application = new TestApplication(false);
    application.activateContainer();

    AtomicReference<Throwable> runFailure = new AtomicReference<>();
    Thread runThread = new Thread(() -> {
      try
      {
        application.run();
      }
      catch (Throwable ex)
      {
        runFailure.set(ex);
      }
    });
    runThread.start();

    try
    {
      assertTrue("Application did not finish startup", application.activationCompleted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      application.shutdown();
      runThread.join(5000);

      assertFalse("Application run did not terminate", runThread.isAlive()); //$NON-NLS-1$
      assertNull(runFailure.get());
      assertEquals(1, application.countEvents("stopRegularExtensions")); //$NON-NLS-1$
      assertFalse(application.isActive());
    }
    finally
    {
      application.shutdown();
      runThread.join(5000);
    }
  }

  public void testDirectDeactivationReleasesWaiter() throws Exception
  {
    TestApplication application = new TestApplication(false);
    application.activateContainer();
    application.activate();

    CountDownLatch waiterReturned = new CountDownLatch(1);
    Thread waiter = new Thread(() -> {
      try
      {
        application.awaitStopped();
        waiterReturned.countDown();
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
      }
    });
    waiter.start();

    Exception exception = application.deactivate();

    assertNull(exception);
    assertTrue("Waiter did not receive deactivation", waiterReturned.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    waiter.join(5000);
    assertFalse(waiter.isAlive());
  }

  public void testStopCommandTriggersOrderlyShutdown() throws Exception
  {
    TestApplication application = new TestApplication(false);
    application.activateContainer();
    application.activate();

    application.processInput(new StringReader("  StOp  \n")); //$NON-NLS-1$

    assertFalse(application.isActive());
    assertEquals(1, application.countEvents("stopRegularExtensions")); //$NON-NLS-1$
    application.awaitStopped();
  }

  public void testEndOfInputDoesNotShutDownTheServer() throws Exception
  {
    TestApplication application = new TestApplication(false);
    application.activateContainer();
    application.activate();

    try
    {
      application.processInput(new StringReader("status\n")); //$NON-NLS-1$
      assertTrue(application.isActive());
      assertEquals(0, application.countEvents("stopRegularExtensions")); //$NON-NLS-1$
    }
    finally
    {
      application.deactivate();
    }
  }

  public void testConcurrentShutdownRequestsRunCleanupOnce() throws Exception
  {
    TestApplication application = new TestApplication(false);
    application.activateContainer();
    application.activate();

    CountDownLatch start = new CountDownLatch(1);
    AtomicReference<Throwable> shutdownFailure = new AtomicReference<>();
    Thread first = createShutdownThread(application, start, shutdownFailure);
    Thread second = createShutdownThread(application, start, shutdownFailure);

    first.start();
    second.start();
    start.countDown();

    first.join(5000);
    second.join(5000);

    assertFalse("First shutdown thread did not terminate", first.isAlive()); //$NON-NLS-1$
    assertFalse("Second shutdown thread did not terminate", second.isAlive()); //$NON-NLS-1$
    assertNull(shutdownFailure.get());
    assertEquals(1, application.countEvents("stopRegularExtensions")); //$NON-NLS-1$
  }

  public void testShutdownDuringStartupWaitsForActivationThenCleansUp() throws Exception
  {
    CountDownLatch browserStarted = new CountDownLatch(1);
    CountDownLatch continueStartup = new CountDownLatch(1);

    TestApplication application = new TestApplication(false, browserStarted, continueStartup);
    application.activateContainer();

    AtomicReference<Throwable> activationFailure = new AtomicReference<>();
    Thread activation = new Thread(() -> {
      try
      {
        application.activate();
      }
      catch (Throwable ex)
      {
        activationFailure.set(ex);
      }
    });
    activation.start();
    assertTrue("Startup did not reach the browser phase", browserStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

    Thread shutdown = new Thread(application::shutdown);
    shutdown.start();
    continueStartup.countDown();
    activation.join(5000);
    shutdown.join(5000);

    assertFalse("Activation thread did not terminate", activation.isAlive()); //$NON-NLS-1$
    assertFalse("Shutdown thread did not terminate", shutdown.isAlive()); //$NON-NLS-1$
    assertNull(activationFailure.get());
    assertFalse(application.isActive());
    assertEquals(1, application.countEvents("stopRegularExtensions")); //$NON-NLS-1$
  }

  public void testFailedActivationReleasesTerminationWaiter() throws Exception
  {
    CountDownLatch browserStarted = new CountDownLatch(1);
    CountDownLatch continueStartup = new CountDownLatch(1);

    TestApplication application = new TestApplication(true, browserStarted, continueStartup);
    application.activateContainer();

    CountDownLatch waiterReturned = new CountDownLatch(1);
    Thread waiter = new Thread(() -> {
      try
      {
        application.awaitStopped();
        waiterReturned.countDown();
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
      }
    });
    waiter.start();

    AtomicReference<Throwable> activationFailure = new AtomicReference<>();
    Thread activation = new Thread(() -> {
      try
      {
        application.activate();
      }
      catch (Throwable ex)
      {
        activationFailure.set(ex);
      }
    });
    activation.start();
    assertTrue("Startup did not reach the browser phase", browserStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    Thread shutdown = new Thread(application::shutdown);
    shutdown.start();
    continueStartup.countDown();

    activation.join(5000);
    shutdown.join(5000);

    assertFalse("Activation thread did not terminate", activation.isAlive()); //$NON-NLS-1$
    assertFalse("Shutdown thread did not terminate", shutdown.isAlive()); //$NON-NLS-1$
    assertTrue(activationFailure.get() instanceof RuntimeException);
    assertEquals("Expected browser startup failure", activationFailure.get().getMessage()); //$NON-NLS-1$
    assertTrue("Waiter did not receive activation cleanup", waiterReturned.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    waiter.join(5000);
    assertFalse(waiter.isAlive());
  }

  private Thread createShutdownThread(TestApplication application, CountDownLatch start, AtomicReference<Throwable> failure)
  {
    return new Thread(() -> {
      try
      {
        start.await();
        application.shutdown();
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
    });
  }

  private void assertConfigurationFailure(TestApplication application, File configFile)
  {
    try
    {
      application.activate();
      fail("Expected configuration validation to fail"); //$NON-NLS-1$
    }
    catch (RuntimeException ex)
    {
      assertTrue(ex.getMessage().contains(configFile.getAbsolutePath()));
      assertTrue(ex.getMessage().contains("-Dnet4j.config")); //$NON-NLS-1$
      assertTrue(ex.getMessage().contains("-Dorg.eclipse.emf.cdo.server.configFileName")); //$NON-NLS-1$
    }

    assertFalse(application.isActive());
    assertTrue(application.events.isEmpty());
  }

  /**
   * Exercises lifecycle hooks with recorded events instead of a live server configuration.
   *
   * @author Eike Stepper
   */
  private static final class TestApplication extends CDOStandaloneServerApplication
  {
    private final List<String> events = new ArrayList<>();

    private final boolean failStartingBrowser;

    private final File configFile;

    private final CountDownLatch browserStarted;

    private final CountDownLatch continueStartup;

    private final CountDownLatch activationCompleted = new CountDownLatch(1);

    private final TestContainer container = new TestContainer(events);

    private int startExtensionGroup;

    private int stopExtensionGroup;

    public TestApplication(boolean failStartingBrowser) throws IOException
    {
      this(failStartingBrowser, null, null, createConfigFile());
    }

    public TestApplication(boolean failStartingBrowser, File configFile)
    {
      this(failStartingBrowser, null, null, configFile);
    }

    public TestApplication(boolean failStartingBrowser, CountDownLatch browserStarted, CountDownLatch continueStartup) throws IOException
    {
      this(failStartingBrowser, browserStarted, continueStartup, createConfigFile());
    }

    private TestApplication(boolean failStartingBrowser, CountDownLatch browserStarted, CountDownLatch continueStartup, File configFile)
    {
      this.failStartingBrowser = failStartingBrowser;
      this.browserStarted = browserStarted;
      this.continueStartup = continueStartup;
      this.configFile = configFile;
    }

    public void activateContainer() throws LifecycleException
    {
      container.activate();
    }

    public void deactivateContainer()
    {
      LifecycleUtil.deactivate(container);
    }

    public void processInput(Reader reader)
    {
      processConsoleInput(reader);
    }

    public void awaitStopped() throws InterruptedException
    {
      awaitTermination();
    }

    public int countEvents(String event)
    {
      return (int)events.stream().filter(event::equals).count();
    }

    @Override
    protected void doAfterActivate() throws Exception
    {
      super.doAfterActivate();
      activationCompleted.countDown();
    }

    @Override
    protected File getConfigFile()
    {
      return configFile;
    }

    @Override
    protected IManagedContainer getApplicationContainer()
    {
      return container;
    }

    @Override
    protected void readExtensions(IManagedContainer container)
    {
      events.add("readExtensions"); //$NON-NLS-1$
    }

    @Override
    protected void startExtensions(File configFile, List<IAppExtension> extensions)
    {
      events.add(startExtensionGroup++ == 0 ? "startEarlyExtensions" : "startRegularExtensions"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void startRepositories(File configFile, IManagedContainer container)
    {
      events.add("startRepositories"); //$NON-NLS-1$
    }

    @Override
    protected void startBrowser(IManagedContainer container)
    {
      events.add("startBrowser"); //$NON-NLS-1$
      if (browserStarted != null)
      {
        browserStarted.countDown();

        try
        {
          continueStartup.await();
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new RuntimeException(ex);
        }
      }

      if (failStartingBrowser)
      {
        throw new IllegalStateException("Expected browser startup failure"); //$NON-NLS-1$
      }
    }

    @Override
    protected void startConsoleInput()
    {
      // The tests supply command readers directly and do not create a thread blocked on System.in.
    }

    @Override
    protected void stopExtensions(List<IAppExtension> extensions)
    {
      events.add(stopExtensionGroup++ == 0 ? "stopRegularExtensions" : "stopEarlyExtensions"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void stopRepositories()
    {
      events.add("stopRepositories"); //$NON-NLS-1$
    }

    private static File createConfigFile() throws IOException
    {
      File configFile = File.createTempFile("cdo-server-config", ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
      configFile.deleteOnExit();
      return configFile;
    }
  }

  /**
   * Records deactivation while retaining normal managed-container cleanup.
   *
   * @author Eike Stepper
   */
  private static final class TestContainer extends ManagedContainer
  {
    public final List<String> events;

    public TestContainer(List<String> events)
    {
      this.events = events;
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      events.add("deactivateContainer"); //$NON-NLS-1$
      super.doDeactivate();
    }
  }
}
