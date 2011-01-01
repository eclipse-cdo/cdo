/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEAttribute;
import org.eclipse.emf.teneo.annotations.pannotation.FetchType;
import org.eclipse.emf.teneo.hibernate.mapper.ManyAttributeMapper;

/**
 * Extends the Teneo ManyAttributeMapper to force eager load of all primitive type lists.
 * 
 * @author Martin Taal
 * @since 3.0
 */
public class CDOManyAttributeMapper extends ManyAttributeMapper
{
  public CDOManyAttributeMapper()
  {
  }

  @Override
  public void processManyAttribute(PAnnotatedEAttribute paAttribute)
  {
    paAttribute.getOneToMany().setFetch(FetchType.EAGER);
    super.processManyAttribute(paAttribute);
  }
}
