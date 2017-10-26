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
import io.mifos.office.api.v1.EventConstants;
import io.mifos.office.api.v1.domain.Employee;
import io.mifos.office.api.v1.domain.Office;
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
public class OrganizationListener {

  private final EventRecorder eventRecorder;

  private final ServiceRunner serviceRunner = new ServiceRunner();

  @Autowired
  @Qualifier("test-logger")
  private Logger logger;

  @Autowired
  public OrganizationListener(final EventRecorder eventRecorder) {
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_INITIALIZE
  )
  public void onInitialized(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                            final String payload) {
    this.eventRecorder.event(tenant, EventConstants.INITIALIZE, payload, String.class);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_EMPLOYEE
  )
  public void onCreateEmployee(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                               final String eventPayload) throws Exception {

    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Employee employee = serviceRunner.getOrganizationManager().api().findEmployee(identifier);
        logger.info("Created employee: {} {}", employee.getGivenName(), employee.getSurname());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_EMPLOYEE
  )
  public void onUpdateEmployee(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                               final String eventPayload) throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Employee employee = serviceRunner.getOrganizationManager().api().findEmployee(identifier);
        logger.info("Updated employee: {} {}", employee.getGivenName(), employee.getSurname());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_EMPLOYEE
  )
  public void onDeleteEmployee(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                               final String eventPayload) throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    logger.info("Deleted employee with identifier: {}", identifier);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_CONTACT_DETAIL
  )
  public void onSetContactDetail(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                 final String eventPayload) throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Employee employee = serviceRunner.getOrganizationManager().api().findEmployee(identifier);
        logger.info("Set employee contact details: ");
        employee.getContactDetails().forEach(contactDetail -> logger.info("{}", contactDetail));
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_CONTACT_DETAIL
  )
  public void onDeleteContactDetail(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                    final String eventPayload) throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    logger.info("Deleted contact details for employee with identifier: {}", identifier);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_OFFICE
  )
  public void onCreateOffice(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                             final String eventPayload)
          throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Office office = serviceRunner.getOrganizationManager().api().findOfficeByIdentifier(identifier);
        logger.info("Created office: {}", office.getName());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_OFFICE
  )
  public void onUpdateOffice(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                             final String eventPayload)
          throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Office office = serviceRunner.getOrganizationManager().api().findOfficeByIdentifier(identifier);
        logger.info("Updated office: {}", office.getName());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_OFFICE
  )
  public void onDeleteOffice(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                             final String eventPayload)
          throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    logger.info("Deleted office with identifier: {}", identifier);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_ADDRESS
  )
  public void onSetAddress(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                           final String eventPayload)
          throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Office office = serviceRunner.getOrganizationManager().api().findOfficeByIdentifier(identifier);
        logger.info("Set office address: {} {} {}", office.getAddress().getRegion(), office.getAddress().getCity(), office.getAddress().getStreet());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_ADDRESS
  )
  public void onDeleteAddress(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String eventPayload)
          throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    logger.info("Deleted office address with identifier: {}", identifier);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_REFERENCE
  )
  public void onPutAReference(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String eventPayload)
          throws Exception {
    String identifier = eventPayload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Office office = serviceRunner.getOrganizationManager().api().findOfficeByIdentifier(identifier);
        logger.info("Put an external office reference: {}", office.getExternalReferences());
      }
    }
  }
}
