/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.evolution.bundle;

import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.spi.evolution.AbstractMigrationContext;
import org.eclipse.emf.cdo.server.spi.evolution.EvolutionSupport;
import org.eclipse.emf.cdo.spi.server.CDOCommand;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.om.monitor.Monitor;

import org.eclipse.emf.common.util.URI;

/**
 * @author Eike Stepper
 */
public class CDOMigrateCommand extends CDOCommand.WithAccessor
{
  public CDOMigrateCommand()
  {
    super("migrate", "migrate the contents of a repository to a new release of the models",
        new CommandParameter[] { CDOCommand.parameter("evolution-uri"), CDOCommand.optional("release-version") });
  }

  @Override
  public void execute(InternalRepository repository, IStoreAccessor accessor, String[] args) throws Exception
  {
    if (accessor instanceof EvolutionSupport)
    {
      EvolutionSupport evolutionSupport = (EvolutionSupport)accessor;

      URI evolutionURI = "reset".equals(args[0]) ? null : URI.createURI(args[0]);
      int releaseVersion = args[1] == null ? AbstractMigrationContext.LATEST_RELEASE_VERSION : Integer.parseInt(args[1]);

      AbstractMigrationContext context = createMigrationContext(evolutionURI, releaseVersion);
      context.migrate(evolutionSupport, new Monitor());
    }
    else
    {
      println("The repository " + repository.getName() + " does not support model evolution");
    }
  }

  protected AbstractMigrationContext createMigrationContext(URI evolutionURI, int releaseVersion)
  {
    return new AbstractMigrationContext(evolutionURI, releaseVersion)
    {
      public void log(Object msg)
      {
        println(msg);
      }
    };
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public Factory()
    {
      super(CDOCommand.PRODUCT_GROUP, "migrate");
    }

    public CDOCommand.WithAccessor create(String description) throws ProductCreationException
    {
      return new CDOMigrateCommand();
    }
  }
}
