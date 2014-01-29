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
import org.eclipse.emf.cdo.releng.internal.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.Targlet;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.p2.core.ProvisionException;
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
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class TargletContainerUI implements IAdapterFactory, ITargetLocationEditor, ITargetLocationUpdater
{
  private static final Class<?>[] ADAPTERS = { ITreeContentProvider.class, ILabelProvider.class,
      ITargetLocationEditor.class, ITargetLocationUpdater.class };

  private ILabelProvider labelProvider;

  public TargletContainerUI()
  {
  }

  @SuppressWarnings("rawtypes")
  public Class[] getAdapterList()
  {
    return ADAPTERS;
  }

  public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType)
  {
    if (adaptableObject instanceof TargletContainer)
    {
      if (adapterType == ITreeContentProvider.class)
      {
        return getContentProvider();
      }

      if (adapterType == ILabelProvider.class)
      {
        return getLabelProvider();
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
    return targetLocation instanceof TargletContainer;
  }

  public IWizard getEditWizard(ITargetDefinition target, ITargetLocation targetLocation)
  {
    return new EditTargletWizard(target, (TargletContainer)targetLocation);
  }

  public boolean canUpdate(ITargetDefinition target, ITargetLocation targetLocation)
  {
    return targetLocation instanceof TargletContainer;
  }

  public IStatus update(ITargetDefinition target, ITargetLocation targetLocation, IProgressMonitor monitor)
  {
    try
    {
      ((TargletContainer)targetLocation).updateProfile(monitor);
      return Status.OK_STATUS;
    }
    catch (ProvisionException ex)
    {
      return ex.getStatus();
    }
  }

  private ITreeContentProvider getContentProvider()
  {
    return new TargletContentProvider();
  }

  private ILabelProvider getLabelProvider()
  {
    if (labelProvider == null)
    {
      labelProvider = new TargletLabelProvider();
    }

    return labelProvider;
  }

  private static void registerAdditionalAdpter(IAdapterFactory factory, EClass eClass)
  {
    EObject eObject = EcoreUtil.create(eClass);
    Class<?> adaptable = eObject.getClass();
    Platform.getAdapterManager().registerAdapters(factory, adaptable);
  }

  static
  {
    final Class<?>[] adapters = { ITreeContentProvider.class, ILabelProvider.class };

    IAdapterFactory factory = new IAdapterFactory()
    {
      @SuppressWarnings("rawtypes")
      public Class[] getAdapterList()
      {
        return adapters;
      }

      public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType)
      {
        if (adapterType == ITreeContentProvider.class)
        {
          return new AdapterFactoryContentProvider(EMFUtil.createAdapterFactory());
        }

        if (adapterType == ILabelProvider.class)
        {
          return new AdapterFactoryLabelProvider(EMFUtil.createAdapterFactory());
        }

        return null;
      }
    };

    registerAdditionalAdpter(factory, SetupPackage.Literals.TARGLET);
    registerAdditionalAdpter(factory, SetupPackage.Literals.INSTALLABLE_UNIT);
    registerAdditionalAdpter(factory, SetupPackage.Literals.REPOSITORY_LIST);
    registerAdditionalAdpter(factory, SetupPackage.Literals.P2_REPOSITORY);
    registerAdditionalAdpter(factory, SetupPackage.Literals.AUTOMATIC_SOURCE_LOCATOR);
  }

  /**
   * @author Eike Stepper
   */
  private static class TargletContentProvider implements ITreeContentProvider
  {
    private Map<Targlet, TargletContainer> parents = new HashMap<Targlet, TargletContainer>();

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

    public Object[] getElements(Object element)
    {
      return getChildren(element);
    }

    public Object[] getChildren(Object element)
    {
      if (element instanceof TargletContainer)
      {
        TargletContainer location = (TargletContainer)element;
        List<Targlet> targlets = location.getTarglets();
        for (Targlet targlet : targlets)
        {
          parents.put(targlet, location);
        }

        return targlets.toArray(new Targlet[targlets.size()]);
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
      if (element instanceof TargletContainer)
      {
        return ResourceManager.getPluginImage("org.eclipse.emf.cdo.releng.setup", "icons/targlets.gif");
      }

      return super.getImage(element);
    }

    @Override
    public String getText(Object element)
    {
      if (element instanceof TargletContainer)
      {
        return ((TargletContainer)element).toString();
      }

      return super.getText(element);
    }
  }

  public static class NewTargletWizard extends Wizard implements ITargetLocationWizard
  {
    private ITargetDefinition target;

    private TargletContainer targlet;

    private TargletContainerUI.TargletWizardPage page;

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
    private TargletContainerUI.TargletWizardPage page;

    private ITargetDefinition target;

    private TargletContainer targlet;

    public EditTargletWizard(ITargetDefinition target, TargletContainer targlet)
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
  @SuppressWarnings("unused")
  private static class TargletWizardPage extends WizardPage implements IEditBundleContainerPage
  {
    private ITargetDefinition target;

    private TargletContainer targlet;

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
    public TargletWizardPage(ITargetDefinition target, TargletContainer targlet)
    {
      this(target);
      this.targlet = targlet;
      setTitle("Edit Targlet");
    }

    public TargletContainer getBundleContainer()
    {
      try
      {
        File p2PoolDir = new File("C:/develop/.p2pool-ide");
        File p2AgentDir = new File("C:/develop/.p2pool-ide/p2");
        String profileID = "C__develop_aaa_cdo.releng_master_tpX";
        String destination = "C:/develop/aaa/cdo.releng/master/tpX"; // XXX

        java.net.URI[] p2Repositories = { new java.net.URI("http://download.eclipse.org/releases/luna") };
        IVersionedId[] rootComponents = { new VersionedId("org.eclipse.emf.ecore.feature.group", Version.emptyVersion) };

        return null;
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
    }

    public void createControl(Composite parent)
    {
      Composite composite = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_BOTH, 0, 0);

      Label label = new Label(composite, SWT.NONE);
      label.setText("Targlet Info");
      label.setLayoutData(new GridData(GridData.FILL_BOTH));

      setControl(composite);
    }

    /**
     * Checks if the page is complete, updating messages and finish button.
     */
    void pageChanged()
    {
      setErrorMessage(null);
      setPageComplete(true);
    }

    /**
     * Restores the state of the wizard from previous invocations
     */
    private void restoreWidgetState()
    {
    }
  }
}
