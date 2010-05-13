/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.eclipse.emf.cdo.examples.server;

import templates.RequestForm;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eike Stepper
 */
public class RequestFormServlet extends AbstractTemplateServlet<Object>
{
  private static final long serialVersionUID = 1L;

  public RequestFormServlet() throws InstantiationException, IllegalAccessException, SecurityException,
      NoSuchMethodException
  {
    super(RequestForm.class);
  }

  @Override
  protected Object createTemplateArgument(HttpServletRequest req)
  {
    return null;
  }
}
