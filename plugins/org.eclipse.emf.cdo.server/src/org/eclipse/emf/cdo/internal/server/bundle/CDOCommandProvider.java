package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.cdo.internal.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    buffer.append("\tcdo start - start repositories from a config file\n");
    buffer.append("\tcdo stop - stop a repository\n");
    buffer.append("\tcdo export - export the contents of a repository to an XML file\n");
    buffer.append("\tcdo import - import the contents of a repository from an XML file\n");
    buffer.append("\tcdo list - list all active repositories\n");
    return buffer.toString();
  }

  public Object _cdo(CommandInterpreter interpreter)
  {
    try
    {
      String cmd = interpreter.nextArgument();
      if ("start".equals(cmd))
      {
        start(interpreter);
        return null;
      }

      if ("stop".equals(cmd))
      {
        stop(interpreter);
        return null;
      }

      if ("export".equals(cmd))
      {
        exportXML(interpreter);
        return null;
      }

      if ("import".equals(cmd))
      {
        importXML(interpreter);
        return null;
      }

      if ("list".equals(cmd))
      {
        list(interpreter);
        return null;
      }

      interpreter.println(getHelp());
    }
    catch (Exception ex)
    {
      interpreter.printStackTrace(ex);
    }

    return null;
  }

  private void start(CommandInterpreter interpreter) throws Exception
  {
    String configFile = interpreter.nextArgument();
    if (configFile == null)
    {
      interpreter.println("Syntax: cdo start <config-file>");
      return;
    }

    IPluginContainer container = CDOServerApplication.getContainer();
    RepositoryConfigurator repositoryConfigurator = new RepositoryConfigurator(container);
    IRepository[] repositories = repositoryConfigurator.configure(new File(configFile));

    interpreter.println("Repositories started:");
    if (repositories != null)
    {
      for (IRepository repository : repositories)
      {
        interpreter.println(repository.getName());
      }
    }
  }

  private void stop(CommandInterpreter interpreter) throws Exception
  {
    String repositoryName = interpreter.nextArgument();
    if (repositoryName == null)
    {
      interpreter.println("Syntax: cdo stop <repository-name>");
      return;
    }

    InternalRepository repository = getRepository(repositoryName);
    if (repository == null)
    {
      interpreter.println("Repository not found: " + repositoryName);
      return;
    }

    LifecycleUtil.deactivate(repository);
    interpreter.println("Repository stopped: " + repositoryName);
  }

  private void exportXML(CommandInterpreter interpreter) throws Exception
  {
    String repositoryName = interpreter.nextArgument();
    String exportFile = interpreter.nextArgument();
    if (repositoryName == null || exportFile == null)
    {
      interpreter.println("Syntax: cdo export <repository-name> <export-file>");
      return;
    }

    InternalRepository repository = getRepository(repositoryName);
    if (repository == null)
    {
      interpreter.println("Repository not found: " + repositoryName);
      return;
    }

    OutputStream out = null;

    try
    {
      out = new FileOutputStream(exportFile);

      CDOServerExporter.XML exporter = new CDOServerExporter.XML(repository);
      exporter.exportRepository(out);
      interpreter.println("Repository exported: " + repositoryName);
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  private void importXML(CommandInterpreter interpreter) throws Exception
  {
    String repositoryName = interpreter.nextArgument();
    String importFile = interpreter.nextArgument();
    if (repositoryName == null || importFile == null)
    {
      interpreter.println("Syntax: cdo import <repository-name> <import-file>");
      return;
    }

    InternalRepository repository = getRepository(repositoryName);
    if (repository == null)
    {
      interpreter.println("Repository not found: " + repositoryName);
      return;
    }

    InputStream in = null;

    try
    {
      in = new FileInputStream(importFile);
      LifecycleUtil.deactivate(repository);

      CDOServerImporter.XML importer = new CDOServerImporter.XML(repository);
      importer.importRepository(in);

      IPluginContainer container = CDOServerApplication.getContainer();
      CDOServerUtil.addRepository(container, repository);

      interpreter.println("Repository imported: " + repositoryName);
    }
    finally
    {
      IOUtil.close(in);
    }
  }

  private void list(CommandInterpreter interpreter) throws Exception
  {
    IPluginContainer container = CDOServerApplication.getContainer();
    for (Object element : container.getElements(RepositoryFactory.PRODUCT_GROUP))
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        interpreter.println(repository.getName());
      }
    }
  }

  private InternalRepository getRepository(String name)
  {
    IPluginContainer container = CDOServerApplication.getContainer();
    for (Object element : container.getElements(RepositoryFactory.PRODUCT_GROUP))
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
}
