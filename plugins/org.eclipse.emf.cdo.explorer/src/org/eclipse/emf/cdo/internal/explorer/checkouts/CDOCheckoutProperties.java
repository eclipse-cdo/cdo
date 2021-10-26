/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout.ObjectType;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryProperties;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutProperties extends Properties<CDOCheckout>
{
  public static final IProperties<CDOCheckout> INSTANCE = new CDOCheckoutProperties();

  public static final String CATEGORY_CHECKOUT = "Checkout"; //$NON-NLS-1$

  private CDOCheckoutProperties()
  {
    super(CDOCheckout.class);

    add(new Property<CDOCheckout>("state")
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getState();
      }
    });

    add(new Property<CDOCheckout>("open", "Open", "Whether this checkout is open", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.isOpen();
      }
    });
    add(new Property<CDOCheckout>("type", "Type", "The type of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getType();
      }
    });

    add(new Property<CDOCheckout>("id", "ID", "The ID of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getID();
      }
    });

    add(new Property<CDOCheckout>("label", "Label", "The label of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getLabel();
      }
    });

    add(new Property<CDOCheckout>("folder", "Folder", "The folder of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return ((AbstractElement)checkout).getFolder();
      }
    });

    add(new Property<CDOCheckout>("prefetch", "Prefetch", "Whether the contents of this checkout are prefetched", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.isPrefetch();
      }
    });

    add(new Property<CDOCheckout>("rootType")
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getRootType();
      }
    });

    add(new Property<CDOCheckout>("canContainResources")
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        ObjectType rootType = checkout.getRootType();
        if (rootType != null)
        {
          switch (rootType)
          {
          case Root:
          case Folder:
            return true;
          }
        }

        return false;
      }
    });

    add(new Property<CDOCheckout>("branchID", "Branch ID", "The ID of the branch of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getBranchID();
      }
    });

    add(new Property<CDOCheckout>("branch", "Branch", "The branch of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getBranchPath();
      }
    });

    add(new Property<CDOCheckout>("timeStamp", "Time Stamp", "The time stamp of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return CDOCommonUtil.formatTimeStamp(checkout.getTimeStamp());
      }
    });

    add(new Property<CDOCheckout>("readOnly", "Read-Only", "Whether this checkout is read-only", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.isReadOnly();
      }
    });

    add(new Property<CDOCheckout>("rootID", "Root ID", "The ID of the root object of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getRootID();
      }
    });

    add(new Property<CDOCheckout>("dirty", "Dirty", "Whether this checkout is dirty", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.isDirty();
      }
    });

    add(new Property<CDOCheckout>("repositoryConnected", "Connected", "Whether the repository of this checkout is connected",
        CDORepositoryProperties.CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        CDORepository repository = checkout.getRepository();
        if (repository != null)
        {
          return repository.isConnected();
        }

        return null;
      }
    });

    add(new Property<CDOCheckout>("repositoryType", "Type", "The type of the repository of this checkout", CDORepositoryProperties.CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        CDORepository repository = checkout.getRepository();
        if (repository != null)
        {
          return repository.getType();
        }

        return null;
      }
    });

    add(new Property<CDOCheckout>("repositoryID", "ID", "The ID of the repository of this checkout", CDORepositoryProperties.CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        CDORepository repository = checkout.getRepository();
        if (repository != null)
        {
          return repository.getID();
        }

        return null;
      }
    });

    add(new Property<CDOCheckout>("repositoryLabel", "Label", "The label of the repository of this checkout", CDORepositoryProperties.CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        CDORepository repository = checkout.getRepository();
        if (repository != null)
        {
          return repository.getLabel();
        }

        return null;
      }
    });

    add(new Property<CDOCheckout>("repositoryFolder", "Folder", "The folder of the repository of this checkout", CDORepositoryProperties.CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        AbstractElement repository = (AbstractElement)checkout.getRepository();
        if (repository != null)
        {
          return repository.getFolder();
        }

        return null;
      }
    });

    add(new Property<CDOCheckout>("repositoryURI", "URI", "The URI of the repository of this checkout", CDORepositoryProperties.CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        CDORepository repository = checkout.getRepository();
        if (repository != null)
        {
          return repository.getURI();
        }

        return null;
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
  public static final class Tester extends DefaultPropertyTester<CDOCheckout>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.explorer.checkout";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
