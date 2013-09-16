/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.hibernate.internal.teneo.bundle;

import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.spi.server.CDOCommand;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class ExportHbmCommand extends CDOCommand.WithRepository
{
  public static final String NAME = "exporthbm";

  public ExportHbmCommand()
  {
    super(NAME, "export generated hibernate mapping to a file", parameter("export-file"));
  }

  @Override
  public void execute(InternalRepository repository, String[] args) throws Exception
  {
    String exportFile = args[0];
    OutputStream out = null;

    try
    {
      out = new FileOutputStream(exportFile);

      final HibernateStore store = (HibernateStore)repository.getStore();
      final String mapping = store.getMappingXml();

      out.write(mapping.getBytes());
      println("Hibernate mapping exported");
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends CDOCommand.Factory
  {
    public Factory()
    {
      super(NAME);
    }

    @Override
    public CDOCommand create(String description) throws ProductCreationException
    {
      return new ExportHbmCommand();
    }
  }
}
