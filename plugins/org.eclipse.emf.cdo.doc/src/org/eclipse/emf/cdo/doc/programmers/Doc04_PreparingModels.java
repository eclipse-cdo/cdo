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
   * The CDO SDK contributes an Ecore model importer to the EMF Generator Model wizard. It creates a
   * GenModel configured for CDO-native generated objects and adjusts CDO-specific generator
   * properties. Choose this importer when generated instances should participate directly in CDO's
   * object and revision APIs. An ordinary EMF model can also be used in legacy mode, but generated
   * objects then need CDO's adaptation layer and do not necessarily implement {@code CDOObject}.
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
   * To convert an existing GenModel, select the CDO Model Migrator action contributed by the SDK.
   * It adjusts the GenModel in place; review and save those changes before regenerating model code.
   * Keep generated code synchronized with the updated GenModel rather than editing generated
   * implementation classes by hand.
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
   * If you migrate a GenModel manually, use the current CDO migrator defaults as the reference. It
   * adjusts feature delegation, generated base types, and generator flags so generated objects use
   * CDO's reflective feature storage path. The relevant properties are:
   * <ul>
   * <li> The <i>Feature Delegation</i> property is set to <code>Reflective</code>.
   * <li> The <i>Root Extends Class</i> property is set to {@link CDOObjectImpl org.eclipse.emf.internal.cdo.CDOObjectImpl}.
   *       The migrator currently selects this implementation base for generated native models. It
   *       is a generator/runtime dependency, not an application API; application code should use
   *       the public {@link CDOObject} contract.
   * <li> The <i>Root Extends Interface</i> property is set to {@link CDOObject org.eclipse.emf.cdo.CDOObject}.
   * <li> Model plug-in variables include <code>CDO=org.eclipse.emf.cdo</code>. If generating an edit
   *       plug-in, edit plug-in variables include <code>CDO_EDIT=org.eclipse.emf.cdo.edit</code> and
   *       the provider root class is <code>org.eclipse.emf.cdo.edit.CDOItemProviderAdapter</code>.
   * <li> Boolean flags are disabled and packed enums are disabled, as configured by the migrator.
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
     * @snip xml ../../../../../../../../org.eclipse.emf.cdo.examples.company/model/company.genmodel
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
   * Reflective feature delegation lets CDO intercept generated model feature access and store values
   * in revision data. Custom getters and setters must preserve that path; switching delegation to
   * <code>None</code> can make generated Java fields diverge from repository-backed state. Keep
   * custom behavior in generated accessor overrides that delegate to the compatible reflective
   * implementation, and verify the generated code against the EMF/CDO versions in use.
   */
  public class Doc_ModifyingGeneratedCode
  {
  }
}
