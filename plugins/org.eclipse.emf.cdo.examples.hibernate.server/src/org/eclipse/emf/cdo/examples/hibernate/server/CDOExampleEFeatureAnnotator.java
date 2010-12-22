/**
 * Copyright (c) 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.examples.hibernate.server;

import org.eclipse.emf.cdo.server.hibernate.teneo.CDOEFeatureAnnotator;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEStructuralFeature;

/**
 * An example of providing an extension for Teneo Mapping. See <a
 * href="http://wiki.eclipse.org/Teneo/Hibernate/Extensions">here</a> for more information on Teneo extensions.
 *
 * @author Martin Taal
 */
public class CDOExampleEFeatureAnnotator extends CDOEFeatureAnnotator
{
  @Override
  public void annotate(PAnnotatedEStructuralFeature aStructuralFeature)
  {
    IOUtil.ERR().println("This method should be called"); //$NON-NLS-1$
    super.annotate(aStructuralFeature);
  }
}
