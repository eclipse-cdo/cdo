/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.composites;

import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.ui.messages.Messages;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.Date;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public class CDOResourceNodeChooserComposite extends Composite
{
  private final String fileExtension;

  protected Text resourcePathComposite;

  protected Text resourceNameComposite;

  private String resourceNamePrefix = "default"; //$NON-NLS-1$

  protected CDOResourceNodeSelectionWidget selectCDOResourceNodeComposite;

  private final CDOView view;

  private ResourceChooserValidator validator;

  public CDOResourceNodeChooserComposite(Composite parent, int style, String fileExtension, CDOView view)
  {
    super(parent, style);
    this.fileExtension = fileExtension;
    this.view = view;
    init();
    setValidator(new ResourceChooserValidator());
  }

  private void createLayout()
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginWidth = 0;
    setLayout(layout);
  }

  public void setResourcePath(String resourcePath)
  {
    resourcePathComposite.setText(resourcePath);
  }

  public void setResourceName(String resourceNamePrefix)
  {
    resourceNameComposite.setText(resourceNamePrefix);
  }

  protected void init()
  {
    createLayout();
    createResourcePathInput();

    createResourceSelectionComposite();

    createResourceInput();
  }

  public String getResourceName()
  {
    return resourceNameComposite.getText();
  }

  public String getResourcePath()
  {
    return resourcePathComposite.getText();
  }

  private void createResourceSelectionComposite()
  {
    selectCDOResourceNodeComposite = new CDOResourceNodeSelectionWidget(this, SWT.NONE);

    GridData gd = new GridData(GridData.FILL_BOTH);

    selectCDOResourceNodeComposite.setLayoutData(gd);

    selectCDOResourceNodeComposite.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        if (event.getSelection().isEmpty())
        {
          resourceNameComposite.setText(""); //$NON-NLS-1$
          return;
        }

        if (event.getSelection() instanceof IStructuredSelection)
        {
          IStructuredSelection selection = (IStructuredSelection)event.getSelection();
          Object element = selection.getFirstElement();
          if (element instanceof CDOResource)
          {
            String resourceName = ((CDOResource)element).getName();
            resourcePathComposite.setText(((CDOResource)element).getPath().replace(resourceName, "")); //$NON-NLS-1$
            resourceNameComposite.setText(resourceName);
          }
          else if (element instanceof CDOResourceNode)
          {
            resourcePathComposite.setText(((CDOResourceNode)element).getPath());
          }
        }

        getValidator().validate();
      }
    });
  }

  private void createResourceInput()
  {
    Composite nameGroup = new Composite(this, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginWidth = 0;
    nameGroup.setLayout(layout);
    nameGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

    Label label1 = new Label(nameGroup, SWT.NONE);
    label1.setText(Messages.DawnCreateNewResourceWizardPage_6);

    resourceNameComposite = new Text(nameGroup, SWT.BORDER | SWT.SINGLE);
    resourceNameComposite.setText(getDefaultName() + "." + fileExtension); //$NON-NLS-1$
    resourceNameComposite.addKeyListener(new KeyListener()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
      }

      @Override
      public void keyReleased(KeyEvent e)
      {
        getValidator().validate();
      }
    });

    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    resourceNameComposite.setLayoutData(gd);
  }

  private GridData createResourcePathInput()
  {
    Label resourcePathLabel = new Label(this, SWT.NULL);
    resourcePathLabel.setText(Messages.DawnCreateNewResourceWizardPage_8);

    resourcePathComposite = new Text(this, SWT.BORDER | SWT.SINGLE);
    resourcePathComposite.setText(""); //$NON-NLS-1$
    resourcePathComposite.addKeyListener(new KeyListener()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
      }

      @Override
      public void keyReleased(KeyEvent e)
      {
        getValidator().validate();
      }

    });
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    resourcePathComposite.setLayoutData(gd);
    return gd;
  }

  public void createAutomaticResourceName()
  {
    int i = 2;
    while (i < 30 && view.hasResource(getURI().path()))
    {
      resourceNameComposite.setText(getResourceNamePrefix() + i + "." + fileExtension); //$NON-NLS-1$
      i++;
    }

    if (i < 30)
    {
      return;
    }
    // if we have tried 30 times to find a new resource name and still not succeeded just add a timestamp to the name
    resourceNameComposite.setText(getResourceNamePrefix() + new Date().getTime() + "." + fileExtension); //$NON-NLS-1$
  }

  public URI getURI()
  {
    String resourcePath = getResourcePath();
    if (resourcePath.length() > 0)
    {
      if (resourcePath.startsWith("/")) //$NON-NLS-1$
      {
        resourcePath = resourcePath.substring(1, resourcePath.length());
      }
      if (!resourcePath.endsWith("/")) //$NON-NLS-1$
      {
        resourcePath = resourcePath + "/"; //$NON-NLS-1$
      }
    }

    String resourceName = getResourceName();
    String authority = PreferenceConstants.getRepositoryName();
    URI uri = URI.createURI("cdo://" + authority + "/" + resourcePath + resourceName); //$NON-NLS-1$ //$NON-NLS-2$

    return uri;
  }

  public String getDefaultName()
  {
    return getResourceNamePrefix();
  }

  public void showResources(boolean showResources)
  {
    selectCDOResourceNodeComposite.setShowResources(showResources);
  }

  public void setResourceNamePrefix(String resourceNamePrefix)
  {
    this.resourceNamePrefix = resourceNamePrefix;
  }

  public String getResourceNamePrefix()
  {
    return resourceNamePrefix;
  }

  public void setValidator(ResourceChooserValidator validator)
  {
    this.validator = validator;
  }

  public ResourceChooserValidator getValidator()
  {
    return validator;
  }

  /**
   * @author Martin Fluegge
   */
  public class ResourceChooserValidator
  {
    public static final int VALIDATION_NONE = 0;

    public static final int VALIDATION_WARN = 1;

    public static final int VALIDATION_ERROR = 2;

    private int resourceValidationType = VALIDATION_ERROR;

    private boolean valid;

    private int messageType = IMessageProvider.NONE;

    private String message;

    private ValidationListener validationListener;

    public ResourceChooserValidator()
    {
    }

    public void validate()
    {
      internalValidate();
      if (validationListener != null)
      {
        validationListener.validationFinished();
      }
    }

    private void internalValidate()
    {
      setValid(true);

      if (getResourceName().length() == 0)
      {
        setMessage(Messages.DawnCreateNewResourceWizardPage_10, IMessageProvider.ERROR);
        setValid(false);
        return;
      }

      if (!getResourceName().endsWith("." + fileExtension))
      {
        setValid(false);
        setMessage(Messages.CDOResourceNodeChooserComposite_1 + fileExtension, IMessageProvider.ERROR);
        return;
      }

      if (view != null && getResourceValidationType() != VALIDATION_NONE)
      {
        try
        {
          if (view.hasResource(getURI().path()))
          {
            if (getResourceValidationType() == VALIDATION_WARN)
            {
              setMessage(Messages.DawnCreateNewResourceWizardPage_11, IMessageProvider.WARNING);
              setMessageType(IMessageProvider.WARNING);
              setValid(true);
              return;
            }
            else if (getResourceValidationType() == VALIDATION_ERROR)
            {
              setMessage(Messages.DawnCreateNewResourceWizardPage_12, IMessageProvider.ERROR);
              setValid(false);
              setMessageType(IMessageProvider.ERROR);
              return;
            }
          }
        }
        catch (Exception e)
        {
          setMessage(e.getMessage());
          setMessageType(IMessageProvider.ERROR);
        }
      }
      if (isValid())
      {
        setMessage(null);
        setMessageType(IMessageProvider.NONE);
      }
    }

    private void setMessage(String message, int type)
    {
      setMessage(message);
      setMessageType(type);
    }

    public void setValid(boolean valid)
    {
      this.valid = valid;
    }

    public boolean isValid()
    {
      return valid;
    }

    public void setMessage(String message)
    {
      this.message = message;
    }

    public String getMessage()
    {
      return message;
    }

    public void setMessageType(int messageType)
    {
      this.messageType = messageType;
    }

    public int getMessageType()
    {
      return messageType;
    }

    public void setResourceValidationType(int resourceValidationType)
    {
      this.resourceValidationType = resourceValidationType;
    }

    public int getResourceValidationType()
    {
      return resourceValidationType;
    }

    public void setValidationListener(ValidationListener validationListener)
    {
      this.validationListener = validationListener;
    }

    public ValidationListener getValidationListener()
    {
      return validationListener;
    }
  }

  /**
   * @author Martin Fluegge
   */
  public static interface ValidationListener
  {
    public void validationFinished();
  }
}
