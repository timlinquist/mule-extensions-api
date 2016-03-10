/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.api.introspection;


/**
 * A specialization of the {@link ConnectionProviderModel} interface which adds
 * behavioural components that are relevant to the extension's functioning
 * when in runtime.
 *
 * @see ConnectionProviderModel
 * @since 1.0
 */
public interface RuntimeConnectionProviderModel<Config, Connection> extends ConnectionProviderModel<Config, Connection>
{

    /**
     * @return the {@link ConnectionProviderFactory} to be used to create instances
     * consistent with this model
     */
    ConnectionProviderFactory getConnectionProviderFactory();

}
