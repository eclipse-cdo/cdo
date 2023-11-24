/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.branch;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.Notifier;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 4.11
 */
public class CDOBranchTagImpl extends Notifier implements CDOBranchTag
{
  private String name;

  private InternalCDOBranch branch;

  private long timeStamp;

  public CDOBranchTagImpl(String name, CDOBranch branch, long timeStamp)
  {
    checkName(name);
    checkBranchPoint(branch, timeStamp);

    this.name = name;
    this.branch = (InternalCDOBranch)branch;
    this.timeStamp = timeStamp;
  }

  @Override
  public CDOBranch getBranch()
  {
    return branch;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public InternalCDOBranchManager getBranchManager()
  {
    if (branch != null)
    {
      return branch.getBranchManager();
    }

    return null;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public void setName(String name)
  {
    checkName(name);

    if (name.equals(this.name))
    {
      return;
    }

    InternalCDOBranchManager branchManager = getBranchManager();
    branchManager.renameTag(this.name, name);
  }

  void setNameInternal(String name)
  {
    this.name = name;
  }

  void fireTagRenamedEvent(String oldName, String newName)
  {
    fireEvent(new TagRenamedEventImpl(this, oldName, newName));
  }

  @Override
  public void move(CDOBranchPoint branchPoint)
  {
    CDOBranch newBranch = branchPoint.getBranch();
    long newTimeStamp = branchPoint.getTimeStamp();
    checkBranchPoint(newBranch, newTimeStamp);

    branchPoint = CDOBranchUtil.adjustBranchPoint(branchPoint, getBranchManager());
    if (branchPoint.equals(CDOBranchUtil.copyBranchPoint(this)))
    {
      return;
    }

    InternalCDOBranchManager branchManager = getBranchManager();
    branchManager.moveTag(this, branchPoint);

    moveInternal(newBranch, newTimeStamp);
  }

  void moveInternal(CDOBranch branch, long timeStamp)
  {
    this.branch = (InternalCDOBranch)branch;
    this.timeStamp = timeStamp;
  }

  void fireTagMovedEvent(CDOBranchPoint oldBranchPoint, CDOBranchPoint newBranchPoint)
  {
    fireEvent(new TagMovedEventImpl(this, oldBranchPoint, newBranchPoint));
  }

  @Override
  public void delete()
  {
    if (!isDeleted())
    {
      InternalCDOBranchManager branchManager = getBranchManager();
      branchManager.deleteTag(this);

      deleteInternal();
    }
  }

  void deleteInternal()
  {
    branch = null;
    timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
  }

  void fireTagDeletedEvent()
  {
    fireEvent(new TagDeletedEventImpl(this));
  }

  @Override
  public boolean isDeleted()
  {
    return branch == null;
  }

  /*
   * The hashCode() method is only overridden to make the compiler happy.
   */
  @Override
  public int hashCode()
  {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    return this == obj;
  }

  @Override
  public int compareTo(CDOBranchTag o)
  {
    long ts1 = timeStamp;
    long ts2 = o.getTimeStamp();
    if (ts1 == ts2)
    {
      int b1 = branch.getID();
      int b2 = o.getBranch().getID();
      return Integer.compare(b1, b2);
    }

    return Long.compare(ts1, ts2);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("BranchTag[{0}, {1}, {2}]", name, branch, CDOCommonUtil.formatTimeStamp(timeStamp)); //$NON-NLS-1$
  }

  private static void checkName(String name)
  {
    CheckUtil.checkArg(name != null && !name.isEmpty(), "Invalid name"); //$NON-NLS-1$
  }

  private static void checkBranchPoint(CDOBranch branch, long timeStamp)
  {
    CheckUtil.checkArg(branch, "branch"); //$NON-NLS-1$
    CheckUtil.checkArg(timeStamp > CDOBranchPoint.UNSPECIFIED_DATE, "Invalid timeStamp"); //$NON-NLS-1$
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class TagEventImpl extends Event implements TagEvent
  {
    private static final long serialVersionUID = 1L;

    public TagEventImpl(CDOBranchTagImpl tag)
    {
      super(tag);
    }

    @Override
    public CDOBranchTag getTag()
    {
      return (CDOBranchTag)getSource();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TagRenamedEventImpl extends TagEventImpl implements TagRenamedEvent
  {
    private static final long serialVersionUID = 1L;

    private final String oldName;

    private final String newName;

    public TagRenamedEventImpl(CDOBranchTagImpl tag, String oldName, String newName)
    {
      super(tag);
      this.oldName = oldName;
      this.newName = newName;
    }

    @Override
    public String getOldName()
    {
      return oldName;
    }

    @Override
    public String getNewName()
    {
      return newName;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "oldName=" + oldName + ", newName=" + newName;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TagMovedEventImpl extends TagEventImpl implements TagMovedEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOBranchPoint oldBranchPoint;

    private final CDOBranchPoint newBranchPoint;

    public TagMovedEventImpl(CDOBranchTagImpl tag, CDOBranchPoint oldBranchPoint, CDOBranchPoint newBranchPoint)
    {
      super(tag);
      this.oldBranchPoint = oldBranchPoint;
      this.newBranchPoint = newBranchPoint;
    }

    @Override
    public CDOBranchPoint getOldBranchPoint()
    {
      return oldBranchPoint;
    }

    @Override
    public CDOBranchPoint getNewBranchPoint()
    {
      return newBranchPoint;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "oldBranchPoint=" + oldBranchPoint + ", newBranchPoint=" + newBranchPoint;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TagDeletedEventImpl extends TagEventImpl implements TagDeletedEvent
  {
    private static final long serialVersionUID = 1L;

    public TagDeletedEventImpl(CDOBranchTagImpl tag)
    {
      super(tag);
    }
  }
}
