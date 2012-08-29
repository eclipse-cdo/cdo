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
package org.eclipse.emf.cdo.releng.version.doc.guide.checks;

/**
 * Checking Plug-in Dependency Ranges
 * <p>
 * A plug-in optionally specifies dependencies on other plug-ins and as part of each such dependency specification,
 * can indicate the range of versions of that dependency with which it will function correctly.
 * The range is specified by indicating the version of the lower bound, whether the range includes or excludes the lower bound,
 * the version of the upper bound, and whether the range includes or excludes the upper bound.
 * It's good policy to specify each dependency's range with an inclusive lower bound and an exclusive upper bound
 * (and of course to test the full range, though the latter is an issue outside the scope of the Version Management Tool).
 * The version manager checks whether each dependency of each managed plug-in properly specifies a full range with an
 * inclusive lower bound and an exclusive upper bound. It produces an error for each dependency that violates this guideline.
 * </p>
 * @author Eike Stepper
 */
public class DependencyRanges
{
}
