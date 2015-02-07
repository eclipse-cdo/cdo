/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.properties;

import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class RepositoryProperties extends Properties<CDORepository>
{
  public static final IProperties<CDORepository> INSTANCE = new RepositoryProperties();

  private static final String CATEGORY_REPOSITORY = "Repository"; //$NON-NLS-1$

  private RepositoryProperties()
  {
    super(CDORepository.class);

    add(new Property<CDORepository>("state")
    {
      @Override
      protected Object eval(CDORepository repository)
      {
        return repository.getState();
      }
    });

    add(new Property<CDORepository>("connected", "Connected", "Whether this repository is connected or not",
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDORepository repository)
      {
        return repository.isConnected();
      }
    });

    add(new Property<CDORepository>("id", "ID", "The ID of this repository", CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDORepository repository)
      {
        return repository.getID();
      }
    });

    add(new Property<CDORepository>("folder", "Folder", "The folder of this repository", CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDORepository repository)
      {
        return ((AbstractElement)repository).getFolder();
      }
    });
  }

  public static void main(String[] args)
  {
    new Tester().dumpContributionMarkup();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tester extends DefaultPropertyTester<CDORepository>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.explorer.repository";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
