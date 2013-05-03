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
 * Checking Proper Qualifier Convention
 * <p>
 * The PDE uses the convention of specifying <code>"qualifier"</code> as the value of <a href="http://wiki.eclipse.org/Version_Numbering#When_to_change_the_qualifier_segment">qualifier segment</a>
 * to indicate that it should be computed and substituted at build/publish/export time.
 * All plug-ins and features should follow this convention.
 * The version manager checks that this convention is respected for every plug-in and feature that it manages.
 * A violation will be marked as an error.
 * A quick fix for that error will insert <code>".qualifier"</code> into the version specified in the <code>MANIFEST.MF</code> or <code>feature.xml</code> of the bundle.
 * Alternatively, a quick fix to configure the project to ignore the error can be applied;
 * it changes the Version Management Tool builder's arguments in the <code>.project</code> file.
 * </p>
 * @author Eike Stepper
 */
public class Qualifier
{

}
