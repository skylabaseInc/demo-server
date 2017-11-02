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
import io.mifos.customer.api.v1.CustomerEventConstants;
import io.mifos.customer.api.v1.domain.Customer;
import io.mifos.customer.api.v1.events.ScanEvent;
import io.mifos.dev.ServiceRunner;
import io.mifos.identity.api.v1.domain.Authentication;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class CustomerListener {

  private final EventRecorder eventRecorder;
  private final ServiceRunner serviceRunner = new ServiceRunner();

  @Autowired
  @Qualifier("test-logger")
  private Logger logger;

  @Autowired
  public CustomerListener(final EventRecorder eventRecorder) {
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
      subscription = CustomerEventConstants.DESTINATION,
      destination = CustomerEventConstants.DESTINATION,
      selector = CustomerEventConstants.SELECTOR_INITIALIZE
  )
  public void onInitialized(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                            final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.INITIALIZE, payload, String.class);
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_POST_CUSTOMER
  )
  public void customerCreatedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                   final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.POST_CUSTOMER, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().findCustomer(identifier);
        logger.info("Created customer {}", customer.getGivenName());
      }
    }
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_PUT_CUSTOMER
  )
  public void customerUpdatedEvents(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                    final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.PUT_CUSTOMER, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().findCustomer(identifier);
        logger.info("Updated customer {}", customer.getGivenName());
      }
    }
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_ACTIVATE_CUSTOMER
  )
  public void customerActivatedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                     final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.ACTIVATE_CUSTOMER, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().findCustomer(identifier);
        logger.info("Customer activated: {} {}", customer.getGivenName(), customer.getCurrentState());
      }
    }
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_LOCK_CUSTOMER
  )
  public void customerLockedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                  final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.LOCK_CUSTOMER, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().findCustomer(identifier);
        logger.info("Customer locked: {} {}", customer.getGivenName(), customer.getCurrentState());
      }
    }
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_UNLOCK_CUSTOMER
  )
  public void customerUnlockedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                    final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.UNLOCK_CUSTOMER, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().findCustomer(identifier);
        logger.info("Customer unlocked: {} {}", customer.getGivenName(), customer.getCurrentState());
      }
    }
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_CLOSE_CUSTOMER
  )
  public void customerClosedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                  final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.CLOSE_CUSTOMER, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().findCustomer(identifier);
        logger.info("Customer closed: {} {}", customer.getGivenName(), customer.getCurrentState());
      }
    }
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_REOPEN_CUSTOMER
  )
  public void customerReopenedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                    final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.REOPEN_CUSTOMER, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().findCustomer(identifier);
        logger.info("Customer reopen: {} {}", customer.getGivenName(), customer.getCurrentState());
      }
    }
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_PUT_ADDRESS
  )
  public void addressChangedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                  final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.PUT_ADDRESS, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().findCustomer(identifier);
        logger.info("Customer address modified: {} {}...", customer.getGivenName(), customer.getAddress().getCountry());
      }
    }
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_PUT_CONTACT_DETAILS
  )
  public void contactDetailsChangedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                         final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.PUT_CONTACT_DETAILS, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().findCustomer(identifier);
        logger.info("Customer contact details modified: {} {}...", customer.getGivenName(), customer.getContactDetails().get(0).getValue());
      }
    }
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_POST_IDENTIFICATION_CARD
  )
  public void identificationCardCreateEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                            final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.POST_IDENTIFICATION_CARD, payload, String.class);

    /*String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().fetchIdentificationCards()
        logger.info("Customer contact details modified: {} {}...", customer.getGivenName(), customer.);
      }
    }*/
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_PUT_IDENTIFICATION_CARD
  )
  public void identificationCardChangedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                             final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.PUT_IDENTIFICATION_CARD, payload, String.class);

    /*String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Customer customer = serviceRunner.getCustomerManager().api().fetchIdentificationCards()
        logger.info("Customer contact details modified: {} {}...", customer.getGivenName(), customer.);
      }
    }*/
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_DELETE_IDENTIFICATION_CARD
  )
  public void identificationCardDeletedEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                             final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.DELETE_IDENTIFICATION_CARD, payload, String.class);
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_POST_IDENTIFICATION_CARD_SCAN
  )
  public void identificationCardScanCreateEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                                final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.POST_IDENTIFICATION_CARD_SCAN, payload, ScanEvent.class);
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_DELETE_IDENTIFICATION_CARD_SCAN
  )
  public void identificationCardScanDeleteEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                                final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.DELETE_IDENTIFICATION_CARD_SCAN, payload, ScanEvent.class);
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_PUT_PORTRAIT
  )
  public void portraitPutEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                               final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.POST_PORTRAIT, payload, String.class);
  }

  @JmsListener(
          destination = CustomerEventConstants.DESTINATION,
          selector = CustomerEventConstants.SELECTOR_DELETE_PORTRAIT
  )
  public void portraitDeleteEvent(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                  final String payload) {
    this.eventRecorder.event(tenant, CustomerEventConstants.DELETE_PORTRAIT, payload, String.class);
  }
}
