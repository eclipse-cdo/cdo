/*
 * Copyright (c) 2010-2013, 2016, 2018, 2019, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 420540
 */
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.cdo.common.lock.IDurableLockingManager;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.ILobCleanup.LobCleanupNotSupportedException;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.server.CDOCommand;
import org.eclipse.emf.cdo.spi.server.CDOCommand.CommandException;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOCommandProvider implements CommandProvider
{
  private static final String NEW_LINE = "\r\n"; //$NON-NLS-1$

  private static final CDOCommand list = new CDOCommand("list", "list all active repositories")
  {
    @Override
    public void execute(String[] args) throws Exception
    {
      IManagedContainer container = CDOServerApplication.getContainer();
      for (Object element : container.getElements(RepositoryFactory.PRODUCT_GROUP))
      {
        if (element instanceof InternalRepository)
        {
          InternalRepository repository = (InternalRepository)element;
          println(repository.getName());
        }
      }
    }
  };

  private static final CDOCommand start = new CDOCommand("start", "start repositories from a config file", CDOCommand.parameter("config-file"))
  {
    @Override
    public void execute(String[] args) throws Exception
    {
      String configFile = args[0];

      IManagedContainer container = CDOServerApplication.getContainer();
      RepositoryConfigurator repositoryConfigurator = new RepositoryConfigurator(container);
      IRepository[] repositories = repositoryConfigurator.configure(new File(configFile));

      println("Repositories started:");
      if (repositories != null)
      {
        for (IRepository repository : repositories)
        {
          println(repository.getName());
        }
      }
    }
  };

  private static final CDOCommand stop = new CDOCommand.WithRepository("stop", "stop a repository")
  {
    @Override
    public void execute(InternalRepository repository, String[] args) throws Exception
    {
      LifecycleUtil.deactivate(repository);
      println("Repository stopped");
    }
  };

  private static final CDOCommand exportFile = new CDOCommand.WithRepository("export", "export the contents of a repository to a file",
      CDOCommand.parameter("export-file"), CDOCommand.optional("xml|bin").literal(), CDOCommand.optional("/<branch-path>").literal(),
      CDOCommand.optional("time-stamp"), CDOCommand.optional("withSystemPackages").literal())
  {
    @Override
    public void execute(InternalRepository repository, String[] args) throws Exception
    {
      String exportFile = args[0];
      boolean binary = false;
      boolean withSystemPackages = false;
      String branchPath = null;
      Long timeStamp = null;

      for (int i = 1; i < args.length; i++)
      {
        String arg = args[i];

        if (arg != null)
        {
          if (arg.equalsIgnoreCase("bin"))
          {
            binary = true;
          }
          else if (arg.equalsIgnoreCase("withSystemPackages"))
          {
            withSystemPackages = true;
          }
          else if (arg.startsWith("/"))
          {
            branchPath = arg;
          }
          else
          {
            try
            {
              timeStamp = Long.valueOf(arg);
            }
            catch (NumberFormatException ex)
            {
              timeStamp = null;
            }
          }
        }
      }

      OutputStream out = null;

      try
      {
        out = new FileOutputStream(exportFile);

        CDOServerExporter<?> exporter = binary ? new CDOServerExporter.Binary(repository) : new CDOServerExporter.XML(repository);

        if (branchPath != null)
        {
          exporter.setBranchPath(branchPath);
        }

        if (timeStamp != null)
        {
          exporter.setTimeStamp(timeStamp);
        }

        if (withSystemPackages)
        {
          exporter.setExportSystemPackages(true);
        }

        exporter.exportRepository(out);

        println("Repository exported.");
        exporter.getStatistics().dumpStrings(line -> println(line));
        println("Took " + duration());
      }
      finally
      {
        IOUtil.close(out);
      }
    }
  };

  private static final CDOCommand importFile = new CDOCommand.WithRepository("import", "import the contents of a repository from a file",
      CDOCommand.parameter("import-file"), CDOCommand.optional("xml|bin").literal())
  {
    @Override
    public void execute(InternalRepository repository, String[] args) throws Exception
    {
      String importFile = args[0];
      boolean binary = "bin".equalsIgnoreCase(args[1]);

      InputStream in = null;

      try
      {
        in = new FileInputStream(importFile);
        LifecycleUtil.deactivate(repository);

        CDOServerImporter importer = binary ? new CDOServerImporter.Binary(repository) : new CDOServerImporter.XML(repository);
        importer.importRepository(in);

        IManagedContainer container = CDOServerApplication.getContainer();
        CDOServerUtil.addRepository(container, repository);

        println("Repository imported.");
        importer.getStatistics().dumpStrings(line -> println(line));
        println("Took " + duration());
      }
      finally
      {
        IOUtil.close(in);
      }
    }
  };

  private static final CDOCommand branches = new CDOCommand.WithRepository("branches", "list the branches of a repository")
  {
    @Override
    public void execute(InternalRepository repository, String[] args) throws Exception
    {
      branches(repository.getBranchManager().getMainBranch(), "");
    }

    private void branches(InternalCDOBranch branch, String prefix)
    {
      println(prefix + branch);
      prefix += CDOCommand.INDENT;
      for (InternalCDOBranch child : branch.getBranches())
      {
        branches(child, prefix);
      }
    }
  };

  private static final CDOCommand packages = new CDOCommand.WithRepository("packages", "list the packages of a repository")
  {
    @Override
    public void execute(InternalRepository repository, String[] args) throws Exception
    {
      InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
      for (InternalCDOPackageUnit packageUnit : packageRegistry.getPackageUnits())
      {
        println(packageUnit);
        for (InternalCDOPackageInfo packageInfo : packageUnit.getPackageInfos())
        {
          println(CDOCommand.INDENT + packageInfo);
        }
      }
    }
  };

  private static final CDOCommand sessions = new CDOCommand.WithRepository("sessions", "list the sessions of a repository")
  {
    @Override
    public void execute(InternalRepository repository, String[] args) throws Exception
    {
      InternalSessionManager sessionManager = repository.getSessionManager();
      for (InternalSession session : sessionManager.getSessions())
      {
        println(session);
        for (InternalView view : session.getViews())
        {
          println(INDENT + view);
        }
      }
    }
  };

  private static final CDOCommand locks = new CDOCommand.WithRepository("locks", "list the locks of a repository", CDOCommand.optional("username-prefix"))
  {
    @Override
    public void execute(InternalRepository repository, String[] args) throws Exception
    {
      String usernamePrefix = args[0];

      repository.getLockingManager().getLockAreas(usernamePrefix, new IDurableLockingManager.LockArea.Handler()
      {
        @Override
        public boolean handleLockArea(IDurableLockingManager.LockArea area)
        {
          println(area.getDurableLockingID());
          println(INDENT + "userID = " + area.getUserID());
          println(INDENT + "branch = " + area.getBranch());
          println(INDENT + "timeStamp = " + CDOCommonUtil.formatTimeStamp(area.getTimeStamp()));
          println(INDENT + "readOnly = " + area.isReadOnly());
          println(INDENT + "locks = " + area.getLocks());
          return true;
        }
      });
    }
  };

  private static final CDOCommand deletelocks = new CDOCommand.WithRepository("deletelocks", "delete a durable locking area of a repository",
      CDOCommand.parameter("area-id"))
  {
    @Override
    public void execute(InternalRepository repository, String[] args) throws Exception
    {
      String areaID = args[0];

      IView view = repository.getLockingManager().getLockOwner(areaID);
      if (view == null)
      {
        throw new CommandException("No view with durable locking ID " + areaID);
      }

      repository.unlockAdministratively((InternalView)view);
    }
  };

  private static final CDOCommand cleanuplobs = new CDOCommand.WithRepository("cleanuplobs", "clean up unreferenced LOBs in a repository",
      CDOCommand.parameter("dry-run", true).literal())
  {
    @Override
    public void execute(InternalRepository repository, String[] args) throws Exception
    {
      boolean dryRun = "dry-run".equalsIgnoreCase(args[0]);

      try
      {
        int deleted = repository.cleanupLobs(dryRun);

        if (dryRun)
        {
          println("Dry run: " + deleted + " unreferenced LOBs would be deleted");
        }
        else
        {
          println(deleted + " unreferenced LOBs deleted");
        }
      }
      catch (LobCleanupNotSupportedException ex)
      {
        println(ex.getMessage());
      }
    }
  };

  public CDOCommandProvider(BundleContext bundleContext)
  {
    bundleContext.registerService(CommandProvider.class.getName(), this, null);
  }

  public Object _cdo(CommandInterpreter interpreter)
  {
    try
    {
      Map<String, CDOCommand> commands = getCommands();
      String cmd = interpreter.nextArgument();

      CDOCommand command = commands.get(cmd);
      if (command != null)
      {
        try
        {
          command.setInterpreter(interpreter);
          command.execute();
          return null;
        }
        finally
        {
          command.setInterpreter(null);
        }
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

  @Override
  public String getHelp()
  {
    StringBuilder builder = new StringBuilder();

    try
    {
      builder.append("---CDO commands---" + NEW_LINE);

      List<CDOCommand> commands = new ArrayList<>(getCommands().values());
      Collections.sort(commands, new Comparator<CDOCommand>()
      {
        @Override
        public int compare(CDOCommand o1, CDOCommand o2)
        {
          return o1.getName().compareTo(o2.getName());
        }
      });

      for (CDOCommand command : commands)
      {
        try
        {
          builder.append(CDOCommand.INDENT);
          builder.append(command.getSyntax());
          builder.append(" - ");
          builder.append(command.getDescription());
          builder.append(NEW_LINE);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    return builder.toString();
  }

  public synchronized Map<String, CDOCommand> getCommands()
  {
    Map<String, CDOCommand> commands = new HashMap<>();
    addCommand(commands, list);
    addCommand(commands, start);
    addCommand(commands, stop);
    addCommand(commands, exportFile);
    addCommand(commands, importFile);
    addCommand(commands, branches);
    addCommand(commands, deletelocks);
    addCommand(commands, locks);
    addCommand(commands, cleanuplobs);
    addCommand(commands, packages);
    addCommand(commands, sessions);

    try
    {
      for (String name : IPluginContainer.INSTANCE.getFactoryTypes(CDOCommand.PRODUCT_GROUP))
      {
        try
        {
          CDOCommand command = createCommand(name);
          addCommand(commands, command);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    return commands;
  }

  protected CDOCommand createCommand(String name)
  {
    return (CDOCommand)IPluginContainer.INSTANCE.getElement(CDOCommand.PRODUCT_GROUP, name, null);
  }

  private void addCommand(Map<String, CDOCommand> commands, CDOCommand command)
  {
    commands.put(command.getName(), command);
  }
}
