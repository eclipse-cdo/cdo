/*
 * Copyright (c) 2018 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.migrator.tasks;

import org.eclipse.emf.cdo.migrator.util.CDOMigratorUtil;

import org.eclipse.emf.codegen.ecore.genmodel.GenDelegationKind;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class MigrateModelTask extends CDOTask
{
  private File location;

  private boolean dynamicFeatureDelegation;

  public void setLocation(File location)
  {
    this.location = location;
  }

  public void setDynamicFeatureDelegation(boolean dynamicFeatureDelegation)
  {
    this.dynamicFeatureDelegation = dynamicFeatureDelegation;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    assertTrue("'location' must be specified.", location != null);
    assertTrue("'location' must be point to an existing file.", location.isFile());
  }

  @Override
  protected void doExecute() throws Exception
  {
    GenModel genModel = CDOMigratorUtil.getGenModel(location.getAbsolutePath());
    GenDelegationKind featureDelegation = dynamicFeatureDelegation ? GenDelegationKind.DYNAMIC_LITERAL : GenDelegationKind.REFLECTIVE_LITERAL;

    String result = CDOMigratorUtil.adjustGenModel(genModel, featureDelegation);
    System.out.println(result);
  }
}
