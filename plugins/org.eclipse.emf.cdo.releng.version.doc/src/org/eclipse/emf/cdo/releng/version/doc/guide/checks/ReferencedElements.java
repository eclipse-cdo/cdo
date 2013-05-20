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
 * Checking whether Components are Managed and Used
 * <p>
 * Publishing Eclipse plug-ins for use by clients involves specifying features to include those plug-ins and then building and publishing or exporting those features.
 * As such, it's expected that every plug-in will be included by some feature.
 * Also, features can include other features, so generally this inclusion structure induces a directed acyclic graph of dependencies of features on other plug-ins and features.
 * <p>
 * The version manager analyzes the dependency graph and produces an error on each reference
 * to a plug-in or feature that's included in the graph
 * but doesn't specify a Version Management Tool builder with the same release as the referencing feature.
 * The quick fix for the error adds the Version Management Tool builder to the <code>.project</code> of the referenced plug-in or feature.
 * <p>
 * The version manager also analyzes the dependency graph to determine the roots,
 * i.e., plug-ins or features that are managed but are not referenced by any other feature,
 * and produces an error on the ID in the <code>MANIFEST.MF</code> or <code>feature.xml</code> of each such component.
 * The quick fix for the error adds the project for that plug-in or feature to the Version Management Tool builder's specified properties file's <code>root.projects</code> property.
 * It's clear that each managed release must have at least one root feature
 * and because plug-ins can't generally be released without being included in a feature, a plug-in is generally not expected to be a root component.
 *
 * @author Eike Stepper
 */
public class ReferencedElements
{
}
