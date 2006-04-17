/**
 * <copyright>
 *
 * Copyright (c) 2002-2004 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: Net4jTestPlugin.java,v 1.1 2006-04-17 21:13:10 nickb Exp $
 */
package org.eclipse.net4j.tests;

import org.eclipse.core.runtime.Plugin;

public class Net4jTestPlugin 
extends Plugin
{
    private static Net4jTestPlugin instance;
    
    public Net4jTestPlugin()
    {
        super();
        instance = this;
    }

    public static Net4jTestPlugin getPlugin()
    {
        return instance;
    }
}
