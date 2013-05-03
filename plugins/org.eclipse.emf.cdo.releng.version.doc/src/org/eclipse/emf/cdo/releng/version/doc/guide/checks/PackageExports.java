/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version.doc.guide.checks;

/**
 * Checking Package Export Versions and Package Import Version Ranges
 * <p>
 * A Java plug-in exports the Java packages it makes available for reuse by other plug-ins.
 * Also, rather than specifying dependencies on other plug-ins Java plug-ins can import other Java packages.
 * Like plug-ins themselves, those exports can specify a version and like plug-in dependencies, Java package imports can specify a version range.
 * Exported Java packages do not automatically inherit the version of their containing plug-in
 * so it's good practice to specify a version for each exported Java package.
 * And just as it's good practice for plug-in dependencies to specify a full dependency range with an inclusive lower bound and an exclusive upper bound,
 * so too it's good practice to specify that for Java package imports.
 * The version manager checks whether each package export specifies a version
 * and whether each package import specifies a proper full version range.
 * It produces an error for each violation.
 * </p>
 *
 * @author Eike Stepper
 */
public class PackageExports
{
}
