/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.GitCloneTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.FileUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.CoreConfig.AutoCRLF;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Git Clone Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl#getRemoteName <em>Remote Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl#getRemoteURI <em>Remote URI</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl#getCheckoutBranch <em>Checkout Branch</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GitCloneTaskImpl extends SetupTaskImpl implements GitCloneTask
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getRemoteName() <em>Remote Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRemoteName()
   * @generated
   * @ordered
   */
  protected static final String REMOTE_NAME_EDEFAULT = "origin";

  /**
   * The cached value of the '{@link #getRemoteName() <em>Remote Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRemoteName()
   * @generated
   * @ordered
   */
  protected String remoteName = REMOTE_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getRemoteURI() <em>Remote URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRemoteURI()
   * @generated
   * @ordered
   */
  protected static final String REMOTE_URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRemoteURI() <em>Remote URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRemoteURI()
   * @generated
   * @ordered
   */
  protected String remoteURI = REMOTE_URI_EDEFAULT;

  /**
   * The default value of the '{@link #getCheckoutBranch() <em>Checkout Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCheckoutBranch()
   * @generated
   * @ordered
   */
  protected static final String CHECKOUT_BRANCH_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getCheckoutBranch() <em>Checkout Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCheckoutBranch()
   * @generated
   * @ordered
   */
  protected String checkoutBranch = CHECKOUT_BRANCH_EDEFAULT;

  private transient Object cachedGit;

  private transient Object cachedRepository;

  private transient File workDir;

  private transient boolean hasCheckout;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GitCloneTaskImpl()
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
    return SetupPackage.Literals.GIT_CLONE_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.GIT_CLONE_TASK__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getRemoteName()
  {
    return remoteName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRemoteName(String newRemoteName)
  {
    String oldRemoteName = remoteName;
    remoteName = newRemoteName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.GIT_CLONE_TASK__REMOTE_NAME, oldRemoteName,
          remoteName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getRemoteURI()
  {
    return remoteURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRemoteURI(String newRemoteURI)
  {
    String oldRemoteURI = remoteURI;
    remoteURI = newRemoteURI;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.GIT_CLONE_TASK__REMOTE_URI, oldRemoteURI,
          remoteURI));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getCheckoutBranch()
  {
    return checkoutBranch;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCheckoutBranch(String newCheckoutBranch)
  {
    String oldCheckoutBranch = checkoutBranch;
    checkoutBranch = newCheckoutBranch;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.GIT_CLONE_TASK__CHECKOUT_BRANCH,
          oldCheckoutBranch, checkoutBranch));
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
    case SetupPackage.GIT_CLONE_TASK__NAME:
      return getName();
    case SetupPackage.GIT_CLONE_TASK__REMOTE_NAME:
      return getRemoteName();
    case SetupPackage.GIT_CLONE_TASK__REMOTE_URI:
      return getRemoteURI();
    case SetupPackage.GIT_CLONE_TASK__CHECKOUT_BRANCH:
      return getCheckoutBranch();
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
    case SetupPackage.GIT_CLONE_TASK__NAME:
      setName((String)newValue);
      return;
    case SetupPackage.GIT_CLONE_TASK__REMOTE_NAME:
      setRemoteName((String)newValue);
      return;
    case SetupPackage.GIT_CLONE_TASK__REMOTE_URI:
      setRemoteURI((String)newValue);
      return;
    case SetupPackage.GIT_CLONE_TASK__CHECKOUT_BRANCH:
      setCheckoutBranch((String)newValue);
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
    case SetupPackage.GIT_CLONE_TASK__NAME:
      setName(NAME_EDEFAULT);
      return;
    case SetupPackage.GIT_CLONE_TASK__REMOTE_NAME:
      setRemoteName(REMOTE_NAME_EDEFAULT);
      return;
    case SetupPackage.GIT_CLONE_TASK__REMOTE_URI:
      setRemoteURI(REMOTE_URI_EDEFAULT);
      return;
    case SetupPackage.GIT_CLONE_TASK__CHECKOUT_BRANCH:
      setCheckoutBranch(CHECKOUT_BRANCH_EDEFAULT);
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
    case SetupPackage.GIT_CLONE_TASK__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case SetupPackage.GIT_CLONE_TASK__REMOTE_NAME:
      return REMOTE_NAME_EDEFAULT == null ? remoteName != null : !REMOTE_NAME_EDEFAULT.equals(remoteName);
    case SetupPackage.GIT_CLONE_TASK__REMOTE_URI:
      return REMOTE_URI_EDEFAULT == null ? remoteURI != null : !REMOTE_URI_EDEFAULT.equals(remoteURI);
    case SetupPackage.GIT_CLONE_TASK__CHECKOUT_BRANCH:
      return CHECKOUT_BRANCH_EDEFAULT == null ? checkoutBranch != null : !CHECKOUT_BRANCH_EDEFAULT
          .equals(checkoutBranch);
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
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", remoteName: ");
    result.append(remoteName);
    result.append(", remoteURI: ");
    result.append(remoteURI);
    result.append(", checkoutBranch: ");
    result.append(checkoutBranch);
    result.append(')');
    return result.toString();
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    workDir = getWorkDir(context);
    if (!workDir.isDirectory())
    {
      return true;
    }

    context.log("Opening Git clone " + workDir);

    Git git = Git.open(workDir);
    if (!hasWorkTree(git))
    {
      FileUtil.rename(workDir);
      return true;
    }

    Repository repository = git.getRepository();
    configureRepository(context, repository);

    hasCheckout = repository.getAllRefs().containsKey("refs/heads/" + getCheckoutBranch());
    if (!hasCheckout)
    {
      cachedGit = git;
      cachedRepository = repository;
      return true;
    }

    return false;
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    Git git = (Git)cachedGit;
    Repository repository = (Repository)cachedRepository;

    if (git == null)
    {
      git = cloneRepository(context, workDir, getCheckoutBranch());
      repository = git.getRepository();
      configureRepository(context, repository);
    }

    if (!hasCheckout)
    {
      createBranch(context, git, getCheckoutBranch());
      checkout(context, git, getCheckoutBranch());
      resetHard(context, git);
    }
  }

  @Override
  public void dispose()
  {
    if (cachedRepository != null)
    {
      ((Repository)cachedRepository).close();
    }
  }

  private File getWorkDir(SetupTaskContext context)
  {
    File gitDir = new File(context.getBranchDir(), "git");
    return new File(gitDir, getName());
  }

  private boolean hasWorkTree(Git git) throws Exception
  {
    try
    {
      StatusCommand statusCommand = git.status();
      statusCommand.call();
      return true;
    }
    catch (NoWorkTreeException ex)
    {
      return false;
    }
  }

  private Git cloneRepository(SetupTaskContext context, File workDir, String checkoutBranch) throws Exception
  {
    URI baseURI = URI.createURI(getRemoteURI());
    String remote = URI.createHierarchicalURI(baseURI.scheme(),
        context.getSetup().getPreferences().getUserName() + "@" + baseURI.authority(), baseURI.device(),
        baseURI.segments(), baseURI.query(), baseURI.fragment()).toString();

    context.log("Cloning Git repo " + remote + " to " + workDir);

    CloneCommand command = Git.cloneRepository();
    command.setNoCheckout(true);
    command.setURI(remote);
    command.setRemote(getRemoteName());
    command.setBranchesToClone(Collections.singleton(checkoutBranch));
    command.setDirectory(workDir);
    command.setTimeout(10);
    command.setProgressMonitor(new ProgressLogWrapper(context));
    return command.call();
  }

  private void configureRepository(SetupTaskContext context, Repository repository) throws Exception, IOException
  {
    StoredConfig config = repository.getConfig();

    boolean changed = false;
    changed |= configureLineEndingConversion(context, config);
    changed |= addPushRefSpec(context, config);
    if (changed)
    {
      config.save();
    }
  }

  private boolean configureLineEndingConversion(SetupTaskContext context, StoredConfig config) throws Exception
  {
    if (context.getOS().isLineEndingConversionNeeded())
    {
      context.log("Setting " + ConfigConstants.CONFIG_KEY_AUTOCRLF + " = true");
      config.setEnum(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_AUTOCRLF, AutoCRLF.TRUE);
      return true;
    }

    return false;
  }

  private boolean addPushRefSpec(SetupTaskContext context, StoredConfig config) throws Exception
  {
    String gerritQueue = "refs/for/" + getCheckoutBranch();
    for (RemoteConfig remoteConfig : RemoteConfig.getAllRemoteConfigs(config))
    {
      if (getRemoteName().equals(remoteConfig.getName()))
      {
        List<RefSpec> pushRefSpecs = remoteConfig.getPushRefSpecs();
        if (hasGerritPushRefSpec(pushRefSpecs, gerritQueue))
        {
          return false;
        }

        RefSpec refSpec = new RefSpec("HEAD:" + gerritQueue);
        context.log("Adding push ref spec: " + refSpec);

        remoteConfig.addPushRefSpec(refSpec);
        remoteConfig.update(config);
        return true;
      }
    }

    return false;
  }

  private boolean hasGerritPushRefSpec(List<RefSpec> pushRefSpecs, String gerritQueue)
  {
    for (RefSpec refSpec : pushRefSpecs)
    {
      if (refSpec.getDestination().equals(gerritQueue))
      {
        return true;
      }
    }

    return false;
  }

  private void createBranch(SetupTaskContext context, Git git, String checkoutBranch) throws Exception
  {
    context.log("Creating local branch " + checkoutBranch);

    CreateBranchCommand command = git.branchCreate();
    command.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM);
    command.setName(checkoutBranch);
    command.setStartPoint("refs/remotes/origin/" + checkoutBranch);
    command.call();
  }

  private void checkout(SetupTaskContext context, Git git, String checkoutBranch) throws Exception
  {
    context.log("Checking out local branch " + checkoutBranch);

    CheckoutCommand command = git.checkout();
    command.setName(checkoutBranch);
    command.call();
  }

  private void resetHard(SetupTaskContext context, Git git) throws Exception
  {
    context.log("Resetting hard");

    ResetCommand command = git.reset();
    command.setMode(ResetType.HARD);
    command.call();
  }

  /**
   * @author Eike Stepper
   */
  private static final class ProgressLogWrapper implements ProgressMonitor
  {
    private SetupTaskContext context;

    public ProgressLogWrapper(SetupTaskContext context)
    {
      this.context = context;
    }

    public void update(int completed)
    {
    }

    public void start(int totalTasks)
    {
    }

    public boolean isCancelled()
    {
      return context.isCancelled();
    }

    public void endTask()
    {
    }

    public void beginTask(String title, int totalWork)
    {
      context.log(title);
    }
  }

} // GitCloneTaskImpl
