package com.midas.app.services;

import com.midas.app.providers.external.stripe.StripeConfiguration;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@RequiredArgsConstructor
public class StripeCustomerFactory {

  private static final Logger logger = LoggerFactory.getLogger(StripeCustomerFactory.class);

  public static String createStripeCustomer(
      String email, String name, StripeConfiguration stripeConfiguration) {
    Stripe.apiKey = stripeConfiguration.getApiKey();
    Map<String, Object> customerParams = new HashMap<>();
    customerParams.put("email", email);
    customerParams.put("name", name);
    try {
      Customer customer = Customer.create(customerParams);
      return customer.getId();
    } catch (StripeException e) {
      logger.error("Failed to create Stripe customer: {}", e.getMessage());
      throw new RuntimeException("Failed to create Stripe customer");
    }
  }
}
