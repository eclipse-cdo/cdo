/*
 * Copyright (c) 2012, 2015, 2016, 2018, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.monitor.SubProgressMonitor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.2
 */
class CDOTransferMappingImpl implements CDOTransferMapping
{
  private final CDOTransfer transfer;

  private final CDOTransferElement source;

  private final CDOTransferMappingImpl parent;

  private List<CDOTransferMapping> children;

  private CDOTransferType transferType;

  private IPath relativePath;

  private Status status;

  public CDOTransferMappingImpl(CDOTransfer transfer, CDOTransferElement source, CDOTransferMapping parent, IProgressMonitor monitor)
  {
    this.transfer = transfer;
    this.source = source;
    this.parent = (CDOTransferMappingImpl)parent;
    relativePath = transfer.getPathProvider().getPath(source);

    CDOTransferType transferType = transfer.getTransferType(source);
    this.transferType = transferType;

    if (parent != null)
    {
      this.parent.addChild(this);
    }

    try
    {
      if (isDirectory())
      {
        CDOTransferElement[] children = source.getChildren();
        monitor.beginTask("", 1 + children.length);
        monitor.subTask("Mapping " + source);
        monitor.worked(1);

        for (CDOTransferElement child : children)
        {
          transfer.map(child, this, new SubProgressMonitor(monitor, 1));
        }
      }
      else
      {
        monitor.beginTask("", 1);
        monitor.subTask("Mapping " + source);
        monitor.worked(1);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public CDOTransferMappingImpl(CDOTransfer transfer)
  {
    this.transfer = transfer;
    source = null;
    parent = null;
    transferType = CDOTransferType.FOLDER;
    relativePath = Path.EMPTY;
  }

  @Override
  public CDOTransfer getTransfer()
  {
    return transfer;
  }

  @Override
  public CDOTransferElement getSource()
  {
    return source;
  }

  @Override
  public CDOTransferMapping getParent()
  {
    return parent;
  }

  @Override
  public boolean isRoot()
  {
    return parent == null;
  }

  @Override
  public boolean isDirectory()
  {
    if (source == null)
    {
      return true;
    }

    return source.isDirectory();
  }

  @Override
  public String getName()
  {
    return relativePath.lastSegment();
  }

  @Override
  public void setName(String name)
  {
    setRelativePath(relativePath.removeLastSegments(1).append(name));
  }

  @Override
  public IPath getRelativePath()
  {
    return relativePath;
  }

  @Override
  public void setRelativePath(IPath path)
  {
    if (!ObjectUtil.equals(relativePath, path))
    {
      IPath oldPath = relativePath;
      relativePath = path;
      unsetStatusRecursively();
      transfer.relativePathChanged(this, oldPath, path);
    }
  }

  @Override
  public void setRelativePath(String path)
  {
    setRelativePath(new Path(path));
  }

  @Override
  public void accept(Visitor visitor)
  {
    if (visitor.visit(this) && children != null)
    {
      for (CDOTransferMapping child : children)
      {
        child.accept(visitor);
      }
    }
  }

  @Override
  public CDOTransferMapping[] getChildren()
  {
    if (ObjectUtil.isEmpty(children))
    {
      return NO_CHILDREN;
    }

    CDOTransferMapping[] result = children.toArray(new CDOTransferMapping[children.size()]);
    Arrays.sort(result);
    return result;
  }

  @Override
  public CDOTransferMapping getChild(IPath path)
  {
    if (path.isEmpty())
    {
      return this;
    }

    String name = path.segment(0);
    for (CDOTransferMapping child : getChildren())
    {
      if (name.equals(child.getName()))
      {
        return child.getChild(path.removeFirstSegments(1));
      }
    }

    return null;
  }

  @Override
  public CDOTransferMapping getChild(String path)
  {
    return getChild(new Path(path));
  }

  private void addChild(CDOTransferMapping child)
  {
    ensureChildrenList();
    if (!children.contains(child))
    {
      children.add(child);
      transfer.childrenChanged(this, child, CDOTransfer.ChildrenChangedEvent.Kind.MAPPED);
    }
  }

  private void removeChild(CDOTransferMapping child)
  {
    if (children != null && children.remove(child))
    {
      transfer.childrenChanged(this, child, CDOTransfer.ChildrenChangedEvent.Kind.UNMAPPED);
    }
  }

  private void ensureChildrenList()
  {
    if (children == null)
    {
      children = new ArrayList<>();
    }
  }

  @Override
  public void unmap()
  {
    transfer.unmap(this);
    if (parent != null)
    {
      parent.removeChild(this);
    }
  }

  @Override
  public CDOTransferType getTransferType()
  {
    return transferType;
  }

  @Override
  public void setTransferType(CDOTransferType transferType)
  {
    if (!ObjectUtil.equals(this.transferType, transferType))
    {
      CDOTransferType oldType = this.transferType;
      this.transferType = transferType;
      transfer.transferTypeChanged(this, oldType, transferType);
    }
  }

  @Override
  public IPath getFullPath()
  {
    IPath relativePath = getRelativePath();
    if (isRoot())
    {
      return relativePath;
    }

    IPath path = parent.getFullPath();
    return path.append(relativePath);
  }

  @Override
  public Status getStatus()
  {
    if (status == null)
    {
      status = calculateStatus();
    }

    return status;
  }

  private Status calculateStatus()
  {
    if (parent != null)
    {
      Status status = parent.getStatus();
      if (status != Status.MERGE)
      {
        return status;
      }
    }

    CDOTransferSystem targetSystem = transfer.getTargetSystem();

    IPath fullPath = getFullPath();
    int lastSegment = fullPath.segmentCount();
    for (int i = 1; i <= lastSegment; i++)
    {
      IPath path = fullPath.uptoSegment(i);
      CDOTransferElement target = targetSystem.getElement(path);
      if (target == null)
      {
        return Status.NEW;
      }

      boolean sourceDirectory = i == lastSegment ? isDirectory() : true;
      boolean targetDirectory = target.isDirectory();
      if (!(sourceDirectory && targetDirectory))
      {
        return Status.CONFLICT;
      }
    }

    return Status.MERGE;
  }

  private void unsetStatusRecursively()
  {
    if (status != null)
    {
      status = null;
      if (children != null && !children.isEmpty())
      {
        for (CDOTransferMapping child : children)
        {
          ((CDOTransferMappingImpl)child).unsetStatusRecursively();
        }
      }
    }
  }

  @Override
  public CDOTransferElement getTarget()
  {
    CDOTransferSystem targetSystem = transfer.getTargetSystem();
    IPath fullPath = getFullPath();
    return targetSystem.getElement(fullPath);
  }

  @Override
  public int compareTo(CDOTransferMapping o)
  {
    boolean directory = isDirectory();
    boolean oDirectory = o.isDirectory();
    if (directory != oDirectory)
    {
      return directory ? -1 : 1;
    }

    return getName().compareTo(o.getName());
  }

  @Override
  public String toString()
  {
    return getFullPath().toString();
  }
}
