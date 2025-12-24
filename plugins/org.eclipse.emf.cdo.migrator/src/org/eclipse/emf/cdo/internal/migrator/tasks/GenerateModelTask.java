/*
 * Copyright (c) 2018, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator.tasks;

import org.eclipse.emf.cdo.internal.migrator.CDOMigratorUtil;

import org.eclipse.emf.codegen.ecore.CodeGenEcorePlugin;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.util.GenModelUtil;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.apache.tools.ant.BuildException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Eike Stepper
 */
public class GenerateModelTask extends CDOTask
{
  private static final Set<ProjectType> ALL_PROJECT_TYPES = new LinkedHashSet<>(Arrays.asList( //
      ProjectType.model, //
      ProjectType.edit, //
      ProjectType.editor, //
      ProjectType.tests //
  ));

  private final List<Input> inputs = new ArrayList<>();

  private final List<Output> outputs = new ArrayList<>();

  private boolean recursive;

  private String modelPath;

  private ProjectType projectType;

  public GenerateModelTask()
  {
  }

  public Input getInput(String modelPath)
  {
    for (Input input : inputs)
    {
      if (input.getModelPath().equals(modelPath))
      {
        return input;
      }
    }

    if (Objects.equals(this.modelPath, modelPath))
    {
      Input input = new Input();
      input.setModelPath(modelPath);
      return input;
    }

    return null;
  }

  public Input createInput()
  {
    Input input = new Input();
    inputs.add(input);
    return input;
  }

  public Output createOutput()
  {
    Output output = new Output();
    outputs.add(output);
    return output;
  }

  public void setRecursive(boolean recursive)
  {
    this.recursive = recursive;
  }

  public void setModelPath(String modelPath)
  {
    this.modelPath = modelPath;
  }

  public ProjectType getProjectType()
  {
    return projectType;
  }

  public void setProjectType(ProjectType projectType)
  {
    this.projectType = projectType;
  }

  private Set<ProjectType> getProjectTypes()
  {
    Set<ProjectType> projectTypes = new HashSet<>();

    if (isSet(outputs))
    {
      for (Output output : outputs)
      {
        addProjectType(projectTypes, output.getProjectType());
      }
    }
    else
    {
      addProjectType(projectTypes, projectType);
    }

    return projectTypes;
  }

  private void addProjectType(Set<ProjectType> projectTypes, ProjectType projectType)
  {
    if (projectType == ProjectType.all)
    {
      projectTypes.addAll(ALL_PROJECT_TYPES);
    }
    else if (projectType != ProjectType.none)
    {
      projectTypes.add(projectType);
    }
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    assertTrue("Either the 'modelPath' attribute or nested 'input' elements must be specified.", isSet(modelPath) ^ isSet(inputs));
    assertTrue("Either the 'projectType' attribute or nested 'output' elements can be specified.", !(isSet(modelPath) && isSet(inputs)));

    if (!isSet(projectType) && !isSet(outputs))
    {
      projectType = ProjectType.model;
    }
  }

  @Override
  protected void doExecute() throws Exception
  {
    if (isSet(inputs))
    {
      generate(inputs.stream().map(Input::getModelPath).collect(Collectors.toList()));
    }
    else
    {
      generate(Arrays.asList(modelPath));
    }
  }

  private void generate(List<String> modelPaths) throws Exception
  {
    long start = System.currentTimeMillis();
    int projects = 0;
    Set<GenModel> visited = new HashSet<>();
    Monitor monitor = verbose ? new BasicMonitor.Printing(System.out) : BasicMonitor.toMonitor(new NullProgressMonitor());

    List<GenModel> genModels = CDOMigratorUtil.getGenModels(modelPaths);
    for (GenModel genModel : genModels)
    {
      projects += generate(genModel, genModels, visited, monitor);
    }

    long millis = System.currentTimeMillis() - start;
    System.out.println("==================================================================================");
    System.out.println("Generated " + projects + " projects for " + visited.size() + " models in " + millis + " milliseconds");
  }

  private int generate(GenModel genModel, List<GenModel> genModels, Set<GenModel> visited, Monitor monitor) throws Exception
  {
    int projects = 0;

    if (genModel != null && visited.add(genModel))
    {
      EList<GenPackage> usedGenPackages = genModel.getUsedGenPackages();
      if (usedGenPackages != null)
      {
        for (GenPackage usedGenPackage : usedGenPackages)
        {
          GenModel usedGenModel = usedGenPackage.getGenModel();
          projects += generate(usedGenModel, genModels, visited, monitor);
        }
      }

      if (!recursive && !genModels.contains(genModel))
      {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Ignoring   " + modelPath);
      }
      else
      {
        URI uri = genModel.eResource().getURI();
        String modelPath = uri.toPlatformString(true);

        Input input = getInput(modelPath);
        Set<ProjectType> projectTypes = input == null ? getProjectTypes() : input.getProjectTypes();

        Generator generator = null;

        for (ProjectType projectType : ALL_PROJECT_TYPES)
        {
          if (projectTypes.contains(projectType) && canGenerate(genModel, projectType.getDirectoryGetter()))
          {
            if (generator == null)
            {
              System.out.println("----------------------------------------------------------------------------------");
              System.out.println("Validating " + modelPath);

              genModel.setCanGenerate(true);
              genModel.setValidateModel(true);

              validate(genModel);

              generator = GenModelUtil.createGenerator(genModel);
            }

            System.out.println("Generating " + modelPath + " (" + projectType + " project)");
            generator.generate(genModel, projectType.getKey(), projectType.getLabel(), monitor);

            ++projects;
          }
        }
      }
    }

    return projects;
  }

  private static boolean canGenerate(GenModel genModel, Function<GenModel, String> directoryGetter)
  {
    String directory = directoryGetter.apply(genModel);
    return isSet(directory);
  }

  private static void validate(GenModel genModel) throws DiagnosticException
  {
    Diagnostic diagnostic = genModel.diagnose();
    if (diagnostic != null && diagnostic.getSeverity() != Diagnostic.OK)
    {
      List<Diagnostic> children = diagnostic.getChildren();
      if (children.isEmpty())
      {
        System.err.println(diagnostic);
      }
      else
      {
        for (Diagnostic child : children)
        {
          System.err.println(child);
        }
      }

      System.err.flush();

      throw new DiagnosticException(diagnostic);
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class Input
  {
    private final List<Output> outputs = new ArrayList<>();

    private String modelPath;

    private ProjectType projectType;

    public Input()
    {
    }

    public String getModelPath()
    {
      return modelPath;
    }

    public void setModelPath(String modelPath)
    {
      this.modelPath = modelPath;
    }

    public Set<ProjectType> getProjectTypes()
    {
      if (isSet(outputs))
      {
        Set<ProjectType> projectTypes = new HashSet<>();
        for (Output output : outputs)
        {
          addProjectType(projectTypes, output.getProjectType());
        }

        return projectTypes;
      }

      if (isSet(projectType))
      {
        return Collections.singleton(projectType);
      }

      return GenerateModelTask.this.getProjectTypes();
    }

    public ProjectType getProjectType()
    {
      return projectType;
    }

    public void setProjectType(ProjectType projectType)
    {
      this.projectType = projectType;
    }

    public List<Output> getOutputs()
    {
      return outputs;
    }

    public Output createOutput()
    {
      Output output = new Output();
      outputs.add(output);
      return output;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("modelPath = ");
      builder.append(modelPath);
      return builder.toString();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Output
  {
    private ProjectType projectType;

    public Output()
    {
    }

    public ProjectType getProjectType()
    {
      return projectType;
    }

    public void setProjectType(ProjectType projectType)
    {
      this.projectType = projectType;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("projectType = ");
      builder.append(projectType);
      return builder.toString();
    }
  }

  /**
   * @author Eike Stepper
   */
  public enum ProjectType
  {
    model(GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, CodeGenEcorePlugin.INSTANCE.getString("_UI_ModelProject_name"), GenModel::getModelDirectory),

    edit(GenBaseGeneratorAdapter.EDIT_PROJECT_TYPE, CodeGenEcorePlugin.INSTANCE.getString("_UI_EditProject_name"), GenModel::getEditDirectory),

    editor(GenBaseGeneratorAdapter.EDITOR_PROJECT_TYPE, CodeGenEcorePlugin.INSTANCE.getString("_UI_EditorProject_name"), GenModel::getEditorDirectory),

    tests(GenBaseGeneratorAdapter.TESTS_PROJECT_TYPE, CodeGenEcorePlugin.INSTANCE.getString("_UI_TestsProject_name"), GenModel::getTestsDirectory),

    none(null, null, null),

    all(null, null, null);

    private final String key;

    private final String label;

    private final Function<GenModel, String> directoryGetter;

    private ProjectType(String key, String label, Function<GenModel, String> directoryGetter)
    {
      this.key = key;
      this.label = label;
      this.directoryGetter = directoryGetter;
    }

    public String getKey()
    {
      return key;
    }

    public String getLabel()
    {
      return label;
    }

    public Function<GenModel, String> getDirectoryGetter()
    {
      return directoryGetter;
    }
  }
}
