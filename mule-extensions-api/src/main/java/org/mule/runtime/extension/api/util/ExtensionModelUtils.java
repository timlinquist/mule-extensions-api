/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.extension.api.util;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.mule.runtime.api.meta.ExpressionSupport.REQUIRED;
import static org.mule.runtime.api.meta.ExpressionSupport.SUPPORTED;
import static org.mule.runtime.api.meta.model.parameter.ParameterRole.BEHAVIOUR;
import static org.mule.runtime.api.meta.model.parameter.ParameterRole.CONTENT;
import static org.mule.runtime.api.meta.model.parameter.ParameterRole.PRIMARY_CONTENT;
import static org.mule.runtime.api.util.Preconditions.checkArgument;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.NamedObject;
import org.mule.runtime.api.meta.model.ComponentModel;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.api.meta.model.SubTypesModel;
import org.mule.runtime.api.meta.model.config.ConfigurationModel;
import org.mule.runtime.api.meta.model.operation.HasOperationModels;
import org.mule.runtime.api.meta.model.operation.OperationModel;
import org.mule.runtime.api.meta.model.parameter.ParameterModel;
import org.mule.runtime.api.meta.model.parameter.ParameterRole;
import org.mule.runtime.api.meta.model.parameter.ParameterizedModel;
import org.mule.runtime.api.meta.model.source.HasSourceModels;
import org.mule.runtime.api.meta.model.source.SourceModel;
import org.mule.runtime.api.meta.model.util.ExtensionWalker;
import org.mule.runtime.api.meta.model.util.IdempotentExtensionWalker;
import org.mule.runtime.api.util.Reference;
import org.mule.runtime.extension.api.annotation.param.Content;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Utility methods for analyzing and decomposing {@link ExtensionModel} instances
 *
 * @since 1.0
 */
public class ExtensionModelUtils {

  private ExtensionModelUtils() {}

  /**
   * Collects the {@link ParameterModel parameters} from {@code model} which supports or requires expressions
   *
   * @param model a {@link ParameterizedModel}
   * @return a {@link List} of {@link ParameterModel}. Can be empty but will never be {@code null}
   */
  public static List<ParameterModel> getDynamicParameters(ParameterizedModel model) {
    return model.getAllParameterModels().stream().filter(parameter -> acceptsExpressions(parameter.getExpressionSupport()))
        .collect(toList());
  }

  /**
   * @param support a {@link ExpressionSupport}
   * @return Whether or not the given {@code support} is one which accepts or requires expressions
   */
  public static boolean acceptsExpressions(ExpressionSupport support) {
    return support == SUPPORTED || support == REQUIRED;
  }

  /**
   * Returns a {@link List} with all the {@link ComponentModel} available to the {@code extensionModel} which requires a
   * connection. This includes both {@link SourceModel} and {@link OperationModel}.
   *
   * @param extensionModel a {@link ExtensionModel}
   * @return a {@link List} of {@link ComponentModel}. It might be empty but will never be {@code null}
   */
  public static List<ComponentModel> getConnectedComponents(ExtensionModel extensionModel) {
    List<ComponentModel> connectedModels = new LinkedList<>();
    new IdempotentExtensionWalker() {

      @Override
      public void onOperation(OperationModel model) {
        collect(model);
      }

      @Override
      public void onSource(SourceModel model) {
        collect(model);
      }

      private void collect(ComponentModel model) {
        if (model.requiresConnection()) {
          connectedModels.add(model);
        }
      }
    }.walk(extensionModel);

    return connectedModels;
  }

  /**
   * Returns a {@link List} with all the {@link ComponentModel} available to the {@code configurationModel} which requires a
   * connection. This includes both {@link SourceModel} and {@link OperationModel}.
   *
   * @param extensionModel     the {@link ExtensionModel} that owns the {@code configurationModel}
   * @param configurationModel the {@link ConfigurationModel} which components you want
   * @return a {@link List} of {@link ComponentModel}. It might be empty but will never be {@code null}
   */
  public static List<ComponentModel> getConnectedComponents(ExtensionModel extensionModel,
                                                            ConfigurationModel configurationModel) {
    List<ComponentModel> connectedModels = new LinkedList<>();
    new ExtensionWalker() {

      @Override
      public void onOperation(HasOperationModels owner, OperationModel model) {
        if (owner == configurationModel) {
          collect(owner, model);
        }
      }

      @Override
      public void onSource(HasSourceModels owner, SourceModel model) {
        collect(owner, model);
      }

      private void collect(Object owner, ComponentModel model) {
        if (owner == configurationModel && model.requiresConnection()) {
          connectedModels.add(model);
        }
      }
    }.walk(extensionModel);

    return connectedModels;
  }

  /**
   * @param extensionModel the model which owns the {@code component}
   * @param component      a component
   * @return Whether the given {@code component} needs to be provided with a config
   * in order to function
   */
  public static boolean requiresConfig(ExtensionModel extensionModel, NamedObject component) {
    if (!(component instanceof ComponentModel)) {
      return false;
    }

    ComponentModel model = (ComponentModel) component;
    if (model.requiresConnection()) {
      return true;
    }

    Reference<Boolean> result = new Reference<>(false);
    new ExtensionWalker() {

      @Override
      public void onOperation(HasOperationModels owner, OperationModel model) {
        resolve(model, owner);
      }

      @Override
      public void onSource(HasSourceModels owner, SourceModel model) {
        resolve(model, owner);
      }

      private void resolve(ComponentModel model, Object owner) {
        if (owner != null && model == component) {
          result.set(true);
        }
      }
    }.walk(extensionModel);

    return result.get();
  }

  public static boolean isContent(ParameterModel parameterModel) {
    return isContent(parameterModel.getRole());
  }

  public static boolean isContent(ParameterRole purpose) {
    checkArgument(purpose != null, "cannot evaluate null purpose");
    return purpose != BEHAVIOUR;
  }

  public static ParameterRole roleOf(Optional<Content> content) {
    return content.map(c -> c.primary() ? PRIMARY_CONTENT : CONTENT).orElse(BEHAVIOUR);
  }

  public static Map<MetadataType, Set<MetadataType>> toSubTypesMap(Collection<SubTypesModel> subTypes) {
    return subTypes.stream().collect(toMap(SubTypesModel::getBaseType, SubTypesModel::getSubTypes));
  }

}
