/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.setup.helper.Progress;

import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;

/**
 * @author Eike Stepper
 */
public final class Variables
{
  public static void set(String name, String description, String value) throws Exception
  {
    IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
    IValueVariable variable = manager.getValueVariable(name);
    if (variable == null)
    {
      variable = manager.newValueVariable(name, description);
      manager.addVariables(new IValueVariable[] { variable });
    }

    String oldDescription = variable.getDescription();
    if (!description.equals(oldDescription))
    {
      variable.setDescription(description);
    }

    String oldValue = variable.getValue();
    if (!value.equals(oldValue))
    {
      Progress.log().addLine("Setting string substitution variable " + name + " = " + value);
      variable.setValue(value);
    }
  }
}
