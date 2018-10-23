/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.spi.evolution;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.Release;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractMigrationContext implements MigrationContext
{
  public static final int LATEST_RELEASE_VERSION = -1;

  private static final String PROP_RELEASE_VERSION = "org.eclipse.emf.cdo.server.evolution.releaseVersion";

  private static final Set<String> PROPS = CollectionUtil.setOf(PROP_RELEASE_VERSION);

  private static final boolean ALWAYS_RESET = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.evolution.AbstractMigrationContext.alwaysReset");

  private final Evolution evolution;

  private final Release newRelease;

  /**
   *
   * @param evolutionURI the <code>URI</code> of the evolution resource,
   *                     or <code>null</code> to request deletion of the migration properties
   *                     from the {@link IStore store}.
   * @param releaseVersion the version of the release to migrate to, or any value if the <code>evolutionURI</code>
   *                       parameter is null.
   */
  public AbstractMigrationContext(URI evolutionURI, int releaseVersion)
  {
    if (evolutionURI != null)
    {
      evolution = loadEvolution(evolutionURI);

      if (releaseVersion == LATEST_RELEASE_VERSION)
      {
        Release latestRelease = evolution.getLatestRelease();
        releaseVersion = latestRelease == null ? 0 : latestRelease.getVersion();
      }

      newRelease = evolution.getRelease(releaseVersion);
      if (newRelease == null)
      {
        throw new IllegalArgumentException("Release v" + releaseVersion + " does not exist");
      }

      controlRootPackages();
    }
    else
    {
      evolution = null;
      newRelease = null;
    }
  }

  public AbstractMigrationContext(Release release)
  {
    newRelease = release;

    if (newRelease != null)
    {
      evolution = newRelease.getEvolution();
      controlRootPackages();
    }
    else
    {
      evolution = null;
    }
  }

  public boolean migrate(EvolutionSupport evolutionSupport, OMMonitor monitor)
  {
    String repositoryName = evolutionSupport.getStore().getRepository().getName();

    if (evolution == null)
    {
      deleteReleaseVersion(evolutionSupport);
      log("Migration properties of " + repositoryName + " deleted");
      return true;
    }

    if (ALWAYS_RESET)
    {
      deleteReleaseVersion(evolutionSupport);
    }

    int oldVersion = readReleaseVersion(evolutionSupport);

    Release[] releases = computeReleases(oldVersion);
    if (releases.length == 0)
    {
      log("Migration of " + repositoryName + " not needed");
      return false;
    }

    monitor.begin(releases.length);

    try
    {
      InternalCDORevisionCache revisionCache = ((InternalCDORevisionManager)evolutionSupport.getStore().getRepository().getRevisionManager()).getCache();

      for (Release toRelease : releases)
      {
        int toVersion = toRelease.getVersion();
        String msg = repositoryName + (oldVersion == 0 ? "" : " from release v" + oldVersion) + " to release v" + toVersion;
        log("===============================================");
        log("Migrating " + msg);

        evolutionSupport.migrateTo(toRelease, this, monitor.fork());
        revisionCache.clear();

        writeReleaseVersion(evolutionSupport, toVersion);
        log("Migrated " + msg);
        log("===============================================");
        log("");

        oldVersion = toVersion;
      }

      return true;
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * Moves each root package of the new release into its own resource with the {@link EPackage#getNsURI() nsURI},
   * such that {@link EcoreUtil#getURI(org.eclipse.emf.ecore.EObject) EcoreUtil.getURI(modelElement)} returns a
   * URI that starts with the nsURI of the rootPackage.
   */
  protected void controlRootPackages()
  {
    Resource releaseResource = newRelease.eResource();
    ResourceSet resourceSet = releaseResource.getResourceSet();

    for (EPackage rootPackage : newRelease.getRootPackages())
    {
      if (rootPackage.eResource() == releaseResource)
      {
        URI nsURI = URI.createURI(rootPackage.getNsURI());

        Resource resource = resourceSet.createResource(nsURI);
        resource.getContents().add(rootPackage);
      }
    }
  }

  protected Evolution loadEvolution(URI evolutionURI)
  {
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet();
    Resource resource = resourceSet.getResource(evolutionURI, true);
    Evolution evolution = (Evolution)resource.getContents().get(0);

    CDOView view = evolution.cdoView();
    if (view != null)
    {
      // TODO Create temp resource set? Copy models?
      evolution = EcoreUtil.copy(evolution);
      view.getSession().close();
    }

    return evolution;
  }

  protected Release[] computeReleases(int oldVersion)
  {
    List<Release> releases = new ArrayList<Release>();
    Release oldRelease = oldVersion == 0 ? null : evolution.getRelease(oldVersion);

    for (Release fromRelease = oldRelease; oldVersion < newRelease.getVersion();)
    {
      Release toRelease = fromRelease == null ? evolution.getInitialRelease() : fromRelease.getNextRelease();
      releases.add(toRelease);

      fromRelease = toRelease;
      oldVersion = fromRelease.getVersion();
    }

    return releases.toArray(new Release[releases.size()]);
  }

  protected int readReleaseVersion(IStoreAccessor accessor)
  {
    IStore store = accessor.getStore();
    Map<String, String> properties = store.getPersistentProperties(PROPS);

    String currentVersion = properties.get(PROP_RELEASE_VERSION);
    return currentVersion == null ? 0 : Integer.parseInt(currentVersion);
  }

  protected void writeReleaseVersion(IStoreAccessor accessor, int releaseVersion)
  {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put(PROP_RELEASE_VERSION, Integer.toString(releaseVersion));

    IStore store = accessor.getStore();
    store.setPersistentProperties(properties);
  }

  protected void deleteReleaseVersion(IStoreAccessor accessor)
  {
    IStore store = accessor.getStore();
    store.removePersistentProperties(PROPS);
  }
}
