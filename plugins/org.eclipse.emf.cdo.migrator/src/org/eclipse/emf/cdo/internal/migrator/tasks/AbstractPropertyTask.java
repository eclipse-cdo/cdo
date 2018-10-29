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
package org.eclipse.emf.cdo.internal.migrator.tasks;

import org.apache.tools.ant.BuildException;

/**
 * @author Eike Stepper
 */
public abstract class AbstractPropertyTask extends CDOTask
{
  private String property;

  private String value;

  public void setProperty(String property)
  {
    this.property = property;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  protected boolean isPropertyRequired()
  {
    return false;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    if (isPropertyRequired())
    {
      assertTrue("'property' must be specified.", isSet(property));
    }
  }

  @Override
  protected final void doExecute() throws Exception
  {
    String result = doCompute();
    if (isSet(result) && isSet(property))
    {
      if (isSet(value))
      {
        result = value;
      }

      getProject().setNewProperty(property, result);
    }
  }

  protected abstract String doCompute() throws Exception;
}
