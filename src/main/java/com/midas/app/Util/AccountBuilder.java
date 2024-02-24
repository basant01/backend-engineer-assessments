package com.midas.app.Util;

import com.midas.app.models.Account;
import com.midas.app.models.Enum.ProviderType;

public class AccountBuilder {
  private Account account;

  public AccountBuilder() {
    this.account = new Account();
  }

  public AccountBuilder withEmail(String email) {
    this.account.setEmail(email);
    return this;
  }

  public AccountBuilder withFirstName(String firstName) {
    this.account.setFirstName(firstName);
    return this;
  }

  public AccountBuilder withLastName(String lastName) {
    this.account.setLastName(lastName);
    return this;
  }

  public AccountBuilder withProviderType(ProviderType providerType) {
    this.account.setProviderType(providerType);
    return this;
  }

  public AccountBuilder withProviderId(String providerId) {
    this.account.setProviderId(providerId);
    return this;
  }

  public Account build() {
    return this.account;
  }
}
