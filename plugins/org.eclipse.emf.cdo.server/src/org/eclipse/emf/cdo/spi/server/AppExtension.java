/*
 * Copyright (c) 2020, 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

/**
 * @author Eike Stepper
 * @since 4.10
 */
public abstract class AppExtension extends AbstractAppExtension implements IAppExtension3, IAppExtension5
{
  public AppExtension()
  {
  }

  @Override
  public abstract String getName();

  @Override
  public boolean startBeforeRepositories()
  {
    return false;
  }

  @Override
  public final void start(File configFile) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void stop() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void start(IRepository[] repositories, File configFile) throws Exception
  {
    Document document = getDocument(configFile);

    RepositoryConfigurator.forEachChildElement(document.getDocumentElement(), "repository", repositoryConfig -> {
      String repositoryName = getAttribute(repositoryConfig, "name"); //$NON-NLS-1$

      InternalRepository repository = getRepository(repositories, repositoryName);
      if (repository != null)
      {
        try
        {
          start(repository, repositoryConfig);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    });
  }

  @Override
  public void stop(IRepository[] repositories) throws Exception
  {
    for (IRepository repository : repositories)
    {
      try
      {
        stop((InternalRepository)repository);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  protected abstract void start(InternalRepository repository, Element repositoryConfig) throws Exception;

  protected abstract void stop(InternalRepository repository) throws Exception;
}
