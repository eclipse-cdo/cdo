/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Erdal Karaca - initial API and implementation
 */
package org.eclipse.emf.cdo.server.hibernate.internal.teneo.bundle;

import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import org.osgi.framework.BundleContext;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Provides a command to export the hbm file directly from the osgi prompt.
 * 
 * See:
 * 
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=378797
 * 
 * Exports the hbm through the following osgi command:
 * hibernate mapping repo1 /tmp/hbm.xml
 * 
 * @author Erdal Karaca
 */
public class HibernateCommandProvider implements CommandProvider
{
  private static final String NEW_LINE = "\r\n"; //$NON-NLS-1$

  private static final String INDENT = "   "; //$NON-NLS-1$

  public HibernateCommandProvider(BundleContext bundleContext)
  {
    bundleContext.registerService(CommandProvider.class.getName(), this, null);
  }

  public String getHelp()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("---CDO Hibernate commands---" + NEW_LINE);
    buffer.append(INDENT + "hibernate mapping - export generated hibernate file" + NEW_LINE);
    return buffer.toString();
  }

  public Object _hibernate(CommandInterpreter interpreter)
  {
    try
    {
      String cmd = interpreter.nextArgument();
      if ("mapping".equals(cmd))
      {
        exportHbm(interpreter);
        return null;
      }

      interpreter.println(getHelp());
    }
    catch (CommandException ex)
    {
      interpreter.println(ex.getMessage());
    }
    catch (Exception ex)
    {
      interpreter.printStackTrace(ex);
    }

    return null;
  }

  private void exportHbm(CommandInterpreter interpreter) throws Exception
  {
    String syntax = "Syntax: hibernate mapping <repository-name> <export-file>";
    InternalRepository repository = getRepository(interpreter, syntax);
    String exportFile = nextArgument(interpreter, syntax);
    final HibernateStore store = (HibernateStore)repository.getStore();
    OutputStream out = null;

    try
    {
      final String mapping = store.getMappingXml();
      out = new FileOutputStream(exportFile);
      out.write(mapping.getBytes());
      interpreter.println("Hibernate mapping exported");
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  private String nextArgument(CommandInterpreter interpreter, String syntax)
  {
    String argument = interpreter.nextArgument();
    if (argument == null && syntax != null)
    {
      throw new CommandException(syntax);
    }

    return argument;
  }

  private InternalRepository getRepository(CommandInterpreter interpreter, String syntax)
  {
    String repositoryName = nextArgument(interpreter, syntax);
    InternalRepository repository = getRepository(repositoryName);
    if (repository == null)
    {
      throw new CommandException("Repository not found: " + repositoryName);
    }

    return repository;
  }

  private InternalRepository getRepository(String name)
  {
    for (Object element : IPluginContainer.INSTANCE.getElements(RepositoryFactory.PRODUCT_GROUP))
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        if (repository.getName().equals(name))
        {
          return repository;
        }
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private static final class CommandException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public CommandException(String message)
    {
      super(message);
    }
  }
}
