/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.editor;

import org.eclipse.emf.cdo.ui.CDOLoadResourceProvider;
import org.eclipse.emf.cdo.ui.CDOLoadResourceProvider.ImageProvider;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.ui.CommonUIPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.LoadResourceAction;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.widgets.WidgetFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOLoadResourceAction extends LoadResourceAction
{
  public CDOLoadResourceAction()
  {
  }

  public CDOLoadResourceAction(EditingDomain domain)
  {
    super(domain);
  }

  @Override
  public void run()
  {
    LoadResourceDialog dialog = new CDOLoadResourceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), domain);
    dialog.open();
  }

  /**
   * @author Eike Stepper
   */
  public static class CDOLoadResourceDialog extends LoadResourceDialog
  {
    public CDOLoadResourceDialog(Shell parent, EditingDomain domain)
    {
      super(parent, domain);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
      Shell shell = getShell();
      boolean multi = isMulti();

      ResourceSet resourceSet = domain.getResourceSet();
      List<CDOLoadResourceProvider> providers = CDOLoadResourceProvider.Factory.getProviders(IPluginContainer.INSTANCE, resourceSet);

      // Create a composite with standard margins and spacing.
      GridLayout areaLayout = new GridLayout();
      areaLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
      areaLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
      areaLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
      areaLayout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
      areaLayout.numColumns = 1 // resourceURILabel
          + 1 // browseFileSystemButton
          + (EMFPlugin.IS_RESOURCES_BUNDLE_AVAILABLE ? 1 : 0) // browseWorkspaceButton
          + providers.size(); // Provider buttons

      Composite composite = WidgetFactory.composite(SWT.NONE).layout(areaLayout).layoutData(new GridData(GridData.FILL_BOTH)).create(parent);
      applyDialogFont(composite);

      Label resourceURILabel = new Label(composite, SWT.LEFT);
      resourceURILabel.setText(CommonUIPlugin.INSTANCE.getString(multi ? "_UI_ResourceURIs_label" : "_UI_ResourceURI_label"));
      resourceURILabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).create());

      Button browseFileSystemButton = new Button(composite, SWT.PUSH);
      browseFileSystemButton.setText("&File System...");
      browseFileSystemButton.setImage(SharedIcons.getImage(SharedIcons.OBJ_FOLDER));
      browseFileSystemButton.setLayoutData(GridDataFactory.fillDefaults().create());
      prepareBrowseFileSystemButton(browseFileSystemButton);

      if (EMFPlugin.IS_RESOURCES_BUNDLE_AVAILABLE)
      {
        Button browseWorkspaceButton = new Button(composite, SWT.PUSH);
        browseWorkspaceButton.setText("&Workspace...");
        browseWorkspaceButton.setImage(SharedIcons.getImage(SharedIcons.OBJ_PROJECT));
        browseWorkspaceButton.setLayoutData(GridDataFactory.fillDefaults().create());
        prepareBrowseWorkspaceButton(browseWorkspaceButton);
      }

      for (CDOLoadResourceProvider provider : providers)
      {
        Button button = new Button(composite, SWT.PUSH);
        button.setText(provider.getButtonText(resourceSet));

        if (provider instanceof ImageProvider)
        {
          ImageProvider imageProvider = (ImageProvider)provider;
          button.setImage(imageProvider.getButtonImage(resourceSet));
        }

        button.setLayoutData(GridDataFactory.fillDefaults().create());
        button.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            List<URI> uris = provider.browseResources(resourceSet, shell, multi);
            if (ObjectUtil.isEmpty(uris))
            {
              return;
            }

            if (multi)
            {
              StringBuilder builder = new StringBuilder();

              for (URI uri : uris)
              {
                builder.append(uri.toString());
                builder.append("  ");
              }

              uriField.setText((uriField.getText() + "  " + builder.toString()).trim());
            }
            else
            {
              uriField.setText(uris.get(0).toString());
            }
          }
        });
      }

      uriField = new Text(composite, SWT.BORDER);
      uriField.setLayoutData(GridDataFactory.fillDefaults().span(areaLayout.numColumns, 1).grab(true, false).create());

      Label separatorLabel = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
      separatorLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

      return composite;
    }
  }
}
