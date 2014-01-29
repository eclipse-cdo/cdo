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
import org.eclipse.emf.cdo.releng.setup.util.SetupUtil;

import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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

  private static final String WORKSPACE_RELATIVE_PROPERTIES = ".metadata/.plugins/" + Activator.PLUGIN_ID //$NON-NLS-1$
      + "/targlet-container.properties"; //$NON-NLS-1$

  private static final String WORKSPACE_LOCATION = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();

  private static final File WORKSPACE_PROPERTIES_FILE = new File(WORKSPACE_LOCATION, WORKSPACE_RELATIVE_PROPERTIES);

  private static TargletContainerManager instance;

  private final CountDownLatch initialized = new CountDownLatch(1);

  private Throwable initializationProblem;

  private IProvisioningAgent agent;

  private IProfileRegistry profileRegistry;

  private Map<String, TargletContainerDescriptor> descriptors = new HashMap<String, TargletContainerDescriptor>();

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

    new Job("Initialize Targlet Containers")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          // initialize(monitor);
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

  // public synchronized IProfile getProfile(TargletContainer container, AtomicBoolean needsUpdate,
  // IProgressMonitor monitor) throws ProvisionException
  // {
  // waitUntilInitialized();
  //
  // String digest = container.getDigest();
  // String profileID = getProfileID(digest);
  //
  // IProfile profile = profileRegistry.getProfile(profileID);
  // if (profile == null)
  // {
  // Map<String, String> properties = new HashMap<String, String>();
  // properties.put(PROP_TARGLET_CONTAINER_WORKSPACE, WORKSPACE_LOCATION);
  // properties.put(PROP_TARGLET_CONTAINER_DIGEST, digest);
  // properties.put(IProfile.PROP_ENVIRONMENTS, container.getEnvironmentProperties());
  // properties.put(IProfile.PROP_NL, container.getNLProperty());
  // properties.put(IProfile.PROP_CACHE, POOL_FOLDER.getAbsolutePath());
  // properties.put(IProfile.PROP_INSTALL_FEATURES, Boolean.TRUE.toString());
  //
  // profile = profileRegistry.addProfile(profileID, properties);
  //
  // // initialize(monitor);
  // }
  //
  // if (needsUpdate != null)
  // {
  // long[] timestamps = profileRegistry.listProfileTimestamps(profile.getProfileId());
  // needsUpdate.set(timestamps == null || timestamps.length <= 1);
  // }
  //
  // return profile;
  // }
  //
  // private void initialize(IProgressMonitor monitor) throws ProvisionException
  // {
  // HashBag<String> currentDigests = collectCurrentDigests(monitor);
  // removeGarbageProfiles(currentDigests);
  // }
  //
  // private HashBag<String> collectCurrentDigests(IProgressMonitor monitor) throws ProvisionException
  // {
  // HashBag<String> digests = new HashBag<String>();
  //
  // @SuppressWarnings("restriction")
  // ITargetPlatformService targetService = (ITargetPlatformService)org.eclipse.pde.internal.core.PDECore.getDefault()
  // .acquireService(ITargetPlatformService.class.getName());
  //
  // for (ITargetHandle targetHandle : targetService.getTargets(monitor))
  // {
  // try
  // {
  // ITargetDefinition definition = targetHandle.getTargetDefinition();
  // for (ITargetLocation targetLocation : definition.getTargetLocations())
  // {
  // if (targetLocation instanceof TargletContainer)
  // {
  // TargletContainer targletContainer = (TargletContainer)targetLocation;
  // String digest = targletContainer.getDigest();
  // digests.add(digest);
  // }
  // }
  // }
  // catch (Exception ex)
  // {
  // throwProvisionException(ex);
  // }
  // }
  //
  // writeDigests(WORKSPACE_PROPERTIES_FILE, digests);
  // return digests;
  // }
  //
  // private void removeGarbageProfiles(HashBag<String> currentDigests)
  // {
  // Map<String, HashBag<String>> workspaces = new HashMap<String, HashBag<String>>();
  // workspaces.put(WORKSPACE_LOCATION, currentDigests);
  //
  // for (IProfile profile : profileRegistry.getProfiles())
  // {
  // String workspace = profile.getProperty(PROP_TARGLET_CONTAINER_WORKSPACE);
  // if (workspace != null)
  // {
  // HashBag<String> workspaceDigests = workspaces.get(workspace);
  // if (workspaceDigests == null)
  // {
  // File file = new File(workspace, WORKSPACE_RELATIVE_PROPERTIES);
  // if (!file.exists())
  // {
  // removeProfile(profile, workspace);
  // continue;
  // }
  //
  // workspaceDigests = readDigests(file);
  // workspaces.put(workspace, workspaceDigests);
  // }
  //
  // String digest = profile.getProperty(PROP_TARGLET_CONTAINER_DIGEST);
  // if (!workspaceDigests.contains(digest))
  // {
  // removeProfile(profile, workspace);
  // }
  // }
  // }
  // }
  //
  // private void removeProfile(IProfile profile, String workspace)
  // {
  // String profileID = profile.getProfileId();
  // profileRegistry.removeProfile(profileID);
  // Activator.log("Profile " + profileID + " for workspace " + workspace + " removed");
  // }

  private void waitUntilInitialized() throws ProvisionException
  {
    try
    {
      initialized.await();
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

  public synchronized TargletContainerDescriptor getDescriptor(String id) throws ProvisionException
  {
    waitUntilInitialized();

    TargletContainerDescriptor descriptor = descriptors.get(id);
    if (descriptor == null)
    {
      descriptor = new TargletContainerDescriptor(id);
      descriptors.put(id, descriptor);
    }

    saveDescriptors();
    return descriptor;
  }

  public IProfile getProfile(String digest, IProgressMonitor monitor) throws ProvisionException
  {
    waitUntilInitialized();

    String profileID = getProfileID(digest);
    return profileRegistry.getProfile(profileID);
  }

  private String getProfileID(String digest)
  {
    return SetupUtil.encodePath(WORKSPACE_LOCATION) + "-" + digest;
  }

  public IProfile createProfile(String id, String digest, String environmentProperties, String nlProperty,
      IProgressMonitor monitor) throws ProvisionException
  {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put(PROP_TARGLET_CONTAINER_WORKSPACE, WORKSPACE_LOCATION);
    properties.put(PROP_TARGLET_CONTAINER_ID, id);
    properties.put(PROP_TARGLET_CONTAINER_DIGEST, digest);
    properties.put(IProfile.PROP_ENVIRONMENTS, environmentProperties);
    properties.put(IProfile.PROP_NL, nlProperty);
    properties.put(IProfile.PROP_CACHE, POOL_FOLDER.getAbsolutePath());
    properties.put(IProfile.PROP_INSTALL_FEATURES, Boolean.TRUE.toString());

    String profileID = getProfileID(digest);
    return profileRegistry.addProfile(profileID, properties);
  }

  private void saveDescriptors()
  {
    WORKSPACE_PROPERTIES_FILE.getParentFile().mkdirs();
    FileOutputStream out = null;

    try
    {
      out = new FileOutputStream(WORKSPACE_PROPERTIES_FILE);

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
  }

  @SuppressWarnings("unused")
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

  // public synchronized IProfile getProfile(TargletContainer container, AtomicBoolean needsUpdate,
  // IProgressMonitor monitor) throws ProvisionException
  // {
  // waitUntilInitialized();
  //
  // String digest = container.getDigest();
  // String profileID = getProfileID(digest);
  //
  // IProfile profile = profileRegistry.getProfile(profileID);
  // if (profile == null)
  // {
  // Map<String, String> properties = new HashMap<String, String>();
  // properties.put(PROP_TARGLET_CONTAINER_WORKSPACE, WORKSPACE_LOCATION);
  // properties.put(PROP_TARGLET_CONTAINER_DIGEST, digest);
  // properties.put(IProfile.PROP_ENVIRONMENTS, container.getEnvironmentProperties());
  // properties.put(IProfile.PROP_NL, container.getNLProperty());
  // properties.put(IProfile.PROP_CACHE, POOL_FOLDER.getAbsolutePath());
  // properties.put(IProfile.PROP_INSTALL_FEATURES, Boolean.TRUE.toString());
  //
  // profile = profileRegistry.addProfile(profileID, properties);
  //
  // // initialize(monitor);
  // }
  //
  // if (needsUpdate != null)
  // {
  // long[] timestamps = profileRegistry.listProfileTimestamps(profile.getProfileId());
  // needsUpdate.set(timestamps == null || timestamps.length <= 1);
  // }
  //
  // return profile;
  // }
  //
  // private void initialize(IProgressMonitor monitor) throws ProvisionException
  // {
  // HashBag<String> currentDigests = collectCurrentDigests(monitor);
  // removeGarbageProfiles(currentDigests);
  // }
  //
  // private HashBag<String> collectCurrentDigests(IProgressMonitor monitor) throws ProvisionException
  // {
  // HashBag<String> digests = new HashBag<String>();
  //
  // @SuppressWarnings("restriction")
  // ITargetPlatformService targetService = (ITargetPlatformService)org.eclipse.pde.internal.core.PDECore.getDefault()
  // .acquireService(ITargetPlatformService.class.getName());
  //
  // for (ITargetHandle targetHandle : targetService.getTargets(monitor))
  // {
  // try
  // {
  // ITargetDefinition definition = targetHandle.getTargetDefinition();
  // for (ITargetLocation targetLocation : definition.getTargetLocations())
  // {
  // if (targetLocation instanceof TargletContainer)
  // {
  // TargletContainer targletContainer = (TargletContainer)targetLocation;
  // String digest = targletContainer.getDigest();
  // digests.add(digest);
  // }
  // }
  // }
  // catch (Exception ex)
  // {
  // throwProvisionException(ex);
  // }
  // }
  //
  // writeDigests(WORKSPACE_PROPERTIES_FILE, digests);
  // return digests;
  // }
  //
  // private void removeGarbageProfiles(HashBag<String> currentDigests)
  // {
  // Map<String, HashBag<String>> workspaces = new HashMap<String, HashBag<String>>();
  // workspaces.put(WORKSPACE_LOCATION, currentDigests);
  //
  // for (IProfile profile : profileRegistry.getProfiles())
  // {
  // String workspace = profile.getProperty(PROP_TARGLET_CONTAINER_WORKSPACE);
  // if (workspace != null)
  // {
  // HashBag<String> workspaceDigests = workspaces.get(workspace);
  // if (workspaceDigests == null)
  // {
  // File file = new File(workspace, WORKSPACE_RELATIVE_PROPERTIES);
  // if (!file.exists())
  // {
  // removeProfile(profile, workspace);
  // continue;
  // }
  //
  // workspaceDigests = readDigests(file);
  // workspaces.put(workspace, workspaceDigests);
  // }
  //
  // String digest = profile.getProperty(PROP_TARGLET_CONTAINER_DIGEST);
  // if (!workspaceDigests.contains(digest))
  // {
  // removeProfile(profile, workspace);
  // }
  // }
  // }
  // }
  //
  // private void removeProfile(IProfile profile, String workspace)
  // {
  // String profileID = profile.getProfileId();
  // profileRegistry.removeProfile(profileID);
  // Activator.log("Profile " + profileID + " for workspace " + workspace + " removed");
  // }
  //
  // private static void writeDigests(File file, HashBag<String> digests)
  // {
  // file.getParentFile().mkdirs();
  // FileWriter out = null;
  //
  // try
  // {
  // out = new FileWriter(file);
  // BufferedWriter writer = new BufferedWriter(out);
  //
  // List<String> list = new ArrayList<String>(digests);
  // Collections.sort(list);
  //
  // for (String digest : list)
  // {
  // String line = digest + "=" + digests.getCounterFor(digest);
  // writer.write(line);
  // writer.newLine();
  // }
  //
  // writer.close();
  // }
  // catch (IOException ex)
  // {
  // throw new IORuntimeException(ex);
  // }
  // finally
  // {
  // IOUtil.close(out);
  // }
  // }
  //
  // private static HashBag<String> readDigests(File file)
  // {
  // Reader in = null;
  //
  // try
  // {
  // in = new FileReader(file);
  // BufferedReader reader = new BufferedReader(in);
  //
  // HashBag<String> digests = new HashBag<String>();
  // String line;
  //
  // while ((line = reader.readLine()) != null)
  // {
  // int pos = line.indexOf('=');
  // String digest = line.substring(0, pos).trim();
  // String count = line.substring(pos + 1).trim();
  //
  // digests.add(digest, Integer.valueOf(count));
  // }
  //
  // return digests;
  // }
  // catch (IOException ex)
  // {
  // throw new IORuntimeException(ex);
  // }
  // finally
  // {
  // IOUtil.close(in);
  // }
  // }

  static void throwProvisionException(Throwable t) throws ProvisionException
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
