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
 * Checking that Features have a Feature Nature
 * <p>
 * The PDE provides a feature nature and an associate feature builder for project's that act as features.
 * The version manager checks whether each managed feature properly specifies both that nature and the associated builder
 * and produces an error on the <code>.project</code> if that's not the case.
 * A quick fix for the error will add PDE's feature nature and feature builder to the <code>.project</code> file.
 * Alternatively, a quick fix to configure the project to ignore the error can be applied;
 * it changes the Version Management Tool builder's arguments in the <code>.project</code> file.
 * </p>
 * @author Eike Stepper
 */
public class FeatureNature
{
}
