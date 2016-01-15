/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.api.introspection.declaration.fluent;

import org.mule.extension.api.introspection.ExceptionEnricherFactory;
import org.mule.extension.api.introspection.ExtensionModel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * A declaration object for a {@link ExtensionModel}. It contains raw, unvalidated
 * data which is used to declare the structure of a {@link ExtensionModel}
 *
 * @since 1.0
 */
public class Declaration extends BaseDeclaration<Declaration>
{

    private String name;
    private String version;
    private String vendor;
    private Optional<ExceptionEnricherFactory> exceptionEnricherFactory;

    private final List<ConfigurationDeclaration> configurations = new LinkedList<>();
    private final List<OperationDeclaration> operations = new LinkedList<>();
    private final List<ConnectionProviderDeclaration> connectionProviders = new LinkedList<>();
    private final List<SourceDeclaration> messageSources = new LinkedList<>();

    /**
     * Creates a new instance
     */
    Declaration()
    {
        super("");
    }

    /**
     * Returns an immutable list with the {@link ConfigurationDeclaration} instances
     * that have been declared so far.
     *
     * @return an unmodifiable list. May be empty but will never be {@code null}
     */
    public List<ConfigurationDeclaration> getConfigurations()
    {
        return configurations;
    }

    /**
     * Adds a {@link ConfigurationDeclaration}
     *
     * @param config a not {@code null} {@link ConfigurationDeclaration}
     * @return this declaration
     * @throws {@link IllegalArgumentException} if {@code config} is {@code null}
     */
    public Declaration addConfig(ConfigurationDeclaration config)
    {
        if (config == null)
        {
            throw new IllegalArgumentException("Can't add a null config");
        }

        configurations.add(config);
        return this;
    }

    /**
     * @return an unmodifiable {@link List} with
     * the available {@link OperationDeclaration}s
     */
    public List<OperationDeclaration> getOperations()
    {
        return Collections.unmodifiableList(operations);
    }

    /**
     * @return an unmodifiable {@link List} with the available {@link ConnectionProviderDeclaration}s
     */
    public List<ConnectionProviderDeclaration> getConnectionProviders()
    {
        return Collections.unmodifiableList(connectionProviders);
    }

    /**
     * @return an unmodifiable {@link List} with the available {@link SourceDeclaration}s
     */
    public List<SourceDeclaration> getMessageSources()
    {
        return Collections.unmodifiableList(messageSources);
    }

    /**
     * Adds a {@link ConnectionProviderDeclaration}
     *
     * @param connectionProvider a not {@code null} {@link ConnectionProviderDeclaration}
     * @return {@code this} declaration
     * @throws IllegalArgumentException if {@code connectionProvider} is {@code null}
     */
    public Declaration addConnectionProvider(ConnectionProviderDeclaration connectionProvider)
    {
        if (connectionProvider == null)
        {
            throw new IllegalArgumentException("Can't add a null connection provider");
        }

        connectionProviders.add(connectionProvider);
        return this;
    }

    /**
     * Adds a {@link OperationDeclaration}
     *
     * @param operation a not {@code null} {@link OperationDeclaration}
     * @return {@code this} declaration
     * @throws {@link IllegalArgumentException} if {@code operation} is {@code null}
     */
    public Declaration addOperation(OperationDeclaration operation)
    {
        if (operation == null)
        {
            throw new IllegalArgumentException("Can't add a null operation");
        }

        operations.add(operation);
        return this;
    }

    /**
     * Adds a {@link SourceDeclaration}
     *
     * @param sourceDeclaration a not {@code null} {@link SourceDeclaration}
     * @return {@code this} declaration
     * @throws {@link IllegalArgumentException} if {@code sourceDeclaration} is {@code null}
     */
    public Declaration addMessageSource(SourceDeclaration sourceDeclaration)
    {
        if (sourceDeclaration == null)
        {
            throw new IllegalArgumentException("Can't add a null message source");
        }

        messageSources.add(sourceDeclaration);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    void setVersion(String version)
    {
        this.version = version;
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor(String vendor)
    {
        this.vendor = vendor;
    }

    public Optional<ExceptionEnricherFactory> getExceptionEnricherFactory()
    {
        return exceptionEnricherFactory;
    }

    public void setExceptionEnricherFactory(Optional<ExceptionEnricherFactory> exceptionEnricherFactory)
    {
        this.exceptionEnricherFactory = exceptionEnricherFactory;
    }
}
