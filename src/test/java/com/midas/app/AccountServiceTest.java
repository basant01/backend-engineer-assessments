package com.midas.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.midas.app.models.Account;
import com.midas.app.models.Enum.ProviderType;
import com.midas.app.providers.external.stripe.StripeConfiguration;
import com.midas.app.providers.external.stripe.StripePaymentProvider;
import com.midas.app.repositories.AccountRepository;
import com.midas.app.services.AccountService;
import com.midas.app.services.AccountServiceImpl;
import io.temporal.client.WorkflowClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class AccountServiceTest {

  @Mock WorkflowClient workflowClient;

  @Test
  void testCreateAccountWithStripeIntegration() {
    // Mock dependencies
    StripeConfiguration stripeConfiguration = mock(StripeConfiguration.class);
    AccountRepository accountRepository = mock(AccountRepository.class);
    StripePaymentProvider stripePaymentProvider = new StripePaymentProvider(stripeConfiguration);

    // Create account service
    AccountService accountService =
        new AccountServiceImpl(accountRepository, stripeConfiguration, workflowClient);

    // Create test account details
    Account accountDetails =
        Account.builder().firstName("John").lastName("Doe").email("john.doe@example.com").build();

    // Test account creation
    Account createdAccount = accountService.createAccount(accountDetails);

    // Verify that the account was created with a provider ID
    assertNotNull(createdAccount.getProviderId());
    assertEquals(accountDetails.getFirstName(), createdAccount.getFirstName());
    assertEquals(accountDetails.getLastName(), createdAccount.getLastName());
    assertEquals(accountDetails.getEmail(), createdAccount.getEmail());
    assertEquals(ProviderType.STRIPE, createdAccount.getProviderType());
  }
}
