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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.engine.Phase;
import org.eclipse.equinox.internal.p2.engine.PhaseSet;
import org.eclipse.equinox.internal.p2.engine.phases.Collect;
import org.eclipse.equinox.internal.p2.engine.phases.Install;
import org.eclipse.equinox.internal.p2.engine.phases.Property;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IEngine;
import org.eclipse.equinox.p2.engine.IPhaseSet;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IFileArtifactRepository;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public final class TargletContainerManager
{
  public static final File AGENT_FOLDER = new File(System.getProperty("user.home"), ".p2"); //$NON-NLS-1$

  public static final File POOL_FOLDER = new File(AGENT_FOLDER, "pool"); //$NON-NLS-1$

  private static String AGENT_FILTER = "(locationURI=" + AGENT_FOLDER.toURI() + ")"; //$NON-NLS-1$

  private static final String PROP_TARGLET_CONTAINER_WORKSPACE = "targlet.container.workspace"; //$NON-NLS-1$

  private static final String PROP_TARGLET_CONTAINER_ID = "targlet.container.id"; //$NON-NLS-1$

  private static final String PROP_TARGLET_CONTAINER_DIGEST = "targlet.container.digest"; //$NON-NLS-1$

  private static final String WORKSPACE_LOCATION = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();

  private static final String WORKSPACE_STATE_RELATIVE_PATH = ".metadata/.plugins/" + Activator.PLUGIN_ID //$NON-NLS-1$
      + "/targlet-containers.state"; //$NON-NLS-1$

  private static final File WORKSPACE_STATE_FILE = new File(WORKSPACE_LOCATION, WORKSPACE_STATE_RELATIVE_PATH);

  private static final String TRUE = Boolean.TRUE.toString();

  private static TargletContainerManager instance;

  private final CountDownLatch initialized = new CountDownLatch(1);

  private Throwable initializationProblem;

  private IProvisioningAgent agent;

  private IProfileRegistry profileRegistry;

  private IPlanner planner;

  private IEngine engine;

  private Map<String, TargletContainerDescriptor> descriptors;

  private TargletContainerManager() throws ProvisionException
  {
    BundleContext context = Activator.getBundleContext();

    try
    {
      Collection<ServiceReference<IProvisioningAgent>> ref = null;

      try
      {
        ref = context.getServiceReferences(IProvisioningAgent.class, AGENT_FILTER);
      }
      catch (InvalidSyntaxException ex)
      {
        // Can't happen because we write the filter ourselves
      }

      if (ref == null || ref.size() == 0)
      {
        throw new ProvisionException("Provisioning agent could not be loaded for " + AGENT_FOLDER);
      }

      agent = context.getService(ref.iterator().next());
      context.ungetService(ref.iterator().next());
    }
    catch (Exception ex)
    {
      AGENT_FOLDER.mkdirs();
      ServiceReference<IProvisioningAgentProvider> providerRef = context
          .getServiceReference(IProvisioningAgentProvider.class);

      try
      {
        IProvisioningAgentProvider provider = context.getService(providerRef);
        agent = provider.createAgent(AGENT_FOLDER.toURI());
      }
      finally
      {
        context.ungetService(providerRef);
      }
    }

    profileRegistry = (IProfileRegistry)agent.getService(IProfileRegistry.SERVICE_NAME);
    if (profileRegistry == null)
    {
      throw new ProvisionException("Profile registry could not be loaded");
    }

    planner = (IPlanner)agent.getService(IPlanner.SERVICE_NAME);
    if (planner == null)
    {
      throw new ProvisionException("Planner could not be loaded");
    }

    engine = (IEngine)agent.getService(IEngine.SERVICE_NAME);
    if (engine == null)
    {
      throw new ProvisionException("Engine could not be loaded");
    }

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

  private void initialize(IProgressMonitor monitor) throws ProvisionException
  {
    if (WORKSPACE_STATE_FILE.exists())
    {
      descriptors = loadDescriptors(WORKSPACE_STATE_FILE);
    }
    else
    {
      descriptors = new HashMap<String, TargletContainerDescriptor>();
    }

    saveDescriptors(monitor);

    Set<String> workspaces = new HashSet<String>();
    workspaces.add(WORKSPACE_LOCATION);

    Set<String> workingDigests = new HashSet<String>();
    addWorkingDigests(workingDigests, descriptors);

    for (IProfile profile : profileRegistry.getProfiles())
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
  }

  private void waitUntilInitialized(IProgressMonitor monitor) throws ProvisionException
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
      throwProvisionException(initializationProblem);
    }
  }

  public IProvisioningAgent getAgent()
  {
    return agent;
  }

  public TargletContainerDescriptor getDescriptor(String id, IProgressMonitor monitor) throws ProvisionException
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

  public IProfile getProfile(String digest, IProgressMonitor monitor) throws ProvisionException
  {
    waitUntilInitialized(monitor);

    String profileID = getProfileID(digest);
    return profileRegistry.getProfile(profileID);
  }

  public IProfile getOrCreateProfile(String id, String environmentProperties, String nlProperty, String digest,
      IProgressMonitor monitor) throws ProvisionException
  {
    waitUntilInitialized(monitor);

    String profileID = getProfileID(digest);
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
    return planner.createChangeRequest(profile);
  }

  public void planAndInstall(IProfileChangeRequest request, ProvisioningContext context, IProgressMonitor monitor)
      throws ProvisionException
  {
    IProvisioningPlan plan = planner.getProvisioningPlan(request, context, monitor);
    if (!plan.getStatus().isOK())
    {
      throw new ProvisionException(plan.getStatus());
    }

    IPhaseSet phaseSet = createPhaseSet();

    @SuppressWarnings("restriction")
    IStatus status = org.eclipse.equinox.internal.provisional.p2.director.PlanExecutionHelper.executePlan(plan, engine,
        phaseSet, context, monitor);

    if (!status.isOK())
    {
      throw new ProvisionException(status);
    }
  }

  public IFileArtifactRepository getBundlePool() throws ProvisionException
  {
    IArtifactRepositoryManager manager = (IArtifactRepositoryManager)agent
        .getService(IArtifactRepositoryManager.SERVICE_NAME);
    if (manager == null)
    {
      throw new ProvisionException("Artifact respository manager could not be loaded");
    }

    URI uri = POOL_FOLDER.toURI();

    try
    {
      if (manager.contains(uri))
      {
        return (IFileArtifactRepository)manager.loadRepository(uri, null);
      }
    }
    catch (ProvisionException ex)
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

  private void removeProfile(IProfile profile)
  {
    String profileID = profile.getProfileId();
    profileRegistry.removeProfile(profileID);
    Activator.log("Profile " + profileID + " removed");
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
          ITargetDefinition definition = targetHandle.getTargetDefinition();
          for (ITargetLocation targetLocation : definition.getTargetLocations())
          {
            if (targetLocation instanceof TargletContainer)
            {
              TargletContainer targletContainer = (TargletContainer)targetLocation;
              String id = targletContainer.getID();
              ids.add(id);
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
    List<Phase> phases = new ArrayList<Phase>(4);
    phases.add(new Collect(100));
    phases.add(new Property(1));
    phases.add(new Install(50));
    // phases.add(new CollectNativesPhase(100));

    return new PhaseSet(phases.toArray(new Phase[phases.size()]));
  }

  public static void throwProvisionException(Throwable t) throws ProvisionException
  {
    if (t instanceof ProvisionException)
    {
      throw (ProvisionException)t;
    }

    if (t instanceof Error)
    {
      throw (Error)t;
    }

    throw new ProvisionException(t.getMessage(), t);
  }

  public static synchronized TargletContainerManager getInstance() throws ProvisionException
  {
    if (instance == null)
    {
      instance = new TargletContainerManager();
    }

    return instance;
  }
}
