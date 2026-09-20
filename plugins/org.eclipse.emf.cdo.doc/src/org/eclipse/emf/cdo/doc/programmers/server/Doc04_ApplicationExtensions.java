/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.spi.server.IAppExtension2;
import org.eclipse.emf.cdo.spi.server.IAppExtension3;
import org.eclipse.emf.cdo.spi.server.IAppExtension4;
import org.eclipse.emf.cdo.spi.server.IAppExtension5;

import org.eclipse.net4j.util.event.IListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Application Extensions
 * <p>
 * An {@link IAppExtension application extension} is a supported SPI for application-specific server startup work. It
 * is contributed to the server's {@value IAppExtension#EXT_POINT} extension point. Use it to wire application
 * services, install repository handlers or listeners, and remove those contributions at shutdown.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc04_ApplicationExtensions
{
  /**
   * Choosing an Extension Variant
   * <p>
   * {@link IAppExtension} receives the configuration file in {@link IAppExtension#start(File)} and has a matching
   * {@link IAppExtension#stop()} callback. {@link IAppExtension2} additionally supports a reader for each dynamically
   * managed repository configuration; instances can be started more than once and stop when their repository is
   * deleted. Prefer {@link IAppExtension3} when startup requires the configured {@link IRepository repositories}.
   * <p>
   * Implement {@link IAppExtension4} to set a priority, and {@link IAppExtension5} to provide a name and choose the
   * pre-repository phase. These are orthogonal refinements, not separate extension points.
   */
  public class VariantsAndContext
  {
  }

  /**
   * Lifecycle Discipline
   * <p>
   * Extension callbacks run as part of server startup and shutdown. Keep them bounded, make start failure-safe, and
   * retain only the resources needed to undo installation in {@code stop}. The application catches and logs extension
   * exceptions, so an extension must not assume that a failure has rolled back work it already performed. Do not use
   * internal repository types merely because the packaged application happens to use them.
   */
  public class LifecycleDiscipline
  {
  }

  /**
   * Repository-aware Extension Example
   * <p>
   * This repository-aware extension notifies an application service when its session state changes. It stores every
   * listener that it adds and removes exactly those listeners during shutdown.
   * {@link #createSessionObserverExtension(Consumer) CreateSessionObserverExtension.java}
   */
  public class Examples
  {
  }

  /**
   * Creates a repository-aware extension that adds and removes session-manager listeners predictably.
   *
   * @snip
   */
  public IAppExtension3 createSessionObserverExtension(Consumer<IRepository> sessionStateChanged)
  {
    return new IAppExtension3()
    {
      private final Map<IRepository, IListener> listeners = new HashMap<>();

      @Override
      public void start(File configFile)
      {
      }

      @Override
      public void start(IRepository[] repositories, File configFile)
      {
        for (IRepository repository : repositories)
        {
          IListener listener = event -> sessionStateChanged.accept(repository);
          repository.getSessionManager().addListener(listener);
          listeners.put(repository, listener);
        }
      }

      @Override
      public void stop(IRepository[] repositories)
      {
        for (IRepository repository : repositories)
        {
          IListener listener = listeners.remove(repository);
          if (listener != null)
          {
            repository.getSessionManager().removeListener(listener);
          }
        }
      }

      @Override
      public void stop()
      {
      }
    };
  }
}
