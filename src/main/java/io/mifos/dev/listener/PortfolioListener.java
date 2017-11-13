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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.mifos.core.api.context.AutoGuest;
import io.mifos.core.api.context.AutoUserContext;
import io.mifos.core.lang.AutoTenantContext;
import io.mifos.core.lang.config.TenantHeaderFilter;
import io.mifos.core.test.listener.EventRecorder;
import io.mifos.dev.ServiceRunner;
import io.mifos.identity.api.v1.domain.Authentication;
import io.mifos.individuallending.api.v1.events.IndividualLoanCommandEvent;
import io.mifos.individuallending.api.v1.events.IndividualLoanEventConstants;
import io.mifos.portfolio.api.v1.domain.BalanceSegmentSet;
import io.mifos.portfolio.api.v1.domain.Case;
import io.mifos.portfolio.api.v1.domain.ChargeDefinition;
import io.mifos.portfolio.api.v1.domain.Product;
import io.mifos.portfolio.api.v1.events.BalanceSegmentSetEvent;
import io.mifos.portfolio.api.v1.events.CaseEvent;
import io.mifos.portfolio.api.v1.events.ChargeDefinitionEvent;
import io.mifos.portfolio.api.v1.events.EventConstants;
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
public class PortfolioListener {

  private final EventRecorder eventRecorder;

  private final ServiceRunner serviceRunner = new ServiceRunner();

  @Autowired
  @Qualifier("test-logger")
  private Logger logger;

  @Autowired
  public PortfolioListener(final EventRecorder eventRecorder) {
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_INITIALIZE
  )
  public void onInitialization(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                               final String payload) {
    this.eventRecorder.event(tenant, EventConstants.INITIALIZE, payload, String.class);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_PRODUCT
  )
  public void onCreateProduct(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.POST_PRODUCT, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Product product = serviceRunner.getPortfolioManager().api().getProduct(identifier);
        logger.info("Create product {}", product.getName());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_PRODUCT
  )
  public void onChangeProduct(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.PUT_PRODUCT, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Product product = serviceRunner.getPortfolioManager().api().getProduct(identifier);
        logger.info("Update product {}", product.getName());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_PRODUCT_ENABLE
  )
  public void onEnableProduct(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.PUT_PRODUCT_ENABLE, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Product product = serviceRunner.getPortfolioManager().api().getProduct(identifier);
        logger.info("Enable product: {} {}", product.getName(), product.isEnabled());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_PRODUCT
  )
  public void onDeleteProduct(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.DELETE_PRODUCT, payload, String.class);
    String identifier = payload.replaceAll("^\"|\"$", "");
    logger.info("Deleted product, {}", identifier);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_CHARGE_DEFINITION
  )
  public void onCreateProductChargeDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.POST_CHARGE_DEFINITION, payload, ChargeDefinitionEvent.class);

    JsonObject jsonObj = new JsonParser().parse(payload).getAsJsonObject();
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        ChargeDefinition chargeDefinition = serviceRunner.getPortfolioManager().api().getChargeDefinition(jsonObj.get("productIdentifier").getAsString(), jsonObj.get("chargeDefinitionIdentifier").getAsString());
        logger.info("Create product charge definition: {}", chargeDefinition.getName());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_CHARGE_DEFINITION
  )
  public void onChangeProductChargeDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.PUT_CHARGE_DEFINITION, payload, ChargeDefinitionEvent.class);

    JsonObject jsonObj = new JsonParser().parse(payload).getAsJsonObject();
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        ChargeDefinition chargeDefinition = serviceRunner.getPortfolioManager().api().getChargeDefinition(jsonObj.get("productIdentifier").getAsString(), jsonObj.get("chargeDefinitionIdentifier").getAsString());
        logger.info("Update product charge definition: {}", chargeDefinition.getName());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_PRODUCT_CHARGE_DEFINITION
  )
  public void onDeleteProductChargeDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.DELETE_PRODUCT_CHARGE_DEFINITION, payload, ChargeDefinitionEvent.class);
    JsonObject jsonObj = new JsonParser().parse(payload).getAsJsonObject();
    logger.info("Deleted product charge: {}, for product {}", jsonObj.get("productIdentifier").getAsString(), jsonObj.get("chargeDefinitionIdentifier").getAsString());
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_CASE
  )
  public void onCreateCase(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                           final String payload) {
    this.eventRecorder.event(tenant, EventConstants.POST_CASE, payload, CaseEvent.class);

    JsonObject jsonObj = new JsonParser().parse(payload).getAsJsonObject();
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Case newCase = serviceRunner.getPortfolioManager().api().getCase(jsonObj.get("productIdentifier").getAsString(), jsonObj.get("caseIdentifier").getAsString());
        logger.info("Create case: {}", newCase.getProductIdentifier());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_CASE
  )
  public void onChangeCase(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                           final String payload) {
    this.eventRecorder.event(tenant, EventConstants.PUT_CASE, payload, CaseEvent.class);

    JsonObject jsonObj = new JsonParser().parse(payload).getAsJsonObject();
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        Case newCase = serviceRunner.getPortfolioManager().api().getCase(jsonObj.get("productIdentifier").getAsString(), jsonObj.get("caseIdentifier").getAsString());
        logger.info("Update case: {}", newCase.getProductIdentifier());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_BALANCE_SEGMENT_SET
  )
  public void onCreateBalanceSegmentSet(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                        final String payload) {
    this.eventRecorder.event(tenant, EventConstants.POST_BALANCE_SEGMENT_SET, payload, BalanceSegmentSetEvent.class);

    JsonObject jsonObj = new JsonParser().parse(payload).getAsJsonObject();
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        BalanceSegmentSet balanceSegmentSet = serviceRunner.getPortfolioManager().api().getBalanceSegmentSet(jsonObj.get("productIdentifier").getAsString(), jsonObj.get("balanceSegmentSetIdentifier").getAsString());
        logger.info("Create balance segment set: {}", balanceSegmentSet.getIdentifier());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_BALANCE_SEGMENT_SET
  )
  public void onChangeBalanceSegmentSet(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                        final String payload) {
    this.eventRecorder.event(tenant, EventConstants.PUT_BALANCE_SEGMENT_SET, payload, BalanceSegmentSetEvent.class);

    JsonObject jsonObj = new JsonParser().parse(payload).getAsJsonObject();
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        BalanceSegmentSet balanceSegmentSet = serviceRunner.getPortfolioManager().api().getBalanceSegmentSet(jsonObj.get("productIdentifier").getAsString(), jsonObj.get("balanceSegmentSetIdentifier").getAsString());
        logger.info("Update balance segment set: {}", balanceSegmentSet.getIdentifier());
      }
    }
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_BALANCE_SEGMENT_SET
  )
  public void onDeleteBalanceSegmentSet(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                        final String payload) {
    this.eventRecorder.event(tenant, EventConstants.DELETE_BALANCE_SEGMENT_SET, payload, BalanceSegmentSetEvent.class);
    JsonObject jsonObj = new JsonParser().parse(payload).getAsJsonObject();
    logger.info("Delete balance segment set: {}", jsonObj.get("productIdentifier").getAsString(), jsonObj.get("balanceSegmentSetIdentifier").getAsString());
  }

}