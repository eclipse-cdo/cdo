/*
 * Copyright (c) 2011-2013, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.doc.reference.StoreFeatures;
import org.eclipse.emf.cdo.server.ILockingManager;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepository.ReadAccessHandler;
import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

/**
 * Understanding the Architecture of a Repository
 * <p>
 * The main building block of a CDO repository is split into two layers, the generic repository layer that client
 * applications interact with and the database integration layer that providers can hook into to integrate their data
 * storage solutions with CDO. A number of such integrations already ship with CDO, making it possible to connect a
 * repository to all sorts of JDBC databases, Hibernate, Objectivity/DB, MongoDB or DB4O.
 * <p>
 * While technically a CDO repository depends on EMF this dependency is not of equal importance as it is in a CDO
 * application. In particular the generated application models are not required to be deployed to the server because a
 * CDO repository accesses models reflectively and the model objects are not implemented as {@link EObject EObjects} on
 * the server.
 * <p>
 * The following diagram illustrates the major building blocks of a CDO repository: <p align="center">{@image repository-architecture.png}
 *
 * @author Eike Stepper
 */
public class Architecture
{
  /**
   * OSGi
   * <p>
   * All components of CDO are implemented as <a href="http://www.osgi.org">OSGi</a> bundles. The core components of
   * both clients and servers do not require OSGi to actually run to be functional, they can perfectly be operated
   * stand-alone. If OSGi is running the setup and configuration of some CDO facilities is a little simpler than in
   * stand-alone mode because the needed {@link IFactory factories} get automatically registered with the central
   * {@link IPluginContainer wiring container}.
   * <p>
   * CDO utilizes an {@link OMPlatform operations and maintenance} framework to abstract common platform services such
   * as {@link OMLogger logging}, {@link OMTracer tracing}, {@link OMMonitor monitoring} and {@link OMPreference
   * configuration}. Without the need to depend on additional external libraries these services integrate seamlessly
   * with OSGi, if available at runtime, or emulate similar functionality if running stand-alone.
   */
  public class OSGi
  {
  }

  /**
   * CDO Server Core
   * <p>
   * The core of a CDO server consists of one or more {@link IRepository repositories} each of which, in turn, consists
   * of several generic (network and storage independent) <b>components</b>, such as:
   * <p>
   * <ul>
   * <li>a {@link CDORevision revision} {@link CDORevisionManager manager} and {@link CDORevisionCache cache},
   * <li>a {@link CDOBranch branch} {@link CDOBranchManager manager},
   * <li>a {@link EPackage package} {@link CDOPackageRegistry registry},
   * <li>a {@link CDOLock lock} {@link ILockingManager manager},
   * <li>a {@link CDOSession session} {@link ISessionManager manager},
   * <li>a {@link CDOCommitInfo commit info} {@link CDOCommitInfoManager manager},
   * <li>a {@link IQueryHandler query handler} {@link IQueryHandlerProvider provider}.
   * </ul>
   * <p>
   * In addition the following types of <b>handlers</b> can be hooked up with a repository:
   * <p>
   * <ul>
   * <li>{@link ReadAccessHandler Read access} handlers,
   * <li>{@link WriteAccessHandler Write access} handlers,
   * <li>{@link CDOCommitInfoHandler Commit info} handlers.
   * </ul>
   * <p>
   * All <b>persistent aspects</b> (the storage/retrieval of data in/from a database system) are fully abstracted
   * through the service provider interfaces (SPI) {@link IStore}, {@link IStoreAccessor} and {@link IStoreChunkReader}.
   * Concrete implementations are fully separated and can be plugged into the core as described in {@link Store}.
   * <p>
   * All <b>communication aspects</b> (the sending/receiving of signals to/from a network system) are fully abstracted
   * through the service provider interface (SPI) {@link ISessionProtocol}. Concrete implementations are fully separated
   * and can be plugged into the core as described in {@link Protocol}.
   */
  public class Core
  {
  }

  /**
   * CDO Store
   * <p>
   * A concrete storage adapter, an {@link IStore} implementation that operates on top of the generic {@link Core server
   * core}. A number of such stores already ship with CDO, making it possible to connect a repository to all sorts of
   * JDBC databases, Hibernate, Objectivity/DB, MongoDB or DB4O.
   *
   * @see StoreFeatures
   */
  public class Store
  {
  }

  /**
   * Protocol
   * <p>
   * A concrete communications adapter, an {@link ISessionProtocol} implementation that operates on top of the generic
   * {@link Core server core}. The only session protocol implementation that currently ships with CDO is based on
   * {@link Net4j}.
   */
  public class Protocol
  {
  }

  /**
   * OCL
   * <p>
   * An {@link IQueryHandler} implementation that provides support for OCL queries by executing them at the generic repository level,
   * i.e., independent of the concrete {@link Store} being used.
   * <p>
   * The OCL component is optional.
   */
  public class OCL
  {
  }

  /**
   * Net4j Core
   * <p>
   * The <i>Net4j Signaling Platform</i> is an extensible client/server communications framework. Net4j eases the
   * development of fast and maintainable application {@link ISignalProtocol protocols} that are independent of the
   * physical {@link IConnector transport} medium. Transport protocols are pluggable and Net4j ships with support for
   * {@link ITCPConnector TCP}, {@link SSLUtil SSL}, {@link IWSConnector WS} and {@link IJVMConnector JVM}
   * (in-process) transport. The core of Net4j is a fast, asynchronous and non-blocking {@link IBuffer buffer}
   * multiplexing kernel, based on {@link OSGi} but also executable stand-alone.
   *
   * @see Transport
   * @see Protocol
   */
  public class Net4j
  {
  }

  /**
   * Transport
   * <p>
   * A concrete transport adapter, an {@link IAcceptor} implementation that operates on top of the
   * {@link Net4j Net4j core}. Net4j currently ships with {@link IJVMAcceptor}, {@link ITCPAcceptor}
   * (optionally with SSL support) and {@link IWSAcceptor}.
   */
  public class Transport
  {
  }
}
