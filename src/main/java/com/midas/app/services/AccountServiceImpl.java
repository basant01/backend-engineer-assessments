package com.midas.app.services;

import com.midas.app.Util.AccountBuilder;
import com.midas.app.models.Account;
import com.midas.app.models.Enum.ProviderType;
import com.midas.app.providers.external.stripe.StripeConfiguration;
import com.midas.app.repositories.AccountRepository;
import com.midas.app.workflows.CreateAccountWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.workflow.Workflow;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
  private final Logger logger = Workflow.getLogger(AccountServiceImpl.class);

  private final AccountRepository accountRepository;

  private final StripeConfiguration stripeConfiguration;

  private final WorkflowClient workflowClient;

  /**
   * createAccount creates a new account in the system or provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public Account createAccount(Account details) {

    String email = details.getEmail();
    String firstName = details.getFirstName();
    String lastName = details.getLastName();

    String name = firstName + " " + lastName;

    // Create a Stripe customer and retrieve the customer ID
    String stripeCustomerId =
        StripeCustomerFactory.createStripeCustomer(email, name, stripeConfiguration);

    // Build the Account object
    Account account =
        new AccountBuilder()
            .withEmail(email)
            .withFirstName(firstName)
            .withLastName(lastName)
            .withProviderType(ProviderType.STRIPE)
            .withProviderId(stripeCustomerId)
            .build();

    // Save the account details
    Account savedAccount = accountRepository.save(account);

    // Start the Temporal workflow to create the account asynchronously
    initiateWorkflow(savedAccount);

    return savedAccount;
  }

  private void initiateWorkflow(Account account) {
    // Build workflow options
    WorkflowOptions options =
        WorkflowOptions.newBuilder().setTaskQueue(CreateAccountWorkflow.QUEUE_NAME).build();

    // Start the workflow
    CreateAccountWorkflow workflow =
        workflowClient.newWorkflowStub(CreateAccountWorkflow.class, options);
    workflow.createAccount(account);
  }

  /**
   * getAccounts returns a list of accounts.
   *
   * @return List<Account>
   */
  @Override
  public List<Account> getAccounts() {
    return accountRepository.findAll();
  }
}
