/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IVersionBuilderArguments extends Map<String, String>
{
  public static final String DEFAULT_VALIDATOR_CLASS_NAME = "org.eclipse.emf.cdo.releng.version.digest.DigestValidator$BuildModel";

  public static final String RELEASE_PATH_ARGUMENT = "release.path";

  public static final String VALIDATOR_CLASS_ARGUMENT = "validator.class";

  public static final String IGNORE_DEPENDENCY_RANGES_ARGUMENT = "ignore.missing.dependency.ranges";

  public static final String IGNORE_EXPORT_VERSIONS_ARGUMENT = "ignore.missing.export.versions";

  public static final String IGNORE_CONTENT_REDUNDANCY_ARGUMENT = "ignore.feature.content.redundancy";

  public static final String IGNORE_CONTENT_CHANGES_ARGUMENT = "ignore.feature.content.changes";

  public static final String IGNORE_MALFORMED_VERSIONS_ARGUMENT = "ignore.malformed.versions";

  public String getReleasePath();

  public boolean isIgnoreMissingDependencyRanges();

  public boolean isIgnoreMissingExportVersions();

  public boolean isIgnoreFeatureContentRedundancy();

  public boolean isIgnoreFeatureContentChanges();

  public boolean isIgnoreMalformedVersionsButton();

  public void applyTo(IProject project) throws CoreException;
}
