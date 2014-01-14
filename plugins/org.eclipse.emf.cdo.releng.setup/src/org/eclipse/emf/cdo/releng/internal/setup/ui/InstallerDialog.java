/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.releng.internal.setup.ui.PropertyField.FileField;
import org.eclipse.emf.cdo.releng.internal.setup.ui.PropertyField.ValueListener;
import org.eclipse.emf.cdo.releng.internal.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.internal.setup.util.UpdateUtil;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.log.ProgressLogRunnable;
import org.eclipse.emf.cdo.releng.setup.util.OS;
import org.eclipse.emf.cdo.releng.setup.util.ServiceUtil;
import org.eclipse.emf.cdo.releng.setup.util.SetupResource;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

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
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class InstallerDialog extends AbstractSetupDialog
{
  public static final String TRAIN_URL = "http://download.eclipse.org/releases/luna";

  public static final int RETURN_WORKBENCH_NETWORK_PREFERENCES = -2;

  public static final int RETURN_WORKBENCH = -3;

  public static final int RETURN_RESTART = -4;

  private static final int ECLIPSE_VERSION_COLUMN_INDEX = 1;

  private static final String ECLIPSE_VERSION_COLUMN_NAME = "eclipse";

  private static final Object[] NO_ELEMENTS = new Object[0];

  private final StartType startType;

  private final boolean considerVisibleProjects;

  private Map<Branch, Setup> setups;

  private ResourceSet resourceSet;

  private Preferences preferences;

  private Configuration configuration;

  private CheckboxTreeViewer viewer;

  private ComboBoxViewerCellEditor cellEditor;

  private FileField installFolderField;

  private FileField bundlePoolField;

  private FileField bundlePoolTPField;

  private ToolItem updateToolItem;

  private UpdateSearchState updateSearchState;

  private Link versionLink;

  private ComposedAdapterFactory adapterFactory = EMFUtil.createAdapterFactory();

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
  protected Control createDialogArea(Composite parent)
  {
    try
    {
      return super.createDialogArea(parent);
    }
    catch (final Throwable ex)
    {
      Activator.log(ex);
      parent.getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          ErrorDialog.open(ex);
        }
      });

      return null;
    }
  }

  @Override
  protected void createUI(Composite parent)
  {
    viewer = new CheckboxTreeViewer(parent, SWT.FULL_SELECTION | SWT.NO_SCROLL | SWT.V_SCROLL);

    final Tree tree = viewer.getTree();
    tree.setLinesVisible(true);
    tree.setHeaderVisible(true);
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    final AdapterFactoryContentProvider contentProvider = new AdapterFactoryContentProvider(adapterFactory)
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
                  catch (UpdateUtil.UpdatingException ex)
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

    DialogLabelProvider labelProvider = new DialogLabelProvider(adapterFactory, viewer);
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

        if (element instanceof Branch && ECLIPSE_VERSION_COLUMN_NAME.equals(property))
        {
          Branch branch = (Branch)element;
          return getSetup(branch);
        }

        return null;
      }
    });
    viewer.setColumnProperties(new String[] { "project", ECLIPSE_VERSION_COLUMN_NAME });

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
    cellEditor.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));

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

                    updateEnablements();
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

            updateEnablements();
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

                updateEnablements();
                viewer.editElement(branch, ECLIPSE_VERSION_COLUMN_INDEX);
              }
            });
          }
          else
          {
            Branch branch = (Branch)element;
            viewer.setChecked(branch.getProject(), false);
            updateEnablements();
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

    GridLayout layout = new GridLayout(4, false);
    layout.marginWidth = 10;
    layout.marginHeight = 10;

    Composite group = new Composite(parent, SWT.NONE);
    group.setLayout(layout);
    group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

    installFolderField = new PropertyField.FileField("Install Folder");
    installFolderField.setToolTip("Points to the folder where the setup tool will create the project folders.");
    installFolderField.setDialogText("Select Install Folder");
    installFolderField.setDialogMessage("Select an install folder.");
    installFolderField.fill(group);
    installFolderField.addValueListener(new ValueListener()
    {
      public void valueChanged(String oldValue, String newValue) throws Exception
      {
        preferences.setInstallFolder(newValue);
        EMFUtil.saveEObject(preferences);
        validate();
      }
    });

    bundlePoolField = new PropertyField.FileField("Bundle Pool")
    {
      @Override
      protected String computeLinkedValue(String thisValue, String linkValue)
      {
        return new File(linkValue, ".p2pool-ide").getAbsolutePath();
      }
    };
    bundlePoolField.setToolTip("Points to the folder where the setup tool will create the p2 bundle pool for IDEs.");
    bundlePoolField.setDialogText("Select Bundle Pool Folder");
    bundlePoolField.setDialogMessage("Select a p2 bundle pool folder for IDEs.");
    bundlePoolField.setLinkField(installFolderField);
    bundlePoolField.fill(group);
    bundlePoolField.setEnabled(false);
    bundlePoolField.addValueListener(new ValueListener()
    {
      public void valueChanged(String oldValue, String newValue) throws Exception
      {
        preferences.setBundlePoolFolder(newValue);
        EMFUtil.saveEObject(preferences);
        validate();
      }
    });

    bundlePoolTPField = new PropertyField.FileField("TP Bundle Pool")
    {
      @Override
      protected String computeLinkedValue(String thisValue, String linkValue)
      {
        return new File(linkValue, ".p2pool-tp").getAbsolutePath();
      }
    };
    bundlePoolTPField
        .setToolTip("Points to the folder where the setup tool will create the p2 bundle pool for target platforms.");
    bundlePoolTPField.setDialogText("Select TP Bundle Pool Folder");
    bundlePoolTPField.setDialogMessage("Select a p2 bundle pool folder for target platforms.");
    bundlePoolTPField.setLinkField(installFolderField);
    bundlePoolTPField.fill(group);
    bundlePoolTPField.setEnabled(false);
    bundlePoolTPField.addValueListener(new ValueListener()
    {
      public void valueChanged(String oldValue, String newValue) throws Exception
      {
        preferences.setBundlePoolFolderTP(newValue);
        EMFUtil.saveEObject(preferences);
        validate();
      }
    });

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
      UIUtil.handleException(ex);
    }

    super.okPressed();
  }

  protected boolean update(final boolean needsEarlyConfirmation)
  {
    Runnable postInstall = new Runnable()
    {
      public void run()
      {
        setUpdateIcon(0);
      }
    };

    Runnable restartHandler = new Runnable()
    {
      public void run()
      {
        close();
        setReturnCode(RETURN_RESTART);
      }
    };

    return UpdateUtil
        .update(InstallerDialog.this.getShell(), needsEarlyConfirmation, true, postInstall, restartHandler);
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
            IStatus status = UpdateUtil.checkForUpdates(agent, true, null, SubMonitor.convert(null));
            if (status == UpdateUtil.UPDATE_FOUND_STATUS)
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
        catch (Exception ex)
        {
          // Likely due to early exit. Ignore
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

  private String getProductVersion()
  {
    IProvisioningAgent agent = ServiceUtil.getService(IProvisioningAgent.class);

    try
    {
      ProvisioningSession session = new ProvisioningSession(agent);
      Pair<String, List<IInstallableUnit>> profileAndIUs = UpdateUtil.getInstalledUnits(session, UpdateUtil.PRODUCT_ID);
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

  private SetupResource loadResourceSafely(URI uri) throws UpdateUtil.UpdatingException
  {
    SetupResource resource = EMFUtil.loadResourceSafely(resourceSet, uri);
    if (resource.getToolVersion() > SetupTaskMigrator.TOOL_VERSION)
    {
      if (update(true))
      {
        throw new UpdateUtil.UpdatingException();
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
    directories.add(new File(SetupConstants.USER_HOME).getAbsoluteFile());

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
      final Shell shell = viewer.getControl().getShell();
      final Display display = shell.getDisplay();

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
              display.syncExec(new Runnable()
              {
                public void run()
                {
                  boolean confirmation = MessageDialog.openQuestion(null, SHELL_TEXT,
                      "The configuration could not be loaded so it's likely you're not connected to the internet or are behind a firewall."
                          + "The following URI is inaccessable:\n" + "  " + EMFUtil.CONFIGURATION_URI + "\n\n"
                          + "Do you wish to configure your network connections?");
                  if (confirmation)
                  {
                    display.asyncExec(new Runnable()
                    {
                      public void run()
                      {
                        if (!resourceSet.getURIConverter().exists(SetupConstants.PREFERENCES_URI, null))
                        {
                          Resource resource = resourceSet.createResource(SetupConstants.PREFERENCES_URI);
                          preferences = SetupFactory.eINSTANCE.createPreferences();
                          resource.getContents().add(preferences);
                          EMFUtil.saveEObject(preferences);
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

            if (resourceSet.getURIConverter().exists(SetupConstants.PREFERENCES_URI, null))
            {
              Resource resource = loadResourceSafely(SetupConstants.PREFERENCES_URI);
              preferences = (Preferences)resource.getContents().get(0);

              installFolder = safe(preferences.getInstallFolder());
              bundlePoolFolder = safe(preferences.getBundlePoolFolder());
              bundlePoolTPFolder = safe(preferences.getBundlePoolFolderTP());
            }
            else
            {
              Resource resource = resourceSet.createResource(SetupConstants.PREFERENCES_URI);
              preferences = SetupFactory.eINSTANCE.createPreferences();
              resource.getContents().add(preferences);
              EMFUtil.saveEObject(preferences);

              File rootFolder = new File(SetupConstants.USER_HOME);

              installFolder = safe(getAbsolutePath(rootFolder));
              bundlePoolFolder = safe(getAbsolutePath(new File(installFolder, ".p2pool-ide")));
              bundlePoolTPFolder = safe(getAbsolutePath(new File(installFolder, ".p2pool-tp")));
            }

            ItemProvider input = new ItemProvider(adapterFactory);
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
          catch (UpdateUtil.UpdatingException ex)
          {
            // Ignore
          }
          finally
          {
            monitor.done();
          }
        }
      };

      UIUtil.runInProgressDialog(shell, runnable);
    }
    catch (InterruptedException ex)
    {
      // Do nothing
    }
    catch (InvocationTargetException ex)
    {
      UIUtil.handleException(ex.getCause());
    }
    catch (Throwable ex)
    {
      UIUtil.handleException(ex);
    }
  }

  private void initUI(final ItemProvider input, final String installFolder, final String bundlePoolFolder,
      final String bundlePoolTPFolder)
  {
    viewer.getControl().getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        installFolderField.setValue(installFolder);

        if (bundlePoolFolder.length() != 0)
        {
          bundlePoolField.setValue(bundlePoolFolder);
        }

        bundlePoolField.setLinkedFromValue();

        if (bundlePoolTPFolder.length() != 0)
        {
          bundlePoolTPField.setValue(bundlePoolTPFolder);
        }

        bundlePoolTPField.setLinkedFromValue();

        final Tree tree = viewer.getTree();
        final TreeColumn projectColumn = tree.getColumn(0);
        final TreeColumn eclipseColumn = tree.getColumn(ECLIPSE_VERSION_COLUMN_INDEX);

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

            eclipseColumn.pack();
            eclipseColumn.setWidth(eclipseColumn.getWidth() + 10);

            projectColumn.setWidth(size.x - eclipseColumn.getWidth());
          }
        };

        tree.addControlListener(columnResizer);
        tree.getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            columnResizer.controlResized(null);
          }
        });

        viewer.setInput(input);
        cellEditor.setInput(this);
      }
    });
  }

  private void updateEnablements()
  {
    boolean[] neededBundlePools = getNeededBundlePools();

    bundlePoolField.setEnabled(neededBundlePools[0]);
    bundlePoolTPField.setEnabled(neededBundlePools[1]);
  }

  private boolean[] getNeededBundlePools()
  {
    boolean[] result = { false, false };

    for (Object element : viewer.getCheckedElements())
    {
      if (element instanceof Branch)
      {
        Branch branch = (Branch)element;

        Setup setup = getSetup(branch);
        if (setup != null)
        {
          EList<SetupTask> tasks = setup.getSetupTasks(true, null);
          for (SetupTask task : tasks)
          {
            result[0] |= task.needsBundlePool();
            result[1] |= task.needsBundlePoolTP();

            if (result[0] && result[1])
            {
              // Exit early if both pools are needed
              return result;
            }
          }
        }
      }
    }

    return result;
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
        catch (UpdateUtil.UpdatingException ex)
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

    if (installFolderField.getValue().length() == 0)
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
    return getSetupURI(branch, installFolderField.getValue());
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
    final String installFolder = installFolderField.getValue();

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
          setup.eResource().setURI(getSetupURI(branch));
          EMFUtil.saveEObject(setup);

          SetupTaskPerformer performer = new SetupTaskPerformer(Trigger.BOOTSTRAP, installFolder, setup);
          setupTaskPerformers.add(performer);
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
      URI uri = EMFUtil.CONFIGURATION_URI;
      uri = resourceSet.getURIConverter().normalize(uri);
      return "The current product version is " + version + ".\n" + uri;
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
        List<IInstallableUnit> installedUnits = UpdateUtil.getInstalledUnits(session).getElement2();

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

          String version = rows[i][ECLIPSE_VERSION_COLUMN_INDEX];
          item.setText(ECLIPSE_VERSION_COLUMN_INDEX, version);

          if (UpdateUtil.hasPrefix(id))
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
