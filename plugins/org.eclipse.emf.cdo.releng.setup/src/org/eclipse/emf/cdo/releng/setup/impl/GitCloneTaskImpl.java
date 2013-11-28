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
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl#getRemoteName <em>Remote Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl#getRemoteURI <em>Remote URI</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl#getUserID <em>User ID</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl#getCheckoutBranch <em>Checkout Branch</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GitCloneTaskImpl extends SetupTaskImpl implements GitCloneTask
{
  private static final String[] REQUIRED_IUS = { "org.eclipse.egit.feature.group" };

  private static final String[] REQUIRED_REPOSITORIES = { "${train.url}" };

  /**
   * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocation()
   * @generated
   * @ordered
   */
  protected static final String LOCATION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocation()
   * @generated
   * @ordered
   */
  protected String location = LOCATION_EDEFAULT;

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
   * The default value of the '{@link #getUserID() <em>User ID</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserID()
   * @generated
   * @ordered
   */
  protected static final String USER_ID_EDEFAULT = "${git.user.id}";

  /**
   * The cached value of the '{@link #getUserID() <em>User ID</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #getUserID()
   * @generated
   * @ordered
   */
  protected String userID = USER_ID_EDEFAULT;

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

  private transient GitDelegate gitDelegate;

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
  public String getLocation()
  {
    return location;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setLocation(String newLocation)
  {
    String oldLocation = location;
    location = newLocation;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.GIT_CLONE_TASK__LOCATION, oldLocation,
          location));
    }
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
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.GIT_CLONE_TASK__REMOTE_NAME, oldRemoteName,
          remoteName));
    }
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
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.GIT_CLONE_TASK__REMOTE_URI, oldRemoteURI,
          remoteURI));
    }
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
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.GIT_CLONE_TASK__CHECKOUT_BRANCH,
          oldCheckoutBranch, checkoutBranch));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getUserID()
  {
    return userID;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setUserID(String newUserID)
  {
    String oldUserID = userID;
    userID = newUserID;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.GIT_CLONE_TASK__USER_ID, oldUserID, userID));
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
    case SetupPackage.GIT_CLONE_TASK__LOCATION:
      return getLocation();
    case SetupPackage.GIT_CLONE_TASK__REMOTE_NAME:
      return getRemoteName();
    case SetupPackage.GIT_CLONE_TASK__REMOTE_URI:
      return getRemoteURI();
    case SetupPackage.GIT_CLONE_TASK__USER_ID:
      return getUserID();
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
    case SetupPackage.GIT_CLONE_TASK__LOCATION:
      setLocation((String)newValue);
      return;
    case SetupPackage.GIT_CLONE_TASK__REMOTE_NAME:
      setRemoteName((String)newValue);
      return;
    case SetupPackage.GIT_CLONE_TASK__REMOTE_URI:
      setRemoteURI((String)newValue);
      return;
    case SetupPackage.GIT_CLONE_TASK__USER_ID:
      setUserID((String)newValue);
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
    case SetupPackage.GIT_CLONE_TASK__LOCATION:
      setLocation(LOCATION_EDEFAULT);
      return;
    case SetupPackage.GIT_CLONE_TASK__REMOTE_NAME:
      setRemoteName(REMOTE_NAME_EDEFAULT);
      return;
    case SetupPackage.GIT_CLONE_TASK__REMOTE_URI:
      setRemoteURI(REMOTE_URI_EDEFAULT);
      return;
    case SetupPackage.GIT_CLONE_TASK__USER_ID:
      setUserID(USER_ID_EDEFAULT);
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
    case SetupPackage.GIT_CLONE_TASK__LOCATION:
      return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
    case SetupPackage.GIT_CLONE_TASK__REMOTE_NAME:
      return REMOTE_NAME_EDEFAULT == null ? remoteName != null : !REMOTE_NAME_EDEFAULT.equals(remoteName);
    case SetupPackage.GIT_CLONE_TASK__REMOTE_URI:
      return REMOTE_URI_EDEFAULT == null ? remoteURI != null : !REMOTE_URI_EDEFAULT.equals(remoteURI);
    case SetupPackage.GIT_CLONE_TASK__USER_ID:
      return USER_ID_EDEFAULT == null ? userID != null : !USER_ID_EDEFAULT.equals(userID);
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
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (location: ");
    result.append(location);
    result.append(", remoteName: ");
    result.append(remoteName);
    result.append(", remoteURI: ");
    result.append(remoteURI);
    result.append(", userID: ");
    result.append(userID);
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

  @Override
  protected String[] getRequiredInstallableUnits()
  {
    return REQUIRED_IUS;
  }

  @Override
  protected String[] getRequiredP2Repositories()
  {
    return REQUIRED_REPOSITORIES;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    gitDelegate = GitUtil.create();
    return gitDelegate.isNeeded(context, getLocation(), getCheckoutBranch(), getRemoteName());
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    if (gitDelegate != null)
    {
      gitDelegate.perform(context, getCheckoutBranch(), getRemoteName(), context
          .redirect(URI.createURI(getRemoteURI())).toString(), getUserID());
    }
  }

  @Override
  public void dispose()
  {
    if (gitDelegate != null)
    {
      gitDelegate.dispose();
      gitDelegate = null;
    }
  }

  /**
   * @author Ed Merks
   */
  private interface GitDelegate
  {
    public boolean isNeeded(SetupTaskContext context, String name, String checkoutBranch, String remoteName)
        throws Exception;

    public void perform(SetupTaskContext context, String checkoutBranch, String remoteName, String remoteURI,
        String userID) throws Exception;

    public void dispose();
  }

  /**
   * @author Ed Merks
   */
  private static class GitUtil implements GitDelegate
  {
    private File workDir;

    private boolean hasCheckout;

    private Git cachedGit;

    private Repository cachedRepository;

    private static GitDelegate create()
    {
      return new GitUtil();
    }

    public boolean isNeeded(SetupTaskContext context, String location, String checkoutBranch, String remoteName)
        throws Exception
    {
      workDir = new File(location);
      if (!workDir.isDirectory())
      {
        return true;
      }

      if (workDir.list().length > 1)
      {
        return false;
      }

      context.log("Opening Git clone " + workDir);

      Git git = Git.open(workDir);
      if (!GitUtil.hasWorkTree(git))
      {
        FileUtil.rename(workDir);
        return true;
      }

      Repository repository = git.getRepository();
      GitUtil.configureRepository(context, repository, checkoutBranch, remoteName);

      hasCheckout = repository.getAllRefs().containsKey("refs/heads/" + checkoutBranch);
      if (!hasCheckout)
      {
        cachedGit = git;
        cachedRepository = repository;
        return true;
      }

      return false;
    }

    public void perform(SetupTaskContext context, String checkoutBranch, String remoteName, String remoteURI,
        String userID) throws Exception
    {
      if (cachedGit == null)
      {
        cachedGit = GitUtil.cloneRepository(context, workDir, checkoutBranch, remoteName, remoteURI, userID);
        cachedRepository = cachedGit.getRepository();
        if (!URI.createURI(remoteURI).isFile())
        {
          GitUtil.configureRepository(context, cachedRepository, checkoutBranch, remoteName);
        }
      }

      if (!hasCheckout)
      {
        GitUtil.createBranch(context, cachedGit, checkoutBranch, remoteName);
        GitUtil.checkout(context, cachedGit, checkoutBranch);
        GitUtil.resetHard(context, cachedGit);
      }
    }

    public void dispose()
    {
      if (cachedRepository != null)
      {
        cachedRepository.close();
      }
    }

    private static boolean hasWorkTree(Git git) throws Exception
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

    private static Git cloneRepository(SetupTaskContext context, File workDir, String checkoutBranch,
        String remoteName, String remoteURI, String userID) throws Exception
    {
      String remote;

      URI baseURI = URI.createURI(remoteURI);
      if (baseURI.isFile())
      {
        remote = baseURI.toString();
      }
      else
      {
        String scheme = baseURI.scheme();
        String[] segments = baseURI.segments();

        if (ANONYMOUS.equals(userID))
        {
          if (baseURI.port() != null)
          {
            scheme = "git";

            String[] newSegments = new String[1 + segments.length];
            newSegments[0] = "gitroot";
            System.arraycopy(segments, 0, newSegments, 1, segments.length);
            segments = newSegments;
          }

          remote = URI.createHierarchicalURI(scheme, baseURI.host(), baseURI.device(), segments, baseURI.query(),
              baseURI.fragment()).toString();
        }
        else
        {
          remote = URI.createHierarchicalURI(scheme, userID + "@" + baseURI.authority(), baseURI.device(), segments,
              baseURI.query(), baseURI.fragment()).toString();
        }
      }

      context.log("Cloning Git repo " + remote + " to " + workDir);

      CloneCommand command = Git.cloneRepository();
      command.setNoCheckout(true);
      command.setURI(remote);
      command.setRemote(remoteName);
      command.setBranchesToClone(Collections.singleton(checkoutBranch));
      command.setDirectory(workDir);
      command.setTimeout(10);
      command.setProgressMonitor(new ProgressLogWrapper(context));
      return command.call();
    }

    private static void configureRepository(SetupTaskContext context, Repository repository, String checkoutBranch,
        String remoteName) throws Exception, IOException
    {
      StoredConfig config = repository.getConfig();

      boolean changed = false;
      changed |= configureLineEndingConversion(context, config);
      changed |= addPushRefSpec(context, config, checkoutBranch, remoteName);
      if (changed)
      {
        config.save();
      }
    }

    private static boolean configureLineEndingConversion(SetupTaskContext context, StoredConfig config)
        throws Exception
    {
      if (context.getOS().isLineEndingConversionNeeded())
      {
        if (context.isPerforming())
        {
          context.log("Setting " + ConfigConstants.CONFIG_KEY_AUTOCRLF + " = true");
        }

        config.setEnum(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_AUTOCRLF, AutoCRLF.TRUE);
        return true;
      }

      return false;
    }

    private static boolean addPushRefSpec(SetupTaskContext context, StoredConfig config, String checkoutBranch,
        String remoteName) throws Exception
    {
      String gerritQueue = "refs/for/" + checkoutBranch;
      for (RemoteConfig remoteConfig : RemoteConfig.getAllRemoteConfigs(config))
      {
        if (remoteName.equals(remoteConfig.getName()))
        {
          List<RefSpec> pushRefSpecs = remoteConfig.getPushRefSpecs();
          if (hasGerritPushRefSpec(pushRefSpecs, gerritQueue))
          {
            return false;
          }

          RefSpec refSpec = new RefSpec("HEAD:" + gerritQueue);

          if (context.isPerforming())
          {
            context.log("Adding push ref spec: " + refSpec);
          }

          remoteConfig.addPushRefSpec(refSpec);
          remoteConfig.update(config);
          return true;
        }
      }

      return false;
    }

    private static boolean hasGerritPushRefSpec(List<RefSpec> pushRefSpecs, String gerritQueue)
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

    private static void createBranch(SetupTaskContext context, Git git, String checkoutBranch, String remoteName)
        throws Exception
    {
      context.log("Creating local branch " + checkoutBranch);

      CreateBranchCommand command = git.branchCreate();
      command.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM);
      command.setName(checkoutBranch);
      command.setStartPoint("refs/remotes/" + remoteName + "/" + checkoutBranch);
      command.call();

      StoredConfig config = git.getRepository().getConfig();
      config.setBoolean(ConfigConstants.CONFIG_BRANCH_SECTION, checkoutBranch, ConfigConstants.CONFIG_KEY_REBASE, true);
      config.save();
    }

    private static void checkout(SetupTaskContext context, Git git, String checkoutBranch) throws Exception
    {
      context.log("Checking out local branch " + checkoutBranch);

      CheckoutCommand command = git.checkout();
      command.setName(checkoutBranch);
      command.call();
    }

    private static void resetHard(SetupTaskContext context, Git git) throws Exception
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
  }

} // GitCloneTaskImpl
