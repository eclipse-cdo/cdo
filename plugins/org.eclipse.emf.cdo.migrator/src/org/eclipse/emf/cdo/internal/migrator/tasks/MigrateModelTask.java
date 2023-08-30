/*
 * Copyright (c) 2018, 2023 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.internal.migrator.messages.Messages;

import org.eclipse.emf.codegen.ecore.genmodel.GenDelegationKind;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import org.apache.tools.ant.BuildException;

/**
 * @author Eike Stepper
 */
public class MigrateModelTask extends CDOTask
{
  private String modelPath;

  private boolean dynamicFeatureDelegation;

  public void setModelPath(String modelPath)
  {
    this.modelPath = modelPath;
  }

  public void setDynamicFeatureDelegation(boolean dynamicFeatureDelegation)
  {
    this.dynamicFeatureDelegation = dynamicFeatureDelegation;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    assertTrue("'modelPath' must be specified.", modelPath != null && modelPath.length() != 0);
  }

  @Override
  protected void doExecute() throws Exception
  {
    GenModel genModel = CDOMigratorUtil.getGenModel(modelPath);
    GenDelegationKind featureDelegation = dynamicFeatureDelegation ? GenDelegationKind.DYNAMIC_LITERAL : GenDelegationKind.REFLECTIVE_LITERAL;

    String msg = CDOMigratorUtil.adjustGenModel(genModel, featureDelegation);
    if (msg == null)
    {
      verbose(Messages.getString("MigrateAction_3"));
    }
    else
    {
      genModel.eResource().save(null);
      verbose(Messages.getString("MigrateAction_4") + msg);
    }
  }
}
