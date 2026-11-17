/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.internal.cdo.util.ExtensionRegistryBootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;

/**
 * Runs the shared CDO server lifecycle as a regular Java application.
 *
 * @author Eike Stepper
 */
public class CDOStandaloneServerApplication extends AbstractCDOServerApplication
{
  private final CountDownLatch startupCompleted = new CountDownLatch(1);

  private final CountDownLatch stopped = new CountDownLatch(1);

  private final Object consoleLock = new Object();

  private volatile boolean shutdownRequested;

  private volatile boolean activationStarted;

  private volatile boolean startedSuccessfully;

  private boolean runStarted;

  private boolean consoleInputStarted;

  public CDOStandaloneServerApplication()
  {
  }

  /**
   * Activates the server and waits until console input, programmatic shutdown, or JVM shutdown completes its lifecycle.
   * The standalone server requires a readable regular configuration file resolved through {@link #getConfigFile()}.
   *
   * @throws Exception if startup fails or the waiting thread is interrupted
   */
  public void run() throws Exception
  {
    synchronized (consoleLock)
    {
      if (runStarted)
      {
        throw new IllegalStateException("This standalone server application has already been run"); //$NON-NLS-1$
      }

      runStarted = true;
    }

    ExtensionRegistryBootstrap.init();

    Thread shutdownHook = new Thread(this::shutdown, "CDO server shutdown"); //$NON-NLS-1$
    Runtime.getRuntime().addShutdownHook(shutdownHook);

    try
    {
      activate();
    }
    catch (Exception | Error ex)
    {
      startupCompleted.countDown();
      removeShutdownHook(shutdownHook);

      if (!(ex instanceof ConfigurationException))
      {
        System.err.println("Could not start the CDO server."); //$NON-NLS-1$
        ex.printStackTrace();
      }

      throw ex;
    }

    if (shutdownRequested)
    {
      shutdown();
    }
    else
    {
      System.out.println();
      System.out.println("Type 'stop' and press Enter to shut down the server."); //$NON-NLS-1$
      System.out.println("In a terminal, Ctrl+C is also supported."); //$NON-NLS-1$
      startConsoleInput();
    }

    try
    {
      awaitTermination();
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      shutdown();
      throw new RuntimeException("Interrupted while waiting for CDO server shutdown", ex); //$NON-NLS-1$
    }
    finally
    {
      removeShutdownHook(shutdownHook);
    }
  }

  /**
   * Requests orderly shutdown. If activation is in progress, the request waits for activation to settle before
   * deactivating an active server. Repeated or concurrent calls are safe; the lifecycle performs cleanup once and
   * signals termination after cleanup completes.
   */
  public void shutdown()
  {
    synchronized (consoleLock)
    {
      shutdownRequested = true;
    }

    if (!activationStarted)
    {
      startupCompleted.countDown();
      stopped.countDown();
      return;
    }

    Exception exception = deactivate();
    if (exception != null)
    {
      reportShutdownFailure(exception);
      return;
    }

    try
    {
      startupCompleted.await();
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      reportShutdownFailure(ex);
      return;
    }

    if (isActive())
    {
      exception = deactivate();
      if (exception != null)
      {
        reportShutdownFailure(exception);
      }
    }
  }

  /**
   * Waits until lifecycle cleanup has completed.
   *
   * @throws InterruptedException if the waiting thread is interrupted
   */
  protected final void awaitTermination() throws InterruptedException
  {
    stopped.await();
  }

  /**
   * Starts one daemon thread that accepts the {@code stop} command from standard input.
   * The command thread is optional and never controls the application's termination wait.
   */
  protected void startConsoleInput()
  {
    synchronized (consoleLock)
    {
      if (consoleInputStarted || shutdownRequested || !isActive())
      {
        return;
      }

      InputStream input = System.in;
      if (input == null)
      {
        return;
      }

      consoleInputStarted = true;
      Thread inputThread = new Thread(() -> processConsoleInput(new InputStreamReader(input)), "CDO server console input"); //$NON-NLS-1$
      inputThread.setDaemon(true);

      try
      {
        inputThread.start();
      }
      catch (SecurityException ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * Reads console commands until EOF, interruption, or a {@code stop} command.
   * Only the stop command initiates deactivation; input failures are logged and leave the server running.
   *
   * @param input the reader used to supply command lines; the reader is not closed
   */
  protected final void processConsoleInput(Reader input)
  {
    try
    {
      BufferedReader reader = input instanceof BufferedReader ? (BufferedReader)input : new BufferedReader(input);
      String line;
      while (!Thread.currentThread().isInterrupted() && (line = reader.readLine()) != null)
      {
        if ("stop".equalsIgnoreCase(line.trim())) //$NON-NLS-1$
        {
          shutdown();
          return;
        }
      }
    }
    catch (IOException ex)
    {
      OM.LOG.error(ex);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    startedSuccessfully = false;

    try
    {
      super.doActivate();
    }
    catch (Exception | Error ex)
    {
      startupCompleted.countDown();
      throw ex;
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    activationStarted = true;

    if (shutdownRequested)
    {
      startupCompleted.countDown();
      stopped.countDown();
      throw new IllegalStateException("CDO server shutdown was requested before startup"); //$NON-NLS-1$
    }

    try
    {
      validateConfigurationFile();
      super.doBeforeActivate();
    }
    catch (Exception | Error ex)
    {
      startupCompleted.countDown();
      stopped.countDown();
      throw ex;
    }
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();
    startedSuccessfully = true;
    startupCompleted.countDown();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    boolean reportLifecycle = startedSuccessfully;

    try
    {
      super.doDeactivate();
    }
    catch (Exception | Error ex)
    {
      if (reportLifecycle)
      {
        reportShutdownFailure(ex);
      }

      throw ex;
    }
    finally
    {
      stopped.countDown();
    }
  }

  private static void removeShutdownHook(Thread shutdownHook)
  {
    try
    {
      Runtime.getRuntime().removeShutdownHook(shutdownHook);
    }
    catch (IllegalStateException ignore)
    {
      // JVM shutdown has already started, so the hook cannot be removed.
    }
  }

  private static void reportShutdownFailure(Throwable failure)
  {
    System.err.println("CDO server shutdown failed."); //$NON-NLS-1$
    failure.printStackTrace();
  }

  private void validateConfigurationFile() throws ConfigurationException
  {
    File configFile = getConfigFile();
    String path = configFile == null ? "<unresolved>" : configFile.getAbsolutePath(); //$NON-NLS-1$
    String message = "Standalone CDO server requires an existing, readable regular configuration file: " + path //$NON-NLS-1$
        + ". Set -Dnet4j.config to select the configuration directory and " //$NON-NLS-1$
        + "-Dorg.eclipse.emf.cdo.server.configFileName to select the configuration filename " //$NON-NLS-1$
        + "(defaults: config and cdo-server.xml)."; //$NON-NLS-1$

    if (configFile == null || !Files.isRegularFile(configFile.toPath()) || !Files.isReadable(configFile.toPath()))
    {
      throw new ConfigurationException(message);
    }

    try (InputStream ignored = Files.newInputStream(configFile.toPath()))
    {
      // Opening the file verifies actual read access without rejecting a valid empty configuration.
    }
    catch (IOException ex)
    {
      throw new ConfigurationException(message, ex);
    }
  }

  /**
   * Describes a standalone configuration file that cannot be opened before server startup.
   *
   * @author Eike Stepper
   */
  private static final class ConfigurationException extends IllegalStateException
  {
    private static final long serialVersionUID = 1L;

    ConfigurationException(String message)
    {
      super(message);
    }

    ConfigurationException(String message, Throwable cause)
    {
      super(message, cause);
    }
  }
}
