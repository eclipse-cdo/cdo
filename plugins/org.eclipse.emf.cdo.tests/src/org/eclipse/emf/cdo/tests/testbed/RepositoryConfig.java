/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.testbed;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.StoreUtil;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class RepositoryConfig extends Config implements RepositoryProvider
{
  public static final String DIMENSION = "repository";

  public static final RepositoryConfig[] CONFIGS = { MEM.INSTANCE, DBHorizontalHsql.INSTANCE,
      DBHorizontalDerby.INSTANCE, Hibernate.INSTANCE };

  private IRepository repository;

  private IStore store;

  public RepositoryConfig(String name)
  {
    super(DIMENSION, name);
  }

  public Map<String, String> getRepositoryProperties()
  {
    Map<String, String> repositoryProperties = new HashMap<String, String>();
    initRepositoryProperties(repositoryProperties);

    Map<String, String> properties = getCurrentTest().getProperties();
    if (properties != null)
    {
      repositoryProperties.putAll(properties);
    }

    return repositoryProperties;
  }

  public synchronized IRepository getRepository()
  {
    if (repository == null)
    {
      repository = createRepository();
    }

    return repository;
  }

  public synchronized IStore getStore()
  {
    if (store == null)
    {
      store = createStore();
    }

    return store;
  }

  protected void initRepositoryProperties(Map<String, String> props)
  {
    props.put(Props.PROP_OVERRIDE_UUID, "TEST_UUID");
    props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "true");
    props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");
  }

  protected IRepository createRepository()
  {
    return CDOServerUtil.createRepository(REPOSITORY_NAME, getStore(), getRepositoryProperties());
  }

  protected abstract IStore createStore();

  /**
   * @author Eike Stepper
   */
  public static final class MEM extends RepositoryConfig
  {
    public static final String NAME = "MEM";

    public static final MEM INSTANCE = new MEM();

    public MEM()
    {
      super(NAME);
    }

    @Override
    protected IStore createStore()
    {
      return StoreUtil.createMEMStore();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class DB extends RepositoryConfig
  {
    public DB(String name)
    {
      super(name);
    }

    protected IStore createHsqlStore()
    {
      return null;
    }

    protected IStore createDerbyStore()
    {
      return null;
    }

    protected IMappingStrategy createHorizontalMappingStrategy()
    {
      return null;
    }

    protected abstract IMappingStrategy createMappingStrategy();
  }

  /**
   * @author Eike Stepper
   */
  public static final class DBHorizontalHsql extends DB
  {
    public static final String NAME = "DBHorizontalHsql";

    public static final DBHorizontalHsql INSTANCE = new DBHorizontalHsql();

    public DBHorizontalHsql()
    {
      super(NAME);
    }

    @Override
    protected IStore createStore()
    {
      return createHsqlStore();
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return createHorizontalMappingStrategy();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class DBHorizontalDerby extends DB
  {
    public static final String NAME = "DBHorizontalDerby";

    public static final DBHorizontalDerby INSTANCE = new DBHorizontalDerby();

    public DBHorizontalDerby()
    {
      super(NAME);
    }

    @Override
    protected IStore createStore()
    {
      return createDerbyStore();
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return createHorizontalMappingStrategy();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Hibernate extends RepositoryConfig
  {
    public static final String NAME = "Hibernate";

    public static final Hibernate INSTANCE = new Hibernate();

    public static final String MAPPING_FILE = "mappingfile";

    public Hibernate()
    {
      super(NAME);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(Props.PROP_SUPPORTING_AUDITS, "false");
      props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "false");
      props.put(Props.PROP_VERIFYING_REVISIONS, "false");
    }

    @Override
    protected IStore createStore()
    {
      return null;
    }
  }
}
