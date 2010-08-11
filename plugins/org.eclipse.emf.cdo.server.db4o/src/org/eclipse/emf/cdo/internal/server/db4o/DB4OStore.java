/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.db4o;

import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db4o.IDB4OStore;
import org.eclipse.emf.cdo.spi.server.LongIDStore;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OStore extends LongIDStore implements IDB4OStore
{
  private transient ObjectServer server;

  @ExcludeFromDump
  private transient final StoreAccessorPool readerPool = new StoreAccessorPool(this, null);

  @ExcludeFromDump
  private transient final StoreAccessorPool writerPool = new StoreAccessorPool(this, null);

  private transient String storeLocation;

  private transient int port;

  private transient Configuration serverConfiguration;

  private ServerInfo serverInfo;

  private boolean requiredToSupportAudits;

  private boolean requiredToSupportBranches;

  public DB4OStore(String storeLocation, int port)
  {
    // super(IDB4OStore.TYPE, Collections.singleton(ChangeFormat.REVISION), Collections
    // .singleton(RevisionTemporality.NONE), Collections.singleton(RevisionParallelism.NONE));

    super(IDB4OStore.TYPE, set(ChangeFormat.REVISION), set(RevisionTemporality.NONE, RevisionTemporality.AUDITING),
        set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));

    this.storeLocation = storeLocation;
    this.port = port;
  }

  public DB4OStore(String storeLocation, int port, Configuration serverConfiguration)
  {
    this(storeLocation, port);
    this.serverConfiguration = serverConfiguration;
  }

  public long getCreationTime()
  {
    return getServerInfo().getCreationTime();
  }

  public boolean isFirstTime()
  {
    return getServerInfo().isFirstTime();
  }

  public ObjectContainer openClient()
  {
    // Configuration configuration = clientConfiguration;
    // if (configuration == null)
    // {
    // configuration = createClientConfiguration();
    // }

    // server.openClient(configuration);
    return server.openClient();
  }

  public Map<String, String> getPropertyValues(Set<String> names)
  {
    Map<String, String> properties = getServerInfo().getProperties();
    Map<String, String> propertiesSubSet = new HashMap<String, String>();
    for (String key : names)
    {
      propertiesSubSet.put(key, properties.get(key));
    }
    return propertiesSubSet;
  }

  public void setPropertyValues(Map<String, String> properties)
  {
    getServerInfo().setProperties(properties);
    commitServerInfo(getServerInfo());
  }

  public boolean isRequiredToSupportAudits()
  {
    return requiredToSupportAudits;
  }

  public boolean isRequiredToSupportBranches()
  {
    return requiredToSupportBranches;
  }

  @Override
  protected void doBeforeActivate()
  {
    requiredToSupportAudits = getRepository().isSupportingAudits();
    requiredToSupportBranches = getRepository().isSupportingBranches();
  }

  public void removePropertyValues(Set<String> names)
  {
    Map<String, String> properties = getServerInfo().getProperties();
    for (String key : names)
    {
      properties.remove(key);
    }
    getServerInfo().setProperties(properties);
    commitServerInfo(getServerInfo());
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    Configuration configuration = serverConfiguration;
    if (configuration == null)
    {
      configuration = createServerConfiguration();
    }

    server = Db4o.openServer(configuration, storeLocation, port);
    getServerInfo();
  }

  private ServerInfo getServerInfo()
  {
    if (serverInfo == null)
    {
      serverInfo = initServerInfo();
    }

    return serverInfo;
  }

  private ServerInfo initServerInfo()
  {
    ObjectContainer container = openClient();
    ObjectSet<ServerInfo> infos = container.query(ServerInfo.class);

    if (infos.size() > 1)
    {
      throw new IllegalStateException("ServeInfo is stored in container more than once");
    }

    ServerInfo newServerInfo = null;
    if (infos.isEmpty())
    {
      newServerInfo = new ServerInfo();
      newServerInfo.setFirstTime(true);
      newServerInfo.setCreationTime(System.currentTimeMillis());
      commitServerInfo(newServerInfo);
    }
    else
    {
      newServerInfo = infos.get(0);
      if (newServerInfo.isFirstTime())
      {
        newServerInfo.setFirstTime(false);
        commitServerInfo(newServerInfo);
      }
    }
    container.close();
    return newServerInfo;
  }

  private void commitServerInfo(ServerInfo serverInfo)
  {
    ObjectContainer container = openClient();
    container.store(serverInfo);
    container.commit();
    container.close();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    server.close();
    server = null;
    super.doDeactivate();
  }

  protected Configuration createServerConfiguration()
  {
    Configuration conf = Db4o.newConfiguration();
    return conf;
  }

  @Override
  protected IStoreAccessor createReader(ISession session)
  {
    return new DB4OStoreAccessor(this, session);
  }

  @Override
  protected IStoreAccessor createWriter(ITransaction transaction)
  {
    return new DB4OStoreAccessor(this, transaction);
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    return readerPool;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    return writerPool;
  }
}
