/*******************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.ecoretools.diagram.part;

import org.eclipse.emf.cdo.dawn.ui.wizards.dialogs.CDOResourceNodeSelectionDialog;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecoretools.diagram.part.EcoreCreationWizardPage;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Text;

import java.lang.reflect.Field;

/**
 * @author Martin Fluegge
 */
public class DawnEcoreCreationWizardPage extends EcoreCreationWizardPage
{
  private final CDOView view;

  private URI semanticModelURI;

  public DawnEcoreCreationWizardPage(String pageName, IStructuredSelection selection, CDOView view)
  {
    super(pageName, selection);
    this.view = view;
  }

  @Override
  protected void handleDirectoryChoose()
  {
    CDOResourceNodeSelectionDialog dialog = new CDOResourceNodeSelectionDialog(getShell(), view);
    if (dialog.open() == Window.OK)
    {
      URI uri = dialog.getResults();
      if (uri != null)
      {
        // modelFd.setText(((IFile)results[0]).getFullPath().toString());

        // update the other widgets
        loadModelFile();
      }
    }
  }

  @Override
  protected void handleModelChoose()
  {
    CDOResourceNodeSelectionDialog dialog = new CDOResourceNodeSelectionDialog(getShell(), view, true);

    if (dialog.open() == Window.OK)
    {
      semanticModelURI = dialog.getResults();
      if (semanticModelURI != null)
      {
        Text modelFd = (Text)getField("modelFd");
        modelFd.setText(semanticModelURI.toString());

        // update the other widgets
        loadModelFile();
      }
    }
  }

  @Override
  protected boolean loadModelFile()
  {
    if (semanticModelURI == null)
    {
      return false;
    }

    Resource resource = getResourceSet().getResource(semanticModelURI, true);

    if (resource != null)
    {
      ComposedAdapterFactory adapterFactory = getAdapterFactory();

      TreeViewer viewer = (TreeViewer)getField("viewer");

      // Set the tree providers of the domain file contents
      AdapterFactoryContentProvider adapterContentProvider = new AdapterFactoryContentProvider(adapterFactory);
      adapterContentProvider.inputChanged(viewer, null, null);
      viewer.setContentProvider(new WizardContentProvider(adapterContentProvider));
      viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));

      viewer.setInput(resource.getContents());
      viewer.refresh();
      viewer.setSelection(new StructuredSelection(resource.getContents()), true);

      return true;
    }
    return false;
  }

  @Override
  protected ResourceSet getResourceSet()
  {
    return view.getResourceSet();
  }

  Object getField(String name)
  {
    final Field fields[] = this.getClass().getSuperclass().getDeclaredFields();
    for (int i = 0; i < fields.length; ++i)
    {
      if (name.equals(fields[i].getName()))
      {
        try
        {
          fields[i].setAccessible(true);
          return fields[i].get(this);
        }
        catch (IllegalAccessException ex)
        {
          throw new RuntimeException("IllegalAccessException accessing " + name);
        }
      }
    }
    return null;
  }

  // TODO need to overwrite this
  @Override
  protected boolean validatePage()
  {
    // erase all previous messages
    setMessage(null);
    setErrorMessage(null);

    // if (isNewModel())
    // {
    // return validateNewModelGroup();
    // }
    // else
    // {
    // return validateExistingModel();
    // }
    return true;
  }

  @Override
  public URI getDomainModelURI()
  {
    return semanticModelURI;
  }

  /**
   * Return the diagram model URI
   * 
   * @return URI
   */
  @Override
  public URI getDiagramModelURI()
  {
    // if (isNewModel())
    // {
    // return URI.createPlatformResourceURI(
    // directoryFd.getText().concat(File.separator).concat(getFileName()).concat(DIAGRAM_EXT), false);
    // }
    // return URI.createPlatformResourceURI(modelFd.getText().concat(DIAGRAM_EXT), false);
    return URI.createURI(semanticModelURI.toString() + "diag");
  }
}
