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
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.GitCloneTask;
import org.eclipse.emf.cdo.releng.setup.MaterializationTask;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.editor.ProjectTemplate;
import org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupEditorPlugin;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupModelWizard;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class AutomaticProjectTemplate extends ProjectTemplate
{
  private static final String BC = "http://www.eclipse.org/buckminster/Common-1.0";

  private static final String RM = "http://www.eclipse.org/buckminster/RMap-1.0";

  private static final Pattern GIT_URL_PATTERN = Pattern.compile("\\[remote \"([^\"]*)\"].*?url *= *([^\\r\\n]*)",
      Pattern.DOTALL | Pattern.MULTILINE);

  private static final String BRANCH_NAME = "master";

  private static final String REMOVE_TEXT = "&Remove";

  private static final String ASSOCIATED_ELEMENTS = "associated-elements";

  private static String lastFolder;

  private final Branch branch;

  private int folders;

  private DocumentBuilder documentBuilder;

  public AutomaticProjectTemplate()
  {
    super("Analyze folders such as a Git working trees");

    IDialogSettings section = getSettings();
    lastFolder = section.get("lastFolder");

    branch = addBranch();
    branch.eAdapters().add(new EContentAdapter()
    {
      @Override
      public void notifyChanged(Notification notification)
      {
        super.notifyChanged(notification);
        if (!notification.isTouch())
        {
          final TreeViewer preViewer = getContainer().getPreViewer();
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
  }

  @Override
  public boolean isValid(Branch branch)
  {
    return super.isValid(branch) && folders > 1;
  }

  @Override
  public Control createControl(Composite parent)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.verticalSpacing = 5;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(layout);
    SetupModelWizard.applyGridData(composite);

    Text branchText = addBranchControl(composite);
    branchText.setText(BRANCH_NAME);

    addFolderControl(composite);
    return composite;
  }

  protected Text addBranchControl(Composite composite)
  {
    new Label(composite, SWT.NONE).setText("Branch:");

    final Text branchText = new Text(composite, SWT.BORDER);
    SetupModelWizard.applyGridData(branchText);
    branchText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        branch.setName(branchText.getText());
        getContainer().validate();
      }
    });

    new Label(composite, SWT.NONE);
    return branchText;
  }

  protected Text addFolderControl(final Composite composite)
  {
    ++folders;

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

          --folders;
          getContainer().validate();
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

          IDialogSettings section = getSettings();
          section.put("lastFolder", lastFolder);

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
                  analyzeFolder(new File(folder), elements, monitor);

                  text.getDisplay().syncExec(new Runnable()
                  {
                    public void run()
                    {
                      text.setData(ASSOCIATED_ELEMENTS, elements);
                      updateControl(text, button, folder);
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

              private void updateControl(Text text, Button button, String folder)
              {
                text.setText(folder);
                button.setText(REMOVE_TEXT);

                addFolderControl(composite);

                changeShellHeight(shell, 20);
                composite.getParent().getParent().layout();
                getContainer().validate();
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

  private void analyzeFolder(File folder, List<EObject> elements, IProgressMonitor monitor) throws Exception
  {
    Set<String> variableNames = new HashSet<String>();

    EList<SetupTask> projectTasks = getProject().getSetupTasks();
    for (SetupTask setupTask : projectTasks)
    {
      if (setupTask instanceof ContextVariableTask)
      {
        ContextVariableTask variable = (ContextVariableTask)setupTask;
        variableNames.add(variable.getName());
      }
    }

    Set<String> newVariableNames = new HashSet<String>();
    GitCloneTask gitCloneTask = analyzeGit(folder, elements, newVariableNames);

    String location = gitCloneTask == null ? folder.getAbsolutePath() : gitCloneTask.getLocation();
    analyzeMaterialization(folder, location, elements, newVariableNames, gitCloneTask, monitor);

    List<String> list = new ArrayList<String>(newVariableNames);
    Collections.sort(list);

    for (String variableName : list)
    {
      if (!variableNames.contains(variableName))
      {
        ContextVariableTask variable = SetupFactory.eINSTANCE.createContextVariableTask();
        variable.setName(variableName);

        projectTasks.add(variable);
      }
    }
  }

  private GitCloneTask analyzeGit(File folder, List<EObject> elements, Set<String> variableNames)
  {
    File git = new File(folder, ".git");
    if (git.isDirectory())
    {
      File config = new File(git, "config");
      if (config.isFile())
      {
        String content = IOUtil.readTextFile(config);

        Map<String, String> uris = new LinkedHashMap<String, String>();
        Matcher matcher = GIT_URL_PATTERN.matcher(content);
        while (matcher.find())
        {
          uris.put(matcher.group(1), matcher.group(2));
        }

        if (!uris.isEmpty())
        {
          String remoteName = "origin";
          String mainURI = uris.get(remoteName);
          if (mainURI == null)
          {
            Map.Entry<String, String> entry = uris.entrySet().iterator().next();
            remoteName = entry.getKey();
            mainURI = entry.getValue();
          }

          URI baseURI = URI.createURI(mainURI);

          String userID = baseURI.userInfo();
          if (!StringUtil.isEmpty(userID))
          {
            String host = baseURI.host();
            if (!StringUtil.isEmpty(baseURI.port()))
            {
              host += ":" + baseURI.port();
            }

            baseURI = URI.createHierarchicalURI(baseURI.scheme(), host, baseURI.device(), baseURI.segments(),
                baseURI.query(), baseURI.fragment());
          }

          String location = "${setup.branch.dir/git/" + new Path(baseURI.path()).lastSegment() + "}";

          GitCloneTask task = SetupFactory.eINSTANCE.createGitCloneTask();
          task.setLocation(location);
          task.setUserID("${git.user.id}");
          task.setRemoteURI(baseURI.toString());
          task.setRemoteName(remoteName);
          task.setCheckoutBranch("master");

          branch.getSetupTasks().add(task);
          elements.add(task);

          variableNames.add("git.user.id");
          return task;
        }
      }
    }

    return null;
  }

  private void analyzeMaterialization(File folder, String location, List<EObject> elements, Set<String> variableNames,
      SetupTask requirement, IProgressMonitor monitor) throws Exception
  {
    List<String> componentLocations = new ArrayList<String>();
    Set<Pair<String, ComponentType>> roots = MaterializationTaskImpl.analyzeRoots(folder, false, componentLocations,
        monitor);

    SetupTaskContainer container = branch;
    MaterializationTask lastTask = null;

    List<File> additionalResources = analyzeAdditionalResources(componentLocations);
    if (additionalResources.size() > 1)
    {
      CompoundSetupTask compound = SetupFactory.eINSTANCE.createCompoundSetupTask();
      compound.setName("Possible Materializations");
      compound
          .setDocumentation("There are several possible materializations. By default all but the last one are disabled.");

      elements.add(compound);
      branch.getSetupTasks().add(compound);
      container = compound;
    }

    for (File additionalResource : additionalResources)
    {
      MaterializationTask task = SetupFactory.eINSTANCE.createMaterializationTask();
      task.setDocumentation("Generated from " + additionalResource);
      task.setDisabled(true);

      if (requirement != null)
      {
        task.getRequirements().add(requirement);
      }

      AutomaticSourceLocator sourceLocator = SetupFactory.eINSTANCE.createAutomaticSourceLocator();
      sourceLocator.setRootFolder(location);
      task.getSourceLocators().add(sourceLocator);

      for (Pair<String, ComponentType> root : roots)
      {
        Component component = SetupFactory.eINSTANCE.createComponent();
        component.setName(root.getElement1());
        component.setType(root.getElement2());
        task.getRootComponents().add(component);
      }

      DocumentBuilder documentBuilder = getDocumentBuilder();
      Document document = MaterializationTaskImpl.loadDocument(documentBuilder, additionalResource);

      String name = additionalResource.getName();
      if (name.endsWith(".target"))
      {
        analyzeTargetDefinition(task, document);
      }
      else if (additionalResource.getName().endsWith(".rmap"))
      {
        analyzeResourceMap(task, document, variableNames);
      }

      sortChildren(task);

      if (container == branch)
      {
        elements.add(task);
      }

      container.getSetupTasks().add(task);
      lastTask = task;
    }

    if (lastTask != null)
    {
      lastTask.setDisabled(false);
    }
    else
    {
      // TODO
    }
  }

  private List<File> analyzeAdditionalResources(List<String> componentLocations)
  {
    List<File> additionalResources = new ArrayList<File>();
    for (String componentLocation : componentLocations)
    {
      analyzeAdditionalResources(additionalResources, new File(componentLocation));
    }

    Collections.sort(additionalResources, new Comparator<File>()
    {
      public int compare(File o1, File o2)
      {
        return o1.getName().compareTo(o2.getName());
      }
    });

    return additionalResources;
  }

  private void analyzeAdditionalResources(List<File> result, File folder)
  {
    File[] files = folder.listFiles();
    for (int i = 0; i < files.length; i++)
    {
      File file = files[i];
      if (file.isDirectory())
      {
        analyzeAdditionalResources(result, file);
      }
      else
      {
        String name = file.getName();
        if (name.endsWith(".target") || name.endsWith(".rmap"))
        {
          result.add(file);
        }
      }
    }
  }

  private void analyzeTargetDefinition(MaterializationTask task, Document document)
  {
    NodeList units = document.getElementsByTagNameNS("*", "unit");
    for (int i = 0; i < units.getLength(); i++)
    {
      Element unit = (Element)units.item(i);
      String id = unit.getAttribute("id");
      String version = unit.getAttribute("version");

      ComponentType type = id.endsWith(".feature.group") ? ComponentType.ECLIPSE_FEATURE : ComponentType.OSGI_BUNDLE;
      addRootComponent(task, id, type, version);
    }

    NodeList repositories = document.getElementsByTagNameNS("*", "repository");
    for (int i = 0; i < repositories.getLength(); i++)
    {
      Element repository = (Element)repositories.item(i);
      String location = repository.getAttribute("location");

      addP2Repository(task, location);
    }
  }

  private void analyzeResourceMap(MaterializationTask task, Document document, Set<String> variableNames)
  {
    Map<String, String> propertiesMap = new HashMap<String, String>();

    NodeList properties = document.getElementsByTagNameNS(RM, "property");
    for (int i = 0; i < properties.getLength(); i++)
    {
      Element property = (Element)properties.item(i);
      String key = property.getAttribute("key");
      String value = property.getAttribute("value");

      if (value != null)
      {
        propertiesMap.put(key, value);
      }
    }

    NodeList providers = document.getElementsByTagNameNS(RM, "provider");
    for (int i = 0; i < providers.getLength(); i++)
    {
      Element provider = (Element)providers.item(i);
      String readerType = provider.getAttribute("readerType");
      if ("p2".equals(readerType))
      {
        NodeList uris = provider.getElementsByTagNameNS(RM, "uri");
        for (int j = 0; j < uris.getLength(); j++)
        {
          Element uri = (Element)uris.item(j);
          String format = uri.getAttribute("format");

          NodeList propertyRefs = provider.getElementsByTagNameNS(BC, "propertyRef");
          for (int k = 0; k < propertyRefs.getLength(); k++)
          {
            Element propertyRef = (Element)propertyRefs.item(k);
            String key = propertyRef.getAttribute("key");

            if (key != null)
            {
              String value = propertiesMap.get(key);
              if (value == null)
              {
                value = "#{" + key + "}";
                variableNames.add(key);
              }

              format = format.replace("{" + k + "}", value);
            }
          }

          format = format.replace('#', '$');
          addP2Repository(task, format);
        }
      }
    }
  }

  private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException
  {
    if (documentBuilder == null)
    {
      documentBuilder = MaterializationTaskImpl.createDocumentBuilder();
    }

    return documentBuilder;
  }

  private static void addRootComponent(MaterializationTask task, String id, ComponentType type, String version)
  {
    if (id.endsWith(".source"))
    {
      return;
    }

    Component rootComponent = SetupFactory.eINSTANCE.createComponent();
    rootComponent.setName(id);
    rootComponent.setType(type);

    if (!StringUtil.isEmpty(version))
    {
      VersionRange versionRange = new VersionRange("[" + version + "," + version + "]");
      rootComponent.setVersionRange(versionRange);
    }

    task.getRootComponents().add(rootComponent);
  }

  private static void addP2Repository(MaterializationTask task, String url)
  {
    if (url.endsWith("/"))
    {
      url = url.substring(0, url.length() - 1);
    }

    EList<P2Repository> p2Repositories = task.getP2Repositories();
    for (P2Repository repository : p2Repositories)
    {
      if (ObjectUtil.equals(repository.getURL(), url))
      {
        return;
      }
    }

    P2Repository repository = SetupFactory.eINSTANCE.createP2Repository();
    repository.setURL(url);

    p2Repositories.add(repository);
  }

  private static void sortChildren(MaterializationTask task)
  {
    ECollections.sort(task.getRootComponents(), new Comparator<Component>()
    {
      public int compare(Component o1, Component o2)
      {
        return o1.getName().compareTo(o2.getName());
      }
    });

    ECollections.sort(task.getP2Repositories(), new Comparator<P2Repository>()
    {
      public int compare(P2Repository o1, P2Repository o2)
      {
        return o1.getURL().compareTo(o2.getURL());
      }
    });
  }

  private static void changeShellHeight(Shell shell, int delta)
  {
    // Point size = shell.getSize();
    // size.y += delta;
    // shell.setSize(size);
  }

  private static IDialogSettings getSettings()
  {
    IDialogSettings dialogSettings = SetupEditorPlugin.getPlugin().getDialogSettings();
    IDialogSettings section = dialogSettings.getSection(AutomaticProjectTemplate.class.getName());
    if (section == null)
    {
      section = dialogSettings.addNewSection(AutomaticProjectTemplate.class.getName());
    }

    return section;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends ProjectTemplate.Factory
  {
    public Factory()
    {
      super("automatic");
    }

    @Override
    public ProjectTemplate createProjectTemplate()
    {
      return new AutomaticProjectTemplate();
    }
  }
}
