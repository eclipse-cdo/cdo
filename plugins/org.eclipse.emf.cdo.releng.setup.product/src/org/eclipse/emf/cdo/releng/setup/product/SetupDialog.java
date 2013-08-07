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
package org.eclipse.emf.cdo.releng.setup.product;

import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.DirectorCall;
import org.eclipse.emf.cdo.releng.setup.EclipseVersion;
import org.eclipse.emf.cdo.releng.setup.LinkLocation;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.ToolInstallation;
import org.eclipse.emf.cdo.releng.setup.helper.OS;
import org.eclipse.emf.cdo.releng.setup.helper.Progress;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLogRunnable;
import org.eclipse.emf.cdo.releng.setup.provider.BranchItemProvider;
import org.eclipse.emf.cdo.releng.setup.provider.ConfigurationItemProvider;
import org.eclipse.emf.cdo.releng.setup.provider.ProjectItemProvider;
import org.eclipse.emf.cdo.releng.setup.provider.SetupItemProviderAdapterFactory;
import org.eclipse.emf.cdo.releng.setup.ui.ProgressLogDialog;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class SetupDialog extends TitleAreaDialog
{
  private static final String SETUP_URI = System.getProperty("setup.uri",
      "http://git.eclipse.org/c/cdo/cdo.git/plain/plugins/org.eclipse.emf.cdo.releng.setup/model/Configuration.xmi");

  private static final String EGIT_URI = System.getProperty("egit.uri", "http://download.eclipse.org/releases/kepler");

  private static final String BUCKY_URI = System.getProperty("bucky.uri",
      "http://download.eclipse.org/tools/buckminster/updates-4.3");

  private static final String RELENG_URI = System.getProperty("releng.uri",
      "http://download.eclipse.org/modeling/emf/cdo/updates/integration");

  private HashMap<Branch, Setup> setups;

  private AdapterFactory adapterFactory;

  private ResourceSet resourceSet;

  private Preferences preferences;

  private Configuration configuration;

  private CheckboxTreeViewer viewer;

  private Text userNameText;

  private Text installFolderText;

  private Text gitPrefixText;

  /**
   * Create the dialog.
   * @param parentShell
   */
  public SetupDialog(Shell parentShell)
  {
    super(parentShell);
    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);

    resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
    resourceSet.getLoadOptions().put(XMIResource.OPTION_RECORD_UNKNOWN_FEATURE, true);

    adapterFactory = new SetupDialogAdapterFactory();
  }

  @Override
  public boolean close()
  {
    savePreferences();
    return super.close();
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
    setTitleImage(ResourceManager.getPluginImage("org.eclipse.emf.cdo.releng.setup.ui", "icons/install_wiz.gif"));

    URI configurationURI = URI.createURI(SETUP_URI);
    Resource resource = resourceSet.getResource(configurationURI, true);
    configuration = (Configuration)resource.getContents().get(0);

    Composite area = (Composite)super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    GridLayout gl_container = new GridLayout(1, false);
    gl_container.marginWidth = 0;
    gl_container.marginHeight = 0;
    container.setLayout(gl_container);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    final String ECLIPSE_VERSION_COLUMN = "eclipseVersion";

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
            setup.setEclipseVersion((EclipseVersion)value);
            viewer.update(modelElement, new String[] { property });
          }
        }
      }
    });
    viewer.setColumnProperties(new String[] { "project", ECLIPSE_VERSION_COLUMN });

    ComboBoxViewerCellEditor cellEditor = new ComboBoxViewerCellEditor(viewer.getTree());
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
        EList<EclipseVersion> eclipseVersions = configuration.getEclipseVersions();
        return eclipseVersions.toArray(new EclipseVersion[eclipseVersions.size()]);
      }
    });

    viewer.setCellEditors(new CellEditor[] { null, cellEditor });
    cellEditor.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
    cellEditor.setInput(this);

    viewer.addCheckStateListener(new ICheckStateListener()
    {
      public void checkStateChanged(CheckStateChangedEvent event)
      {
        boolean checked = event.getChecked();
        Object element = event.getElement();
        if (element instanceof Project)
        {
          Project project = (Project)element;

          for (Branch branch : project.getBranches())
          {
            viewer.setChecked(branch, checked);
          }
        }
        else if (element instanceof Branch && !checked)
        {
          Branch branch = (Branch)element;
          viewer.setChecked(branch.getProject(), false);
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
              if (setup == null)
              {
                setup = SetupFactory.eINSTANCE.createSetup();
                setup.setBranch(branch);
                setup.setEclipseVersion(getDefaultEclipseVersion());

                Resource resource = resourceSet.createResource(getSetupURI(branch));
                resource.getContents().add(setup);
              }

              EclipseVersion eclipseVersion = setup.getEclipseVersion();
              return labelProvider.getText(eclipseVersion);
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
    grpPreferences.setText("Preferences");
    grpPreferences.setBounds(0, 0, 70, 82);

    Label userNameLabel = new Label(grpPreferences, SWT.NONE);
    userNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    userNameLabel.setBounds(0, 0, 55, 15);
    userNameLabel.setText("User Name:");

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
        validate();
      }
    });

    Label empty = new Label(grpPreferences, SWT.NONE);
    empty.setBounds(0, 0, 55, 15);

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
        validate();
      }
    });

    Button gitPrefixButton = new Button(grpPreferences, SWT.NONE);
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

    init();

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

  /**
   * Return the initial size of the dialog.
   */
  @Override
  protected Point getInitialSize()
  {
    return new Point(500, 550);
  }

  @Override
  protected void okPressed()
  {
    try
    {
      install();
    }
    catch (Exception ex)
    {
      Activator.log(ex);
      MessageDialog.openError(getShell(), "Error", ex.getMessage());
    }

    super.okPressed();
  }

  private void init()
  {
    if (resourceSet.getURIConverter().exists(Preferences.PREFERENCES_URI, null))
    {
      Resource resource = resourceSet.getResource(Preferences.PREFERENCES_URI, true);
      preferences = (Preferences)resource.getContents().get(0);

      userNameText.setText(safe(preferences.getUserName()));
      installFolderText.setText(safe(preferences.getInstallFolder()));
      gitPrefixText.setText(safe(preferences.getGitPrefix()));
    }
    else
    {
      Resource resource = resourceSet.createResource(Preferences.PREFERENCES_URI);
      preferences = SetupFactory.eINSTANCE.createPreferences();
      resource.getContents().add(preferences);

      File rootFolder = new File(System.getProperty("user.home", "."));

      userNameText.setText(safe(System.getProperty("user.name", "<username>")).toLowerCase());
      installFolderText.setText(safe(getAbsolutePath(rootFolder)));
      gitPrefixText.setText(safe(getAbsolutePath(new File(OS.INSTANCE.getGitPrefix()))));
    }

    initWorkspaces();
    viewer.setInput(configuration);
    viewer.expandAll();
  }

  private void initWorkspaces()
  {
    setups = new HashMap<Branch, Setup>();
    for (Project project : configuration.getProjects())
    {
      for (Branch branch : project.getBranches())
      {
        Setup setup;
        URI uri = getSetupURI(branch);
        if (resourceSet.getURIConverter().exists(uri, null))
        {
          Resource resource = resourceSet.getResource(uri, true);
          setup = (Setup)resource.getContents().get(0);
        }
        else
        {
          setup = SetupFactory.eINSTANCE.createSetup();
          setup.setBranch(branch);
          setup.setEclipseVersion(getDefaultEclipseVersion());

          Resource resource = resourceSet.createResource(uri);
          resource.getContents().add(setup);
        }

        setups.put(branch, setup);
      }
    }
  }

  private EclipseVersion getDefaultEclipseVersion()
  {
    EList<EclipseVersion> eclipseVersions = configuration.getEclipseVersions();
    return eclipseVersions.get(eclipseVersions.size() - 1);
  }

  private void validate()
  {
    Button installButton = getButton(IDialogConstants.OK_ID);
    if (installButton == null)
    {
      return;
    }

    if (viewer.getCheckedElements().length == 0)
    {
      setErrorMessage("Select one or more project branches to install.");
      installButton.setEnabled(false);
      return;
    }

    if (userNameText.getText().length() == 0)
    {
      setErrorMessage("Enter your user name.");
      installButton.setEnabled(false);
      return;
    }

    if (installFolderText.getText().length() == 0)
    {
      setErrorMessage("Enter the install folder.");
      installButton.setEnabled(false);
      return;
    }

    String gitPrefix = gitPrefixText.getText();
    if (gitPrefix.length() == 0)
    {
      setErrorMessage("Enter the Git prefix folder (which contains 'etc/gitconfig').");
      installButton.setEnabled(false);
      return;
    }

    File etc = new File(gitPrefix, "etc");
    File gitconfig = new File(etc, "gitconfig");
    if (!gitconfig.isFile())
    {
      setErrorMessage("The Git prefix folder does not contain 'etc/gitconfig'.");
      installButton.setEnabled(false);
      return;
    }

    if (Platform.OS_WIN32.equals(Platform.getOS()))
    {
      if (!containsAutoCRLF(gitconfig))
      {
        setErrorMessage("The gitconfig file must contain 'autocrlf = true' on Windows.\nWhen you've added this line modify the Git Prefix field to revalidate...");
        installButton.setEnabled(false);
        return;
      }
    }

    setErrorMessage(null);
    setMessage("Click the Install button to start the installation process.");
    installButton.setEnabled(true);
  }

  private boolean containsAutoCRLF(File file)
  {
    for (String line : OS.INSTANCE.readText(file))
    {
      if (line.contains("autocrlf") && line.contains("true"))
      {
        return true;
      }
    }

    return false;
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
    File installFolder = new File(installFolderText.getText());
    File projectFolder = new File(installFolder, branch.getProject().getName());
    File branchFolder = new File(projectFolder, branch.getName());
    File setupFile = new File(branchFolder, "setup.xmi");
    return URI.createFileURI(setupFile.getAbsolutePath());
  }

  private boolean isInstalled(Object object)
  {
    if (object instanceof Branch)
    {
      Branch branch = (Branch)object;
      return branch.isInstalled(installFolderText.getText());
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

    File logFile = new File(installFolder, "setup.log");
    ProgressLogDialog.run(getShell(), logFile, "Setting up IDE", new ProgressLogRunnable()
    {
      public boolean run(ProgressLog log) throws Exception
      {
        for (Object checkedElement : checkedElements)
        {
          if (checkedElement instanceof Branch)
          {
            Branch branch = (Branch)checkedElement;
            Setup setup = setups.get(branch);
            if (setup != null)
            {
              try
              {
                install(setup, installFolder, gitPrefix);
              }
              catch (IOException ex)
              {
                throw new RuntimeException(ex);
              }
            }
          }
        }

        return false;
      }
    });
  }

  private void install(Setup setup, String installFolder, String gitPrefix) throws Exception
  {
    Branch branch = setup.getBranch();
    Progress.log().addLine("Setting up " + branch.getProject().getName() + " " + branch.getName());

    URI branchURI = branch.getURI(installFolder).trimSegments(1);
    String destination = branchURI.appendSegment("eclipse").toFileString();

    Set<String> updateLocations = new HashSet<String>();
    updateLocations.add(EGIT_URI);
    updateLocations.add(BUCKY_URI);
    updateLocations.add(RELENG_URI);

    String bundlePool = new File(installFolder, ".p2pool-ide").getAbsolutePath();

    install(bundlePool, destination, updateLocations, setup.getEclipseVersion().getDirectorCall());

    Director.from(bundlePool) //
        .feature("org.eclipse.egit") //
        .repository(EGIT_URI) //
        //
        .feature("org.eclipse.buckminster.core.feature") //
        .feature("org.eclipse.buckminster.git.feature") //
        .feature("org.eclipse.buckminster.pde.feature") //
        .repository(BUCKY_URI) //
        //
        .feature("org.eclipse.emf.cdo.releng.setup.ide") //
        .repository(RELENG_URI) //
        //
        .install(destination);

    install(bundlePool, destination, updateLocations, branch.getProject());
    install(bundlePool, destination, updateLocations, branch);
    install(bundlePool, destination, updateLocations, preferences);

    install(preferences.getLinkLocations(), destination);

    File branchFolder = new File(branchURI.toFileString());
    mangleEclipseIni(destination, branchFolder, gitPrefix);

    setup.setPreferences(EcoreUtil.copy(preferences));
    for (String updateLocation : updateLocations)
    {
      P2Repository p2Repository = SetupFactory.eINSTANCE.createP2Repository();
      p2Repository.setUrl(updateLocation);

      setup.getUpdateLocations().add(p2Repository);
    }

    setup.eResource().save(null);

    new File(branchFolder, "ws").mkdirs();
    launchIDE(setup, branchFolder);
  }

  private void install(String bundlePool, String destination, Set<String> updateLocations, ToolInstallation installation)
      throws Exception
  {
    for (DirectorCall directorCall : installation.getDirectorCalls())
    {
      install(bundlePool, destination, updateLocations, directorCall);
    }
  }

  private void install(String bundlePool, String destination, Set<String> updateLocations, DirectorCall directorCall)
  {
    Director.install(bundlePool, directorCall, destination);
    for (P2Repository p2Repository : directorCall.getP2Repositories())
    {
      updateLocations.add(p2Repository.getUrl());
    }
  }

  private void install(EList<LinkLocation> linkLocations, String destination) throws Exception
  {
    if (!linkLocations.isEmpty())
    {
      File links = new File(destination, "links");
      links.mkdirs();

      for (LinkLocation linkLocation : linkLocations)
      {
        File path = new File(linkLocation.getPath()).getCanonicalFile();

        String name = linkLocation.getName();
        if (name == null || name.length() == 0)
        {
          name = path.getName();
        }

        File link = new File(links, name + ".link");

        List<String> lines = Collections.singletonList("path=" + path.toString().replace("\\", "\\\\"));
        OS.INSTANCE.writeText(link, lines);
      }
    }
  }

  private void mangleEclipseIni(String destination, File branchFolder, String gitPrefix)
  {
    File eclipseIni = new File(destination, "eclipse.ini");

    List<String> oldLines = OS.INSTANCE.readText(eclipseIni);
    List<String> newLines = new ArrayList<String>(oldLines);
    mangleEclipseIni(newLines, branchFolder, gitPrefix);

    if (!newLines.equals(oldLines))
    {
      Progress.log().addLine("Adjusting eclipse.ini");
      OS.INSTANCE.writeText(eclipseIni, newLines);
    }
  }

  private void mangleEclipseIni(List<String> lines, File branchFolder, String gitPrefix)
  {
    String maxHeap = Platform.getOSArch().endsWith("_64") ? "-Xmx4g" : "-Xmx1g";

    int xmx = findLine(lines, "-Xmx");
    if (xmx == -1)
    {
      lines.add(maxHeap);
    }
    else
    {
      lines.set(xmx, maxHeap);
    }

    int maxperm = findLine(lines, "--launcher.XXMaxPermSize");
    if (maxperm == -1)
    {
      maxperm = findLine(lines, "-vmargs");
      lines.add(maxperm, "--launcher.XXMaxPermSize");
      lines.add(maxperm + 1, "512m");
    }
    else
    {
      lines.set(maxperm + 1, "512m");
    }

    String ws = new File(branchFolder, "ws").getAbsolutePath();

    int data = findLine(lines, "-data");
    if (data == -1)
    {
      data = findLine(lines, "-vmargs");
      lines.add(data, "-data");
      lines.add(data + 1, ws);
    }
    else
    {
      lines.set(data + 1, ws);
    }

    if (gitPrefix.length() != 0)
    {
      gitPrefix = "-Djgit.gitprefix=" + gitPrefix;
      if (!lines.contains(gitPrefix))
      {
        lines.add(gitPrefix);
      }
    }

    String setupIDE = "-Dorg.eclipse.emf.cdo.releng.setup.ide=true";
    if (!lines.contains(setupIDE))
    {
      lines.add(setupIDE);
    }

    File projectFolder = branchFolder.getParentFile();
    String poolPath = new File(projectFolder.getParentFile(), ".p2pool-tp").getAbsolutePath();
    String bundlePool = "-Dorg.eclipse.buckminster.core.bundle.pool=" + poolPath;
    if (!lines.contains(bundlePool))
    {
      lines.add(bundlePool);
    }
  }

  private int findLine(List<String> lines, String search)
  {
    int index = 0;
    for (String line : lines)
    {
      if (line.contains(search))
      {
        return index;
      }

      ++index;
    }

    return -1;
  }

  private void launchIDE(Setup setup, File branchFolder) throws IOException
  {
    Progress.log().addLine("Launching IDE");

    String eclipseExecutable = OS.INSTANCE.getEclipseExecutable();
    String eclipsePath = new File(branchFolder, "eclipse/" + eclipseExecutable).getAbsolutePath();
    File ws = new File(branchFolder, "ws");

    ProcessBuilder builder = new ProcessBuilder(eclipsePath);
    builder.directory(ws);
    builder.start();
  }

  private void savePreferences()
  {
    preferences.setUserName(userNameText.getText());
    preferences.setInstallFolder(installFolderText.getText());
    preferences.setGitPrefix(gitPrefixText.getText());

    try
    {
      XMLResource xmlResource = (XMLResource)preferences.eResource();
      xmlResource.getEObjectToExtensionMap().clear();
      xmlResource.save(null);
    }
    catch (IOException ex)
    {
      Activator.log(ex);
    }
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
        return getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY);
      }

      return super.getBackground(object);
    }
  }
}
