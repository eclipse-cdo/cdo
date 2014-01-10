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
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.ui.LicenseDialog;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.LicenseInfo;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.P2Task;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.log.ProgressLogMonitor;
import org.eclipse.emf.cdo.releng.setup.util.DownloadUtil;
import org.eclipse.emf.cdo.releng.setup.util.FileUtil;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.internal.p2.director.app.DirectorApplication;
import org.eclipse.equinox.internal.p2.director.app.ILog;
import org.eclipse.equinox.internal.p2.director.app.Messages;
import org.eclipse.equinox.internal.p2.director.app.PrettyQuery;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.internal.repository.tools.MirrorApplication;
import org.eclipse.equinox.p2.internal.repository.tools.RepositoryDescriptor;
import org.eclipse.equinox.p2.internal.repository.tools.SlicingOptions;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.ILicense;
import org.eclipse.equinox.p2.metadata.MetadataFactory;
import org.eclipse.equinox.p2.metadata.MetadataFactory.InstallableUnitDescription;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.RepositoryTracker;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Install Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl#getInstallableUnits <em>Installable Units</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl#getP2Repositories <em>P2 Repositories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl#isLicenseConfirmationDisabled <em>License Confirmation Disabled</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl#isMergeDisabled <em>Merge Disabled</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class P2TaskImpl extends SetupTaskImpl implements P2Task
{
  private static final boolean SKIP = "true".equals(System.getProperty(SetupConstants.PROP_P2_TASK_SKIP));

  private static final Class<DirectorApplication> DIRECTOR_CLASS = DirectorApplication.class;

  private static final Class<MirrorApplication> MIRROR_CLASS = MirrorApplication.class;

  private static final Object FIRST_CALL_DETECTION_KEY = new Object();

  private static final Integer EXIT_ERROR = 13;

  /**
   * The cached value of the '{@link #getInstallableUnits() <em>Installable Units</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstallableUnits()
   * @generated
   * @ordered
   */
  protected EList<InstallableUnit> installableUnits;

  /**
   * The cached value of the '{@link #getP2Repositories() <em>P2 Repositories</em>}' containment reference list.
   * <!-- begin-user-doc -->
      EList<SetupTask> setupTasks = preferences.getSetupTasks();
   * <!-- end-user-doc -->
   * @see #getP2Repositories()
   * @generated
   * @ordered
   */
  protected EList<P2Repository> p2Repositories;

  /**
   * The default value of the '{@link #isLicenseConfirmationDisabled() <em>License Confirmation Disabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isLicenseConfirmationDisabled()
   * @generated
   * @ordered
   */
  protected static final boolean LICENSE_CONFIRMATION_DISABLED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isLicenseConfirmationDisabled() <em>License Confirmation Disabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isLicenseConfirmationDisabled()
   * @generated
   * @ordered
   */
  protected boolean licenseConfirmationDisabled = LICENSE_CONFIRMATION_DISABLED_EDEFAULT;

  /**
   * The default value of the '{@link #isMergeDisabled() <em>Merge Disabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isMergeDisabled()
   * @generated
   * @ordered
   */
  protected static final boolean MERGE_DISABLED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isMergeDisabled() <em>Merge Disabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isMergeDisabled()
   * @generated
   * @ordered
   */
  protected boolean mergeDisabled = MERGE_DISABLED_EDEFAULT;

  private transient List<InstallableUnit> neededInstallableUnits;

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
      installableUnits = new EObjectContainmentEList.Resolving<InstallableUnit>(InstallableUnit.class, this,
          SetupPackage.P2_TASK__INSTALLABLE_UNITS);
    }
    return installableUnits;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isLicenseConfirmationDisabled()
  {
    return licenseConfirmationDisabled;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLicenseConfirmationDisabled(boolean newLicenseConfirmationDisabled)
  {
    boolean oldLicenseConfirmationDisabled = licenseConfirmationDisabled;
    licenseConfirmationDisabled = newLicenseConfirmationDisabled;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.P2_TASK__LICENSE_CONFIRMATION_DISABLED,
          oldLicenseConfirmationDisabled, licenseConfirmationDisabled));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isMergeDisabled()
  {
    return mergeDisabled;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMergeDisabled(boolean newMergeDisabled)
  {
    boolean oldMergeDisabled = mergeDisabled;
    mergeDisabled = newMergeDisabled;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.P2_TASK__MERGE_DISABLED, oldMergeDisabled,
          mergeDisabled));
    }
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
      p2Repositories = new EObjectContainmentEList.Resolving<P2Repository>(P2Repository.class, this,
          SetupPackage.P2_TASK__P2_REPOSITORIES);
    }
    return p2Repositories;
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
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      return ((InternalEList<?>)getInstallableUnits()).basicRemove(otherEnd, msgs);
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      return ((InternalEList<?>)getP2Repositories()).basicRemove(otherEnd, msgs);
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
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      return getInstallableUnits();
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      return getP2Repositories();
    case SetupPackage.P2_TASK__LICENSE_CONFIRMATION_DISABLED:
      return isLicenseConfirmationDisabled();
    case SetupPackage.P2_TASK__MERGE_DISABLED:
      return isMergeDisabled();
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
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      getInstallableUnits().clear();
      getInstallableUnits().addAll((Collection<? extends InstallableUnit>)newValue);
      return;
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      getP2Repositories().clear();
      getP2Repositories().addAll((Collection<? extends P2Repository>)newValue);
      return;
    case SetupPackage.P2_TASK__LICENSE_CONFIRMATION_DISABLED:
      setLicenseConfirmationDisabled((Boolean)newValue);
      return;
    case SetupPackage.P2_TASK__MERGE_DISABLED:
      setMergeDisabled((Boolean)newValue);
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
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      getInstallableUnits().clear();
      return;
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      getP2Repositories().clear();
      return;
    case SetupPackage.P2_TASK__LICENSE_CONFIRMATION_DISABLED:
      setLicenseConfirmationDisabled(LICENSE_CONFIRMATION_DISABLED_EDEFAULT);
      return;
    case SetupPackage.P2_TASK__MERGE_DISABLED:
      setMergeDisabled(MERGE_DISABLED_EDEFAULT);
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
    case SetupPackage.P2_TASK__INSTALLABLE_UNITS:
      return installableUnits != null && !installableUnits.isEmpty();
    case SetupPackage.P2_TASK__P2_REPOSITORIES:
      return p2Repositories != null && !p2Repositories.isEmpty();
    case SetupPackage.P2_TASK__LICENSE_CONFIRMATION_DISABLED:
      return licenseConfirmationDisabled != LICENSE_CONFIRMATION_DISABLED_EDEFAULT;
    case SetupPackage.P2_TASK__MERGE_DISABLED:
      return mergeDisabled != MERGE_DISABLED_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (licenseConfirmationDisabled: ");
    result.append(licenseConfirmationDisabled);
    result.append(", mergeDisabled: ");
    result.append(mergeDisabled);
    result.append(')');
    return result.toString();
  }

  private static Set<IInstallableUnit> getInstalledUnits()
  {
    Set<IInstallableUnit> result = new HashSet<IInstallableUnit>();
    ProvisioningUI provisioningUI = ProvisioningUI.getDefaultUI();
    ProvisioningSession session = provisioningUI.getSession();
    String profileId = provisioningUI.getProfileId();
    IProfile profile = ProvUI.getProfileRegistry(session).getProfile(profileId);
    IQueryResult<IInstallableUnit> queryResult = profile.query(QueryUtil.createIUAnyQuery(), null);

    for (IInstallableUnit installableUnit : queryResult)
    {
      result.add(installableUnit);
    }

    return result;
  }

  private static Set<String> getKnownRepositories()
  {
    Set<String> result = new HashSet<String>();
    ProvisioningUI provisioningUI = ProvisioningUI.getDefaultUI();
    ProvisioningSession session = provisioningUI.getSession();
    for (java.net.URI knowRepository : provisioningUI.getRepositoryTracker().getKnownRepositories(session))
    {
      result.add(knowRepository.toString());
    }

    return result;
  }

  @Override
  public Object getOverrideToken()
  {
    if (isMergeDisabled())
    {
      return super.getOverrideToken();
    }

    return getClass();
  }

  @Override
  public void overrideFor(SetupTask overriddenSetupTask)
  {
    super.overrideFor(overriddenSetupTask);

    P2Task overriddenP2Task = (P2Task)overriddenSetupTask;

    getInstallableUnits().addAll(overriddenP2Task.getInstallableUnits());
    getP2Repositories().addAll(overriddenP2Task.getP2Repositories());
  }

  @Override
  public void consolidate()
  {
    Set<String> installableUnitKeys = new HashSet<String>();
    for (Iterator<InstallableUnit> it = getInstallableUnits().iterator(); it.hasNext();)
    {
      InstallableUnit installableUnit = it.next();
      if (!installableUnitKeys.add(installableUnit.getID() + "->" + installableUnit.getVersionRange().toString()))
      {
        it.remove();
      }
    }

    Set<String> repositoryKeys = new HashSet<String>();
    for (Iterator<P2Repository> it = getP2Repositories().iterator(); it.hasNext();)
    {
      P2Repository p2Repository = it.next();
      if (!repositoryKeys.add(p2Repository.getURL()))
      {
        it.remove();
      }
    }
  }

  @Override
  public boolean needsBundlePool()
  {
    return true;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    if (SKIP)
    {
      return false;
    }

    if (!SetupConstants.SETUP_IDE)
    {
      return true;
    }

    Set<IInstallableUnit> installedUnits = getInstalledUnits();
    Trigger trigger = context.getTrigger();
    for (InstallableUnit installableUnit : getInstallableUnits())
    {
      String id = installableUnit.getID();
      VersionRange versionRange = installableUnit.getVersionRange();
      if (versionRange == null)
      {
        versionRange = VersionRange.emptyRange;
      }

      if (trigger != Trigger.MANUAL && isInstalled(installedUnits, id, versionRange))
      {
        continue;
      }

      if (neededInstallableUnits == null)
      {
        neededInstallableUnits = new ArrayList<InstallableUnit>();
      }

      neededInstallableUnits.add(installableUnit);
    }

    Set<String> knownRepositories = getKnownRepositories();
    for (P2Repository p2Repository : getP2Repositories())
    {
      String url = context.redirect(URI.createURI(p2Repository.getURL())).toString();
      if (!knownRepositories.contains(url))
      {
        return true;
      }
    }

    return neededInstallableUnits != null;
  }

  private boolean isInstalled(Set<IInstallableUnit> installedUnits, String id, VersionRange versionRange)
  {
    for (IInstallableUnit installedUnit : installedUnits)
    {
      if (id.equals(installedUnit.getId()) && versionRange.isIncluded(installedUnit.getVersion()))
      {
        return true;
      }
    }

    return false;
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    if (SetupConstants.SETUP_IDE)
    {
      ProgressLogMonitor monitor = new ProgressLogMonitor(context);

      ProvisioningUI provisioningUI = ProvisioningUI.getDefaultUI();
      ProvisioningSession session = provisioningUI.getSession();
      IMetadataRepositoryManager manager = (IMetadataRepositoryManager)session.getProvisioningAgent().getService(
          IMetadataRepositoryManager.SERVICE_NAME);
      RepositoryTracker repositoryTracker = provisioningUI.getRepositoryTracker();

      Set<String> knownRepositories = getKnownRepositories();
      Set<IMetadataRepository> repositories = new HashSet<IMetadataRepository>();
      List<java.net.URI> repos = new ArrayList<java.net.URI>();
      for (P2Repository p2Repository : getP2Repositories())
      {
        String url = context.redirect(URI.createURI(p2Repository.getURL())).toString();
        java.net.URI uri = new java.net.URI(url);
        context.log("Using repository " + uri);
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

      Set<IInstallableUnit> installedUnits = context.getTrigger() == Trigger.MANUAL ? getInstalledUnits() : null;
      if (neededInstallableUnits != null)
      {
        List<IInstallableUnit> toInstall = new ArrayList<IInstallableUnit>();
        for (InstallableUnit installableUnit : neededInstallableUnits)
        {
          IQuery<IInstallableUnit> iuQuery = QueryUtil.createIUQuery(installableUnit.getID(),
              installableUnit.getVersionRange());
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
            }
          }

          if (candidate != null)
          {
            if (installedUnits == null || !installedUnits.contains(candidate))
            {
              toInstall.add(candidate);
            }
          }
          else
          {
            // This will fail.
            // TODO
            InstallableUnitDescription installableUnitDescription = new InstallableUnitDescription();
            installableUnitDescription.setId(installableUnit.getID());
            toInstall.add(MetadataFactory.createInstallableUnit(installableUnitDescription));
          }
        }

        if (toInstall.isEmpty())
        {
          context.log("All units are up-to-date.");
          return;
        }

        context.log("Resolving...");
        InstallOperation installOperation = new InstallOperation(session, toInstall);
        String profileId = provisioningUI.getProfileId();
        installOperation.setProfileId(profileId);

        ProvisioningContext provisioningContext = makeProvisioningContext(session, repos);
        installOperation.setProvisioningContext(provisioningContext);

        IStatus status = installOperation.resolveModal(monitor);
        if (status.getSeverity() != IStatus.ERROR && status.getSeverity() != IStatus.CANCEL)
        {
          IProvisioningPlan provisioningPlan = installOperation.getProvisioningPlan();
          processLicenses(context, provisioningPlan, monitor);

          ProvisioningJob provisioningJob = installOperation.getProvisioningJob(null);
          provisioningJob.run(monitor);
        }
        else
        {
          context.log(status);
          throw new CoreException(Status.CANCEL_STATUS);
        }

        context.setRestartNeeded("New software has been installed.");
      }
    }
    else
    {
      callDirectorApp(context);
    }
  }

  private void processLicenses(final SetupTaskContext context, IProvisioningPlan provisioningPlan,
      IProgressMonitor monitor) throws Exception
  {
    if (isLicenseConfirmationDisabled())
    {
      return;
    }

    final Preferences preferences = context.getPreferences();
    Set<LicenseInfo> acceptedLicenses = new HashSet<LicenseInfo>(preferences.getAcceptedLicenses());

    final Map<ILicense, List<IInstallableUnit>> licensesToIUs = new HashMap<ILicense, List<IInstallableUnit>>();
    Set<Pair<ILicense, String>> set = new HashSet<Pair<ILicense, String>>();

    IQueryable<IInstallableUnit> queryable = provisioningPlan.getAdditions();
    IQueryResult<IInstallableUnit> result = queryable.query(QueryUtil.ALL_UNITS, monitor);
    for (IInstallableUnit iu : result)
    {
      Collection<ILicense> licenses = iu.getLicenses(null);
      for (ILicense license : licenses)
      {
        String uuid = license.getUUID();
        if (acceptedLicenses.contains(new LicenseInfo(uuid, null)))
        {
          continue;
        }

        String name = iu.getProperty(IInstallableUnit.PROP_NAME, null);
        if (name == null)
        {
          name = iu.getId();
        }

        if (!set.add(Pair.create(license, name)))
        {
          continue;
        }

        List<IInstallableUnit> ius = licensesToIUs.get(license);
        if (ius == null)
        {
          ius = new ArrayList<IInstallableUnit>();
          licensesToIUs.put(license, ius);
        }

        ius.add(iu);
      }
    }

    if (!licensesToIUs.isEmpty())
    {
      final Exception exception[] = { null };
      Display.getDefault().syncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            LicenseDialog dialog = new LicenseDialog(null, licensesToIUs);
            if (dialog.open() == LicenseDialog.OK)
            {
              if (dialog.isRememberAcceptedLicenses())
              {
                for (ILicense license : licensesToIUs.keySet())
                {
                  LicenseInfo licenseInfo = new LicenseInfo(license);
                  context.getPreferences().getAcceptedLicenses().add(licenseInfo);
                }
              }
            }
            else
            {
              throw new UnsupportedOperationException("Licenses have been declined");
            }
          }
          catch (Exception ex)
          {
            exception[0] = ex;
          }
        }
      });

      if (exception[0] != null)
      {
        throw exception[0];
      }
    }
  }

  private ProvisioningContext makeProvisioningContext(ProvisioningSession session, Collection<java.net.URI> repositories)
  {
    java.net.URI[] repos = repositories.toArray(new java.net.URI[repositories.size()]);
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
    if (context.put(FIRST_CALL_DETECTION_KEY, Boolean.TRUE) == null)
    {
      FileUtil.delete(new File(context.getEclipseDir(),
          "configuration/org.eclipse.equinox.simpleconfigurator/bundles.info"), new ProgressLogMonitor(context));
      FileUtil.delete(context.getP2ProfileDir(), new ProgressLogMonitor(context));
    }

    File eclipseDir = context.getEclipseDir();
    File iniFile = new File(eclipseDir, "eclipse.ini");
    boolean checkForDuplicates = iniFile.exists();

    String destination = eclipseDir.toString();

    final File p2PoolDir = context.getP2PoolDir();
    String bundlePool = p2PoolDir.toString();
    String bundleAgent = context.getP2AgentDir().toString();

    String os = context.getOS().getOsgiOS();
    String ws = context.getOS().getOsgiWS();
    String arch = context.getOS().getOsgiArch();

    EList<P2Repository> p2Repositories = getP2Repositories();
    EList<InstallableUnit> installableUnits = getInstallableUnits();

    context.log("Calling director to install " + installableUnits.size()
        + (installableUnits.size() == 1 ? " unit" : " units") + " from " + p2Repositories.size()
        + (p2Repositories.size() == 1 ? " repository" : " repositories") + " to " + destination);

    new File(destination).mkdirs();

    String repositories = makeList(context, p2Repositories, SetupPackage.Literals.P2_REPOSITORY__URL);
    for (String repository : repositories.split(","))
    {
      context.log("Using repository " + repository);
    }

    context.log("Executing p2 director...");

    String ius = makeList(context, installableUnits, SetupPackage.Literals.INSTALLABLE_UNIT__ID);

    String[] args = { "-destination", destination, "-repository", repositories, "-installIU", ius, "-profile",
        context.getP2ProfileName(), "-profileProperties", "org.eclipse.update.install.features=true", "-bundlepool",
        bundlePool, "-shared", bundleAgent, "-p2.os", os, "-p2.ws", ws, "-p2.arch", arch };

    DirectorApplication app = new DirectorApplication()
    {
      @Override
      public Object run(String[] args)
      {
        long time = System.currentTimeMillis();

        try
        {
          processArguments(args);
          initializeServices();

          IProvisioningAgent targetAgent = getTargetAgent();

          registerPlanner(context, targetAgent);
          initializeRepositories();
          initProfile(context, targetAgent);
          performProvisioningActions();

          context.log(NLS.bind(Messages.Operation_complete, new Long(System.currentTimeMillis() - time)));
          return IApplication.EXIT_OK;
        }
        catch (Exception ex)
        {
          Activator.log(ex);
          context.log(Messages.Operation_failed);
          context.log(ex);
          return EXIT_ERROR;
        }
        finally
        {
          cleanupRepositories();
          cleanupServices();
        }
      }

      @Override
      public void processArguments(String[] args) throws CoreException
      {
        super.processArguments(args);
        processIUs();
      }

      private IPlanner registerPlanner(final SetupTaskContext context, final IProvisioningAgent targetAgent)
      {
        IPlanner planner = new IPlanner()
        {
          IPlanner delegate = (IPlanner)targetAgent.getService(IPlanner.SERVICE_NAME);

          public IProvisioningPlan getProvisioningPlan(IProfileChangeRequest profileChangeRequest,
              ProvisioningContext provisioningContext, IProgressMonitor monitor)
          {
            IProvisioningPlan provisioningPlan = delegate.getProvisioningPlan(profileChangeRequest,
                provisioningContext, monitor);

            try
            {
              processLicenses(context, provisioningPlan, monitor);
            }
            catch (Exception ex)
            {
              throw new RuntimeException(ex);
            }

            return provisioningPlan;
          }

          public IProvisioningPlan getDiffPlan(IProfile currentProfile, IProfile targetProfile, IProgressMonitor monitor)
          {
            return delegate.getDiffPlan(currentProfile, targetProfile, monitor);
          }

          public IProfileChangeRequest createChangeRequest(IProfile profileToChange)
          {
            return delegate.createChangeRequest(profileToChange);
          }

          public IQueryResult<IInstallableUnit> updatesFor(IInstallableUnit iu, ProvisioningContext context,
              IProgressMonitor monitor)
          {
            return delegate.updatesFor(iu, context, monitor);
          }
        };

        targetAgent.registerService(IPlanner.SERVICE_NAME, planner);

        Field field = ReflectUtil.getField(DIRECTOR_CLASS, "planner");
        ReflectUtil.setValue(field, this, planner);

        return planner;
      }

      private void initProfile(final SetupTaskContext context, final IProvisioningAgent targetAgent)
      {
        final IProfileRegistry oldProfileRegistry = (IProfileRegistry)targetAgent
            .getService(IProfileRegistry.SERVICE_NAME);
        IProfileRegistry newProfileRegistry = new IProfileRegistry()
        {
          public IProfile getProfile(String id)
          {
            return oldProfileRegistry.getProfile(id);
          }

          public IProfile getProfile(String id, long timestamp)
          {
            return oldProfileRegistry.getProfile(id, timestamp);
          }

          public long[] listProfileTimestamps(String id)
          {
            return oldProfileRegistry.listProfileTimestamps(id);
          }

          public IProfile[] getProfiles()
          {
            return oldProfileRegistry.getProfiles();
          }

          public IProfile addProfile(String id) throws ProvisionException
          {
            return oldProfileRegistry.addProfile(id);
          }

          public IProfile addProfile(String id, Map<String, String> properties) throws ProvisionException
          {
            context.getOS().modifyProfileProperties(properties);

            // // Put the original registry back because it will be cast to SimpleProfileRegistry later on
            // targetAgent.registerService(IProfileRegistry.SERVICE_NAME, oldProfileRegistry);

            return oldProfileRegistry.addProfile(id, properties);
          }

          public boolean containsProfile(String profileId)
          {
            return oldProfileRegistry.containsProfile(profileId);
          }

          public void removeProfile(String id, long timestamp) throws ProvisionException
          {
            oldProfileRegistry.removeProfile(id, timestamp);
          }

          public void removeProfile(String id)
          {
            oldProfileRegistry.removeProfile(id);
          }

          public boolean isCurrent(IProfile profile)
          {
            return oldProfileRegistry.isCurrent(profile);
          }

          public IStatus setProfileStateProperties(String id, long timestamp, Map<String, String> properties)
          {
            return oldProfileRegistry.setProfileStateProperties(id, timestamp, properties);
          }

          public IStatus setProfileStateProperty(String id, long timestamp, String key, String value)
          {
            return oldProfileRegistry.setProfileStateProperty(id, timestamp, key, value);
          }

          public Map<String, String> getProfileStateProperties(String id, long timestamp)
          {
            return oldProfileRegistry.getProfileStateProperties(id, timestamp);
          }

          public Map<String, String> getProfileStateProperties(String id, String key)
          {
            return oldProfileRegistry.getProfileStateProperties(id, key);
          }

          public IStatus removeProfileStateProperties(String id, long timestamp, Collection<String> keys)
          {
            return oldProfileRegistry.removeProfileStateProperties(id, timestamp, keys);
          }
        };

        targetAgent.registerService(IProfileRegistry.SERVICE_NAME, newProfileRegistry);

        try
        {
          initializeProfile();
        }
        finally
        {
          targetAgent.registerService(IProfileRegistry.SERVICE_NAME, oldProfileRegistry);
        }
      }

      private void processIUs()
      {
        List<IQuery<IInstallableUnit>> rootsToInstall = getRootsToInstall();
        rootsToInstall.clear();

        for (InstallableUnit installableUnit : getInstallableUnits())
        {
          String id = installableUnit.getID();
          VersionRange versionRange = installableUnit.getVersionRange();
          if (versionRange == null)
          {
            versionRange = VersionRange.emptyRange;
          }

          IQuery<IInstallableUnit> query = new PrettyQuery<IInstallableUnit>(QueryUtil.createIUQuery(id,
              Version.emptyVersion.equals(versionRange) ? VersionRange.emptyRange : versionRange), id + " "
              + versionRange);
          rootsToInstall.add(query);
        }
      }

      private List<IQuery<IInstallableUnit>> getRootsToInstall()
      {
        Field field = ReflectUtil.getField(DIRECTOR_CLASS, "rootsToInstall");
        @SuppressWarnings("unchecked")
        List<IQuery<IInstallableUnit>> rootsToInstall = (List<IQuery<IInstallableUnit>>)ReflectUtil.getValue(field,
            this);
        return rootsToInstall;
      }

      private IProvisioningAgent getTargetAgent()
      {
        Field field = ReflectUtil.getField(DIRECTOR_CLASS, "targetAgent");
        return (IProvisioningAgent)ReflectUtil.getValue(field, this);
      }

      private IProfile initializeProfile()
      {
        Method method = ReflectUtil.getMethod(DIRECTOR_CLASS, "initializeProfile");
        return (IProfile)ReflectUtil.invokeMethod(method, this);
      }

      private void initializeServices()
      {
        Method method = ReflectUtil.getMethod(DIRECTOR_CLASS, "initializeServices");
        ReflectUtil.invokeMethod(method, this);
      }

      private void performProvisioningActions()
      {
        Method method = ReflectUtil.getMethod(DIRECTOR_CLASS, "performProvisioningActions");
        ReflectUtil.invokeMethod(method, this);
      }

      private void initializeRepositories()
      {
        Method method = ReflectUtil.getMethod(DIRECTOR_CLASS, "initializeRepositories");
        ReflectUtil.invokeMethod(method, this);
      }

      private void cleanupServices()
      {
        Method method = ReflectUtil.getMethod(DIRECTOR_CLASS, "cleanupServices");
        ReflectUtil.invokeMethod(method, this);
      }

      private void cleanupRepositories()
      {
        Method method = ReflectUtil.getMethod(DIRECTOR_CLASS, "cleanupRepositories");
        ReflectUtil.invokeMethod(method, this);
      }
    };

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
        context.log(status);
      }

      public void close()
      {
      }
    });

    Thread poolMonitorThread = new Thread("Bundle Pool Monitor")
    {
      private File featuresDir = new File(p2PoolDir, "features");

      private File pluginsDir = new File(p2PoolDir, "plugins");

      private Set<String> features = new HashSet<String>();

      private Set<String> plugins = new HashSet<String>();

      {
        if (featuresDir.isDirectory())
        {
          features.addAll(Arrays.asList(featuresDir.list()));
        }

        if (pluginsDir.isDirectory())
        {
          plugins.addAll(Arrays.asList(pluginsDir.list()));
        }

        setDaemon(true);
        start();
      }

      @Override
      public void run()
      {
        try
        {
          for (; !interrupted(); sleep(1000))
          {
            if (featuresDir.isDirectory())
            {
              for (String feature : featuresDir.list())
              {
                if (features.add(feature))
                {
                  context.log("Downloading feature " + feature);
                }
              }
            }

            if (pluginsDir.isDirectory())
            {
              for (String bundle : pluginsDir.list())
              {
                if (plugins.add(bundle))
                {
                  context.log("Downloading bundle " + bundle);
                }
              }
            }
          }
        }
        catch (Throwable ex)
        {
          // Ignore
        }
      }
    };

    Object exitCode = app.run(args);
    poolMonitorThread.interrupt();

    if (checkForDuplicates)
    {
      FileOutputStream out = null;
      try
      {
        String contents = DownloadUtil.load(context.getURIConverter(), URI.createFileURI(iniFile.toString()), null);
        Pattern section = Pattern.compile(
            "^(-vmargs)([\n\r]+.*)\\z|^(-[^\\n\\r]*[\\n\\r]*)((?:^[^-][^\\n\\r]*)*[\\n\\r]*)", Pattern.MULTILINE
                | Pattern.DOTALL);
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (Matcher matcher = section.matcher(contents); matcher.find();)
        {
          String argument = matcher.group(3);
          String extension;
          if (argument == null)
          {
            argument = matcher.group(1);
            extension = matcher.group(2);
          }
          else
          {
            extension = matcher.group(4);
          }

          if (!argument.startsWith("--launcher.XXMaxPermSize") || !map.containsKey(argument))
          {
            map.put(argument, extension);
          }
        }

        StringBuilder newContents = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet())
        {
          newContents.append(entry.getKey());
          newContents.append(entry.getValue());
        }

        out = new FileOutputStream(iniFile);
        out.write(newContents.toString().getBytes());
      }
      catch (IOException ex)
      {
        // Ignore.
      }
      finally
      {
        IOUtil.close(out);
      }
    }

    if (EXIT_ERROR.equals(exitCode))
    {
      throw new CoreException(Status.CANCEL_STATUS);
    }
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
      if (attribute == SetupPackage.Literals.P2_REPOSITORY__URL)
      {
        value = context.redirect(URI.createURI(value)).toString();
      }

      builder.append(value);
    }

    return builder.toString();
  }

  @Override
  public MirrorRunnable mirror(final MirrorContext context, final File mirrorsDir, boolean includingLocals)
      throws Exception
  {
    return new MirrorRunnable()
    {
      public void run(IProgressMonitor monitor) throws Exception
      {
        String targetURL = URI.createFileURI(context.getP2PoolTPDir().toString()).toString();

        SlicingOptions slicingOptions = new SlicingOptions();
        slicingOptions.latestVersionOnly(true);
        slicingOptions.everythingGreedy(false);

        MirrorApplication app = new MirrorApplication()
        {
          @Override
          public IStatus run(IProgressMonitor monitor) throws ProvisionException
          {
            IStatus mirrorStatus = Status.OK_STATUS;
            monitor.beginTask("", 1 + 100 + 1000 + 100);

            try
            {
              initializeRepos(new SubProgressMonitor(monitor, 1));
              initializeIUs();

              IQueryable<IInstallableUnit> slice = slice(new SubProgressMonitor(monitor, 100));

              mirrorStatus = mirrorArtifacts(slice, new SubProgressMonitor(monitor, 1000));
              mirrorMetadata(slice, new SubProgressMonitor(monitor, 100));
            }
            finally
            {
              finalizeRepositories();
            }

            if (mirrorStatus.isOK())
            {
              return Status.OK_STATUS;
            }

            return mirrorStatus;
          }

          private void initializeIUs()
          {
            Method method = ReflectUtil.getMethod(MIRROR_CLASS, "initializeIUs");
            ReflectUtil.invokeMethod(method, this);
          }

          @SuppressWarnings("unchecked")
          private IQueryable<IInstallableUnit> slice(IProgressMonitor monitor)
          {
            Method method = ReflectUtil.getMethod(MIRROR_CLASS, "slice", IProgressMonitor.class);
            return (IQueryable<IInstallableUnit>)ReflectUtil.invokeMethod(method, this, monitor);
          }

          private IStatus mirrorArtifacts(IQueryable<IInstallableUnit> slice, IProgressMonitor monitor)
          {
            Method method = ReflectUtil.getMethod(MIRROR_CLASS, "mirrorArtifacts", IQueryable.class,
                IProgressMonitor.class);
            return (IStatus)ReflectUtil.invokeMethod(method, this, slice, monitor);
          }

          private void mirrorMetadata(IQueryable<IInstallableUnit> slice, IProgressMonitor monitor)
          {
            Method method = ReflectUtil.getMethod(MIRROR_CLASS, "mirrorMetadata", IQueryable.class,
                IProgressMonitor.class);
            ReflectUtil.invokeMethod(method, this, slice, monitor);
          }
        };

        app.setIncludePacked(false);
        app.setVerbose(true);
        app.setSlicingOptions(slicingOptions);

        RepositoryDescriptor destination = new RepositoryDescriptor();
        destination.setLocation(new java.net.URI(targetURL));
        destination.setAppend(true);
        app.addDestination(destination);

        initSourceRepos(app, context, targetURL);
        initRootIUs(app);

        app.run(monitor);
      }

      private void initSourceRepos(MirrorApplication app, final MirrorContext context, String targetURL)
          throws URISyntaxException
      {
        for (P2Repository p2Repository : getP2Repositories())
        {
          String sourceURL = context.redirect(URI.createURI(p2Repository.getURL())).toString();

          RepositoryDescriptor descriptor = new RepositoryDescriptor();
          descriptor.setLocation(new java.net.URI(sourceURL));
          app.addSource(descriptor);

          context.addRedirection(sourceURL, targetURL);
        }
      }

      private void initRootIUs(MirrorApplication app)
      {
        EList<InstallableUnit> installableUnits = getInstallableUnits();
        String[] rootIUs = new String[installableUnits.size()];
        for (int i = 0; i < rootIUs.length; i++)
        {
          InstallableUnit installableUnit = installableUnits.get(i);
          rootIUs[i] = installableUnit.getID();

          VersionRange range = installableUnit.getVersionRange();
          if (!VersionRange.emptyRange.equals(range))
          {
            rootIUs[i] += "/" + range;
          }
        }

        Field field = ReflectUtil.getField(MIRROR_CLASS, "rootIUs");
        ReflectUtil.setValue(field, app, rootIUs);
      }
    };
  }
} // InstallTaskImpl
