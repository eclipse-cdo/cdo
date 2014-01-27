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

import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eike Stepper
 */
public final class TargletProfileManager
{
  public static final File AGENT_FOLDER = new File(System.getProperty("user.home"), ".p2"); //$NON-NLS-1$

  public static final File POOL_FOLDER = new File(AGENT_FOLDER, "pool"); //$NON-NLS-1$

  private static String AGENT_FILTER = "(locationURI=" + AGENT_FOLDER.toURI() + ")"; //$NON-NLS-1$

  private static final String PROP_TARGLET_CONTAINER_WORKSPACE = "targlet.container.workspace"; //$NON-NLS-1$

  private static final String PROP_TARGLET_CONTAINER_DIGEST = "targlet.container.digest"; //$NON-NLS-1$

  private static final String PROP_ARCH = "osgi.arch"; //$NON-NLS-1$

  private static final String PROP_OS = "osgi.os"; //$NON-NLS-1$

  private static final String PROP_WS = "osgi.ws"; //$NON-NLS-1$

  private static final String WORKSPACE_RELATIVE_PROPERTIES = ".metadata/.plugins/" + Activator.PLUGIN_ID //$NON-NLS-1$
      + "/targlet-container.properties"; //$NON-NLS-1$

  private static final String WORKSPACE_LOCATION = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();

  private static final File WORKSPACE_PROPERTIES_FILE = new File(WORKSPACE_LOCATION, WORKSPACE_RELATIVE_PROPERTIES);

  private static TargletProfileManager instance;

  private final CountDownLatch initialized = new CountDownLatch(1);

  private Throwable initializationProblem;

  private IProvisioningAgent agent;

  private IProfileRegistry profileRegistry;

  private HashBag<String> digests;

  private TargletProfileManager() throws ProvisionException
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

  public IProvisioningAgent getAgent()
  {
    return agent;
  }

  public synchronized IProfile getProfile(TargletBundleContainer container, AtomicBoolean needsUpdate)
      throws ProvisionException
  {
    waitUntilInitialized();

    String digest = container.getDigest();
    String profileID = SetupUtil.encodePath(WORKSPACE_LOCATION) + "-" + digest;

    IProfile profile = profileRegistry.getProfile(profileID);
    if (profile == null)
    {
      Map<String, String> properties = new HashMap<String, String>();
      properties.put(PROP_TARGLET_CONTAINER_WORKSPACE, WORKSPACE_LOCATION);
      properties.put(PROP_TARGLET_CONTAINER_DIGEST, digest);
      properties.put(IProfile.PROP_CACHE, POOL_FOLDER.getAbsolutePath());
      properties.put(IProfile.PROP_INSTALL_FEATURES, Boolean.TRUE.toString());
      properties.put(IProfile.PROP_ENVIRONMENTS, generateEnvironmentProperties(container.getTarget()));
      properties.put(IProfile.PROP_NL, generateNLProperty(container.getTarget()));

      profile = profileRegistry.addProfile(profileID, properties);

      digests.add(digest);
      writeDigests(WORKSPACE_PROPERTIES_FILE, digests);
    }

    if (needsUpdate != null)
    {
      long[] timestamps = profileRegistry.listProfileTimestamps(profile.getProfileId());
      needsUpdate.set(timestamps == null || timestamps.length <= 1);
    }

    return profile;
  }

  private void initialize(IProgressMonitor monitor) throws Exception
  {
    collectDigests(monitor);
    removeGarbageProfiles();
  }

  private void collectDigests(IProgressMonitor monitor) throws CoreException
  {
    digests = new HashBag<String>();

    @SuppressWarnings("restriction")
    ITargetPlatformService targetService = (ITargetPlatformService)org.eclipse.pde.internal.core.PDECore.getDefault()
        .acquireService(ITargetPlatformService.class.getName());

    for (ITargetHandle targetHandle : targetService.getTargets(monitor))
    {
      ITargetDefinition definition = targetHandle.getTargetDefinition();
      for (ITargetLocation targetLocation : definition.getTargetLocations())
      {
        if (targetLocation instanceof TargletBundleContainer)
        {
          TargletBundleContainer targletContainer = (TargletBundleContainer)targetLocation;
          String digest = targletContainer.getDigest();
          digests.add(digest);
        }
      }
    }

    writeDigests(WORKSPACE_PROPERTIES_FILE, digests);
  }

  private void removeGarbageProfiles()
  {
    Map<String, HashBag<String>> workspaces = new HashMap<String, HashBag<String>>();
    workspaces.put(WORKSPACE_LOCATION, digests);

    for (IProfile profile : profileRegistry.getProfiles())
    {
      String workspace = profile.getProperty(PROP_TARGLET_CONTAINER_WORKSPACE);
      if (workspace != null)
      {
        HashBag<String> workspaceDigests = workspaces.get(workspace);
        if (workspaceDigests == null)
        {
          File file = new File(workspace, WORKSPACE_RELATIVE_PROPERTIES);
          if (!file.exists())
          {
            removeProfile(profile, workspace);
            continue;
          }

          workspaceDigests = readDigests(file);
          workspaces.put(workspace, workspaceDigests);
        }

        String digest = profile.getProperty(PROP_TARGLET_CONTAINER_DIGEST);
        if (!workspaceDigests.contains(digest))
        {
          removeProfile(profile, workspace);
        }
      }
    }
  }

  private void removeProfile(IProfile profile, String workspace)
  {
    String profileID = profile.getProfileId();
    profileRegistry.removeProfile(profileID);
    Activator.log("Profile " + profileID + " for workspace " + workspace + " removed");
  }

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

  private static String generateEnvironmentProperties(ITargetDefinition target)
  {
    StringBuilder builder = new StringBuilder();
    String ws = target.getWS();
    if (ws == null)
    {
      ws = Platform.getWS();
    }

    builder.append(PROP_WS);
    builder.append("="); //$NON-NLS-1$
    builder.append(ws);
    builder.append(","); //$NON-NLS-1$
    String os = target.getOS();
    if (os == null)
    {
      os = Platform.getOS();
    }

    builder.append(PROP_OS);
    builder.append("="); //$NON-NLS-1$
    builder.append(os);
    builder.append(","); //$NON-NLS-1$
    String arch = target.getArch();
    if (arch == null)
    {
      arch = Platform.getOSArch();
    }

    builder.append(PROP_ARCH);
    builder.append("="); //$NON-NLS-1$
    builder.append(arch);
    return builder.toString();
  }

  private static String generateNLProperty(ITargetDefinition target)
  {
    String nl = target.getNL();
    if (nl == null)
    {
      nl = Platform.getNL();
    }

    return nl;
  }

  private static void writeDigests(File file, HashBag<String> digests)
  {
    file.getParentFile().mkdirs();
    FileWriter out = null;

    try
    {
      out = new FileWriter(file);
      BufferedWriter writer = new BufferedWriter(out);

      List<String> list = new ArrayList<String>(digests);
      Collections.sort(list);

      for (String digest : list)
      {
        String line = digest + "=" + digests.getCounterFor(digest);
        writer.write(line);
        writer.newLine();
      }

      writer.close();
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

  private static HashBag<String> readDigests(File file)
  {
    Reader in = null;

    try
    {
      in = new FileReader(file);
      BufferedReader reader = new BufferedReader(in);

      HashBag<String> digests = new HashBag<String>();
      String line;

      while ((line = reader.readLine()) != null)
      {
        int pos = line.indexOf('=');
        String digest = line.substring(0, pos).trim();
        String count = line.substring(pos + 1).trim();

        digests.add(digest, Integer.valueOf(count));
      }

      return digests;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.close(in);
    }
  }

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

  public static synchronized TargletProfileManager getInstance() throws ProvisionException
  {
    if (instance == null)
    {
      instance = new TargletProfileManager();
    }

    return instance;
  }
}
