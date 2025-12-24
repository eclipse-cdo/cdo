/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.branch;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class BranchProperties extends Properties<CDOBranch>
{
  public static final IProperties<CDOBranch> INSTANCE = new BranchProperties();

  private static final String CATEGORY_BRANCH = "Branch"; //$NON-NLS-1$

  private BranchProperties()
  {
    super(CDOBranch.class);

    add(new Property<CDOBranch>("id", //$NON-NLS-1$
        "ID", "The ID of this branch.", CATEGORY_BRANCH)
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        return branch.getID();
      }
    });

    add(new Property<CDOBranch>("name", //$NON-NLS-1$
        "Name", "The name of this branch.", CATEGORY_BRANCH)
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        return branch.getName();
      }
    });

    add(new Property<CDOBranch>("pathName", //$NON-NLS-1$
        "Path Name", "The path name of this branch.", CATEGORY_BRANCH)
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        return branch.getPathName();
      }
    });

    add(new Property<CDOBranch>("baseBranchID", //$NON-NLS-1$
        "Base Branch ID", "The base branch ID of this branch.", CATEGORY_BRANCH)
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        CDOBranch baseBranch = branch.getBranch();
        if (baseBranch != null)
        {
          return baseBranch.getID();
        }

        return null;
      }
    });

    add(new Property<CDOBranch>("baseBranchID", //$NON-NLS-1$
        "Base Branch ID", "The base branch ID of this branch.", CATEGORY_BRANCH)
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        CDOBranch baseBranch = branch.getBranch();
        if (baseBranch != null)
        {
          return baseBranch.getID();
        }

        return null;
      }
    });

    add(new Property<CDOBranch>("baseTimeStamp", //$NON-NLS-1$
        "Base Time Stamp", "The base time stamp of this branch.", CATEGORY_BRANCH)
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        return CDOCommonUtil.formatTimeStamp(branch.getTimeStamp());
      }
    });

    add(new Property<CDOBranch>("deleted", //$NON-NLS-1$
        "Deleted", "Whether this branch is deleted.", CATEGORY_BRANCH)
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        return branch.isDeleted();
      }
    });

    add(new Property<CDOBranch>("local", //$NON-NLS-1$
        "Local", "Whether this branch is local.", CATEGORY_BRANCH)
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        return branch.isLocal();
      }
    });

    add(new Property<CDOBranch>("main") //$NON-NLS-1$
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        return branch.isMainBranch();
      }
    });

    add(new Property<CDOBranch>("proxy") //$NON-NLS-1$
    {
      @Override
      protected Object eval(CDOBranch branch)
      {
        return ((InternalCDOBranch)branch).isProxy();
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
  public static final class Tester extends DefaultPropertyTester<CDOBranch>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.branch";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
