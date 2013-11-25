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
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.GitCloneTask;
import org.eclipse.emf.cdo.releng.setup.MaterializationTask;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.editor.ProjectTemplate;
import org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupModelWizard;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
  private static final Pattern GIT_URL_PATTERN = Pattern.compile("\\[remote \"origin\"].*?url *= *([^\\r\\n]*)",
      Pattern.DOTALL | Pattern.MULTILINE);

  private static final Pattern TARGET_URL_PATTERN = Pattern.compile("<repository.*?location.*?=.*?\"([^\"]*)\"",
      Pattern.DOTALL | Pattern.MULTILINE);

  private static final Pattern RMAP_URL_PATTERN = Pattern.compile(
      "<(rm:)?provider.*?readerType[^=]*?=[^\"]*?\"p2\".*?format[^=]*?=[^\"]*?\"([^\"]*)\"", Pattern.DOTALL
          | Pattern.MULTILINE);

  // private static final Pattern RMAP_PROPERTY1_PATTERN = Pattern.compile(
  // "<(rm:)?property.*?key[^=]*?=[^\"]*?\"([^\"]*)\".*?value[^=]*?=[^\"]*?\"([^\"]*)\"", Pattern.DOTALL
  // | Pattern.MULTILINE);
  //
  // private static final Pattern RMAP_PROPERTY2_PATTERN = Pattern.compile(
  // "<(rm:)?property.*?value[^=]*?=[^\"]*?\"([^\"]*)\".*?key[^=]*?=[^\"]*?\"([^\"]*)\"", Pattern.DOTALL
  // | Pattern.MULTILINE);
  //
  // private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("{([0-9]+)}");

  private static final String BRANCH_NAME = "master";

  private static final String REMOVE_TEXT = "Remove";

  private static final String ASSOCIATED_ELEMENTS = "associated-elements";

  private static String lastFolder;

  public AutomaticProjectTemplate()
  {
    super("automatic", "Analyze a folder (such as a Git working tree)");
  }

  @Override
  public boolean isValid(Branch branch)
  {
    return super.isValid(branch) && contains(branch.getSetupTasks(), MaterializationTask.class);
  }

  @Override
  public Control createControl(Composite parent, final Container container, final Project project)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.verticalSpacing = 10;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(layout);
    SetupModelWizard.applyGridData(composite);

    final Branch branch = SetupFactory.eINSTANCE.createBranch();
    project.getBranches().add(branch);
    branch.eAdapters().add(new EContentAdapter()
    {
      @Override
      public void notifyChanged(Notification notification)
      {
        super.notifyChanged(notification);
        if (!notification.isTouch())
        {
          final TreeViewer preViewer = container.getPreViewer();
          if (preViewer != null)
          {
            preViewer.getControl().getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                preViewer.setExpandedState(branch, true);
              }
            });
          }
        }
      }
    });

    Text branchText = addBranchControl(composite, container, branch);
    branchText.setText(BRANCH_NAME);

    // addVariablesControl(composite, container, branch);

    addFolderControl(composite, container, branch);
    return composite;
  }

  private Text addBranchControl(Composite composite, final Container container, final Branch branch)
  {
    new Label(composite, SWT.NONE).setText("Branch:");

    final Text branchText = new Text(composite, SWT.BORDER);
    SetupModelWizard.applyGridData(branchText);
    branchText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        branch.setName(branchText.getText());
        container.validate();
      }
    });

    new Label(composite, SWT.NONE);
    return branchText;
  }

  // private Button addVariablesControl(Composite composite, final Container container, final Branch branch)
  // {
  // new Label(composite, SWT.NONE);
  //
  // final Button button = new Button(composite, SWT.CHECK);
  // button.setText("Create variables for user IDs");
  // button.addSelectionListener(new SelectionAdapter()
  // {
  // @Override
  // public void widgetSelected(SelectionEvent e)
  // {
  // if (button.getSelection())
  // {
  // for (SetupTask task : branch.getSetupTasks())
  // {
  // if (task instanceof GitCloneTask)
  // {
  // GitCloneTask gitCloneTask = (GitCloneTask)task;
  // addVariable(gitCloneTask);
  // changeShellHeight(button.getShell(), 10);
  // }
  // }
  // }
  // else
  // {
  // // changeShellHeight(button.getShell(), -10);
  // }
  //
  // container.validate();
  // }
  // });
  //
  // new Label(composite, SWT.NONE);
  // return button;
  // }

  private Text addFolderControl(final Composite composite, final Container container, final Branch branch)
  {
    final Label label = new Label(composite, SWT.NONE);
    label.setText("Folder:");

    final Text text = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
    SetupModelWizard.applyGridData(text);

    final Button button = new Button(composite, SWT.NONE);
    button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    button.setBounds(0, 0, 75, 25);
    button.setText("&Add...");
    button.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        final Shell shell = text.getShell();

        if (button.getText().equals(REMOVE_TEXT))
        {
          @SuppressWarnings("unchecked")
          List<EObject> elements = (List<EObject>)text.getData(ASSOCIATED_ELEMENTS);
          for (EObject element : elements)
          {
            EcoreUtil.delete(element, true);
          }

          label.dispose();
          text.dispose();
          button.dispose();

          changeShellHeight(shell, -20);
          composite.getParent().getParent().layout();
          container.validate();
          return;
        }

        DirectoryDialog dialog = new DirectoryDialog(shell);
        dialog.setText(AbstractSetupDialog.SHELL_TEXT);
        dialog.setMessage("Select a folder to analyze:");
        if (lastFolder != null)
        {
          dialog.setFilterPath(lastFolder);
        }

        final String folder = dialog.open();
        if (folder != null)
        {
          lastFolder = folder;

          try
          {
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(shell);
            dlg.run(true, true, new IRunnableWithProgress()
            {
              public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
              {
                monitor.beginTask("Analyzing " + folder, IProgressMonitor.UNKNOWN);

                try
                {
                  final List<EObject> elements = new ArrayList<EObject>();
                  analyzeFolder(branch, new File(folder), elements, monitor);

                  text.getDisplay().syncExec(new Runnable()
                  {
                    public void run()
                    {
                      text.setData(ASSOCIATED_ELEMENTS, elements);
                      updateControl(text, button, folder, container, branch);
                    }
                  });
                }
                catch (OperationCanceledException ex)
                {
                  throw new InterruptedException();
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

              private void updateControl(Text text, Button button, String folder, Container container, Branch branch)
              {
                text.setText(folder);
                button.setText(REMOVE_TEXT);

                addFolderControl(composite, container, branch);

                changeShellHeight(shell, 20);
                composite.getParent().getParent().layout();
                container.validate();
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

    return text;
  }

  private void analyzeFolder(Branch branch, File folder, List<EObject> elements, IProgressMonitor monitor)
      throws Exception
  {
    EList<SetupTask> tasks = branch.getSetupTasks();
    String location = analyzeGit(folder, elements, tasks);
    analyzeMaterialization(folder, elements, tasks, location, monitor);
  }

  private String analyzeGit(File folder, List<EObject> elements, EList<SetupTask> tasks)
  {
    File git = new File(folder, ".git");
    if (git.isDirectory())
    {
      File config = new File(git, "config");
      if (config.isFile())
      {
        String content = IOUtil.readTextFile(config);

        Matcher matcher = GIT_URL_PATTERN.matcher(content);
        if (matcher.find())
        {
          URI baseURI = URI.createURI(matcher.group(1));
          String userID = baseURI.userInfo();
          if (StringUtil.isEmpty(userID))
          {
            userID = GitCloneTask.ANONYMOUS;
          }

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
          elements.add(task);

          ContextVariableTask variable = addVariable(task);
          elements.add(variable);
          return location;
        }
      }
    }

    return folder.getAbsolutePath();
  }

  private void analyzeMaterialization(File folder, List<EObject> elements, EList<SetupTask> tasks, String location,
      IProgressMonitor monitor) throws ParserConfigurationException
  {
    MaterializationTask task = SetupFactory.eINSTANCE.createMaterializationTask();

    AutomaticSourceLocator sourceLocator = SetupFactory.eINSTANCE.createAutomaticSourceLocator();
    sourceLocator.setRootFolder(location);
    task.getSourceLocators().add(sourceLocator);

    List<String> componentLocations = new ArrayList<String>();
    for (Pair<String, ComponentType> root : MaterializationTaskImpl.analyzeRoots(folder, componentLocations, monitor))
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

    elements.add(task);
    tasks.add(task);
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
        analyzeFile(task, file, TARGET_URL_PATTERN, 1);
      }
      else if (file.getName().endsWith(".rmap"))
      {
        analyzeFile(task, file, RMAP_URL_PATTERN, 2);
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

  private static ContextVariableTask addVariable(GitCloneTask task)
  {
    String userID = task.getUserID();
    String name = "git.user." + URI.createURI(task.getRemoteURI()).lastSegment().toLowerCase();
    if (name.endsWith(".git"))
    {
      name = name.substring(0, name.length() - ".git".length());
    }

    ContextVariableTask variable = SetupFactory.eINSTANCE.createContextVariableTask();
    variable.setName(name);
    variable.setValue(userID);
    variable.setDocumentation("Unset the value to prompt the user for the user ID.");

    ((Branch)task.eContainer()).getProject().getSetupTasks().add(variable);
    task.setUserID("${" + name + "}");

    return variable;
  }

  private static void changeShellHeight(Shell shell, int delta)
  {
    Point size = shell.getSize();
    size.y += delta;
    shell.setSize(size);
  }
}
