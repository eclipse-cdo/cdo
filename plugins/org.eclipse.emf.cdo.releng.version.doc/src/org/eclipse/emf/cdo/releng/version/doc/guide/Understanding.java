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
package org.eclipse.emf.cdo.releng.version.doc.guide;

/**
 * Understanding Versions
 * <p>
 * Eclipse plug-ins and features are uniquely identified by their OSGi-based bundle ID and version.
 * The PDE tools validate that each is well formed, but there's more to it than that.
 * The version has <a href="http://www.osgi.org/wiki/uploads/Links/SemanticVersioning.pdf">semantic significance</a>,
 * i.e., when a bundle introduces breaking API changes, the <a href="http://wiki.eclipse.org/Version_Numbering#When_to_change_the_major_segment">major segment</a> of its version must be incremented.
 * The <a href="http://wiki.eclipse.org/PDE/API_Tools/User_Guide">PDE API Tools<a/> provide excellent support
 * for managing the major segment and the <a href="<a href="http://wiki.eclipse.org/Version_Numbering#When_to_change_the_minor_segment">minor segment</a> of the version.
 * Unfortunately there's nothing to help manage the <a href="http://wiki.eclipse.org/Version_Numbering#When_to_change_the_micro">micro segment</a>, also referred to as the service segment;
 * its correct management is <a href="http://wiki.eclipse.org/Version_Numbering#Overall_example">quite complex</a>.
 * This is where the Version Management Tool, also referred to as the version manager, helps fill the void.
 * Note that managing the <a href="http://wiki.eclipse.org/Version_Numbering#When_to_change_the_qualifier_segment">qualifier segment</a> is not problematic
 * because the PDE has adopted the convention of using the value <code>"qualifier"</code> to specify that the qualifier segment be computed and substituted at build/publish/export time.
 * Even in this regard though, the version manager helps by detecting when bundles aren't properly using <code>".qualifier"</code> convention.
 * </p
 * <p>
 * The most important aspect of the version manager is to help manage semantic versions for plug-ins and features.
 * It goes well beyond that task by providing an assortment of checks to detect when proper plug-in and feature conventions are violated.
 * We've already mentioned one such example, i.e., a bundle's ID should use <code>"qualifier"</code> as the qualifier segment, but there are many more such checks.
 * The version manager strives to provide quick fixes to easily whip your bundles into the proper shape,
 * along with the ability to disable warnings for style guideline violations you don't wish to respect.
 * </p>
 *
 * @number 1
 * @author Eike Stepper
 */
public class Understanding
{
}
