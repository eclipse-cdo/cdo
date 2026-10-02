/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.doc.operators.Doc00_OperatingServer;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryActivityLog;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Advanced Server Integration
 * <p>
 * Advanced integrations compose repositories with other repositories, diagnostics, or data-transfer facilities. They
 * remain application integrations, not a substitute for {@link Doc00_OperatingServer Operator's Guide procedures}
 * for production topology, backups, or failover operation.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc09_AdvancedServerIntegration
{
  /**
   * Synchronization and Failover
   * <p>
   * An offline clone or failover participant is assembled from a local {@link IStore}, repository properties, and an
   * {@link IRepositorySynchronizer}. Create the synchronizer from a remote {@link CDOSessionConfigurationFactory};
   * that factory supplies the remote session configuration, including connector and repository name. Configure retry
   * and recommit policy before activation, create the specialized repository with
   * {@link CDOServerUtil#createOfflineClone(String, IStore, Map, IRepositorySynchronizer)} or a failover-participant
   * factory, then register it with the owning container. Repository activation starts synchronization; container
   * deactivation stops the repository and synchronizer. The synchronizer owns the remote session it opens, while the
   * application still owns the container, local store resources, and separately created connector infrastructure.
   * <p>
   * {@link ISynchronizableRepository} is a repository that synchronizes against a master through an
   * {@link IRepositorySynchronizer}. It exposes the synchronizer, replicator session, replication progress, and
   * explicit online/offline transition. The last replicated branch ID and commit timestamp describe replication
   * progress; {@link ISynchronizableRepository#hasBeenReplicated()} distinguishes a repository that has completed an
   * initial replication from one that has not. These values are progress information, not a promise that the remote
   * repository is currently reachable or that a promotion is safe.
   * <p>
   * {@link CDOServerUtil#createRepositorySynchronizer(CDOSessionConfigurationFactory)} creates the supplied
   * synchronizer from a remote session configuration factory. The synchronizer owns the remote session it opens and
   * exposes retry and recommit controls. Keep the local repository and synchronizer under a clear lifecycle owner so
   * shutdown closes the replication connection with the repository. These APIs expose state and transitions; they do
   * not select a master, detect safe promotion, resolve split-brain, or guarantee automatic high availability. Those
   * decisions and storage durability remain application and operations responsibilities. Progress and state events
   * are useful inputs, but are not an operational failover protocol.
   * {@link CDOServerUtil#createOfflineClone(String, IStore, Map, IRepositorySynchronizer)} and the
   * failover-participant factories assemble specialized repository variants, but the
   * application must still choose and coordinate the master, backup, storage, and transition policy. The API does not
   * itself establish an operational failover topology or guarantee promotion safety. Treat source/target selection,
   * retry policy, and lifecycle as application design; production failover and recovery procedures belong to the
   * Operator's Guide.
   */
  public class SynchronizationAndFailover
  {
  }

  /**
   * Monitoring and Activity Logs
   * <p>
   * Repository state, manager containers, lifecycle events, and commit information provide lightweight programmatic
   * diagnostics. {@link RepositoryActivityLog} is a lifecycle hook that registers a session-manager listener and a
   * write-access handler while active. The rolling implementation records repository activation, session/view and
   * transaction lifecycle, and commit start/finish events. Deactivating it removes those hooks and closes the rolling
   * log. Keep the log active only while its repository is active, and treat it as diagnostic output rather than an
   * audit trail or retention policy.
   * {@link #withActivityLog(IRepository, Runnable) WithActivityLog.java}
   */
  public class MonitoringAndDiagnostics
  {
  }

  /**
   * Import and Export
   * <p>
   * {@link CDOServerExporter} and {@link CDOServerImporter} are stream-based programmatic transfer boundaries. Choose
   * matching XML or binary implementations. An exporter reads package metadata, branches/revisions, large-object
   * contents, and commit information. It flushes but does not close the caller's output stream, and it temporarily
   * activates a repository only if it was inactive. An importer targets a newly constructed inactive repository: its
   * constructor prepares the target store to drop existing data and activates the target before reading. Never point
   * it at a live repository whose data must be preserved. It consumes but does not own the input stream. The caller
   * closes streams and deactivates the target after import, including when import fails. A failed transfer may leave
   * partial target data; discard that target and retry into a fresh one.
   * <p>
   * This sequence is useful for controlled migration/interchange. It is not an atomic backup or restore operation.
   * The example uses an application-owned file as the transfer boundary.
   * {@link #transferRepository(IRepository, IRepository, File) TransferRepository.java}
   * Backup consistency, scheduling, storage retention, and recovery runbooks remain Operator's Guide concerns.
   */
  public class ImportAndExport
  {
    /**
     * Exports a repository to a caller-owned file and imports it into a fresh, inactive target repository.
     * The target must be disposable because importer initialization drops its existing data and a failed import may be
     * partial.
     *
     * @param source an active source repository
     * @param emptyTarget a newly created target repository with an empty store
     * @param exchangeFile caller-owned transfer file
     * @throws Exception if export or import fails
     * @snip
     */
    public void transferRepository(IRepository source, IRepository emptyTarget, File exchangeFile) throws Exception
    {
      try (OutputStream output = new FileOutputStream(exchangeFile))
      {
        new CDOServerExporter.XML(source).exportRepository(output);
      }

      try
      {
        try (InputStream input = new FileInputStream(exchangeFile))
        {
          new CDOServerImporter.XML(emptyTarget).importRepository(input);
        }
      }
      finally
      {
        LifecycleUtil.deactivate(emptyTarget);
      }
    }
  }

  /**
   * Net4j and Other Intentional Extensions
   * <p>
   * Use the managed container to integrate supported acceptors, connectors, factories, and monitors with a CDO
   * repository. Do not customize internal signal indications or protocol dispatch. When an extension cannot be
   * expressed through the public API or documented SPI presented in this guide, keep it isolated and verify that it is
   * intentionally supported before relying on it across CDO releases.
   */
  public class IntegrationBoundary
  {
  }

  /**
   * Runs application work while an activity log is attached to and observing a repository, then removes its hooks.
   *
   * @param repository the active repository to observe
   * @param applicationWork application work performed while the log is active
   * @snip
   */
  public void withActivityLog(IRepository repository, Runnable applicationWork)
  {
    RepositoryActivityLog log = new RepositoryActivityLog.Rolling("activities", 1000000L, true);
    log.setRepository(repository);

    try
    {
      LifecycleUtil.activate(log);
      applicationWork.run();
    }
    finally
    {
      LifecycleUtil.deactivate(log);
      log.setRepository(null);
    }
  }
}
