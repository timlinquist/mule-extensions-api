/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.extension.api.stereotype;

import static java.util.Optional.empty;

import java.util.Optional;

/**
 * A widely held but fixed and oversimplified image or idea of the owning model. Examples would be {@code validator},
 * {@code outbound}, etc.
 *
 * @since 1.0
 */
public interface StereotypeDefinition<E extends Enum<E>> {

  /**
   * @return The type of the current error type definition
   */
  default String getName() {
    return ((E) this).name();
  }

  /**
   * @return The {@link Optional} parent of the current error type definition
   */
  default Optional<StereotypeDefinition<? extends Enum<?>>> getParent() {
    return empty();
  }

}
