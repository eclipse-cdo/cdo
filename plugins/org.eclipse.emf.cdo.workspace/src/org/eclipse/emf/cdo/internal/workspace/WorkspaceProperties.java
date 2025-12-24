/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.workspace;

import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class WorkspaceProperties extends Properties<CDOWorkspace>
{
  public static final IProperties<CDOWorkspace> INSTANCE = new WorkspaceProperties();

  private WorkspaceProperties()
  {
    super(CDOWorkspace.class);

    add(new Property<CDOWorkspace>("open", //$NON-NLS-1$
        "Open", "Whether this workspace is open or not.")
    {
      @Override
      protected Object eval(CDOWorkspace workspace)
      {
        return !workspace.isClosed();
      }
    });

    add(new Property<CDOWorkspace>("branchPath", //$NON-NLS-1$
        "Branch", "The remote branch of this workspace.")
    {
      @Override
      protected Object eval(CDOWorkspace workspace)
      {
        return workspace.getBranchPath();
      }
    });

    add(new Property<CDOWorkspace>("timeStamp", //$NON-NLS-1$
        "Time Stamp", "The time stamp of this workspace.")
    {
      @Override
      protected Object eval(CDOWorkspace workspace)
      {
        return CDOCommonUtil.formatTimeStamp(workspace.getTimeStamp());
      }
    });

    add(new Property<CDOWorkspace>("fixed", //$NON-NLS-1$
        "Fixed", "Whether this workspace is fixed to a specific remote time stamp or not.")
    {
      @Override
      protected Object eval(CDOWorkspace workspace)
      {
        return workspace.isFixed();
      }
    });

    add(new Property<CDOWorkspace>("dirty", //$NON-NLS-1$
        "Dirty", "Whether this workspace is locally dirty or not.")
    {
      @Override
      protected Object eval(CDOWorkspace workspace)
      {
        return workspace.isDirty();
      }
    });

    add(new Property<CDOWorkspace>("idGenerationLocation", //$NON-NLS-1$
        "ID Generation Location", "One of STORE, CLIENT.")
    {
      @Override
      protected Object eval(CDOWorkspace workspace)
      {
        return ((InternalCDOWorkspace)workspace).getIDGenerationLocation();
      }
    });

    add(new Property<CDOWorkspace>("base", //$NON-NLS-1$
        "Base", "The base of this workspace.")
    {
      @Override
      protected Object eval(CDOWorkspace workspace)
      {
        return ((InternalCDOWorkspace)workspace).getBase();
      }
    });

    add(new Property<CDOWorkspace>("local", //$NON-NLS-1$
        "Local", "The local repository of this workspace.")
    {
      @Override
      protected Object eval(CDOWorkspace workspace)
      {
        return ((InternalCDOWorkspace)workspace).getLocalRepository();
      }
    });

    add(new Property<CDOWorkspace>("remote", //$NON-NLS-1$
        "Remote", "The remote repository of this workspace.")
    {
      @Override
      protected Object eval(CDOWorkspace workspace)
      {
        return ((InternalCDOWorkspace)workspace).getRemoteSessionConfigurationFactory();
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
  public static final class Tester extends DefaultPropertyTester<CDOWorkspace>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.workspace";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
