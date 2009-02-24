/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.store.verifier;

import static junit.framework.Assert.assertTrue;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.tests.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Winkler
 */
public abstract class AbstractDBStoreVerifier
{
  protected static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractDBStoreVerifier.class);

  private IRepository repository;

  private IDBStoreAccessor accessor = null;

  public AbstractDBStoreVerifier(IRepository repository)
  {
    this.repository = repository;
    assertTrue(repository.getStore() instanceof IDBStore);
  }

  protected IRepository getRepository()
  {
    return repository;
  }

  protected IDBStore getStore()
  {
    return (IDBStore)repository.getStore();
  }

  protected Statement getStatement()
  {
    if (accessor == null)
    {
      accessor = (IDBStoreAccessor)repository.getStore().getReader(null);
    }

    return accessor.getJDBCDelegate().getStatement();
  }

  protected List<IClassMapping> getClassMappings()
  {
    ArrayList<IClassMapping> result = new ArrayList<IClassMapping>();
    for (CDOPackage pkg : repository.getPackageManager().getPackages())
    {
      // CDO core package is not mapped in horizontal mapping
      if (pkg.equals(repository.getPackageManager().getCDOCorePackage()))
      {
        continue;
      }

      for (CDOClass cls : pkg.getClasses())
      {
        result.add(getStore().getMappingStrategy().getClassMapping(cls));
      }
    }

    return result;
  }

  protected void cleanUp()
  {
    if (accessor != null)
    {
      accessor.release();
    }
  }

  public void verify() throws VerificationException
  {
    try
    {
      TRACER.format("Starting {0} ...", getClass().getSimpleName());
      doVerify();
      TRACER.format("{0} completed without complaints ...", getClass().getSimpleName());

    }
    catch (Exception e)
    {
      throw new VerificationException(e);
    }
    finally
    {
      cleanUp();
    }
  }

  protected void sqlDump(String sql)
  {
    ResultSet rs = null;
    try
    {
      TRACER.format("Dumping output of {0}", sql);
      rs = getStatement().executeQuery(sql);
      int numCol = rs.getMetaData().getColumnCount();

      StringBuilder row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append(String.format("%10s | ", rs.getMetaData().getColumnLabel(c)));
      }

      TRACER.trace(row.toString());

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------+--");
      }

      TRACER.trace(row.toString());

      while (rs.next())
      {
        row = new StringBuilder();
        for (int c = 1; c <= numCol; c++)
        {
          row.append(String.format("%10s | ", rs.getString(c)));
        }

        TRACER.trace(row.toString());
      }

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------+-");
      }

      TRACER.trace(row.toString());
    }
    catch (SQLException ex)
    {
      // NOP
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (SQLException ex)
        {
          // NOP
        }
      }
    }
  }

  protected abstract void doVerify() throws Exception;

  /**
   * @author Stefan Winkler
   */
  public static class VerificationException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public VerificationException(String message)
    {
      super(message);
    }

    public VerificationException(String message, Throwable t)
    {
      super(message, t);
    }

    public VerificationException(Throwable t)
    {
      super(t);
    }
  }
}
