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
package org.eclipse.emf.cdo.releng.internal.setup.targlets;

import org.eclipse.emf.cdo.releng.internal.setup.ui.ResourceManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IVersionedId;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionedId;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.internal.ui.SWTFactory;
import org.eclipse.pde.internal.ui.shared.target.EditBundleContainerWizard;
import org.eclipse.pde.internal.ui.shared.target.IEditBundleContainerPage;
import org.eclipse.pde.internal.ui.shared.target.StyledBundleLabelProvider;
import org.eclipse.pde.ui.target.ITargetLocationEditor;
import org.eclipse.pde.ui.target.ITargetLocationUpdater;
import org.eclipse.pde.ui.target.ITargetLocationWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class TargletUIFactory implements IAdapterFactory, ITargetLocationEditor, ITargetLocationUpdater
{
  private static final Class<?>[] ADAPTERS = { ILabelProvider.class, ITreeContentProvider.class,
      ITargetLocationEditor.class, ITargetLocationUpdater.class };

  private ILabelProvider labelProvider;

  public TargletUIFactory()
  {
  }

  @SuppressWarnings("rawtypes")
  public Class[] getAdapterList()
  {
    return ADAPTERS;
  }

  @SuppressWarnings("rawtypes")
  public Object getAdapter(Object adaptableObject, Class adapterType)
  {
    if (adaptableObject instanceof TargletBundleContainer)
    {
      if (adapterType == ILabelProvider.class)
      {
        return getLabelProvider();
      }

      if (adapterType == ITreeContentProvider.class)
      {
        return getContentProvider();
      }

      if (adapterType == ITargetLocationEditor.class)
      {
        return this;
      }

      if (adapterType == ITargetLocationUpdater.class)
      {
        return this;
      }
    }

    return null;
  }

  public boolean canEdit(ITargetDefinition target, ITargetLocation targetLocation)
  {
    return targetLocation instanceof TargletBundleContainer;
  }

  public IWizard getEditWizard(ITargetDefinition target, ITargetLocation targetLocation)
  {
    return new EditTargletWizard(target, (TargletBundleContainer)targetLocation);
  }

  public boolean canUpdate(ITargetDefinition target, ITargetLocation targetLocation)
  {
    return targetLocation instanceof TargletBundleContainer;
  }

  public IStatus update(ITargetDefinition target, ITargetLocation targetLocation, IProgressMonitor monitor)
  {
    try
    {
      ((TargletBundleContainer)targetLocation).updateProfile(monitor);
      return Status.OK_STATUS;
    }
    catch (ProvisionException ex)
    {
      return ex.getStatus();
    }
  }

  private ILabelProvider getLabelProvider()
  {
    if (labelProvider == null)
    {
      labelProvider = new TargletLabelProvider();
    }

    return labelProvider;
  }

  private ITreeContentProvider getContentProvider()
  {
    return new TargletContentProvider();
  }

  /**
   * @author Eike Stepper
   */
  private static class TargletLabelProvider extends StyledBundleLabelProvider
  {
    public TargletLabelProvider()
    {
      super(true, false);
    }

    @Override
    public Image getImage(Object element)
    {
      if (element instanceof TargletBundleContainer)
      {
        return ResourceManager.getPluginImage("org.eclipse.emf.cdo.releng.setup", "icons/targlets.gif");
      }

      return super.getImage(element);
    }

    @Override
    public String getText(Object element)
    {
      if (element instanceof TargletBundleContainer)
      {
        TargletBundleContainer targlet = (TargletBundleContainer)element;
        return targlet.getProfileID();
      }

      return super.getText(element);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class TargletContentProvider implements ITreeContentProvider
  {
    private Map<IInstallableUnit, TargletBundleContainer> parents = new HashMap<IInstallableUnit, TargletBundleContainer>();

    public TargletContentProvider()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public void dispose()
    {
      // TODO dispose() is not called by PDE!
      parents = null;
    }

    public Object[] getElements(Object inputElement)
    {
      return getChildren(inputElement);
    }

    public Object[] getChildren(Object parentElement)
    {
      if (false)
      {
        TargletBundleContainer location = (TargletBundleContainer)parentElement;
        if (location.isResolved())
        {
          try
          {
            // // if this is a bundle container then we must be sure that all bundle containers are
            // // happy since they all share the same profile.
            // ITargetDefinition target = location.getTarget();
            // if (target != null && P2TargetUtils.isResolved(target))
            // {
            IInstallableUnit[] units = location.getUnits();
            for (int i = 0; i < units.length; i++)
            {
              parents.put(units[i], location);
            }

            return units;
            // }
          }
          catch (CoreException e)
          {
            return new Object[] { e.getStatus() };
          }
        }
      }

      return new Object[0];
    }

    public boolean hasChildren(Object element)
    {
      return getChildren(element).length != 0;
    }

    public Object getParent(Object element)
    {
      return parents.get(element);
    }
  }

  public static class NewTargletWizard extends Wizard implements ITargetLocationWizard
  {
    private ITargetDefinition target;

    private TargletBundleContainer targlet;

    private TargletUIFactory.TargletWizardPage page;

    private static final String SETTINGS_SECTION = "editBundleContainerWizard";

    public NewTargletWizard()
    {
      setWindowTitle("Add Targlet");
    }

    public void setTarget(ITargetDefinition target)
    {
      this.target = target;
    }

    @Override
    public void addPages()
    {
      // IDialogSettings settings = PDEPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION);
      // if (settings == null)
      // {
      // settings = PDEPlugin.getDefault().getDialogSettings().addNewSection(SETTINGS_SECTION);
      // }
      //
      // setDialogSettings(settings);

      page = new TargletWizardPage(target);
      addPage(page);
    }

    @Override
    public boolean performFinish()
    {
      targlet = page.getBundleContainer();
      return true;
    }

    public ITargetLocation[] getLocations()
    {
      return new ITargetLocation[] { targlet };
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class EditTargletWizard extends EditBundleContainerWizard
  {
    private TargletUIFactory.TargletWizardPage page;

    private ITargetDefinition target;

    private TargletBundleContainer targlet;

    public EditTargletWizard(ITargetDefinition target, TargletBundleContainer targlet)
    {
      super(target, targlet);
      this.target = target;
      this.targlet = targlet;
      setWindowTitle("Edit Targlet");
    }

    @Override
    public void addPages()
    {
      page = new TargletWizardPage(target, targlet);
      addPage(page);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class TargletWizardPage extends WizardPage implements IEditBundleContainerPage
  {
    private ITargetDefinition target;

    private TargletBundleContainer targlet;

    /**
     * Constructor for creating a new container
     */
    public TargletWizardPage(ITargetDefinition target)
    {
      super("AddP2Container");
      setTitle("Add Targlet");
      setMessage("Select content from a software site to be added to your target.");
      this.target = target;
    }

    /**
     * Constructor for editing an existing container
     */
    public TargletWizardPage(ITargetDefinition target, TargletBundleContainer targlet)
    {
      this(target);
      this.targlet = targlet;
      setTitle("Edit Targlet");
    }

    public TargletBundleContainer getBundleContainer()
    {
      try
      {
        File p2PoolDir = new File("C:/develop/.p2pool-ide");
        File p2AgentDir = new File("C:/develop/.p2pool-ide/p2");
        String profileID = "C__develop_aaa_cdo.releng_master_tpX";
        String destination = "C:/develop/aaa/cdo.releng/master/tpX"; // XXX

        java.net.URI[] p2Repositories = { new java.net.URI("http://download.eclipse.org/releases/luna") };
        IVersionedId[] rootComponents = { new VersionedId("org.eclipse.emf.ecore.feature.group", Version.emptyVersion) };

        int xxx;
        return null;

        // return create(p2PoolDir, p2AgentDir, profileID, destination, p2Repositories, rootComponents,
        // new NullProgressMonitor());
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
    }

    public void storeSettings()
    {
      // IDialogSettings settings = getDialogSettings();
      // if (settings != null)
      // {
      // settings.put(SETTINGS_GROUP_BY_CATEGORY, fShowCategoriesButton.getSelection());
      // settings.put(SETTINGS_SHOW_OLD_VERSIONS, fShowOldVersionsButton.getSelection());
      // settings.put(SETTINGS_SELECTED_REPOSITORY, fRepoLocation != null ? fRepoLocation.toString() : null);
      // }
    }

    public void createControl(Composite parent)
    {
      Composite composite = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_BOTH, 0, 0);

      Label label = new Label(composite, SWT.NONE);
      label.setText("Targlet Info");
      label.setLayoutData(new GridData(GridData.FILL_BOTH));

      // restoreWidgetState();
      setControl(composite);
      // setPageComplete(false);

      // if (fEditContainer == null)
      // {
      // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.LOCATION_ADD_SITE_WIZARD);
      // }
      // else
      // {
      // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.LOCATION_EDIT_SITE_WIZARD);
      // }
    }

    /**
     * Checks if the page is complete, updating messages and finish button.
     */
    void pageChanged()
    {
      // if (fSelectedIUStatus.getSeverity() == IStatus.ERROR)
      // {
      // setErrorMessage(fSelectedIUStatus.getMessage());
      // setPageComplete(false);
      // }
      // else if (fAvailableIUGroup != null && fAvailableIUGroup.getCheckedLeafIUs().length == 0)
      // {
      // // On page load and when sites are selected, we might not have an error status, but we want finish to
      // remain
      // // disabled
      // setPageComplete(false);
      // }
      // else
      {
        setErrorMessage(null);
        setPageComplete(true);
      }
    }

    /**
     * Restores the state of the wizard from previous invocations
     */
    private void restoreWidgetState()
    {
      // IDialogSettings settings = getDialogSettings();
      // URI uri = null;
      // boolean showCategories = fQueryContext.shouldGroupByCategories();
      // boolean showOldVersions = fQueryContext.getShowLatestVersionsOnly();
      //
      // // Init the checkboxes and repo selector combo
      // if (fEditContainer != null)
      // {
      // if (fEditContainer.getRepositories() != null)
      // {
      // uri = fEditContainer.getRepositories()[0];
      // }
      // }
      // else if (settings != null)
      // {
      // String stringURI = settings.get(SETTINGS_SELECTED_REPOSITORY);
      // if (stringURI != null && stringURI.trim().length() > 0)
      // {
      // try
      // {
      // uri = new URI(stringURI);
      // }
      // catch (URISyntaxException e)
      // {
      // PDEPlugin.log(e);
      // }
      // }
      // }
      //
      // if (settings != null)
      // {
      // if (settings.get(SETTINGS_GROUP_BY_CATEGORY) != null)
      // {
      // showCategories = settings.getBoolean(SETTINGS_GROUP_BY_CATEGORY);
      // }
      // if (settings.get(SETTINGS_SHOW_OLD_VERSIONS) != null)
      // {
      // showOldVersions = settings.getBoolean(SETTINGS_SHOW_OLD_VERSIONS);
      // }
      // }
      //
      // if (uri != null)
      // {
      // fRepoSelector.setRepositorySelection(AvailableIUGroup.AVAILABLE_SPECIFIED, uri);
      // }
      // else if (fEditContainer != null)
      // {
      // fRepoSelector.setRepositorySelection(AvailableIUGroup.AVAILABLE_ALL, null);
      // }
      // else
      // {
      // fRepoSelector.setRepositorySelection(AvailableIUGroup.AVAILABLE_NONE, null);
      // }
      //
      // fShowCategoriesButton.setSelection(showCategories);
      // fShowOldVersionsButton.setSelection(showOldVersions);
      //
      // if (fEditContainer != null)
      // {
      // fIncludeRequiredButton.setSelection(fEditContainer.getIncludeAllRequired());
      // fAllPlatformsButton.setSelection(fEditContainer.getIncludeAllEnvironments());
      // fIncludeSourceButton.setSelection(fEditContainer.getIncludeSource());
      // fConfigurePhaseButton.setSelection(fEditContainer.getIncludeConfigurePhase());
      // }
      // else
      // {
      // // If we are creating a new container, but there is an existing iu container we should use it's settings
      // // (otherwise we overwrite them)
      // ITargetLocation[] knownContainers = fTarget.getTargetLocations();
      // if (knownContainers != null)
      // {
      // for (int i = 0; i < knownContainers.length; i++)
      // {
      // if (knownContainers[i] instanceof IUBundleContainer)
      // {
      // fIncludeRequiredButton.setSelection(((IUBundleContainer)knownContainers[i]).getIncludeAllRequired());
      // fAllPlatformsButton.setSelection(((IUBundleContainer)knownContainers[i]).getIncludeAllEnvironments());
      // fIncludeSourceButton.setSelection(((IUBundleContainer)knownContainers[i]).getIncludeSource());
      // fConfigurePhaseButton
      // .setSelection(((IUBundleContainer)knownContainers[i]).getIncludeConfigurePhase());
      // }
      // }
      // }
      // }
      //
      // // If the user can create two containers with different settings for include required we won't resolve
      // // correctly
      // // If the user has an existing container, don't let them edit the options, bug 275013
      // if (fTarget != null)
      // {
      // ITargetLocation[] containers = fTarget.getTargetLocations();
      // if (containers != null)
      // {
      // for (int i = 0; i < containers.length; i++)
      // {
      // if (containers[i] instanceof IUBundleContainer && containers[i] != fEditContainer)
      // {
      // fIncludeRequiredButton.setSelection(((IUBundleContainer)containers[i]).getIncludeAllRequired());
      // fAllPlatformsButton.setSelection(((IUBundleContainer)containers[i]).getIncludeAllEnvironments());
      // break;
      // }
      // }
      // }
      // }
      //
      // fAllPlatformsButton.setEnabled(!fIncludeRequiredButton.getSelection());
      //
      // updateViewContext();
      // fRepoSelector.getDefaultFocusControl().setFocus();
      // updateDetails();
      //
      // // If we are editing a bundle check any installable units
      // if (fEditContainer != null)
      // {
      // try
      // {
      // // TODO This code does not do a good job, selecting, revealing, and collapsing all
      // // Only able to check items if we don't have categories
      // fQueryContext.setViewType(IUViewQueryContext.AVAILABLE_VIEW_FLAT);
      // fAvailableIUGroup.updateAvailableViewState();
      // fAvailableIUGroup.setChecked(fEditContainer.getInstallableUnits());
      // // Make sure view is back in proper state
      // updateViewContext();
      // IInstallableUnit[] units = fAvailableIUGroup.getCheckedLeafIUs();
      // if (units.length > 0)
      // {
      // fAvailableIUGroup.getCheckboxTreeViewer().setSelection(new StructuredSelection(units[0]), true);
      // if (units.length == 1)
      // {
      // fSelectionCount.setText(NLS.bind(Messages.EditIUContainerPage_itemSelected,
      // Integer.toString(units.length)));
      // }
      // else
      // {
      // fSelectionCount.setText(NLS.bind(Messages.EditIUContainerPage_itemsSelected,
      // Integer.toString(units.length)));
      // }
      // }
      // else
      // {
      // fSelectionCount.setText(NLS.bind(Messages.EditIUContainerPage_itemsSelected, Integer.toString(0)));
      // }
      // fAvailableIUGroup.getCheckboxTreeViewer().collapseAll();
      //
      // }
      // catch (CoreException e)
      // {
      // PDEPlugin.log(e);
      // }
      // }
    }
  }
}
