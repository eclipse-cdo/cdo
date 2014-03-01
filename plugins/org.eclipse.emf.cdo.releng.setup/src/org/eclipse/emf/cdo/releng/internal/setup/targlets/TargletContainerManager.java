/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.targlets;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.setup.util.ServiceUtil;
import org.eclipse.emf.cdo.releng.setup.util.SetupUtil;

import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.engine.DownloadManager;
import org.eclipse.equinox.internal.p2.engine.InstallableUnitOperand;
import org.eclipse.equinox.internal.p2.engine.InstallableUnitPhase;
import org.eclipse.equinox.internal.p2.engine.Phase;
import org.eclipse.equinox.internal.p2.engine.PhaseSet;
import org.eclipse.equinox.internal.p2.engine.phases.Collect;
import org.eclipse.equinox.internal.p2.engine.phases.Install;
import org.eclipse.equinox.internal.p2.engine.phases.Property;
import org.eclipse.equinox.internal.p2.engine.phases.Uninstall;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IPhaseSet;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.engine.spi.ProvisioningAction;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRequest;
import org.eclipse.equinox.p2.repository.artifact.IFileArtifactRepository;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public final class TargletContainerManager extends P2
{

  public static final File POOL_FOLDER = new File(AGENT_FOLDER, "pool"); //$NON-NLS-1$

  private static final String WORKSPACE_LOCATION = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();

  private static final String WORKSPACE_STATE_RELATIVE_PATH = ".metadata/.plugins/" + Activator.PLUGIN_ID //$NON-NLS-1$
      + "/targlet-containers.state"; //$NON-NLS-1$

  private static final File WORKSPACE_STATE_FILE = new File(WORKSPACE_LOCATION, WORKSPACE_STATE_RELATIVE_PATH);

  private static final String TRUE = Boolean.TRUE.toString();

  private static TargletContainerManager instance;

  private final CountDownLatch initialized = new CountDownLatch(1);

  private Throwable initializationProblem;

  private Map<String, TargletContainerDescriptor> descriptors;

  private static final String NATIVE_ARTIFACTS = "nativeArtifacts"; //$NON-NLS-1$

  private static final String NATIVE_TYPE = "org.eclipse.equinox.p2.native"; //$NON-NLS-1$

  private static final String PARM_OPERAND = "operand"; //$NON-NLS-1$

  private TargletContainerManager() throws CoreException
  {
    new Job("Initialize Targlet Containers")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          initialize(monitor);
        }
        catch (Throwable t)
        {
          initializationProblem = t;
          Activator.log(t);
        }
        finally
        {
          initialized.countDown();
        }

        return Status.OK_STATUS;
      }
    }.schedule();
  }

  private void initialize(IProgressMonitor monitor) throws CoreException
  {
    if (WORKSPACE_STATE_FILE.exists())
    {
      try
      {
        descriptors = loadDescriptors(WORKSPACE_STATE_FILE);
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }

    if (descriptors == null)
    {
      descriptors = new HashMap<String, TargletContainerDescriptor>();
    }

    saveDescriptors(monitor);

    Set<String> workspaces = new HashSet<String>();
    workspaces.add(WORKSPACE_LOCATION);

    Set<String> workingDigests = new HashSet<String>();
    addWorkingDigests(workingDigests, descriptors);

    IProfileRegistry profileRegistry = getProfileRegistry();
    for (IProfile profile : profileRegistry.getProfiles())
    {
      try
      {
        String workspace = profile.getProperty(PROP_TARGLET_CONTAINER_WORKSPACE);
        if (workspace != null)
        {
          if (workspaces.add(workspace))
          {
            File file = new File(workspace, WORKSPACE_STATE_RELATIVE_PATH);
            if (file.exists())
            {
              Map<String, TargletContainerDescriptor> workspaceDescriptors = loadDescriptors(file);
              addWorkingDigests(workingDigests, workspaceDescriptors);
            }
          }

          String digest = profile.getProperty(PROP_TARGLET_CONTAINER_DIGEST);
          if (!workingDigests.contains(digest))
          {
            String profileID = profile.getProfileId();
            profileRegistry.removeProfile(profileID);
            Activator.log("Profile " + profileID + " for workspace " + workspace + " removed");
          }
        }
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }
  }

  private void waitUntilInitialized(IProgressMonitor monitor) throws CoreException
  {
    try
    {
      for (;;)
      {
        if (monitor.isCanceled())
        {
          throw new OperationCanceledException();
        }

        if (initialized.await(100, TimeUnit.MILLISECONDS))
        {
          break;
        }
      }
    }
    catch (InterruptedException ex)
    {
      throw new TimeoutRuntimeException(ex);
    }

    if (initializationProblem != null)
    {
      Activator.coreException(initializationProblem);
    }
  }

  public TargletContainerDescriptor getDescriptor(String id, IProgressMonitor monitor) throws CoreException
  {
    waitUntilInitialized(monitor);

    TargletContainerDescriptor descriptor = descriptors.get(id);
    if (descriptor == null)
    {
      descriptor = new TargletContainerDescriptor(id);
      descriptors.put(id, descriptor);
      saveDescriptors(monitor);
    }

    return descriptor;
  }

  public IProfile getProfile(String digest, IProgressMonitor monitor) throws CoreException
  {
    waitUntilInitialized(monitor);

    String profileID = getProfileID(digest);
    IProfileRegistry profileRegistry = getProfileRegistry();
    return profileRegistry.getProfile(profileID);
  }

  public IProfile getOrCreateProfile(String id, String environmentProperties, String nlProperty, String digest,
      IProgressMonitor monitor) throws CoreException
  {
    waitUntilInitialized(monitor);

    String profileID = getProfileID(digest);
    IProfileRegistry profileRegistry = getProfileRegistry();
    IProfile profile = profileRegistry.getProfile(profileID);
    if (profile == null)
    {
      Map<String, String> properties = new HashMap<String, String>();
      properties.put(PROP_TARGLET_CONTAINER_WORKSPACE, WORKSPACE_LOCATION);
      properties.put(PROP_TARGLET_CONTAINER_ID, id);
      properties.put(PROP_TARGLET_CONTAINER_DIGEST, digest);
      properties.put(IProfile.PROP_ENVIRONMENTS, environmentProperties);
      properties.put(IProfile.PROP_NL, nlProperty);
      properties.put(IProfile.PROP_CACHE, POOL_FOLDER.getAbsolutePath());
      properties.put(IProfile.PROP_INSTALL_FEATURES, TRUE);

      profile = profileRegistry.addProfile(profileID, properties);
    }

    return profile;
  }

  public IProfileChangeRequest createProfileChangeRequest(IProfile profile)
  {
    return getPlanner().createChangeRequest(profile);
  }

  public void planAndInstall(IProfileChangeRequest request, ProvisioningContext context, IProgressMonitor monitor)
      throws CoreException
  {
    IProvisioningPlan plan = getPlanner().getProvisioningPlan(request, context, monitor);
    if (!plan.getStatus().isOK())
    {
      throw new ProvisionException(plan.getStatus());
    }

    IPhaseSet phaseSet = createPhaseSet();

    @SuppressWarnings("restriction")
    IStatus status = org.eclipse.equinox.internal.provisional.p2.director.PlanExecutionHelper.executePlan(plan,
        getEngine(), phaseSet, context, monitor);

    if (!status.isOK())
    {
      throw new ProvisionException(status);
    }
  }

  public IFileArtifactRepository getBundlePool() throws CoreException
  {
    IArtifactRepositoryManager manager = getArtifactRepositoryManager();
    URI uri = POOL_FOLDER.toURI();

    try
    {
      if (manager.contains(uri))
      {
        return (IFileArtifactRepository)manager.loadRepository(uri, null);
      }
    }
    catch (CoreException ex)
    {
      //$FALL-THROUGH$
    }

    IArtifactRepository result = manager.createRepository(uri, "Shared Bundle Pool",
        IArtifactRepositoryManager.TYPE_SIMPLE_REPOSITORY, null);
    return (IFileArtifactRepository)result;
  }

  public void saveDescriptors(IProgressMonitor monitor)
  {
    // Remove unused descriptors
    Set<String> containerIDs = getContainerIDs(monitor);
    descriptors.keySet().retainAll(containerIDs);

    WORKSPACE_STATE_FILE.getParentFile().mkdirs();
    FileOutputStream out = null;

    try
    {
      out = new FileOutputStream(WORKSPACE_STATE_FILE);

      ObjectOutputStream stream = new ObjectOutputStream(out);
      stream.writeObject(descriptors);
      stream.close();
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.close(out);
    }

    IProfileRegistry profileRegistry = getProfileRegistry();
    for (IProfile profile : profileRegistry.getProfiles())
    {
      String workspace = profile.getProperty(PROP_TARGLET_CONTAINER_WORKSPACE);
      if (WORKSPACE_LOCATION.equals(workspace))
      {
        String id = profile.getProperty(PROP_TARGLET_CONTAINER_ID);
        TargletContainerDescriptor descriptor = descriptors.get(id);
        if (descriptor != null)
        {
          String workingDigest = descriptor.getWorkingDigest();
          if (workingDigest != null)
          {
            String digest = profile.getProperty(PROP_TARGLET_CONTAINER_DIGEST);
            if (workingDigest.equals(digest))
            {
              continue;
            }
          }
        }

        removeProfile(profile);
      }
    }
  }

  private static Set<String> getContainerIDs(IProgressMonitor monitor)
  {
    ITargetPlatformService service = null;

    try
    {
      service = ServiceUtil.getService(ITargetPlatformService.class);
      Set<String> ids = new HashSet<String>();

      for (ITargetHandle targetHandle : service.getTargets(monitor))
      {
        try
        {
          ITargetDefinition target = targetHandle.getTargetDefinition();
          ITargetLocation[] targetLocations = target.getTargetLocations();
          if (targetLocations != null)
          {
            for (ITargetLocation location : targetLocations)
            {
              if (location instanceof TargletContainer)
              {
                TargletContainer targletContainer = (TargletContainer)location;
                String id = targletContainer.getID();
                ids.add(id);
              }
            }
          }
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }

      return ids;
    }
    finally
    {
      ServiceUtil.ungetService(service);
    }
  }

  private static String getProfileID(String suffix)
  {
    return SetupUtil.encodePath(WORKSPACE_LOCATION) + "-" + suffix;
  }

  private static Map<String, TargletContainerDescriptor> loadDescriptors(File file)
  {
    FileInputStream in = null;

    try
    {
      in = new FileInputStream(file);
      ObjectInputStream stream = new ObjectInputStream(in);

      @SuppressWarnings("unchecked")
      Map<String, TargletContainerDescriptor> result = (Map<String, TargletContainerDescriptor>)stream.readObject();
      return result;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    catch (ClassNotFoundException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.close(in);
    }
  }

  private static void addWorkingDigests(Set<String> workingDigests, Map<String, TargletContainerDescriptor> descriptors)
  {
    for (TargletContainerDescriptor descriptor : descriptors.values())
    {
      workingDigests.add(descriptor.getWorkingDigest());
    }
  }

  private static IPhaseSet createPhaseSet()
  {
    ArrayList<Phase> phases = new ArrayList<Phase>(4);
    phases.add(new Collect(100));
    phases.add(new Property(1));
    phases.add(new Uninstall(50, true));
    phases.add(new Install(50));
    phases.add(new CollectNativesPhase(100));

    return new PhaseSet(phases.toArray(new Phase[phases.size()]));
  }

  public static synchronized TargletContainerManager getInstance() throws CoreException
  {
    if (instance == null)
    {
      instance = new TargletContainerManager();
    }

    return instance;
  }

  /**
   * @author Pascal Rapicault
   */
  private static final class CollectNativesPhase extends InstallableUnitPhase
  {
    public CollectNativesPhase(int weight)
    {
      super(NATIVE_ARTIFACTS, weight);
    }

    @Override
    protected List<ProvisioningAction> getActions(InstallableUnitOperand operand)
    {
      IInstallableUnit installableUnit = operand.second();
      if (installableUnit != null && installableUnit.getTouchpointType().getId().equals(NATIVE_TYPE))
      {
        List<ProvisioningAction> list = new ArrayList<ProvisioningAction>(1);
        list.add(new CollectNativesAction());
        return list;
      }

      return null;
    }

    @Override
    protected IStatus initializePhase(IProgressMonitor monitor, IProfile profile, Map<String, Object> parameters)
    {
      parameters.put(NATIVE_ARTIFACTS, new ArrayList<Object>());
      parameters.put(PARM_PROFILE, profile);
      return null;
    }

    @Override
    protected IStatus completePhase(IProgressMonitor monitor, IProfile profile, Map<String, Object> parameters)
    {
      @SuppressWarnings("unchecked")
      List<IArtifactRequest> artifactRequests = (List<IArtifactRequest>)parameters.get(NATIVE_ARTIFACTS);
      ProvisioningContext context = (ProvisioningContext)parameters.get(PARM_CONTEXT);
      IProvisioningAgent agent = (IProvisioningAgent)parameters.get(PARM_AGENT);
      DownloadManager downloadManager = new DownloadManager(context, agent);
      for (Iterator<IArtifactRequest> it = artifactRequests.iterator(); it.hasNext();)
      {
        downloadManager.add(it.next());
      }

      return downloadManager.start(monitor);
    }
  }

  /**
   * @author Pascal Rapicault
   */
  private static final class CollectNativesAction extends ProvisioningAction
  {
    @Override
    public IStatus execute(Map<String, Object> parameters)
    {
      InstallableUnitOperand operand = (InstallableUnitOperand)parameters.get(PARM_OPERAND);
      IInstallableUnit installableUnit = operand.second();
      if (installableUnit == null)
      {
        return Status.OK_STATUS;
      }

      try
      {
        Collection<?> toDownload = installableUnit.getArtifacts();
        if (toDownload == null)
        {
          return Status.OK_STATUS;
        }

        @SuppressWarnings("unchecked")
        List<IArtifactRequest> artifactRequests = (List<IArtifactRequest>)parameters.get(NATIVE_ARTIFACTS);

        IArtifactRepository artifactRepository = getInstance().getBundlePool();
        IArtifactRepositoryManager manager = getArtifactRepositoryManager();

        for (Iterator<?> it = toDownload.iterator(); it.hasNext();)
        {
          IArtifactKey keyToDownload = (IArtifactKey)it.next();
          IArtifactRequest request = manager.createMirrorRequest(keyToDownload, artifactRepository, null, null);
          artifactRequests.add(request);
        }
      }
      catch (CoreException ex)
      {
        return ex.getStatus();
      }

      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(Map<String, Object> parameters)
    {
      // Nothing to do for now
      return Status.OK_STATUS;
    }
  }
}
