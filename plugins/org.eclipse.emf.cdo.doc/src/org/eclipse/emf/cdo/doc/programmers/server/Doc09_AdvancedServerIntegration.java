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
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryActivityLog;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

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
   * {@link ISynchronizableRepository} is a repository that synchronizes against a master through an
   * {@link IRepositorySynchronizer}. It exposes the synchronizer, replicator session, replication progress, and
   * explicit online/offline transition. {@link CDOServerUtil#createRepositorySynchronizer(org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory)}
   * creates the supplied synchronizer from a remote session configuration factory. The same utility offers offline
   * clone and failover-participant creation. Treat source/target selection, retry policy, and lifecycle as part of the
   * application design; production failover topology and recovery procedures belong to the Operator's Guide.
   */
  public class SynchronizationAndFailover
  {
  }

  /**
   * Monitoring and Activity Logs
   * <p>
   * Repository state, manager containers, lifecycle events, and commit information provide lightweight programmatic
   * diagnostics. {@link RepositoryActivityLog} is a lifecycle hook that records repository activation, sessions,
   * views, transactions, and commit start/finish events. Its supplied rolling implementation can be attached to an
   * application-owned repository. Keep custom diagnostics bounded and do not treat an activity log as an operational
   * retention policy.
   * {@link #createActivityLog(IRepository) CreateActivityLog.java}
   */
  public class MonitoringAndDiagnostics
  {
  }

  /**
   * Import and Export
   * <p>
   * {@link CDOServerExporter} and {@link CDOServerImporter} are the programmatic transfer boundaries for repository
   * contents and history. Subclass or configure them only through their documented API for controlled application
   * migration and interchange. Backup schedules, storage retention, and restore runbooks are operational concerns.
   */
  public class ImportAndExport
  {
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
   * Creates and activates an activity log attached to the supplied repository.
   *
   * @snip
   */
  public RepositoryActivityLog createActivityLog(IRepository repository)
  {
    RepositoryActivityLog log = new RepositoryActivityLog.Rolling("activities", 1000000L, true);
    log.setRepository(repository);
    LifecycleUtil.activate(log);
    return log;
  }
}
