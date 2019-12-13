/*
 * Copyright (c) 2008-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.options.IOptionsEvent;

/**
 * 258850: Make Options extend INotifier
 * <p>
 * See bug 258850
 *
 * @author Victor Roldan Betancort
 */
public class Bugzilla_258850_Test extends AbstractCDOTest
{
  public void testBugzilla_258278() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      TestListener listener = new TestListener()
      {
        @Override
        public void notifyEvent(IEvent event)
        {
          if (event instanceof IOptionsEvent)
          {
            if (event instanceof CDOView.Options.InvalidationNotificationEvent)
            {
              setSuccess(true);
            }
          }
        }
      };

      listener.setSuccess(false);
      transaction.options().addListener(listener);
      transaction.options().setInvalidationNotificationEnabled(true);

      assertEquals(true, listener.getSucess());

      transaction.commit();
      session.close();
    }
  }

  public abstract class TestListener implements IListener
  {
    private boolean success = false;

    public void setSuccess(boolean success)
    {
      this.success = success;
    }

    public boolean getSucess()
    {
      return success;
    }

    @Override
    public abstract void notifyEvent(IEvent event);
  }
}
