/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.FileUtil;
import org.eclipse.emf.cdo.releng.setup.util.TargetPlatformUtil;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogMonitor;

import org.eclipse.net4j.util.io.FileLock;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.materializer.MaterializationContext;
import org.eclipse.buckminster.core.materializer.MaterializationJob;
import org.eclipse.buckminster.core.metadata.model.BillOfMaterials;
import org.eclipse.buckminster.core.mspec.builder.MaterializationSpecBuilder;
import org.eclipse.buckminster.core.mspec.model.MaterializationSpec;
import org.eclipse.buckminster.core.parser.IParser;
import org.eclipse.buckminster.core.query.model.ComponentQuery;
import org.eclipse.buckminster.core.resolver.IResolver;
import org.eclipse.buckminster.core.resolver.MainResolver;
import org.eclipse.buckminster.core.resolver.ResolutionContext;
import org.eclipse.buckminster.download.DownloadManager;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.publisher.AbstractPublisherApplication;
import org.eclipse.equinox.p2.publisher.eclipse.FeaturesAndBundlesPublisherApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Buckminster Import Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl#getMspec <em>Mspec</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl#getTargetPlatform <em>Target Platform</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl#getBundlePool <em>Bundle Pool</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BuckminsterImportTaskImpl extends SetupTaskImpl implements BuckminsterImportTask
{
  /**
   * The default value of the '{@link #getMspec() <em>Mspec</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMspec()
   * @generated
   * @ordered
   */
  protected static final String MSPEC_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getMspec() <em>Mspec</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMspec()
   * @generated
   * @ordered
   */
  protected String mspec = MSPEC_EDEFAULT;

  /**
   * The default value of the '{@link #getTargetPlatform() <em>Target Platform</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetPlatform()
   * @generated
   * @ordered
   */
  protected static final String TARGET_PLATFORM_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTargetPlatform() <em>Target Platform</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetPlatform()
   * @generated
   * @ordered
   */
  protected String targetPlatform = TARGET_PLATFORM_EDEFAULT;

  /**
   * The default value of the '{@link #getBundlePool() <em>Bundle Pool</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBundlePool()
   * @generated
   * @ordered
   */
  protected static final String BUNDLE_POOL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getBundlePool() <em>Bundle Pool</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBundlePool()
   * @generated
   * @ordered
   */
  protected String bundlePool = BUNDLE_POOL_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BuckminsterImportTaskImpl()
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
    return SetupPackage.Literals.BUCKMINSTER_IMPORT_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getMspec()
  {
    return mspec;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMspec(String newMspec)
  {
    String oldMspec = mspec;
    mspec = newMspec;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC, oldMspec,
          mspec));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getTargetPlatform()
  {
    return targetPlatform;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTargetPlatform(String newTargetPlatform)
  {
    String oldTargetPlatform = targetPlatform;
    targetPlatform = newTargetPlatform;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BUCKMINSTER_IMPORT_TASK__TARGET_PLATFORM,
          oldTargetPlatform, targetPlatform));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getBundlePool()
  {
    return bundlePool;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBundlePool(String newBundlePool)
  {
    String oldBundlePool = bundlePool;
    bundlePool = newBundlePool;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BUCKMINSTER_IMPORT_TASK__BUNDLE_POOL,
          oldBundlePool, bundlePool));
    }
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      return getMspec();
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__TARGET_PLATFORM:
      return getTargetPlatform();
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__BUNDLE_POOL:
      return getBundlePool();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      setMspec((String)newValue);
      return;
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__TARGET_PLATFORM:
      setTargetPlatform((String)newValue);
      return;
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__BUNDLE_POOL:
      setBundlePool((String)newValue);
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      setMspec(MSPEC_EDEFAULT);
      return;
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__TARGET_PLATFORM:
      setTargetPlatform(TARGET_PLATFORM_EDEFAULT);
      return;
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__BUNDLE_POOL:
      setBundlePool(BUNDLE_POOL_EDEFAULT);
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      return MSPEC_EDEFAULT == null ? mspec != null : !MSPEC_EDEFAULT.equals(mspec);
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__TARGET_PLATFORM:
      return TARGET_PLATFORM_EDEFAULT == null ? targetPlatform != null : !TARGET_PLATFORM_EDEFAULT
          .equals(targetPlatform);
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__BUNDLE_POOL:
      return BUNDLE_POOL_EDEFAULT == null ? bundlePool != null : !BUNDLE_POOL_EDEFAULT.equals(bundlePool);
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
    result.append(" (mspec: ");
    result.append(mspec);
    result.append(", targetPlatform: ");
    result.append(targetPlatform);
    result.append(", bundlePool: ");
    result.append(bundlePool);
    result.append(')');
    return result.toString();
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  public boolean isNeeded(SetupTaskContext context)
  {
    return context.getTrigger() == Trigger.MANUAL
        || !TargetPlatformUtil.hasTargetPlatform(context.getTargetPlatformDir().toString(), context);
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    IProgressMonitor monitor = new ProgressLogMonitor(context);
    monitor.beginTask("Starting Buckminster import", 160);

    try
    {
      File tpOld = null;
      File tp = context.getTargetPlatformDir();
      if (tp.exists())
      {
        tpOld = new File(tp.getParentFile(), tp.getName() + "." + System.currentTimeMillis());
        FileUtil.rename(tp, tpOld);
      }

      FileLock tpPoolLock = null;
      boolean isAutoBuilding = disableAutoBuilding();

      try
      {
        File tpPool = updateTargetPlatformPool(context);
        tpPoolLock = FileLock.forWrite(tpPool);

        File targetPlatformLocation = context.getTargetPlatformDir();
        targetPlatformLocation.mkdirs();
        TargetPlatformUtil.setTargetPlatform(targetPlatformLocation.toString(), context.getSetup().getBranch()
            .getProject().getName()
            + " Target", true, context);

        BuckminsterHelper.materialize(context, getMspec(), monitor);

        if (tpPoolLock != null)
        {
          updateBundlePool(context, tp.getAbsolutePath(), tpPool.getAbsolutePath());
        }

        if (context.isCancelled())
        {
          throw new InterruptedException();
        }
      }
      catch (Exception ex)
      {
        File tpBroken = new File(tp.getParentFile(), tp.getName() + "." + System.currentTimeMillis());
        FileUtil.rename(tp, tpBroken);
        if (tpOld != null)
        {
          FileUtil.rename(tpOld, tp);
        }

        FileUtil.deleteAsync(tpBroken);
        throw ex;
      }
      finally
      {
        try
        {
          if (tpPoolLock != null)
          {
            tpPoolLock.release();
          }
        }
        finally
        {
          restoreAutoBuilding(isAutoBuilding);
        }
      }

      if (tpOld != null)
      {
        FileUtil.deleteAsync(tpOld);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public static boolean disableAutoBuilding() throws CoreException
  {
    boolean autoBuilding = ResourcesPlugin.getWorkspace().isAutoBuilding();
    if (autoBuilding)
    {
      restoreAutoBuilding(false);
    }

    return autoBuilding;
  }

  public static void restoreAutoBuilding(boolean autoBuilding) throws CoreException
  {
    if (autoBuilding != ResourcesPlugin.getWorkspace().isAutoBuilding())
    {
      IWorkspaceDescription description = ResourcesPlugin.getWorkspace().getDescription();
      description.setAutoBuilding(autoBuilding);

      ResourcesPlugin.getWorkspace().setDescription(description);
    }
  }

  private static File updateTargetPlatformPool(SetupTaskContext context) throws Exception
  {
    File installFolder = new File(context.getSetup().getPreferences().getInstallFolder());

    File idePool = new File(installFolder, ".p2pool-ide");
    idePool.mkdirs();

    File tpPool = new File(installFolder, ".p2pool-tp");
    tpPool.mkdirs();

    FileLock idePoolLock = FileLock.forWrite(idePool);

    try
    {
      updateBundlePool(context, idePool.getAbsolutePath(), tpPool.getAbsolutePath());
    }
    finally
    {
      idePoolLock.release();
    }

    return tpPool;
  }

  private static void updateBundlePool(SetupTaskContext context, String source, String bundlePool) throws Exception
  {
    context.log("Updating bundle pool: " + bundlePool);

    String bundlePoolURL = "file:/" + bundlePool.replace('\\', '/');
    String[] args = { "-source", source, "-metadataRepository", bundlePoolURL, "-artifactRepository", bundlePoolURL,
        "-append", "-publishArtifacts" };

    AbstractPublisherApplication publisher = new FeaturesAndBundlesPublisherApplication();
    publisher.run(args);
  }

  private static class BuckminsterHelper
  {
    private static MaterializationSpec getMSpec(URL mspecURL, IProgressMonitor monitor) throws Exception
    {
      monitor.subTask("Downloading MSpec");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DownloadManager.readInto(mspecURL, null, baos, MonitorUtils.subMonitor(monitor, 20));

      monitor.subTask("Parsing MSpec");
      IParser<MaterializationSpec> parser = CorePlugin.getDefault().getParserFactory()
          .getMaterializationSpecParser(true);
      return parser.parse(mspecURL.toString(), new ByteArrayInputStream(baos.toByteArray()));
    }

    private static ComponentQuery getCQuery(URL cqueryURL, IProgressMonitor monitor) throws Exception
    {
      monitor.subTask("Downloading CQuery");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DownloadManager.readInto(cqueryURL, null, baos, MonitorUtils.subMonitor(monitor, 20));

      monitor.subTask("Parsing CQuery");
      return ComponentQuery.fromStream(cqueryURL, null, new ByteArrayInputStream(baos.toByteArray()), true);
    }

    private static void materialize(SetupTaskContext context, String mSpec, IProgressMonitor monitor)
        throws MalformedURLException, Exception
    {
      URL mSpecURL = new URL(context.expandString(mSpec));
      MaterializationSpec mspec = BuckminsterHelper.getMSpec(mSpecURL, monitor); // 20 ticks
      ComponentQuery cquery = BuckminsterHelper.getCQuery(mspec.getResolvedURL(), monitor); // 20 ticks

      IResolver resolver = new MainResolver(new ResolutionContext(mspec, cquery));
      resolver.getContext().setContinueOnError(true);

      monitor.subTask("Resolving components");
      BillOfMaterials bom = resolver.resolve(MonitorUtils.subMonitor(monitor, 40));

      MaterializationSpecBuilder mspecBuilder = new MaterializationSpecBuilder();
      mspecBuilder.initFrom(mspec);
      mspecBuilder.setName(bom.getViewName());

      bom.addMaterializationNodes(mspecBuilder);

      ResolutionContext resolutionContext = new ResolutionContext(bom.getQuery());
      MaterializationContext materializationContext = new MaterializationContext(bom, mspec, resolutionContext);

      monitor.subTask("Materializing components");
      MaterializationJob job = new MaterializationJob(materializationContext);
      job.run(MonitorUtils.subMonitor(monitor, 80));
    }
  }

} // BuckminsterImportTaskImpl
