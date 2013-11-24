/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.presentation.templates;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.ui.AbstractSetupDialog;
import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Component;
import org.eclipse.emf.cdo.releng.setup.ComponentType;
import org.eclipse.emf.cdo.releng.setup.GitCloneTask;
import org.eclipse.emf.cdo.releng.setup.MaterializationTask;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.editor.ProjectTemplate;
import org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupModelWizard;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class AutomaticProjectTemplate extends ProjectTemplate
{
  private static final Pattern GIT_PATTERN = Pattern.compile("\\[remote \"origin\"].*?url *= *([^\\r\\n]*)",
      Pattern.DOTALL | Pattern.MULTILINE);

  private static final Pattern TARGET_PATTERN = Pattern.compile("<repository.*?location.*?=.*?\"([^\"]*)\"",
      Pattern.DOTALL | Pattern.MULTILINE);

  private static final Pattern RMAP_PATTERN = Pattern.compile(
      "<(rm:)?provider.*?readerType[^=]*?=[^\"]*?\"p2\".*?format[^=]*?=[^\"]*?\"([^\"]*)\"", Pattern.DOTALL
          | Pattern.MULTILINE);

  private static final String BRANCH_NAME = "master";

  private Branch branch;

  public AutomaticProjectTemplate()
  {
    super("automatic", "Analyze a folder (such as a Git working tree)");
    branch = addBranch(BRANCH_NAME);
  }

  @Override
  public Control createControl(Composite parent)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.verticalSpacing = 10;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(layout);
    SetupModelWizard.applyGridData(composite);

    new Label(composite, SWT.NONE).setText("Branch name:");

    final Text branchText = new Text(composite, SWT.BORDER);
    SetupModelWizard.applyGridData(branchText);
    branchText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        branch.setName(branchText.getText());
      }
    });

    new Label(composite, SWT.NONE);

    new Label(composite, SWT.NONE).setText("Folder:");

    final Text folderText = new Text(composite, SWT.BORDER);
    SetupModelWizard.applyGridData(folderText);

    Button folderButton = new Button(composite, SWT.NONE);
    folderButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    folderButton.setBounds(0, 0, 75, 25);
    folderButton.setText("Browse...");
    folderButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        Shell shell = folderText.getShell();

        DirectoryDialog dialog = new DirectoryDialog(shell);
        dialog.setText(AbstractSetupDialog.SHELL_TEXT);
        dialog.setMessage("Select a folder to analyze");

        final String folder = dialog.open();
        if (folder != null)
        {
          try
          {
            folderText.setText(folder);

            ProgressMonitorDialog dlg = new ProgressMonitorDialog(shell);
            dlg.run(true, true, new IRunnableWithProgress()
            {
              public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
              {
                monitor.beginTask("Analyzing " + folder, IProgressMonitor.UNKNOWN);

                try
                {
                  analyzeFolder(new File(folder));
                }
                catch (Exception ex)
                {
                  throw new InvocationTargetException(ex);
                }
                finally
                {
                  monitor.done();
                }
              }
            });
          }
          catch (Exception ex)
          {
            Activator.log(ex);
            MessageDialog.openError(shell, AbstractSetupDialog.SHELL_TEXT,
                "An error occured. The Error Log contains more information.");
          }
        }
      }
    });

    branchText.setText(BRANCH_NAME);
    return composite;
  }

  @Override
  public boolean isPageValid()
  {
    return false;
  }

  protected void analyzeFolder(File folder) throws Exception
  {
    EList<SetupTask> tasks = branch.getSetupTasks();
    tasks.clear();

    String location = analyzeGit(folder, tasks);
    analyzeMaterialization(folder, tasks, location);
  }

  private String analyzeGit(File folder, EList<SetupTask> tasks)
  {
    File git = new File(folder, ".git");
    if (git.isDirectory())
    {
      File config = new File(git, "config");
      if (config.isFile())
      {
        String content = IOUtil.readTextFile(config);

        Matcher matcher = GIT_PATTERN.matcher(content);
        if (matcher.find())
        {
          URI baseURI = URI.createURI(matcher.group(1));
          String userID = baseURI.userInfo();

          URI uri = URI.createHierarchicalURI(baseURI.scheme(), baseURI.host(), baseURI.device(), baseURI.segments(),
              baseURI.query(), baseURI.fragment());
          String location = "${setup.branch.dir/git/" + new Path(uri.path()).lastSegment() + "}";

          GitCloneTask task = SetupFactory.eINSTANCE.createGitCloneTask();
          task.setLocation(location);
          task.setUserID(userID);
          task.setRemoteURI(uri.toString());
          task.setRemoteName("origin");
          task.setCheckoutBranch("master");

          tasks.add(task);
          return location;
        }
      }
    }

    return folder.getAbsolutePath();
  }

  private void analyzeMaterialization(File folder, EList<SetupTask> tasks, String location)
      throws ParserConfigurationException
  {
    MaterializationTask task = SetupFactory.eINSTANCE.createMaterializationTask();
    tasks.add(task);

    AutomaticSourceLocator sourceLocator = SetupFactory.eINSTANCE.createAutomaticSourceLocator();
    sourceLocator.setRootFolder(location);
    task.getSourceLocators().add(sourceLocator);

    List<String> componentLocations = new ArrayList<String>();
    for (Pair<String, ComponentType> root : MaterializationTaskImpl.analyzeRoots(folder, componentLocations))
    {
      Component component = SetupFactory.eINSTANCE.createComponent();
      component.setName(root.getElement1());
      component.setType(root.getElement2());
      task.getRootComponents().add(component);
    }

    for (String componentLocation : componentLocations)
    {
      analyzeP2Repositories(task, new File(componentLocation));
    }
  }

  private void analyzeP2Repositories(MaterializationTask task, File folder)
  {
    File[] files = folder.listFiles();
    for (int i = 0; i < files.length; i++)
    {
      File file = files[i];
      if (file.isDirectory())
      {
        analyzeP2Repositories(task, file);
      }
      else if (file.getName().endsWith(".target"))
      {
        analyzeFile(task, file, TARGET_PATTERN, 1);
      }
      else if (file.getName().endsWith(".rmap"))
      {
        analyzeFile(task, file, RMAP_PATTERN, 2);
      }
    }
  }

  private void analyzeFile(MaterializationTask task, File file, Pattern pattern, int group)
  {
    String content = IOUtil.readTextFile(file);

    Matcher matcher = pattern.matcher(content);
    while (matcher.find())
    {
      String url = matcher.group(group);

      P2Repository repository = SetupFactory.eINSTANCE.createP2Repository();
      repository.setURL(url);
      task.getP2Repositories().add(repository);
    }
  }
}
