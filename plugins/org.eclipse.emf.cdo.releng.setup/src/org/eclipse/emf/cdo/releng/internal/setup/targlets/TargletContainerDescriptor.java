/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.targlets;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;

import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class TargletContainerDescriptor implements Serializable, Comparable<TargletContainerDescriptor>
{
  private static final long serialVersionUID = 1L;

  private String id;

  private String workingDigest;

  private Set<File> workingProjects;

  private UpdateProblem updateProblem;

  private transient IProfile transactionProfile;

  public TargletContainerDescriptor()
  {
  }

  public TargletContainerDescriptor(String id)
  {
    this.id = id;
  }

  public String getID()
  {
    return id;
  }

  public String getWorkingDigest()
  {
    return workingDigest;
  }

  public Set<File> getWorkingProjects()
  {
    return workingProjects;
  }

  public UpdateProblem getUpdateProblem()
  {
    return updateProblem;
  }

  public int compareTo(TargletContainerDescriptor o)
  {
    return id.compareTo(o.getID());
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (id == null ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    TargletContainerDescriptor other = (TargletContainerDescriptor)obj;
    if (id == null)
    {
      if (other.id != null)
      {
        return false;
      }
    }
    else if (!id.equals(other.id))
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("TargletContainerDescriptor[id={0}, workingDigest={1}]", id, workingDigest);
  }

  IProfile startUpdateTransaction(String environmentProperties, String nlProperty, String digest,
      IProgressMonitor monitor) throws CoreException
  {
    if (transactionProfile != null)
    {
      throw new ProvisionException("An update transaction is already ongoing");
    }

    TargletContainerManager manager = TargletContainerManager.getInstance();
    transactionProfile = manager.getOrCreateProfile(id, environmentProperties, nlProperty, digest, monitor);
    return transactionProfile;
  }

  void commitUpdateTransaction(String digest, Set<File> projectLocations, IProgressMonitor monitor)
      throws CoreException
  {
    if (transactionProfile == null)
    {
      throw new ProvisionException("No update transaction is ongoing");
    }

    transactionProfile = null;
    workingDigest = digest;
    workingProjects = projectLocations;
    resetUpdateProblem();

    saveDescriptors(monitor);
  }

  void rollbackUpdateTransaction(Throwable t, IProgressMonitor monitor) throws CoreException
  {
    transactionProfile = null;
    updateProblem = new UpdateProblem(t);

    saveDescriptors(monitor);
  }

  void resetUpdateProblem()
  {
    updateProblem = null;
  }

  private static void saveDescriptors(IProgressMonitor monitor) throws CoreException
  {
    TargletContainerManager manager = TargletContainerManager.getInstance();
    manager.saveDescriptors(monitor);
  }

  /**
   * @author Eike Stepper
   */
  public static final class UpdateProblem implements Serializable, IStatus
  {
    private static final long serialVersionUID = 2L;

    private static final UpdateProblem[] NO_CHILDREN = {};

    private String plugin;

    private String message;

    private int severity;

    private int code;

    private Throwable exception;

    private UpdateProblem[] children;

    public UpdateProblem()
    {
    }

    public UpdateProblem(Throwable t)
    {
      this(Activator.getStatus(t));
    }

    public UpdateProblem(IStatus status)
    {
      plugin = status.getPlugin();
      message = status.getMessage();
      severity = status.getSeverity();
      code = status.getCode();
      exception = status.getException();

      IStatus[] statusChildren = status.getChildren();
      if (statusChildren != null && statusChildren.length != 0)
      {
        children = new UpdateProblem[statusChildren.length];
        for (int i = 0; i < statusChildren.length; i++)
        {
          IStatus statusChild = statusChildren[i];
          children[i] = new UpdateProblem(statusChild);
        }
      }
    }

    public String getPlugin()
    {
      return plugin;
    }

    public String getMessage()
    {
      return message;
    }

    public int getSeverity()
    {
      return severity;
    }

    public int getCode()
    {
      return code;
    }

    public Throwable getException()
    {
      return exception;
    }

    public UpdateProblem[] getChildren()
    {
      return isMultiStatus() ? children : NO_CHILDREN;
    }

    public boolean isMultiStatus()
    {
      return children != null;
    }

    public boolean isOK()
    {
      return severity == OK;
    }

    public boolean matches(int severityMask)
    {
      return (severity & severityMask) != 0;
    }

    public IStatus toStatus()
    {
      if (isMultiStatus())
      {
        IStatus[] statusChildren = new IStatus[children.length];
        for (int i = 0; i < children.length; i++)
        {
          statusChildren[i] = children[i].toStatus();
        }

        return new MultiStatus(plugin, code, statusChildren, message, exception);
      }

      return new Status(severity, plugin, code, message, exception);
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      toString(builder, 0);
      return builder.toString();
    }

    private void toString(StringBuilder builder, int level)
    {
      for (int i = 0; i < level; i++)
      {
        builder.append(' ');
      }

      switch (severity)
      {
      case IStatus.OK:
        builder.append("OK ");
        break;

      case IStatus.INFO:
        builder.append("INFO ");
        break;

      case IStatus.WARNING:
        builder.append("WARNING ");
        break;

      case IStatus.ERROR:
        builder.append("ERROR ");
        break;

      case IStatus.CANCEL:
        builder.append("CANCEL ");
        break;

      default:
      }

      builder.append(message);
      builder.append(StringUtil.NL);
    }
  }
}
