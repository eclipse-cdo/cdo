/*
 * Copyright (c) 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.repositories;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchCreationContext;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryElement;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.ConsumerWithException;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.UUIDGenerator;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.security.CredentialsProviderFactory;
import org.eclipse.net4j.util.security.CredentialsUpdateOperation;
import org.eclipse.net4j.util.security.ICredentialsProvider;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsUpdate;
import org.eclipse.net4j.util.security.IPasswordCredentialsUpdateProvider;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.SecurityUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.provider.IProviderHints;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public abstract class CDORepositoryImpl extends AbstractElement implements CDORepository
{
  public static final String PROP_NAME = "name";

  public static final String PROP_REALM = "realm";

  public static final String REPOSITORY_KEY = CDORepository.class.getName();

  private static final boolean READABLE_IDS = OMPlatform.INSTANCE.isProperty("cdo.explorer.readableIDs");

  private static final boolean SET_USER_NAME = OMPlatform.INSTANCE.isProperty("cdo.explorer.setUserName");

  private final Set<CDOCheckout> checkouts = new HashSet<>();

  private final Set<CDOCheckout> openCheckouts = new HashSet<>();

  private final IListener branchManagerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOBranchChangedEvent)
      {
        CDOBranchChangedEvent e = (CDOBranchChangedEvent)event;
        if (e.getChangeKind() == ChangeKind.RENAMED)
        {
          CDORepositoryManagerImpl manager = getManager();
          if (manager != null)
          {
            manager.fireElementChangedEvent(ElementsChangedEvent.StructuralImpact.NONE, e.getBranch());
          }
        }
      }
    }
  };

  private final IListener mainBranchListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof IContainerEvent)
      {
        @SuppressWarnings("unchecked")
        IContainerEvent<CDOBranch> e = (IContainerEvent<CDOBranch>)event;

        fireEvent(new ContainerEvent<>(CDORepositoryImpl.this, Arrays.asList(e.getDeltas())));
      }
    }
  };

  private final IListener sessionReleaser = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      releaseSession();
    }
  };

  private String name;

  private VersioningMode versioningMode;

  private IDGeneration idGeneration;

  private String realm;

  private State state = State.Disconnected;

  private boolean explicitlyConnected;

  private int sessionRefCount;

  private CDOSession session;

  public static final String PROP_VERSIONING_MODE = "versioningMode";

  public static final String PROP_ID_GENERATION = "idGeneration";

  public CDORepositoryImpl()
  {
  }

  @Override
  public IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  public CDORepositoryManagerImpl getManager()
  {
    return OM.getRepositoryManager();
  }

  @Override
  public final String getName()
  {
    return name;
  }

  @Override
  public final VersioningMode getVersioningMode()
  {
    return versioningMode;
  }

  @Override
  public final IDGeneration getIDGeneration()
  {
    return idGeneration;
  }

  @Override
  public IPasswordCredentials getCredentials()
  {
    return getCredentials(null);
  }

  @Override
  public IPasswordCredentials getCredentials(String realm)
  {
    IPasswordCredentials[] result = { null };

    withSecureNode(false, node -> {
      String userID = node.get("username", null);
      if (!StringUtil.isEmpty(userID))
      {
        String password = node.get("password", null);
        result[0] = new PasswordCredentials(userID, password);
      }
    });

    return result[0];
  }

  @Override
  public void setCredentials(IPasswordCredentials credentials)
  {
    if (credentials == null)
    {
      // Delete node.
      withSecureNode(false, node -> {
        node.removeNode();
        node.flush();
      });

      return;
    }

    String password = SecurityUtil.toString(credentials.getPassword());

    withSecureNode(true, node -> {
      node.put("uri", getURI(), false);
      node.put("username", credentials.getUserID(), false);

      if (password == null)
      {
        node.remove("password");
      }
      else
      {
        node.put("password", password, true);
      }

      node.flush();
    });
  }

  @Override
  public IPasswordCredentialsUpdate getCredentialsUpdate(String userID, CredentialsUpdateOperation operation)
  {
    return getCredentialsUpdate(null, userID, operation);
  }

  @Override
  public IPasswordCredentialsUpdate getCredentialsUpdate(String realm, String userID, CredentialsUpdateOperation operation)
  {
    IManagedContainer container = getContainer();

    ICredentialsProvider provider = container.getElementOrNull(CredentialsProviderFactory.PRODUCT_GROUP, "cdo-explorer", null);
    if (provider == null)
    {
      provider = container.getElementOrNull(CredentialsProviderFactory.PRODUCT_GROUP, "interactive", null);
      if (provider == null)
      {
        provider = container.getElementOrNull(CredentialsProviderFactory.PRODUCT_GROUP, "default", null);
      }
    }

    if (provider instanceof IPasswordCredentialsUpdateProvider)
    {
      return ((IPasswordCredentialsUpdateProvider)provider).getCredentialsUpdate(realm, userID, operation);
    }

    return null;
  }

  @Override
  public boolean isInteractive()
  {
    return false;
  }

  @Override
  public final State getState()
  {
    return state;
  }

  @Override
  public final boolean isConnected()
  {
    return session != null;
  }

  @Override
  public final void connect()
  {
    explicitlyConnected = true;
    doConnect();
  }

  protected void doConnect()
  {
    boolean connected = false;
    CDOSession newSession = null;

    synchronized (this)
    {
      if (!isConnected())
      {
        try
        {
          state = State.Connecting;

          session = openSession();
          session.properties().put(REPOSITORY_KEY, this);
          session.addListener(new LifecycleEventAdapter()
          {
            @Override
            protected void onDeactivated(ILifecycle lifecycle)
            {
              explicitlyConnected = false;
              doDisconnect(true);
            }
          });

          CDOBranchManager branchManager = session.getBranchManager();
          branchManager.addListener(branchManagerListener);

          CDOBranch mainBranch = branchManager.getMainBranch();
          mainBranch.addListener(mainBranchListener);

          newSession = session;
          state = State.Connected;
        }
        catch (RuntimeException | Error ex)
        {
          state = State.Disconnected;
          throw ex;
        }

        connected = true;
      }
    }

    if (connected)
    {
      CDORepositoryManagerImpl manager = getManager();
      if (manager != null)
      {
        manager.fireRepositoryConnectionEvent(this, newSession, true);
      }
    }
  }

  @Override
  public final void disconnect()
  {
    disconnect(false);
  }

  public final void disconnect(boolean force)
  {
    explicitlyConnected = false;
    doDisconnect(force);
  }

  protected void doDisconnect(boolean force)
  {
    if (!force)
    {
      if (explicitlyConnected || sessionRefCount != 0)
      {
        return;
      }
    }

    boolean disconnected = false;
    CDOSession oldSession = null;

    synchronized (this)
    {
      if (isConnected())
      {
        state = State.Disconnecting;
        oldSession = session;

        try
        {
          closeSession();
        }
        finally
        {
          session = null;
          sessionRefCount = 0;
          state = State.Disconnected;
        }

        disconnected = true;
      }
    }

    if (disconnected)
    {
      CDORepositoryManagerImpl manager = getManager();
      if (manager != null)
      {
        manager.fireRepositoryConnectionEvent(this, oldSession, false);
      }
    }
  }

  public final void disconnectIfUnused()
  {
    synchronized (checkouts)
    {
      if (openCheckouts.isEmpty())
      {
        doDisconnect(false);
      }
    }
  }

  @Override
  public final CDOSession getSession()
  {
    return session;
  }

  @Override
  public CDOSession acquireSession()
  {
    ++sessionRefCount;
    doConnect();

    return session;
  }

  @Override
  public void releaseSession()
  {
    --sessionRefCount;
    doDisconnect(false);
  }

  @Override
  public CDOTransaction openTransaction(CDOBranchPoint target, ResourceSet resourceSet)
  {
    CDOSession session = acquireSession();
    CDOTransaction transaction = session.openTransaction(target, resourceSet);
    transaction.addListener(sessionReleaser);
    return transaction;
  }

  @Override
  public CDOTransaction openTransaction(String durableLockingID, ResourceSet resourceSet)
  {
    CDOSession session = acquireSession();
    CDOTransaction transaction = session.openTransaction(durableLockingID, resourceSet);
    transaction.addListener(sessionReleaser);
    return transaction;
  }

  @Override
  public CDOView openView(CDOBranchPoint target, ResourceSet resourceSet)
  {
    CDOSession session = acquireSession();
    CDOView view = session.openView(target, resourceSet);
    view.addListener(sessionReleaser);
    return view;
  }

  @Override
  public CDOView openView(String durableLockingID, ResourceSet resourceSet)
  {
    CDOSession session = acquireSession();
    CDOView view = session.openView(durableLockingID, resourceSet);
    view.addListener(sessionReleaser);
    return view;
  }

  @Override
  public void delete(boolean deleteContents)
  {
    disconnect();

    // Delete secure preference node.
    setCredentials(null);

    CDORepositoryManagerImpl manager = getManager();
    if (manager != null)
    {
      manager.removeElement(this);
    }

    super.delete(deleteContents);
  }

  @Override
  public final CDOCheckout[] getCheckouts()
  {
    synchronized (checkouts)
    {
      return checkouts.toArray(new CDOCheckout[checkouts.size()]);
    }
  }

  public final void addCheckout(CDOCheckout checkout)
  {
    synchronized (checkouts)
    {
      checkouts.add(checkout);
    }
  }

  public final void removeCheckout(CDOCheckout checkout)
  {
    synchronized (checkouts)
    {
      checkouts.remove(checkout);
    }
  }

  public final CDOSession openCheckout(CDOCheckout checkout)
  {
    connect();

    synchronized (checkouts)
    {
      openCheckouts.add(checkout);
    }

    return session;
  }

  public final void closeCheckout(CDOCheckout checkout)
  {
    synchronized (checkouts)
    {
      openCheckouts.remove(checkout);
    }
  }

  @Override
  public final boolean isEmpty()
  {
    if (isConnected())
    {
      return session.getBranchManager().getMainBranch().isEmpty();
    }

    return false;
  }

  @Override
  public final CDOBranch[] getElements()
  {
    if (isConnected())
    {
      return session.getBranchManager().getMainBranch().getBranches();
    }

    return new CDOBranch[0];
  }

  @Override
  @SuppressWarnings({ "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    if (isConnected())
    {
      if (adapter == CDOSession.class)
      {
        return session;
      }

      if (adapter == CDOBranchCreationContext.class)
      {
        if (session.getRepositoryInfo().isSupportingBranches())
        {
          return new CDOBranchCreationContext()
          {
            @Override
            public CDOBranchPoint getBase()
            {
              return session.getBranchManager().getMainBranch().getHead();
            }
          };
        }
      }
    }

    if (adapter == CDORepositoryElement.class)
    {
      return new CDORepositoryElement()
      {
        @Override
        public CDORepository getRepository()
        {
          return CDORepositoryImpl.this;
        }

        @Override
        public int getBranchID()
        {
          return CDOBranch.MAIN_BRANCH_ID;
        }

        @Override
        public long getTimeStamp()
        {
          return CDOBranchPoint.UNSPECIFIED_DATE;
        }

        @Override
        public CDOID getObjectID()
        {
          return null;
        }
      };
    }

    return super.getAdapter(adapter);
  }

  @Override
  public String toString()
  {
    return getLabel();
  }

  @Override
  protected void init(File folder, String type, Properties properties)
  {
    super.init(folder, type, properties);
    name = properties.getProperty(PROP_NAME);

    String versioningModeProperty = properties.getProperty(PROP_VERSIONING_MODE);
    versioningMode = versioningModeProperty == null ? null : VersioningMode.valueOf(versioningModeProperty);

    String idGenerationProperty = properties.getProperty(PROP_ID_GENERATION);
    idGeneration = idGenerationProperty == null ? null : IDGeneration.valueOf(idGenerationProperty);

    realm = properties.getProperty(PROP_REALM);
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.setProperty(PROP_NAME, name);

    if (versioningMode != null)
    {
      properties.setProperty(PROP_VERSIONING_MODE, versioningMode.toString());
    }

    if (idGeneration != null)
    {
      properties.setProperty(PROP_ID_GENERATION, idGeneration.toString());
    }

    if (realm != null)
    {
      properties.setProperty(PROP_REALM, realm);
    }
  }

  protected IConnector getConnector()
  {
    IManagedContainer container = getContainer();
    return Net4jUtil.getConnector(container, getConnectorType(), getConnectorDescription());
  }

  protected CDOSessionConfiguration createSessionConfiguration()
  {
    IConnector connector = getConnector();

    CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
    config.setConnector(connector);
    config.setRepositoryName(name);

    if (READABLE_IDS)
    {
      config.setIDGenerator(new CDOIDGenerator()
      {
        private final UUIDGenerator decoder = new UUIDGenerator();

        private Map<EClass, AtomicInteger> counters;

        private int typeCounter;

        @Override
        public CDOID generateCDOID(EObject object)
        {
          if (counters == null)
          {
            counters = new HashMap<>();
            CDOView view = CDOUtil.getView(object);
            view.getSession().getRevisionManager().handleRevisions(null, null, false, CDOBranchPoint.UNSPECIFIED_DATE, false, new CDORevisionHandler()
            {
              @Override
              public boolean handleRevision(CDORevision revision)
              {
                EClass eClass = revision.getEClass();
                AtomicInteger counter = getCounter(eClass);

                String id = revision.getID().toString();
                id = id.substring(0, id.length() - "A".length());
                id = id.substring(id.lastIndexOf('_') + 1);

                int counterValue = Integer.parseInt(id);
                if (counterValue > counter.get())
                {
                  counter.set(counterValue);
                }

                return true;
              }
            });
          }

          EClass eClass = object.eClass();

          String type = eClass.getName();
          if (type.length() > 16)
          {
            String suffix = "_" + (++typeCounter);
            type = type.substring(0, 16 - suffix.length()) + suffix;
            System.out.println(eClass.getName() + " --> " + type);
          }

          type = "_" + type;

          AtomicInteger counter = getCounter(eClass);

          String str = "_" + counter.incrementAndGet();
          String id = type + "____________________________________".substring(0, 22 - type.length() - str.length()) + str + "A";

          if ("_CDOResource_________5A".equals(id))
          {
            System.out.println();
          }

          byte[] value = decoder.decode(id);

          String encoded = decoder.encode(value);
          if (!encoded.equals(id))
          {
            throw new IllegalStateException();
          }

          return CDOIDUtil.createUUID(value);
        }

        private AtomicInteger getCounter(EClass eClass)
        {
          AtomicInteger counter = counters.get(eClass);
          if (counter == null)
          {
            counter = new AtomicInteger();
            counters.put(eClass, counter);
          }
          return counter;
        }

        @Override
        public void reset()
        {
        }
      });
    }

    return config;
  }

  public CDOSession openSession()
  {
    CDOSessionConfiguration sessionConfiguration = createSessionConfiguration();
    sessionConfiguration.setPassiveUpdateEnabled(true);
    sessionConfiguration.setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    sessionConfiguration.setCredentialsProvider(this);

    CDOSession session = sessionConfiguration.openSession();
    session.options().setGeneratedPackageEmulationEnabled(true);

    if (SET_USER_NAME && StringUtil.isEmpty(session.getUserID()))
    {
      String userName = System.getProperty("user.name");
      if (!StringUtil.isEmpty(userName))
      {
        ((InternalCDOSession)session).setUserID(userName);
      }
    }

    return session;
  }

  protected void closeSession()
  {
    session.close();
  }

  private void withSecureNode(boolean createOnDemand, ConsumerWithException<ISecurePreferences, Exception> consumer)
  {
    try
    {
      ISecurePreferences securePreferences = getSecurePreferences();
      if (securePreferences != null)
      {
        String path = getSecurePath(securePreferences);
        if (createOnDemand || securePreferences.nodeExists(path))
        {
          ISecurePreferences node = securePreferences.node(path);
          consumer.accept(node);
        }
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  private String getSecurePath(ISecurePreferences securePreferences)
  {
    String stateLocation = OM.getStateLocation().replace('/', '\\');
    String id = getID().replace('/', '_');

    return "CDO/" + stateLocation + "/" + id;
  }

  private static ISecurePreferences getSecurePreferences() throws IOException
  {
    Map<Object, Object> options = new HashMap<>();
    options.put(IProviderHints.PROMPT_USER, Boolean.FALSE);

    ISecurePreferences result = SecurePreferencesFactory.open(null, options);
    if (result != null)
    {
      // Try to refresh the entire secure storage, if needed.
      try
      {
        // Fetch the root node.
        Object root = ReflectUtil.getValue("node", result); //$NON-NLS-1$

        // Just to be sure...
        root = ReflectUtil.invokeMethod("getRoot", root); //$NON-NLS-1$

        // Check if it has been modified, i.e., whether it is dirty from unsaved changes.
        boolean modified = ReflectUtil.invokeMethod("isModified", root); //$NON-NLS-1$
        if (!modified)
        {
          // If it's not dirty, check if the expected time stamp is different from the timestamp on disk.
          long lastModified = ReflectUtil.invokeMethod("getLastModified", root); //$NON-NLS-1$
          long timestamp = ReflectUtil.getValue("timestamp", root); //$NON-NLS-1$
          if (lastModified != timestamp)
          {
            // If so, reload the secure storage from disk.
            ReflectUtil.invokeMethod("load", root); //$NON-NLS-1$
          }
        }
      }
      catch (Throwable ex)
      {
        OM.LOG.error(ex);
      }
    }

    return result;
  }
}
