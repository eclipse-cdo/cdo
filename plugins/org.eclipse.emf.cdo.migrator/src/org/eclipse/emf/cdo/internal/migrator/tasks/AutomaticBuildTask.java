/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator.tasks;

import org.eclipse.core.resources.IWorkspaceDescription;

import org.apache.tools.ant.BuildException;

/**
 * @author Eike Stepper
 */
public class AutomaticBuildTask extends AbstractPropertyTask
{
  private Boolean enable;

  private String _if;

  public void setEnable(Boolean enable)
  {
    this.enable = enable;
  }

  public void setIf(String _if)
  {
    this._if = _if;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    super.checkAttributes();
    assertTrue("'enable' must be specified.", enable != null);
  }

  @Override
  protected String doCompute() throws Exception
  {
    if (isSet(_if))
    {
      if (!TRUE.equals(getProject().getProperty(_if)))
      {
        return FALSE;
      }
    }

    IWorkspaceDescription description = workspace.getDescription();
    boolean wasEnabled = description.isAutoBuilding();

    if (wasEnabled == enable)
    {
      return FALSE;
    }

    description.setAutoBuilding(enable);
    workspace.setDescription(description);

    verbose("Automatic build " + (enable ? "enabled" : "disabled"));
    return TRUE;
  }
}
