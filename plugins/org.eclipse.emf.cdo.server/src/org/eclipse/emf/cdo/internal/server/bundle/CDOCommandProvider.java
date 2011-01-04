/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - https://bugs.eclipse.org/333260
 */
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import org.osgi.framework.BundleContext;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class CDOCommandProvider implements CommandProvider
{
  public CDOCommandProvider(BundleContext bundleContext)
  {
    bundleContext.registerService(CommandProvider.class.getName(), this, null);
  }

  public String getHelp()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("---CDO commands---\n");
    buffer.append("\tcdo list - list all active repositories\n");
    buffer.append("\tcdo export - export the contents of a repository to an XML file\n");
    return buffer.toString();
  }

  public Object _cdo(CommandInterpreter interpreter)
  {
    try
    {
      String cmd = interpreter.nextArgument();
      if ("list".equals(cmd))
      {
        list(interpreter);
        return null;
      }

      if ("export".equals(cmd))
      {
        exportXML(interpreter);
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

  protected void list(CommandInterpreter interpreter) throws Exception
  {
    IManagedContainer container = IPluginContainer.INSTANCE;
    for (Object element : container.getElements(RepositoryFactory.PRODUCT_GROUP))
    {
      if (element instanceof IRepository)
      {
        IRepository repository = (IRepository)element;
        interpreter.println(repository.getName());
      }
    }
  }

  protected void exportXML(CommandInterpreter interpreter) throws Exception
  {
    String syntax = "Syntax: cdo export <repository-name> <export-file>";
    IRepository repository = getRepository(interpreter, syntax);
    String exportFile = nextArgument(interpreter, syntax);
    OutputStream out = null;

    try
    {
      out = new FileOutputStream(exportFile);

      CDOServerExporter.XML exporter = new CDOServerExporter.XML(repository);
      exporter.exportRepository(out);
      interpreter.println("Repository exported");
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  private String nextArgument(CommandInterpreter interpreter, String syntax)
  {
    String argument = interpreter.nextArgument();
    if (argument == null)
    {
      throw new CommandException(syntax);
    }

    return argument;
  }

  private IRepository getRepository(CommandInterpreter interpreter, String syntax)
  {
    String repositoryName = nextArgument(interpreter, syntax);
    IRepository repository = getRepository(repositoryName);
    if (repository == null)
    {
      throw new CommandException("Repository not found: " + repositoryName);
    }

    return repository;
  }

  private IRepository getRepository(String name)
  {
    IManagedContainer container = IPluginContainer.INSTANCE;
    for (Object element : container.getElements(RepositoryFactory.PRODUCT_GROUP))
    {
      if (element instanceof IRepository)
      {
        IRepository repository = (IRepository)element;
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
