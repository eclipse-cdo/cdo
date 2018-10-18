/*
 * Copyright (c) 2010-2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerBrowser.AbstractPage;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.spi.db.ddl.InternalDBNamedElement.DumpFormat;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class DBSchemaPage extends AbstractPage
{
  public DBSchemaPage()
  {
    super("schema", "DB Schema");
  }

  public boolean canDisplay(InternalRepository repository)
  {
    return repository.getStore() instanceof IDBStore;
  }

  public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out)
  {
    IDBStore store = (IDBStore)repository.getStore();
    IDBSchema schema = store.getDBSchema();

    OutputStreamWriter writer = null;

    try
    {
      writer = new DumpFormat.HTML(out);
      DBUtil.dump(schema, writer);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      IOUtil.close(writer);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String TYPE = "dbschema";

    public Factory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    public DBSchemaPage create(String description) throws ProductCreationException
    {
      return new DBSchemaPage();
    }
  }
}
