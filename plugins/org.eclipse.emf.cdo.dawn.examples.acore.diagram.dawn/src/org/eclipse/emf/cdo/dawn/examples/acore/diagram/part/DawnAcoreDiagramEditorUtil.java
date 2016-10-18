/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.part;

import org.eclipse.emf.cdo.dawn.commands.CreateSemanticResourceRecordingCommand;
import org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.DawnACoreRootEditPart;
import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.transaction.DawnGMFEditingDomainFactory;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Martin Fluegge
 */
public class DawnAcoreDiagramEditorUtil extends AcoreDiagramEditorUtil
{

  // static CDOSession session;

  /**
   * @generated
   */
  public static boolean openDiagram(Resource diagram) throws PartInitException
  {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    return null != page.openEditor(new DawnEditorInput(diagram.getURI()), DawnAcoreDiagramEditor.ID);
  }

  /**
   * Runs the wizard in a dialog.
   *
   * @generated
   */
  public static void runWizard(Shell shell, Wizard wizard, String settingsKey)
  {
    IDialogSettings pluginDialogSettings = AcoreDiagramEditorPlugin.getInstance().getDialogSettings();
    IDialogSettings wizardDialogSettings = pluginDialogSettings.getSection(settingsKey);
    if (wizardDialogSettings == null)
    {
      wizardDialogSettings = pluginDialogSettings.addNewSection(settingsKey);
    }
    wizard.setDialogSettings(wizardDialogSettings);
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.create();
    dialog.getShell().setSize(Math.max(500, dialog.getShell().getSize().x), 500);
    dialog.open();
  }

  @SuppressWarnings("deprecation")
  public static Resource createDiagram(URI diagramURI, URI modelURI, IProgressMonitor progressMonitor)
  {
    TransactionalEditingDomain editingDomain = DawnGMFEditingDomainFactory.getInstance().createEditingDomain();

    progressMonitor.beginTask(Messages.AcoreDiagramEditorUtil_CreateDiagramProgressTask, 3);

    CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(), PreferenceConstants.getServerName());
    CDOConnectionUtil.instance.openSession();
    ResourceSet resourceSet = editingDomain.getResourceSet();

    AcoreDiagramEditorPlugin.getInstance().logInfo("URI toString: " + diagramURI.toString());
    AcoreDiagramEditorPlugin.getInstance().logInfo("URI authority: " + diagramURI.authority());

    CDOTransaction transaction = CDOConnectionUtil.instance.openCurrentTransaction(resourceSet, diagramURI.toString());

    final Resource diagramResource = resourceSet.createResource(diagramURI);

    AcoreDiagramEditorPlugin.getInstance().logInfo("Diagram URI: " + diagramURI);
    AcoreDiagramEditorPlugin.getInstance().logInfo("Diagram Resource: " + diagramResource);

    CreateSemanticResourceRecordingCommand createSemanticResourceCommand = new CreateSemanticResourceRecordingCommand(editingDomain, transaction,
        modelURI.path());

    editingDomain.getCommandStack().execute(createSemanticResourceCommand);
    final Resource modelResource = createSemanticResourceCommand.getResource();
    AcoreDiagramEditorPlugin.getInstance().logInfo("Model URI: " + modelURI);
    AcoreDiagramEditorPlugin.getInstance().logInfo("Model Resource: " + modelResource);

    final String diagramName = diagramURI.lastSegment();
    AbstractTransactionalCommand command = new AbstractTransactionalCommand(editingDomain, Messages.AcoreDiagramEditorUtil_CreateDiagramCommandLabel,
        Collections.EMPTY_LIST)
    {
      @Override
      protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
      {
        EObject model = null;
        if (modelResource.getContents().size() > 0)
        {
          model = modelResource.getContents().get(0);
        }
        else
        {
          model = createInitialModel();
          attachModelToResource(model, modelResource);
        }

        Diagram diagram = ViewService.createDiagram(model, DawnACoreRootEditPart.MODEL_ID, AcoreDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
        if (diagram != null)
        {
          diagramResource.getContents().add(diagram);
          diagram.setName(diagramName);
          diagram.setElement(model);
        }

        try
        {
          modelResource.save(Collections.EMPTY_MAP);
          diagramResource.save(Collections.EMPTY_MAP);
        }
        catch (IOException e)
        {
          AcoreDiagramEditorPlugin.getInstance().logError("Unable to store model and diagram resources", e); //$NON-NLS-1$
        }

        return CommandResult.newOKCommandResult();
      }
    };

    try
    {
      OperationHistoryFactory.getOperationHistory().execute(command, new org.eclipse.core.runtime.SubProgressMonitor(progressMonitor, 1), null);
    }
    catch (ExecutionException e)
    {
      AcoreDiagramEditorPlugin.getInstance().logError("Unable to create model and diagram", e); //$NON-NLS-1$
    }
    setCharset(WorkspaceSynchronizer.getFile(modelResource));
    setCharset(WorkspaceSynchronizer.getFile(diagramResource));
    return diagramResource;
  }

  /**
   * Create a new instance of domain element associated with canvas. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static EObject createInitialModel()
  {
    return AcoreFactory.eINSTANCE.createACoreRoot();
  }

  /**
   * Store model element in the resource. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static void attachModelToResource(EObject model, Resource resource)
  {
    resource.getContents().add(model);
  }

  // /**
  // * @generated
  // */
  // public static void selectElementsInDiagram(IDiagramWorkbenchPart diagramPart, List/* EditPart */editParts)
  // {
  // diagramPart.getDiagramGraphicalViewer().deselectAll();
  //
  // EditPart firstPrimary = null;
  // for (Iterator it = editParts.iterator(); it.hasNext();)
  // {
  // EditPart nextPart = (EditPart)it.next();
  // diagramPart.getDiagramGraphicalViewer().appendSelection(nextPart);
  // if (firstPrimary == null && nextPart instanceof IPrimaryEditPart)
  // {
  // firstPrimary = nextPart;
  // }
  // }
  //
  // if (!editParts.isEmpty())
  // {
  // diagramPart.getDiagramGraphicalViewer().reveal(firstPrimary != null ? firstPrimary : (EditPart)editParts.get(0));
  // }
  // }

  // /**
  // * @generated
  // */
  // private static int findElementsInDiagramByID(DiagramEditPart diagramPart, EObject element, List editPartCollector)
  // {
  // IDiagramGraphicalViewer viewer = (IDiagramGraphicalViewer)diagramPart.getViewer();
  // final int intialNumOfEditParts = editPartCollector.size();
  //
  // if (element instanceof View)
  // { // support notation element lookup
  // EditPart editPart = (EditPart)viewer.getEditPartRegistry().get(element);
  // if (editPart != null)
  // {
  // editPartCollector.add(editPart);
  // return 1;
  // }
  // }
  //
  // String elementID = EMFCoreUtil.getProxyID(element);
  // List associatedParts = viewer.findEditPartsForElement(elementID, IGraphicalEditPart.class);
  // // perform the possible hierarchy disjoint -> take the top-most parts only
  // for (Iterator editPartIt = associatedParts.iterator(); editPartIt.hasNext();)
  // {
  // EditPart nextPart = (EditPart)editPartIt.next();
  // EditPart parentPart = nextPart.getParent();
  // while (parentPart != null && !associatedParts.contains(parentPart))
  // {
  // parentPart = parentPart.getParent();
  // }
  // if (parentPart == null)
  // {
  // editPartCollector.add(nextPart);
  // }
  // }
  //
  // if (intialNumOfEditParts == editPartCollector.size())
  // {
  // if (!associatedParts.isEmpty())
  // {
  // editPartCollector.add(associatedParts.iterator().next());
  // }
  // else
  // {
  // if (element.eContainer() != null)
  // {
  // return findElementsInDiagramByID(diagramPart, element.eContainer(), editPartCollector);
  // }
  // }
  // }
  // return editPartCollector.size() - intialNumOfEditParts;
  // }

  // /**
  // * @generated
  // */
  // public static View findView(DiagramEditPart diagramEditPart, EObject targetElement,
  // LazyElement2ViewMap lazyElement2ViewMap)
  // {
  // boolean hasStructuralURI = false;
  // if (targetElement.eResource() instanceof XMLResource)
  // {
  // hasStructuralURI = ((XMLResource)targetElement.eResource()).getID(targetElement) == null;
  // }
  //
  // View view = null;
  // if (hasStructuralURI && !lazyElement2ViewMap.getElement2ViewMap().isEmpty())
  // {
  // view = (View)lazyElement2ViewMap.getElement2ViewMap().get(targetElement);
  // }
  // else if (findElementsInDiagramByID(diagramEditPart, targetElement, lazyElement2ViewMap.editPartTmpHolder) > 0)
  // {
  // EditPart editPart = (EditPart)lazyElement2ViewMap.editPartTmpHolder.get(0);
  // lazyElement2ViewMap.editPartTmpHolder.clear();
  // view = editPart.getModel() instanceof View ? (View)editPart.getModel() : null;
  // }
  //
  // return (view == null) ? diagramEditPart.getDiagramView() : view;
  // }

  // /**
  // * @generated
  // */
  // public static class LazyElement2ViewMap
  // {
  // /**
  // * @generated
  // */
  // private Map element2ViewMap;
  //
  // /**
  // * @generated
  // */
  // private View scope;
  //
  // /**
  // * @generated
  // */
  // private Set elementSet;
  //
  // /**
  // * @generated
  // */
  // public final List editPartTmpHolder = new ArrayList();
  //
  // /**
  // * @generated
  // */
  // public LazyElement2ViewMap(View scope, Set elements)
  // {
  // this.scope = scope;
  // this.elementSet = elements;
  // }
  //
  // /**
  // * @generated
  // */
  // public final Map getElement2ViewMap()
  // {
  // if (element2ViewMap == null)
  // {
  // element2ViewMap = new HashMap();
  // // map possible notation elements to itself as these can't be found by view.getElement()
  // for (Iterator it = elementSet.iterator(); it.hasNext();)
  // {
  // EObject element = (EObject)it.next();
  // if (element instanceof View)
  // {
  // View view = (View)element;
  // if (view.getDiagram() == scope.getDiagram())
  // {
  // element2ViewMap.put(element, element); // take only those that part of our diagram
  // }
  // }
  // }
  //
  // buildElement2ViewMap(scope, element2ViewMap, elementSet);
  // }
  // return element2ViewMap;
  // }
  //
  // /**
  // * @generated
  // */
  // static Map buildElement2ViewMap(View parentView, Map element2ViewMap, Set elements)
  // {
  // if (elements.size() == element2ViewMap.size())
  // return element2ViewMap;
  //
  // if (parentView.isSetElement() && !element2ViewMap.containsKey(parentView.getElement())
  // && elements.contains(parentView.getElement()))
  // {
  // element2ViewMap.put(parentView.getElement(), parentView);
  // if (elements.size() == element2ViewMap.size())
  // return element2ViewMap;
  // }
  //
  // for (Iterator it = parentView.getChildren().iterator(); it.hasNext();)
  // {
  // buildElement2ViewMap((View)it.next(), element2ViewMap, elements);
  // if (elements.size() == element2ViewMap.size())
  // return element2ViewMap;
  // }
  // for (Iterator it = parentView.getSourceEdges().iterator(); it.hasNext();)
  // {
  // buildElement2ViewMap((View)it.next(), element2ViewMap, elements);
  // if (elements.size() == element2ViewMap.size())
  // return element2ViewMap;
  // }
  // for (Iterator it = parentView.getSourceEdges().iterator(); it.hasNext();)
  // {
  // buildElement2ViewMap((View)it.next(), element2ViewMap, elements);
  // if (elements.size() == element2ViewMap.size())
  // return element2ViewMap;
  // }
  // return element2ViewMap;
  // }
  // } // LazyElement2ViewMap

}
