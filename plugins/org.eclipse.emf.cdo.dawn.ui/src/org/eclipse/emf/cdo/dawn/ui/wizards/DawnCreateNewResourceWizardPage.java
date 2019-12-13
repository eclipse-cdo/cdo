/*
 * Copyright (c) 2010-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.wizards;

import org.eclipse.emf.cdo.dawn.ui.composites.CDOResourceNodeChooserComposite;
import org.eclipse.emf.cdo.dawn.ui.composites.CDOResourceNodeChooserComposite.ResourceChooserValidator;
import org.eclipse.emf.cdo.dawn.ui.composites.CDOResourceNodeChooserComposite.ValidationListener;
import org.eclipse.emf.cdo.dawn.ui.messages.Messages;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Martin Fluegge
 */
public class DawnCreateNewResourceWizardPage extends WizardPage
{
  private Composite container;

  private boolean showResources;

  private final String fileExtension;

  private int resourceValidationType = ResourceChooserValidator.VALIDATION_ERROR;

  /**
   * left for backward compatibility with the generated editors. This field might soon be removed. Use
   * <b>org.eclipse.emf.cdo.dawn.ui.composites.ResourceChooserValidator.VALIDATION_WARN</b> instead.
   */
  @Deprecated
  public static final int VALIDATION_WARN = ResourceChooserValidator.VALIDATION_WARN;

  private final CDOView view;

  // private String resourceNamePrefix = "default"; //$NON-NLS-1$

  private boolean createAutomaticResourceName;

  /**
   * @since 1.0
   */
  protected CDOResourceNodeChooserComposite chooserComposite;

  public DawnCreateNewResourceWizardPage(String fileExtension)
  {
    this(fileExtension, true, null);
  }

  public DawnCreateNewResourceWizardPage(String fileExtension, boolean showResources, CDOView view)
  {
    super(Messages.DawnCreateNewResourceWizardPage_0);
    this.view = view;
    setTitle(Messages.DawnCreateNewResourceWizardPage_2);
    setDescription(Messages.DawnCreateNewResourceWizardPage_3);
    this.showResources = showResources;
    this.fileExtension = fileExtension;
  }

  @Override
  public void createControl(Composite parent)
  {
    container = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 1;
    chooserComposite = new CDOResourceNodeChooserComposite(container, SWT.NONE, fileExtension, view);
    chooserComposite.showResources(showResources);
    ResourceChooserValidator validator = chooserComposite.getValidator();
    validator.setResourceValidationType(resourceValidationType);
    validator.setValidationListener(new ValidationListener()
    {
      @Override
      public void validationFinished()
      {
        validatePage();
      }
    });

    if (createAutomaticResourceName)
    {
      chooserComposite.createAutomaticResourceName();
    }

    GridData gd = new GridData(GridData.FILL_BOTH);
    chooserComposite.setLayoutData(gd);

    setControl(container);
    validator.validate();
  }

  private void validatePage()
  {
    ResourceChooserValidator validator = chooserComposite.getValidator();
    boolean valid = validator.isValid();

    if (!valid)
    {
      int type = validator.getMessageType();
      setMessage(validator.getMessage(), type);
    }
    else
    {
      setMessage("");
    }
    setPageComplete(valid);
  }

  public URI getURI()
  {
    return chooserComposite.getURI();
  }

  public void setShowResources(boolean showResources)
  {
    this.showResources = showResources;
  }

  public boolean isShowResources()
  {
    return showResources;
  }

  public void setResourceNamePrefix(String resourceNamePrefix)
  {
    // this.resourceNamePrefix = resourceNamePrefix;
    chooserComposite.setResourceNamePrefix(resourceNamePrefix);
    chooserComposite.setResourceName(resourceNamePrefix);
  }

  public String getResourceNamePrefix()
  {
    return chooserComposite.getResourceNamePrefix();
  }

  public void setResourcePath(String text)
  {
    if (!text.endsWith("/") || !!text.endsWith("\\")) //$NON-NLS-1$ //$NON-NLS-2$
    {
      text += "/"; //$NON-NLS-1$
    }
    chooserComposite.setResourcePath(text);
  }

  public String getResourcePath()
  {
    return chooserComposite.getResourcePath();
  }

  public void setCreateAutomaticResourceName(boolean createAutomaticResourceName)
  {
    this.createAutomaticResourceName = createAutomaticResourceName;
  }

  public boolean isCreateAutomaticResourceName()
  {
    return createAutomaticResourceName;
  }

  public void setResourceValidationType(int resourceValidationType)
  {
    this.resourceValidationType = resourceValidationType;
  }

  public int getResourceValidationType()
  {
    return resourceValidationType;
  }

  public String getDefaultName()
  {
    return chooserComposite.getDefaultName();
  }
}
