/*
 * Copyright (c) 2011-2013, 2015, 2016, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 420528
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class ViewProperties extends Properties<CDOView>
{
  public static final IProperties<CDOView> INSTANCE = new ViewProperties();

  private static final String CATEGORY_VIEW = "View"; //$NON-NLS-1$

  private static final String CATEGORY_SESSION = "Session"; //$NON-NLS-1$

  private ViewProperties()
  {
    super(CDOView.class);

    add(new Property<CDOView>("open", //$NON-NLS-1$
        "Open", "Whether this view is open or not.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return !view.isClosed();
      }
    });

    add(new Property<CDOView>("viewID", //$NON-NLS-1$
        "ID", "The ID of this view.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getViewID();
      }
    });

    add(new Property<CDOView>("viewURI", //$NON-NLS-1$
        "URI", "The URI of this view.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getURI();
      }
    });

    add(new Property<CDOView>("branchName") //$NON-NLS-1$
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getBranch().getName();
      }
    });

    add(new Property<CDOView>("branch", //$NON-NLS-1$
        "Branch", "The branch of this view.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getBranch().getPathName();
      }
    });

    add(new Property<CDOView>("timeStamp", //$NON-NLS-1$
        "Time Stamp", "The time stamp of this view.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return CDOCommonUtil.formatTimeStamp(view.getTimeStamp());
      }
    });

    add(new Property<CDOView>("lastUpdateTime", //$NON-NLS-1$
        "Last Update", "The time stamp of the last passive update.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return CDOCommonUtil.formatTimeStamp(view.getLastUpdateTime());
      }
    });

    add(new Property<CDOView>("rootResourcePermission", //$NON-NLS-1$
        "Root Resource Permission", "The permission the current user has for the root resource of this view.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getRootResource().cdoPermission();
      }
    });

    add(new Property<CDOView>("readOnly", //$NON-NLS-1$
        "Read-Only", "Whether this view is read-only or not.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.isReadOnly();
      }
    });

    add(new Property<CDOView>("dirty", //$NON-NLS-1$
        "Dirty", "Whether this view is dirty or not.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.isDirty();
      }
    });

    add(new Property<CDOView>("durable", //$NON-NLS-1$
        "Durable", "Whether this view is durable or not.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getDurableLockingID() != null;
      }
    });

    add(new Property<CDOView>("sessionID", //$NON-NLS-1$
        "Session ID", "The ID of the session of this view.", CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getSession().getSessionID();
      }
    });

    add(new Property<CDOView>("userID", //$NON-NLS-1$
        "User ID", "The user ID of the session of this view.", CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getSession().getUserID();
      }
    });

    add(new Property<CDOView>("userAuthenticated")
    {
      @Override
      protected Object eval(CDOView view)
      {
        return !StringUtil.isEmpty(view.getSession().getUserID());
      }
    });

    add(new Property<CDOView>("historical") //$NON-NLS-1$
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE;
      }
    });

    add(new Property<CDOView>("autoReleaseLocks") //$NON-NLS-1$
    {
      @Override
      protected Object eval(CDOView view)
      {
        if (view instanceof CDOTransaction)
        {
          return ((CDOTransaction)view).options().isAutoReleaseLocksEnabled();
        }

        return false;
      }
    });
  }

  public static void main(String[] args)
  {
    new Tester().dumpContributionMarkup();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tester extends DefaultPropertyTester<CDOView>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.view";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
