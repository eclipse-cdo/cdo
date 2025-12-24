/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.users;

import org.eclipse.emf.cdo.doc.online.EMFFormsGuide;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_ProjectExplorerIntegration;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_HistoricalCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc06_UsingResources.Doc_CreatingResourceNodes.Doc_CreatingModelResources;
import org.eclipse.emf.cdo.doc.users.Doc09_TechnicalBackground.Doc_BackgroundModelElements;
import org.eclipse.emf.cdo.doc.users.Doc09_TechnicalBackground.Doc_BackgroundTransactions;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transfer.CDOTransfer;
import org.eclipse.emf.cdo.ui.CDOEditorOpener;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * Working with Models and Model Elements
 * <p>
 * All modifications of model elements that are triggered in the {@link Doc_ProjectExplorerIntegration Project Explorer}
 * (as opposed to being triggered in a {@link Doc_EditingModelElementsEditor model editor})
 * are performed in a separate background {@link CDOTransaction transaction}, see {@link Doc_BackgroundTransactions} for details.
 * <p>
 * Modifying model elements is only possible in checkouts that are not read-only, i.e., not in {@link Doc_HistoricalCheckouts}.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc07_UsingModels
{
  /**
   * Creating Model Elements
   * <p>
   * New model elements can be created directly in the {@link Doc_ProjectExplorerIntegration Project Explorer}. The process is
   * slightly different depending on whether the new model element is supposed to be created at the root of an existing
   * {@link Doc_CreatingModelResources model resource}, in which case arbitrary element types are allowed, or whether it is supposed
   * to be created under an existing typed model element, in which case the choice of element types is already determined by the
   * type of the existing container element.
   * <p>
   * In any case, new model elements are created by selecting the container, i.e., a model resource
   * or an existing model element that can have children, opening the context menu and opening the New sub menu.
   * This sub menu looks different depending on the type of the container. It is explained in the following two sub sections.
   *
   * @see Doc_BackgroundModelElements
   */
  public class Doc_CreatingModelElements
  {
    /**
     * Creating Root Model Elements
     * <p>
     * When creating new model elements directly under an existing {@link Doc_CreatingModelResources model resource}
     * the New sub menu (see {@link Doc_CreatingModelElements}) looks as follows: <p align="center">{@image new-root-object.png}
     * <p>
     * The first group of the New sub menu contains sub menus for all {@link EPackage packages} that are already used in the repository of the
     * selected checkout. A package sub menu contains creation actions for all concrete {@link EClass classes} of that package.
     * <p>
     * The second group of the New sub menu contains the Other action, which, upon selection, will open the following dialog:
     * <p align="center">{@image new-root-object-dialog.png}
     * <p>
     * The New Root Object dialog is horizontally split.
     * The left side shows all registered packages with the icons of not-yet-loaded packages being grayed out. The filter field on top
     * of the package list can be used to narrow down the choice of packages.
     * The right side shows all concrete classes of the package select on the left side. The filter field on top
     * of the class list can be used to narrow down the choice of classes.
     * <p>
     * When a class is selected and OK is pressed an instance of that class is created and inserted at the root level of the
     * containing model resource. Double-clicking the class has the same effect.
     * <p>
     * CDO supports the creation of multiple root elements in model resources, both in the {@link Doc_ProjectExplorerIntegration}
     * and {@link Doc_EditingModelElementsEditor}.
     */
    public class Doc_CreatingRootElements
    {
    }

    /**
     * Creating Nested Model Elements
     * <p>
     * When creating new model elements under an existing model element the New sub menu (see {@link Doc_CreatingModelElements})
     * looks much simpler because the choice of element types is already determined by the
     * type of the existing container element: <p align="center">{@image new-object.png}
     * <p>
     * What element types are offered and how many instances of those types can be created under a given container element depends
     * solely on the type of that container element.
     */
    public class Doc_CreatingNestedElements
    {
    }
  }

  /**
   * Moving and Copying Model Elements within a Checkout
   * <p>
   * Model elements can be moved within the resource tree of a checkout by using drag and drop.
   * <p>
   * When holding the Ctrl key while dropping model elements onto a checkout or a resource folder copies of the dragged model elements
   * are created under the drop target.
   * <p>
   * Moving a model element to a different container <b>does not break</b> cross references to that model element or model elements
   * that are contained by the moved model element.
   */
  public class Doc_MovingModelElements
  {
  }

  /**
   * Moving and Copying Model Elements between Checkouts and Beyond
   * <p>
   * Moving and copying model elements beyond the current checkout with the {@link CDOTransfer resource transfer framework}
   * is not yet supported for checkouts.
   */
  public class Doc_TransferingModelElements
  {
  }

  /**
   * Deleting Model Elements
   * <p>
   * Model elements can be deleted from their direct container (i.e., a model resource or a containing model element)
   * by selecting the model element and pressing the Del key or opening the context menu and
   * selecting the Delete action. The following confirmation dialog will pop up: <p align="center">{@image object-delete.png}
   * <p>
   * This confirmation dialog only shows the directly selected model elements. Nested model elements
   * are not shown for performance reasons but will also be deleted if OK is pressed.
   * <p>
   * Deleting a model element <b>can break</b> cross references to that model element or model elements
   * that are contained by the deleted model element!
   */
  public class Doc_DeletingModelElements
  {
  }

  /**
   * Editing Model Elements in a Dialog
   * <p>
   * A model element can be edited directly in the {@link Doc_ProjectExplorerIntegration Project Explorer}
   * by double-clicking it or by selecting it and pressing the Enter key or opening the context menu and
   * selecting the Open action. An {@link EMFFormsGuide EMF Forms} dialog similar to the following will pop up:
   * <p align="center">{@image object-Edit.png}
   *
   * @see EMFFormsGuide
   */
  public class Doc_EditingModelElements
  {
  }

  /**
   * Editing Model Elements in an Editor
   * <p>
   * A model element can be edited  in a {@link CDOEditorOpener registered model editor} by double-clicking the containing
   * model resource of the model element or by selecting that resource and pressing
   * the Enter key or opening the context menu and selecting the Open action.
   * <p>
   * The effect of the Open action depends on the chosen editor. CDO's generic model editor, which is available for
   * all model resources, looks similar to the following: <p align="center">{@image model-editor.png}
   * <p>
   * All registered model editors open their own, separate {@link CDOTransaction transaction}, which is typically
   * {@link CDOTransaction#commit(org.eclipse.core.runtime.IProgressMonitor) committed} when the editor is saved.
   * See {@link Doc_BackgroundTransactions} for details on how transactions are typically used by editors.
   */
  public class Doc_EditingModelElementsEditor
  {
  }
}
