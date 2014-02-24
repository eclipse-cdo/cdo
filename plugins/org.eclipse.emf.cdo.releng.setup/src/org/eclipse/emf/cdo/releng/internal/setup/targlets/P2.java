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

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IEngine;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class P2
{
  public static final File AGENT_FOLDER = new File(SetupUtil.getProperty("user.home"), ".p2"); //$NON-NLS-1$

  public static final File PROFILE_REGISTRY_FOLDER = new File(AGENT_FOLDER,
      "org.eclipse.equinox.p2.engine/profileRegistry");

  public static final String PROP_TARGLET_CONTAINER_WORKSPACE = "targlet.container.workspace"; //$NON-NLS-1$

  public static final String PROP_TARGLET_CONTAINER_ID = "targlet.container.id"; //$NON-NLS-1$

  public static final String PROP_TARGLET_CONTAINER_DIGEST = "targlet.container.digest"; //$NON-NLS-1$

  private static String AGENT_FILTER = "(locationURI=" + AGENT_FOLDER.toURI() + ")"; //$NON-NLS-1$

  private static IProvisioningAgent agent;

  private static IProfileRegistry profileRegistry;

  private static IPlanner planner;

  private static IEngine engine;

  public static synchronized IProvisioningAgent getAgent()
  {
    if (agent == null)
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
          throw new IllegalStateException("Provisioning agent could not be loaded for " + AGENT_FOLDER);
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
        catch (ProvisionException ex2)
        {
          throw new IllegalStateException(ex2);
        }
        finally
        {
          context.ungetService(providerRef);
        }
      }
    }

    return agent;
  }

  public static synchronized IPlanner getPlanner()
  {
    if (planner == null)
    {
      planner = (IPlanner)getAgent().getService(IPlanner.SERVICE_NAME);
      if (planner == null)
      {
        throw new IllegalStateException("Planner could not be loaded");
      }
    }

    return planner;
  }

  public static synchronized IEngine getEngine()
  {
    if (engine == null)
    {
      engine = (IEngine)getAgent().getService(IEngine.SERVICE_NAME);
      if (engine == null)
      {
        throw new IllegalStateException("Engine could not be loaded");
      }
    }

    return engine;
  }

  public static synchronized IArtifactRepositoryManager getArtifactRepositoryManager()
  {
    IArtifactRepositoryManager manager = (IArtifactRepositoryManager)getAgent().getService(
        IArtifactRepositoryManager.SERVICE_NAME);
    if (manager == null)
    {
      throw new IllegalStateException("Artifact respository manager could not be loaded");
    }

    return manager;
  }

  public static synchronized IProfileRegistry getProfileRegistry()
  {
    if (planner == null)
    {
      profileRegistry = (IProfileRegistry)getAgent().getService(IProfileRegistry.SERVICE_NAME);
      if (profileRegistry == null)
      {
        throw new IllegalStateException("Profile registry could not be loaded");
      }
    }

    return profileRegistry;
  }

  public static synchronized void removeProfile(IProfile profile)
  {
    String profileID = profile.getProfileId();
    getProfileRegistry().removeProfile(profileID);
    Activator.log("Profile " + profileID + " removed");
  }

  public static boolean isTargletProfile(IProfile profile)
  {
    return profile.getProperty(PROP_TARGLET_CONTAINER_ID) != null;
  }
}
