/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.projectconfig.presentation;

import org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration;
import org.eclipse.emf.cdo.releng.projectconfig.util.ProjectConfigUtil;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchWindow;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
public class ProjectConfigPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
  private static final String PREFERENCES_NODE_NAME = ProjectConfigEditorPlugin.INSTANCE.getSymbolicName();

  private static final Preferences PREFERENCES = InstanceScope.INSTANCE.getNode(PREFERENCES_NODE_NAME);

  private static final String CONFIGURATION_MANAGEMENT = "configurationManagement";

  public static boolean isConfigurationManagementAutomatic()
  {
    return "automatic".equals(getPreference(CONFIGURATION_MANAGEMENT, "manual"));
  }

  private static void setConfigurationManagementAutomatic(boolean automatic)
  {
    setPreference(CONFIGURATION_MANAGEMENT, automatic ? "automatic" : "manual");
  }

  private static final String CONFIGURATION_VALIDATION = "configurationValidation";

  public static boolean isConfigurationValidationPrompt()
  {
    return "prompt".equals(getPreference(CONFIGURATION_VALIDATION, "prompt"));
  }

  private static void setConfigurationValidationPrompt(boolean prompt)
  {
    setPreference(CONFIGURATION_VALIDATION, prompt ? "prompt" : "ignore");
  }

  private static final String PROPERTY_MODIFICATION_HANDLING = "propertyModificationHandling";

  public enum PropertyModificationHandling
  {
    OVERWRITE, PROPAGATE, PROMPT
  }

  public static PropertyModificationHandling getPropertyModificationHandling()
  {
    return PropertyModificationHandling.valueOf(getPreference(PROPERTY_MODIFICATION_HANDLING,
        PropertyModificationHandling.PROMPT.toString()));
  }

  private static void setPropertyModificationHandling(PropertyModificationHandling propertyModificationHandling)
  {
    setPreference(PROPERTY_MODIFICATION_HANDLING, propertyModificationHandling.toString());
  }

  private static String getPreference(String key, String defaultValue)
  {
    return Platform.getPreferencesService().getString(PREFERENCES_NODE_NAME, key, defaultValue,
        new IScopeContext[] { InstanceScope.INSTANCE });
  }

  private static void setPreference(String key, String value)
  {
    PREFERENCES.put(key, value);
  }

  private IWorkbench workbench;

  private boolean configurationManagementAutomatic = isConfigurationManagementAutomatic();

  private boolean configurationValidationPrompt = isConfigurationValidationPrompt();

  private PropertyModificationHandling propertyModificationHandling = getPropertyModificationHandling();

  private Button automaticPreferenceManagementButton;

  private Button ignoreErrorsButton;

  private Button promptErrorsButton;

  private Button overwriteButton;

  private Button propagate;

  private Button promptButton;

  public ProjectConfigPreferencePage()
  {
    super("<taken from plugin.xml>");
    // setDescription(" Manage project preference configuration:\n ");
    noDefaultAndApplyButton();
  }

  public void init(IWorkbench workbench)
  {
    this.workbench = workbench;
  }

  private void update()
  {
    ignoreErrorsButton.setEnabled(configurationManagementAutomatic);
    promptErrorsButton.setEnabled(configurationManagementAutomatic);

    overwriteButton.setEnabled(configurationManagementAutomatic);
    propagate.setEnabled(configurationManagementAutomatic);
    promptButton.setEnabled(configurationManagementAutomatic);
  }

  @Override
  protected Control createContents(Composite parent)
  {
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    layout.numColumns = 1;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(layout);

    automaticPreferenceManagementButton = new Button(composite, SWT.CHECK);
    automaticPreferenceManagementButton.setText("Automatic preference management");
    automaticPreferenceManagementButton.setSelection(configurationManagementAutomatic);
    automaticPreferenceManagementButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        configurationManagementAutomatic = !configurationManagementAutomatic;
        update();
      }
    });

    {
      Group group = new Group(composite, SWT.NONE);
      group.setLayout(new GridLayout(1, false));
      GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
      layoutData.horizontalIndent = 10;
      group.setLayoutData(layoutData);
      group.setText("Invalid Configuration Handling");

      {
        ignoreErrorsButton = new Button(group, SWT.RADIO);
        ignoreErrorsButton.setText("Ignore errors");
        if (!configurationValidationPrompt)
        {
          ignoreErrorsButton.setSelection(true);
        }

        ignoreErrorsButton.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            configurationValidationPrompt = false;
          }
        });
      }

      {
        promptErrorsButton = new Button(group, SWT.RADIO);
        promptErrorsButton.setText("Prompt to fix errors");
        if (configurationValidationPrompt)
        {
          promptErrorsButton.setSelection(true);
        }

        promptErrorsButton.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            configurationValidationPrompt = true;
          }
        });
      }
    }

    {
      Group group = new Group(composite, SWT.NONE);
      group.setLayout(new GridLayout(1, false));
      GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
      layoutData.horizontalIndent = 10;
      group.setLayoutData(layoutData);
      group.setText("Managed Property Modification Handling");

      {
        overwriteButton = new Button(group, SWT.RADIO);
        overwriteButton.setText("Overwrite managed property with managing property");
        if (propertyModificationHandling == PropertyModificationHandling.OVERWRITE)
        {
          overwriteButton.setSelection(true);
        }

        overwriteButton.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            propertyModificationHandling = PropertyModificationHandling.OVERWRITE;
          }
        });
      }

      {
        propagate = new Button(group, SWT.RADIO);
        propagate.setText("Propogate managed property to managing property");
        if (propertyModificationHandling == PropertyModificationHandling.PROPAGATE)
        {
          propagate.setSelection(true);
        }

        propagate.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            propertyModificationHandling = PropertyModificationHandling.PROPAGATE;
          }
        });
      }

      {
        promptButton = new Button(group, SWT.RADIO);
        promptButton.setText("Prompt");
        if (propertyModificationHandling == PropertyModificationHandling.PROMPT)
        {
          promptButton.setSelection(true);
        }

        promptButton.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            propertyModificationHandling = PropertyModificationHandling.PROMPT;
          }
        });
      }
    }

    update();

    Label label = new Label(composite, SWT.NONE);
    label.setText("Manage Configuration:");

    TreeViewer treeViewer = new TreeViewer(composite);
    AdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
    treeViewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
    treeViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
    treeViewer.setInput(ProjectConfigUtil.getWorkspaceConfiguration().eResource());
    treeViewer.getControl().setLayoutData(
        new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

    return composite;
  }

  @Override
  protected void contributeButtons(Composite parent)
  {
    super.contributeButtons(parent);

    GridLayout gridLayout = (GridLayout)parent.getLayout();
    gridLayout.numColumns += 2;

    {
      Button applyButton = new Button(parent, SWT.PUSH);
      applyButton.setText("Apply");

      Dialog.applyDialogFont(applyButton);
      int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
      Point minButtonSize = applyButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      data.widthHint = Math.max(widthHint, minButtonSize.x);

      applyButton.setLayoutData(data);
      applyButton.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          performOk();

          WorkspaceConfiguration workspaceConfiguration = ProjectConfigUtil.getWorkspaceConfiguration();
          workspaceConfiguration.updatePreferenceProfileReferences();
          workspaceConfiguration.applyPreferenceProfiles();
        }
      });
    }

    {
      Button editButton = new Button(parent, SWT.PUSH);
      editButton.setText("Edit...");

      Dialog.applyDialogFont(editButton);
      int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
      Point minButtonSize = editButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      data.widthHint = Math.max(widthHint, minButtonSize.x);

      editButton.setLayoutData(data);
      editButton.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          performOk();

          // Invoke the close method on the preference dialog, but avoid using internal API, so do it reflectively.
          IPreferencePageContainer container = getContainer();

          try
          {
            Method method = container.getClass().getMethod("close");
            method.invoke(container);
          }
          catch (Throwable ex)
          {
            ProjectConfigEditorPlugin.INSTANCE.log(ex);
          }

          openWorkingSetsEditor();
        }
      });
    }
  }

  protected void openWorkingSetsEditor()
  {
    final IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
    Display display = activeWorkbenchWindow.getShell().getDisplay();
    display.asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          IEditorInput editorInput = new URIEditorInput(ProjectConfigUtil.PROJECT_CONFIG_URI
              .appendSegment("All.projectconfig"), "Project Preference Configuration");
          IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
          activePage.openEditor(editorInput,
              "org.eclipse.emf.cdo.releng.projectconfig.presentation.ProjectConfigEditorID");
          activePage.showView(IPageLayout.ID_PROP_SHEET);
        }
        catch (Exception ex)
        {
          ProjectConfigEditorPlugin.INSTANCE.log(ex);
        }
      }
    });
  }

  @Override
  public boolean performOk()
  {
    setConfigurationManagementAutomatic(configurationManagementAutomatic);
    setConfigurationValidationPrompt(configurationValidationPrompt);
    setPropertyModificationHandling(propertyModificationHandling);

    try
    {
      PREFERENCES.flush();
    }
    catch (BackingStoreException ex)
    {
      ProjectConfigEditorPlugin.INSTANCE.log(ex);
    }

    ProjectConfigEditorPlugin.getPlugin().update();

    return true;
  }
}
