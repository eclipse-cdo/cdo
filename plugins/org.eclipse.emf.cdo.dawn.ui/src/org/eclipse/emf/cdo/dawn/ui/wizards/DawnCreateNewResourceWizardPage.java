/*******************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.ui.wizards;

import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.ui.views.DawnWizardPageItemProvider;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.emf.common.util.URI;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import java.util.Date;

/**
 * @author Martin Fluegge
 */
public class DawnCreateNewResourceWizardPage extends WizardPage
{

  public static int VALIDATION_NONE = 0;

  public static int VALIDATION_WARN = 1;

  public static int VALIDATION_ERROR = 2;

  protected Text resourcePathText;

  protected Text resourceText;

  private Composite container;

  private TreeViewer viewer;

  private ContainerItemProvider<IContainer<Object>> itemProvider;

  private boolean showResources;

  private final String fileExtension;

  private String resourceNamePrefix = "default";

  private final CDOView view;

  private boolean createAutomaticResourceName;

  private int resourceValidationType = VALIDATION_ERROR;

  public DawnCreateNewResourceWizardPage(String fileExtension)
  {
    this(fileExtension, true, null);
  }

  public DawnCreateNewResourceWizardPage(String fileExtension, boolean showResources, CDOView view)
  {
    super("First Page");
    this.view = view;
    setTitle("Create a Dawn Resource");
    setDescription("Creates Dawn Resource");
    this.showResources = showResources;
    this.fileExtension = fileExtension;

  }

  public void createControl(Composite parent)
  {
    container = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 1;

    createResourcePathInput();

    itemProvider = createContainerItemProvider();

    viewer = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    viewer.setContentProvider(createContentProvider());
    viewer.setLabelProvider(createLabelProvider());
    viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
    viewer.setInput(getManagedContainer());
    viewer.addFilter(new ViewerFilter()
    {
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element)
      {
        if (element instanceof CDOResource)
        {
          return showResources;
        }

        return true;
      }
    });

    viewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        if (event.getSelection().isEmpty())
        {
          resourceText.setText("");
          return;
        }

        if (event.getSelection() instanceof IStructuredSelection)
        {
          IStructuredSelection selection = (IStructuredSelection)event.getSelection();
          Object element = selection.getFirstElement();
          if (element instanceof CDOResource)
          {
            String resourceName = ((CDOResource)element).getName();
            resourcePathText.setText(((CDOResource)element).getPath().replace(resourceName, ""));
            resourceText.setText(resourceName);
          }
          else if (element instanceof CDOResourceNode)
          {
            resourcePathText.setText(((CDOResourceNode)element).getPath());
          }
        }

        validatePage();
      }
    });

    createResourceInput();
    if (createAutomaticResourceName)
    {
      createAutomaticResourceName(fileExtension, view);
    }
    setControl(container);
    validatePage();
  }

  private void createResourceInput()
  {

    Composite nameGroup = new Composite(container, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginWidth = 0;
    nameGroup.setLayout(layout);
    nameGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

    Label label1 = new Label(nameGroup, SWT.NONE);
    label1.setText("File name:");

    resourceText = new Text(nameGroup, SWT.BORDER | SWT.SINGLE);
    resourceText.setText(getDefaultName() + "." + fileExtension);
    resourceText.addKeyListener(new KeyListener()
    {
      public void keyPressed(KeyEvent e)
      {
      }

      public void keyReleased(KeyEvent e)
      {
        validatePage();
      }
    });

    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    resourceText.setLayoutData(gd);
  }

  private GridData createResourcePathInput()
  {
    Label resourcePathLabel = new Label(container, SWT.NULL);
    resourcePathLabel.setText("Enter or select the parent folder: ");

    resourcePathText = new Text(container, SWT.BORDER | SWT.SINGLE);
    resourcePathText.setText("");
    resourcePathText.addKeyListener(new KeyListener()
    {
      public void keyPressed(KeyEvent e)
      {
      }

      public void keyReleased(KeyEvent e)
      {
        validatePage();
      }
    });
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    resourcePathText.setLayoutData(gd);
    return gd;
  }

  private void validatePage()
  {
    boolean valid = false;
    if (resourceText.getText().length() != 0)
    {
      setPageComplete(true);
      valid = true;
    }
    else
    {
      setErrorMessage("Please insert a name for the resource");
      valid = false;
    }
    if (view != null && resourceValidationType != VALIDATION_NONE)
    {
      try
      {
        if (view.hasResource(getURI().path()))
        {
          String newMessage = "A resource with the same name already exists!";
          if (resourceValidationType == VALIDATION_WARN)
          {
            setMessage(newMessage, IMessageProvider.WARNING);
            valid = true;
          }
          else if (resourceValidationType == VALIDATION_ERROR)
          {
            setErrorMessage("A resource with the same name already exists!");
            valid = false;
          }
        }
      }
      catch (Exception e)
      {
        setErrorMessage(e.getMessage());
      }
    }
    if (valid)
    {
      setErrorMessage(null);
    }

    setPageComplete(valid);
  }

  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new DawnWizardPageItemProvider<IContainer<Object>>(new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof CDOSession;
      }
    });
  }

  protected IBaseLabelProvider createLabelProvider()
  {
    ILabelDecorator labelDecorator = createLabelDecorator();
    return new DecoratingLabelProvider(itemProvider, labelDecorator);
  }

  protected IContentProvider createContentProvider()
  {
    return itemProvider;
  }

  protected ILabelDecorator createLabelDecorator()
  {
    return PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
  }

  protected IManagedContainer getManagedContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  // public URI getURI()
  // {
  // return URI.createURI("cdo://" + resourcePathText.getText() + "/" + resourceText.getText());
  // }

  public URI getURI()
  {
    String resourcePath = resourcePathText.getText();
    if (resourcePath.length() > 0)
    {
      if (resourcePath.startsWith("/"))
      {
        resourcePath = resourcePath.substring(1, resourcePath.length());
      }
      if (!resourcePath.endsWith("/"))
      {
        resourcePath = resourcePath + "/";
      }
    }

    String resourceName = resourceText.getText();
    String authority = PreferenceConstants.getRepositoryName();
    URI uri = URI.createURI("cdo://" + authority + "/" + resourcePath + resourceName);

    return uri;
  }

  public void setShowResources(boolean showResources)
  {
    this.showResources = showResources;
  }

  public boolean isShowResources()
  {
    return showResources;
  }

  public String getDefaultName()
  {
    return getResourceNamePrefix();
  }

  public void setResourceNamePrefix(String resourceNamePrefix)
  {
    this.resourceNamePrefix = resourceNamePrefix;
    resourceText.setText(resourceNamePrefix);
  }

  public String getResourceNamePrefix()
  {
    return resourceNamePrefix;
  }

  public void setResourcePath(String text)
  {
    if (!text.endsWith("/") || !!text.endsWith("\\"))
    {
      text += "/";
    }
    resourcePathText.setText(text);
  }

  public String getResourcePath()
  {
    return resourcePathText.getText();
  }

  public void setCreateAutomaticResourceName(boolean createAutomaticResourceName)
  {
    this.createAutomaticResourceName = createAutomaticResourceName;
  }

  public boolean isCreateAutomaticResourceName()
  {
    return createAutomaticResourceName;
  }

  private void createAutomaticResourceName(String fileExtension, CDOView view)
  {
    int i = 2;
    while (i < 30 && view.hasResource(getURI().path()))
    {
      resourceText.setText(resourceNamePrefix + i + "." + fileExtension);
      i++;
    }

    if (i < 30)
    {
      return;
    }
    // if we have tried 30 times to find a new resource name and still not succeeded just add a timestamp to the name
    resourceText.setText(resourceNamePrefix + new Date().getTime() + "." + fileExtension);
  }

  public void setResourceValidationType(int resourceValidationType)
  {
    this.resourceValidationType = resourceValidationType;
  }

  public int getResourceValidationType()
  {
    return resourceValidationType;
  }
}
