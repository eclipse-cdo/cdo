/*
 * Copyright (c) 2007-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import javax.jms.TemporaryTopic;

public class TemporaryTopicImpl extends TopicImpl implements TemporaryTopic
{
  private static final long serialVersionUID = 1L;

  private static int counter;

  public TemporaryTopicImpl()
  {
    super("TempTopic" + ++counter); //$NON-NLS-1$
  }

  @Override
  public void delete()
  {
    throw new NotYetImplementedException();
  }
}
