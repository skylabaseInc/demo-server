/*
 * Copyright 2017 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.dev.listener;

import io.mifos.core.api.context.AutoGuest;
import io.mifos.core.api.context.AutoUserContext;
import io.mifos.core.lang.AutoTenantContext;
import io.mifos.core.lang.config.TenantHeaderFilter;
import io.mifos.core.test.listener.EventRecorder;
import io.mifos.dev.ServiceRunner;
import io.mifos.identity.api.v1.domain.Authentication;
import io.mifos.identity.api.v1.domain.Role;
import io.mifos.identity.api.v1.domain.User;
import io.mifos.identity.api.v1.events.ApplicationPermissionEvent;
import io.mifos.identity.api.v1.events.ApplicationPermissionUserEvent;
import io.mifos.identity.api.v1.events.ApplicationSignatureEvent;
import io.mifos.identity.api.v1.events.EventConstants;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author Myrle Krantz
 */
@SuppressWarnings("unused")
@Component
public class IdentityListener {

  private final EventRecorder eventRecorder;

  private final ServiceRunner serviceRunner = new ServiceRunner();

  @Autowired
  @Qualifier("test-logger")
  private Logger logger;

  @Autowired
  public IdentityListener(final EventRecorder eventRecorder) {
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_USER
  )
  public void onCreateUser(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_POST_USER, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final User user = serviceRunner.getIdentityManager().api().getUser(identifier);
        logger.info("Created user {} with role {}", user.getIdentifier(), user.getRole());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_USER_ROLEIDENTIFIER
  )
  public void onChangeUserRole(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_PUT_USER_ROLEIDENTIFIER, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final User user = serviceRunner.getIdentityManager().api().getUser(identifier);
        logger.info("Updated user {} role {}", user.getIdentifier(), user.getRole());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_USER_PASSWORD
  )
  public void onChangeUserPassword(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_PUT_USER_PASSWORD, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final User user = serviceRunner.getIdentityManager().api().getUser(identifier);
        logger.info("Updated user {} password", user.getIdentifier());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_PERMITTABLE_GROUP
  )
  public void onCreatePermittableGroup(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_POST_PERMITTABLE_GROUP, payload, String.class);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_APPLICATION_PERMISSION
  )
  public void onCreateApplicationPermission(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_POST_APPLICATION_PERMISSION, payload, ApplicationPermissionEvent.class);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_APPLICATION_SIGNATURE
  )
  public void onSetApplicationSignature(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_PUT_APPLICATION_SIGNATURE, payload, ApplicationSignatureEvent.class);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_APPLICATION_PERMISSION_USER_ENABLED
  )
  public void onPutApplicationPermissionEnabledForUser(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_PUT_APPLICATION_PERMISSION_USER_ENABLED, payload, ApplicationPermissionUserEvent.class);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_ROLE
  )
  public void onCreateRole(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_POST_ROLE, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Role role = serviceRunner.getIdentityManager().api().getRole(identifier);
        logger.info("Created role, {}", role.getIdentifier());
        role.getPermissions().forEach(permission -> logger.info("{}", permission.getPermittableEndpointGroupIdentifier()));
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_ROLE
  )
  public void onChangeRole(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_PUT_ROLE, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Role role = serviceRunner.getIdentityManager().api().getRole(identifier);
        logger.info("Updated role, {}", role.getIdentifier());
        role.getPermissions().forEach(permission -> logger.info("{}", permission.getPermittableEndpointGroupIdentifier()));
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_ROLE
  )
  public void onDeleteRole(
          @Header(TenantHeaderFilter.TENANT_HEADER)final String tenant,
          final String payload) throws Exception {
    eventRecorder.event(tenant, EventConstants.OPERATION_DELETE_ROLE, payload, String.class);
    final String identifier = payload.replaceAll("^\"|\"$", "");
    logger.info("Deleted role, {}", identifier);
  }
}
