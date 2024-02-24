package com.midas.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.midas.app.controllers.AccountController;
import com.midas.app.models.Account;
import com.midas.app.models.Enum.ProviderType;
import com.midas.app.services.AccountService;
import com.midas.generated.model.AccountDto;
import com.midas.generated.model.CreateAccountDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AccountControllerIntegrationTest {

  @Test
  void testSignupProcessAndRetrieveAccounts() {
    // Mock dependencies
    AccountService accountService = mock(AccountService.class);
    AccountController accountController = new AccountController(accountService);

    // Create test account details
    CreateAccountDto createAccountDto = new CreateAccountDto();
    createAccountDto.setFirstName("John");
    createAccountDto.setLastName("Doe");
    createAccountDto.setEmail("john.doe@example.com");

    // Mock account creation
    Account createdAccount =
        Account.builder()
            .firstName("Basant")
            .lastName("Rawat")
            .email("basantrawat01@gmail.com")
            .providerType(ProviderType.STRIPE)
            .providerId("stripe_customer_id")
            .build();
    when(accountService.createAccount(any())).thenReturn(createdAccount);

    // Test signup process
    ResponseEntity<AccountDto> response = accountController.createUserAccount(createAccountDto);

    // Verify signup process
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("basantrawat01@gmail.com", response.getBody().getEmail());

    // Test retrieval of accounts
    ResponseEntity<List<AccountDto>> accountsResponse = accountController.getUserAccounts();

    // Verify retrieval of accounts
    assertEquals(HttpStatus.OK, accountsResponse.getStatusCode());
    assertNotNull(accountsResponse.getBody());
    assertEquals(1, accountsResponse.getBody().size());
    assertEquals("John", accountsResponse.getBody().get(0).getFirstName());
    assertEquals("Doe", accountsResponse.getBody().get(0).getLastName());
    assertEquals("john.doe@example.com", accountsResponse.getBody().get(0).getEmail());
  }
}
