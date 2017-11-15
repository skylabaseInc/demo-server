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

import io.mifos.accounting.api.v1.EventConstants;
import io.mifos.accounting.api.v1.domain.Account;
import io.mifos.accounting.api.v1.domain.JournalEntry;
import io.mifos.accounting.api.v1.domain.Ledger;
import io.mifos.core.api.context.AutoGuest;
import io.mifos.core.api.context.AutoUserContext;
import io.mifos.core.lang.AutoTenantContext;
import io.mifos.core.lang.config.TenantHeaderFilter;
import io.mifos.core.test.listener.EventRecorder;
import io.mifos.dev.ServiceRunner;
import io.mifos.identity.api.v1.domain.Authentication;
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
public class AccountingListener {

  private final EventRecorder eventRecorder;

  private final ServiceRunner serviceRunner = new ServiceRunner();

  @Autowired
  @Qualifier("test-logger")
  private Logger logger;

  @Autowired
  public AccountingListener(final EventRecorder eventRecorder) {
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_INITIALIZE,
          subscription = EventConstants.DESTINATION
  )
  public void onInitialization(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                               final String payload) {
    this.eventRecorder.event(tenant, EventConstants.INITIALIZE, payload, String.class);
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_LEDGER,
          subscription = EventConstants.DESTINATION
  )
  public void onPostLedger(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                           final String payload) {
    this.eventRecorder.event(tenant, EventConstants.POST_LEDGER, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Ledger ledger = serviceRunner.getLedgerManager().api().findLedger(identifier);
        logger.info("Created ledger account {}", ledger.getName());
      }
    }
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_LEDGER,
          subscription = EventConstants.DESTINATION
  )
  public void onPutLedger(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                          final String payload) {
    this.eventRecorder.event(tenant, EventConstants.PUT_LEDGER, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Ledger ledger = serviceRunner.getLedgerManager().api().findLedger(identifier);
        logger.info("Modified ledger account {}", ledger.getName());
      }
    }
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_LEDGER,
          subscription = EventConstants.DESTINATION
  )
  public void onDeleteLedger(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                             final String payload) {
    this.eventRecorder.event(tenant, EventConstants.DELETE_LEDGER, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    logger.info("Deleted ledger account, {}", identifier);
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_ACCOUNT,
          subscription = EventConstants.DESTINATION
  )
  public void onCreateAccount(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.POST_ACCOUNT, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Account account = serviceRunner.getLedgerManager().api().findAccount(identifier);
        logger.info("Created account {}", account.getName());
      }
    }
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_ACCOUNT,
          subscription = EventConstants.DESTINATION
  )
  public void onChangeAccount(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.PUT_ACCOUNT, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Account account = serviceRunner.getLedgerManager().api().findAccount(identifier);
        logger.info("Modified account {}", account.getName());
      }
    }
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_CLOSE_ACCOUNT,
          subscription = EventConstants.DESTINATION
  )
  public void onCloseAccount(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                             final String payload) {
    this.eventRecorder.event(tenant, EventConstants.CLOSE_ACCOUNT, payload, String.class);

    String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Account account = serviceRunner.getLedgerManager().api().findAccount(identifier);
        logger.info("Account closed: {} {}", account.getName(), account.getState());
      }
    }
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_LOCK_ACCOUNT,
          subscription = EventConstants.DESTINATION
  )
  public void onLockAccount(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                            final String payload) {
    this.eventRecorder.event(tenant, EventConstants.LOCK_ACCOUNT, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Account account = serviceRunner.getLedgerManager().api().findAccount(identifier);
        logger.info("Account locked: {} {} ", account.getName(), account.getState());
      }
    }
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_UNLOCK_ACCOUNT,
          subscription = EventConstants.DESTINATION
  )
  public void onUnlockAccount(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.UNLOCK_ACCOUNT, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Account account = serviceRunner.getLedgerManager().api().findAccount(identifier);
        logger.info("Account unlocked: {} {} ", account.getName(), account.getState());
      }
    }
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_REOPEN_ACCOUNT,
          subscription = EventConstants.DESTINATION
  )
  public void onReopenAccount(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.REOPEN_ACCOUNT, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final Account account = serviceRunner.getLedgerManager().api().findAccount(identifier);
        logger.info("Account reopened: {} {} ", account.getName(), account.getState());
      }
    }
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_ACCOUNT,
          subscription = EventConstants.DESTINATION
  )
  public void onDeleteAccount(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                              final String payload) {
    this.eventRecorder.event(tenant, EventConstants.DELETE_ACCOUNT, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    logger.info("Deleted account, {}", identifier);
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_JOURNAL_ENTRY,
          subscription = EventConstants.DESTINATION
  )
  public void onPostJournalEntry(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                 final String payload) {
    this.eventRecorder.event(tenant, EventConstants.POST_JOURNAL_ENTRY, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final JournalEntry journalEntry = serviceRunner.getLedgerManager().api().findJournalEntry(identifier);
        logger.info("Journal entry created ( creditor:{}, debtor:{} )", journalEntry.getCreditors(), journalEntry.getDebtors());
      }
    }
  }

  @JmsListener(
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_RELEASE_JOURNAL_ENTRY,
          subscription = EventConstants.DESTINATION
  )
  public void onJournalEntryProcessed(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                      final String payload) {
    this.eventRecorder.event(tenant, EventConstants.RELEASE_JOURNAL_ENTRY, payload, String.class);

    final String identifier = payload.replaceAll("^\"|\"$", "");
    try (final AutoTenantContext ignored = new AutoTenantContext(tenant)) {
      final Authentication syncGatewayAuthentication;

      try (final AutoGuest ignored2 = new AutoGuest()) {
        syncGatewayAuthentication = serviceRunner.getIdentityManager().api().login(serviceRunner.getSyncUser().getIdentifier(), serviceRunner.getSyncUser().getPassword());
      }

      try (final AutoUserContext ignored2 = new AutoUserContext(serviceRunner.getSyncUser().getIdentifier(), syncGatewayAuthentication.getAccessToken())) {
        final JournalEntry journalEntry = serviceRunner.getLedgerManager().api().findJournalEntry(identifier);
        logger.info("Journal entry processed ( {} {} )", journalEntry.getCreditors(), journalEntry.getDebtors());
      }
    }
  }
}
