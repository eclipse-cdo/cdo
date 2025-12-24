/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.users;

import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_ProjectExplorerIntegration;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_HistoricalCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc09_TechnicalBackground.Doc_BackgroundTransactions;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transfer.CDOTransfer;

/**
 * Working with Folders and Resources
 * <p>
 * The models of a checkout are organized in the virtual file system (aka resource tree) of a repository. This resource tree
 * consists of folders and different types of resources, all categorized as {@link CDOResourceNode resource nodes}.
 * <p>
 * All modifications of the resource tree that are triggered in the {@link Doc_ProjectExplorerIntegration Project Explorer}
 * are performed in a separate background {@link CDOTransaction transaction}, see {@link Doc_BackgroundTransactions} for details.
 * <p>
 * Modifying the resource tree is only possible in checkouts that are not read-only, i.e., not in {@link Doc_HistoricalCheckouts}.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc06_UsingResources
{
  /**
   * Creating Resource Nodes
   * <p>
   * New resource nodes can be created directly in the {@link Doc_ProjectExplorerIntegration Project Explorer} by opening the
   * context menu on a checkout or on an existing folder under a checkout and then opening the New sub menu: <p align="center">{@image new-menu.png}
   * <p>
   * The following sub sections describe how to create the different types of resource nodes and how to work with them.
   */
  public class Doc_CreatingResourceNodes
  {

    /**
     * Creating Folders
     * <p>
     * A {@link CDOResourceFolder folder} is a named container for a list of nested resource nodes.
     * Folders provide a means to organize and reorganize the models and files in a checkout according to any criteria that seems adequate
     * for their use cases.
     * <p>
     * A new folder can be created by opening the context menu on a checkout or on an existing folder under a checkout and then
     * selecting New -> Folder. The following dialog will pop up to ask for the name of the new folder:
     * <p align="center">{@image new-folder.png}
     * <p>
     * The name of the new folder is validated to be unique among all nested resource nodes under the container of the new folder.
     */
    public class Doc_CreatingFolders
    {
    }

    /**
     * Creating Model Resources
     * <p>
     * A {@link CDOResource model resource} is a named container for a list of nested model elements.
     * Model resources provide a means to organize and reorganize the model elements in a checkout according to any criteria that seems adequate
     * for their use cases.
     * <p>
     * A new model resource can be created by opening the context menu on a checkout or on an existing folder under a checkout and then
     * selecting New -> Model Resource. The following dialog will pop up to ask for the name of the new model resource:
     * <p align="center">{@image new-resource.png}
     * <p>
     * The name of the new model resource is validated to be unique among all nested resource nodes under the container of the new model resource.
     */
    public class Doc_CreatingModelResources
    {
    }

    /**
     * Creating Text Files
     * <p>
     * A {@link CDOTextResource text file} is a named container for a stream of text characters and the {@link CDOTextResource#getEncoding() encoding}
     * of these characters. Text files provide a means to efficiently store and retrieve unmodeled data.
     * <p>
     * A new text file can be created by opening the context menu on a checkout or on an existing folder under a checkout and then
     * selecting New -> Text File. The following dialog will pop up to ask for the name of the new text file:
     * <p align="center">{@image new-text.png}
     * <p>
     * The name of the new text file is validated to be unique among all nested resource nodes under the container of the new text file.
     */
    public class Doc_CreatingTextFiles
    {
    }

    /**
     * Creating Binary Files
     * <p>
     * A {@link CDOBinaryResource binary file} is a named container for a stream of bytes. Binary files provide a means to efficiently store
     * and retrieve unmodeled data.
     * <p>
     * A new binary file can be created by opening the context menu on a checkout or on an existing folder under a checkout and then
     * selecting New -> Binary File. The following dialog will pop up to ask for the name of the new binary file:
     * <p align="center">{@image new-binary.png}
     * <p>
     * The name of the new binary file is validated to be unique among all nested resource nodes under the container of the new binary file.
     */
    public class Doc_CreatingBinaryFiles
    {
    }
  }

  /**
   * Renaming Resource Nodes
   * <p>
   * A resource node can be renamed at any time by selecting the resource node and pressing the F2 key or opening the context menu and
   * selecting the Rename action. The following dialog will pop up: <p align="center">{@image resource-rename.png}
   * <p>
   * Renaming a resource node <b>does not break</b> cross references to the model elements that are contained
   * in the model resources in or under the renamed resource node.
   */
  public class Doc_RenamingResourceNodes
  {
  }

  /**
   * Moving and Copying Resource Nodes within a Checkout
   * <p>
   * Resource nodes can be moved within the resource tree of a checkout by using drag and drop.
   * <p>
   * When holding the Ctrl key while dropping the resource nodes onto a checkout or a resource folder copies of the dragged
   * resource nodes are created under the drop target.
   * <p>
   * Moving a resource node to a different folder <b>does not break</b> cross references to the model elements that are contained
   * in the model resources in or under the moved resource node.
   */
  public class Doc_MovingResourceNodes
  {
  }

  /**
   * Moving and Copying Resource Nodes between Checkouts and Beyond
   * <p>
   * Moving and copying resource nodes beyond the current checkout with the {@link CDOTransfer resource transfer framework}
   * is not yet supported for checkouts.
   */
  public class Doc_TransferingResourceNodes
  {
  }

  /**
   * Deleting Resource Nodes
   * <p>
   * Resource nodes can be deleted from the resource tree of a checkout by selecting the resource node and pressing
   * the Del key or opening the context menu and
   * selecting the Delete action. The following confirmation dialog will pop up: <p align="center">{@image object-delete.png}
   * <p>
   * This confirmation dialog only shows the directly selected resource nodes. Nested resource nodes or model elements
   * are not shown for performance reasons but will also be deleted if OK is pressed.
   * <p>
   * Deleting a resource node <b>can break</b> cross references to the model elements that are contained
   * in the model resources in or under the deleted resource node!
   */
  public class Doc_DeletingResourceNodes
  {
  }
}
