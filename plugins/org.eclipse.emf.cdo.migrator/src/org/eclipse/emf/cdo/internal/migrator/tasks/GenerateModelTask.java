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

import org.eclipse.emf.cdo.internal.migrator.CDOMigratorUtil;

import org.eclipse.emf.codegen.ecore.CodeGenEcorePlugin;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.util.GenModelUtil;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.apache.tools.ant.BuildException;

/**
 * @author Eike Stepper
 */
public class GenerateModelTask extends CDOTask
{
  private String modelPath;

  public void setModelPath(String modelPath)
  {
    this.modelPath = modelPath;
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
    genModel.setCanGenerate(true);
    genModel.setValidateModel(true);

    Diagnostic diagnostic = genModel.diagnose();
    if (diagnostic.getSeverity() != Diagnostic.OK)
    {
      System.err.println(diagnostic);
      throw new BuildException(diagnostic.getException());
    }

    Monitor monitor = verbose ? new BasicMonitor.Printing(System.out) : BasicMonitor.toMonitor(new NullProgressMonitor());

    Generator generator = GenModelUtil.createGenerator(genModel);
    generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, CodeGenEcorePlugin.INSTANCE.getString("_UI_ModelProject_name"), monitor);
  }
}
