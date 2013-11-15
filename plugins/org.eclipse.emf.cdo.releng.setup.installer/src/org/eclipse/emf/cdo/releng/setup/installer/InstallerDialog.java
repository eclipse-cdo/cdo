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
package org.eclipse.emf.cdo.releng.setup.installer;

import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskPerformer;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ErrorDialog;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ProgressLogDialog;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ResourceManager;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.GitCloneTask;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.provider.BranchItemProvider;
import org.eclipse.emf.cdo.releng.setup.provider.ConfigurationItemProvider;
import org.eclipse.emf.cdo.releng.setup.provider.ProjectItemProvider;
import org.eclipse.emf.cdo.releng.setup.provider.SetupItemProviderAdapterFactory;
import org.eclipse.emf.cdo.releng.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.util.OS;
import org.eclipse.emf.cdo.releng.setup.util.ServiceUtil;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class InstallerDialog extends TitleAreaDialog
{
  public static final int RETURN_WORKBENCH = -2;

  public static final int RETURN_RESTART = -3;

  private Map<Branch, Setup> setups;

  private AdapterFactory adapterFactory;

  private ResourceSet resourceSet;

  private Preferences preferences;

  private Configuration configuration;

  private CheckboxTreeViewer viewer;

  private Text userNameText;

  private Text installFolderText;

  private Text gitPrefixText;

  private ComboBoxViewerCellEditor cellEditor;

  /**
   * Create the dialog.
   * @param parentShell
   */
  public InstallerDialog(Shell parentShell)
  {
    super(parentShell);
    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
    setHelpAvailable(true);

    resourceSet = EMFUtil.createResourceSet();
    adapterFactory = new SetupDialogAdapterFactory();
  }

  @Override
  public boolean close()
  {
    saveEObject(preferences);
    return super.close();
  }

  /**
   * Return the initial size of the dialog.
   */
  @Override
  protected Point getInitialSize()
  {
    return new Point(500, 700);
  }

  /**
   * Create contents of the dialog.
   * @param parent
   */
  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(ProgressLogDialog.TITLE);
    setTitle(ProgressLogDialog.TITLE);
    setTitleImage(ResourceManager.getPluginImage("org.eclipse.emf.cdo.releng.setup", "icons/install_wiz.gif"));

    Composite area = (Composite)super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    GridLayout gl_container = new GridLayout(1, false);
    gl_container.marginWidth = 0;
    gl_container.marginHeight = 0;
    container.setLayout(gl_container);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    final String ECLIPSE_VERSION_COLUMN = "eclipse";

    viewer = new CheckboxTreeViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
    Tree tree = viewer.getTree();
    tree.setLinesVisible(true);
    tree.setHeaderVisible(true);
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
    SetupDialogLabelProvider labelProvider = new SetupDialogLabelProvider(adapterFactory, viewer);
    viewer.setLabelProvider(labelProvider);
    viewer.setCellModifier(new ICellModifier()
    {
      public boolean canModify(Object element, String property)
      {
        return !isInstalled(element);
      }

      public Object getValue(Object element, String property)
      {
        if (element instanceof Branch && ECLIPSE_VERSION_COLUMN.equals(property))
        {
          Branch branch = (Branch)element;
          Setup setup = setups.get(branch);
          if (setup != null)
          {
            return setup.getEclipseVersion();
          }
        }

        return null;
      }

      public void modify(Object element, String property, Object value)
      {
        TreeItem item = (TreeItem)element;
        Object modelElement = item.getData();
        if (modelElement instanceof Branch && ECLIPSE_VERSION_COLUMN.equals(property))
        {
          Branch branch = (Branch)modelElement;
          Setup setup = setups.get(branch);
          if (setup != null)
          {
            setup.setEclipseVersion((Eclipse)value);
            viewer.update(modelElement, new String[] { property });
          }
        }
      }
    });
    viewer.setColumnProperties(new String[] { "project", ECLIPSE_VERSION_COLUMN });

    cellEditor = new ComboBoxViewerCellEditor(viewer.getTree());
    cellEditor.setContentProvider(new IStructuredContentProvider()
    {
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
      {
      }

      public void dispose()
      {
      }

      public Object[] getElements(Object inputElement)
      {
        EList<Eclipse> eclipses = configuration.getEclipseVersions();
        return eclipses.toArray(new Eclipse[eclipses.size()]);
      }
    });

    viewer.setCellEditors(new CellEditor[] { null, cellEditor });
    cellEditor.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));

    viewer.addCheckStateListener(new ICheckStateListener()
    {
      public void checkStateChanged(CheckStateChangedEvent event)
      {
        boolean checked = event.getChecked();
        final Object element = event.getElement();
        if (element instanceof Project)
        {
          Project project = (Project)element;

          for (Branch branch : project.getBranches())
          {
            viewer.setChecked(branch, checked);
          }
        }
        else if (element instanceof Branch)
        {
          if (checked)
          {
            viewer.getControl().getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                viewer.editElement(element, 1);
              }
            });
          }
          else
          {
            Branch branch = (Branch)element;
            viewer.setChecked(branch.getProject(), false);
          }
        }

        validate();
      }
    });

    TreeViewerColumn treeViewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
    treeViewerColumn.setLabelProvider(new DecoratingColumLabelProvider(labelProvider, new SetupDialogLabelDecorator()));
    TreeColumn trclmnProjectBranch = treeViewerColumn.getColumn();
    trclmnProjectBranch.setWidth(300);
    trclmnProjectBranch.setText("Project / Branch");

    TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(viewer, SWT.NONE);
    treeViewerColumn_1
        .setLabelProvider(new DecoratingColumLabelProvider(labelProvider, new SetupDialogLabelDecorator())
        {
          @Override
          public String getText(Object element)
          {
            if (element instanceof Branch)
            {
              Branch branch = (Branch)element;
              Setup setup = setups.get(branch);
              Eclipse eclipse = setup.getEclipseVersion();
              return labelProvider.getText(eclipse);
            }

            return "";
          }

          @Override
          public Image getImage(Object element)
          {
            return null;
          }
        });

    TreeColumn trclmnEclipseVersion = treeViewerColumn_1.getColumn();
    trclmnEclipseVersion.setWidth(150);
    trclmnEclipseVersion.setText("Eclipse Version");

    Group grpPreferences = new Group(container, SWT.NONE);
    grpPreferences.setLayout(new GridLayout(3, false));
    grpPreferences.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    // grpPreferences.setText("Preferences");
    grpPreferences.setBounds(0, 0, 70, 82);

    Label userNameLabel = new Label(grpPreferences, SWT.NONE);
    userNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    userNameLabel.setBounds(0, 0, 55, 15);
    userNameLabel.setText("Git/Gerrit ID:");

    userNameText = new Text(grpPreferences, SWT.BORDER);
    userNameText
        .setToolTipText("Must match your account on Git/Gerrit.\nDon't forget to upload your public key to that account!");
    GridData gd_userNameText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gd_userNameText.widthHint = 165;
    userNameText.setLayoutData(gd_userNameText);
    userNameText.setBounds(0, 0, 76, 21);
    userNameText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        preferences.setUserName(userNameText.getText());
        validate();
      }
    });

    // Label empty = new Label(grpPreferences, SWT.NONE);
    // empty.setBounds(0, 0, 55, 15);
    Button editButton = new Button(grpPreferences, SWT.NONE);
    editButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    editButton.setBounds(0, 0, 75, 25);
    editButton.setText("Preferences...");
    editButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        close();
        setReturnCode(RETURN_WORKBENCH);
      }
    });

    Label installFolderLabel = new Label(grpPreferences, SWT.NONE);
    installFolderLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    installFolderLabel.setBounds(0, 0, 55, 15);
    installFolderLabel.setText("Install Folder:");

    installFolderText = new Text(grpPreferences, SWT.BORDER);
    installFolderText.setToolTipText("Points to the folder where the setup tool will create the project folders.");
    installFolderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    installFolderText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        preferences.setInstallFolder(installFolderText.getText());
        setups = initSetups();
        validate();
      }
    });

    Button installFolderButton = new Button(grpPreferences, SWT.NONE);
    installFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    installFolderButton.setBounds(0, 0, 75, 25);
    installFolderButton.setText("Browse...");
    installFolderButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        DirectoryDialog dlg = new DirectoryDialog(getShell());
        dlg.setText("Select Install Folder");
        dlg.setMessage("Select a folder");
        String dir = dlg.open();
        if (dir != null)
        {
          installFolderText.setText(dir);
        }
      }
    });

    Label gitPrefixLabel = new Label(grpPreferences, SWT.NONE);
    gitPrefixLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    gitPrefixLabel.setText("Git Prefix:");

    gitPrefixText = new Text(grpPreferences, SWT.BORDER);
    gitPrefixText.setToolTipText("Points to your native Git installation in order to reuse the 'etc/gitconfig' file.");
    gitPrefixText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    gitPrefixText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        preferences.setGitPrefix(gitPrefixText.getText());
        validate();
      }
    });

    Button gitPrefixButton = new Button(grpPreferences, SWT.NONE);
    gitPrefixButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    gitPrefixButton.setBounds(0, 0, 75, 25);
    gitPrefixButton.setText("Browse...");
    gitPrefixButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        DirectoryDialog dlg = new DirectoryDialog(getShell());
        dlg.setText("Select Git Prefix Folder");
        dlg.setMessage("Select a folder");
        String dir = dlg.open();
        if (dir != null)
        {
          gitPrefixText.setText(dir);
        }
      }
    });

    parent.getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        init();
      }
    });

    return area;
  }

  /**
   * Create contents of the button bar.
   * @param parent
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, "Install", true);
    validate();
  }

  @Override
  protected Control createHelpControl(Composite parent)
  {
    ToolBar toolBar = (ToolBar)super.createHelpControl(parent);

    ImageDescriptor imageDescriptor = Activator.getImageDescriptor("icons/dialog_update.gif");
    final Image image = imageDescriptor.createImage(toolBar.getDisplay());

    ToolItem updateButton = new ToolItem(toolBar, SWT.PUSH);
    updateButton.setImage(image);
    updateButton.setToolTipText("Update");
    updateButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        updatePressed();
      }
    });

    updateButton.addDisposeListener(new DisposeListener()
    {
      public void widgetDisposed(DisposeEvent e)
      {
        image.dispose();
      }
    });

    return toolBar;
  }

  @Override
  protected void okPressed()
  {
    try
    {
      install();
    }
    catch (Throwable ex)
    {
      handleException(ex);
    }

    super.okPressed();
  }

  protected void updatePressed()
  {
    try
    {
      IRunnableWithProgress runnable = new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          IProvisioningAgent agent = ServiceUtil.getService(IProvisioningAgent.class);

          try
          {
            IStatus updateStatus = checkForUpdates(agent, monitor);
            if (updateStatus.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE)
            {
              InstallerDialog.this.getShell().getDisplay().asyncExec(new Runnable()
              {
                public void run()
                {
                  MessageDialog.openInformation(null, "Update", "No updates were found");
                }
              });
            }
            else if (updateStatus.getSeverity() != IStatus.ERROR)
            {
              InstallerDialog.this.getShell().getDisplay().asyncExec(new Runnable()
              {
                public void run()
                {
                  close();
                  setReturnCode(RETURN_RESTART);
                  MessageDialog.openInformation(null, "Update", "Updates were installed, restart required");
                }
              });
            }
            else
            {
              throw new InvocationTargetException(new CoreException(updateStatus));
            }
          }
          finally
          {
            ServiceUtil.ungetService(agent);
            monitor.done();
          }
        }
      };

      ProgressMonitorDialog dialog = new ProgressMonitorDialog(null)
      {
        @Override
        protected Point getInitialSize()
        {
          Point calculatedSize = super.getInitialSize();
          if (calculatedSize.x < 800)
          {
            calculatedSize.x = 800;
          }

          return calculatedSize;
        }
      };

      dialog.run(true, true, runnable);
    }
    catch (InterruptedException ex)
    {
      // Do nothing
    }
    catch (InvocationTargetException ex)
    {
      handleException(ex.getCause());
    }
    catch (Throwable ex)
    {
      handleException(ex);
    }
  }

  private IStatus checkForUpdates(IProvisioningAgent agent, IProgressMonitor monitor)
  {
    SubMonitor sub = SubMonitor.convert(monitor, "Checking for updates...", 1000);

    try
    {
      addRepository(agent, SetupTaskPerformer.RELENG_URL, sub.newChild(200));
    }
    catch (ProvisionException ex)
    {
      return ex.getStatus();
    }

    ProvisioningSession session = new ProvisioningSession(agent);
    IProfileRegistry profileRegistry = (IProfileRegistry)agent.getService(IProfileRegistry.class.getName());
    IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
    IQueryResult<IInstallableUnit> queryResult = profile.query(QueryUtil.createIUAnyQuery(), null);

    List<IInstallableUnit> ius = new ArrayList<IInstallableUnit>();
    for (IInstallableUnit installableUnit : queryResult)
    {
      String id = installableUnit.getId();
      if (id.startsWith("org.eclipse.emf.cdo") || id.startsWith("org.eclipse.net4j"))
      {
        ius.add(installableUnit);
      }
    }

    UpdateOperation operation = new UpdateOperation(session, ius);
    IStatus status = operation.resolveModal(sub.newChild(300));
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE)
    {
      return status;
    }

    if (status.getSeverity() == IStatus.CANCEL)
    {
      throw new OperationCanceledException();
    }

    if (status.getSeverity() != IStatus.ERROR)
    {
      ProvisioningJob job = operation.getProvisioningJob(null);
      if (job == null)
      {
        String resolutionDetails = operation.getResolutionDetails();
        throw new IllegalStateException(resolutionDetails);
      }

      status = job.runModal(sub.newChild(300));
      if (status.getSeverity() == IStatus.CANCEL)
      {
        throw new OperationCanceledException();
      }
    }

    return status;
  }

  private void addRepository(IProvisioningAgent agent, String location, IProgressMonitor monitor)
      throws ProvisionException
  {
    SubMonitor sub = SubMonitor.convert(monitor, "Loading " + location, 1000);

    try
    {
      java.net.URI uri = new java.net.URI(location);
      addMetadataRepository(agent, uri, sub.newChild(500));
      addArtifactRepository(agent, uri, sub.newChild(500));
    }
    catch (URISyntaxException ex)
    {
      throw new IllegalArgumentException(ex);
    }
  }

  private void addMetadataRepository(IProvisioningAgent agent, java.net.URI location, IProgressMonitor monitor)
      throws ProvisionException
  {
    IMetadataRepositoryManager manager = (IMetadataRepositoryManager)agent
        .getService(IMetadataRepositoryManager.SERVICE_NAME);
    if (manager == null)
    {
      throw new IllegalStateException("No metadata repository manager found");
    }

    manager.loadRepository(location, monitor);
  }

  private void addArtifactRepository(IProvisioningAgent agent, java.net.URI location, IProgressMonitor monitor)
      throws ProvisionException
  {
    IArtifactRepositoryManager manager = (IArtifactRepositoryManager)agent
        .getService(IArtifactRepositoryManager.SERVICE_NAME);
    if (manager == null)
    {
      throw new IllegalStateException("No metadata repository manager found");
    }

    manager.loadRepository(location, monitor);
  }

  private void init()
  {
    try
    {
      IRunnableWithProgress runnable = new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          monitor.subTask("Loading " + EMFUtil.SETUP_URI.trimFragment());

          Resource configurationResource = EMFUtil.loadResourceSafe(resourceSet, EMFUtil.SETUP_URI);
          configuration = (Configuration)configurationResource.getContents().get(0);

          InternalEList<Project> configuredProjects = (InternalEList<Project>)configuration.getProjects();
          monitor.beginTask("Processing the configuration", 1 + configuredProjects.size());

          String userName;
          String installFolder;
          String gitPrefix;

          monitor.subTask("Loading " + Preferences.PREFERENCES_URI.trimFragment());

          if (resourceSet.getURIConverter().exists(Preferences.PREFERENCES_URI, null))
          {
            Resource resource = EMFUtil.loadResourceSafe(resourceSet, Preferences.PREFERENCES_URI);
            preferences = (Preferences)resource.getContents().get(0);

            userName = safe(preferences.getUserName());
            installFolder = safe(preferences.getInstallFolder());
            gitPrefix = safe(preferences.getGitPrefix());
          }
          else
          {
            Resource resource = resourceSet.createResource(Preferences.PREFERENCES_URI);
            preferences = SetupFactory.eINSTANCE.createPreferences();
            resource.getContents().add(preferences);

            File rootFolder = new File(System.getProperty("user.home", "."));

            userName = "";
            installFolder = safe(getAbsolutePath(rootFolder));
            gitPrefix = safe(getAbsolutePath(new File(OS.INSTANCE.getGitPrefix())));
          }

          monitor.worked(1);

          ItemProvider input = new ItemProvider();
          EList<Object> projects = input.getChildren();

          for (int i = 0; i < configuredProjects.size(); i++)
          {
            if (monitor.isCanceled())
            {
              throw new OperationCanceledException();
            }

            InternalEObject project = (InternalEObject)configuredProjects.basicGet(i);
            if (project.eIsProxy())
            {
              URI uri = project.eProxyURI().trimFragment();
              if (!uri.equals(EMFUtil.EXAMPLE_PROXY_URI))
              {
                monitor.subTask("Loading " + uri);
              }

              project = (InternalEObject)configuredProjects.get(i);
            }

            if (!project.eIsProxy() && !((Project)project).getBranches().isEmpty())
            {
              projects.add(project);
            }

            monitor.worked(1);
          }

          initUI(input, userName, installFolder, gitPrefix);
          monitor.done();
        }

        private void initUI(final ItemProvider input, final String userName, final String installFolder,
            final String gitPrefix)
        {
          viewer.getControl().getDisplay().asyncExec(new Runnable()
          {
            public void run()
            {
              userNameText.setText(userName);
              installFolderText.setText(installFolder);
              gitPrefixText.setText(gitPrefix);

              viewer.setInput(input);
              viewer.expandAll();

              cellEditor.setInput(this);
            }
          });
        }
      };

      ProgressMonitorDialog dialog = new ProgressMonitorDialog(viewer.getControl().getShell());
      dialog.run(true, true, runnable);
    }
    catch (InterruptedException ex)
    {
      // Do nothing
    }
    catch (InvocationTargetException ex)
    {
      handleException(ex.getCause());
    }
    catch (Throwable ex)
    {
      handleException(ex);
    }
  }

  private Map<Branch, Setup> initSetups()
  {
    Map<Branch, Setup> setups = new HashMap<Branch, Setup>();
    for (Project project : configuration.getProjects())
    {
      for (Branch branch : project.getBranches())
      {
        Setup setup;
        URI uri = getSetupURI(branch);
        if (resourceSet.getURIConverter().exists(uri, null))
        {
          Resource resource = EMFUtil.loadResourceSafe(resourceSet, uri);
          setup = (Setup)resource.getContents().get(0);
        }
        else
        {
          setup = SetupFactory.eINSTANCE.createSetup();
          setup.setEclipseVersion(getDefaultEclipseVersion());
          setup.setBranch(branch);
          setup.setPreferences(preferences);

          Resource resource = resourceSet.createResource(uri);
          resource.getContents().add(setup);
        }

        setups.put(branch, setup);
      }
    }

    return setups;
  }

  private Eclipse getDefaultEclipseVersion()
  {
    EList<Eclipse> eclipses = configuration.getEclipseVersions();
    return eclipses.get(eclipses.size() - 1);
  }

  private void validate()
  {
    if (viewer != null)
    {
      viewer.refresh(true);
    }

    Button installButton = getButton(IDialogConstants.OK_ID);
    if (installButton == null)
    {
      return;
    }

    if (viewer.getCheckedElements().length == 0)
    {
      setMessage("Select one or more project branches to install.", IMessageProvider.NONE);
      installButton.setEnabled(false);
      return;
    }

    if (userNameText.getText().length() == 0)
    {
      setMessage("Enter your Eclipse Git/Gerrit ID or '" + GitCloneTask.ANONYMOUS + "'.", IMessageProvider.ERROR);
      installButton.setEnabled(false);
      return;
    }

    if (installFolderText.getText().length() == 0)
    {
      setMessage("Enter the install folder.", IMessageProvider.ERROR);
      installButton.setEnabled(false);
      return;
    }

    if (Platform.OS_WIN32.equals(Platform.getOS()))
    {
      String gitPrefix = gitPrefixText.getText();
      if (gitPrefix.length() == 0)
      {
        setMessage("Enter the Git installation folder to use its 'etc/gitconfig' in the installed IDE.",
            IMessageProvider.WARNING);
        installButton.setEnabled(true);
        return;
      }

      File etc = new File(gitPrefix, "etc");
      File gitconfig = new File(etc, "gitconfig");
      if (!gitconfig.isFile())
      {
        setMessage("The Git prefix folder does not contain 'etc/gitconfig'.", IMessageProvider.WARNING);
        installButton.setEnabled(true);
        return;
      }
    }

    setMessage("Click the Install button to start the installation process.", IMessageProvider.NONE);
    installButton.setEnabled(true);
  }

  private String safe(String string)
  {
    if (string == null)
    {
      return "";
    }

    return string;
  }

  private String getAbsolutePath(File file)
  {
    try
    {
      return file.getCanonicalPath();
    }
    catch (IOException ex)
    {
      return file.getAbsolutePath();
    }
  }

  private URI getSetupURI(Branch branch)
  {
    return getSetupURI(branch, installFolderText.getText());
  }

  private URI getSetupURI(Branch branch, String installFolder)
  {
    File projectFolder = new File(installFolder, branch.getProject().getName().toLowerCase());
    File branchFolder = new File(projectFolder, branch.getName().toLowerCase());
    File setupFile = new File(branchFolder, "setup.xmi");
    return URI.createFileURI(setupFile.getAbsolutePath());
  }

  private boolean isInstalled(Object object)
  {
    if (object instanceof Branch)
    {
      Branch branch = (Branch)object;
      Setup setup = setups.get(branch);
      URI uri = setup.eResource().getURI();
      return resourceSet.getURIConverter().exists(uri, null);
    }

    return false;
  }

  private void install() throws Exception
  {
    final Object[] checkedElements = viewer.getCheckedElements();
    final String installFolder = installFolderText.getText();
    final String gitPrefix = safe(gitPrefixText.getText());

    File folder = new File(installFolder);
    folder.mkdirs();

    final List<SetupTaskPerformer> setupTaskPerformers = new ArrayList<SetupTaskPerformer>();
    for (Object checkedElement : checkedElements)
    {
      if (checkedElement instanceof Branch)
      {
        Branch branch = (Branch)checkedElement;
        Setup setup = setups.get(branch);
        if (setup != null)
        {
          setupTaskPerformers.add(createTaskPerformer(setup, installFolder, gitPrefix));
        }
      }
    }

    ProgressLogDialog.run(getShell(), new ProgressLogRunnable()
    {
      public Set<String> run(ProgressLog log) throws Exception
      {
        for (SetupTaskPerformer setupTaskPerformer : setupTaskPerformers)
        {
          try
          {
            install(setupTaskPerformer);
          }
          catch (IOException ex)
          {
            throw new RuntimeException(ex);
          }
        }

        return null;
      }
    }, setupTaskPerformers);
  }

  private SetupTaskPerformer createTaskPerformer(Setup setup, String installFolder, String gitPrefix)
  {
    saveEObject(setup);

    Branch branch = setup.getBranch();
    Project project = branch.getProject();

    String branchFolder = branch.getName().toLowerCase();
    String projectFolder = project.getName().toLowerCase();
    File branchDir = new File(installFolder, projectFolder + "/" + branchFolder);

    SetupTaskPerformer performer = new SetupTaskPerformer(branchDir);
    return performer;
  }

  private void install(SetupTaskPerformer performer) throws Exception
  {
    performer.getWorkspaceDir().mkdirs();
    performer.perform();

    performer.log("Launching IDE");
    String eclipseExecutable = performer.getOS().getEclipseExecutable();
    String eclipsePath = new File(performer.getBranchDir(), "eclipse/" + eclipseExecutable).getAbsolutePath();
    File ws = new File(performer.getBranchDir(), "ws");

    ProcessBuilder builder = new ProcessBuilder(eclipsePath);
    builder.directory(ws);
    builder.start();
  }

  private void saveEObject(EObject eObject)
  {
    try
    {
      XMLResource xmlResource = (XMLResource)eObject.eResource();
      xmlResource.getEObjectToExtensionMap().clear();
      xmlResource.save(null);
    }
    catch (IOException ex)
    {
      Activator.log(ex);
    }
  }

  private void handleException(Throwable ex)
  {
    Activator.log(ex);
    ErrorDialog.open(ex);
  }

  /**
   * @author Eike Stepper
   */
  private final class SetupDialogAdapterFactory extends SetupItemProviderAdapterFactory
  {
    @Override
    public Adapter createConfigurationAdapter()
    {
      if (configurationItemProvider == null)
      {
        configurationItemProvider = new ConfigurationItemProvider(this)
        {
          @Override
          public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
          {
            if (childrenFeatures == null)
            {
              childrenFeatures = new ArrayList<EStructuralFeature>();
              childrenFeatures.add(SetupPackage.Literals.CONFIGURATION__PROJECTS);
            }

            return childrenFeatures;
          }
        };
      }

      return configurationItemProvider;
    }

    @Override
    public Adapter createProjectAdapter()
    {
      if (projectItemProvider == null)
      {
        projectItemProvider = new ProjectItemProvider(this)
        {
          @Override
          public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
          {
            if (childrenFeatures == null)
            {
              childrenFeatures = new ArrayList<EStructuralFeature>();
              childrenFeatures.add(SetupPackage.Literals.PROJECT__BRANCHES);
            }

            return childrenFeatures;
          }
        };
      }

      return projectItemProvider;
    }

    @Override
    public Adapter createBranchAdapter()
    {
      if (branchItemProvider == null)
      {
        branchItemProvider = new BranchItemProvider(this)
        {
          @Override
          public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
          {
            if (childrenFeatures == null)
            {
              childrenFeatures = new ArrayList<EStructuralFeature>();
            }

            return childrenFeatures;
          }
        };
      }

      return branchItemProvider;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SetupDialogLabelDecorator extends LabelProvider implements ILabelDecorator
  {
    public Image decorateImage(Image image, Object element)
    {
      return image;
    }

    public String decorateText(String text, Object element)
    {
      return text;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SetupDialogLabelProvider extends AdapterFactoryLabelProvider.ColorProvider
  {
    private SetupDialogLabelProvider(AdapterFactory adapterFactory, Viewer viewer)
    {
      super(adapterFactory, viewer);
    }

    @Override
    public Color getForeground(Object object)
    {
      if (isInstalled(object))
      {
        return getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE);
      }

      return super.getBackground(object);
    }
  }
}
