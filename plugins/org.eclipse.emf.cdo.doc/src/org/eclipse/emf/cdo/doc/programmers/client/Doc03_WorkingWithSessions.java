package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonRepository.CommitInfoStorage;
import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.CDOCommonRepository.Mode;
import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.CDOCommonRepository.StateChangedEvent;
import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.common.CDOCommonRepository.TypeChangedEvent;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag.TagMovedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag.TagRenamedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionsLoadedEvent;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.doc.programmers.client.Doc03_WorkingWithSessions.CreatingAndConfiguringSessions.PassiveUpdatesAndRefreshing;
import org.eclipse.emf.cdo.doc.programmers.client.Doc03_WorkingWithSessions.SessionFacilities.RevisionManager.PrefetchingRevisions;
import org.eclipse.emf.cdo.doc.programmers.server.Architecture;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionRecoveryEvent;
import org.eclipse.emf.cdo.net4j.FailoverCDOSessionConfiguration;
import org.eclipse.emf.cdo.net4j.ReconnectingCDOSessionConfiguration;
import org.eclipse.emf.cdo.net4j.RecoveringCDOSessionConfiguration;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.net4j.FailoverMonitor;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSession.Options;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.session.CDOSessionPermissionsChangedEvent;
import org.eclipse.emf.cdo.session.CDOSessionRegistry;
import org.eclipse.emf.cdo.session.CDOUserInfoManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionEvent;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage.Priority;
import org.eclipse.emf.cdo.session.remote.CDORemoteTopic;
import org.eclipse.emf.cdo.session.remote.CDORemoteTopicEvent;
import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionOpener;
import org.eclipse.emf.cdo.ui.CDOTopicProvider;
import org.eclipse.emf.cdo.ui.CDOTopicProvider.Topic;
import org.eclipse.emf.cdo.ui.DefaultTopicProvider;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.util.ContextOperationAuthorization;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOPrefetcherManager;
import org.eclipse.emf.cdo.view.CDOPrefetcherManager.Prefetcher;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOUnit;
import org.eclipse.emf.cdo.view.CDOUnitManager;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewContainer;
import org.eclipse.emf.cdo.view.CDOViewOpener;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.doc.Overview;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock.DelegateDetector;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleEvent;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;
import org.eclipse.net4j.util.ui.security.CredentialsDialog;
import org.eclipse.net4j.util.ui.security.InteractiveCredentialsProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.CDOOperationAuthorizer;
import org.eclipse.emf.spi.cdo.CDOPermissionUpdater3;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * Working with Sessions
 * <p>
 * This chapter covers all aspects of session management in CDO client applications,
 * including configuration, lifecycle, facilities, and events.
 * <p>
 * Sessions are the foundation of any CDO client application. They represent the
 * connection to a CDO repository and provide access to views, transactions, and
 * various session facilities. Understanding how to create, configure, and manage
 * sessions is essential for building robust and scalable CDO applications.
 * <p>
 * Sessions are created (opened) when needed, kept open for the duration of their use,
 * and closed when no longer required. A client application may have multiple sessions
 * open simultaneously, each connected to different repositories or using different
 * configurations.
 * <p>
 * <b>Session API Structure</b>
 * <p>
 * The session API in CDO is organized into two main components: the basic CDO
 * session and the specific Net4j session.
 * <ul>
 *   <li><b>Basic CDO Session:</b> The {@link CDOSession}
 *        interface provides the core functionality for interacting with a CDO repository.
 *        It offers access to views, transactions, branches, packages, and other repository
 *        entities, independent of the underlying transport protocol.</li>
 *   <li><b>Net4j Session:</b> The {@link CDONet4jSession}
 *        interface extends the basic session with features specific to the Net4j transport
 *        layer, such as advanced connector management, protocol selection, and network
 *        configuration. Net4j sessions are created and configured using
 *        {@link CDONet4jSessionConfiguration} and
 *        {@link CDONet4jUtil}.</li>
 * </ul>
 * This separation allows applications to use the generic session API for repository
 * operations, while leveraging Net4j-specific capabilities when needed for networked
 * environments.
 * <p>
 * Other transport-specific session types are possible in general, allowing for future
 * extensibility of the session API to support additional transport protocols. However,
 * as of now, only the Net4j implementation is available and supported in CDO. This means
 * that while the API is designed to be flexible, all practical usage currently relies on
 * the Net4j session type for networked repository access.
 * <p>
 * <b>Table of Contents</b> {@toc}
 */
public class Doc03_WorkingWithSessions
{
  /**
   * Creating and Configuring Sessions
   * <p>
   * Learn how to instantiate and configure CDOSession objects, including specifying repository details, authentication,
   * and connection parameters. Proper session configuration ensures secure and efficient communication with the CDO
   * server.
   */
  public class CreatingAndConfiguringSessions
  {
    /**
     * Session Configurations
     * <p>
     * In order to create a session, you first need to create a session configuration. This configuration
     * specifies the connector to use and the repository name to connect to, as well as various other options.
     * <p>
     * Here is an example of how to create and open a session using a given connector and repository name:
     * {@link SessionConfigurations#createSession(IConnector, String) CreateSession.java}
     * <p>
     * Note that the connector must be created and configured separately. For more information about
     * connectors, refer to the {@link Net4jConnectors} section or the {@link Overview Net4j documentation}.
     * <p>
     * Also note that the repository must already exist on the server. For more information about
     * repositories, refer to the {@link Architecture Server Programming} section.
     * <p>
     * Once a session is opened, you can still change various options on it, such as enabling or
     * disabling passive updates. For more information about session options, refer to the
     * {@link SessionOptions} section.
     */
    public class SessionConfigurations
    {
      /**
       * @snip
       */
      public void createSession(IConnector connector, String repositoryName)
      {
        CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
        configuration.setConnector(connector);
        configuration.setRepositoryName(repositoryName);

        CDOSession session = configuration.openNet4jSession();
        System.out.println("Session opened for repository: " + session.getRepositoryInfo().getName());

        // Use the session...

        // Finally, close the session when done.
        session.close();
      }
    }

    /**
     * Net4j Connectors
     * <p>
     * Net4j connectors are the transport layer for CDO communication. Learn how to configure TCP, JVM, SSL, WS or WSS
     * connectors to establish reliable and secure connections between client and server.
     * <p>
     * Typically a Net4j connector is created and configured using a Net4j container. Within an Eclipse environment,
     * the container is usually managed by the Eclipse extension registry. Such a container can be accessed using
     * IPluginContainer.INSTANCE. In a non-Eclipse environment, you can create and manage the container
     * programmatically. Refer to {@link IManagedContainer} for more details.
     * <p>
     * The following example demonstrates how to set up a JVM connector within an Eclipse environment:
     * {@link Net4jConnectors#createConnectorInEclipse() CreateConnectorInEclipse.java}
     * <p>
     * The following example demonstrates how to set up a TCP connector within a non-Eclipse environment:
     * {@link Net4jConnectors#createConnectorInStandalone() CreateConnectorInStandalone.java}
     * <p>
     * More details on the architecture and usage of Net4j connectors can be found in the
     * {@link Overview Net4j documentation}.
     */
    public class Net4jConnectors
    {
      /**
       * @snip
       */
      public void createConnectorInEclipse()
      {
        // Obtain the connector from the plugin container by its product group, factory type, and factory-specific
        // description.
        IConnector connector = IPluginContainer.INSTANCE.getElementOrNull("org.eclipse.net4j.connectors", "jvm", "acceptor1");

        // Use the connector to create and open a session...

        // Finally, close the connector when done.
        connector.close();
      }

      /**
       * @snip
       */
      public void createConnectorInStandalone()
      {
        IManagedContainer container = ContainerUtil.createContainer();
        ContainerUtil.prepareContainer(container); // Register basic Net4j factories.
        TCPUtil.prepareContainer(container); // Register TCP connector factory.
        container.activate();

        // Obtain the connector from the container by its product group, factory type, and factory-specific description.
        IConnector connector = container.getElementOrNull("org.eclipse.net4j.connectors", "tcp", "localhost:2036");

        // Use the connector to create and open a session...

        // Finally, close the connector when done.
        connector.close();

        // Deactivate the container when done.
        container.deactivate();
      }
    }

    /**
     * Authentication and Security
     * <p>
     * The repository may require clients to authenticate before allowing access. In this this case, the repository
     * administrator configures one or more authentication providers on the server side. When a client opens a session,
     * an open-session request is sent to the server. If the server requires authentication, it initiates an authentication
     * challenge-response sequence using one of its configured authentication providers. The client must respond with
     * appropriate credentials. If the credentials are valid, the server allows the session to be opened; otherwise,
     * the session opening fails.
     * <p>
     * Here is an example of how to configure a session with fixed credentials:
     * {@link AuthenticationAndSecurity#configureCredentialsProvider() ConfigureCredentialsProvider.java}
     * <p>
     * Here is an example of how to configure a session with an interactive credentials provider that opens a
     * {@link CredentialsDialog} whenever challenged from the server:
     * {@link AuthenticationAndSecurity#interactiveCredentialsProvider() InteractiveCredentialsProvider.java}
     * <p>
     * The actual password is transmitted securely using a cryptographically strong protocol called <i>Diffie-Hellman Key Exchange</i>.
     * It is not sent over the network in clear text, but is restored to clear text on the server side for verification.
     */
    public class AuthenticationAndSecurity
    {
      /**
       * @snip
       */
      public void configureCredentialsProvider()
      {
        IPasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider("userID", "password");

        CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
        configuration.setCredentialsProvider(credentialsProvider);
      }

      /**
       * @snip
       */
      public void interactiveCredentialsProvider()
      {
        IPasswordCredentialsProvider credentialsProvider = new InteractiveCredentialsProvider();

        CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
        configuration.setCredentialsProvider(credentialsProvider);
      }
    }

    /**
     * Recovering from Disconnects
     * <p>
     * Discover strategies for handling session disconnects and automatic reconnection. Learn how to maintain session
     * continuity and recover gracefully from network interruptions.
     * <p>
     * Normally, when a session is disconnected, it is automatically closed and cannot be used anymore. However, you can
     * create a reconnecting session that automatically tries to reconnect when it gets disconnected. This is done by using
     * CDONet4jUtil.createReconnectingSessionConfiguration() instead of CDONet4jUtil.createNet4jSessionConfiguration().
     * <p>
     * Here is an example of how to create and open a reconnecting session:
     * {@link RecoveringFromDisconnects#reconnectingSession(String, String) ReconnectingSession.java}
     * <p>
     * A session can also be disconnected in case of server problems, as opposed to network problems.
     * One way to protect against such situations is to use a fail-over session.
     * <p>
     * Here is an example of how to create and open a reconnecting session:
     * {@link RecoveringFromDisconnects#failoverSession(String, String) FailoverSession.java}
     * <p>
     * Fail-over sessions use a special network service, called fail-over monitor, to ask for the address of a live server.
     * The configuration and operation of the fail-over monitor is beyond the scope of this documentation.
     * For more information, refer to {@link FailoverMonitor}.
     */
    public class RecoveringFromDisconnects
    {
      /**
       * @snip
       */
      public void reconnectingSession(String hostAndPort, String repositoryName)
      {
        IManagedContainer container = IPluginContainer.INSTANCE;

        ReconnectingCDOSessionConfiguration configuration = //
            CDONet4jUtil.createReconnectingSessionConfiguration(hostAndPort, repositoryName, container);
        configuration.setHeartBeatEnabled(true); // Enable heart beats to detect network problems early.
        configuration.setHeartBeatPeriod(5000); // Send a heart beat every 5 seconds.
        configuration.setHeartBeatTimeout(2000); // Consider the connection dead if no heart beat response is received
                                                 // within 2 seconds.
        configuration.setConnectorTimeout(5000); // Timeout if unable to connect within 5 seconds.
        configuration.setReconnectInterval(5000); // Try to reconnect every 5 seconds when the connection is lost.
        configuration.setMaxReconnectAttempts(-1); // Try to reconnect forever.

        CDOSession session = configuration.openNet4jSession();
        System.out.println("Session opened for repository: " + session.getRepositoryInfo().getName());
        // Use the session...

        // Finally, close the session when done.
        session.close();
      }

      /**
       * @snip
       */
      public void failoverSession(String monitorConnectorDescription, String repositoryGroup)
      {
        IManagedContainer container = IPluginContainer.INSTANCE;

        FailoverCDOSessionConfiguration configuration = //
            CDONet4jUtil.createFailoverSessionConfiguration(monitorConnectorDescription, repositoryGroup, container);
        configuration.setHeartBeatEnabled(true); // Enable heart beats to detect network problems early.
        configuration.setHeartBeatPeriod(5000); // Send a heart beat every 5 seconds.
        configuration.setHeartBeatTimeout(2000); // Consider the connection dead if no heart beat response is received
                                                 // within 2 seconds.
        configuration.setConnectorTimeout(5000); // Timeout if unable to connect within 5 seconds.

        CDOSession session = configuration.openNet4jSession();
        System.out.println("Session opened for repository: " + session.getRepositoryInfo().getName());
        // Use the session...

        // Finally, close the session when done.
        session.close();
      }
    }

    /**
     * Passive Updates and Refreshing
     * <p>
     * Passive updates allow sessions to receive notifications about changes in the repository. Understand how to enable
     * passive updates and refresh views to keep your client data synchronized.
     * <p>
     * By default, sessions receive passive updates from the server. This means that when other clients make changes to
     * the repository, the server notifies all connected sessions about these changes. The sessions then invalidate
     * the affected objects in their views, marking them as stale. When your application accesses a stale object, it is
     * automatically refreshed from the server, ensuring that your application always works with the latest data.
     * <p>
     * Passive updates can be disabled if your application requires more control over when objects are refreshed.
     * In this case, you can call the refresh() method on a view to manually refresh all stale objects.
     * <p>
     * Here is an example of how to enable/disable passive updates and manually refresh views:
     * {@link #passiveUpdatesAndRefreshing(IConnector, String) PassiveUpdatesAndRefreshing.java}
     * <p>
     * Note that passive updates are a session-wide setting that affects all views within the session.
     * When passive updates are re-enabled, a refresh is automatically performed to synchronize all views
     * with the server. For more information about views, refer to the {@link Doc04_WorkingWithViews.WaitingForUpdates} section.
     * <p>
     * When passive updates are enabled, the amount of information sent from the server to the client
     * can be controlled using the passive update mode setting. There are three modes available:
     * <ul>
     * <li><b>INVALIDATIONS:</b> Only the IDs of invalidated objects are sent to the client. This is the default mode and
     *      usually the most efficient.</li>
     * <li><b>CHANGES:</b> In this mode, the server sends the actual changes made to invalidated objects, such as attribute updates
     *      or reference modifications. This mode can be more efficient if the client often accesses invalidated objects shortly after
     *      they are invalidated. But it can also be less efficient if many invalidated objects are never accessed again.</li>
     * <li><b>ADDITIONS:</b> In addition to sending changes, the server also sends information about newly created objects.
     *      This mode can be useful for clients that need to keep track of the overall state of the repository,
     *      but it is usually the least efficient.</li>
     * </ul>
     * <p>
     * Here is an example of how to set the passive update mode:
     * {@link PassiveUpdatesAndRefreshing#passiveUpdateModes(CDOSession) PassiveUpdateModes.java}
     * <p>
     * Note that the passive update mode can also be changed at any time on a session via the {@link Doc03_WorkingWithSessions.SessionOptions session options}.
     * For more information about passive update modes, refer to {@link PassiveUpdateMode}.
     */
    public class PassiveUpdatesAndRefreshing
    {
      /**
       * @snip
       */
      public void passiveUpdatesAndRefreshing(IConnector connector, String repositoryName)
      {
        CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
        configuration.setConnector(connector);
        configuration.setRepositoryName(repositoryName);
        configuration.setPassiveUpdateEnabled(true); // This is the default.

        CDOSession session = configuration.openNet4jSession();
        // Use the session with passive updates enabled...

        session.options().setPassiveUpdateEnabled(false); // Disable passive updates.
        // Use the session with passive updates disabled...

        session.refresh(); // Manually refresh all stale objects in all views.
        // Use the session with passive updates disabled...

        session.options().setPassiveUpdateEnabled(true); // Re-enable passive updates.
        // Use the session with passive updates enabled...

        // Finally, close the session when done.
        session.close();
      }

      /**
       * @snip
       */
      public void passiveUpdateModes(CDOSession session)
      {
        session.options().setPassiveUpdateEnabled(true);
        session.options().setPassiveUpdateMode(PassiveUpdateMode.INVALIDATIONS);
        // All views in the session now receive invalidations only...

        session.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
        // All views in the session now receive changes...

        session.options().setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);
        // All views in the session now receive changes and additions...
      }
    }

    /**
     * Waiting For Updates
     * <p>
     * In some scenarios, your application may need to wait for a specific update to be processed before proceeding.
     * As updates are generally triggered by the server asynchronously, for example when another client commits
     * changes, there is no guarantee that an update will be received immediately after it occurs.
     * <p>
     * To handle such situations, you can use the {@link CDOSession#waitForUpdate(long) waitForUpdate(long updateTime)} method on a session.
     * This method blocks the calling thread until all updates that occurred before the specified update time have been processed.
     * If you want to limit the time spent waiting, you can also specify a timeout value in milliseconds. If the timeout
     * expires before the updates are processed, the method returns false; otherwise, it returns true.
     * <p>
     * Here is an example of how to wait for updates:
     * {@link WaitingForUpdates#waitingForUpdates(CDOSession, long) WaitingForUpdates.java}
     * <p>
     * Note that more commonly, applications use listeners to be notified of updates asynchronously.
     * For more information about session events and listeners, refer to the {@link SessionEvents} section.
     * <p>
     * Note also that applications usually wait for updates on views rather than sessions.
     * For more information about views, refer to the {@link ViewManagement} section.
     */
    public class WaitingForUpdates
    {
      /**
       * @snip
       */
      public void waitingForUpdates(CDOSession session, long updateTime)
      {
        long timeout = 10000; // Wait for up to 10 seconds.

        if (session.waitForUpdate(updateTime, timeout))
        {
          // All updates that occurred before the call to waitForUpdate() have been processed.
        }
        else
        {
          // The timeout expired before all updates were processed.
        }
      }
    }
  }

  /**
   * View Management
   * <p>
   * Views are the primary mechanism for accessing and working with models that are stored in a CDO repository. They provide
   * a consistent and isolated perspective on the repository state, allowing clients to read and, in the case of
   * transactions, modify data. Understanding how to create, manage, and interact with views is essential for building
   * robust CDO client applications.
   * <p>
   * Views are created from sessions and are associated with a specific branch point in the repository. This branch point
   * determines the exact state of the repository that the view exposes. Views can be read-only or read-write (transactions),
   * and they can also be configured to access historical states of the repository through audit views.
   * <p>
   * This section covers the key aspects of view management, including the purpose of views and transactions,
   * branch points and view targets, types of views, finding views by ID, listening for view-related events, and closing views.
   * For more detailed information on working with views, refer to the {@link Doc04_WorkingWithViews} section.
   * <p>
   * Note that a {@link CDOSession} inherits most of the view management functionality through the following super interfaces:
   * <ul>
   * <li>{@link CDOViewContainer} and {@link IContainer IContainer&lt;CDOView&gt;}: Provide methods for managing a collection of
   *      views within the session, including the ability to listen for view addition and removal events.</li>
   * <li>{@link CDOViewOpener} and {@link CDOTransactionOpener}: Provide various methods for opening views and transactions.</li>
   * </ul>
   */
  public class ViewManagement
  {
    /**
     * Purpose of Views and Transactions
     * <p>
     * A CDO repository can store many different versions of a model. Each time a change is committed to the repository,
     * a new version of the model is created. These versions are organized into branches, allowing for parallel lines of development.
     * Each version of the model is identified by a unique combination of a branch and a timestamp, known as a <i>branch point</i>.
     * This multiplicity of versions and branches is the reason why a CDOSession alone is not sufficient to access a consistent
     * model state of the repository. Instead, clients need views and transactions to specify which version of the model they want to work with.
     * <p>
     * A view represents a read-only window into the repository. Read-only views allow clients to
     * navigate and query models without the risk of modifying data. For example, a client application
     * might use a single read-only view to display a model in a different places of its user interface,
     * allowing users to browse and inspect the data.
     * <p>
     * Transactions, on the other hand,
     * are special types of views that enable clients to make changes to the repository. Changes made in a transaction
     * are isolated from other views until the transaction is committed, ensuring data consistency and supporting
     * concurrent development.
     * <p>
     * For more detailed information on working with views, refer to the {@link Doc04_WorkingWithViews} section.
     */
    public class PurposeOfViewsAndTransactions
    {
    }

    /**
     * Branch Points and the View Target
     * <p>
     * Every view or transaction in CDO is associated with a specific {@link CDOBranchPoint branch point}, which determines the exact state of the
     * repository that the view exposes. The branch point consists of a {@link CDOBranchPoint#getBranch() branch} and a
     * {@link CDOBranchPoint#getTimeStamp() timestamp}, together called the
     * <b>view target</b>. This mechanism allows clients to access not only the latest state of the main branch, but also
     * historical states (by specifying an earlier timestamp), or parallel lines of development (by specifying a different
     * branch).
     * <p>
     * Normally, when you open a view or transaction, you specify the branch and/or timestamp that you want to target.
     * Read-only views can target any branch point, including historical states, while transactions always target the
     * head of a branch, allowing clients to make changes to the latest state of that branch.
     * <p>
     * When you open a view or transaction without specifying a branch point, it defaults to the head of the main branch,
     * showing the most recent state of the repository. To access a different branch or a historical state, you can
     * explicitly specify a branch and/or timestamp when opening the view. This is useful for auditing, time-travel queries,
     * or working with feature branches.
     * <p>
     * For both views and transactions, the branch point is initially set when the view is created, but it can be changed
     * later using the {@link CDOView#setBranchPoint(CDOBranchPoint) setBranchPoint()} method. Changing the branch point
     * causes the view to refresh its contents to reflect the state of the repository at the new target branch point.
     * All model objects in the view are updated to match the new branch point, ensuring that the view always presents a
     * consistent snapshot of the repository. This process is automatic and absolutely transparent to the client application.
     * It enables powerful scenarios such as switching between different branches or historical states on-the-fly,
     * without needing to close and reopen views. The restriction is that transactions can only be switched to other branch heads,
     * not to historical states.
     * <p>
     * The head of a branch is represented by a special timestamp value called {@link CDOBranchPoint#UNSPECIFIED_DATE UNSPECIFIED_DATE}.
     * It ultimately represents a timestamp that is always greater than any other timestamp in the repository, effectively pointing to the
     * latest commit on that branch. When a transaction is committed, it creates a new version of the model at the head of the branch,
     * and all views targeting that branch head are automatically updated to reflect the new state. The head of a branch is
     * always moving forward in time as new commits are made, while historical states remain fixed at their respective timestamps.
     * As transactions always target the branch head, they are always working with the most recent state of the branch.
     * <p>
     * For more information about branches and branch points, see the {@link CDOBranchPoint}
     * interface and the {@link CDOBranchManager} facility.
     */
    public class BranchPointsAndTheViewTarget
    {
    }

    /**
     * Opening Views and Transactions
     * <p>
     * Views and transactions are created from a session using the various {@link CDOSession#openView() openView()} and
     * {@link CDOSession#openTransaction() openTransaction()} methods. These methods allow you to specify the initial branch and/or timestamp
     * that you want to target, as well as a {@link ResourceSet} to use for loading model objects.
     * <p>
     * You can also omit the branch, timestamp, and/or ResourceSet, in which case the view or transaction starts with reasonable defaults
     * as follows:
     * <ul>
     * <li>If no branch is specified, the main branch is used.</li>
     * <li>If no timestamp is specified, the head of the branch is used.</li>
     * <li>If no ResourceSet is specified, a new one is created automatically.</li>
     * </ul>
     * <p>
     * Here is an example of how to open views and transactions with different configurations:
     * {@link #openingViewsAndTransactions(CDOSession, ResourceSet) OpeningViewsAndTransactions.java}
     * <p>
     * Note that views and transactions are lightweight objects that can be created and disposed of as needed.
     * They should be closed when no longer needed to free up resources.
     * <p>
     * Here is a summary of the various ways to open views and transactions:
     * <ul>
     * <li><b>openView():</b> Opens a read-only view targeting the head of the main branch with a new ResourceSet.</li>
     * <li><b>openView(CDOBranch branch):</b> Opens a read-only view targeting the head of the specified branch with a new ResourceSet.</li>
     * <li><b>openView(CDOBranch branch, long timeStamp):</b> Opens a read-only view targeting the specified branch and timestamp with a new ResourceSet.</li>
     * <li><b>openView(ResourceSet resourceSet):</b> Opens a read-only view targeting the head of the main branch with the specified ResourceSet.</li>
     * <li><b>openView(CDOBranch branch, ResourceSet resourceSet):</b> Opens a read-only view targeting the head of the specified branch with the specified ResourceSet.</li>
     * <li><b>openView(CDOBranch branch, long timeStamp, ResourceSet resourceSet):</b> Opens a read-only view targeting the specified branch and timestamp with the specified ResourceSet.</li>
     * <li><b>openTransaction():</b> Opens a transaction targeting the head of the main branch with a new ResourceSet.</li>
     * <li><b>openTransaction(CDOBranch branch):</b> Opens a transaction targeting the head of the specified branch with a new ResourceSet.</li>
     * <li><b>openTransaction(ResourceSet resourceSet):</b> Opens a transaction targeting the head of the main branch with the specified ResourceSet.</li>
     * <li><b>openTransaction(CDOBranch branch, ResourceSet resourceSet):</b> Opens a transaction targeting the head of the specified branch with the specified ResourceSet.</li>
     * </ul>
     * <p>
     */
    public class OpeningViewsAndTransactions
    {
      /**
       * @snip
       */
      @SuppressWarnings("deprecation")
      public void openingViewsAndTransactions(CDOSession session, ResourceSet resourceSet2)
      {
        // Open a read-only view targeting the head of the main branch with a new ResourceSet.
        CDOView view1 = session.openView();
        ResourceSet resourceSet1 = view1.getResourceSet();
        Assert.isNotNull(resourceSet1);

        // Open a read-only view targeting a specific branch and timestamp with an existing ResourceSet.
        CDOBranch branch = session.getBranchManager().getMainBranch().getBranch("feature");
        long timeStamp = new Date(2021 - 1990, 6, 1).getTime();
        CDOView view2 = session.openView(branch, timeStamp, resourceSet2);
        System.out.println("Resources in resourceSet2: " + resourceSet2.getResources());

        // Open a read-only view targeting the head of the main branch with an existing ResourceSet.
        ResourceSet resourceSet3 = new ResourceSetImpl();
        CDOView view3 = session.openView(resourceSet3);

        // Open a transaction targeting the head of a specific branch with a new ResourceSet.
        CDOTransaction transaction1 = session.openTransaction(branch);

        // Open a transaction targeting the head of the main branch with an existing ResourceSet.
        CDOTransaction transaction2 = session.openTransaction(resourceSet3);

        // Use the views and transactions...

        // Finally, close the views and transactions when done.
        CollectionUtil.close(view1, view2, view3, transaction1, transaction2);
      }
    }

    /**
     * Accessing Open Views
     * <p>
     * A session maintains a collection of all currently open views and transactions. You can access this collection using the
     * {@link CDOSession#getViews() getViews()} method, which returns an unmodifiable list of all open views.
     * This allows you to iterate over the views, inspect their properties, or perform operations on them as needed.
     * Example:
     * {@link #accessOpenViews(CDOSession) AccessOpenViews.java}
     */
    public class AccessingOpenViews
    {
      /**
       * @snip
       */
      public void accessOpenViews(CDOSession session)
      {
        CDOView[] views = session.getViews();
        for (CDOView view : views)
        {
          System.out.println("Open view: " + view.getViewID() //
              + ", Branch: " + view.getBranch().getName() //
              + ", Timestamp: " + view.getTimeStamp());

          if (view instanceof CDOTransaction)
          {
            CDOTransaction transaction = (CDOTransaction)view;
            System.out.println("  is a transaction with " //
                + transaction.getDirtyObjects().size() + " dirty objects.");
          }
        }
      }
    }

    /**
     * Finding Views by ID
     * <p>
     * Each view in a session is assigned a unique integer ID. You can retrieve a view by its ID using the
     * {@link CDOSession#getView(int) getView(int viewID)} method. This is useful for
     * managing multiple views or for integrating with external systems that track view identifiers.
     * Example:
     * {@link #findViewByID(CDOSession, int) FindViewByID.java}
     */
    public class FindingViewsByID
    {
      /**
       * @snip
       */
      public void findViewByID(CDOSession session, int viewID)
      {
        CDOView view = session.getView(viewID);
        if (view != null)
        {
          System.out.println("Found view with ID: " + viewID);
        }
        else
        {
          System.out.println("No view found with ID: " + viewID);
        }
      }
    }

    /**
     * Listening for View-Related Events
     * <p>
     * A session is an {@link IContainer IContainer&lt;CDOView&gt;} and, hence, emits events when views are opened or closed.
     * You can listen for these events by registering an
     * {@link IContainerEvent} listener with the session. This allows your
     * application to react to changes in the set of open views, such as updating UI components or managing resources.
     * Example:
     * {@link #addViewsChangedListener(CDOSession) AddViewsChangedListener.java}
     */
    public class ListeningForViewEvents
    {
      /**
       * @snip
       */
      public void addViewsChangedListener(CDOSession session)
      {
        session.addListener(new ContainerEventAdapter<CDOView>()
        {
          @Override
          protected void onAdded(IContainer<CDOView> container, CDOView view)
          {
            System.out.println("View opened: " + view);
          }

          @Override
          protected void onRemoved(IContainer<CDOView> container, CDOView view)
          {
            System.out.println("View closed: " + view);
          }
        });
      }
    }

    /**
     * Closing Views
     * <p>
     * It is important to close views when they are no longer needed to free up resources and avoid memory leaks.
     * You can close a view by calling its {@link CDOView#close() close()} method.
     * Example:
     * {@link #closeView(CDOView) CloseView.java}
     */
    public class ClosingViews
    {
      /**
       * @snip
       */
      public void closeView(CDOView view)
      {
        view.close();
        System.out.println("View closed.");
      }
    }
  }

  /**
   * Authorizable Operations
   * <p>
   * CDO's main focus is on modeling and model management. From this perspective, models are very much like normal <b>data</b>,
   * and CDO provides a rich set of features for working with model data, including access control and permissions.
   * However, it also includes a basic
   * authorization framework that allows you to perform certain <b>operations</b> only if you have the necessary permissions.
   * An authorizable operation can be associated with various actions within CDO, such as opening a session,
   * creating a transaction, or performing specific repository operations.
   * <p>
   * The {@link AuthorizableOperation} interface represents an operation that can be authorized.
   * Each operation has a unique name and may include additional parameters that provide context for the authorization decision.
   * An instance of AuthorizableOperation is typically created by the client application and then passed to the local session
   * for authorization. The session checks whether the current user has the necessary permissions to perform the operation
   * and returns a veto string if the operation is not authorized. If the operation is authorized, the veto string is <code>null</code>.
   * <p>
   * Here is an example of how to create and authorize an operation:
   * {@link AuthorizableOperations#authorizeOperation(CDOSession) AuthorizeOperation.java}
   * <p>
   * Note that the actual enforcement of authorization is first performed on the client side for efficiency.
   * For this purpose, the session consults a chain of {@link CDOOperationAuthorizer local authorizers} that can be registered with the session.
   * Each authorizer can approve or veto the operation based on its own criteria. It can also modify the operation by adding or changing parameters.
   * If none of the local authorizers vetoes the operation, it is sent to the server for final authorization.
   * The server performs its own authorization checks, which may involve consulting server-side authorizers or access control lists.
   * If the server vetoes the operation, the client is notified of the veto.
   * <p>
   * For more information about authorizable operations, refer to the {@link AuthorizableOperation} interface.
   * <p>
   * In an RCP application, you often want to hide or disable UI elements that trigger operations the user is not authorized to perform.
   * And often, these operations are associated with a selected {@link EObject} in a viewer. In this case, you can use the
   * {@link ContextOperationAuthorization} class to create and authorize multiple operations based on the current selection context.
   * This class simplifies the process of checking authorization for operations related to specific model elements.
   * Here is an example of how to use ContextOperationAuthorization:
   * {@link AuthorizableOperations#authorizeContextOperation(StructuredViewer) AuthorizeContextOperation.java}
   */
  public class AuthorizableOperations
  {
    /**
     * @snip
     */
    public void authorizeOperation(CDOSession session)
    {
      AuthorizableOperation firstMove = AuthorizableOperation //
          .builder("my.chess.game.MoveChessman") //
          .parameter("from", "e2") //
          .parameter("to", "e4") //
          .build();

      String[] veto = session.authorizeOperations(firstMove);
      if (veto[0] == null)
      {
        // Perform the operation...
      }
      else
      {
        System.out.println("Operation not authorized: " + veto[0]);
      }
    }

    /**
     * @snip
     */
    public void authorizeContextOperation(StructuredViewer viewer)
    {
      EObject context = (EObject)viewer.getStructuredSelection().getFirstElement();

      ContextOperationAuthorization authorization = new ContextOperationAuthorization(context //
          , "my.chess.game.MoveChessman" //
          , "my.chess.game.GiveUp");

      if (authorization.isGranted("my.chess.game.GiveUp"))
      {
        System.out.println("User is authorized to give up.");
      }
      else
      {
        String veto = authorization.getVeto("my.chess.game.GiveUp");
        System.out.println("User is not authorized to give up: " + veto);
      }
    }
  }

  /**
   * Client and Server Entities
   * <p>
   * A CDO repository can make arbitrary information available to client sessions in the form of entities.
   * Entities have a {@link Entity#name() name} and are contained in a {@link Entity#namespace() namespace}.
   * The namespace and name of an entity together form its {@link Entity#id() unique identifier}.
   * The associated information is represented by {@link Entity#properties() key-value pairs}, where both keys and values are strings.
   * The information is usually static and does not change over the lifetime of a session.
   * <p>
   * Entities are read-only and cannot be modified by client sessions.
   * They are typically used to convey configuration settings, metadata, or other
   * information that clients may need to interact with the repository effectively.
   * For example, a repository might provide entities that describe supported features,
   * data formats, or integration points with other systems. A repository can store and manage any number of entities.
   * <p>
   * <b>Client entities</b> are a special subset of those entities. They are loaded
   * when a session is opened and remain available for the lifetime of the session. The repository operator can
   * specify which entities are client entities and what information they contain.
   * <p>
   * Client sessions can access client entities using the {@link CDOSession#clientEntities() clientEntities()} method.
   * Calling this method returns a map of all client entities available in the connected repository. You can look up
   * a specific entity by its unique identifier using the map's get() method. For example:
   * {@link #accessClientEntities(CDOSession) AccessClientEntities.java}
   * <p>
   * Non-client entities are not automatically loaded when a session is opened. Instead, they can be accessed
   * using the {@link CDOSession#requestEntities(String, String...) requestEntities(String namespace, String... names)}
   * method. This method allows you to specify a namespace and one or more entity names to retrieve.
   * The method returns a map of the requested entities that exist in the repository. Note that the map keys are the
   * entity names, not their unique identifiers. If an entity with the specified name does not exist in the given
   * namespace, it is simply not included in the returned map.
   * Here is an example of how to request and access specific server entities:
   * {@link #accessServerEntities(CDOSession) AccessServerEntities.java}
   * <p>
   * For more information about entities, refer to the {@link Entity} interface.
   */
  public class ClientAndServerEntities
  {
    /**
     * @snip
     */
    public void accessClientEntities(CDOSession session)
    {
      Entity entity = session.clientEntities().get("namespace" + Entity.NAME_SEPARATOR + "name");
      if (entity != null)
      {
        String value = entity.property("key");
        System.out.println("Property value: " + value);
      }
      else
      {
        // Handle the case where the entity is not found...
      }
    }

    /**
     * @snip
     */
    public void accessServerEntities(CDOSession session)
    {
      Map<String, Entity> entities = session.requestEntities("namespace", "name1", "name2");

      Entity entity1 = entities.get("name1");
      if (entity1 != null)
      {
        System.out.println("Entity 1: " + entity1.property("my-key"));
      }

      Entity entity2 = entities.get("name2");
      if (entity2 != null)
      {
        System.out.println("Entity 2: " + entity2.property("my-key"));
      }
    }
  }

  /**
   * Session Facilities
   * <p>
   * Sessions provide access to several facilities that manage branches, packages, revisions, and more. Each facility
   * offers specialized APIs for advanced repository operations.
   */
  public class SessionFacilities
  {
    /**
     * Repository Info
     * <p>
     * Access metadata and status information about the connected repository, including version, capabilities, and
     * health. The repository info is available as soon as the session is opened.
     * <p>
     * The {@code CDORepositoryInfo} interface provides access to repository metadata and status. It extends
     * {@code CDOCommonRepository}, so it inherits all of its methods. Key methods include:
     * <p>
     * <table border="1" cellspacing="0" cellpadding="4">
     *   <tr><th>Method</th><th>Return Type</th><th>Description</th></tr>
     *   <tr><td>{@link CDORepositoryInfo#getSession() getSession()}</td><td>{@link CDOSession}</td><td>Returns the session.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getName() getName()}</td><td><code>String</code></td><td>Returns the repository name.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getUUID() getUUID()}</td><td><code>String</code></td><td>Returns the repository UUID.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getType() getType()}</td><td>{@link Type}</td><td>Returns the repository type (e.g., MASTER, BACKUP, CLONE).</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getState() getState()}</td><td>{@link State}</td><td>Returns the current repository state (e.g., ONLINE, OFFLINE, SYNCING).</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getCreationTime() getCreationTime()}</td><td><code>long</code></td><td>Returns the creation time.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getStoreType() getStoreType()}</td><td><code>String</code></td><td>Returns the store type (e.g., "db").</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getObjectIDTypes() getObjectIDTypes()}</td><td>{@link Set}&lt;{@link CDOID.ObjectType}&gt;</td><td>Returns supported object ID types.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getIDGenerationLocation() getIDGenerationLocation()}</td><td>{@link IDGenerationLocation}</td><td>Returns where object IDs are generated.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getLobDigestAlgorithm() getLobDigestAlgorithm()}</td><td><code>String</code></td><td>Returns the LOB digest algorithm.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getCommitInfoStorage() getCommitInfoStorage()}</td><td>{@link CommitInfoStorage}</td><td>Returns the commit info storage type.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#getRootResourceID() getRootResourceID()}</td><td>{@link CDOID}</td><td>Returns the root resource ID.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#isAuthenticating() isAuthenticating()}</td><td><code>boolean</code></td><td>Returns whether authentication is required.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#isSupportingLoginPeeks() isSupportingLoginPeeks()}</td><td><code>boolean</code></td><td>Returns whether login peeking is supported.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#isSupportingAudits() isSupportingAudits()}</td><td><code>boolean</code></td><td>Returns whether audit views are supported.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#isSupportingBranches() isSupportingBranches()}</td><td><code>boolean</code></td><td>Returns whether branching is supported.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#isSupportingUnits() isSupportingUnits()}</td><td><code>boolean</code></td><td>Returns whether units are supported.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#isSerializingCommits() isSerializingCommits()}</td><td><code>boolean</code></td><td>Returns whether commits are serialized.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#isEnsuringReferentialIntegrity() isEnsuringReferentialIntegrity()}</td><td><code>boolean</code></td><td>Returns whether referential integrity is enforced.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#isAuthorizingOperations() isAuthorizingOperations()}</td><td><code>boolean</code></td><td>Returns whether operations are authorized.</td></tr>
     *   <tr><td>{@link CDOCommonRepository#waitWhileInitial(IProgressMonitor) waitWhileInitial(IProgressMonitor)}</td><td><code>boolean</code></td><td>Waits while the repository is initializing.</td></tr>
     *   <tr><td>{@link CDOTimeProvider#getTimeStamp() getTimeStamp()}</td><td><code>long</code></td><td>Returns the current repository time.</td></tr>
     *   <tr><td>{@link CDORepositoryInfo#getTimeStamp(boolean) getTimeStamp(boolean)}</td><td><code>long</code></td><td>Returns the current repository time, optionally refreshing it from the server.</td></tr>
     *   <tr><td><code>IAdaptable.getAdapter(Class&lt;T&gt;)</code></td><td><code>&lt;T&gt; T</code></td><td>Returns an associated adapter object.</td></tr>
     * </table>
     * These methods allow you to query all relevant metadata and capabilities of the repository. In addition a repository
     * can make arbitrary information available to client sessions in the form of entities. For more information about entities, refer to
     * {@link ClientAndServerEntities}.
     * <p>
     * The {@code CDORepositoryInfo} (via {@code CDOCommonRepository}) can fire two important events:
     * <ul>
     *   <li>{@link TypeChangedEvent} – Fired when the repository type changes, for example in a fail-over cluster.</li>
     *   <li>{@link StateChangedEvent} – Fired when the repository state changes, for example in a replicating deployment.</li>
     * </ul>
     * To receive these events, register an event listener with the {@link CDORepositoryInfo#getSession() session} of the repository info object.
     * This allows your application to react to changes in the repository's type or state, such as updating the UI or
     * triggering fail-over logic:
     * <p>
     * {@link #addRepositoryInfoListener(CDOSession) AddRepositoryInfoListener.java}
     * <p>
     */
    public class RepositoryInfo
    {
      /**
       * @snip
       */
      public void addRepositoryInfoListener(CDOSession session)
      {
        session.addListener(event -> {
          if (event instanceof TypeChangedEvent)
          {
            // Handle repository type change.
            handleTypeChanged(((TypeChangedEvent)event).getNewType());
          }
          else if (event instanceof StateChangedEvent)
          {
            // Handle repository state change.
            handleStateChanged(((StateChangedEvent)event).getNewState());
          }
        });
      }

      private void handleTypeChanged(Type newType)
      {
      }

      private void handleStateChanged(State newState)
      {
      }
    }

    /**
     * Package Registry
     * <p>
     * The package registry manages EMF {@link EPackage packages} known to the session. CDO's package registry is a sub-type of EMF's
     * package registry. It adds support for dynamic package loading from the repository and ensures that packages are
     * available when needed. To achieve this, the package registry can fetch packages from the repository on demand.
     * This is particularly useful when working with models that reference packages not yet registered in the client.
     * <p>
     * The CDO package registry is automatically available in every session and can be accessed using the
     * {@link CDOSession#getPackageRegistry() getPackageRegistry()} method. You can use the package registry to
     * register new packages, look up existing packages, and query cached sub-type information about all registered
     * {@link EClass EClasses}.
     * <p>
     * Please note that EMF packages may contain nested packages and that it is not possible to transfer a package
     * between clients and servers without also transferring all its nested packages. Therefore, CDO introduces the
     * concept of <b>package units</b>. A package unit represents a <b>tree</b> of related packages that can be transferred as a
     * single unit. For each package in the tree, the package unit contains a {@link CDOPackageInfo} object that
     * provides metadata about the package, such as its name and namespace URI. The package info is also able to resolve
     * the actual {@link EPackage package} when needed. Once a package is resolved, it is registered in the package registry and
     * the package info is added to the {@link EPackage#eAdapters() eAdapters()} of the package for easy lookup.
     * <p>
     * For more information about package units, refer to {@link CDOPackageRegistry}, {@link CDOPackageUnit}, and {@link CDOPackageInfo}.
     */
    public class PackageRegistry
    {
    }

    /**
     * Revision Manager
     * <p>
     * The {@link CDORepositoryManager revision manager} loads, caches, and provides object {@link CDORevision revisions}.
     * It is automatically available in every session and can be accessed using the
     * {@link CDOSession#getRevisionManager() getRevisionManager()} method. The revision manager
     * supports efficient retrieval of revisions by their {@link CDOID ID} and either their
     * {@link CDOBranchPoint branch point} or version. It also supports bulk loading of revisions
     * and pre-fetching of related revisions to optimize performance.
     * <p>
     * Note that the explicit use of revision management is usually not necessary when working with views and transactions.
     * Views and transactions automatically manage revisions for the objects they contain.
     * But the revision manager is inevitable when you need to access revisions directly, for example when
     * implementing version control tools, custom synchronization, or replication logic.
     * <p>
     * The combination of ID, branch, and version uniquely identifies a revision within the repository. Hence the
     * {@link CDORevisionManager#getRevisionByVersion(CDOID, CDOBranchVersion, int, boolean) getRevisionByVersion()} method
     * can be used to retrieve a specific revision:
     * {@link RevisionManager#getRevisionByVersion(CDOSession) GetRevisionByVersion.java}
     * <p>
     * In addition, the revision manager provides methods to retrieve revisions by their branch point. That is,
     * given an ID and a branch point (branch and time), you can retrieve the revision that was current
     * in the specified branch at the specified time. This is useful for accessing historical states of model objects without
     * needing to know their exact version numbers and without opening an audit view. Here is an example:
     * {@link RevisionManager#getRevision(CDOSession) GetRevision.java}
     * <p>
     * The revision manager also supports bulk loading of revisions using the
     * {@link CDORevisionManager#getRevisions(List, CDOBranchPoint, int, int, boolean) getRevisions()} method. Here is an example:
     * {@link RevisionManager#getMultipleRevisions(CDOSession, CDORevision) GetMultipleRevisions.java}
     * <p>
     * The above methods assume that you know the IDs of the revisions you want to retrieve.
     * But sometimes you may want to find revisions based on their type, branch, and time.
     * The revision manager provides the
     * {@link CDORevisionManager#handleRevisions(EClass, CDOBranch, boolean, long, boolean, CDORevisionHandler) handleRevisions()}
     * method for this purpose. Here is an example:
     * {@link RevisionManager#handleRevisions(CDOSession) HandleRevisions.java}
     * <p>
     * Sometimes you may want to notified when revisions are loaded into the local cache. The following example shows how to register
     * a listener for this purpose:
     * {@link RevisionManager#listenToRevisionLoading(CDOSession) ListenToRevisionLoading.java}
     * <p>
     * For more information about revision management, refer to the {@link CDORevisionManager} interface.
     */
    public class RevisionManager
    {
      /**
       * @snip
       */
      public void getRevisionByVersion(CDOSession session)
      {
        CDOID rootResourceID = session.getRepositoryInfo().getRootResourceID();
        CDOBranch mainBranch = session.getBranchManager().getMainBranch();
        CDOBranchVersion firstVersion = mainBranch.getVersion(CDOBranchVersion.FIRST_VERSION);
        boolean loadOnDemand = true; // Set to false to only consider locally cached revisions.

        CDORevisionManager revisionManager = session.getRevisionManager();
        CDORevision firstRevision = revisionManager.getRevisionByVersion(rootResourceID, firstVersion, CDORevision.UNCHUNKED, loadOnDemand);
        System.out.println("First revision found: " + firstRevision);
      }

      /**
       * @snip
       */
      public void getRevision(CDOSession session)
      {
        CDOID rootResourceID = session.getRepositoryInfo().getRootResourceID();
        CDOBranch mainBranch = session.getBranchManager().getMainBranch();
        CDOBranchPoint tenSecondsAgo = mainBranch.getPoint(System.currentTimeMillis() - 10000); // 10 seconds ago
        boolean loadOnDemand = true; // Set to false to only consider locally cached revisions.

        CDORevisionManager revisionManager = session.getRevisionManager();
        CDORevision oldRevision = revisionManager.getRevision(rootResourceID, tenSecondsAgo, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, loadOnDemand);
        System.out.println("Old revision found: " + oldRevision);
      }

      /**
       * @snip
       */
      public void getMultipleRevisions(CDOSession session, CDORevision rootResource)
      {
        CDOList list = rootResource.data().getListOrNull(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS);
        if (list != null)
        {
          List<CDOID> ids = list.stream() //
              .filter(CDOID.class::isInstance) //
              .map(CDOID.class::cast) //
              .collect(Collectors.toList()); // Same as CDOIDUtil.getCDOIDs(list);

          CDORevisionManager revisionManager = session.getRevisionManager();
          CDOBranchPoint head = session.getBranchManager().getMainBranch().getHead();

          // Collect available revisions from the local cache and load missing ones from the repository in one batch.
          List<CDORevision> revisions = revisionManager.getRevisions(ids, head, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
          System.out.println("Revisions found: " + revisions);
        }
      }

      /**
       * @snip
       */
      public void handleRevisions(CDOSession session)
      {
        // Find all revisions of type CDOResourceFolder. Use null to find revisions of any type.
        EClass eClass = EresourcePackage.Literals.CDO_RESOURCE_FOLDER;

        CDOBranch mainBranch = session.getBranchManager().getMainBranch();
        boolean exactBranch = true; // Use false to also consider sub-branches.

        long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE; // Find latest revisions.
        boolean exactTime = false; // Use true to only consider revisions with the exact timestamp.

        session.getRevisionManager().handleRevisions(eClass, mainBranch, exactBranch, timeStamp, exactTime, //
            revision -> {
              System.out.println("Resource Folder found: " + revision);
              return exactBranch; // Return true to continue processing more revisions.
            });
      }

      /**
       * @snip
       */
      public void listenToRevisionLoading(CDOSession session)
      {
        // Keep strong references to the loaded revisions to prevent them from being garbage collected.
        Set<CDORevision> strongReferences = new HashSet<>();

        session.getRevisionManager().addListener(event -> {
          if (event instanceof CDORevisionsLoadedEvent)
          {
            CDORevisionsLoadedEvent e = (CDORevisionsLoadedEvent)event;

            // Add all revisions that were requested to the strong reference set.
            strongReferences.addAll(e.getPrimaryLoadedRevisions());

            // Add additional revisions that were loaded because of prefetching.
            strongReferences.addAll(e.getAdditionalLoadedRevisions());
          }
        });
      }

      /**
       * Working with Revisions
       * <p>
       * A revision is an immutable object that represents a specific state of a model object at a given period in time.
       * A new revision is always created and stored when a new model object is created or an existing model object is modified.
       * Revisions are never modified after they are created. Instead, new revisions are created to represent
       * new states of model objects. This immutability ensures that historical states of model objects are preserved
       * and can be accessed at any time (if the repository mode is at least {@link Mode#AUDITING AUDITING}).
       * <p>
       * Revisions are identified by:
       * <ol>
       * <li> their {@link CDOID ID}, which uniquely refers to the model object they represent,
       * <li> their {@link CDOBranch branch}, which specifies the branch in which the revision was created, and
       * <li> their {@link CDORevision#getVersion() version}, which indicates the sequence of the revision within its branch.
       * </ol>
       * <p>
       * The CDORevision interface provides methods to access technical details about a revision, such as its {@link EClass},
       * ID, branch, version, and timestamps. The actual model data of a revision, including attribute values and references,
       * is accessed through the {@link CDORevisionData} interface, which can be obtained by calling the {@link CDORevision#data()}
       * method. Here is an example of how to access revision data:
       * {@link #accessRevisionData(CDORevision) AccessRevisionData.java}
       * <p>
       * There are other methods available on the CDORevisionData interface, especially for working with
       * multi-valued features, such as {@link CDORevisionData#size(EStructuralFeature) size()},
       * {@link CDORevisionData#isEmpty(EStructuralFeature) isEmpty}, {@link CDORevisionData#contains(EStructuralFeature, Object) contains()},
       * {@link CDORevisionData#indexOf(EStructuralFeature, Object) indexOf()}, and so on.
       * <p>
       * In addition to accessing revision data, you can also compare two revisions to determine the differences between them.
       * The {@link CDORevision#compare(CDORevision) compare()} method computes the delta between two revisions
       * and returns a {@link CDORevisionDelta} object that describes the changes. Here is an example of how to compare two revisions:
       * {@link #compareRevisions(CDORevision, CDORevision) CompareRevisions.java}
       * <p>
       * For more information about revisions, refer to the {@link CDORevision} and {@link CDORevisionData} interfaces.
       */
      public class WorkingWithRevisions
      {
        /**
         * @snip
         */
        public void accessRevisionData(CDORevision revision)
        {
          EClass eClass = revision.getEClass();
          CDORevisionData data = revision.data();

          for (EStructuralFeature feature : eClass.getEAllStructuralFeatures())
          {
            Object value = data.getValue(feature);
            System.out.println("Feature: " + feature.getName() + " = " + value);
          }
        }

        /**
         * @snip
         */
        public void compareRevisions(CDORevision revision1, CDORevision revision2)
        {
          // Compute the delta between two revisions, relative to revision1.
          CDORevisionDelta changes = revision2.compare(revision1);

          // Print the changes between the two revisions.
          changes.getFeatureDeltas().forEach(featureDelta -> {
            EStructuralFeature feature = featureDelta.getFeature();
            System.out.println("Feature changed: " + feature.getName());

            if (featureDelta instanceof CDOSetFeatureDelta)
            {
              CDOSetFeatureDelta setFeatureDelta = (CDOSetFeatureDelta)featureDelta;
              System.out.println("  Old value: " + setFeatureDelta.getOldValue());
              System.out.println("  New value: " + setFeatureDelta.getValue());
            }
            else if (featureDelta instanceof CDOListFeatureDelta)
            {
              CDOListFeatureDelta listFeatureDelta = (CDOListFeatureDelta)featureDelta;

              listFeatureDelta.getListChanges().forEach(listChange -> {
                if (listChange instanceof CDOAddFeatureDelta)
                {
                  CDOAddFeatureDelta addChange = (CDOAddFeatureDelta)listChange;
                  System.out.println("  Added value: " + addChange.getValue() + " at index " + addChange.getIndex());
                }
                else if (listChange instanceof CDORemoveFeatureDelta)
                {
                  CDORemoveFeatureDelta removeChange = (CDORemoveFeatureDelta)listChange;
                  System.out.println("  Removed value: " + removeChange.getValue() + " from index " + removeChange.getIndex());
                }
                else if (listChange instanceof CDOSetFeatureDelta)
                {
                  CDOSetFeatureDelta setChange = (CDOSetFeatureDelta)listChange;
                  System.out.println("  Changed value at index " + setChange.getIndex() + " from " + setChange.getOldValue() + " to " + setChange.getValue());
                }
                else if (listChange instanceof CDOMoveFeatureDelta)
                {
                  CDOMoveFeatureDelta moveChange = (CDOMoveFeatureDelta)listChange;
                  System.out.println(
                      "  Moved value: " + moveChange.getValue() + " from index " + moveChange.getOldPosition() + " to index " + moveChange.getNewPosition());
                }
              });
            }
            else if (featureDelta instanceof CDOUnsetFeatureDelta)
            {
              System.out.println("  Feature was unset.");
            }
            else if (featureDelta instanceof CDOClearFeatureDelta)
            {
              System.out.println("  Feature was cleared.");
            }
            else if (featureDelta instanceof CDOContainerFeatureDelta)
            {
              System.out.println("  Container changed:");
            }
          });

          // Create a copy of revision1 to demonstrate applying the changes.
          CDORevision revision3 = revision1.copy();

          // Apply the changes to revision3 (for demonstration purposes only).
          // Note that revisions are immutable in most cases. This is just to show how to apply changes
          // to a revision, for example, when implementing a custom merge or patching algorithm.
          // In a real application, you would typically not do this.
          changes.applyTo(revision3);

          // Verify that applying the changes results in a revision that is structurally equal to revision2.
          CDORevisionDelta empty = revision3.compare(revision2);
          Assert.isTrue(empty.isEmpty(), "Revisions should be structurally equal after applying changes.");
        }
      }

      /**
       * Pre-fetching Revisions
       * <p>
       * In addition to loading revisions on demand, the revision manager supports pre-fetching of related revisions.
       * When a revision is loaded, the revision manager can automatically load additional revisions that are
       * related to the requested revision. This is particularly useful when you know that you will need
       * related revisions soon after loading a specific revision. The pre-fetched revisions are loaded from the repository
       * in the same batch as the requested revision, which can significantly reduce the number of round-trips to the server
       * and improve performance. They remain in the local cache and can be accessed later without additional network calls.
       * <p>
       * There are different mechanisms available for pre-fetching related revisions:
       * <ul>
       * <li> The <code>prefetchDepth</code> parameter of the revision manager's loading methods.
       *      This parameter specifies how many levels of contained objects should be pre-fetched.
       *      For example, a prefetch depth of 1 means that all directly contained objects will be pre-fetched,
       *      and a prefetch depth of 2 means that all directly contained objects and their directly contained objects will be pre-fetched, and so on.
       *      The prefetch depth can be set to {@link CDORevision#DEPTH_NONE DEPTH_NONE} (no pre-fetching),
       *      {@link CDORevision#DEPTH_INFINITE DEPTH_INFINITE} (pre-fetch all contained objects recursively),
       *      or any positive integer value.
       * <li> A per-{@link CDOView} pre-fetching policy that defines which related revisions should be pre-fetched
       *      when a revision is loaded. The pre-fetching policy can be implemented using the
       *      {@link CDORevisionPrefetchingPolicy} interface and set on the view using the
       *      {@link CDOView.Options#setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy) setRevisionPrefetchingPolicy()} method.
       *      The pre-fetching policy can define complex rules for pre-fetching related revisions based on
       *      the type of the requested revision, its features, and other criteria. The result of the pre-fetching policy
       *      is a list of {@link CDOID revision IDs} that should be pre-fetched along with the requested revision.
       * <li> When you cannot specify the IDs of the revisions you want to load in advance, you can use a session-wide {@link CDOFetchRuleManager}
       *      to define multiple {@link CDOFetchRule fetch rules} for pre-fetching target revisions of one or more {@link EReference references}
       *      of the requested revisions. The fetch rules are sent to the server whenever revisions are loaded and evaluated there.
       *      The target revisions of the references that match a fetch rule are then pre-fetched and sent back to the client
       *      along with the requested revisions. For more information about fetch rules, refer to the {@link CDOFetchRule} interface.
       * <li> If you don't want to pre-fetch revisions based on dynamic and transient rules, but rather based on permanent partitions
       *      of your model, you can use {@link CDOUnit}s. Units of your model are disjunct (non-overlapping) subtrees within the model,
       *      identified by their root objects. They can be created, opened, and closed using the {@link CDOUnitManager unit manager}
       *      of a {@link CDOView}. Units are very useful when your model, for example, contains multiple independent projects or documents,
       *      and you want to load and work with a <b>complete</b> project or document (including all its contained objects) at once.
       *      For more information about units, refer to {@link Doc04_WorkingWithViews.Units}.
       * <li> And then there is a mighty {@link CDOPrefetcherManager} that can be used to manage the pre-fetching for an entire
       *      {@link ResourceSet}. It automatically creates and manages {@link Prefetcher pre-fetchers} for all {@link CDOView views}
       *      that are associated with the resource set. Each pre-fetcher asynchronously pre-fetches the revisions of all objects of its view.
       *      The pre-fetching is performed in the background and does not block the main thread of the application. A pre-fetcher
       *      listens to changes of the view's target branch point and automatically adjusts its pre-fetching accordingly.
       *      A pre-fetcher can also be configured to pre-fetch {@link CDOPrefetcherManager#isPrefetchLockStates() lock states}
       *      of objects in addition to their revisions.
       * </ul>
       */
      public class PrefetchingRevisions
      {
        /**
         * @snip
         */
        public void accessRevisionData(CDORevision revision)
        {
          EClass eClass = revision.getEClass();
          CDORevisionData data = revision.data();

          for (EStructuralFeature feature : eClass.getEAllStructuralFeatures())
          {
            Object value = data.getValue(feature);
            System.out.println("Feature: " + feature.getName() + " = " + value);
          }
        }
      }
    }

    /**
     * Branch Manager
     * <p>
     * Manage {@link CDOBranch branches} and {@link CDOBranchTag tags} within the repository. Branches enable parallel
     * development and versioning. Tags provide meaningful names for specific {@link CDOBranchPoint points} in a branch's history.
     * <p>
     * The branch manager is automatically available in every session and can be accessed using the
     * {@link CDOSession#getBranchManager() getBranchManager()} method. The branches and tags managed by the branch manager
     * are specific to the connected repository and are not shared between different repositories or even between different sessions within
     * the same repository. Not all branches and tags may be immediately available in the branch manager.
     * Branches and tags are loaded on demand when they are accessed for the first time.
     * <p>
     * Note that only the main branch is guaranteed to be available for all {@link CDOCommonRepository.Mode repository modes}.
     * Sub-branches are only supported in {@link CDOCommonRepository.Mode#BRANCHING branching}. Tags are only supported in
     * {@link CDOCommonRepository.Mode#AUDITING auditing}.
     * <p>
     * For more information about branch management, refer to the {@link CDOBranchManager} interface.
     */
    public class BranchManager
    {
      /**
       * Working with Branches
       * <p>
       * Branches are organized in a tree structure, starting from the {@link CDOBranchManager#getMainBranch() main branch}. In addition to
       * their immutable technical IDs, branches have user-friendly names, which can change over time. Due to the tree structure
       * of branches, a branch name is only unique within its parent branch. To uniquely identify a branch, you need to
       * specify its {@link CDOBranch#getPathName() full path}, which includes the names of all its ancestor branches.
       * <p>
       * Here is an example of how to access branches using the branch manager:
       * {@link #getBranch(CDOSession) GetBranch.java}
       * <p>
       * Here is an example of how to retrieve all sub-branches of a given branch:
       * {@link #getSubBranches(CDOBranchManager) GetSubBranches.java}
       * <p>
       * Here is an example of how to create new branches:
       * {@link #createBranch(CDOBranchManager) CreateBranch.java}
       * <p>
       * Here is an example of how to listen for branch changes:
       * {@link #listenToBranchChanges(CDOBranchManager) ListenToBranchChanges.java}
       * <p>
       * For more information about branches, refer to the {@link CDOBranch} interface.
       */
      public class WorkingWithBranches
      {
        /**
         * @snip
         */
        public void getBranch(CDOSession session)
        {
          CDOBranchManager branchManager = session.getBranchManager();

          // Get the main branch using its constant ID.
          CDOBranch mainBranch = branchManager.getBranch(CDOBranch.MAIN_BRANCH_ID);
          Assert.isTrue(mainBranch == branchManager.getMainBranch());

          // Get a branch from the manager with the full path.
          CDOBranch featureBranch = branchManager.getBranch("MAIN/features/awesome-feature");
          System.out.println("Feature branch: " + featureBranch);

          // Alternatively, navigate the branch tree starting from the main branch.
          featureBranch = mainBranch.getBranch("features").getBranch("awesome-feature");

          // List all direct sub-branches of a branch.
          for (CDOBranch subBranch : mainBranch.getBranch("features").getBranches())
          {
            System.out.println("Sub-branch of 'features': " + subBranch);
          }
        }

        /**
         * @snip
         */
        public void getSubBranches(CDOBranchManager branchManager)
        {
          // Get all direct and indirect sub-branches of the branch with ID 815.
          LinkedHashSet<CDOBranch> subBranches = branchManager.getBranches(815);
          subBranches.forEach(System.out::println);
        }

        /**
         * @snip
         */
        public void createBranch(CDOBranchManager branchManager)
        {
          CDOBranch mainBranch = branchManager.getMainBranch();
          CDOBranch features = mainBranch.createBranch("features");
          CDOBranch awesomeFeature = features.createBranch("awesome-feature");

          // Outputs "MAIN/features/awesome-feature".
          System.out.println(awesomeFeature.getPathName());
        }

        /**
         * @snip
         */
        public void listenToBranchChanges(CDOBranchManager branchManager)
        {
          branchManager.addListener(new CDOBranchManager.EventAdapter()
          {
            @Override
            protected void onBranchCreated(CDOBranch branch)
            {
              // Handle branch creation.
            }

            @Override
            protected void onBranchRenamed(CDOBranch branch)
            {
              // Handle branch renaming.
            }

            @Override
            protected void onBranchesDeleted(CDOBranch rootBranch, int[] branchIDs)
            {
              // Handle branch deletion.
            }
          });
        }
      }

      /**
       * Working with Tags
       * <p>
       * Tags are used to mark specific points in a branch's history with meaningful names. Unlike branches, tags do not
       * have a hierarchical structure. A tag name is unique within the entire repository. Tags
       * refer to a specific {@link CDOBranchPoint branch point}, which consists of a branch and a timestamp.
       * Tags are often used to mark releases, milestones, or other significant events in the development process.
       * Both the name and the target branch point of a tag can change over time, also referred to as "renaming tags" and "moving tags".
       * <p>
       * Here is an example of how to access tags using the branch manager:
       * {@link #getTag(CDOSession) GetTag.java}
       * <p>
       * Here is an example of how to retrieve all tags associated with a specific branch:
       * {@link #getTagsOfBranch(CDOBranchManager) GetTagsOfBranch.java}
       * <p>
       * Here is an example of how to create new tags:
       * {@link #createTag(CDOBranchManager) CreateTag.java}
       * <p>
       * Whether a tag created, renamed, moved, or deleted in your session or in another session,
       * the tag list of the branch manager can notify you about these changes.
       * Here is an example of how to listen for tag changes:
       * {@link #listenToTagChanges(CDOBranchManager) ListenToTagChanges.java}
       * <p>
       * For more information about tags, refer to the {@link CDOBranchTag} and {@link CDOTagList} interfaces.
       */
      public class WorkingWithTags
      {
        /**
         * @snip
         */
        public void getTag(CDOSession session)
        {
          CDOBranchManager branchManager = session.getBranchManager();
          CDOBranchTag relase_1_0 = branchManager.getTag("v1.0");

          CDOView auditView = session.openView(relase_1_0);
          // Use the audit view which reflects the state of the repository at the time of release v1.0.

          auditView.close();
        }

        /**
         * @snip
         */
        public void getTagsOfBranch(CDOBranchManager branchManager)
        {
          CDOTagList tagList = branchManager.getTagList();
          CDOBranchTag[] mainBranchTags = tagList.getTags(branchManager.getMainBranch());

          for (CDOBranchTag tag : mainBranchTags)
          {
            System.out.println("Tag on main branch: " + tag.getName() + " -> " + tag.getTimeStamp());
          }
        }

        /**
         * @snip
         */
        public void createTag(CDOBranchManager branchManager)
        {
          CDOBranch mainBranch = branchManager.getMainBranch();
          CDOBranchTag tag = branchManager.createTag("v2.0", mainBranch.getHead());
          System.out.println("Created tag: " + tag);
        }

        /**
         * @snip
         */
        public void listenToTagChanges(CDOBranchManager branchManager)
        {
          branchManager.getTagList().addListener(new ContainerEventAdapter<CDOBranchTag>()
          {
            @Override
            protected void onAdded(IContainer<CDOBranchTag> tagList, CDOBranchTag tag)
            {
              // Handle tag creation.
            }

            @Override
            protected void onRemoved(IContainer<CDOBranchTag> tagList, CDOBranchTag tag)
            {
              // Handle tag deletion.
            }

            @Override
            protected void notifyOtherEvent(IEvent event)
            {
              if (event instanceof TagRenamedEvent)
              {
                // Handle tag renaming.
              }
              else if (event instanceof TagMovedEvent)
              {
                // Handle tag moving.
              }
            }
          });
        }
      }
    }

    /**
     * Fetch Rule Manager
     * <p>
     * Control how data is fetched from the repository. Learn to define fetch rules for efficient data loading and
     * minimize network overhead.
     * <p>
     * You can use a session-wide {@link CDOFetchRuleManager}
     * to define multiple {@link CDOFetchRule fetch rules} for pre-fetching target revisions of one or more {@link EReference references}
     * of the requested revisions. The fetch rules are sent to the server whenever revisions are loaded and evaluated there.
     * The target revisions of the references that match a fetch rule are then pre-fetched and sent back to the client
     * along with the requested revisions.
     * <p>
     * A fetch rule consists of:
     * <ul>
     * <li> a target {@link EClass class} that specifies the type of the revisions for which the fetch rule applies,
     * <li> one or more {@link EReference references} of the target class whose target revisions should be pre-fetched, and
     * </ul>
     * <p>
     * Here is an example of how to create a custom fetch rule manager that pre-fetches the contents of a specific resource
     * whenever that resource is loaded:
     * {@link #useCustomFetchRuleManager(IConnector, String) UseCustomFetchRuleManager.java}
     * <p>
     * As fetch rules specify what features to pre-fetch, it often makes sense to monitor what features are actually
     * used in your application. This can be achieved by using a {@link CDOFeatureAnalyzer} that is attached to the
     * {@link CDOView.Options#setFeatureAnalyzer(CDOFeatureAnalyzer) view options} of your views and transactions.
     * <p>
     * CDO provides two standard implementations of a combined fetch rule manager and feature analyzer:
     * <ul>
     * <li> {@link CDOUtil#createUIFeatureAnalyzer(long) UIFeatureAnalyzer} – Designed for interactive applications with a user interface.
     *      It tracks feature usage and creates fetch rules based on recent access patterns to optimize data
     *      loading for typical user interactions.
     * <li> {@link CDOUtil#createModelBasedFeatureAnalyzer() ModelBasedFeatureAnalyzer} – Designed for applications that process models in a non-interactive way.
     *     It analyzes the model structure and creates fetch rules based on the containment hierarchy
     *     to ensure that related objects are loaded together. This is particularly useful for batch processing
     *     and model transformations.
     * </ul>
     * <p>
     * Here is an example of how to use the UI feature analyzer in a transaction:
     * {@link #useUIFeatureAnalyzer(IConnector, String) UseUIFeatureAnalyzer.java}
     * <p>
     * See also {@link PrefetchingRevisions} and {@link Doc04_WorkingWithViews.Units}.
     * For more information about fetch rules, refer to the {@link CDOFetchRule} interface.
     */
    public class FetchRuleManager
    {
      /**
       * @snip
       */
      public void useCustomFetchRuleManager(IConnector connector, String repositoryName)
      {
        CDOFetchRuleManager fetchRuleManager = new CDOFetchRuleManager()
        {
          private final CDOID importantResource = CDOIDUtil.createLong(42);

          @Override
          public CDOID getContext()
          {
            return importantResource;
          }

          @Override
          public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
          {
            if (ids.contains(importantResource))
            {
              CDOFetchRule fetchRule = new CDOFetchRule(EresourcePackage.Literals.CDO_RESOURCE);
              fetchRule.addFeature(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS);
              return Collections.singletonList(fetchRule);
            }

            return null;
          }
        };

        CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
        configuration.setConnector(connector);
        configuration.setRepositoryName(repositoryName);
        configuration.setFetchRuleManager(fetchRuleManager);

        CDOSession session = configuration.openNet4jSession();
        // Use the session with the custom fetch rule manager...

        session.close();
      }

      /**
       * @snip
       */
      public void useUIFeatureAnalyzer(IConnector connector, String repositoryName)
      {
        CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
        configuration.setConnector(connector);
        configuration.setRepositoryName(repositoryName);
        configuration.setFetchRuleManager(CDOUtil.createThreadLocalFetchRuleManager());

        CDOSession session = configuration.openNet4jSession();
        CDOTransaction transaction = session.openTransaction();

        // Attach a UI feature analyzer to the transaction, which automatically creates and updates fetch rules
        // based on the features that are accessed through the transaction.
        // The fetch rules are sent to the server whenever revisions are loaded in the context of the transaction.
        // The parameter (400L) specifies the maximum time between two operations on the same context
        // for them to be considered related and thus influence the same fetch rule.
        transaction.options().setFeatureAnalyzer(CDOUtil.createUIFeatureAnalyzer(400L));

        // Use the transaction with the UI feature analyzer...

        session.close(); // Close the session and the transaction.
      }
    }

    /**
     * Commit Info Manager
     * <p>
     * Access commit history and metadata. This facility allows you to query, filter, and analyze commit information for
     * auditing and tracking changes.
     * <p>
     * A {@link CDOCommitInfo} object represents metadata about a single commit operation in the repository.
     * It includes information such as the commit's unique identifier (the timestamp of the commit), the user who made the commit,
     * the branch in which the commit was made, an optional commit message, and an optional {@link CDOCommitData} object, which
     * provides detailed information about the {@link CDOCommitData changes} made in the commit.
     * <p>
     * Whether a repository <b>stores</b> commit information depends on its {@link CDOCommonRepository.Mode mode}. Commit
     * information is only available in {@link CDOCommonRepository.Mode#AUDITING auditing} mode or higher. In
     * addition, the repository must be configured to store commit information, i.e., its
     * {@link IRepository.Props#COMMIT_INFO_STORAGE} property must not be set to {@link CommitInfoStorage#YES} or
     * {@link CommitInfoStorage#WITH_MERGE_SOURCE}.
     * <p>
     * Whether a repository <b>provides</b> commit information with complete {@link CDOCommitData change set data} to a client
     * depends on the passive update mode of the client's {@link CDOSession session}, see {@link PassiveUpdatesAndRefreshing}.
     * <p>
     * The commit info manager is automatically available in every session and can be accessed using the
     * {@link CDOSession#getCommitInfoManager() getCommitInfoManager()} method. The commit info manager
     * supports efficient retrieval of commit information by timestamp, branch, and other criteria.
     * <p>
     * Here is an example of how to retrieve commit information using the commit info manager:
     * {@link #getCommitInfo(CDOSession, CDOSession) GetCommitInfo.java}
     * <p>
     * Here is an example of how to retrieve the last commit info of a specific branch:
     * {@link #getLastCommitInfo(CDOSession) GetLastCommitInfo.java}
     * <p>
     * There are many more methods available on the commit info manager for querying commit history,
     * such as retrieving commits within a specific time range, filtering commits by user or message,
     * and accessing the detailed change data of commits.
     * <p>
     * For more information about commit info management, refer to the {@link CDOCommitInfoManager} interface.
     */
    public class CommitInfoManager
    {
      /**
       * @snip
       */
      public void getCommitInfo(CDOSession session1, CDOSession session2) throws ConcurrentAccessException, CommitException
      {
        // Create and commit a new resource in session1 to generate some commit info.
        CDOTransaction transaction = session1.openTransaction();
        transaction.createResource("/my/resource");
        CDOCommitInfo commit = transaction.commit();

        // The commit ID is the timestamp of the commit.
        long commitID = commit.getTimeStamp();
        System.out.println("Commit ID: " + commitID);

        // Retrieve the commit info in session2 using the commit info manager.
        CDOCommitInfo commitInfo = session2.getCommitInfoManager().getCommitInfo(commitID);

        // Print commit info details.
        System.out.println("Timestamp: " + commitInfo.getTimeStamp());
        System.out.println("Previous TimeStamp: " + commitInfo.getPreviousTimeStamp());
        System.out.println("Branch: " + commitInfo.getBranch());
        System.out.println("User ID: " + commitInfo.getUserID());
        System.out.println("Comment: " + commitInfo.getComment());
        System.out.println("Affected IDs: " + commitInfo.getAffectedIDs());
        System.out.println("New Package Units: " + commitInfo.getNewPackageUnits());
        System.out.println("New Objects: " + commitInfo.getNewObjects());
        System.out.println("Changed Objects: " + commitInfo.getChangedObjects());
        System.out.println("Detached Objects: " + commitInfo.getDetachedObjects());
        System.out.println("Merge Source: " + commitInfo.getMergeSource());
      }

      /**
       * @snip
       */
      public void getLastCommitInfo(CDOSession session)
      {
        CDOBranch mainBranch = session.getBranchManager().getMainBranch();

        // Retrieve the last commit info of the main branch.
        CDOCommitInfo commitInfo = session.getCommitInfoManager().getLastOfBranch(mainBranch);
        System.out.println("Commit Info: " + commitInfo);
      }
    }

    /**
     * User Info Manager
     * <p>
     * If a repository is configured to authenticate users, the authenticated session provides access to just the
     * {@link CDOSession#getUserID() user ID} of the authenticated user. The user info manager allows you to retrieve
     * additional information about users, such as their full name, email address, and other custom attributes.
     * This information can be useful for displaying user-friendly names in the UI, sending notifications,
     * and performing user-specific operations. Technically, the user infos are made available as {@link Entity}s that the repository
     * creates and manages according to its user management configuration. See also {@link ClientAndServerEntities}.
     * <p>
     * User infos that are loaded from the repository are cached in the user info manager to optimize performance
     * and reduce network overhead.
     * <p>
     * The user info manager is automatically available in every session and can be accessed using the
     * {@link CDOSession#getUserInfoManager() getUserInfoManager()} method.
     * <p>
     * Here is an example of how to retrieve user information using the user info manager:
     * {@link #getMultipleUserInfos(CDOSession) GetMultipleUserInfos.java}
     * <p>
     * For more information about user info management, refer to the {@link CDOUserInfoManager} interface.
     */
    public class UserInfoManager
    {
      /**
       * @snip
       */
      public void getMultipleUserInfos(CDOSession session)
      {
        Map<String, Entity> userInfos = //
            session.getUserInfoManager().getUserInfos("john.doe", "jane.smith");

        userInfos.forEach((userID, userInfo) //
        -> System.out.println(userID + ", Groups: " + userInfo.property("groups")));
      }
    }

    /**
     * Remote Session Manager
     * <p>
     * Monitor and interact with other sessions connected to the repository. Useful for collaboration and administrative
     * tasks.
     * <p>
     * In collaborative environments, it is often necessary to monitor other sessions connected to the same repository,
     * exchange messages, and coordinate activities. The Remote Session Manager provides APIs to observe, interact with,
     * and manage remote sessions, enabling features such as notifications, messaging, and presence.
     * <p>
     * The {@link CDORemoteSession} interface represents a session connected to the repository, other than the local session.
     * It provides methods to query session properties, send messages, and subscribe to events.
     * Events such as session connection, disconnection, and message reception are emitted to allow applications to react
     * to changes in remote session state.
     * <p>
     * The {@link CDORemoteSessionManager} manages all remote sessions for a repository. It allows you to enumerate active sessions,
     * subscribe to session events, and send messages to one or more sessions. Events include session addition, removal, and message delivery.
     * To access the remote session manager, use the {@link CDOSession#getRemoteSessionManager() getRemoteSessionManager()} method.
     * <p>
     * Here is an example of how to use the remote session manager to enumerate remote sessions:
     * {@link #enumeratingRemoteSessions(CDOSession) EnumeratingRemoteSessions.java}
     * <p>
     * To opt-in for remote session management, the client's remote session manager must be enabled by <b>subscribing</b> to remote session information.
     * This subscription can be done automatically when you add a listener via {@code addListener()} to the remote session manager.
     * Automatic subscription occurs when the first listener is added and is canceled when the last listener is removed.
     * Subscription can also be forced, by calling {@link CDORemoteSessionManager#setForceSubscription(boolean) setForceSubscription(true)}.
     * Without subscription, the remote session manager will not receive updates about remote sessions.
     * <p>
     * Here is an example of how to subscribe to remote session events using the remote session manager:
     * {@link #subscribing(CDOSession) Subscribing.java}
     */
    public class RemoteSessionManager
    {
      /**
       * @snip
       */
      public void enumeratingRemoteSessions(CDOSession session)
      {
        for (CDORemoteSession remoteSession : session.getRemoteSessionManager().getRemoteSessions())
        {
          System.out.println("Remote session ID: " + remoteSession.getSessionID());
          System.out.println("Remote session user: " + remoteSession.getUserID());
          System.out.println("Remote session subscription: " + remoteSession.isSubscribed());
        }
      }

      /**
       * @snip
       */
      public void subscribing(CDOSession session)
      {
        IListener listener = new CDORemoteSessionManager.EventAdapter()
        {
          @Override
          protected void onLocalSubscriptionChanged(boolean subscribed)
          {
            System.out.println("Local session subscribed: " + true);
          }

          @Override
          protected void onOpened(CDORemoteSession remoteSession)
          {
            System.out.println("Remote session opened: " + remoteSession.getUserID());
          }

          @Override
          protected void onClosed(CDORemoteSession remoteSession)
          {
            System.out.println("Remote session closed: " + remoteSession.getUserID());
          }

          @Override
          protected void onSubscribed(CDORemoteSession remoteSession)
          {
            System.out.println("Remote session able to receive messages: " + remoteSession);
          }

          @Override
          protected void onUnsubscribed(CDORemoteSession remoteSession)
          {
            System.out.println("Remote session unable to receive messages: " + remoteSession);
          }

          @Override
          protected void onMessageReceived(CDORemoteSession remoteSession, CDORemoteSessionMessage message)
          {
            System.out.println("Message from " + remoteSession.getUserID() + ": " + message);
          }
        };

        session.getRemoteSessionManager().addListener(listener);
        // The listener addition causes the remote session manager to subscribe to remote session information.
        // Now it will start receiving updates about remote sessions.

        // To stop receiving updates, remove the listener.
        // The listener removal causes the remote session manager to unsubscribe from remote session information:
        session.getRemoteSessionManager().removeListener(listener);

        // Alternatively, you can force subscription regardless of listener presence:
        session.getRemoteSessionManager().setForceSubscription(true);
      }

      /**
       * Exchanging Messages With Other Sessions
       * <p>
       * Exchanging messages between sessions enables real-time collaboration, notifications, and coordination among users.
       * This is useful for chat, alerts, or workflow triggers.
       * <p>
       * The {@link CDORemoteSessionMessage} interface represents a message sent between sessions.
       * It contains a {@link CDORemoteSessionMessage#getType() type} indicating the message's purpose,
       * a {@link CDORemoteSessionMessage#getPriority() priority}, and {@link CDORemoteSessionMessage#getData() payload data}
       * with the actual content in form of a <code>byte[]</code>. To interpret the payload data, you need to agree
       * on a serialization format with the message sender and receiver. The type of the message can be used to
       * differentiate between different kinds of messages and to determine how to process the payload data.
       * CDO does not impose any restrictions on the message content. You can use any serialization format you like,
       * such as JSON, XML, Protocol Buffers, or custom binary formats.
       * <p>
       * You can send messages to a specific remote session by using {@link CDORemoteSession#sendMessage(CDORemoteSessionMessage)}.
       * A message sent to a remote session is delivered only if the target session is subscribed to receive messages.
       * You can also broadcast messages to all subscribed remote sessions using
       * {@link CDORemoteSessionManager#sendMessage(CDORemoteSessionMessage, CDORemoteSession...)},
       * or {@link CDORemoteTopic#sendMessage(CDORemoteSessionMessage)} (see {@link RemoteTopics}).
       * These methods deliver the message to the target session(s) or topic subscribers.
       * <p>
       * Here is an example of how to send messages to other sessions using the remote session manager:
       * {@link #sendRemoteMessage(CDOSession) SendRemoteMessage.java}
       * <p>
       * To receive messages, register a listener for {@link CDORemoteSessionEvent.MessageReceived} events.
       * The listener will be notified whenever a message is received from another session or topic.
       * <p>
       * Here is an example of how to receive messages from other sessions using the remote session manager:
       * {@link #receiveRemoteMessage(CDOSession) ReceiveRemoteMessage.java}
       */
      public class RemoteMessages
      {
        /**
         * @snip
         */
        public void sendRemoteMessage(CDOSession session)
        {
          CDORemoteSessionManager remoteSessionManager = session.getRemoteSessionManager();

          // Create a message with type "chat", priority HIGH, and payload "Hello, World!".
          String messageType = "com.foo.chat";
          Priority messagePriority = Priority.HIGH;
          byte[] messageData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
          CDORemoteSessionMessage message = new CDORemoteSessionMessage(messageType, messagePriority, messageData);

          // Determine the target remote sessions (e.g., all sessions of user "john.doe").
          CDORemoteSession[] johnsSessions = remoteSessionManager.getRemoteSessions("john.doe");

          // Send the message to each of John's sessions.
          for (CDORemoteSession remoteSession : johnsSessions)
          {
            remoteSession.sendMessage(message);
          }

          // Alternatively, broadcast the message in a single network request to each of John's sessions.
          remoteSessionManager.sendMessage(message, johnsSessions);

          // Alternatively, broadcast the message to all subscribed remote sessions.
          remoteSessionManager.sendMessage(message);
        }

        /**
         * @snip
         */
        public void receiveRemoteMessage(CDOSession session)
        {
          session.getRemoteSessionManager().addListener(new CDORemoteSessionManager.EventAdapter()
          {
            @Override
            protected void onMessageReceived(CDORemoteSession remoteSession, CDORemoteSessionMessage message)
            {
              if ("com.foo.chat".equals(message.getType())) // Check the message type.
              {
                String text = new String(message.getData(), StandardCharsets.UTF_8); // Decode the payload data.
                System.out.println("Chat message from " + remoteSession.getUserID() + ": " + text);
              }
            }
          });
        }
      }

      /**
       * Collaborating on Shared Topics
       * <p>
       * Shared topics allow users to collaborate by grouping sessions around common subjects, such as projects,
       * documents, or activities. Topics provide a way to broadcast messages, track presence, and coordinate work.
       * <p>
       * The {@link CDORemoteTopic} interface represents a collaboration topic. It emits events for topic subscription,
       * message delivery, and presence changes. You can subscribe to a topic using {@link CDORemoteSessionManager#subscribeTopic(String)}.
       * This adds your session to the topic and enables you to receive messages and presence updates.
       * If the topic does not exist, it is created automatically.
       * <p>
       * The presence of remote sessions in a topic can be queried using {@link CDORemoteTopic#getRemoteSessions()}.
       * As the {@link CDORemoteTopic} is an {@link IContainer IContainer&lt;CDORemoteSession&gt;}, you can also listen for presence changes.
       * Changes in presence are notified via {@link IContainer} events.
       * <p>
       * To collaborate, send messages to a topic using {@link CDORemoteTopic#sendMessage(CDORemoteSessionMessage)}. These
       * messages are broadcast to all sessions that are subscribed to the topic.
       * Subscribers receive messages via {@link CDORemoteTopicEvent.MessageReceived} events.
       * <p>
       * To leave a topic, call {@link CDORemoteTopic#unsubscribe()}. This removes the session from the topic and
       * stops further event notifications. Other sessions in the topic are notified of your departure.
       * <p>
       * Here is an example of how to collaborate on a shared topic using the remote session manager:
       * {@link #collaborateOnTopic(CDOSession) CollaborateOnTopic.java}
       */
      public class RemoteTopics
      {
        /**
         * @snip
         */
        public void collaborateOnTopic(CDOSession session)
        {
          CDORemoteSessionManager remoteSessionManager = session.getRemoteSessionManager();

          // Subscribe to the "com.foo.projectX" topic; create it if it does not exist yet.
          CDORemoteTopic projectX = remoteSessionManager.subscribeTopic("com.foo.projectX");

          // List current topic members.
          for (CDORemoteSession topicMember : projectX.getRemoteSessions())
          {
            System.out.println("Topic member: " + topicMember.getUserID());
          }

          // Send a message to all topic members.
          projectX.sendMessage(new CDORemoteSessionMessage( //
              "com.foo.chat", Priority.NORMAL, "Hello, Project X team!".getBytes(StandardCharsets.UTF_8)));

          // Listen for topic events.
          projectX.addListener(new ContainerEventAdapter<CDORemoteSession>()
          {
            @Override
            protected void onAdded(IContainer<CDORemoteSession> topic, CDORemoteSession remoteSession)
            {
              System.out.println("Topic member joined: " + remoteSession.getUserID());
            }

            @Override
            protected void onRemoved(IContainer<CDORemoteSession> topic, CDORemoteSession remoteSession)
            {
              System.out.println("Topic member left: " + remoteSession.getUserID());
            }

            @Override
            protected void notifyOtherEvent(IEvent event)
            {
              if (event instanceof CDORemoteTopicEvent.MessageReceived)
              {
                CDORemoteTopicEvent.MessageReceived messageEvent = (CDORemoteTopicEvent.MessageReceived)event;

                CDORemoteSessionMessage message = messageEvent.getMessage();
                if ("com.foo.chat".equals(message.getType())) // Check the message type.
                {
                  String text = new String(message.getData(), StandardCharsets.UTF_8); // Decode the payload data.
                  System.out.println("Chat message from " + messageEvent.getRemoteSession().getUserID() + ": " + text);
                }
              }
            }
          });

          // Unsubscribe from the topic when done.
          projectX.unsubscribe();
        }
      }

      /**
       * Integrating Topics With Your User Interface
       * <p>
       * Integrating {@link CDORemoteTopic topics} into your UI allows users to see available topics, track presence,
       * and view messages in real time. This enhances collaboration and awareness.
       * <p>
       * You can create a custom UI component to display topics, their members, and messages using the APIs provided by
       * the {@link CDORemoteSessionManager} and {@link CDORemoteTopic} interfaces. This component can list all subscribed topics,
       * show the presence of members in each topic, and display messages as they are received. Such a custom component
       * can be tailored to fit the specific needs and design of your application.
       * <p>
       * Alternatively, you can use the built-in {@code CDORemoteTopicsView} provided by CDO.
       * The {@code CDORemoteTopicsView} provides a user interface for displaying topics, session presence, and messages.
       * It displays all topics that the local session is subscribed to, along with the members of each topic.
       * It can be extended to show additional topic information, such as images, labels, descriptions, or custom metadata.
       * This extension can be achieved by implementing a custom {@code CDOTopicProvider}, which defines how topics are represented
       * in the view. The {@code CDOTopicProvider} interface allows you to specify the topic ID, label, description,
       * and image for each topic, as well as how to create and manage topic listeners. The view uses the topic provider to
       * display topics and their members.
       * <p>
       * To integrate an editor with {@code CDORemoteTopicsView}, implement a {@code CDOTopicProvider} and its
       * {@code Topic} and {@code Listener} interfaces. For example, the built-in {@code CDOEditor} treats
       * {@link CDOResource CDOResources} as topics, subscribing to the relevant topic on activation and unsubscribing on deactivation.
       * This ensures the editor's presence and activity are reflected in the topics view on all clients.
       * <p>
       * Here is an example of how to integrate topics with your user interface using a custom topic provider:
       * {@link #integrateTopicsWithUI(CDOSession) IntegrateTopicsWithUI.java}
       */
      public class IntegratingTopicsWithUI
      {
        /**
         * @snip
         */
        @SuppressWarnings("unused")
        public void integrateTopicsWithUI(CDOSession session)
        {
          // The CDORemoteTopicsView listens to the active workbench part and queries its topic provider (if any).
          // When the active part changes, the view updates to show the topics provided by the new part.
          // Here we define a custom editor that provides topics to the CDORemoteTopicsView by implementing the
          // IEditorPart.getAdapter() method to return a CDOTopicProvider. You could also implement the
          // CDOTopicProvider interface directly in your editor class.
          class MyEditor extends EditorPart
          {
            // Use a default topic provider that can hold multiple topics.
            // It also cares about topic listener registration and notification.
            private final DefaultTopicProvider topicProvider = new DefaultTopicProvider();

            @Override
            public void init(IEditorSite site, IEditorInput input) throws PartInitException
            {
              // Assume that the editor input provides the necessary topic information.
              if (input instanceof MyEditorInput)
              {
                MyEditorInput myInput = (MyEditorInput)input;

                // Use the session and the topic information from the editor input.
                CDOSession session = myInput.getSession();
                String id = myInput.getTopicID();
                Image image = myInput.getTopicImage();
                String text = myInput.getTopicText();
                String description = myInput.getTopicDescription();

                // Create a topic and add it to the topic provider.
                // Note that this topic is a "UI topic" that exists only in the context of this editor.
                // The actual CDORemoteTopic is created and managed by the CDORemoteTopicsView!
                Topic topic = new Topic(session, id, image, text, description);
                topicProvider.addTopic(topic);
              }
            }

            @Override
            public <T> T getAdapter(Class<T> type)
            {
              // The CDORemoteTopicsView queries this method to get the topic provider.
              // The view will then display the topics provided by this editor and listen for topic events.
              if (type == CDOTopicProvider.class)
              {
                return type.cast(topicProvider);
              }

              return null;
            }
          }
        }
      }
    }
  }

  /**
   * Session Events
   * <p>
   * Sessions emit events for lifecycle changes, errors, and repository updates. Learn how to listen for and handle these
   * events to build responsive applications.
   * <p>
   * The following events are fired from {@link CDOSession}:
   * <ul>
   *   <li><b>{@link CDOSessionInvalidationEvent}</b>: Fired when the session receives invalidations due to commits from
   *        other sessions. Contains details about the commit, branch, user, and changed objects.
   *   <li><b>{@link CDOSessionPermissionsChangedEvent}</b>: Fired when the session's permissions change, such as when
   *        the user's role is updated. Contains details about the new permissions.
   *   <li><b>{@link CDOSessionLocksChangedEvent}</b>: Fired when the session's lock states change, such as when
   *        locks are acquired or released. Contains details about the affected objects and their new lock states.
   *   <li><b>{@link CDOSessionRecoveryEvent}</b>: Fired when a {@link RecoveringCDOSessionConfiguration recovering session}
   *        attempts to recover from a connection loss. Indicates the progress and outcome of the recovery attempt.
   *        Contains details about the recovery state and any errors encountered.
   *   <li><b>{@link RepositoryTypeChangedEvent}</b>: Fired when the session detects a change in the repository type,
   *        such as during a fail-over scenario. Contains details about the old and new repository types.
   *   <li><b>{@link RepositoryStateChangedEvent}</b>: Fired when the session detects a change in the repository state,
   *        such as when the repository goes offline or comes back online. Contains details about the old and new states.
   *   <li><b>{@link LifecycleEvent}</b>: Fired when the session is opened or closed. Contains details about the lifecycle state.
   * </ul>
   * <p>
   * Applications can listen for these events by registering listeners with the session. This enables responsive handling of repository changes, errors, and lifecycle events.
   * <p>
   * Example of listening for session events:
   * {@link #addSessionListener(CDOSession) AddSessionListener.java}
   */
  public class SessionEvents
  {
    /**
     * @snip
     */
    public void addSessionListener(CDOSession session)
    {
      session.addListener(event -> {
        if (event instanceof CDOSessionInvalidationEvent)
        {
          CDOSessionInvalidationEvent invalidation = (CDOSessionInvalidationEvent)event;
          System.out.println("Commit ID: " + invalidation.getTimeStamp());
          System.out.println("Committed on: " + invalidation.getBranch());
          System.out.println("Committed by: " + invalidation.getUserID());
          System.out.println("Changes: " + invalidation.getChangedObjects());
        }
      });
    }
  }

  /**
   * Session Options
   * <p>
   * Configure session options to customize behavior such as passive updates, caching, and performance settings.
   * <p>
   * The {@link CDOSession.Options options} of a session are accessible via the {@link CDOSession#options() options()} method.
   * <p>
   * An option has getter and setter methods to retrieve and modify its value. When options are changed,
   * the {@link IOptionsContainer} emits events to notify listeners of the changes. This allows applications to react
   * dynamically to configuration changes.
   * <p>
   * Each option is documented in its own sub-chapter below.
   */
  public class SessionOptions
  {
    /**
     * Generated Package Emulation
     * <p>
     * Controls whether the session creates dynamic EMF packages for
     * missing generated packages. This is useful when working with models that have not been
     * generated yet. Can be enabled or disabled. By default, it is disabled.
     * <p>
     * API: {@link CDOSession.Options#isGeneratedPackageEmulationEnabled()}, {@link CDOSession.Options#setGeneratedPackageEmulationEnabled(boolean)}
     */
    public class GeneratedPackageEmulation
    {
    }

    /**
     * Passive Update Enabled
     * <p>
     * Controls whether the session receives passive updates from the server.
     * Can be enabled or disabled. By default, it is enabled.
     * <p>
     * API: {@link CDOSession.Options#isPassiveUpdateEnabled()}, {@link CDOSession.Options#setPassiveUpdateEnabled(boolean)}
     */
    public class PassiveUpdateEnabled
    {
    }

    /**
     * Passive Update Mode
     * <p>
     * Specifies the mode for passive updates (INVALIDATIONS, CHANGES, ADDITIONS).
     * The mode determines the type of updates the session receives from the server. By default, it is set to
     * {@link PassiveUpdateMode#INVALIDATIONS INVALIDATIONS}. In this mode, the session receives invalidation notifications
     * for changes made by other sessions. This means that when another session commits changes to the repository,
     * the session is notified that certain objects have changed, but it does not receive the actual changes.
     * Other modes include {@link PassiveUpdateMode#CHANGES CHANGES}, where the session receives detailed change information,
     * and {@link PassiveUpdateMode#ADDITIONS ADDITIONS}, where the session receives notifications about changes and new objects being added.
     * The choice of mode depends on the application's requirements for data freshness and performance.
     * <p>
     * API: {@link CDOSession.Options#getPassiveUpdateMode()}, {@link CDOSession.Options#setPassiveUpdateMode(PassiveUpdateMode)}
     */
    public class Doc_PassiveUpdateMode
    {
    }

    /**
     * Lock Notification Enabled
     * <p>
     * Controls whether the session receives lock notifications.
     * Can be enabled or disabled. By default, it is disabled.
     * <p>
     * API: {@link CDOSession.Options#isLockNotificationEnabled()}, {@link CDOSession.Options#setLockNotificationEnabled(boolean)}
     */
    public class LockNotificationEnabled
    {
    }

    /**
     * Lock Notification Mode
     * <p>
     * Controls the mode for lock notifications {@link LockNotificationMode#OFF OFF},
     * {@link LockNotificationMode#IF_REQUIRED_BY_VIEWS IF_REQUIRED_BY_VIEWS}, or {@link LockNotificationMode#ALWAYS ALWAYS}.
     * The mode determines whether and when the session receives lock notifications from the server.
     * By default, it is set to {@link LockNotificationMode#IF_REQUIRED_BY_VIEWS IF_REQUIRED_BY_VIEWS}, which means that
     * the session receives lock notifications only if it has views that require lock information. Other modes include
     * {@link LockNotificationMode#OFF OFF}, where the session does not receive any lock notifications, and
     * {@link LockNotificationMode#ALWAYS ALWAYS}, where the session receives all lock notifications regardless of its views.
     * The choice of mode depends on the application's requirements for lock awareness and performance.
     * <p>
     * API: {@link CDOSession.Options#getLockNotificationMode()}, {@link CDOSession.Options#setLockNotificationMode(LockNotificationMode)}
     */
    public class Doc_LockNotificationMode
    {
    }

    /**
     * Collection Loading Policy
     * <p>
     * Defines how large collections are loaded (e.g., all at once or in chunks).
     * Collection loading can impact performance and memory usage. A collection loading policy helps balance
     * performance and memory consumption when dealing with large collections.
     * <p>
     * You can create custom policies by calling {@link CDOUtil#createCollectionLoadingPolicy(int, int) createCollectionLoadingPolicy(int, int)}
     * with your desired initial chunk size and resolve chunk size. The initial chunk size defines how many elements are loaded
     * when the collection is first accessed. The resolve chunk size defines how many additional elements are loaded
     * when more elements in the collection are accessed. A smaller chunk size reduces memory consumption but may require
     * more network round-trips. A larger chunk size improves performance but increases memory usage. The optimal chunk sizes
     * depend on your application's access patterns and resource constraints.
     * <p>
     * API: {@link CDOSession.Options#getCollectionLoadingPolicy()}, {@link CDOSession.Options#setCollectionLoadingPolicy(CDOCollectionLoadingPolicy)}
     */
    public class CollectionLoadingPolicy
    {
    }

    /**
     * Large Object Cache
     * <p>
     * Manages caching of large objects to optimize memory usage and performance. You
     * can either implement your own cache by implementing the {@link CDOLobStore} interface and setting it via
     * {@link Options#setLobCache(CDOLobStore) Options.setLobCache(CDOLobStore)}, or you can use the built-in
     * {@link CDOLobStoreImpl}, which provides a simple file-based cache. The built-in cache can be configured to
     * store large objects in a specific directory on the file system. By default, the large object cache is
     * set to {@link CDOLobStoreImpl#INSTANCE}, which stores large objects in a temporary directory.
     * <p>
     * API: {@link CDOSession.Options#getLobCache()}, {@link CDOSession.Options#setLobCache(CDOLobStore)}
     */
    public class LargeObjectCache
    {
    }

    /**
     * Permission Updater
     * <p>
     * Controls how to update permissions of {@link CDORevision revisions} when
     * passive updates are received or the {@link InternalCDOSession#updatePermissions()} method is called.
     * By default, the permission updater is set to {@link CDOPermissionUpdater3#SERVER}, which updates permissions
     * from the server.
     * <p>
     * API: {@link CDOSession.Options#getPermissionUpdater()}, {@link CDOSession.Options#setPermissionUpdater(CDOPermissionUpdater3)}
     */
    public class PermissionUpdater
    {
    }

    /**
     * Delegable View Lock Enabled
     * <p>
     * Specifies whether the session supports delegable view locks. All view methods
     * are protected from concurrent access by the view's <i>view lock</i>. By default, the view lock is the constant object
     * returned by {@link CDOView#getViewLock()}. This lock is reentrant, but it is not delegable. This means that if a thread
     * holds the view lock and <b>calls</b> a method that tries to acquire the same view lock again, it will succeed. However,
     * if the thread that holds the view lock schedules a task to be executed asynchronously (e.g., using
     * {@link Display#syncExec(Runnable) Display.syncExec(Runnable)}) and waits for its completion, the task will not be able
     * to acquire the view lock because it is executed in a different thread. This can lead to deadlocks.
     * <p>
     * To avoid such deadlocks, you can enable delegable view locks. They are based on fact that the thread that
     * holds the view lock does block and no longer needs the lock while waiting for the asynchronous task to complete.
     * Hence, it can <b>delegate</b> the lock to another thread.
     * <p>
     * When delegable view locks are enabled, the view locks become instances of {@link DelegableReentrantLock},
     * which uses {@link DelegateDetector}s that are contributed to {@link IPluginContainer#INSTANCE IPluginContainer.INSTANCE} in order
     * to decide whether a thread is allowed to delegate its lock to another thread.
     * The <code>org.eclipse.net4j.util.ui</code> plug-in contributes a
     * <code>org.eclipse.net4j.util.internal.ui.DisplayDelegateDetector</code> that allows
     * delegation to SWT's UI thread. This is particularly useful when a background thread that holds the view lock
     * (such as a model change listener or EMF {@link Adapter}) needs to update the UI <b>synchronously</b> and
     * the update code (which is executed in the UI thread) needs to access the view.
     * <p>
     * An alternative to globally enabling delegable view locks for all views in a session is to use
     * {@link CDOUtil#setNextViewLock(Lock) CDOUtil.setNextViewLock(Lock)} to set a specific delegable view lock
     * for the next view that is created in the current thread.
     * <p>
     * API: {@link CDOSession.Options#isDelegableViewLockEnabled()}, {@link CDOSession.Options#setDelegableViewLockEnabled(boolean)}
     */
    public class DelegableViewLockEnabled
    {
    }

    /**
     * Prefetch Send Max Revision Keys
     * <p>
     * Revision prefetching can improve performance by reducing
     * the number of network round-trips required to load multiple revisions. On the other hand, prefetching
     * can lead to some revisions being loaded unnecessarily, because the session already has them in its cache.
     * To address this, the session tells the server which revisions it already has in its cache by sending their
     * revision keys in the prefetch request. However, sending too many revision keys can increase
     * network overhead and impact performance negatively.
     * To balance these concerns, the <i>prefetch send max revision keys</i> option allows you to limit
     * the number of revision keys that are sent in a single prefetch request. The default value is 100.
     * You can also change this default value by setting the system property <code>PREFETCH_SEND_MAX_REVISION_KEYS</code>.
     * <p>
     * API: {@link CDOSession.Options#getPrefetchSendMaxRevisionKeys()}, {@link CDOSession.Options#setPrefetchSendMaxRevisionKeys(int)}
     */
    public class PrefetchSendMaxRevisionKeys
    {
    }
  }

  /**
   * Session Properties
   * <p>
   * Access and modify session properties to store custom metadata and configuration values.
   * <p>
   * The {@link CDOSession#properties() CDOSession.properties()} map allows you to store and retrieve custom
   * properties associated with the session.
   * This can be useful for storing session-specific metadata, configuration values, or other information
   * that you want to associate with the session. The properties map is a simple key-value store
   * where the keys are strings and the values are arbitrary objects. The map is thread-safe,
   * so you can safely access and modify it from multiple threads. It also supports change listeners,
   * so you can be notified when properties are added, removed, or changed.
   * <p>
   * The properties are not persisted and are lost when the session is closed.
   */
  public class SessionProperties
  {
  }

  /**
   * Session Registry
   * <p>
   * The {@link CDOSessionRegistry} keeps track of all active sessions in the application.
   * It allows you to enumerate, find, and manage sessions. You can also register listeners
   * to be notified of session lifecycle events, such as when sessions are opened or closed.
   * <p>
   * The session registry is a singleton and can be accessed via {@link CDOSessionRegistry#INSTANCE}.
   * It provides methods to get all active sessions and find sessions by their global ID.
   * <p>
   * Global IDs are assigned to each session when it is opened. They are unique within the application
   * and can be used to identify and reference sessions. The session registry maintains
   * a mapping between global IDs and sessions, allowing you to look up sessions by their global ID.
   * The {@link CDOSessionRegistry#getID(CDOSession) global ID of a session}, which is assigned by the session registry,
   * must not be confused with the session's repository-specific ID,
   * which is assigned by the repository and may not be unique across different repositories.
   * <p>
   * Here is an example of how to use the session registry to close all active sessions:
   * {@link #closeAllSessions() CloseAllSessions.java}
   */
  public class SessionRegistry
  {
    /**
     * @snip
     */
    public void closeAllSessions()
    {
      for (CDOSession session : CDOSessionRegistry.INSTANCE.getSessions())
      {
        session.close();
      }
    }
  }

  /**
   * Required for the {@code IntegratingTopicsWithUI} example.
   */
  class EditorPart extends org.eclipse.ui.part.EditorPart
  {
    @Override
    public void doSave(IProgressMonitor monitor)
    {
    }

    @Override
    public void doSaveAs()
    {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
    }

    @Override
    public boolean isDirty()
    {
      return false;
    }

    @Override
    public boolean isSaveAsAllowed()
    {
      return false;
    }

    @Override
    public void createPartControl(Composite parent)
    {
    }

    @Override
    public void setFocus()
    {
    }
  }

  /**
   * Required for the {@code IntegratingTopicsWithUI} example.
   */
  interface MyEditorInput extends IEditorInput
  {
    public CDOSession getSession();

    public String getTopicDescription();

    public String getTopicText();

    public Image getTopicImage();

    public String getTopicID();
  }
}
