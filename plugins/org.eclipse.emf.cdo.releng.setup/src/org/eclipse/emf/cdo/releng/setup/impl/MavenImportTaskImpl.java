/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.internal.setup.util.BasicProjectAnalyzer;
import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.cdo.releng.predicates.PredicatesFactory;
import org.eclipse.emf.cdo.releng.predicates.PredicatesFactory.ProjectDescriptionFactory;
import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.MavenImportTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.log.ProgressLogMonitor;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.LocalProjectScanner;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Maven Import Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MavenImportTaskImpl#getSourceLocators <em>Source Locators</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MavenImportTaskImpl extends SetupTaskImpl implements MavenImportTask
{
  private static final IWorkspaceRoot ROOT = ResourcesPlugin.getWorkspace().getRoot();

  /**
   * The cached value of the '{@link #getSourceLocators() <em>Source Locators</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceLocators()
   * @generated
   * @ordered
   */
  protected EList<AutomaticSourceLocator> sourceLocators;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MavenImportTaskImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.MAVEN_IMPORT_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AutomaticSourceLocator> getSourceLocators()
  {
    if (sourceLocators == null)
    {
      sourceLocators = new EObjectContainmentEList.Resolving<AutomaticSourceLocator>(AutomaticSourceLocator.class,
          this, SetupPackage.MAVEN_IMPORT_TASK__SOURCE_LOCATORS);
    }
    return sourceLocators;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.MAVEN_IMPORT_TASK__SOURCE_LOCATORS:
      return ((InternalEList<?>)getSourceLocators()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.MAVEN_IMPORT_TASK__SOURCE_LOCATORS:
      return getSourceLocators();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.MAVEN_IMPORT_TASK__SOURCE_LOCATORS:
      getSourceLocators().clear();
      getSourceLocators().addAll((Collection<? extends AutomaticSourceLocator>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.MAVEN_IMPORT_TASK__SOURCE_LOCATORS:
      getSourceLocators().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.MAVEN_IMPORT_TASK__SOURCE_LOCATORS:
      return sourceLocators != null && !sourceLocators.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    EList<AutomaticSourceLocator> sourceLocators = getSourceLocators();
    if (sourceLocators.isEmpty())
    {
      return false;
    }

    if (context.getTrigger() != Trigger.MANUAL)
    {
      for (IProject project : ROOT.getProjects())
      {
        IPath projectFolder = project.getLocation();
        for (AutomaticSourceLocator sourceLocator : sourceLocators)
        {
          Path rootFolder = new Path(sourceLocator.getRootFolder());
          if (rootFolder.isPrefixOf(projectFolder))
          {
            // In STARTUP trigger don't perform if there's already at least 1 project from the source locators
            return false;
          }
        }
      }
    }

    return true;
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    MavenUtil.perform(context, this);
  }

  /**
   * @author Eike Stepper
   */
  private static final class MavenUtil
  {
    public static void perform(SetupTaskContext context, MavenImportTaskImpl impl) throws Exception
    {
      List<String> folders = new ArrayList<String>();
      EList<AutomaticSourceLocator> sourceLocators = impl.getSourceLocators();

      for (AutomaticSourceLocator sourceLocator : sourceLocators)
      {
        folders.add(sourceLocator.getRootFolder());
      }

      MavenModelManager modelManager = MavenPlugin.getMavenModelManager();
      IProgressMonitor monitor = new ProgressLogMonitor(context);

      LocalProjectScanner projectScanner = new LocalProjectScanner(null, folders, false, modelManager);
      projectScanner.run(monitor);

      Set<MavenProjectInfo> projectInfos = new LinkedHashSet<MavenProjectInfo>();
      for (Iterator<MavenProjectInfo> it = projectScanner.getProjects().iterator(); it.hasNext();)
      {
        MavenProjectInfo projectInfo = it.next();
        processMavenProject(projectInfo, projectInfos, sourceLocators);
      }

      if (!projectInfos.isEmpty())
      {
        ProjectImportConfiguration configuration = new ProjectImportConfiguration();

        IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();
        projectConfigurationManager.importProjects(projectInfos, configuration, monitor);
      }
    }

    private static void processMavenProject(MavenProjectInfo projectInfo, Set<MavenProjectInfo> projectInfos,
        EList<AutomaticSourceLocator> sourceLocators)
    {
      File folder = projectInfo.getPomFile().getParentFile();

      IProject project = PredicatesFactory.eINSTANCE.loadProject(folder, ProjectDescriptionFactory.MAVEN);
      if (project != null)
      {
        if (matches(project, sourceLocators))
        {
          String projectName = project.getName();
          if (!ROOT.getProject(projectName).exists())
          {
            projectInfos.add(projectInfo);
          }
        }
      }

      for (MavenProjectInfo childProjectInfo : projectInfo.getProjects())
      {
        processMavenProject(childProjectInfo, projectInfos, sourceLocators);
      }
    }

    private static boolean matches(IProject project, EList<AutomaticSourceLocator> sourceLocators)
    {
      for (AutomaticSourceLocator sourceLocator : sourceLocators)
      {
        EList<Predicate> predicates = sourceLocator.getPredicates();
        if (!predicates.isEmpty())
        {
          if (!BasicProjectAnalyzer.matches(project, predicates))
          {
            return false;
          }
        }
      }

      return true;
    }
  }

} // MavenImportTaskImpl
