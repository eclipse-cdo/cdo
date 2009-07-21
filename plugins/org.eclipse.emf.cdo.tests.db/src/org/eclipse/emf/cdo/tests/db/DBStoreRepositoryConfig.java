package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;

import javax.sql.DataSource;

/**
 * @author Eike Stepper
 */
public abstract class DBStoreRepositoryConfig extends RepositoryConfig
{
  private static final long serialVersionUID = 1L;

  public DBStoreRepositoryConfig(String name)
  {
    super(name);
  }

  @Override
  public IStore createStore(String repoName)
  {
    IMappingStrategy mappingStrategy = createMappingStrategy();
    IDBAdapter dbAdapter = createDBAdapter();
    DataSource dataSource = createDataSource(repoName);
    return CDODBUtil.createStore(mappingStrategy, dbAdapter, DBUtil.createConnectionProvider(dataSource));
  }

  protected abstract IMappingStrategy createMappingStrategy();

  protected abstract IDBAdapter createDBAdapter();

  protected abstract DataSource createDataSource(String repoName);
}
