/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.internal.ui.actions;

import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.internal.ui.views.Net4jIntrospectorView;
import org.eclipse.net4j.util.ui.actions.SafeAction;

/**
 * @author Eike Stepper
 */
public class IntrospectAction extends SafeAction
{
  private Object object;

  public IntrospectAction(Object object)
  {
    super(Messages.getString("IntrospectAction_0")); //$NON-NLS-1$
    this.object = object;
  }

  @Override
  protected void safeRun() throws Exception
  {
    Net4jIntrospectorView introspector = Net4jIntrospectorView.getInstance(true);
    introspector.setValue(object);
  }
}
