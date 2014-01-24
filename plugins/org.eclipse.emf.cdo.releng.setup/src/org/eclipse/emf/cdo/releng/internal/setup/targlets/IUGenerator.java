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

import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.publisher.PublisherInfo;
import org.eclipse.equinox.p2.publisher.eclipse.BundlesAction;
import org.eclipse.equinox.p2.publisher.eclipse.Feature;
import org.eclipse.equinox.p2.publisher.eclipse.FeaturesAction;
import org.eclipse.osgi.service.resolver.BundleDescription;

import java.io.File;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;

/**
 * @author Eike Stepper
 */
public interface IUGenerator
{
  public IInstallableUnit generateIU(File location) throws Exception;

  /**
   * @author Eike Stepper
   */
  public static final class BundleIUGenerator extends BundlesAction implements IUGenerator
  {
    public static final IUGenerator INSTANCE = new BundleIUGenerator();

    private BundleIUGenerator()
    {
      super((File[])null);
      setPublisherInfo(new PublisherInfo());
    }

    public IInstallableUnit generateIU(File location) throws Exception
    {
      Dictionary<String, String> manifest = loadManifest(location);
      if (manifest == null)
      {
        return null;
      }

      String version = manifest.get(org.osgi.framework.Constants.BUNDLE_VERSION);
      if (version.endsWith(".qualifier"))
      {
        version = version.substring(0, version.length() - ".qualifier".length());
        manifest.put(org.osgi.framework.Constants.BUNDLE_VERSION, version);
      }

      BundleDescription description = createBundleDescription(manifest, location);
      if (description == null)
      {
        return null;
      }

      IInstallableUnit iu = createBundleIU(description, null, info);
      if (iu instanceof InstallableUnit)
      {
        ((InstallableUnit)iu).setArtifacts(new IArtifactKey[0]);
      }

      return iu;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class FeatureIUGenerator extends FeaturesAction implements IUGenerator
  {
    public static final IUGenerator INSTANCE = new FeatureIUGenerator();

    private FeatureIUGenerator()
    {
      super((File[])null);
      setPublisherInfo(new PublisherInfo());
    }

    public IInstallableUnit generateIU(File location) throws Exception
    {
      Feature[] features = getFeatures(new File[] { location });
      if (features == null || features.length == 0)
      {
        return null;
      }

      Feature feature = features[0];

      String version = feature.getVersion();
      if (version.endsWith(".qualifier"))
      {
        version = version.substring(0, version.length() - ".qualifier".length());
        feature.setVersion(version);
      }

      List<IInstallableUnit> childIUs = Collections.emptyList();
      return createGroupIU(feature, childIUs, info);
    }
  }
}
