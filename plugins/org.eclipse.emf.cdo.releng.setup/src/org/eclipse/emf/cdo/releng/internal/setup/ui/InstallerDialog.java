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
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.AbstractSetupTaskContext;
import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskMigrator;
import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskPerformer;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.util.OS;
import org.eclipse.emf.cdo.releng.setup.util.ServiceUtil;
import org.eclipse.emf.cdo.releng.setup.util.SetupResource;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.provider.IItemFontProvider;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedFontRegistry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eike Stepper
 */
public class InstallerDialog extends AbstractSetupDialog
{
  public static final String TRAIN_URL = "http://download.eclipse.org/releases/luna";

  public static final int RETURN_WORKBENCH_NETWORK_PREFERENCES = -2;

  public static final int RETURN_WORKBENCH = -3;

  public static final int RETURN_RESTART = -4;

  private static final String ECLIPSE_VERSION_COLUMN = "eclipse";

  private static final String[] PRODUCT_PREFIXES = { "org.eclipse.emf.cdo.releng", "org.eclipse.net4j" };

  private static final String PRODUCT_ID = "org.eclipse.emf.cdo.releng.setup.installer.product";

  private static final Object[] NO_ELEMENTS = new Object[0];

  private static final IStatus UPDATE_FOUND_STATUS = new Status(IStatus.OK, Activator.PLUGIN_ID, "Updates found");

  private StartType startType;

  private Map<Branch, Setup> setups;

  private ResourceSet resourceSet;

  private Preferences preferences;

  private Configuration configuration;

  private CheckboxTreeViewer viewer;

  private Text installFolderText;

  private Text bundlePoolText;

  private Text bundlePoolTPText;

  private ComboBoxViewerCellEditor cellEditor;

  private Link versionLink;

  private boolean considerVisibleProjects;

  private ToolItem updateToolItem;

  private UpdateSearchState updateSearchState;

  public InstallerDialog(Shell parentShell, StartType startType, boolean considerVisibleProjects)
  {
    super(parentShell, "Install Development Environments", 500, 500, Activator.getDefault().getBundle(),
        "/help/installer/InstallerDialog.html");
    this.startType = startType;
    this.considerVisibleProjects = considerVisibleProjects;
    resourceSet = EMFUtil.createResourceSet();
  }

  public InstallerDialog(Shell parentShell, Project project)
  {
    this(parentShell, StartType.EDITOR, false);

    URI uri = project.eResource().getURI();
    if (uri.isPlatformResource())
    {
      IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri.toPlatformString(true)));
      uri = URI.createFileURI(file.getLocation().toString());
    }

    resourceSet.getURIConverter().getURIMap().put(EMFUtil.EXAMPLE_PROXY_URI, uri);
  }

  @Override
  protected String getDefaultMessage()
  {
    return null;
  }

  @Override
  protected void createUI(Composite parent)
  {
    viewer = new CheckboxTreeViewer(parent, SWT.FULL_SELECTION | SWT.NO_SCROLL | SWT.V_SCROLL);

    final Tree tree = viewer.getTree();
    tree.setLinesVisible(true);
    tree.setHeaderVisible(true);
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    final Map<Object, Object> parentMap = new HashMap<Object, Object>();
    final AdapterFactoryContentProvider contentProvider = new AdapterFactoryContentProvider(EMFUtil.ADAPTER_FACTORY)
    {
      @Override
      public boolean hasChildren(Object object)
      {
        if (object instanceof Configuration)
        {
          return !((Configuration)object).getProjects().isEmpty();
        }

        if (object instanceof Project)
        {
          return true;
        }

        if (object instanceof Branch)
        {
          return false;
        }

        return super.hasChildren(object);
      }

      @Override
      public Object getParent(Object object)
      {
        Object parent = parentMap.get(object);
        if (parent != null)
        {
          return parent;
        }

        if (object instanceof Project)
        {
          return viewer.getInput();
        }

        return super.getParent(object);
      }

      @Override
      public Object[] getChildren(Object object)
      {
        if (object instanceof Configuration)
        {
          EList<Project> projects = ((Configuration)object).getProjects();
          return projects.toArray(new Project[projects.size()]);
        }

        if (object instanceof Project)
        {
          final InternalEObject eObject = (InternalEObject)object;
          final URI eProxyURI = eObject.eProxyURI();
          if (eProxyURI != null)
          {
            final URI resourceURI = eProxyURI.trimFragment();
            Resource resource = resourceSet.getResource(resourceURI, false);
            if (resource == null)
            {
              tree.getDisplay().asyncExec(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    Resource resource = loadResourceSafely(resourceURI);
                    final Project project = (Project)resource.getEObject(eProxyURI.fragment());
                    if (project != null)
                    {
                      // Force proxy reference from the configuration to resolve too.
                      EList<Project> projects = configuration.getProjects();
                      int index = projects.indexOf(eObject);
                      if (index != -1)
                      {
                        projects.get(index);
                      }

                      ItemProvider input = (ItemProvider)viewer.getInput();
                      EList<Object> children = input.getChildren();
                      index = children.indexOf(eObject);
                      children.set(index, project);
                      tree.getDisplay().asyncExec(new Runnable()
                      {
                        public void run()
                        {
                          InstallerDialog.this.viewer.expandToLevel(project, 1);
                        }
                      });
                    }
                  }
                  catch (UpdatingException ex)
                  {
                    // Ignore
                  }
                }
              });

              return NO_ELEMENTS;
            }

            object = resource.getEObject(eProxyURI.fragment());
          }
        }

        if (object instanceof Project)
        {
          return ((Project)object).getBranches().toArray();
        }

        if (object instanceof Branch)
        {
          return NO_ELEMENTS;
        }

        return super.getChildren(object);
      }
    };

    viewer.setContentProvider(contentProvider);

    DialogLabelProvider labelProvider = new DialogLabelProvider(EMFUtil.ADAPTER_FACTORY, viewer);
    viewer.setLabelProvider(labelProvider);
    viewer.setCellModifier(new ICellModifier()
    {
      public boolean canModify(Object element, String property)
      {
        Setup setup = getElementSetup(element, property);
        return setup != null;
      }

      public Object getValue(Object element, String property)
      {
        Setup setup = getElementSetup(element, property);
        if (setup != null)
        {
          return setup.getEclipseVersion();
        }

        return null;
      }

      public void modify(Object element, String property, Object value)
      {
        Setup setup = getElementSetup(element, property);
        if (setup != null)
        {
          setup.setEclipseVersion((Eclipse)value);
          viewer.update(setup.getBranch(), new String[] { property });
        }
      }

      private Setup getElementSetup(Object element, String property)
      {
        if (element instanceof TreeItem)
        {
          TreeItem item = (TreeItem)element;
          element = item.getData();
        }

        if (element instanceof Branch && ECLIPSE_VERSION_COLUMN.equals(property))
        {
          Branch branch = (Branch)element;
          return getSetup(branch);
        }

        return null;
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
        Object selection = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
        if (selection instanceof Branch)
        {
          Branch branch = (Branch)selection;
          EList<Eclipse> eclipses = getAllowedEclipseVersions(branch);
          return eclipses.toArray(new Eclipse[eclipses.size()]);
        }

        return NO_ELEMENTS;
      }
    });

    viewer.setCellEditors(new CellEditor[] { null, cellEditor });
    cellEditor.setLabelProvider(new AdapterFactoryLabelProvider(EMFUtil.ADAPTER_FACTORY));

    viewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        ComboViewer comboViewer = cellEditor.getViewer();
        comboViewer.refresh();

        Object selection = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
        if (selection instanceof Branch)
        {
          Branch branch = (Branch)selection;
          Setup setup = getSetup(branch);
          if (setup != null)
          {
            Eclipse eclipse = setup.getEclipseVersion();
            comboViewer.setSelection(new StructuredSelection(eclipse));
          }
        }
      }
    });

    viewer.addCheckStateListener(new ICheckStateListener()
    {
      public void checkStateChanged(CheckStateChangedEvent event)
      {
        final boolean checked = event.getChecked();
        final Object element = event.getElement();
        if (element instanceof Project)
        {
          final Project project = (Project)element;
          if (project.eIsProxy())
          {
            // Force the proxy to resolve.
            contentProvider.getChildren(project);
            viewer.getControl().getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                final EObject resolvedProject = EcoreUtil.resolve(project, resourceSet);
                viewer.getControl().getDisplay().asyncExec(new Runnable()
                {
                  public void run()
                  {
                    viewer.setChecked(resolvedProject, checked);
                    for (Object branch : contentProvider.getChildren(resolvedProject))
                    {
                      viewer.setChecked(branch, checked);
                    }
                  }
                });
              }
            });
          }
          else
          {
            for (Object branch : contentProvider.getChildren(project))
            {
              viewer.setChecked(branch, checked);
            }

            viewer.expandToLevel(project, 1);
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
                Branch branch = (Branch)element;
                boolean allChecked = true;
                for (Branch branch2 : branch.getProject().getBranches())
                {
                  if (!viewer.getChecked(branch2))
                  {
                    allChecked = false;
                    break;
                  }
                }

                if (allChecked)
                {
                  viewer.setChecked(branch.getProject(), true);
                }

                viewer.editElement(branch, 1);
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

    TreeViewerColumn projectViewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
    projectViewerColumn.setLabelProvider(new DecoratingColumLabelProvider(labelProvider, new DialogLabelDecorator()));

    final TreeColumn projectColumn = projectViewerColumn.getColumn();
    projectColumn.setText("Project / Branch");
    projectColumn.setResizable(false);
    projectColumn.setMoveable(false);

    TreeViewerColumn eclipseViewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
    eclipseViewerColumn.setLabelProvider(new DecoratingColumLabelProvider(labelProvider, new DialogLabelDecorator())
    {
      @Override
      public String getText(Object element)
      {
        if (element instanceof Branch)
        {
          Branch branch = (Branch)element;
          Setup setup = getSetup(branch);
          if (setup != null)
          {
            Eclipse eclipse = setup.getEclipseVersion();
            return labelProvider.getText(eclipse);
          }
        }

        return "";
      }

      @Override
      public Image getImage(Object element)
      {
        return null;
      }
    });

    final TreeColumn eclipseColumn = eclipseViewerColumn.getColumn();
    eclipseColumn.setText("Eclipse Version");
    eclipseColumn.setMoveable(false);
    eclipseColumn.setResizable(false);

    createSeparator(parent);

    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 10;
    layout.marginHeight = 10;

    Composite group = new Composite(parent, SWT.NONE);
    group.setLayout(layout);
    group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

    {
      Label label = new Label(group, SWT.NONE);
      label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      label.setBounds(0, 0, 55, 15);
      label.setText("Install Folder:");

      installFolderText = new Text(group, SWT.BORDER);
      installFolderText.setToolTipText("Points to the folder where the setup tool will create the project folders.");
      installFolderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
      installFolderText.addModifyListener(new ModifyListener()
      {
        private String previousText;

        public void modifyText(ModifyEvent e)
        {
          setups = null;

          String text = installFolderText.getText();
          preferences.setInstallFolder(text);

          String defaultBundlePoolSuffix = File.separator + ".p2pool-ide";
          if (previousText == null || bundlePoolText.getText().equals(previousText + defaultBundlePoolSuffix))
          {
            bundlePoolText.setText(text + defaultBundlePoolSuffix);
          }

          String defaultBundlePoolTPSuffix = File.separator + ".p2pool-tp";
          if (previousText == null || bundlePoolTPText.getText().equals(previousText + defaultBundlePoolTPSuffix))
          {
            bundlePoolTPText.setText(text + defaultBundlePoolTPSuffix);
          }

          previousText = text;

          saveEObject(preferences);
          validate();
        }
      });

      Button button = new Button(group, SWT.NONE);
      button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
      button.setBounds(0, 0, 75, 25);
      button.setText("Browse...");
      button.addSelectionListener(new SelectionAdapter()
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
    }

    {
      Label label = new Label(group, SWT.NONE);
      label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      label.setText("Bundle Pool:");

      bundlePoolText = new Text(group, SWT.BORDER);
      bundlePoolText
          .setToolTipText("Points to the folder where the setup tool will create the p2 bundle pool for IDEs.");
      bundlePoolText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
      bundlePoolText.addModifyListener(new ModifyListener()
      {
        public void modifyText(ModifyEvent e)
        {
          preferences.setBundlePoolFolder(bundlePoolText.getText());
          saveEObject(preferences);
          validate();
        }
      });

      Button button = new Button(group, SWT.NONE);
      button.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, false, false, 1, 1));
      button.setBounds(0, 0, 75, 25);
      button.setText("Browse...");
      button.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          DirectoryDialog dlg = new DirectoryDialog(getShell());
          dlg.setText("Bundle Pool Folder");
          dlg.setMessage("Select a p2 bundle pool folder for IDEs.");
          String dir = dlg.open();
          if (dir != null)
          {
            bundlePoolText.setText(dir);
          }
        }
      });
    }

    {
      Label label = new Label(group, SWT.NONE);
      label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      label.setText("TP Bundle Pool:");

      bundlePoolTPText = new Text(group, SWT.BORDER);
      bundlePoolTPText
          .setToolTipText("Points to the folder where the setup tool will create the p2 bundle pool for target platforms.");
      bundlePoolTPText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
      bundlePoolTPText.addModifyListener(new ModifyListener()
      {
        public void modifyText(ModifyEvent e)
        {
          preferences.setBundlePoolFolderTP(bundlePoolTPText.getText());
          saveEObject(preferences);
          validate();
        }
      });

      Button button = new Button(group, SWT.NONE);
      button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
      button.setBounds(0, 0, 75, 25);
      button.setText("Browse...");
      button.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          DirectoryDialog dlg = new DirectoryDialog(getShell());
          dlg.setText("Bundle Pool Folder");
          dlg.setMessage("Select a p2 bundle pool folder for target platforms.");
          String dir = dlg.open();
          if (dir != null)
          {
            bundlePoolTPText.setText(dir);
          }
        }
      });
    }

    parent.getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        init();
      }
    });
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, "Install", true);
    validate();
  }

  @Override
  protected Control createHelpControl(Composite parent)
  {
    Control helpControl = super.createHelpControl(parent);
    setProductVersionLink(parent);
    return helpControl;
  }

  @Override
  protected void createToolItemsForToolBar(ToolBar toolBar)
  {
    createToolItem(toolBar, "icons/install_prefs.gif", "Preferences").addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        close();
        setReturnCode(RETURN_WORKBENCH);
      }
    });

    createToolItem(toolBar, "icons/install_network.gif", "Network connection settings").addSelectionListener(
        new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            close();
            setReturnCode(RETURN_WORKBENCH_NETWORK_PREFERENCES);
          }
        });

    updateToolItem = createToolItem(toolBar, "icons/install_update0.gif", "Update");
    updateToolItem.setDisabledImage(getDefaultImage("icons/install_searching0.gif"));
    updateToolItem.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        update(false);
      }
    });

    initUpdateSearch();
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

  protected boolean update(final boolean needsEarlyConfirmation)
  {
    if (needsEarlyConfirmation)
    {
      final AtomicBoolean result = new AtomicBoolean();
      InstallerDialog.this.getShell().getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          boolean confirmation = MessageDialog
              .openQuestion(
                  null,
                  "Update",
                  "Updates are needed to process the configuration, and then a restart is required. "
                      + "It might be possible for the tool to process the configuration with an older version of the tool, but that's not recommended.\n\n"
                      + "Do you wish to update?");
          result.set(confirmation);
        }
      });

      if (!result.get())
      {
        return false;
      }
    }

    try
    {
      final IRunnableWithProgress runnable = new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          IProvisioningAgent agent = ServiceUtil.getService(IProvisioningAgent.class);

          try
          {
            SubMonitor sub = SubMonitor.convert(monitor, needsEarlyConfirmation ? "Updating..."
                : "Checking for updates...", 1000);

            IStatus updateStatus = checkForUpdates(agent, false, sub);
            if (updateStatus.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE)
            {
              InstallerDialog.this.getShell().getDisplay().asyncExec(new Runnable()
              {
                public void run()
                {
                  MessageDialog.openInformation(null, "Update", "No updates were found.");
                }
              });
            }
            else if (updateStatus.getSeverity() != IStatus.ERROR)
            {
              InstallerDialog.this.getShell().getDisplay().asyncExec(new Runnable()
              {
                public void run()
                {
                  if (!needsEarlyConfirmation)
                  {
                    MessageDialog.openInformation(null, "Update", "Updates were installed. Press OK to restart.");
                  }

                  close();
                  setReturnCode(RETURN_RESTART);
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

      viewer.getControl().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            runInProgressDialog(runnable);
          }
          catch (InvocationTargetException ex)
          {
            handleException(ex.getCause());
          }
          catch (InterruptedException ex)
          {
            // Do nothing
          }
        }
      });
    }
    catch (Throwable ex)
    {
      handleException(ex);
    }

    return true;
  }

  private IStatus checkForUpdates(IProvisioningAgent agent, boolean resolveOnly, SubMonitor sub)
  {
    try
    {
      try
      {
        addRepositories(agent, true, sub);
      }
      catch (ProvisionException ex)
      {
        return ex.getStatus();
      }

      ProvisioningSession session = new ProvisioningSession(agent);
      List<IInstallableUnit> ius = getInstalledUnits(session, PRODUCT_PREFIXES).getElement2();

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

        if (resolveOnly)
        {
          return UPDATE_FOUND_STATUS;
        }

        sub.setTaskName("Installing updates...");

        try
        {
          addRepositories(agent, false, sub);
        }
        catch (ProvisionException ex)
        {
          return ex.getStatus();
        }

        status = job.runModal(sub.newChild(300));
        if (status.getSeverity() == IStatus.CANCEL)
        {
          throw new OperationCanceledException();
        }
      }

      return status;
    }
    finally
    {
      if (!resolveOnly)
      {
        setUpdateIcon(0);
      }
    }
  }

  private void initUpdateSearch()
  {
    if (startType != StartType.APPLICATION)
    {
      return;
    }

    updateSearchState = UpdateSearchState.SEARCHING;

    new Thread("Update Icon Setter")
    {
      @Override
      public void run()
      {
        try
        {
          for (int i = 0; updateSearchState != UpdateSearchState.DONE; i = ++i % 20)
          {
            if (updateToolItem == null || updateToolItem.isDisposed())
            {
              return;
            }

            int icon = i > 7 ? 0 : i;
            setUpdateIcon(icon);
            sleep(80);
          }

          setUpdateIcon(0);
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }
    }.start();

    new Thread("Update Searcher")
    {
      @Override
      public void run()
      {
        try
        {
          IProvisioningAgent agent = ServiceUtil.getService(IProvisioningAgent.class);

          try
          {
            IStatus status = checkForUpdates(agent, true, SubMonitor.convert(null));
            if (status == UPDATE_FOUND_STATUS)
            {
              updateSearchState = UpdateSearchState.FOUND;
            }
            else
            {
              updateSearchState = UpdateSearchState.DONE;
            }
          }
          finally
          {
            ServiceUtil.ungetService(agent);
          }
        }
        catch (OperationCanceledException ex)
        {
          // Ignore
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }
    }.start();
  }

  private void setUpdateIcon(final int icon)
  {
    updateToolItem.getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        if (updateToolItem == null || updateToolItem.isDisposed())
        {
          return;
        }

        try
        {
          switch (updateSearchState)
          {
          case SEARCHING:
            updateToolItem.setToolTipText("Checking for updates...");
            updateToolItem.setDisabledImage(getDefaultImage("icons/install_searching" + icon + ".gif"));
            updateToolItem.setEnabled(false);
            break;

          case FOUND:
            updateToolItem.setToolTipText("Install available updates");
            updateToolItem.setImage(getDefaultImage("icons/install_update" + icon + ".gif"));
            updateToolItem.setEnabled(true);
            break;

          case DONE:
            updateToolItem.setToolTipText("No updates available");
            updateToolItem.setDisabledImage(getDefaultImage("icons/install_update_disabled.gif"));
            updateToolItem.setEnabled(false);
            break;
          }
        }
        catch (Exception ex)
        {
          // Ignore
        }
      }
    });
  }

  private Pair<String, List<IInstallableUnit>> getInstalledUnits(ProvisioningSession session, String... iuPrefixes)
  {
    IProvisioningAgent agent = session.getProvisioningAgent();
    IProfileRegistry profileRegistry = (IProfileRegistry)agent.getService(IProfileRegistry.class.getName());
    IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
    if (profile == null)
    {
      List<IInstallableUnit> none = Collections.emptyList();
      return Pair.create("SelfHostingProfile", none);
    }

    IQueryResult<IInstallableUnit> queryResult = profile.query(QueryUtil.createIUAnyQuery(), null);

    List<IInstallableUnit> ius = new ArrayList<IInstallableUnit>();
    for (IInstallableUnit installableUnit : queryResult)
    {
      String id = installableUnit.getId();

      if (iuPrefixes.length == 0)
      {
        ius.add(installableUnit);
      }
      else
      {
        if (hasPrefix(id, iuPrefixes))
        {
          ius.add(installableUnit);
        }
      }
    }

    return Pair.create(profile.getProfileId(), ius);
  }

  private boolean hasPrefix(String id, String[] iuPrefixes)
  {
    for (int i = 0; i < iuPrefixes.length; i++)
    {
      String iuPrefix = iuPrefixes[i];
      if (id.startsWith(iuPrefix))
      {
        return true;
      }
    }

    return false;
  }

  private String getProductVersion()
  {
    IProvisioningAgent agent = ServiceUtil.getService(IProvisioningAgent.class);

    try
    {
      ProvisioningSession session = new ProvisioningSession(agent);
      Pair<String, List<IInstallableUnit>> profileAndIUs = getInstalledUnits(session, PRODUCT_ID);
      if ("SelfHostingProfile".equals(profileAndIUs.getElement1()))
      {
        return "Self Hosting";
      }

      List<IInstallableUnit> installedUnits = profileAndIUs.getElement2();
      if (installedUnits.isEmpty())
      {
        return null;
      }

      IInstallableUnit product = installedUnits.iterator().next();
      return product.getVersion().toString();
    }
    finally
    {
      ServiceUtil.ungetService(agent);
    }
  }

  private void setProductVersionLink(Composite parent)
  {
    GridLayout parentLayout = (GridLayout)parent.getLayout();
    parentLayout.numColumns++;
    parentLayout.horizontalSpacing = 10;

    versionLink = new Link(parent, SWT.NO_FOCUS);
    versionLink.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER));
    versionLink.setToolTipText("About");

    new Thread("Product Version Setter")
    {
      @Override
      public void run()
      {
        try
        {
          final String version = getProductVersion();
          if (version != null)
          {
            versionLink.getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                try
                {
                  versionLink.addSelectionListener(new SelectionAdapter()
                  {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                      new AboutDialog(version).open();
                    }
                  });

                  versionLink.setText("<a>" + version + "</a>"); //$NON-NLS-1$
                  versionLink.getParent().layout();
                }
                catch (Exception ex)
                {
                  Activator.log(ex);
                }
              }
            });
          }
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }
    }.start();
  }

  private void addRepositories(IProvisioningAgent agent, boolean metadata, SubMonitor sub) throws ProvisionException
  {
    addRepository(agent, TRAIN_URL, metadata, sub.newChild(200));
    addRepository(agent, SetupConstants.RELENG_URL, metadata, sub.newChild(200));
  }

  private void addRepository(IProvisioningAgent agent, String location, boolean metadata, IProgressMonitor monitor)
      throws ProvisionException
  {
    SubMonitor sub = SubMonitor.convert(monitor, "Loading " + location, 500);

    try
    {
      java.net.URI uri = new java.net.URI(location);

      if (metadata)
      {
        addMetadataRepository(agent, uri, sub);
      }
      else
      {
        addArtifactRepository(agent, uri, sub);
      }
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
      throw new IllegalStateException("No artifact repository manager found");
    }

    manager.loadRepository(location, monitor);
  }

  private SetupResource loadResourceSafely(URI uri) throws UpdatingException
  {
    SetupResource resource = EMFUtil.loadResourceSafely(resourceSet, uri);
    if (resource.getToolVersion() > SetupTaskMigrator.TOOL_VERSION)
    {
      if (update(true))
      {
        throw new UpdatingException();
      }
    }

    return resource;
  }

  /**
   * Returns a list of project proxies.
   */
  private List<Project> getVisibleProjects()
  {
    Set<File> directories = new LinkedHashSet<File>();
    directories.add(new File("").getAbsoluteFile());
    directories.add(new File(System.getProperty("user.dir")).getAbsoluteFile());
    directories.add(new File(System.getProperty("user.home")).getAbsoluteFile());

    List<Project> projects = new ArrayList<Project>();
    for (File directory : directories)
    {
      if (directory.isDirectory())
      {
        for (String file : directory.list())
        {
          if (file.endsWith(".setup"))
          {
            ResourceSet resourceSet = new ResourceSetImpl();
            URI projectURI = URI.createFileURI(new File(directory, file).toString());
            SetupResource resource = EMFUtil.loadResourceSafely(resourceSet, projectURI);
            Project project = (Project)EcoreUtil.getObjectByType(resource.getContents(), SetupPackage.Literals.PROJECT);
            if (project != null)
            {
              Project projectProxy = SetupFactory.eINSTANCE.createProject();
              ((InternalEObject)projectProxy).eSetProxyURI(EcoreUtil.getURI(project));
              projectProxy.setName(project.getName());
              projectProxy.setLabel(project.getLabel());
              projects.add(project);
            }
          }
        }
      }
    }

    return projects;
  }

  private void init()
  {
    try
    {
      IRunnableWithProgress runnable = new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          try
          {
            monitor.beginTask("Loading "
                + resourceSet.getURIConverter().normalize(EMFUtil.CONFIGURATION_URI).trimFragment(),
                IProgressMonitor.UNKNOWN);

            Resource configurationResource = loadResourceSafely(EMFUtil.CONFIGURATION_URI);

            EList<EObject> contents = configurationResource.getContents();
            if (contents.isEmpty())
            {
              final Display display = viewer.getControl().getDisplay();
              display.syncExec(new Runnable()
              {
                public void run()
                {
                  boolean confirmation = MessageDialog.openQuestion(null, "Configuration Load Failure",
                      "The configuration could not be loaded so it's likely you're not connected to the internet or are behind a firewall."
                          + "The following URI is inaccessable:\n" + "  " + EMFUtil.CONFIGURATION_URI + "\n\n"
                          + "Do you wish to configure your network connections?");
                  if (confirmation)
                  {
                    display.asyncExec(new Runnable()
                    {
                      public void run()
                      {
                        if (!resourceSet.getURIConverter().exists(Preferences.PREFERENCES_URI, null))
                        {
                          Resource resource = resourceSet.createResource(Preferences.PREFERENCES_URI);
                          preferences = SetupFactory.eINSTANCE.createPreferences();
                          resource.getContents().add(preferences);
                          saveEObject(preferences);
                        }

                        close();
                        setReturnCode(RETURN_WORKBENCH_NETWORK_PREFERENCES);
                      }
                    });
                  }
                }
              });

              throw new InterruptedException("Cannot load configuration");
            }

            configuration = (Configuration)contents.get(0);

            InternalEList<Project> configuredProjects = (InternalEList<Project>)configuration.getProjects();
            if (considerVisibleProjects)
            {
              configuredProjects.addAllUnique(0, getVisibleProjects());
            }

            String installFolder;
            String bundlePoolFolder;
            String bundlePoolTPFolder;

            if (resourceSet.getURIConverter().exists(Preferences.PREFERENCES_URI, null))
            {
              Resource resource = loadResourceSafely(Preferences.PREFERENCES_URI);
              preferences = (Preferences)resource.getContents().get(0);

              installFolder = safe(preferences.getInstallFolder());
              bundlePoolFolder = safe(preferences.getBundlePoolFolder());
              bundlePoolTPFolder = safe(preferences.getBundlePoolFolderTP());
            }
            else
            {
              Resource resource = resourceSet.createResource(Preferences.PREFERENCES_URI);
              preferences = SetupFactory.eINSTANCE.createPreferences();
              resource.getContents().add(preferences);
              saveEObject(preferences);

              File rootFolder = new File(System.getProperty("user.home", "."));

              installFolder = safe(getAbsolutePath(rootFolder));
              bundlePoolFolder = safe(getAbsolutePath(new File(installFolder, ".p2pool-ide")));
              bundlePoolTPFolder = safe(getAbsolutePath(new File(installFolder, ".p2pool-tp")));
            }

            ItemProvider input = new ItemProvider(EMFUtil.ADAPTER_FACTORY);
            EList<Object> projects = input.getChildren();

            for (int i = 0; i < configuredProjects.size(); i++)
            {
              Project project = configuredProjects.basicGet(i);
              if (project.eIsProxy())
              {
                URI uri = ((InternalEObject)project).eProxyURI().trimFragment();
                if (uri.equals(EMFUtil.EXAMPLE_PROXY_URI))
                {
                  URI redirectedURI = resourceSet.getURIConverter().normalize(uri);
                  if (redirectedURI.equals(EMFUtil.EXAMPLE_PROXY_URI))
                  {
                    continue;
                  }

                  // Resolve the project if it's a replacement for the example project.
                  project = configuredProjects.get(i);
                  Resource resource = project.eResource();
                  if (resource != null && resource.getURI().equals(EMFUtil.EXAMPLE_PROXY_URI))
                  {
                    resource.setURI(redirectedURI);
                  }
                }
              }

              projects.add(project);
            }

            initUI(input, installFolder, bundlePoolFolder, bundlePoolTPFolder);
          }
          catch (UpdatingException ex)
          {
            // Ignore
          }
          finally
          {
            monitor.done();
          }
        }

        private void initUI(final ItemProvider input, final String installFolder, final String bundlePoolFolder,
            final String bundlePoolTPFolder)
        {
          viewer.getControl().getDisplay().asyncExec(new Runnable()
          {
            public void run()
            {
              installFolderText.setText(installFolder);

              if (bundlePoolFolder.length() != 0)
              {
                bundlePoolText.setText(bundlePoolFolder);
              }

              if (bundlePoolTPFolder.length() != 0)
              {
                bundlePoolTPText.setText(bundlePoolTPFolder);
              }

              viewer.setInput(input);
              cellEditor.setInput(this);

              final Tree tree = viewer.getTree();
              final TreeColumn projectColumn = tree.getColumn(0);
              final TreeColumn eclipseColumn = tree.getColumn(1);

              final ControlAdapter columnResizer = new ControlAdapter()
              {
                @Override
                public void controlResized(ControlEvent e)
                {
                  Point size = tree.getSize();
                  ScrollBar bar = tree.getVerticalBar();
                  if (bar != null && bar.isVisible())
                  {
                    size.x -= bar.getSize().x;
                  }

                  projectColumn.setWidth(size.x - eclipseColumn.getWidth());
                }
              };

              eclipseColumn.pack();
              eclipseColumn.setWidth(eclipseColumn.getWidth() + 10);

              tree.addControlListener(columnResizer);
              tree.getDisplay().asyncExec(new Runnable()
              {
                public void run()
                {
                  columnResizer.controlResized(null);
                }
              });
            }
          });
        }
      };

      runInProgressDialog(runnable);
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

  private Setup getSetup(Branch branch)
  {
    if (setups == null)
    {
      setups = new HashMap<Branch, Setup>();
    }

    Setup setup = setups.get(branch);
    if (setup == null)
    {
      URI uri = getSetupURI(branch);
      if (resourceSet.getURIConverter().exists(uri, null))
      {
        try
        {
          Resource resource = loadResourceSafely(uri);
          setup = (Setup)resource.getContents().get(0);
        }
        catch (UpdatingException ex)
        {
          return null;
        }
      }
      else
      {
        setup = SetupFactory.eINSTANCE.createSetup();
        setup.setEclipseVersion(getDefaultEclipseVersion(branch));
        setup.setBranch(branch);
        setup.setPreferences(preferences);

        Resource resource = resourceSet.createResource(uri);
        resource.getContents().add(setup);
      }

      setups.put(branch, setup);
    }

    return setup;
  }

  private EList<Eclipse> getAllowedEclipseVersions(Branch branch)
  {
    Set<Eclipse> restrictions = new HashSet<Eclipse>();
    restrictions.addAll(branch.getRestrictions());
    restrictions.addAll(branch.getProject().getRestrictions());

    EList<Eclipse> eclipses = new BasicEList<Eclipse>(configuration.getEclipseVersions());
    if (!restrictions.isEmpty())
    {
      eclipses.retainAll(restrictions);
    }

    return eclipses;
  }

  private Eclipse getDefaultEclipseVersion(Branch branch)
  {
    EList<Eclipse> eclipses = getAllowedEclipseVersions(branch);
    return eclipses.get(eclipses.size() - 1);
  }

  private void validate()
  {
    String text = installFolderText.getText();

    {
      String defaultFolder = text + File.separator + ".p2pool-ide";
      bundlePoolText.setFont(bundlePoolText.getText().equals(defaultFolder) ? ExtendedFontRegistry.INSTANCE.getFont(
          installFolderText.getFont(), IItemFontProvider.ITALIC_FONT) : installFolderText.getFont());
    }

    {
      String defaultFolder = text + File.separator + ".p2pool-tp";
      bundlePoolTPText.setFont(bundlePoolTPText.getText().equals(defaultFolder) ? ExtendedFontRegistry.INSTANCE
          .getFont(installFolderText.getFont(), IItemFontProvider.ITALIC_FONT) : installFolderText.getFont());
    }

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

    if (installFolderText.getText().length() == 0)
    {
      setMessage("Enter the install folder.", IMessageProvider.ERROR);
      installButton.setEnabled(false);
      return;
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
    return AbstractSetupTaskContext.getSetupURI(branchFolder);
  }

  private boolean isInstalled(Object object)
  {
    if (object instanceof Branch)
    {
      Branch branch = (Branch)object;
      Setup setup = getSetup(branch);
      if (setup != null)
      {
        URI uri = setup.eResource().getURI();
        return resourceSet.getURIConverter().exists(uri, null);
      }
    }

    return false;
  }

  private void install() throws Exception
  {
    final Object[] checkedElements = viewer.getCheckedElements();
    final String installFolder = installFolderText.getText();

    File folder = new File(installFolder);
    folder.mkdirs();

    final List<SetupTaskPerformer> setupTaskPerformers = new ArrayList<SetupTaskPerformer>();
    for (Object checkedElement : checkedElements)
    {
      if (checkedElement instanceof Branch)
      {
        Branch branch = (Branch)checkedElement;
        Setup setup = getSetup(branch);
        if (setup != null)
        {
          SetupTaskPerformer taskPerformer = createTaskPerformer(setup, installFolder);
          setupTaskPerformers.add(taskPerformer);
        }
      }
    }

    ProgressDialog.run(getShell(), new ProgressLogRunnable()
    {
      public Set<String> run(ProgressLog log) throws Exception
      {
        for (SetupTaskPerformer setupTaskPerformer : setupTaskPerformers)
        {
          install(setupTaskPerformer);

          Preferences newPreferences = setupTaskPerformer.getPreferences();
          Resource eResource = preferences.eResource();
          eResource.getContents().set(0, newPreferences);
          eResource.save(null);
        }

        return null;
      }
    }, setupTaskPerformers);
  }

  private SetupTaskPerformer createTaskPerformer(Setup setup, String installFolder)
  {
    saveEObject(setup);

    Branch branch = setup.getBranch();
    Project project = branch.getProject();

    String branchFolder = branch.getName().toLowerCase();
    String projectFolder = project.getName().toLowerCase();
    File branchDir = new File(installFolder, projectFolder + "/" + branchFolder);

    return new SetupTaskPerformer(branchDir);
  }

  private void install(SetupTaskPerformer performer) throws Exception
  {
    performer.getWorkspaceDir().mkdirs();
    performer.perform();

    launchIDE(performer);
  }

  private void launchIDE(SetupTaskPerformer performer) throws Exception
  {
    OS os = performer.getOS();
    if (os.isCurrent())
    {
      performer.log("Launching IDE...");

      String eclipseDir = os.getEclipseDir();
      String eclipseExecutable = os.getEclipseExecutable();

      String eclipsePath = new File(performer.getBranchDir(), eclipseDir + "/" + eclipseExecutable).getAbsolutePath();
      File ws = new File(performer.getBranchDir(), "ws");

      ProcessBuilder builder = new ProcessBuilder(eclipsePath);
      builder.directory(ws);
      builder.start();
    }
    else
    {
      performer.log("Launching IDE is not possible for cross-platform installs. Skipping.");
    }
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

  private void runInProgressDialog(IRunnableWithProgress runnable) throws InvocationTargetException,
      InterruptedException
  {
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(viewer.getControl().getShell())
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

  private void handleException(Throwable ex)
  {
    Activator.log(ex);
    ErrorDialog.open(ex);
  }

  /**
   * @author Eike Stepper
   */
  public enum StartType
  {
    APPLICATION, RESTART, EDITOR
  }

  /**
   * @author Eike Stepper
   */
  private enum UpdateSearchState
  {
    SEARCHING, FOUND, DONE
  }

  /**
   * @author Eike Stepper
   */
  private static final class UpdatingException extends Exception
  {
    private static final long serialVersionUID = 1L;
  }

  /**
   * @author Eike Stepper
   */
  private final class DialogLabelDecorator extends LabelProvider implements ILabelDecorator
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
  private final class DialogLabelProvider extends AdapterFactoryLabelProvider.ColorProvider
  {
    private DialogLabelProvider(AdapterFactory adapterFactory, Viewer viewer)
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

  /**
   * @author Eike Stepper
   */
  private final class AboutDialog extends AbstractSetupDialog
  {
    private final String version;

    private AboutDialog(String version)
    {
      super(InstallerDialog.this.getShell(), "About Development Evironment Installer", 700, 500, Activator.getDefault()
          .getBundle());
      this.version = version;
    }

    @Override
    protected String getDefaultMessage()
    {
      return "The current product version is " + version + ".\n" + EMFUtil.CONFIGURATION_URI;
    }

    @Override
    protected void createUI(Composite parent)
    {
      final Table table = new Table(parent, SWT.FULL_SELECTION | SWT.NO_SCROLL | SWT.V_SCROLL);
      table.setHeaderVisible(true);
      table.setLinesVisible(true);
      table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      final TableColumn idColumn = new TableColumn(table, SWT.NONE);
      idColumn.setText("Installed Unit");
      idColumn.setResizable(false);
      idColumn.setMoveable(false);

      final TableColumn versionColumn = new TableColumn(table, SWT.NONE);
      versionColumn.setText("Version");
      versionColumn.setResizable(false);
      versionColumn.setMoveable(false);

      IProvisioningAgent agent = ServiceUtil.getService(IProvisioningAgent.class);

      try
      {
        ProvisioningSession session = new ProvisioningSession(agent);
        List<IInstallableUnit> installedUnits = getInstalledUnits(session).getElement2();

        String[][] rows = new String[installedUnits.size()][];
        for (int i = 0; i < rows.length; i++)
        {
          IInstallableUnit installableUnit = installedUnits.get(i);
          rows[i] = new String[] { installableUnit.getId(), installableUnit.getVersion().toString() };
        }

        Arrays.sort(rows, new Comparator<String[]>()
        {
          public int compare(String[] o1, String[] o2)
          {
            return o1[0].compareTo(o2[0]);
          }
        });

        Color blue = getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE);

        for (int i = 0; i < rows.length; i++)
        {
          TableItem item = new TableItem(table, SWT.NONE);

          String id = rows[i][0];
          item.setText(0, id);

          String version = rows[i][1];
          item.setText(1, version);

          if (hasPrefix(id, PRODUCT_PREFIXES))
          {
            item.setForeground(blue);
          }
        }

        final ControlAdapter columnResizer = new ControlAdapter()
        {
          @Override
          public void controlResized(ControlEvent e)
          {
            Point size = table.getSize();
            ScrollBar bar = table.getVerticalBar();
            if (bar != null && bar.isVisible())
            {
              size.x -= bar.getSize().x;
            }

            idColumn.setWidth(size.x - versionColumn.getWidth());
          }
        };

        versionColumn.pack();
        versionColumn.setWidth(versionColumn.getWidth() + 10);

        table.addControlListener(columnResizer);
        table.getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            columnResizer.controlResized(null);
          }
        });
      }
      finally
      {
        ServiceUtil.ungetService(agent);
      }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
      createButton(parent, IDialogConstants.OK_ID, "Close", true);
    }
  }
}
