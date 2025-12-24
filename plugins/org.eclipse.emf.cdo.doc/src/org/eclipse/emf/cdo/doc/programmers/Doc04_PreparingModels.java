/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface;
import org.eclipse.emf.cdo.doc.users.Doc07_UsingModels.Doc_EditingModelElementsEditor;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

/**
 * Preparing the Models
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 * @number 4
 */
public class Doc04_PreparingModels
{
  /**
   * Creating an Ecore Model
   * <p>
   * There's really not much to say about this step. The .ecore file for CDO models is the same as for pure EMF models.
   * Use the Empty EMF Project New Wizard to create an initial project for your model:
   * <p align="center">{@image EmptyEMFProject.png}
   * <p>
   * Create an ordinary Ecore model file in the models folder.
   * The model1 example model in the usual Ecore model editor looks like follows:
   * <p align="center">{@image Model1Ecore.png}
   * <p>
   * The XML representation of this Ecore model is:
   * {@link #companyEcoreModel()}
   * <p>
   * The model project should look similar to this, now:
   * <p align="center">{@image Model1Project.png}
   */
  public class Doc_CreatingEcore
  {
    /**
     * @snip xml ../../../../../../../../org.eclipse.emf.cdo.examples.company/model/company.ecore
     */
    public void companyEcoreModel()
    {
    }
  }

  /**
   * Using the CDO Model Importer
   * <p>
   * The easiest way to create a CDO enabled GenModel is to use the CDO Migrator utility that is shipped with the CDO SDK.
   * It includes a special Ecore Model Importer that adjusts all the GenModel properties needed to generated CDO native models.
   * Right-click the Ecore model file and select New and Other... and choose the EMF Generator Model New Wizard:
   * <p align="center">{@image Migrator0.png}
   * <p align="center">{@image Migrator1.png}
   * <p>
   * On the next page, the Select a Model Importer page, select the Ecore model (CDO native) importer:
   * <p align="center">{@image Migrator2.png}
   * <p>
   * On the next page, the Ecore Import page, click the Load button:
   * <p align="center">{@image Migrator3.png}
   * <p>
   * On the next page, the Package Selection page, adjust the settings depending on your model and its referenced models:
   * <p align="center">{@image Migrator4.png}
   * <p>
   * After clicking the Finish button your model project should look similar to this (please note that the CDO marker
   * file META-INF/CDO.MF has also been created by the importer):
   * <p align="center">{@image Migrator5.png}
   */
  public class Doc_UsingImporter
  {
  }

  /**
   * Using the CDO Model Migrator
   * <p>
   * If you don't want to use the CDO Model Importer to automatically let a proper GenModel be created for you it
   * is still rather easy to migrate an existing GenModel with the CDO Migrator:
   * <p align="center">{@image Migrator6.png}
   * <p>
   * In case the generator model was successfully migrated to CDO the following dialog box will appear:
   * <p align="center">{@image Migrator7.png}
   * <p>
   * Proceed with Generate The Model.
   */
  public class Doc_UsingMigrator
  {
  }

  /**
   * Migrating a GenModel Manually
   * <p>
   * If you don't want to use the CDO Model Importer to automatically let a proper GenModel be created for you
   * it is still rather easy to migrate an existing GenModel by hand.
   * <p>
   * The EMF generator model for your Ecore model is much like a usual GenModel except for the following four differences:
   * <ul>
   * <li> The <i>Feature Delegation</i> property <b>must be</b> set to <code>Reflective</code>
   * <li> The <i>Model Plug-in Variables</i> property <b>should be</b> set to <code>CDO=org.eclipse.emf.cdo</code>
   * <li> The <i>Root Extends Class</i> property <b>must be</b> set to {@link CDOObjectImpl org.eclipse.emf.internal.cdo.CDOObjectImpl}
   * <li> The <i>Root Extends Interface</i> property <b>can be</b> set to {@link CDOObject org.eclipse.emf.cdo.CDOObject}
   * </ul>
   * <p align="center">{@image GenModel.png}
   * <p>
   * Note that you do not need to generate an editor if you want to use your model with the {@link Doc01_UserInterface CDO User Interface}
   * A dedicated {@link Doc_EditingModelElementsEditor model editor} is only needed if you plan to use your model with normal XML based files as well.
   * Even in this scenario it could be simpler to use the EMF Reflective Model Editor though.
   * <p>
   * The XML representation of this GenModel is:
   * {@link #companyGenModel()}
   */
  public class Doc_MigratingManually
  {
    /**
     * @snip xml ../../../../../../../../../org.eclipse.emf.cdo.examples.company/model/company.genmodel
     */
    public void companyGenModel()
    {
    }
  }

  /**
   * Generating a Model
   * <p>
   * Generate the Java code for your model as you are used to do it:
   * <p align="center">{@image GenerateTheModel.png}
   * <p>
   * The result of the generation can look similar to this (some artifacts are hidden to remove noise from the Package Explorer):
   * <p align="center">{@image GeneratorResults.png}
   */
  public class Doc_GeneratingModel
  {
  }

  /**
   * Modifying Generated Getters and Setters
   * <p>
   * If you want to modify the behavior of generated getters and setters (or have already done so in existing models)
   * you might want to try <i>dynamic feature delegation</i> (introduced in EMF 2.5). With this pattern, the reflective methods like eGet still
   * call your generated method like getX() and then that calls the dynamic reflective method like eDynamicGet. It effectively produces
   * the same behavior as "Reflective" delegating but does so by delegating through your generated accessors allowing you to specialize
   * those as you could when you used "None"...
   */
  public class Doc_ModifyingGeneratedCode
  {
  }
}
