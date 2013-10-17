/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.P2Task;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.SetupTaskScope;
import org.eclipse.emf.cdo.releng.setup.util.FileUtil;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogMonitor;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.internal.p2.director.app.DirectorApplication;
import org.eclipse.equinox.internal.p2.director.app.ILog;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.MetadataFactory;
import org.eclipse.equinox.p2.metadata.MetadataFactory.InstallableUnitDescription;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.RepositoryTracker;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Install Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl#getP2Repositories <em>P2 Repositories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl#getInstallableUnits <em>Installable Units</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class P2TaskImpl extends SetupTaskImpl implements P2Task
{
  /**
   * The cached value of the '{@link #getP2Repositories() <em>P2 Repositories</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getP2Repositories()
   * @generated
   * @ordered
   */
  protected EList<P2Repository> p2Repositories;

  /**
   * The cached value of the '{@link #getInstallableUnits() <em>Installable Units</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstallableUnits()
   * @generated
   * @ordered
   */
  protected EList<InstallableUnit> installableUnits;

  private transient Set<String> neededInstallableUnits;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected P2TaskImpl()
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
    return SetupPackage.Literals.P2_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<InstallableUnit> getInstallableUnits()
  {
    if (installableUnits == null)
    {
      installableUnits = new EObjectContainmentWithInverseEList.Resolving<InstallableUnit>(InstallableUnit.class, this,
          SetupPackage.P2_TASK__INSTALLABLE_UNITS, SetupPackage.INSTALLABLE_UNIT__P2_TASK);
    }
    return installableUnits;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<P2Repository> getP2Repositories()
  {
    if (p2Repositories == null)
    {
      p2Repositories = new EObjectContainmentWithInverseEList.Resolving<P2Repository>(P2Repository.class, this,
          SetupPackage.P2_TASK__P2_REPOSITORIES, SetupPackage.P2_REPOSITORY__P2_TASK);
    }
    return p2Repositories;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getP2Repositories()).basicAdd(otherEnd, msgs);
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getInstallableUnits()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      return ((InternalEList<?>)getP2Repositories()).basicRemove(otherEnd, msgs);
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      return ((InternalEList<?>)getInstallableUnits()).basicRemove(otherEnd, msgs);
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
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      return getP2Repositories();
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      return getInstallableUnits();
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
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      getP2Repositories().clear();
      getP2Repositories().addAll((Collection<? extends P2Repository>)newValue);
      return;
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      getInstallableUnits().clear();
      getInstallableUnits().addAll((Collection<? extends InstallableUnit>)newValue);
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
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      getP2Repositories().clear();
      return;
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      getInstallableUnits().clear();
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
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      return p2Repositories != null && !p2Repositories.isEmpty();
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      return installableUnits != null && !installableUnits.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  private static Set<String> getInstalledUnits()
  {
    Set<String> result = new HashSet<String>();
    ProvisioningUI provisioningUI = ProvisioningUI.getDefaultUI();
    ProvisioningSession session = provisioningUI.getSession();
    String profileId = provisioningUI.getProfileId();
    IProfile profile = ProvUI.getProfileRegistry(session).getProfile(profileId);
    IQueryResult<IInstallableUnit> queryResult = profile.query(QueryUtil.createIUAnyQuery(), null);

    for (IInstallableUnit installableUnit : queryResult)
    {
      result.add(installableUnit.getId());
    }

    return result;
  }

  private static Set<String> getKnownRepositories()
  {
    Set<String> result = new HashSet<String>();
    ProvisioningUI provisioningUI = ProvisioningUI.getDefaultUI();
    ProvisioningSession session = provisioningUI.getSession();
    for (URI knowRepository : provisioningUI.getRepositoryTracker().getKnownRepositories(session))
    {
      result.add(knowRepository.toString());
    }
    return result;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    if (!Activator.SETUP_IDE)
    {
      return true;
    }

    Set<String> installedUnits = getInstalledUnits();
    for (InstallableUnit installableUnit : getInstallableUnits())
    {
      String id = context.expandString(installableUnit.getId());
      if (!installedUnits.contains(id))
      {
        if (neededInstallableUnits == null)
        {
          neededInstallableUnits = new HashSet<String>();
        }
        neededInstallableUnits.add(id);
      }
    }

    Set<String> knownRepositories = getKnownRepositories();
    for (P2Repository p2Repository : getP2Repositories())
    {
      String url = context.expandString(p2Repository.getUrl());
      if (!knownRepositories.contains(url))
      {
        return true;
      }
    }

    return neededInstallableUnits != null;
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    if (Activator.SETUP_IDE)
    {
      ProgressLogMonitor monitor = new ProgressLogMonitor(context);

      ProvisioningUI provisioningUI = ProvisioningUI.getDefaultUI();
      ProvisioningSession session = provisioningUI.getSession();
      IMetadataRepositoryManager manager = (IMetadataRepositoryManager)session.getProvisioningAgent().getService(
          IMetadataRepositoryManager.SERVICE_NAME);
      RepositoryTracker repositoryTracker = provisioningUI.getRepositoryTracker();

      Set<String> knownRepositories = getKnownRepositories();
      Set<IMetadataRepository> repositories = new HashSet<IMetadataRepository>();
      List<URI> repos = new ArrayList<URI>();
      for (P2Repository p2Repository : getP2Repositories())
      {
        String url = context.expandString(p2Repository.getUrl());
        URI uri = new URI(url);
        if (neededInstallableUnits == null)
        {
          if (!knownRepositories.contains(url))
          {
            IMetadataRepository repository = manager.loadRepository(uri, monitor);
            repositoryTracker.addRepository(uri, repository.getName(), session);
          }
        }
        else
        {
          repos.add(uri);

          IMetadataRepository repository = manager.loadRepository(uri, monitor);
          repositories.add(repository);

          if (!knownRepositories.contains(url))
          {
            repositoryTracker.addRepository(uri, repository.getName(), session);
          }
        }
      }

      if (neededInstallableUnits != null)
      {
        List<IInstallableUnit> toInstall = new ArrayList<IInstallableUnit>();
        for (String installableUnit : neededInstallableUnits)
        {
          IQuery<IInstallableUnit> iuQuery = QueryUtil.createIUQuery(installableUnit);
          IInstallableUnit candidate = null;
          for (IMetadataRepository repository : repositories)
          {
            IQueryResult<IInstallableUnit> queryResult = repository.query(iuQuery, monitor);
            for (IInstallableUnit installableUnitMatch : queryResult)
            {
              if (candidate == null || candidate.getVersion().compareTo(installableUnitMatch.getVersion()) < 0)
              {
                candidate = installableUnitMatch;
              }
              break;
            }
          }
          if (candidate != null)
          {
            toInstall.add(candidate);
          }
          else
          {
            // This will fail.
            // TODO
            InstallableUnitDescription installableUnitDescription = new InstallableUnitDescription();
            installableUnitDescription.setId(installableUnit);
            toInstall.add(MetadataFactory.createInstallableUnit(installableUnitDescription));
          }
        }

        InstallOperation installOperation = new InstallOperation(session, toInstall);
        String profileId = provisioningUI.getProfileId();
        installOperation.setProfileId(profileId);

        ProvisioningContext provisioningContext = makeProvisioningContext(session, repos);
        installOperation.setProvisioningContext(provisioningContext);

        IStatus status = installOperation.resolveModal(monitor);
        if (status.isOK())
        {
          ProvisioningJob provisioningJob = installOperation.getProvisioningJob(null);
          provisioningJob.run(monitor);
        }
        else
        {
          context.log(status.toString());
        }
      }
    }
    else
    {
      callDirectorApp(context);
    }
    context.setRestartNeeded();
  }

  private ProvisioningContext makeProvisioningContext(ProvisioningSession session, Collection<URI> repositories)
  {
    URI[] repos = repositories.toArray(new URI[repositories.size()]);
    ProvisioningContext context = new ProvisioningContext(session.getProvisioningAgent());
    context.setMetadataRepositories(repos);
    context.setArtifactRepositories(repos);
    return context;
  }

  // public void computeRemediationOperation(ProfileChangeOperation op, ProvisioningUI ui, IProgressMonitor monitor) {
  // SubMonitor sub = SubMonitor.convert(monitor, ProvUIMessages.ProvisioningOperationWizard_Remediation_Operation,
  // RemedyConfig.getAllRemedyConfigs().length);
  // monitor.setTaskName(ProvUIMessages.ProvisioningOperationWizard_Remediation_Operation);
  // remediationOperation = new RemediationOperation(ui.getSession(), op.getProfileChangeRequest());
  // remediationOperation.resolveModal(monitor);
  // sub.done();
  // }

  private void callDirectorApp(final SetupTaskContext context) throws Exception
  {
    if (getScope() == SetupTaskScope.CONFIGURATION)

    {
      FileUtil.delete(context.getP2ProfileDir(), new ProgressLogMonitor(context));
    }

    String destination = context.getEclipseDir().toString();
    String bundlePool = context.getP2PoolDir().toString();
    String bundleAgent = context.getP2AgentDir().toString();

    String os = Platform.getOS();
    String ws = Platform.getWS();
    String arch = Platform.getOSArch();

    EList<P2Repository> p2Repositories = getP2Repositories();
    EList<InstallableUnit> installableUnits = getInstallableUnits();

    context.log("Calling director to install " + installableUnits.size()
        + (installableUnits.size() == 1 ? " unit" : " units") + " from " + p2Repositories.size()
        + (p2Repositories.size() == 1 ? " repository" : " repositories") + " to " + destination);

    new File(destination).mkdirs();

    String repositories = makeList(context, p2Repositories, SetupPackage.Literals.P2_REPOSITORY__URL);
    String ius = makeList(context, installableUnits, SetupPackage.Literals.INSTALLABLE_UNIT__ID);

    String[] args = { "-destination", destination, "-repository", repositories, "-installIU", ius, "-profile",
        context.getP2ProfileName(), "-profileProperties", "org.eclipse.update.install.features=true", "-bundlepool",
        bundlePool, "-shared", bundleAgent, "-p2.os", os, "-p2.ws", ws, "-p2.arch", arch };

    DirectorApplication app = new DirectorApplication();
    app.setLog(new ILog()
    {
      public void log(String message)
      {
        if (context.isCancelled())
        {
          throw new OperationCanceledException();
        }

        context.log(message);
      }

      public void log(IStatus status)
      {
        log(status.getMessage());
      }

      public void close()
      {
      }
    });

    app.run(args);
  }

  private String makeList(SetupTaskContext context, EList<? extends EObject> objects, EAttribute attribute)
  {
    StringBuilder builder = new StringBuilder();
    for (EObject object : objects)
    {
      if (builder.length() > 0)
      {
        builder.append(',');
      }

      String value = (String)object.eGet(attribute);
      value = context.expandString(value);
      builder.append(value);
    }

    return builder.toString();
  }

} // InstallTaskImpl
