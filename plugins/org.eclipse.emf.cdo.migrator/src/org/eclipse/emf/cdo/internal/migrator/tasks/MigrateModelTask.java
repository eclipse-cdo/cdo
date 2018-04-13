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
package org.eclipse.emf.cdo.internal.migrator.tasks;

import org.eclipse.emf.cdo.internal.migrator.CDOMigratorUtil;

import org.eclipse.emf.codegen.ecore.genmodel.GenDelegationKind;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class MigrateModelTask extends CDOTask
{
  private File modelLocation;

  private boolean dynamicFeatureDelegation;

  public void setModelLocation(File modelLocation)
  {
    this.modelLocation = modelLocation;
  }

  public void setDynamicFeatureDelegation(boolean dynamicFeatureDelegation)
  {
    this.dynamicFeatureDelegation = dynamicFeatureDelegation;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    assertTrue("'modelLocation' must be specified.", modelLocation != null);
    assertTrue("'modelLocation' must be point to an existing file.", modelLocation.isFile());
  }

  @Override
  protected void doExecute() throws Exception
  {
    GenModel genModel = CDOMigratorUtil.getGenModel(modelLocation.getAbsolutePath());
    GenDelegationKind featureDelegation = dynamicFeatureDelegation ? GenDelegationKind.DYNAMIC_LITERAL : GenDelegationKind.REFLECTIVE_LITERAL;

    String result = CDOMigratorUtil.adjustGenModel(genModel, featureDelegation);
    System.out.println(result);
  }
}
