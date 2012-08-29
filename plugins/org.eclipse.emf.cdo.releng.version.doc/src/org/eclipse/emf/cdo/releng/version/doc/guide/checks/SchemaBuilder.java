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
 * Checking whether or not the PDE Schema Builder is Needed
 * <p>
 * The PDE supports a schema builder to process the extension point schema associated with extension points defined in a plug-in.
 * That builder isn't needed when the plug-in doesn't define extension points but should be present when extension points are defined.
 * The version manager generates a warning when the builder is present but isn't needed or when the builder is needed but isn't present.
 * A quick fix for the warning will add or remove the PDE's schema builder as appropriate;
 * it changes the builder specified in the <code>.project</code> file.
 * Alternatively, a quick fix to configure the project to ignore the warning can be applied;
 * it changes the Version Management Tool builder's arguments in the <code>.project</code> file.
 * </p>
 * @author Eike Stepper
 */
public class SchemaBuilder
{
}
