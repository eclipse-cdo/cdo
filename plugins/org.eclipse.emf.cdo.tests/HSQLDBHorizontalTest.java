package org.eclipse.emf.cdo.tests.store;

import org.eclipse.emf.cdo.tests.store.logic.HorizontalTestLogic;

import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.hsqldb.HSQLDBDataSource;
import org.eclipse.net4j.db.internal.hsqldb.HSQLDBAdapter;
import org.eclipse.net4j.internal.db.DataSourceConnectionProvider;

/**
 * @author Eike Stepper
 */
public final class HSQLDBHorizontalTest extends HorizontalTestLogic
{
  @Override
  protected IDBConnectionProvider createDBConnectionProvider()
  {
    HSQLDBDataSource dataSource = new HSQLDBDataSource();
    dataSource.setDatabase("jdbc:hsqldb:mem:storetest");
    dataSource.setUser("sa");

    return new DataSourceConnectionProvider(dataSource);
  }

  @Override
  protected HSQLDBAdapter createDBAdapter()
  {
    return new HSQLDBAdapter();
  }
}