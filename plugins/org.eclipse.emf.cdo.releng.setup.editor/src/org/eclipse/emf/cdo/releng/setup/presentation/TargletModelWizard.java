/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.presentation;

import org.eclipse.emf.cdo.releng.internal.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.Targlet;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ISetSelectionTarget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a simple wizard for creating a new model file.
 */
public class TargletModelWizard extends Wizard implements INewWizard
{
  /**
   * The supported extensions for created files.
   */
  public static final List<String> FILE_EXTENSIONS = Collections.unmodifiableList(Arrays
      .asList(SetupEditorPlugin.INSTANCE.getString("_UI_TargletEditorFilenameExtensions").split("\\s*,\\s*")));

  /**
   * A formatted list of supported file extensions, suitable for display.
   */
  public static final String FORMATTED_FILE_EXTENSIONS = SetupEditorPlugin.INSTANCE.getString(
      "_UI_TargletEditorFilenameExtensions").replaceAll("\\s*,\\s*", ", ");

  /**
   * This caches an instance of the model package.
   */
  protected SetupPackage setupPackage = SetupPackage.eINSTANCE;

  /**
   * This caches an instance of the model factory.
   */
  protected SetupFactory setupFactory = setupPackage.getSetupFactory();

  /**
   * This is the file creation page.
   */
  protected SetupModelWizardNewFileCreationPage newFileCreationPage;

  /**
   * Remember the selection during initialization for populating the default container.
   */
  protected IStructuredSelection selection;

  /**
   * Remember the workbench during initialization.
   */
  protected IWorkbench workbench;

  /**
   * Caches the names of the types that can be created as the root object.
   */
  protected List<String> initialObjectNames;

  /**
   * This just records the information.
   */
  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    this.workbench = workbench;
    this.selection = selection;
    setWindowTitle(SetupEditorPlugin.INSTANCE.getString("_UI_TargletModelWizard_label"));
    setDefaultPageImageDescriptor(ExtendedImageRegistry.INSTANCE.getImageDescriptor(SetupEditorPlugin.INSTANCE
        .getImage("full/wizban/targlet_wiz")));
  }

  /**
   * The framework calls this to create the contents of the wizard.
   */
  @Override
  public void addPages()
  {
    // Create a page, set the title, and the initial model file name.
    //
    newFileCreationPage = new SetupModelWizardNewFileCreationPage("Whatever", selection);
    newFileCreationPage.setTitle(SetupEditorPlugin.INSTANCE.getString("_UI_TargletModelWizard_label"));
    newFileCreationPage.setDescription(SetupEditorPlugin.INSTANCE.getString("_UI_TargletModelWizard_description"));
    newFileCreationPage.setFileName(SetupEditorPlugin.INSTANCE.getString("_UI_TargletEditorFilenameDefaultBase") + "."
        + FILE_EXTENSIONS.get(0));
    addPage(newFileCreationPage);

    // Try and get the resource selection to determine a current directory for the file dialog.
    //
    if (selection != null && !selection.isEmpty())
    {
      // Get the resource...
      //
      Object selectedElement = selection.iterator().next();
      if (selectedElement instanceof IResource)
      {
        // Get the resource parent, if its a file.
        //
        IResource selectedResource = (IResource)selectedElement;
        if (selectedResource.getType() == IResource.FILE)
        {
          selectedResource = selectedResource.getParent();
        }

        // This gives us a directory...
        //
        if (selectedResource instanceof IFolder || selectedResource instanceof IProject)
        {
          // Set this for the container.
          //
          newFileCreationPage.setContainerFullPath(selectedResource.getFullPath());

          // Make up a unique new name here.
          //
          String defaultModelBaseFilename = SetupEditorPlugin.INSTANCE
              .getString("_UI_TargletEditorFilenameDefaultBase");
          String defaultModelFilenameExtension = FILE_EXTENSIONS.get(0);
          String modelFilename = defaultModelBaseFilename + "." + defaultModelFilenameExtension;
          for (int i = 1; ((IContainer)selectedResource).findMember(modelFilename) != null; ++i)
          {
            modelFilename = defaultModelBaseFilename + i + "." + defaultModelFilenameExtension;
          }
          newFileCreationPage.setFileName(modelFilename);
        }
      }
    }
  }

  /**
   * Get the file from the page.
   */
  public IFile getModelFile()
  {
    return newFileCreationPage.getModelFile();
  }

  /**
   * Returns the names of the types that can be created as the root object.
   */
  protected Collection<String> getInitialObjectNames()
  {
    if (initialObjectNames == null)
    {
      initialObjectNames = new ArrayList<String>();
      for (EClassifier eClassifier : setupPackage.getEClassifiers())
      {
        if (eClassifier instanceof EClass)
        {
          EClass eClass = (EClass)eClassifier;
          if (!eClass.isAbstract())
          {
            initialObjectNames.add(eClass.getName());
          }
        }
      }
      Collections.sort(initialObjectNames, CommonPlugin.INSTANCE.getComparator());
    }
    return initialObjectNames;
  }

  @Override
  public boolean canFinish()
  {
    if (!newFileCreationPage.isPageComplete())
    {
      return false;
    }

    return true;
  }

  /**
   * Do the work after everything is specified.
   */
  @Override
  public boolean performFinish()
  {
    try
    {
      // Remember the file.
      //
      final IFile modelFile = getModelFile();

      // Do the work within an operation.
      //
      WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
      {
        @Override
        protected void execute(IProgressMonitor progressMonitor)
        {
          try
          {
            // Create a resource set
            //
            ResourceSet resourceSet = EMFUtil.createResourceSet();

            // Get the URI of the model file.
            //
            URI fileURI = URI.createPlatformResourceURI(modelFile.getFullPath().toString(), true);

            // Create a resource for this file.
            //
            Resource resource = resourceSet.createResource(fileURI);

            // Add the initial model object to the contents.
            //
            String name = modelFile.getName();
            int pos = name.lastIndexOf('.');
            if (pos != -1)
            {
              name = name.substring(0, pos);
            }

            Targlet targlet = SetupFactory.eINSTANCE.createTarglet();
            targlet.setName(name);

            resource.getContents().add(targlet);

            // Save the contents of the resource to the file system.
            //
            Map<Object, Object> options = new HashMap<Object, Object>();
            options.put(XMLResource.OPTION_ENCODING, "UTF-8");
            resource.save(options);
          }
          catch (Exception exception)
          {
            SetupEditorPlugin.INSTANCE.log(exception);
          }
          finally
          {
            progressMonitor.done();
          }
        }
      };

      getContainer().run(false, false, operation);

      // Select the new file resource in the current view.
      //
      IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
      IWorkbenchPage page = workbenchWindow.getActivePage();
      final IWorkbenchPart activePart = page.getActivePart();
      if (activePart instanceof ISetSelectionTarget)
      {
        final ISelection targetSelection = new StructuredSelection(modelFile);
        getShell().getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            ((ISetSelectionTarget)activePart).selectReveal(targetSelection);
          }
        });
      }

      // Open an editor on the new file.
      //
      try
      {
        page.openEditor(new FileEditorInput(modelFile),
            workbench.getEditorRegistry().getDefaultEditor(modelFile.getFullPath().toString()).getId());
      }
      catch (PartInitException exception)
      {
        MessageDialog.openError(workbenchWindow.getShell(),
            SetupEditorPlugin.INSTANCE.getString("_UI_OpenEditorError_label"), exception.getMessage());
        return false;
      }

      return true;
    }
    catch (Exception exception)
    {
      SetupEditorPlugin.INSTANCE.log(exception);
      return false;
    }
  }

  /**
   * This is the one page of the wizard.
   */
  public class SetupModelWizardNewFileCreationPage extends WizardNewFileCreationPage
  {
    /**
     * Pass in the selection.
     */
    public SetupModelWizardNewFileCreationPage(String pageId, IStructuredSelection selection)
    {
      super(pageId, selection);
    }

    /**
     * The framework calls this to see if the file is correct.
     */
    @Override
    protected boolean validatePage()
    {
      if (super.validatePage())
      {
        String extension = new Path(getFileName()).getFileExtension();
        if (extension == null || !FILE_EXTENSIONS.contains(extension))
        {
          String key = FILE_EXTENSIONS.size() > 1 ? "_WARN_FilenameExtensions" : "_WARN_FilenameExtension";
          setErrorMessage(SetupEditorPlugin.INSTANCE.getString(key, new Object[] { FORMATTED_FILE_EXTENSIONS }));
          return false;
        }
        return true;
      }
      return false;
    }

    public IFile getModelFile()
    {
      return ResourcesPlugin.getWorkspace().getRoot().getFile(getContainerFullPath().append(getFileName()));
    }
  }
}
