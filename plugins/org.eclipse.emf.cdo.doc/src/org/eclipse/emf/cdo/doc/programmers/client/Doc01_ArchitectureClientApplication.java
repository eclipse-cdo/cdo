/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.ws.IWSConnector;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

/**
 * Architecture of a Client Application
 * <p>
 * A CDO client combines an EMF model object graph with repository-aware loading and persistence. Application code
 * normally uses generated model interfaces and EMF APIs for domain behavior, then uses CDO APIs to connect, choose a
 * branch or time, manage a view, commit changes, and observe repository events. The session, view, and transaction
 * connect these levels: a session owns connection-wide services; a view loads revision data into its resource set; a
 * transaction tracks local edits and sends them for commit. Revision managers and caches support loading and reuse,
 * while the session protocol carries requests to the server. These managers and the transport are infrastructure;
 * applications generally configure them through session and connector APIs instead of implementing them.
 * <p>
 * The following diagram illustrates the major building blocks of a CDO application: <p align="center">{@image application-architecture.png}
 *
 * @author Eike Stepper
 */
public class Doc01_ArchitectureClientApplication
{
  /**
   * OSGi
   * <p>
   * CDO is distributed as <a href="https://www.osgi.org">OSGi</a> bundles, but its core client and server facilities
   * can also run in a plain Java process. OSGi supplies bundle lifecycle, extension and service discovery, and the
   * shared {@link IPluginContainer wiring container}; registrations declared by bundles are then available without
   * application code registering each {@link IFactory factory}. A standalone application must assemble the required
   * container and factories itself. Application code should usually use the public session and connector APIs rather
   * than depend on OSGi internals.
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
   * EMF
   * <p>
   * The <a href="http://www.eclipse.org/modeling/emf">Eclipse Modeling Framework</a> is a modeling framework and code
   * generation facility for building tools and other applications based on a structured data model. From a model
   * specification described in XMI, EMF provides tools and runtime support to produce a set of Java classes for the model,
   * along with a set of adapter classes that enable viewing and command-based editing of the model, and a basic editor.
   */
  public class EMF
  {
  }

  /**
   * CDO Client
   * <p>
   * The CDO client enables an application to open one or more {@link CDOSession sessions}. Each session represents
   * a connection to a {@link IRepository repository} and provides a broad API to interact with it. A session does <b>not</b>
   * provide direct access to model <i>instances</i>; {@link CDOView views} or {@link CDOTransaction transactions} are needed
   * to navigate or modify the model instance graph.
   * <p>
   * A session consists of several generic (network and storage independent) <b>components</b>, such as:
   * <p>
   * <ul>
   * <li>a {@link CDORevision revision} {@link CDORevisionManager manager} and {@link CDORevisionCache cache},
   * <li>a {@link CDOBranch branch} {@link CDOBranchManager manager},
   * <li>a {@link EPackage package} {@link CDOPackageRegistry registry},
   * <li>a {@link CDOCommitInfo commit info} {@link CDOCommitInfoManager manager},
   * </ul>
   * <p>
   * The client core separates repository behavior from transport. It sends repository operations through the
   * {@link CDOSessionProtocol session protocol} SPI; the shipped protocol uses Net4j signals. Net4j multiplexes that
   * protocol over a connector and transport such as in-process JVM, TCP, SSL, or WebSocket. The server receives the
   * connection, associates it with a server session and repository, executes repository services, and returns results
   * or notifications. Applications normally select/configure a supported connector and do not implement signal
   * indications or protocol dispatch.
   * <p>
   * A small first-application path is: open a {@link Doc03_WorkingWithSessions session}, create a
   * {@link Doc04_WorkingWithViews view} or transaction, load a resource, modify the model, commit through
   * {@link Doc05_WorkingWithTransactions}, and close the owned objects. Framework-specific resource and provider
   * integration is covered in {@link Doc10_IntegratingWithEMFAndOtherFrameworks}.
   *
   * @see EMF
   * @see Models
   */
  public class Client
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
   * multiplexing kernel that can be initialized from OSGi or run standalone. In an in-process application the JVM
   * connector avoids a socket; TCP/SSL and WebSocket connectors cross a process boundary. The transport choice changes
   * how bytes move, while the CDO session protocol and repository operations remain the same.
   *
   * @see Transport
   * @see Protocol
   */
  public class Net4j
  {
  }

  /**
   * Models
   * <p>
   * The models, usually in the form of interfaces that are generated by {@link EMF}, represent the business knowledge
   * in an application. They define the structure (and, mostly irrelevant for CDO, the behavior) of the business entities
   * and they're used by the generic {@link Client CDO client} to manage, e.g., load, commit, query, the business data.
   * <p>
   * <img src="company.png"/>
   * <p>
   * CDO loads individual {@link CDOObject objects} on demand and can unload objects that are no longer strongly reachable
   * under the view's cache policy. For native generated models, the model must be generated with CDO-compatible GenModel
   * properties; ordinary EMF models can instead be used through legacy compatibility. The model interfaces remain the
   * application's domain-facing types, while CDO tracks object identity and revision state.
   * The CDO SDK comes with a convenient migrator tool for existing GenModels and an importer tool for new GenModels.
   *
   * @see EMF
   */
  public class Models
  {
  }

  /**
   * Protocol
   * <p>
   * A concrete communications adapter, a {@link CDOSessionProtocol} implementation that operates inside the
   * {@link Client CDO client}. The only session protocol implementation that currently ships with CDO is based on
   * {@link Net4j}.
   */
  public class Protocol
  {
  }

  /**
   * Transport
   * <p>
   * A concrete transport adapter, an {@link IConnector} implementation that operates on top of the
   * {@link Net4j Net4j core}. Net4j currently ships with {@link IJVMConnector}, {@link ITCPConnector}
   * (optionally with SSL support) and {@link IWSConnector}.
   * <p>
   * The {@link org.eclipse.emf.cdo.doc.programmers.server.Architecture server} must be deployed and configured with
   * the matching transport.
   */
  public class Transport
  {
  }
}
