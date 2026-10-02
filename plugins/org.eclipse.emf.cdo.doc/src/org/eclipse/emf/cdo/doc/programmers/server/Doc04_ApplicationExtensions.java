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
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Application Extensions
 * <p>
 * An {@link IAppExtension application extension} is a supported SPI for application-level server startup work. An OSGi
 * bundle contributes an {@code appExtension} element to {@code org.eclipse.emf.cdo.server.appExtensions}; the server
 * instantiates the declared class, so its no-argument construction must not depend on a repository already existing.
 * Choose a callback interface for the context and lifecycle you need, install repository-specific listeners or
 * handlers in that callback, and remove those exact registrations on stop. An application extension orchestrates
 * application behavior; it is not itself a repository handler, a repository listener, or a managed-container factory.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc04_ApplicationExtensions
{
  /**
   * Choosing an Extension Variant
   * <p>
   * The interfaces extend the base {@link IAppExtension} lifecycle. The packaged server constructs one contributed
   * instance at startup, then calls {@link IAppExtension#start(File)} and later {@link IAppExtension#stop()}. The base
   * form receives the application XML file, but no repository array; look repositories up through a supported
   * container context only when appropriate.
   * <p>
   * {@link IAppExtension2} adds {@link IAppExtension2#startDynamic(Reader)} for each dynamically configured
   * repository. The repository-configuration manager creates a separate extension instance for each dynamic start,
   * passes that repository's XML, and calls that instance's {@code stop()} when the repository deactivates. A dynamic
   * extension must therefore keep per-instance cleanup state and must not assume its base {@code start(File)} was
   * called for that dynamic instance.
   * <p>
   * {@link IAppExtension3} receives the initially configured {@link IRepository repositories} and config file in its
   * repository-aware start callback, with a matching repository-array stop callback instead of the base callback
   * pair. It is the clearest choice for installing one listener/handler per configured repository. The array describes
   * initial startup; later dynamic repositories have their own configuration lifecycle and do not appear in it.
   * {@link IAppExtension4} supplies a numeric priority: smaller values start first within their phase and stop in
   * reverse order; extensions without it use the default priority. {@link IAppExtension5} supplies a logging name and
   * selects the early phase through {@link IAppExtension5#startBeforeRepositories()}. Early callbacks run before the
   * repository configurator and must not use repository state. These are orthogonal choices; an extension can
   * implement more than one variant.
   */
  public class VariantsAndContext
  {
  }

  /**
   * Lifecycle Discipline
   * <p>
   * Startup and stop callbacks run synchronously on the server application lifecycle path. Keep them bounded. Retain
   * only the repository/listener/handler pairs needed for teardown. If setup fails after installing some listeners,
   * remove the partial set in the start method's failure path: the packaged application logs an extension exception
   * and continues, but does not call {@code stop()} as rollback for a failed start. On normal stop, remove those
   * registrations before the owning repositories deactivate. Repository listener callbacks may run on the event
   * delivery thread, so queue expensive work elsewhere. The contribution's {@code predecessor} attribute suppresses
   * a competing extension implementation; it is not an ordering mechanism. Do not depend on internal repository
   * classes merely because the packaged application uses them.
   */
  public class LifecycleDiscipline
  {
  }

  /**
   * Repository-aware Extension Example
   * <p>
   * This repository-aware extension notifies an application service when its session state changes. It stores every
   * listener that it adds, unwinds partial setup if installation fails, and removes exactly those listeners on shutdown.
   * {@link #createSessionObserverExtension(Consumer) CreateSessionObserverExtension.java}
   * <p>
   * Contribute the extension from its bundle's {@code plugin.xml} with
   * {@link #extensionContribution() plugin.xml}.
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
        try
        {
          for (IRepository repository : repositories)
          {
            IListener listener = event -> sessionStateChanged.accept(repository);
            repository.getSessionManager().addListener(listener);
            listeners.put(repository, listener);
          }
        }
        catch (RuntimeException | Error ex)
        {
          removeListeners();
          throw ex;
        }
      }

      private void removeListeners()
      {
        for (Map.Entry<IRepository, IListener> entry : listeners.entrySet())
        {
          entry.getKey().getSessionManager().removeListener(entry.getValue());
        }

        listeners.clear();
      }

      @Override
      public void stop(IRepository[] repositories)
      {
        removeListeners();
      }

      @Override
      public void stop()
      {
        removeListeners();
      }
    };
  }

  /**
   * Declares an application extension class in an OSGi bundle's plugin.xml. The contributed class must implement the
   * selected extension interface and have a public no-argument constructor.
   *
   * @snip xml app-extension.xml
   */
  public void extensionContribution()
  {
  }
}
